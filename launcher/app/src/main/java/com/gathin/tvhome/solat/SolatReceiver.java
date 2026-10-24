package com.gathin.tvhome.solat;
import android.app.*;import android.content.*;import android.os.*;
public class SolatReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){String action=i.getAction();
  if("SOLAT_RESTORE_VOLUME".equals(action)){SolatAlarms.stop(c);SolatReminderService.recoverVolume(c);return;}
  if(!"SOLAT_ALARM".equals(action)){if(Intent.ACTION_BOOT_COMPLETED.equals(action)||Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)||Intent.ACTION_TIME_CHANGED.equals(action)||Intent.ACTION_TIMEZONE_CHANGED.equals(action)||AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action)){SolatAlarms.stop(c);SolatReminderService.recoverVolume(c);SolatAlarms.schedule(c);SolatRefreshJob.ensure(c);}return;}
  SolatAlarms.schedule(c);if(!SolatAlarms.enabled(c)||!SolatAlarms.allowed(c)||!SolatAlarms.canNotify(c)||!SolatStore.zone(c).equals(i.getStringExtra("zone")))return;
  long prayer=i.getLongExtra("prayer",0);int index=i.getIntExtra("index",-1);boolean before=i.getBooleanExtra("before",false);if(index<0||index>=5)return;
  boolean known=false;for(PrayerDay d:SolatStore.load(c))if(d.at(index).toInstant().toEpochMilli()==prayer){known=true;break;}if(!known)return;
  if(!ReminderPlan.accept(before,prayer-(before?ReminderPlan.PRE:0),System.currentTimeMillis()))return;
  String key=SolatStore.zone(c)+":"+prayer+":"+before;
  if(key.equals(SolatStore.prefs(c).getString("consumed","")))return;
  if(!SolatStore.prefs(c).edit().putString("consumed",key).commit())return;
  PowerManager power=c.getSystemService(PowerManager.class);if(power!=null&&!power.isInteractive()){SolatStore.prefs(c).edit().putString("last_event","Dilangkau: TV dalam standby").apply();return;}
  try{c.startForegroundService(new Intent(c,SolatReminderService.class).putExtras(i));}catch(RuntimeException ex){SolatReminderService.fallback(c,"Waktu "+PrayerDay.NAMES[index]+" · perkhidmatan disekat Android",before?10000:Math.max(1,ReminderPlan.until(prayer)-System.currentTimeMillis()));}
 }
}
