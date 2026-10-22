# Quran TV | قرآن تي في

سكرين سيفر وتطبيق لأندرويد TV: آية عشوائية كل 10 ثواني، تلاوة، تفسير، ومواقيت الصلاة، بتصميم زجاجي فوق خلفيات عالية الجودة.

An Android TV screensaver (Daydream) and app showing random ayat, recitation, tafsir and prayer times.

## المميزات
- آية عشوائية كل 10 ثواني كسكرين سيفر
- تلاوة بـ 22 قارئ، وتكمل الآيات ورا بعض
- اختيار أي سورة من القائمة، والتفسير بضغطة
- مواقيت الصلاة مع الوقت المتبقي (80 مدينة أو تحديد تلقائي)
- خلفيات: إسلامية / طبيعة / حضارية
- مجاني وبدون إعلانات

## التثبيت
1. نزّل `QuranTV.apk` (من صفحة Releases).
2. ثبّته على التلفزيون: من فلاشة، أو بتطبيق مثل Downloader، أو بـ adb:
   ```bash
   adb connect <TV_IP>:5555
   adb install -r QuranTV.apk
   ```
3. فعّل السكرين سيفر: **Settings ← System ← Ambient mode / Screen saver ← Quran TV**.

المتطلبات: Android 8.0 أو أحدث (`minSdk 26`)، واتصال بالإنترنت. اتجرّب على TCL Google TV (Android 11) فقط.

## الخصوصية
التطبيق بيحتاج إنترنت وبيكلّم الخدمات دي بس: `api.alquran.cloud` و`everyayah.com` و`api.aladhan.com` و`ipwho.is` (لتحديد المدينة تلقائياً). مفيش تتبع ولا إعلانات.

## هيكل المشروع
```
AndroidManifest.xml
src/sa/riyadh/prayerdream/   كود Java (Activity وDreamService والواجهة)
res/drawable-xhdpi/          الأيقونة والـ banner
```
الخطوط والخلفيات (`assets/`) موجودة داخل الـ APK فقط، وليست في المستودع.

## التواصل
لو لقيت مشكلة أو عندك اقتراح: افتح [Issue](../../issues) أو راسلني على mohammednagyy01@gmail.com

## Credits
مصادر البيانات وحقوق الصور في [CREDITS.md](CREDITS.md).

## License
[MIT](LICENSE)
