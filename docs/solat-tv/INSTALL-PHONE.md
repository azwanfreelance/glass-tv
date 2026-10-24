# Pasang Glass TV + Solat menggunakan telefon Android

Status: alpha untuk ujian pengguna pada Haier MatrixTV EE, Android TV14. APK berjaya dibina tidak bermaksud fungsi perkakasan Haier sudah diuji. Jangan gunakan APK Glass TV asas daripada PR1 untuk varian ini.

## Muat turun dan pindahkan
1. Muat turun fail APK yang disertakan dalam serahan akhir ke telefon. Jika menggunakan GitHub Actions, buka binaan terakhir yang dinyatakan, log masuk GitHub, muat turun artifact `solat-tv-debug-apk` dan ekstrak ZIP sehingga mendapat `app-debug.apk`. Artifact GitHub luput selepas14hari.
2. Pasang **Send files to TV** oleh Yablio dari Google Play pada telefon dan TV: https://play.google.com/store/apps/details?id=com.yablio.sendfilestotv . Kedua-duanya perlu pada Wi-Fi yang sama.
3. Pada TV, pilih **Receive**. Pada telefon pilih **Send**, pilih fail APK, kemudian pilih TV.
4. Pada TV, buka fail yang diterima. Jika diminta, benarkan **Install unknown apps** untuk aplikasi yang membuka fail itu sahaja, kemudian pilih **Install**. Jika aplikasi pemindahan tidak boleh membuka APK, buka melalui pengurus fail TV yang anda percayai. Nama menu bergantung firmware; ikut skrin TV, jangan gunakan skrip atau debugging.
5. Kekalkan Google Play Protect. Selepas pemasangan, tutup semula kebenaran Install unknown apps untuk aplikasi pemindahan/pengurus fail.
6. Buka **Glass TV + Solat** daripada Apps. Pemasangan/pemilihan Home mesti anda lakukan sendiri selepas meluluskan ujian; tiada pemasangan jauh dijalankan.

## Tetapkan melalui remote
1. Tekan ikon Settings > **Solat TV**.
2. Pilih zon mengikut lokasi TV: **SGR01** untuk Hulu Langat/Bangi; **PRK02** untuk Ipoh. Jangan pilih berdasarkan lokasi telefon jika TV di tempat lain.
3. Muat turun jadual JAKIM. Semak lima waktu dan zon sebelum hidupkan peringatan. Jadual disimpan offline; kemas kini berkala12jam dibuat apabila Android membenarkan rangkaian/job berjalan. Selepas cache tamat tahun, muat turun jadual tahun baru; tiada waktu atau Hijri dianggar jika data seterusnya belum ada.
4. Benarkan notifikasi dan alarm tepat melalui butang dalam Settings. Hidupkan peringatan selepas status kedua-duanya OK. Android boleh menyekat selepas revoke/force-stop; buka semula aplikasi dan semak status.
5. Untuk peringatan di atas Netflix/YouTube, benarkan **Display over other apps** jika firmware menyediakan pilihan. Jika tidak, aplikasi menggunakan notifikasi; sesetengah firmware TV tidak memaparkannya seperti telefon.
6. Jika mahu cuba pause, baca penerangan **Notification access** dahulu. Ini kebenaran luas Android untuk akses MediaSession; kod ini tidak membaca/menyimpan notifikasi. Jika pilihan tidak wujud, azan menggunakan audio focus. Sokongan Netflix/YouTube tidak dijamin.
7. Pilihan pertukaran Hijri selepas Maghrib menggunakan rekod rasmi hari berikutnya. Jika rekod tiada, paparan menyatakan belum tersedia.
8. Cuba **Uji peringatan10s** dahulu. Selepas itu, **Uji azan20s + volume11** meminta pengesahan pada skrin TV. Ujian20s ialah petikan; azan berjadual memainkan keseluruhan rakaman154s. Audio biasa digunakan bagi semua lima waktu, belum ada varian Subuh khusus.
9. Semak volume asal pulih dan video kekal dijeda. Solat TV tidak menghantar PLAY. Aplikasi lain tetap boleh mengurus sambung semula sendiri; perkara ini perlu diuji pada TV.
10. **Hentikan azan / tutup peringatan** tersedia melalui Settings. Overlay tidak mengambil fokus remote. Reminder berjadual tamat15minit selepas waktu masuk, bukan15minit selepas audio tamat.

## Home dan kembali ke Google TV
Gunakan **Pilih Home / kembali ke Google TV** jika sistem menyediakan pemilih Home. Pilih Glass TV secara manual apabila anda bersedia. Jangan nyahaktifkan Google TV asal. Jika firmware tidak menyediakan pemilihan launcher, buka Glass TV daripada Apps; menggantikan fungsi butang Home tidak dijamin tanpa kaedah yang anda telah larang.

Untuk rollback, matikan peringatan, pilih Google TV melalui pemilih Home jika tersedia, kemudian uninstall **Glass TV + Solat** melalui Settings > Apps. Google TV asal kekal dipasang. Tiada root, ADB, debloat, Accessibility atau skrip pemasangan diperlukan.

## Batasan alpha
- TV perlu hidup/interactive; event ketika standby dilangkau. Aplikasi tidak menghidupkan TV.
- Volume11 ialah indeks media Android yang dihadkan kepada maksimum peranti. Fixed-volume/HDMI soundbar mungkin mengekalkan volume sendiri.
- Volume asal disimpan sebelum perubahan dan dipulihkan pada selesai, ralat, stop atau watchdog180s jika proses mati. Jika Android membunuh seluruh aplikasi sambil membatalkan semua alarm (misalnya revoke exact-alarm atau force-stop), pemulihan mungkin menunggu pembukaan aplikasi seterusnya.
- Cache tamat, rangkaian gagal dan pertukaran tahun tidak menghasilkan waktu rekaan. Pastikan muat turun tahun baru berjaya sebelum bergantung pada azan tahun baru.
- APK debug untuk ujian peribadi, belum keluaran production. Kunci debug runner boleh berubah antara binaan; kemas kini mungkin perlu uninstall/reinstall dan menetapkan semula zon/kebenaran. Jangan uninstall sebelum mencatat tetapan.
