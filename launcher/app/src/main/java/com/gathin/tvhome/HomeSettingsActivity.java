package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** User-consented HOME request and observable status; never modifies other packages. */
public class HomeSettingsActivity extends Activity {
    private static final int REQUEST_HOME = 340;
    private TextView status;
    private String result = "";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) result = state.getString("home_result", "");
        ScrollView scroll = new ScrollView(this);
        LinearLayout rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        rows.setPadding(48, 28, 48, 28);
        rows.setBackgroundColor(0xff11151a);
        scroll.addView(rows);
        setContentView(scroll);
        TextView title = new TextView(this);
        title.setText("Home utama / Google TV asal");
        title.setTextColor(0xffffffff);
        title.setTextSize(26);
        rows.addView(title);
        status = new TextView(this);
        status.setTextColor(0xffdddddd);
        status.setTextSize(17);
        rows.addView(status);
        add(rows, "Jadikan Glass TV + Solat sebagai Home", this::requestHome);
        add(rows, "Buka pilihan Home sistem / kembali ke Google TV", () -> openSettings(Settings.ACTION_HOME_SETTINGS));
        add(rows, "Buka tetapan aplikasi lalai sistem", () -> openSettings(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
        add(rows, "Semak status semula", this::render);
        add(rows, "Kembali", this::finish);
        render();
    }

    private void add(LinearLayout rows, String text, Runnable action) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setOnClickListener(v -> action.run());
        rows.addView(button, new LinearLayout.LayoutParams(-1, -2));
    }

    private RoleManager roles() {
        return Build.VERSION.SDK_INT >= 29 ? getSystemService(RoleManager.class) : null;
    }

    private void requestHome() {
        try {
            RoleManager manager = roles();
            if (manager == null || !manager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                result = "Sistem tidak menyediakan permintaan peranan HOME. Cuba pilihan Home sistem jika tersedia.";
            } else if (manager.isRoleHeld(RoleManager.ROLE_HOME)) {
                result = "Android melaporkan peranan HOME sudah diberikan. Tekan Home pada remote untuk mengesahkan tingkah laku sebenar.";
            } else {
                // A successful start is not a grant; re-query actual state in onActivityResult.
                startActivityForResult(manager.createRequestRoleIntent(RoleManager.ROLE_HOME), REQUEST_HOME);
                return;
            }
        } catch (RuntimeException unavailable) {
            result = "Permintaan Home tidak dapat dibuka oleh sistem TV ini (" + unavailable.getClass().getSimpleName() + ").";
        }
        render();
    }

    private void openSettings(String action) {
        try { startActivity(new Intent(action)); }
        catch (RuntimeException unavailable) {
            result = "Halaman tetapan ini tidak disediakan oleh firmware TV.";
            render();
        }
    }

    @Override protected void onActivityResult(int request, int code, Intent data) {
        super.onActivityResult(request, code, data);
        if (request != REQUEST_HOME) return;
        // Denied and user-cancelled cannot be distinguished from this result alone.
        result = code == RESULT_OK
                ? "Permintaan selesai. Semak status Android di bawah dan uji butang Home pada remote."
                : "Permintaan tidak diberikan: dibatalkan atau sistem tidak membenarkannya. Tetapan asal dikekalkan.";
        render();
    }

    private void render() {
        if (status == null) return;
        String role = "Tidak disediakan";
        try {
            RoleManager manager = roles();
            if (manager != null && manager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                role = manager.isRoleHeld(RoleManager.ROLE_HOME) ? "Diberikan kepada Glass TV + Solat" : "Belum diberikan";
            }
        } catch (RuntimeException unavailable) { role = "Tidak dapat disemak"; }
        String handler = "Tidak dapat dikenal pasti";
        try {
            Intent home = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolved = getPackageManager().resolveActivity(home, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolved != null && resolved.activityInfo != null) {
                handler = resolved.loadLabel(getPackageManager()) + "\n" + resolved.activityInfo.packageName;
            }
        } catch (RuntimeException ignored) { }
        status.setText("Peranan HOME: " + role + "\nHome yang dipilih Android: " + handler
                + "\n\n" + result
                + "\n\nKeputusan ini tidak menjamin remote Haier mengikut pilihan Android. Jika Home masih membuka Haier, laporkan skrin status ini."
                + "\nTiada ADB, Accessibility atau launcher asal dinyahaktifkan. Tiada perubahan Home tanpa persetujuan melalui skrin Android.");
    }

    @Override protected void onResume() { super.onResume(); render(); }
    @Override protected void onSaveInstanceState(Bundle out) {
        out.putString("home_result", result);
        super.onSaveInstanceState(out);
    }
}
