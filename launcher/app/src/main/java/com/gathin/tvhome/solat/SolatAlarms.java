package com.gathin.tvhome.solat;
import android.app.*;import android.content.*;import android.os.*;
public final class SolatAlarms {
 public static boolean allowed(Context c){return Build.VERSION.SDK_INT<31||c.getSystemService(AlarmManager.class).canScheduleExactAlarms();}
 public static boolean canNotify(Context c){NotificationManager n=c.getSystemService(NotificationManager.class);NotificationChannel ch=n.getNotificationChannel("solat-reminders");return n.areNotificationsEnabled()&&(ch==null||ch.getImportance()!=NotificationManager.IMPORTANCE_NONE)&&(Build.VERSION.SDK_INT<33||c.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)==android.content.pm.PackageManager.PERMISSION_GRANTED);}
 public static boolean scheduleRecovery(Context c){if(!allowed(c))return false;try{PendingIntent p=PendingIntent.getBroadcast(c,82,new Intent(c,SolatReceiver.class).setAction("SOLAT_RESTORE_VOLUME"),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);c.getSystemService(AlarmManager.class).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+180000,p);return true;}catch(RuntimeException ex){return false;}}
 public static void cancelRecovery(Context c){PendingIntent p=PendingIntent.getBroadcast(c,82,new Intent(c,SolatReceiver.class).setAction("SOLAT_RESTORE_VOLUME"),PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE);if(p!=null){c.getSystemService(AlarmManager.class).cancel(p);p.cancel();}}
 public static boolean enabled(Context c){return SolatStore.prefs(c).getBoolean("enabled",false);}
 public static void schedule(Context c){AlarmManager am=c.getSystemService(AlarmManager.class);PendingIntent old=PendingIntent.getBroadcast(c,80,new Intent(c,SolatReceiver.class).setAction("SOLAT_ALARM"),PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE);if(old!=null){am.cancel(old);old.cancel();}
  if(!enabled(c)||!allowed(c)||!canNotify(c))return;
  ReminderPlan.Event e=ReminderPlan.next(SolatStore.load(c),System.currentTimeMillis());if(e==null)return;
  Intent i=new Intent(c,SolatReceiver.class).setAction("SOLAT_ALARM").putExtra("prayer",e.prayer).putExtra("index",e.index).putExtra("before",e.before).putExtra("zone",SolatStore.zone(c));
  PendingIntent pi=PendingIntent.getBroadcast(c,80,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  try{am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,e.when,pi);SolatStore.prefs(c).edit().putString("alarm_status","Dijadualkan: "+PrayerDay.NAMES[e.index]+(e.before?" (10 min awal)":" (azan)")).apply();}
  catch(SecurityException ex){SolatStore.prefs(c).edit().putString("alarm_status","Kebenaran alarm tepat ditarik balik").apply();}
 }
 public static void stop(Context c){c.stopService(new Intent(c,SolatReminderService.class));}
}
