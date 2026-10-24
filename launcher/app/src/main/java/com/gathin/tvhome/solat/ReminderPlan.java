package com.gathin.tvhome.solat;
import java.util.*;
/** Pure scheduling policy, independent of Android clocks and permissions. */
public final class ReminderPlan {
 public static final long PRE=600000,POST=900000;
 public static final class Event {public final long when,prayer;public final boolean before;public final int index;public Event(long p,int i,boolean b){prayer=p;index=i;before=b;when=p-(b?PRE:0);}}
 public static Event next(List<PrayerDay> days,long now){Event next=null;for(PrayerDay d:days)for(int i=0;i<5;i++){long p=d.at(i).toInstant().toEpochMilli();for(boolean b:new boolean[]{true,false}){Event e=new Event(p,i,b);if(e.when>now&&(next==null||e.when<next.when))next=e;}}return next;}
 public static boolean accept(boolean before,long event,long now){return now>=event&&now-event<=(before?10000:60000);}
 public static long until(long prayer){return prayer+POST;}
 public static int volume(int max){return Math.max(0,Math.min(11,max));}
}
