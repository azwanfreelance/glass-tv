package com.gathin.tvhome.solat;
import android.app.*;
import android.os.*;
import android.content.*;
import android.provider.Settings;
import android.widget.*;
import org.json.*;
import java.time.*;
import java.util.*;
/** TV remote-first settings; permissions are granted only through Android UI. */
public class SolatSettingsActivity extends Activity {
 private LinearLayout rows;private TextView status;
 @Override public void onCreate(Bundle b){super.onCreate(b);ScrollView scroll=new ScrollView(this);rows=new LinearLayout(this);rows.setOrientation(1);rows.setPadding(48,24,48,24);rows.setBackgroundColor(0xff11151a);scroll.addView(rows);setContentView(scroll);
  TextView title=new TextView(this);title.setText("Glass TV + Solat");title.setTextSize(28);title.setTextColor(-1);rows.addView(title);
  status=new TextView(this);status.setTextColor(0xffdddddd);status.setTextSize(16);rows.addView(status);
  button("Pilih zon JAKIM",()->chooseZone());
  Switch sw=new Switch(this);sw.setText("Hijri bertukar selepas Maghrib");sw.setTextColor(-1);sw.setChecked(SolatStore.maghrib(this));sw.setOnCheckedChangeListener((v,on)->{SolatStore.prefs(this).edit().putBoolean("maghrib",on).apply();render();});rows.addView(sw);
  button("Muat turun / kemas kini jadual JAKIM",()->refresh());
  button("Tetapan sistem TV",()->open(new Intent(Settings.ACTION_SETTINGS)));
  button("Pilih Home / kembali ke Google TV",()->open(new Intent(Settings.ACTION_HOME_SETTINGS)));
  buildReminderSettings();
  button("Sumber & batasan",()->new AlertDialog.Builder(this).setTitle("Sumber & batasan").setMessage("Waktu & Hijri: portal rasmi e-Solat JAKIM. Tarikh selepas Maghrib menggunakan rekod Hijri hari berikutnya, bukan pengiraan anggaran. Cache tidak diekstrapolasi.\n\nHaier belum diuji secara fizikal. Pemilihan Home mungkin disekat firmware; aplikasi masih boleh dibuka dari Apps. Google TV asal kekal dipasang.\n\nTiada root, ADB, debloat, Accessibility atau force-stop. Audio disediakan pada fasa peringatan.").setPositiveButton("Tutup",null).show());
  button("Kembali",()->finish());render();
 }
 protected void buildReminderSettings(){}
 protected void button(String label,Runnable run){Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setOnClickListener(v->run.run());rows.addView(b,new LinearLayout.LayoutParams(-1,-2));}
 protected void open(Intent i){try{startActivity(i);}catch(Exception e){Toast.makeText(this,"Pilihan ini tidak disediakan firmware TV. Gunakan Settings TV.",Toast.LENGTH_LONG).show();}}
 protected void render(){List<PrayerDay> d=SolatStore.load(this);PrayerDay today=PrayerDay.on(d,LocalDate.now(PrayerDay.MALAYSIA));StringBuilder s=new StringBuilder(SolatStore.summary(this));s.append("\nSumber: e-Solat JAKIM · Asia/Kuala_Lumpur");
  if(today!=null)for(int i=0;i<5;i++)s.append("\n").append(PrayerDay.NAMES[i]).append("  ").append(today.time(i));
  long t=SolatStore.prefs(this).getLong("fetched_"+SolatStore.zone(this),0);s.append("\nKemas kini: ").append(t==0?"Belum tersedia":java.time.Instant.ofEpochMilli(t).atZone(PrayerDay.MALAYSIA).toLocalDateTime().withNano(0));status.setText(s.toString());}
 private void chooseZone(){try{JSONArray a=SolatStore.zones(this);String[] labels=new String[a.length()];for(int i=0;i<a.length();i++)labels[i]=a.getJSONObject(i).getString("code")+" · "+a.getJSONObject(i).getString("name");new AlertDialog.Builder(this).setTitle("Zon rasmi JAKIM").setItems(labels,(dialog,index)->{try{SolatStore.prefs(this).edit().putString("zone",a.getJSONObject(index).getString("code")).commit();onZoneChanged();render();refresh();}catch(Exception ignored){}}).show();}catch(Exception e){Toast.makeText(this,"Senarai zon tidak tersedia",1).show();}}
 protected void onZoneChanged(){}
 protected void onRefreshed(){}
 private void refresh(){Toast.makeText(this,"Memuat turun dari JAKIM…",0).show();new Thread(()->{String result=SolatStore.refresh(this);runOnUiThread(()->{if(!isFinishing()){render();onRefreshed();Toast.makeText(this,result,1).show();}});},"jakim-refresh").start();}
 @Override public void onResume(){super.onResume();if(status!=null)render();}
}
