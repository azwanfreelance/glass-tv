#!/usr/bin/env python3
"""Keep original launcher budget; identify audio by checksum even after resource shrinking."""
from zipfile import ZipFile
from pathlib import Path
import hashlib
import sys
AUDIO_SHA256='35fe06b08fe80505c550c33fed8a783fa9901ddc81ac884958b4be048f5b2a79'
def inspect(apk, digest=AUDIO_SHA256):
    apk=Path(apk)
    with ZipFile(apk) as z:
        entries=[i for i in z.infolist() if i.file_size==1229032 or i.filename.endswith('.ogg')]
        matches=[i for i in entries if hashlib.sha256(z.read(i)).hexdigest()==digest]
    assert len(matches)==1, 'Expected exactly one checksum-verified offline azan recording'
    total=apk.stat().st_size
    code=total-matches[0].compress_size
    assert code<409600, f'Non-audio launcher exceeds original400KB budget: {code}'
    assert total<2097152, f'Total release APK exceeds2MB: {total}'
    return total,code
if __name__=='__main__':
    total,code=inspect(sys.argv[1])
    print(f'Verified release APK: total={total} bytes; non-audio={code} bytes')
