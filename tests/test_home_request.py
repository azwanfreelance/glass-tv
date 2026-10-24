from pathlib import Path
import unittest, xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
class HomeRequestTest(unittest.TestCase):
 def test_home_screen_can_hold_an_activity_result(self):
  root=ET.parse(ROOT/'launcher/app/src/main/AndroidManifest.xml').getroot()
  a=root.find("application/activity[@{http://schemas.android.com/apk/res/android}name='.HomeSettingsActivity']")
  self.assertNotEqual('@android:style/Theme.NoDisplay',a.get('{http://schemas.android.com/apk/res/android}theme'))
 def test_all_home_entry_points_use_the_request_screen(self):
  main=(ROOT/'launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java').read_text()
  settings=(ROOT/'launcher/app/src/main/java/com/gathin/tvhome/solat/SolatSettingsActivity.java').read_text()
  self.assertFalse('which == 1 ? Settings.ACTION_SETTINGS : Settings.ACTION_HOME_SETTINGS' in main)
  self.assertFalse('()->open(new Intent(Settings.ACTION_HOME_SETTINGS))' in settings)
