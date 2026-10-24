package com.gathin.tvhome.solat;
import android.app.*;import android.content.*;import android.content.pm.ServiceInfo;import android.media.*;import android.os.*;import android.graphics.Color;import android.view.*;import android.widget.*;import android.provider.Settings;
import com.gathin.tvhome.R;
/** Exists only during a prealert or the absolute prayer+15min reminder window. */
public class SolatReminderService extends Service {
 private static boolean activeAudio;
 private static final int ID=501;private static final String CHANNEL="solat-reminders";
 private final Handler h=new Handler(Looper.getMainLooper());private MediaPlayer player;private AudioManager audio;private AudioFocusRequest focus;private View overlay;private long expiry;private boolean changed;private int original;private String text;private boolean pre;
 @Override public IBinder onBind(Intent i){return null;}
 static void channel(Context c){NotificationChannel ch=new NotificationChannel(CHANNEL,"Peringatan solat",NotificationManager.IMPORTANCE_HIGH);ch.setSound(null,null);ch.enableVibration(false);c.getSystemService(NotificationManager.class).createNotificationChannel(ch);}
 static Notification note(Context c,String text,long timeout,boolean ongoing){channel(c);PendingIntent settings=PendingIntent.getActivity(c,0,new Intent(c,SolatSettingsActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
  Notification.Builder builder=new Notification.Builder(c,CHANNEL).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Solat TV").setContentText(text).setContentIntent(settings).setCategory(Notification.CATEGORY_REMINDER).setTimeoutAfter(Math.max(1,timeout)).setOnlyAlertOnce(true).setOngoing(ongoing);if(Build.VERSION.SDK_INT>=31)builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);return builder.build();}
 static void fallback(Context c,String text,long timeout){try{c.getSystemService(NotificationManager.class).notify(ID,note(c,text,timeout,false));}catch(RuntimeException ignored){}}
 private void foreground(){Notification n=note(this,text,Math.max(1,expiry-System.currentTimeMillis()),true);if(Build.VERSION.SDK_INT>=34)startForeground(ID,n,ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE|(pre?0:ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK));else startForeground(ID,n);}
 @Override public int onStartCommand(Intent i,int flags,int id){
  if(i==null||!SolatAlarms.canNotify(this)){stopSelf();return START_NOT_STICKY;}cleanup();
  pre=i.getBooleanExtra("before",false);boolean test=i.getBooleanExtra("test",false);long prayer=i.getLongExtra("prayer",System.currentTimeMillis());int index=i.getIntExtra("index",0);if(index<0||index>=5){stopSelf();return START_NOT_STICKY;}
  expiry=pre?System.currentTimeMillis()+10000:ReminderPlan.until(prayer);if(test)expiry=System.currentTimeMillis()+20000;
  if(expiry<=System.currentTimeMillis()){stopSelf();return START_NOT_STICKY;}
  text=pre?PrayerDay.NAMES[index]+" dalam 10 minit": "Sudah masuk waktu "+PrayerDay.NAMES[index]+". Sambung video secara manual selepas solat.";
  try{foreground();showOverlay();if(!pre){SolatStore.prefs(this).edit().putString("last_event","Azan "+PrayerDay.NAMES[index]+" · "+java.time.ZonedDateTime.now(PrayerDay.MALAYSIA)).apply();startAudio();}
   h.postDelayed(this::stopSelf,Math.max(1,expiry-System.currentTimeMillis()));
  }catch(RuntimeException ex){fallback(this,text,Math.max(1,expiry-System.currentTimeMillis()));stopSelf();}return START_NOT_STICKY;
 }
 private void showOverlay(){if(!Settings.canDrawOverlays(this))return;try{
  TextView t=new TextView(this);t.setText(text+"\nSolat TV · Tutup melalui Settings > Hentikan azan");t.setTextColor(Color.WHITE);t.setTextSize(19);t.setPadding(24,18,24,18);t.setBackgroundColor(0xee151c25);
  int width=(int)(430*getResources().getDisplayMetrics().density);WindowManager.LayoutParams lp=new WindowManager.LayoutParams(width,-2,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,android.graphics.PixelFormat.TRANSLUCENT);lp.gravity=Gravity.TOP|Gravity.END;lp.y=28;lp.x=28;getSystemService(WindowManager.class).addView(t,lp);overlay=t;
 }catch(RuntimeException ignored){}}
 private void startAudio(){audio=getSystemService(AudioManager.class);recoverVolume(this);boolean paused=SolatMedia.pause(this);
  AudioAttributes attrs=new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
  // Permanent focus avoids sending transient-focus regain as an invitation to auto-resume video.
  focus=new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(attrs).setOnAudioFocusChangeListener(change->{if(change<0){finishAudio();}}).build();
  if(audio.requestAudioFocus(focus)!=AudioManager.AUDIOFOCUS_REQUEST_GRANTED){SolatStore.prefs(this).edit().putString("media_status","Audio focus ditolak; peringatan sahaja").apply();return;}
  SolatStore.prefs(this).edit().putString("media_status",paused?"Permintaan PAUSE dihantar; tiada arahan PLAY":"Tiada MediaSession PAUSE; fallback audio focus").apply();
  try{if(!audio.isVolumeFixed()&&SolatAlarms.scheduleRecovery(this)){
    original=audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    if(SolatStore.prefs(this).edit().putInt("saved_volume",original).commit()) {changed=true;activeAudio=true;audio.setStreamVolume(AudioManager.STREAM_MUSIC,ReminderPlan.volume(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),0);}
   }
   player=MediaPlayer.create(this,R.raw.adhan,attrs,0);if(player==null)throw new IllegalStateException("Audio tidak tersedia");
   player.setOnCompletionListener(p->finishAudio());player.setOnErrorListener((p,a,b)->{finishAudio();return true;});player.start();
   h.postDelayed(()->{if(player!=null)finishAudio();},8*60000L);
  }catch(RuntimeException e){finishAudio();SolatStore.prefs(this).edit().putString("media_status","Audio tidak tersedia; peringatan sahaja").apply();}
 }
 private void finishAudio(){if(player!=null){try{player.release();}catch(RuntimeException ignored){}player=null;}
  activeAudio=false;
  if(changed){recoverVolume(this);changed=false;}
  if(audio!=null&&focus!=null){audio.abandonAudioFocusRequest(focus);focus=null;}
  // The remaining service is an explicit bounded prayer reminder, not media playback.
  if(Build.VERSION.SDK_INT>=34&&!pre&&expiry>System.currentTimeMillis())try{startForeground(ID,note(this,text,expiry-System.currentTimeMillis(),true),ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);}catch(RuntimeException ignored){}
 }
 static void recoverVolume(Context c){if(activeAudio)return;android.content.SharedPreferences p=SolatStore.prefs(c);if(!p.contains("saved_volume"))return;AudioManager a=c.getSystemService(AudioManager.class);try{if(!a.isVolumeFixed()){int v=p.getInt("saved_volume",0);a.setStreamVolume(AudioManager.STREAM_MUSIC,Math.max(0,Math.min(v,a.getStreamMaxVolume(AudioManager.STREAM_MUSIC))),0);}if(p.edit().remove("saved_volume").commit())SolatAlarms.cancelRecovery(c);}catch(RuntimeException ignored){}}
 private void cleanup(){h.removeCallbacksAndMessages(null);expiry=0;finishAudio();if(overlay!=null){try{getSystemService(WindowManager.class).removeView(overlay);}catch(RuntimeException ignored){}overlay=null;}}
 @Override public void onDestroy(){cleanup();stopForeground(STOP_FOREGROUND_REMOVE);super.onDestroy();}
 @Override public void onTaskRemoved(Intent root){stopSelf();}
}
