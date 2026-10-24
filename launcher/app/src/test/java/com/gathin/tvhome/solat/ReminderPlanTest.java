package com.gathin.tvhome.solat;
import org.junit.Test;import java.time.*;import java.util.*;import static org.junit.Assert.*;
public class ReminderPlanTest {
 private PrayerDay d(){return new PrayerDay(LocalDate.of(2026,10,24),"1448-05-13",new String[]{"06:00","13:00","16:00","19:00","20:00"});}
 @Test public void prealertIsTenMinutesBefore(){PrayerDay d=d();ReminderPlan.Event e=ReminderPlan.next(Collections.singletonList(d),d.at(0).minusMinutes(11).toInstant().toEpochMilli());assertTrue(e.before);assertEquals(d.at(0).minusMinutes(10).toInstant().toEpochMilli(),e.when);}
 @Test public void nextAfterPrealertIsAzan(){PrayerDay d=d();ReminderPlan.Event e=ReminderPlan.next(Collections.singletonList(d),d.at(0).minusMinutes(10).toInstant().toEpochMilli());assertFalse(e.before);assertEquals(d.at(0).toInstant().toEpochMilli(),e.when);}
 @Test public void lateAzanNeverStartsMoreThanOneMinuteLate(){assertFalse(ReminderPlan.accept(false,100000,160001));assertTrue(ReminderPlan.accept(false,100000,159999));assertFalse(ReminderPlan.accept(false,100000,99999));}
 @Test public void latePrealertNeverDisplaysAtAzan(){assertFalse(ReminderPlan.accept(true,100000,110001));}
 @Test public void expiryIsFromPrayerNotAudioEnd(){assertEquals(100000+15*60000,ReminderPlan.until(100000));}
 @Test public void endOfCacheDoesNotInventNextPrayer(){PrayerDay d=d();assertNull(ReminderPlan.next(Collections.singletonList(d),d.at(4).toInstant().toEpochMilli()));}
 @Test public void volumeCapUsesDeviceScale(){assertEquals(11,ReminderPlan.volume(20));assertEquals(7,ReminderPlan.volume(7));}
}
