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
  button("Sumber & batasan",()->new AlertDialog.Builder(this).setTitle("Sumber & batasan").setMessage("Waktu & Hijri: portal rasmi e-Solat JAKIM. Tarikh selepas Maghrib menggunakan rekod Hijri hari berikutnya, bukan pengiraan anggaran. Cache tidak diekstrapolasi.\n\nHaier belum diuji secara fizikal. Pemilihan Home mungkin disekat firmware; aplikasi masih boleh dibuka dari Apps. Google TV asal kekal dipasang.\n\nTiada root, ADB, debloat, Accessibility atau force-stop. Audio: Beautiful adhan, Adam-synagda, Wikimedia Commons, CC0 (154 saat); rakaman azan biasa, bukan varian Subuh khusus. Volume 11 ialah indeks STREAM_MUSIC, bukan jaminan nombor yang sama pada soundbar HDMI. TV standby, kebenaran ditarik balik atau firmware boleh menghalang peringatan. Tiada arahan PLAY dihantar.").setPositiveButton("Tutup",null).show());
  button("Kembali",()->finish());render();
 }
 protected void buildReminderSettings(){
  button("Peringatan: hidup / mati",()->{
   boolean on=!SolatAlarms.enabled(this);
   if(on&&(!SolatAlarms.allowed(this)||!canNotify()||SolatStore.zone(this).isEmpty())){new AlertDialog.Builder(this).setMessage("Pilih zon, muat turun jadual dan benarkan notifikasi serta alarm tepat dahulu. Overlay adalah pilihan. Tiada akses notifikasi aplikasi lain diperlukan.").setPositiveButton("OK",null).show();return;}
   if(on&&PrayerDay.on(SolatStore.load(this),LocalDate.now(PrayerDay.MALAYSIA))==null){Toast.makeText(this,"Muat turun jadual sah dahulu",1).show();return;}
   SolatStore.prefs(this).edit().putBoolean("enabled",on).commit();if(!on)SolatAlarms.stop(this);SolatAlarms.schedule(this);render();
  });
  button("1. Benarkan notifikasi",()->{if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},7);else open(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,getPackageName()));});
  button("2. Benarkan alarm tepat (Android 12+)",()->{if(Build.VERSION.SDK_INT>=31)open(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,android.net.Uri.parse("package:"+getPackageName())));});
  button("3. Paparan di atas aplikasi lain (pilihan)",()->open(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,android.net.Uri.parse("package:"+getPackageName()))));
  button("Uji peringatan 10 saat (tanpa pause)",()->test(true));
  button("Uji azan 20 saat + volume 11",()->new AlertDialog.Builder(this).setMessage("Ujian ini memainkan 20 saat audio azan, meminta audio focus Android (video mungkin dijeda, bergantung aplikasi) dan pulihkan volume asal. Azan berjadual memainkan rakaman penuh. Teruskan pada TV ini?").setPositiveButton("Uji",(d,v)->test(false)).setNegativeButton("Batal",null).show());
  button("Hentikan azan / tutup peringatan",()->{SolatAlarms.stop(this);render();});
 }
 private boolean canNotify(){return SolatAlarms.canNotify(this);}
 private void test(boolean pre){if(!canNotify()||(!pre&&!SolatAlarms.allowed(this))){Toast.makeText(this,"Benarkan notifikasi dan alarm tepat dahulu",1).show();return;}try{startForegroundService(new Intent(this,SolatReminderService.class).putExtra("before",pre).putExtra("test",true).putExtra("index",0).putExtra("prayer",System.currentTimeMillis()));}catch(RuntimeException e){Toast.makeText(this,"Perkhidmatan disekat Android",1).show();}}

 protected void button(String label,Runnable run){Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setOnClickListener(v->run.run());rows.addView(b,new LinearLayout.LayoutParams(-1,-2));}
 protected void open(Intent i){try{startActivity(i);}catch(Exception e){Toast.makeText(this,"Pilihan ini tidak disediakan firmware TV. Gunakan Settings TV.",Toast.LENGTH_LONG).show();}}
 protected void render(){List<PrayerDay> d=SolatStore.load(this);PrayerDay today=PrayerDay.on(d,LocalDate.now(PrayerDay.MALAYSIA));StringBuilder s=new StringBuilder(SolatStore.summary(this));s.append("\nSumber: e-Solat JAKIM · Asia/Kuala_Lumpur");
  if(today!=null)for(int i=0;i<5;i++)s.append("\n").append(PrayerDay.NAMES[i]).append("  ").append(today.time(i));
  long t=SolatStore.prefs(this).getLong("fetched_"+SolatStore.zone(this),0);s.append("\nKemas kini: ").append(t==0?"Belum tersedia":java.time.Instant.ofEpochMilli(t).atZone(PrayerDay.MALAYSIA).toLocalDateTime().withNano(0));s.append("\n\nPeringatan: ").append(SolatAlarms.enabled(this)?"HIDUP":"MATI");
  s.append(" · Alarm tepat: ").append(SolatAlarms.allowed(this)?"OK":"PERLU KEBENARAN");
  s.append("\nNotifikasi: ").append(canNotify()?"OK":"PERLU KEBENARAN").append(" · Overlay: ").append(Settings.canDrawOverlays(this)?"OK":"Notifikasi sahaja");
  s.append("\nVideo: audio focus sahaja; tiada akses notifikasi aplikasi lain");
  s.append("\n").append(SolatStore.prefs(this).getString("alarm_status","Belum dijadualkan"));
  s.append("\n").append(SolatStore.prefs(this).getString("media_status","Audio focus boleh menjeda video jika aplikasi menyokongnya"));
  s.append("\n").append(SolatStore.prefs(this).getString("volume_status","Volume belum diubah"));
  s.append("\n").append(SolatStore.prefs(this).getString("last_event","Belum ada azan"));status.setText(s.toString());}
 private void chooseZone(){try{JSONArray a=SolatStore.zones(this);String[] labels=new String[a.length()];for(int i=0;i<a.length();i++)labels[i]=a.getJSONObject(i).getString("code")+" · "+a.getJSONObject(i).getString("name");new AlertDialog.Builder(this).setTitle("Zon rasmi JAKIM").setItems(labels,(dialog,index)->{try{SolatStore.prefs(this).edit().putString("zone",a.getJSONObject(index).getString("code")).commit();onZoneChanged();render();refresh();}catch(Exception ignored){}}).show();}catch(Exception e){Toast.makeText(this,"Senarai zon tidak tersedia",1).show();}}
 protected void onZoneChanged(){SolatAlarms.stop(this);SolatAlarms.schedule(this);}
 protected void onRefreshed(){SolatAlarms.schedule(this);SolatRefreshJob.ensure(this);}
 private void refresh(){Toast.makeText(this,"Memuat turun dari JAKIM…",0).show();new Thread(()->{String result=SolatStore.refresh(this);runOnUiThread(()->{if(!isFinishing()){render();onRefreshed();Toast.makeText(this,result,1).show();}});},"jakim-refresh").start();}
 @Override public void onResume(){super.onResume();if(status!=null){SolatReminderService.recoverVolume(this);SolatAlarms.schedule(this);SolatRefreshJob.ensure(this);render();}}
}
