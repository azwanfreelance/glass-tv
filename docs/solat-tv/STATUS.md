# Current status: Alpha2 installation fix

User's TV screenshot shows Alpha1 was NOT installed: Play Protect displays “App blocked to protect your device” and a sensitive-data/financial-fraud warning. The earlier handoff only proved builds/tests, not installation. Google guidance matches the notification-listener permission used for optional MediaSession control: https://developers.google.com/android/play-protect/warning-dev-guidance . This is the most strongly supported cause; no Play Protect engine verdict/log was accessed.

Alpha2 genuinely removes NotificationListenerService and the entire getActiveSessions/transport-controls implementation and permission UI. Audio focus is the only video-interruption mechanism; direct MediaSession PAUSE is no longer available. Own reminder notifications (POST_NOTIFICATIONS), overlay, azan/volume recovery, exact alarms, JAKIM and Hijri remain. Play Protect stays ON. New regression guards and a packaged-manifest permission check must pass. Device installation acceptance remains unverified until the user retries; do not claim Google approval or guaranteed installation.

Changes are on fix/solat-tv-notification-access in a separate draft PR, no merge/deployment.

---
## Historical Alpha1 build evidence (superseded for installation)

# Glass TV + Solat TV — 24 October 2026

Requested: Glass TV original launcher for Haier MatrixTV EE Android TV14; JAKIM five prayers, selected zone, offline schedule; official Hijri with optional Maghrib rollover; prealert at -10 minutes, visible10 seconds; full azan, volume11 where supported, restore original volume, optional MediaSession PAUSE; reminder until prayer+15 minutes; no automatic PLAY.

Safety: no root, debloat, ADB connection, stock launcher disabling or Accessibility control. No TV deployment or merge authorised. Original upstream installation scripts are NOT part of the approved installation route.

Baseline: PR1 head7a5d9bed60deea701d1bfb5a73be70189c96618c. Build40048968098 and CI40048968087 succeeded. Artifact12542129099 is the unmodified upstream debug launcher, NOT the prayer version. Self-ADB cache cleaner exists upstream; baseline APK is not recommended for this user's safety requirements.

Haier MatrixTV EE is not listed as hardware-tested in upstream docs/DEVICES.md. Android14 API compatibility is not proof of hardware behavior. Stock HOME picker availability, notification UI, overlay permission, exact alarms, volume/HDMI control and streaming pause require user-approved device testing.

Phase2: PR2 head53c6b4cd4b670fa45eff575ead7183f3b1835782 passed both Actions workflows (build40049474272, CI40049474210). Self-ADB and Accessibility code removed; original launcher preserved.

Phase3: official 60-zone catalogue captured from https://www.e-solat.gov.my/index.php?pageId=50&siteId=24 on 24 October2026. API year response verified live for SGR01, dated rows include hijri/date and five prayers. No estimated Hijri conversion or prayer calculation.

Phase3 build40049979835 and CI40049979839 passed after correcting an escaped multiline help string. Live365-day SGR01 payload exposed Malay month abbreviations; regression now covers all12 months, and the live payload parses2026-01-01 through2026-12-31. This parser correction is included in the reminder phase.

Phase4 first debug APK build40050043848 passed. Release build also compiled; the first size check could not identify the R8-renamed audio resource. The check now identifies audio by its verified content hash and passes. The intermediate APK is superseded by the final artifact below.

Independent code review: immediate foreground-notification behavior is required for a10-second prealert; notification permission/channel readiness is rechecked before side effects. Process-death volume recovery uses a separate exact restore alarm. No device test has run.


## Verified alpha deliverable
- Source commit: `4b762d85e4bad7ce77a496904accaa568cadf53c` on `feature/solat-tv-reminders`.
- Debug build + unit tests: https://github.com/azwanfreelance/glass-tv/actions/runs/40050506418 — success.
- Release build + unit tests + APK budget + Python checks: https://github.com/azwanfreelance/glass-tv/actions/runs/40050506440 — success.
- Release artifact12542753036: `solat-tv-release-apk`; original output `app-release.apk`, handed over as `Glass_TV_Solat_Alpha1.apk`.
- APK1,546,358bytes; non-audio317,326bytes, within original400KB code budget. Full154-second CC0 recording packaged and hash-verified after extraction; APK signing block present.
- APK SHA256: `1b2fc1d41d29c1211046f3b04fcee766a17979e96b4fdbb219d4a0f6f35d1d53`.
- New pure JVM tests:22passed. Local Python suite:10tests,0failures,1pre-existing skip. Full Android debug/release suites pass in Actions.
- Independent review fixes verified; volume watchdog preserves the reminder window if process is still alive.
- PR1–4 remain draft/unmerged. No TV deployment, connection, screenshot or ADB operation occurred.

Implemented in the alpha: original Glass TV Home, JAKIM zone selection/offline annual cache, next prayer and official Hijri/Maghrib rollover,10-second prealert10minutes early, full offline azan, capped volume11 with restore/failsafe, optional MediaSession PAUSE/audio-focus fallback, reminder until15minutes after prayer and manual stop. Reminders start OFF until the user configures zone and permissions.

Not verified on hardware: Haier HOME chooser, remote layout/focus, overlay/notification availability, exact-alarm reliability, Netflix/YouTube pause or spontaneous resume, device/soundbar volume mapping, reboot/standby lifecycle. TV standby events are deliberately skipped. Audio is ordinary azan for allfive prayers, no special Subuh recording. Cache/year rollover depends on successfully fetching the new official rows. Debug signing is used for this personal alpha, so later CI builds may require reinstall if the runner signing key changes. See INSTALL-PHONE.md for phone-only installation and rollback.
