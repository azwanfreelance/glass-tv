package com.gathin.tvhome.solat;
import org.json.*;import java.time.*;import java.time.format.*;import java.util.*;import java.io.*;
public final class JakimParser {
 public static List<PrayerDay> parse(String json,String zone) throws Exception {
  JSONObject o=new JSONObject(json);if(!zone.equals(o.getString("zone")))throw new IOException("Zon respons tidak sepadan");
  JSONArray a=o.getJSONArray("prayerTime");if(a.length()==0||a.length()>370)throw new IOException("Jadual kosong / terlalu besar");
  List<PrayerDay> result=new ArrayList<>();Set<LocalDate> seen=new HashSet<>();
  DateTimeFormatter f=new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-uuuu").toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
  for(int i=0;i<a.length();i++){JSONObject r=a.getJSONObject(i);LocalDate d=LocalDate.parse(r.getString("date"),f);if(!seen.add(d))throw new IOException("Tarikh berulang");
   result.add(new PrayerDay(d,r.getString("hijri"),new String[]{r.getString("fajr"),r.getString("dhuhr"),r.getString("asr"),r.getString("maghrib"),r.getString("isha")}));}
  result.sort(Comparator.comparing(d->d.date));return result;
 }
}
