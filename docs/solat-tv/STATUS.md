# Glass TV + Solat TV — 24 October 2026

Requested: Glass TV original launcher for Haier MatrixTV EE Android TV14; JAKIM five prayers, selected zone, offline schedule; official Hijri with optional Maghrib rollover; prealert at -10 minutes, visible10 seconds; full azan, volume11 where supported, restore original volume, optional MediaSession PAUSE; reminder until prayer+15 minutes; no automatic PLAY.

Safety: no root, debloat, ADB connection, stock launcher disabling or Accessibility control. No TV deployment or merge authorised. Original upstream installation scripts are NOT part of the approved installation route.

Baseline: PR1 head7a5d9bed60deea701d1bfb5a73be70189c96618c. Build40048968098 and CI40048968087 succeeded. Artifact12542129099 is the unmodified upstream debug launcher, NOT the prayer version. Self-ADB cache cleaner exists upstream; baseline APK is not recommended for this user's safety requirements.

Haier MatrixTV EE is not listed as hardware-tested in upstream docs/DEVICES.md. Android14 API compatibility is not proof of hardware behavior. Stock HOME picker availability, notification UI, overlay permission, exact alarms, volume/HDMI control and streaming pause require user-approved device testing.

Phase2: PR2 head53c6b4cd4b670fa45eff575ead7183f3b1835782 passed both Actions workflows (build40049474272, CI40049474210). Self-ADB and Accessibility code removed; original launcher preserved.

Phase3: official 60-zone catalogue captured from https://www.e-solat.gov.my/index.php?pageId=50&siteId=24 on 24 October2026. API year response verified live for SGR01, dated rows include hijri/date and five prayers. No estimated Hijri conversion or prayer calculation.

Phase3 build40049979835 and CI40049979839 passed after correcting an escaped multiline help string. Live365-day SGR01 payload exposed Malay month abbreviations; regression now covers all12 months, and the live payload parses2026-01-01 through2026-12-31. This parser correction is included in the reminder phase.

Phase4 first debug APK build40050043848 passed. Release build also compiled; its separate APK size check failed and is being investigated. Final deliverable must use the post-review commit, not this intermediate APK.

Independent code review: immediate foreground-notification behavior is required for a10-second prealert; notification permission/channel readiness is rechecked before side effects. Process-death volume recovery uses a separate exact restore alarm. No device test has run.
