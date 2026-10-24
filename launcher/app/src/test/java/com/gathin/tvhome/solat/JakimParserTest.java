package com.gathin.tvhome.solat;
import org.junit.Test;import static org.junit.Assert.*;
public class JakimParserTest {
 private String row="{\"date\":\"24-Oct-2026\",\"hijri\":\"1448-05-13\",\"fajr\":\"06:00:00\",\"dhuhr\":\"13:00:00\",\"asr\":\"16:00:00\",\"maghrib\":\"19:00:00\",\"isha\":\"20:00:00\"}";
 private String response(String rows){return "{\"zone\":\"SGR01\",\"prayerTime\":["+rows+"]}";}
 @Test public void officialShapeParses() throws Exception {assertEquals("1448-05-13",JakimParser.parse(response(row),"SGR01").get(0).hijri);}
 @Test(expected=Exception.class) public void wrongZoneRejected() throws Exception{JakimParser.parse(response(row),"PRK02");}
 @Test(expected=Exception.class) public void invalidGregorianDateRejected() throws Exception{JakimParser.parse(response(row.replace("24-Oct","31-Feb")),"SGR01");}
 @Test(expected=Exception.class) public void duplicateDateRejected() throws Exception{JakimParser.parse(response(row+","+row),"SGR01");}
 @Test(expected=Exception.class) public void missingPrayerRejected() throws Exception{JakimParser.parse(response(row.replace("isha","unknown")),"SGR01");}
 @Test(expected=Exception.class) public void htmlErrorRejected() throws Exception{JakimParser.parse("<html>502</html>","SGR01");}
 @Test(expected=Exception.class) public void emptyScheduleRejected() throws Exception{JakimParser.parse(response(""),"SGR01");}
}
