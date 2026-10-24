#!/usr/bin/env python3
"""Build input: CC0 recording by Adam-synagda, unchanged, packaged for offline playback.
Source: https://commons.wikimedia.org/wiki/File:Beautiful_adhan.ogg
"""
from pathlib import Path
import hashlib, urllib.request
URL = "https://upload.wikimedia.org/wikipedia/commons/b/b0/Beautiful_adhan.ogg"
SHA256 = "35fe06b08fe80505c550c33fed8a783fa9901ddc81ac884958b4be048f5b2a79"
TARGET = Path(__file__).resolve().parents[1] / "launcher/app/src/main/res/raw/adhan.ogg"
def main():
    if TARGET.exists(): data = TARGET.read_bytes()
    else:
        request = urllib.request.Request(URL, headers={"User-Agent": "GlassSolatTV/0.1 (build; CC0 audio)"})
        with urllib.request.urlopen(request, timeout=40) as response: data = response.read(2000000)
    if hashlib.sha256(data).hexdigest() != SHA256: raise SystemExit("Audio checksum mismatch; refusing build")
    TARGET.parent.mkdir(parents=True, exist_ok=True)
    TARGET.write_bytes(data)
    print("Verified CC0 azan audio:", len(data), "bytes")
if __name__ == "__main__": main()
