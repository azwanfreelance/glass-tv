from pathlib import Path
import unittest
import xml.etree.ElementTree as ET
ROOT = Path(__file__).resolve().parents[1]
class SolatSafetyTest(unittest.TestCase):
    def test_no_debugging_client_in_apk_sources(self):
        sources = '\n'.join(p.read_text() for p in (ROOT/'launcher/app/src/main/java').rglob('*.java'))
        self.assertFalse('shell:pm trim-caches' in sources)
        self.assertFalse('CacheCleaner.maybeRun' in sources)
    def test_no_accessibility_service_in_apk(self):
        manifest = (ROOT/'launcher/app/src/main/AndroidManifest.xml').read_text()
        self.assertNotIn('BIND_ACCESSIBILITY_SERVICE', manifest)
    def test_distinct_package_prevents_replacing_original(self):
        self.assertIn("applicationId 'my.azwan.solattv'", (ROOT/'launcher/app/build.gradle').read_text())
    def test_no_access_to_other_apps_notifications(self):
        manifest = (ROOT/'launcher/app/src/main/AndroidManifest.xml').read_text()
        self.assertFalse('BIND_NOTIFICATION_LISTENER_SERVICE' in manifest,
                         'A sideloaded prayer app must not request notification access')
        sources = '\n'.join(p.read_text() for p in (ROOT/'launcher/app/src/main/java').rglob('*.java'))
        self.assertFalse('NotificationListenerService' in sources)
        self.assertFalse('getActiveSessions' in sources)
