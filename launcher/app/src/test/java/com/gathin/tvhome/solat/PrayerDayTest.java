package com.gathin.tvhome.solat;
import org.junit.Test;
import java.time.*;
import java.util.*;
import static org.junit.Assert.*;
public class PrayerDayTest {
 private PrayerDay day(String date,String hijri) { return new PrayerDay(LocalDate.parse(date),hijri,new String[]{"06:00:00","13:00:00","16:00:00","19:00:00","20:00:00"}); }
 @Test public void nextPrayerAndMidnight() {
  PrayerDay d=day("2026-10-24","1448-05-13"), n=day("2026-10-25","1448-05-14");
  assertEquals("Subuh", PrayerDay.next(Arrays.asList(d,n),d.at(0).minusSeconds(1)).name);
  assertEquals("Zohor", PrayerDay.next(Arrays.asList(d,n),d.at(0)).name);
  assertEquals(n.at(0), PrayerDay.next(Arrays.asList(d,n),d.at(4)).time);
  assertNull(PrayerDay.next(Collections.singletonList(d),d.at(4)));
 }
 @Test public void hijriUsesNextOfficialRowAfterMaghrib() {
  PrayerDay d=day("2026-10-24","1448-05-29"), n=day("2026-10-25","1448-06-01");
  assertEquals("1448-05-29",PrayerDay.hijri(Arrays.asList(d,n),d.at(3).minusSeconds(1),true));
  assertEquals("1448-06-01",PrayerDay.hijri(Arrays.asList(d,n),d.at(3),true));
  assertEquals("1448-05-29",PrayerDay.hijri(Arrays.asList(d,n),d.at(3),false));
  assertNull(PrayerDay.hijri(Collections.singletonList(d),d.at(3),true));
 }
 @Test public void staleDateIsNotReused() { PrayerDay d=day("2026-10-24","1448-05-13");assertNull(PrayerDay.hijri(Collections.singletonList(d),d.at(0).plusDays(1),false)); }
 @Test(expected=IllegalArgumentException.class) public void invalidTimeRejected(){new PrayerDay(LocalDate.now(),"1448-05-13",new String[]{"25:00","13:00","16:00","19:00","20:00"});}
 @Test(expected=IllegalArgumentException.class) public void unorderedTimesRejected(){new PrayerDay(LocalDate.now(),"1448-05-13",new String[]{"06:00","16:00","13:00","19:00","20:00"});}
 @Test(expected=IllegalArgumentException.class) public void invalidHijriRejected(){day("2026-10-24","1448-13-01");}
 @Test public void malaysiaZoneIndependentOfDevice(){assertEquals(ZoneOffset.ofHours(8),day("2026-10-24","1448-05-13").at(0).getOffset());}
}
