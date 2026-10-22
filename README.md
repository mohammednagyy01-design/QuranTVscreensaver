# قرآن تي في | Quran TV

[العربية](#العربية) | [English](#english)

---

## العربية

**قرآن تي في** تطبيقٌ وشاشةُ توقّفٍ (Screensaver) لأجهزة التلفاز العاملة بنظام Android TV و Google TV. حين يتوقّف التلفاز عن العرض ويدخل في وضع السكون، تظهر على الشاشة آيةٌ من القرآن الكريم تتجدّد كل عشر ثوانٍ، بتصميمٍ زجاجيٍّ أنيق فوق خلفياتٍ عالية الجودة، بدلًا من الشاشة الفارغة.

### المميزات
- **آيةٌ من القرآن الكريم تتجدّد كل 10 ثوانٍ:** تُعرض على الشاشة وقت السكون، فيقع نظرُك على كلام الله كلما نظرتَ إلى التلفاز.
- **التلاوة بأصوات 22 قارئًا:** يمكنك اختيار القارئ الذي تحبّ سماعه، وتتابع التلاوة آيةً بعد آيةٍ دون توقّف، كما في القراءة المعتادة.
- **اختيار السورة:** يمكنك اختيار أيّ سورةٍ من قائمة السور، وبدء قراءتها أو سماعها.
- **التفسير:** يظهر تفسير الآية بضغطةٍ واحدة من جهاز التحكم، دون الحاجة إلى مغادرة الشاشة.
- **مواقيت الصلاة:** تُعرض أوقات الصلوات الخمس مع الوقت المتبقي على الصلاة القادمة. يمكنك اختيار مدينتك من بين 80 مدينة، أو تركُ التطبيق يحدّدها تلقائيًا.
- **خلفيات متنوّعة:** يمكنك الاختيار بين ثلاث مجموعات: إسلامية، وطبيعة، وحضارية.
- **مجانيٌّ بلا إعلانات:** لا يتضمّن التطبيق أيّ إعلانات أو اشتراكات.

### التثبيت
1. **التحميل:** نزّل ملف `QuranTV.apk` من [صفحة آخر إصدار](../../releases/latest). ملف APK هو ملف تثبيت التطبيقات على أجهزة أندرويد.
2. **النقل إلى التلفاز:** ثبّته على التلفاز بإحدى الطرق الآتية:
   - من ذاكرة USB: انسخ الملف إليها ثم افتحه من مدير الملفات في التلفاز.
   - بتطبيق مثل Downloader: أدخل فيه رابط التحميل المباشر.
   - بأداة adb من الحاسوب، بعد تفعيل خيارات المطوّر وتصحيح USB على التلفاز:
     ```bash
     adb connect <TV_IP>:5555
     adb install -r QuranTV.apk
     ```
     (استبدل `<TV_IP>` بعنوان IP الخاص بالتلفاز، وتجده في إعدادات الشبكة.)
3. **تفعيل شاشة التوقّف:** من إعدادات التلفاز اختر **Settings ← System ← Ambient mode (أو Screen saver) ← Quran TV**. بعد ذلك يعمل التطبيق تلقائيًا كلما دخل التلفاز في وضع السكون.

**المتطلبات:** نظام Android 8.0 أو أحدث (`minSdk 26`)، واتصال بالإنترنت لأنّ الآيات والتلاوة والمواقيت تُحمَّل عبر الشبكة. وقد جُرّب التطبيق على تلفاز TCL يعمل بنظام Google TV (Android 11) فقط، فإن جرّبته على جهازٍ آخر فيسعدنا أن تخبرنا بالنتيجة.

### الخصوصية
- يحتاج التطبيق إلى الإنترنت، ولا يتّصل إلا بهذه الخدمات، ولكلٍّ منها غرضٌ محدّد:
  - `api.alquran.cloud`: لجلب نصّ الآيات وتفسيرها.
  - `everyayah.com`: لجلب التلاوة الصوتية.
  - `api.aladhan.com`: لحساب مواقيت الصلاة لمدينتك.
  - `ipwho.is`: لتحديد مدينتك تقريبيًا من عنوان الإنترنت (IP) عند اختيار التحديد التلقائي.
- لا يتضمّن التطبيق أيّ أدوات تتبّع أو إعلانات، ولا يجمع بياناتك الشخصية.
- تُحفَظ تفضيلاتك (مثل القارئ والمدينة) على جهاز التلفاز نفسه فقط، ولا تُرسَل إلى أيّ جهة.

### هيكل المشروع
```
AndroidManifest.xml          إعدادات التطبيق والصلاحيات
src/sa/riyadh/prayerdream/   الكود المصدري بلغة Java (الواجهة، وخدمة شاشة التوقّف)
res/drawable-xhdpi/          أيقونة التطبيق وصورة الـ banner التي تظهر في قائمة التلفاز
```
الخطوط والخلفيات (مجلد `assets/`) مضمَّنة داخل ملف APK فقط، وليست موجودة في هذا المستودع.

### التواصل
إن وجدتَ مشكلةً أو كان لديك اقتراحٌ، يسعدنا تواصلك: افتح [Issue](../../issues) في هذا المستودع، أو راسلنا على mohammednagyy01@gmail.com

### المصادر وحقوق الصور
مصادر البيانات وحقوق الصور محفوظة في ملف [CREDITS.md](CREDITS.md).

### الرخصة
المشروع مرخَّص برخصة [MIT](LICENSE)، وتتيح لك استخدامه وتعديله ومشاركته بحرية مع الإبقاء على إشعار حقوق المؤلف.

---

## English

**Quran TV** is an app and screensaver (Daydream) for Android TV and Google TV devices. When the TV goes idle, instead of a blank screen it shows an ayah from the Holy Quran that refreshes every 10 seconds, in an elegant glass-style design over high-quality backgrounds.

### Features
- **An ayah from the Holy Quran that refreshes every 10 seconds:** it appears on screen while the TV is idle, so every glance at the TV meets the words of Allah.
- **Recitation by 22 reciters:** choose the reciter you like to hear, and the recitation continues ayah after ayah without stopping, as in normal reading.
- **Surah selection:** pick any surah from the list and start reading or listening to it.
- **Tafsir:** the explanation of the ayah opens with a single press of the remote, without leaving the screen.
- **Prayer times:** the five daily prayer times are shown with the time remaining until the next prayer. Choose your city from 80 cities, or let the app detect it automatically.
- **Backgrounds:** choose from three collections: Islamic, nature and civilization.
- **Free, with no ads:** the app contains no advertisements or subscriptions.

### Install
1. **Download:** get `QuranTV.apk` from the [latest release](../../releases/latest). An APK is the installation file for Android apps.
2. **Move it to the TV:** install it using one of these methods:
   - From a USB drive: copy the file onto it, then open it from the TV's file manager.
   - With an app such as Downloader: enter the direct download link in it.
   - With adb from a computer, after enabling Developer options and USB debugging on the TV:
     ```bash
     adb connect <TV_IP>:5555
     adb install -r QuranTV.apk
     ```
     (Replace `<TV_IP>` with the TV's IP address, which you can find in its network settings.)
3. **Enable the screensaver:** in the TV settings go to **Settings → System → Ambient mode (or Screen saver) → Quran TV**. From then on the app runs automatically whenever the TV goes idle.

**Requirements:** Android 8.0 or newer (`minSdk 26`) and an internet connection, because the ayat, recitation and prayer times are loaded online. The app has been tested only on a TCL TV running Google TV (Android 11); if you try it on another device, we would be glad to hear how it went.

### Privacy
- The app needs the internet and connects only to the services below, each for one specific purpose:
  - `api.alquran.cloud`: to fetch the text and tafsir of the ayat.
  - `everyayah.com`: to fetch the audio recitation.
  - `api.aladhan.com`: to calculate prayer times for your city.
  - `ipwho.is`: to estimate your city from your IP address when automatic detection is selected.
- The app contains no tracking tools or ads, and does not collect your personal data.
- Your preferences (such as the reciter and the city) are stored on the TV itself only and are not sent anywhere.

### Project layout
```
AndroidManifest.xml          app configuration and permissions
src/sa/riyadh/prayerdream/   Java source (the UI and the screensaver service)
res/drawable-xhdpi/          the app icon and the banner shown in the TV launcher
```
The fonts and backgrounds (the `assets/` folder) are bundled in the APK only and are not part of this repository.

### Contact
If you find a problem or have a suggestion, we would be glad to hear from you: open an [issue](../../issues) in this repository, or email mohammednagyy01@gmail.com

### Credits
Data sources and image attributions are listed in [CREDITS.md](CREDITS.md).

### License
The project is released under the [MIT License](LICENSE), which lets you use, modify and share it freely as long as the copyright notice is kept.
