import importlib.util, tempfile, unittest, zipfile, contextlib, io
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
class PackagedPermissionsTest(unittest.TestCase):
 def test_sensitive_manifest_strings_are_rejected_in_both_encodings(self):
  spec=importlib.util.spec_from_file_location('permissions',ROOT/'tools/check_apk_permissions.py')
  mod=importlib.util.module_from_spec(spec);spec.loader.exec_module(mod)
  with tempfile.TemporaryDirectory() as folder:
   apk=Path(folder)/'sample.apk'
   for encoding in ('utf-8','utf-16le'):
    with zipfile.ZipFile(apk,'w') as z:z.writestr('AndroidManifest.xml','android.permission.BIND_NOTIFICATION_LISTENER_SERVICE'.encode(encoding))
    with self.assertRaises(AssertionError):mod.inspect(apk)
   with zipfile.ZipFile(apk,'w') as z:z.writestr('AndroidManifest.xml',b'android.permission.POST_NOTIFICATIONS')
   with contextlib.redirect_stdout(io.StringIO()):mod.inspect(apk)
