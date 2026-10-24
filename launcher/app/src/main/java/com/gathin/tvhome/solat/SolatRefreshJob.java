package com.gathin.tvhome.solat;
import android.app.job.*;import android.content.*;
public class SolatRefreshJob extends JobService {
 public static void ensure(Context c){JobScheduler s=c.getSystemService(JobScheduler.class);if(s.getPendingJob(92)==null)s.schedule(new JobInfo.Builder(92,new ComponentName(c,SolatRefreshJob.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPeriodic(12*60*60*1000L).setPersisted(true).build());}
 @Override public boolean onStartJob(JobParameters p){new Thread(()->{if(!SolatStore.zone(this).isEmpty()){String result=SolatStore.refresh(this);SolatStore.prefs(this).edit().putString("refresh_status",result).apply();SolatAlarms.schedule(this);}jobFinished(p,false);},"jakim-periodic").start();return true;}
 @Override public boolean onStopJob(JobParameters p){return true;}
}
