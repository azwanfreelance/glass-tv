package com.gathin.tvhome.solat;
import java.time.*;
import java.time.format.*;
import java.util.*;
/** Only official dated rows; never extrapolate a prayer or Hijri date. */
public final class PrayerDay {
 public static final ZoneId MALAYSIA=ZoneId.of("Asia/Kuala_Lumpur");
 public static final String[] NAMES={"Subuh","Zohor","Asar","Maghrib","Isyak"};
 public final LocalDate date; public final String hijri; private final LocalTime[] times=new LocalTime[5];
 public PrayerDay(LocalDate date,String hijri,String[] values){
  if(values.length!=5 || !hijri.matches("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|30)")) throw new IllegalArgumentException("Tarikh Hijri tidak sah");
  this.date=date;this.hijri=hijri;
  try{for(int i=0;i<5;i++){times[i]=LocalTime.parse(values[i]);if(i>0&&!times[i].isAfter(times[i-1]))throw new IllegalArgumentException("Susunan waktu tidak sah");}}
  catch(DateTimeException ex){throw new IllegalArgumentException("Waktu tidak sah",ex);}
 }
 public ZonedDateTime at(int i){return date.atTime(times[i]).atZone(MALAYSIA);}
 public String time(int i){return times[i].format(DateTimeFormatter.ofPattern("HH:mm"));}
 public static final class Next {public final String name; public final ZonedDateTime time;public final int index; Next(int i,ZonedDateTime t){index=i;name=NAMES[i];time=t;}}
 public static Next next(List<PrayerDay> days,ZonedDateTime now){Next result=null;for(PrayerDay d:days)for(int i=0;i<5;i++)if(d.at(i).isAfter(now)&&(result==null||d.at(i).isBefore(result.time)))result=new Next(i,d.at(i));return result;}
 public static PrayerDay on(List<PrayerDay> days,LocalDate date){for(PrayerDay d:days)if(d.date.equals(date))return d;return null;}
 public static String hijri(List<PrayerDay> days,ZonedDateTime now,boolean maghrib){
  ZonedDateTime local=now.withZoneSameInstant(MALAYSIA);PrayerDay today=on(days,local.toLocalDate());if(today==null)return null;
  if(maghrib&&!local.isBefore(today.at(3))){PrayerDay next=on(days,today.date.plusDays(1));return next==null?null:next.hijri;}return today.hijri;
 }
 public static String hijriLabel(String h){if(h==null)return "Hijri: data JAKIM belum tersedia";String[] m={"Muharam","Safar","Rabiulawal","Rabiulakhir","Jamadilawal","Jamadilakhir","Rejab","Syaaban","Ramadan","Syawal","Zulkaedah","Zulhijjah"};String[] a=h.split("-");return Integer.parseInt(a[2])+" "+m[Integer.parseInt(a[1])-1]+" "+a[0]+"H";}
}
