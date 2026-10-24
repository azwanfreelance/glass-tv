#!/usr/bin/env python3
"""Check the packaged manifest, including merged library components, for excessive access."""
from zipfile import ZipFile
import sys
FORBIDDEN = (
    'android.permission.BIND_NOTIFICATION_LISTENER_SERVICE',
    'android.permission.BIND_ACCESSIBILITY_SERVICE',
    'android.permission.READ_SMS',
    'android.permission.RECEIVE_SMS',
    'android.service.notification.NotificationListenerService',
)
def inspect(apk):
    with ZipFile(apk) as archive:
        manifest = archive.read('AndroidManifest.xml')
    # Android binary XML string pools encode complete strings as UTF8 or UTF16LE.
    found = [name for name in FORBIDDEN if any(name.encode(codec) in manifest for codec in ('utf-8','utf-16le'))]
    assert not found, 'Unexpected sensitive access in packaged manifest: ' + ', '.join(found)
    print('Packaged manifest: no notification-listener, Accessibility or SMS access')
if __name__ == '__main__':
    inspect(sys.argv[1])
