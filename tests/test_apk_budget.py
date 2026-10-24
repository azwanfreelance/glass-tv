import hashlib, importlib.util, tempfile, unittest, zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
class ApkBudgetTest(unittest.TestCase):
 def test_shrunk_audio_is_found_by_bytes_not_filename(self):
  spec=importlib.util.spec_from_file_location('budget',ROOT/'tools/check_apk_size.py')
  mod=importlib.util.module_from_spec(spec);spec.loader.exec_module(mod)
  with tempfile.TemporaryDirectory() as d:
   path=Path(d)/'app.apk'
   with zipfile.ZipFile(path,'w') as z:z.writestr('res/a.ogg',b'full audio');z.writestr('classes.dex',b'code')
   total,code=mod.inspect(path,hashlib.sha256(b'full audio').hexdigest())
   self.assertLess(code,total)
   with self.assertRaises(AssertionError):mod.inspect(path,'0'*64)
