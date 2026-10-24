package com.gathin.tvhome.solat;
import android.content.*;
import org.json.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.io.*;
import java.net.*;
/** Private per-zone cache. Failed downloads never replace valid cached data. */
public final class SolatStore {
 public static final String SOURCE="https://www.e-solat.gov.my/index.php?r=esolatApi/takwimsolat";
 public static SharedPreferences prefs(Context c){return c.getSharedPreferences("solat",Context.MODE_PRIVATE);}
 public static String zone(Context c){return prefs(c).getString("zone","");}
 public static boolean maghrib(Context c){return prefs(c).getBoolean("maghrib",true);}
 public static JSONArray zones(Context c) throws Exception {try(InputStream in=c.getAssets().open("jakim-zones.json")){return new JSONArray(new String(readSmall(in),java.nio.charset.StandardCharsets.UTF_8));}}
 private static byte[] readSmall(InputStream in) throws IOException {ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){out.write(b,0,n);if(out.size()>64000)throw new IOException("Senarai terlalu besar");}return out.toByteArray();}
 public static boolean validZone(Context c,String z){try{JSONArray a=zones(c);for(int i=0;i<a.length();i++)if(z.equals(a.getJSONObject(i).getString("code")))return true;}catch(Exception ignored){}return false;}
 public static List<PrayerDay> parse(String json,String zone) throws Exception {
  JSONObject o=new JSONObject(json);if(!zone.equals(o.getString("zone")))throw new IOException("Zon respons tidak sepadan");
  JSONArray a=o.getJSONArray("prayerTime");if(a.length()==0||a.length()>370)throw new IOException("Jadual kosong / terlalu besar");
  List<PrayerDay> result=new ArrayList<>();Set<LocalDate> seen=new HashSet<>();
  DateTimeFormatter f=new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-uuuu").toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
  for(int i=0;i<a.length();i++){JSONObject r=a.getJSONObject(i);LocalDate d=LocalDate.parse(r.getString("date"),f);if(!seen.add(d))throw new IOException("Tarikh berulang");
   result.add(new PrayerDay(d,r.getString("hijri"),new String[]{r.getString("fajr"),r.getString("dhuhr"),r.getString("asr"),r.getString("maghrib"),r.getString("isha")}));}
  result.sort(Comparator.comparing(d->d.date));return result;
 }
 public static List<PrayerDay> load(Context c){String z=zone(c);if(!validZone(c,z))return Collections.emptyList();try{return parse(prefs(c).getString("cache_"+z,""),z);}catch(Exception e){return Collections.emptyList();}}
 public static String refresh(Context c){String z=zone(c);if(!validZone(c,z))return "Pilih zon JAKIM dahulu";HttpURLConnection con=null;
  try{con=(HttpURLConnection)new URL(SOURCE+"&period=year&zone="+z).openConnection();con.setConnectTimeout(15000);con.setReadTimeout(20000);con.setInstanceFollowRedirects(false);
   if(con.getResponseCode()!=200)throw new IOException("HTTP "+con.getResponseCode());
   ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=con.getInputStream()){byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1){out.write(b,0,n);if(out.size()>512000)throw new IOException("Respons terlalu besar");}}
   String raw=out.toString("UTF-8");List<PrayerDay> days=parse(raw,z);LocalDate now=LocalDate.now(PrayerDay.MALAYSIA);
   if(PrayerDay.on(days,now)==null)throw new IOException("Jadual tiada tarikh hari ini");
   if(!zone(c).equals(z))return "Zon telah berubah; kemas kini diabaikan";
   if(!prefs(c).edit().putString("cache_"+z,raw).putLong("fetched_"+z,System.currentTimeMillis()).commit())throw new IOException("Gagal menyimpan jadual");
   return "Jadual JAKIM disimpan offline: "+days.size()+" hari";
  }catch(Exception e){return "Kemas kini gagal; cache sah dikekalkan ("+e.getClass().getSimpleName()+")";}finally{if(con!=null)con.disconnect();}
 }
 public static String summary(Context c){String z=zone(c);if(z.isEmpty())return "Solat TV · Pilih zon JAKIM";List<PrayerDay>d=load(c);ZonedDateTime now=ZonedDateTime.now(PrayerDay.MALAYSIA);
  if(PrayerDay.on(d,now.toLocalDate())==null)return z+" · Jadual tamat / belum dimuat turun";
  PrayerDay.Next next=PrayerDay.next(d,now);String n=next==null?"Jadual seterusnya belum tersedia":next.name+" "+next.time.format(DateTimeFormatter.ofPattern("HH:mm"));
  return z+" · "+n+"  ·  "+PrayerDay.hijriLabel(PrayerDay.hijri(d,now,maghrib(c)));
 }
}
