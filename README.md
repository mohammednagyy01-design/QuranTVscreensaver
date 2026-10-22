# Quran TV | قرآن تي في

[English](#english) | [العربية](#العربية)

---

## English

An Android TV screensaver (Daydream) and app that shows random ayat, recitation, tafsir and prayer times, in a glass-style design over high-quality backgrounds.

### Features
- A random ayah every 10 seconds as a screensaver
- Recitation by 22 reciters, continuing ayah after ayah
- Pick any surah from the list, and open the tafsir with one press
- Prayer times with time remaining (80 cities, or automatic location)
- Backgrounds: Islamic / nature / civilization
- Free, no ads

### Install
1. Download `QuranTV.apk` from the [latest release](../../releases/latest).
2. Install it on your TV from a USB drive, with an app such as Downloader, or with adb:
   ```bash
   adb connect <TV_IP>:5555
   adb install -r QuranTV.apk
   ```
3. Enable the screensaver: **Settings → System → Ambient mode / Screen saver → Quran TV**.

Requirements: Android 8.0 or newer (`minSdk 26`) and an internet connection. Tested on a TCL Google TV (Android 11) only.

### Privacy
The app needs internet and talks only to these services: `api.alquran.cloud`, `everyayah.com`, `api.aladhan.com` and `ipwho.is` (to detect your city automatically). No tracking, no ads.

### Project layout
```
AndroidManifest.xml
src/sa/riyadh/prayerdream/   Java source (Activity, DreamService, UI)
res/drawable-xhdpi/          icon and banner
```
Fonts and backgrounds (`assets/`) are bundled in the APK only and are not in this repository.

### Contact
Found a bug or have a suggestion? Open an [issue](../../issues) or email mohammednagyy01@gmail.com

### Credits
Data sources and image attributions: [CREDITS.md](CREDITS.md).

### License
[MIT](LICENSE)

---

## العربية

سكرين سيفر وتطبيق لأندرويد TV: آية عشوائية كل 10 ثواني، تلاوة، تفسير، ومواقيت الصلاة، بتصميم زجاجي فوق خلفيات عالية الجودة.

### المميزات
- آية عشوائية كل 10 ثواني كسكرين سيفر
- تلاوة بـ 22 قارئ، وتكمل الآيات ورا بعض
- اختيار أي سورة من القائمة، والتفسير بضغطة
- مواقيت الصلاة مع الوقت المتبقي (80 مدينة أو تحديد تلقائي)
- خلفيات: إسلامية / طبيعة / حضارية
- مجاني وبدون إعلانات

### التثبيت
1. نزّل `QuranTV.apk` من [آخر إصدار](../../releases/latest).
2. ثبّته على التلفزيون: من فلاشة، أو بتطبيق مثل Downloader، أو بـ adb:
   ```bash
   adb connect <TV_IP>:5555
   adb install -r QuranTV.apk
   ```
3. فعّل السكرين سيفر: **Settings ← System ← Ambient mode / Screen saver ← Quran TV**.

المتطلبات: Android 8.0 أو أحدث (`minSdk 26`)، واتصال بالإنترنت. اتجرّب على TCL Google TV (Android 11) فقط.

### الخصوصية
التطبيق بيحتاج إنترنت وبيكلّم الخدمات دي بس: `api.alquran.cloud` و`everyayah.com` و`api.aladhan.com` و`ipwho.is` (لتحديد المدينة تلقائياً). مفيش تتبع ولا إعلانات.

### هيكل المشروع
```
AndroidManifest.xml
src/sa/riyadh/prayerdream/   كود Java (Activity وDreamService والواجهة)
res/drawable-xhdpi/          الأيقونة والـ banner
```
الخطوط والخلفيات (`assets/`) موجودة داخل الـ APK فقط، وليست في المستودع.

### التواصل
لو لقيت مشكلة أو عندك اقتراح: افتح [Issue](../../issues) أو راسلني على mohammednagyy01@gmail.com

### المصادر والحقوق
مصادر البيانات وحقوق الصور في [CREDITS.md](CREDITS.md).

### الرخصة
[MIT](LICENSE)
