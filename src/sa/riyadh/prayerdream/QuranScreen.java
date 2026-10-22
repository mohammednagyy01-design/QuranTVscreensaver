package sa.riyadh.prayerdream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/** Quran TV screen (shared by the screensaver service and the launcher activity). */
public class QuranScreen {
    private static final String TAG = "PrayerDream";
    private static final String[] PKEYS = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
    private static final String[] PNAMES = {"الفجر", "الظهر", "العصر", "المغرب", "العشاء"};
    private TimeZone tz = TimeZone.getTimeZone("Asia/Riyadh");
    private static final String[] G_MONTHS = {"يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"};
    private static final String[] H_MONTHS = {"محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى",
            "جمادى الآخرة", "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"};

    private static final String[][] RECITERS = {
        {"Alafasy_128kbps", "مشاري راشد العفاسي"},
        {"Abdurrahmaan_As-Sudais_192kbps", "عبد الرحمن السديس"},
        {"Saood_ash-Shuraym_128kbps", "سعود الشريم"},
        {"MaherAlMuaiqly128kbps", "ماهر المعيقلي"},
        {"Husary_128kbps", "محمود خليل الحصري"},
        {"Minshawy_Murattal_128kbps", "محمد صديق المنشاوي"},
        {"Abdul_Basit_Murattal_192kbps", "عبد الباسط عبد الصمد"},
        {"Hudhaify_128kbps", "علي الحذيفي"},
        {"Ghamadi_40kbps", "سعد الغامدي"},
        {"Hani_Rifai_192kbps", "هاني الرفاعي"},
        {"Abdullah_Basfar_192kbps", "عبد الله بصفر"},
        {"Salah_Al_Budair_128kbps", "صلاح البدير"},
        {"Muhammad_Ayyoub_128kbps", "محمد أيوب"},
        {"Yasser_Ad-Dussary_128kbps", "ياسر الدوسري"},
        {"Nasser_Alqatami_128kbps", "ناصر القطامي"},
        {"Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net", "أحمد العجمي"},
        {"Muhsin_Al_Qasim_192kbps", "محسن القاسم"},
        {"Abdullah_Matroud_128kbps", "عبد الله مطرود"},
        {"Fares_Abbad_64kbps", "فارس عباد"},
        {"Ayman_Sowaid_64kbps", "أيمن سويد"},
        {"Khaalid_Abdullaah_al-Qahtaanee_192kbps", "خالد القحطاني"},
        {"Ibrahim_Akhdar_64kbps", "إبراهيم الأخضر"},
    };

    private static final String[][] CATEGORIES = {
        {"islamic", "إسلامية"}, {"nature", "طبيعة"}, {"civilization", "حضارية"}, {"", "منوّعة"}};
    private static final float[] SPEEDS = {0.75f, 1f, 1.25f, 1.5f};
    private static final int LOOKAHEAD = 10;
    // name|country|ISO code|time zone|latitude|longitude
    private static final String[] CITY_DATA = {
        "الرياض|السعودية|SA|Asia/Riyadh|24.7136|46.6753",
        "جدة|السعودية|SA|Asia/Riyadh|21.4858|39.1925",
        "مكة المكرمة|السعودية|SA|Asia/Riyadh|21.3891|39.8579",
        "المدينة المنورة|السعودية|SA|Asia/Riyadh|24.5247|39.5692",
        "الدمام|السعودية|SA|Asia/Riyadh|26.4207|50.0888",
        "الخبر|السعودية|SA|Asia/Riyadh|26.2172|50.1971",
        "الهفوف|السعودية|SA|Asia/Riyadh|25.3647|49.5878",
        "الطائف|السعودية|SA|Asia/Riyadh|21.2703|40.4158",
        "تبوك|السعودية|SA|Asia/Riyadh|28.3835|36.5662",
        "أبها|السعودية|SA|Asia/Riyadh|18.2164|42.5053",
        "بريدة|السعودية|SA|Asia/Riyadh|26.3260|43.9750",
        "حائل|السعودية|SA|Asia/Riyadh|27.5114|41.7208",
        "جازان|السعودية|SA|Asia/Riyadh|16.8892|42.5511",
        "نجران|السعودية|SA|Asia/Riyadh|17.4917|44.1322",
        "الجبيل|السعودية|SA|Asia/Riyadh|27.0046|49.6460",
        "ينبع|السعودية|SA|Asia/Riyadh|24.0895|38.0618",
        "الباحة|السعودية|SA|Asia/Riyadh|20.0129|41.4677",
        "سكاكا|السعودية|SA|Asia/Riyadh|29.9697|40.2064",
        "عرعر|السعودية|SA|Asia/Riyadh|30.9753|41.0381",
        "القاهرة|مصر|EG|Africa/Cairo|30.0444|31.2357",
        "الإسكندرية|مصر|EG|Africa/Cairo|31.2001|29.9187",
        "الجيزة|مصر|EG|Africa/Cairo|30.0131|31.2089",
        "المنصورة|مصر|EG|Africa/Cairo|31.0409|31.3785",
        "طنطا|مصر|EG|Africa/Cairo|30.7865|31.0004",
        "الزقازيق|مصر|EG|Africa/Cairo|30.5877|31.5020",
        "بورسعيد|مصر|EG|Africa/Cairo|31.2653|32.3019",
        "الإسماعيلية|مصر|EG|Africa/Cairo|30.5965|32.2715",
        "السويس|مصر|EG|Africa/Cairo|29.9668|32.5498",
        "أسيوط|مصر|EG|Africa/Cairo|27.1809|31.1837",
        "الأقصر|مصر|EG|Africa/Cairo|25.6872|32.6396",
        "أسوان|مصر|EG|Africa/Cairo|24.0889|32.8998",
        "شرم الشيخ|مصر|EG|Africa/Cairo|27.9158|34.3300",
        "دبي|الإمارات|AE|Asia/Dubai|25.2048|55.2708",
        "أبوظبي|الإمارات|AE|Asia/Dubai|24.4539|54.3773",
        "الشارقة|الإمارات|AE|Asia/Dubai|25.3463|55.4209",
        "العين|الإمارات|AE|Asia/Dubai|24.2075|55.7447",
        "رأس الخيمة|الإمارات|AE|Asia/Dubai|25.7895|55.9432",
        "مدينة الكويت|الكويت|KW|Asia/Kuwait|29.3759|47.9774",
        "الدوحة|قطر|QA|Asia/Qatar|25.2854|51.5310",
        "المنامة|البحرين|BH|Asia/Bahrain|26.2285|50.5860",
        "مسقط|عُمان|OM|Asia/Muscat|23.5880|58.3829",
        "صلالة|عُمان|OM|Asia/Muscat|17.0151|54.0924",
        "عمّان|الأردن|JO|Asia/Amman|31.9454|35.9284",
        "إربد|الأردن|JO|Asia/Amman|32.5556|35.8479",
        "العقبة|الأردن|JO|Asia/Amman|29.5321|35.0063",
        "بيروت|لبنان|LB|Asia/Beirut|33.8938|35.5018",
        "دمشق|سوريا|SY|Asia/Damascus|33.5138|36.2765",
        "حلب|سوريا|SY|Asia/Damascus|36.2021|37.1343",
        "بغداد|العراق|IQ|Asia/Baghdad|33.3152|44.3661",
        "البصرة|العراق|IQ|Asia/Baghdad|30.5085|47.7835",
        "أربيل|العراق|IQ|Asia/Baghdad|36.1911|44.0092",
        "القدس|فلسطين|PS|Asia/Jerusalem|31.7683|35.2137",
        "غزة|فلسطين|PS|Asia/Gaza|31.5017|34.4668",
        "رام الله|فلسطين|PS|Asia/Hebron|31.9038|35.2034",
        "صنعاء|اليمن|YE|Asia/Aden|15.3694|44.1910",
        "عدن|اليمن|YE|Asia/Aden|12.7855|45.0187",
        "طرابلس|ليبيا|LY|Africa/Tripoli|32.8872|13.1913",
        "بنغازي|ليبيا|LY|Africa/Tripoli|32.1167|20.0667",
        "تونس|تونس|TN|Africa/Tunis|36.8065|10.1815",
        "الجزائر|الجزائر|DZ|Africa/Algiers|36.7538|3.0588",
        "وهران|الجزائر|DZ|Africa/Algiers|35.6987|-0.6349",
        "الرباط|المغرب|MA|Africa/Casablanca|34.0209|-6.8416",
        "الدار البيضاء|المغرب|MA|Africa/Casablanca|33.5731|-7.5898",
        "مراكش|المغرب|MA|Africa/Casablanca|31.6295|-7.9811",
        "الخرطوم|السودان|SD|Africa/Khartoum|15.5007|32.5599",
        "إسطنبول|تركيا|TR|Europe/Istanbul|41.0082|28.9784",
        "أنقرة|تركيا|TR|Europe/Istanbul|39.9334|32.8597",
        "كراتشي|باكستان|PK|Asia/Karachi|24.8607|67.0011",
        "لاهور|باكستان|PK|Asia/Karachi|31.5497|74.3436",
        "إسلام آباد|باكستان|PK|Asia/Karachi|33.6844|73.0479",
        "جاكرتا|إندونيسيا|ID|Asia/Jakarta|-6.2088|106.8456",
        "كوالالمبور|ماليزيا|MY|Asia/Kuala_Lumpur|3.1390|101.6869",
        "لندن|بريطانيا|GB|Europe/London|51.5072|-0.1276",
        "باريس|فرنسا|FR|Europe/Paris|48.8566|2.3522",
        "برلين|ألمانيا|DE|Europe/Berlin|52.5200|13.4050",
        "نيويورك|أمريكا|US|America/New_York|40.7128|-74.0060",
        "شيكاغو|أمريكا|US|America/Chicago|41.8781|-87.6298",
        "لوس أنجلوس|أمريكا|US|America/Los_Angeles|34.0522|-118.2437",
        "تورنتو|كندا|CA|America/Toronto|43.6532|-79.3832",
        "سيدني|أستراليا|AU|Australia/Sydney|-33.8688|151.2093"};
    private static final String[] SURAH_NAMES = {"الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام", "الأعراف",
        "الأنفال", "التوبة", "يونس", "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم",
        "طه", "الأنبياء", "الحج", "المؤمنون", "النور", "الفرقان", "الشعراء", "النمل", "القصص", "العنكبوت", "الروم",
        "لقمان", "السجدة", "الأحزاب", "سبأ", "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر", "فصلت", "الشورى",
        "الزخرف", "الدخان", "الجاثية", "الأحقاف", "محمد", "الفتح", "الحجرات", "ق", "الذاريات", "الطور", "النجم",
        "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة", "المنافقون",
        "التغابن", "الطلاق", "التحريم", "الملك", "القلم", "الحاقة", "المعارج", "نوح", "الجن", "المزمل", "المدثر",
        "القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس", "التكوير", "الانفطار", "المطففين", "الانشقاق",
        "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح", "التين",
        "العلق", "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر", "العصر", "الهمزة", "الفيل", "قريش",
        "الماعون", "الكوثر", "الكافرون", "النصر", "المسد", "الإخلاص", "الفلق", "الناس"};
    private static final int[] AYAH_COUNT = {7,286,200,176,120,165,206,75,129,109,123,111,43,52,99,128,111,110,98,135,112,78,118,64,77,227,93,88,69,60,34,30,73,54,45,83,182,88,75,85,54,53,89,59,37,35,38,29,18,45,60,49,62,55,78,96,29,22,24,13,14,11,11,18,12,12,30,52,52,44,28,28,20,56,40,31,50,40,46,42,29,19,36,25,22,17,19,26,30,20,15,21,11,8,8,19,5,8,8,11,11,8,3,9,5,4,7,3,6,3,5,4,5,6};
    private static final int TOTAL_AYAHS = 6236;
    // QCF surah-header font: code point of the banner glyph for surah 1..114
    private static final int[] SURAH_GLYPH = {0xFC45,0xFC46,0xFC47,0xFC4A,0xFC4B,0xFC4E,0xFC4F,0xFC51,0xFC52,0xFC53,0xFC55,0xFC56,0xFC58,0xFC5A,0xFC5B,0xFC5C,0xFC5D,0xFC5E,0xFC61,0xFC62,0xFC64,0xFB51,0xFB52,0xFB54,0xFB55,0xFB57,0xFB58,0xFB5A,0xFB5B,0xFB5D,0xFB5E,0xFB60,0xFB61,0xFB63,0xFB64,0xFB66,0xFB67,0xFB69,0xFB6A,0xFB6C,0xFB6D,0xFB6F,0xFB70,0xFB72,0xFB73,0xFB75,0xFB76,0xFB78,0xFB79,0xFB7B,0xFB7C,0xFB7E,0xFB7F,0xFB81,0xFB82,0xFB84,0xFB85,0xFB87,0xFB88,0xFB8A,0xFB8B,0xFB8D,0xFB8E,0xFB90,0xFB91,0xFB93,0xFB94,0xFB96,0xFB97,0xFB99,0xFB9A,0xFB9C,0xFB9D,0xFB9F,0xFBA0,0xFBA2,0xFBA3,0xFBA5,0xFBA6,0xFBA8,0xFBA9,0xFBAB,0xFBAC,0xFBAE,0xFBAF,0xFBB1,0xFBB2,0xFBB4,0xFBB5,0xFBB7,0xFBB8,0xFBBA,0xFBBB,0xFBBD,0xFBBE,0xFBC0,0xFBC1,0xFBD3,0xFBD4,0xFBD6,0xFBD7,0xFBD9,0xFBDA,0xFBDC,0xFBDD,0xFBDF,0xFBE0,0xFBE2,0xFBE3,0xFBE5,0xFBE6,0xFBE8,0xFBE9,0xFBEB};

    private static class Ayah {
        String text, tafsir, surahName, type;
        int surah, ayah, count;
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random rnd = new Random();
    private final String[] ptimes = new String[5];
    private final ConcurrentHashMap<Integer, Ayah[]> surahCache = new ConcurrentHashMap<>();
    private final List<int[]> history = new ArrayList<>();
    private int histPos = -1;
    private Ayah cur;

    private float scale;
    private FrameLayout root;
    private ImageView bgBottom, bgTop;
    private GlassCard ayahCard, prayerCard, chipLeft, chipRight;
    private Typeface hafs, surahFont;
    private TextView surahHead;
    private TextView clock, ampm, dateG, dateH, pill, ayahView, tafsirView, nextName, remaining, hint;
    private TextView chipLeftText, chipRightText;
    private final TextView[] pName = new TextView[5];
    private final TextView[] pTime = new TextView[5];
    private View progressBar;
    private float progress = 0f;
    private final IconView[] icons = new IconView[11];
    private static final int MENU_ROWS = 5;
    private static final int MENU_RECITER = 0, MENU_SURAH = 1, MENU_CITY = 2;
    private int cityIdx = 0; // index into CITY_DATA, -1 = detect from the internet
    private String[] cityRec = CITY_DATA[0].split("\\|");
    private TextView cityLabel;
    private int menuType = MENU_RECITER;
    private String[] menuItems = new String[MENU_ROWS];
    private int menuCurrent = -1;
    private GlassCard aboutCard;
    private boolean aboutOpen = false;
    private boolean manualHold = false;
    private GlassCard menuCard;
    private final TextView[] menuRows = new TextView[MENU_ROWS];
    private boolean menuOpen = false;
    private int menuIdx = 0;
    private long lastBgChange = 0;
    private static final long BG_MS = 60000;
    private int focus = 2;

    private SharedPreferences prefs;
    private MediaPlayer player;
    private File audioDir;
    private int reciter = 0, category = 0, speedIdx = 1;
    private boolean repeat = false, wantPlay = false, playing = false, showTafsir = false;
    private long shownAt = 0;
    private static final long IDLE_MS = 10000;
    private String loadedDay = "";
    private int loadGen = 0, bgGen = 0, bgCounter = 0, prefetchGen = 0;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            update();
            handler.postDelayed(this, 1000);
        }
    };

    // ---------- lifecycle ----------
    public interface Host { void finish(); }

    private final Context ctx;
    private final Host host;
    private boolean started = false;

    public QuranScreen(Context ctx, Host host) {
        this.ctx = ctx;
        this.host = host;
        prefs = ctx.getSharedPreferences("prayer", Context.MODE_PRIVATE);
        reciter = Math.min(prefs.getInt("reciter", 0), RECITERS.length - 1);
        category = Math.min(prefs.getInt("category", 0), CATEGORIES.length - 1);
        speedIdx = Math.min(prefs.getInt("speed", 1), SPEEDS.length - 1);
        scale = ctx.getResources().getDisplayMetrics().widthPixels / 1280f * 1.15f;
        audioDir = new File(ctx.getCacheDir(), "aud");
        audioDir.mkdirs();
        try {
            hafs = Typeface.createFromAsset(ctx.getAssets(), "fonts/UthmanicHafs_V22.ttf");
            surahFont = Typeface.createFromAsset(ctx.getAssets(), "fonts/QCF_SurahHeader_COLOR-Regular.ttf");
        } catch (Exception e) {
            hafs = Typeface.SERIF;
            surahFont = Typeface.DEFAULT;
        }
        cityIdx = prefs.getInt("city", 0);
        if (cityIdx >= CITY_DATA.length) cityIdx = 0;
        applyCity();
        buildUi();
        applyCity();
        loadPrayerCache();
    }

    public View getRoot() { return root; }

    public void start() {
        if (started) return;
        started = true;
        wantPlay = false;
        playing = false;
        showTafsir = false;
        history.clear();
        histPos = -1;
        cur = null;
        focus = 2;
        clearAudioCache();
        handler.post(ticker);
        handler.postDelayed(new Runnable() {
            @Override public void run() { hint.animate().alpha(0f).setDuration(800).start(); }
        }, 25000);
        nextBackground();
        nextAyah();
        if (cityIdx == -1) detectLocation();
        if (!prefs.getBoolean("city_prompted", false)) {
            prefs.edit().putBoolean("city_prompted", true).apply();
            handler.postDelayed(new Runnable() {
                @Override public void run() { if (!menuOpen && !aboutOpen) openMenu(MENU_CITY); }
            }, 1500);
        }
    }

    public void stop() {
        started = false;
        prefetchGen++;
        handler.removeCallbacksAndMessages(null);
        releasePlayer();
        clearAudioCache();
    }

    private void clearAudioCache() {
        File[] fs = audioDir.listFiles();
        if (fs != null) for (File f : fs) f.delete();
    }

    // ---------- UI ----------
    private float s(float v) { return v * scale; }

    private TextView tv(float px, int color, boolean bold, int gravity) {
        TextView t = new TextView(ctx);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, s(px));
        t.setTextColor(color);
        t.setGravity(gravity);
        t.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        t.setShadowLayer(s(4), 0, s(1), 0x66000000);
        t.setIncludeFontPadding(false);
        return t;
    }

    private LinearLayout.LayoutParams lp(int w, int h, float topMargin) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h);
        p.topMargin = (int) s(topMargin);
        return p;
    }

    private void buildUi() {
        root = new FrameLayout(ctx);
        root.setBackgroundColor(Color.BLACK);
        bgBottom = new ImageView(ctx);
        bgTop = new ImageView(ctx);
        bgBottom.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bgTop.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bgTop.setAlpha(0f);
        root.addView(bgBottom, new FrameLayout.LayoutParams(-1, -1));
        root.addView(bgTop, new FrameLayout.LayoutParams(-1, -1));
        View dim = new View(ctx);
        dim.setBackgroundColor(0x1A000000); // 10% darkening over the photo
        root.addView(dim, new FrameLayout.LayoutParams(-1, -1));

        // top-left: time + dates
        LinearLayout top = new LinearLayout(ctx);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        clock = tv(30, Color.WHITE, true, Gravity.START);
        ampm = tv(13, 0xFFE6E6E6, false, Gravity.START);
        View div = new View(ctx);
        div.setBackgroundColor(0x80FFFFFF);
        LinearLayout dates = new LinearLayout(ctx);
        dates.setOrientation(LinearLayout.VERTICAL);
        dateG = tv(13, Color.WHITE, true, Gravity.START);
        dateH = tv(13, Color.WHITE, true, Gravity.START);
        dates.addView(dateG);
        dates.addView(dateH, lp(-2, -2, 3));
        top.addView(clock);
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-2, -2);
        ap.leftMargin = (int) s(5);
        ap.topMargin = (int) s(8);
        top.addView(ampm, ap);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams((int) Math.max(1, s(1)), (int) s(30));
        dp.leftMargin = (int) s(12);
        dp.rightMargin = (int) s(12);
        top.addView(div, dp);
        top.addView(dates);
        FrameLayout.LayoutParams tp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.LEFT);
        tp.setMargins((int) s(28), (int) s(24), 0, 0);
        root.addView(top, tp);

        TextView dua = tv(17, Color.WHITE, false, Gravity.RIGHT);
        dua.setTypeface(hafs);
        dua.setText("اللهم اجعل منزلنا منزلاً مباركاً");
        FrameLayout.LayoutParams dpp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.RIGHT);
        dpp.setMargins(0, (int) s(28), (int) s(28), 0);
        root.addView(dua, dpp);

        // centre: ayah glass card
        ayahCard = new GlassCard(ctx, s(15), Math.max(1.5f, s(1.3f)));
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        int pad = (int) s(22);
        col.setPadding(pad, (int) s(10), pad, (int) s(10));

        surahHead = tv(60, Color.WHITE, false, Gravity.CENTER);
        surahHead.setTypeface(surahFont);
        pill = tv(10.5f, Color.WHITE, true, Gravity.CENTER);
        pill.setPadding((int) s(12), (int) s(5), (int) s(12), (int) s(5));
        pill.setBackground(pillBg());
        ayahView = tv(22, Color.WHITE, false, Gravity.CENTER);
        ayahView.setTypeface(hafs);
        ayahView.setLineSpacing(0, 1.12f);
        ayahView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ayahView.setMaxWidth((int) s(540));
        tafsirView = tv(12, 0xFFF2F2F2, false, Gravity.CENTER);
        tafsirView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        tafsirView.setLineSpacing(0, 1.2f);
        tafsirView.setMaxWidth((int) s(540));
        tafsirView.setMaxLines(5);
        tafsirView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tafsirView.setVisibility(View.GONE);

        LinearLayout controls = new LinearLayout(ctx);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER);
        int[] types = {IconView.REPEAT, IconView.PREV, IconView.PLAY, IconView.NEXT,
                IconView.SPEED, IconView.MIC, IconView.LIST, IconView.PIN, IconView.IMAGE, IconView.BOOK, IconView.INFO};
        for (int i = 0; i < 11; i++) {
            icons[i] = new IconView(ctx, types[i], Math.max(1.5f, s(1.4f)));
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams((int) s(30), (int) s(30));
            ip.leftMargin = (int) s(4);
            ip.rightMargin = (int) s(4);
            controls.addView(icons[i], ip);
        }
        col.addView(surahHead, lp(-2, -2, 0));
        col.addView(pill, lp(-2, -2, 4));
        col.addView(ayahView, lp(-2, -2, 9));
        col.addView(tafsirView, lp(-2, -2, 8));
        col.addView(controls, lp(-2, -2, 11));
        ayahCard.addView(col, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER_HORIZONTAL | Gravity.TOP));
        ayahCard.setMinimumWidth((int) s(540));
        root.addView(ayahCard, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));

        // bottom-centre: prayer card
        prayerCard = new GlassCard(ctx, s(11), Math.max(1.5f, s(1.1f)));
        LinearLayout pc = new LinearLayout(ctx);
        pc.setOrientation(LinearLayout.VERTICAL);
        pc.setPadding((int) s(14), (int) s(8), (int) s(14), (int) s(9));
        LinearLayout head = new LinearLayout(ctx);
        head.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout nextBox = new LinearLayout(ctx);
        nextBox.setOrientation(LinearLayout.VERTICAL);
        TextView nl = tv(9.5f, 0xFFD0D0D0, false, Gravity.RIGHT);
        nl.setText("الصلاة القادمة");
        nextName = tv(13, Color.WHITE, true, Gravity.RIGHT);
        nextBox.addView(nl, lp(-1, -2, 0));
        nextBox.addView(nextName, lp(-1, -2, 2));
        LinearLayout remBox = new LinearLayout(ctx);
        remBox.setOrientation(LinearLayout.VERTICAL);
        TextView rl = tv(9.5f, 0xFFD0D0D0, false, Gravity.LEFT);
        rl.setText("الوقت المتبقي");
        remaining = tv(13, Color.WHITE, true, Gravity.LEFT);
        remBox.addView(rl, lp(-1, -2, 0));
        remBox.addView(remaining, lp(-1, -2, 2));
        head.addView(remBox, new LinearLayout.LayoutParams(0, -2, 1f));
        head.addView(nextBox, new LinearLayout.LayoutParams(0, -2, 1f));
        cityLabel = tv(10, 0xFFE8C872, true, Gravity.CENTER);
        cityLabel.setText(cityRec[0] + " • " + cityRec[1]);
        pc.addView(cityLabel, lp(-1, -2, 0));
        pc.addView(head, lp(-1, -2, 5));

        progressBar = new View(ctx) {
            private final Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);
            @Override protected void onDraw(Canvas c) {
                float h = getHeight(), w = getWidth();
                pt.setColor(0x55FFFFFF);
                c.drawRoundRect(0, 0, w, h, h / 2, h / 2, pt);
                pt.setColor(0xFFB5E86A);
                c.drawRoundRect(0, 0, Math.max(h, w * progress), h, h / 2, h / 2, pt);
            }
        };
        pc.addView(progressBar, lp(-1, (int) Math.max(3, s(2.5f)), 7));

        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        for (int i = 0; i < 5; i++) {
            LinearLayout c = new LinearLayout(ctx);
            c.setOrientation(LinearLayout.VERTICAL);
            c.setGravity(Gravity.CENTER_HORIZONTAL);
            pName[i] = tv(10.5f, Color.WHITE, true, Gravity.CENTER);
            pName[i].setText(PNAMES[i]);
            pTime[i] = tv(12, Color.WHITE, true, Gravity.CENTER);
            pTime[i].setText("--:--");
            c.addView(pName[i]);
            c.addView(pTime[i], lp(-2, -2, 4));
            row.addView(c, new LinearLayout.LayoutParams(0, -2, 1f));
        }
        pc.addView(row, lp(-1, -2, 8));
        prayerCard.addView(pc, new FrameLayout.LayoutParams((int) s(400), -2));
        FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        pp.bottomMargin = (int) s(46);
        root.addView(prayerCard, pp);

        // bottom corners: reciter chip + background chip
        chipLeft = chip();
        chipLeftText = (TextView) chipLeft.getChildAt(0);
        FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.LEFT);
        l.setMargins((int) s(28), 0, 0, (int) s(46));
        root.addView(chipLeft, l);
        chipRight = chip();
        chipRightText = (TextView) chipRight.getChildAt(0);
        FrameLayout.LayoutParams r = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.RIGHT);
        r.setMargins(0, 0, (int) s(28), (int) s(46));
        root.addView(chipRight, r);

        menuCard = new GlassCard(ctx, s(12), Math.max(1.5f, s(1.1f))).scrim(0x66000000);
        LinearLayout mcol = new LinearLayout(ctx);
        mcol.setOrientation(LinearLayout.VERTICAL);
        mcol.setPadding((int) s(8), (int) s(8), (int) s(8), (int) s(8));
        for (int i = 0; i < MENU_ROWS; i++) {
            menuRows[i] = tv(13, Color.WHITE, false, Gravity.CENTER);
            menuRows[i].setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            menuRows[i].setPadding((int) s(10), (int) s(5), (int) s(10), (int) s(5));
            mcol.addView(menuRows[i], new LinearLayout.LayoutParams((int) s(250), -2));
        }
        menuCard.addView(mcol, new FrameLayout.LayoutParams(-2, -2));
        menuCard.setVisibility(View.GONE);
        root.addView(menuCard, new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.LEFT));

        aboutCard = new GlassCard(ctx, s(16), Math.max(1.5f, s(1.3f))).scrim(0x55000000);
        LinearLayout acol = new LinearLayout(ctx);
        acol.setOrientation(LinearLayout.VERTICAL);
        acol.setGravity(Gravity.CENTER_HORIZONTAL);
        acol.setPadding((int) s(30), (int) s(22), (int) s(30), (int) s(22));
        addAbout(acol, "Quran TV", 26, 0xFFE8C872, true, 0);
        addAbout(acol, "سكرين سيفر وتطبيق لشاشات التلفزيون: آية عشوائية، وتلاوة بصوت الشيخ الذي تختاره، ومواعيد الصلاة", 12.5f, Color.WHITE, false, 6);
        addAbout(acol, "المطوّر: Mohamed Nagy", 15, Color.WHITE, true, 16);
        addAbout(acol, "للتواصل: mohammednagyy01@gmail.com", 12.5f, 0xFFE0E0E0, false, 3);
        addAbout(acol, "المشروع مستوحى من إضافة Quran Tab\nchromewebstore.google.com/detail/quran-tab\nومن أدوات Muslim Daily Tools\nmuslimdailytools.com/#tools", 12, Color.WHITE, false, 16);
        addAbout(acol, "كل الشكر للقائمين على Quran Tab وMuslim Daily Tools وعلى رأسهم محمد أبوسريع، جزاهم الله خيراً", 12.5f, 0xFFE8C872, true, 10);
        addAbout(acol, "النص: alquran.cloud  •  الصوت: everyayah.com  •  الخطوط: Tarteel QUL  •  المواقيت: Aladhan  •  الصور: Wikimedia Commons", 10, 0xFFC8C8C8, false, 16);
        addAbout(acol, "يحتاج التطبيق إلى اتصال بالإنترنت", 10.5f, 0xFFE0E0E0, false, 8);
        addAbout(acol, "اضغط أي زر للإغلاق", 10, 0xAAFFFFFF, false, 6);
        aboutCard.addView(acol, new FrameLayout.LayoutParams(-2, -2));
        aboutCard.setVisibility(View.GONE);
        root.addView(aboutCard, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));

        hint = tv(9.5f, 0xCCFFFFFF, false, Gravity.CENTER);
        hint.setText("◄ ►  تنقّل    •    OK  تنفيذ    •    ▲ ▼  آية أخرى    •    رجوع  خروج");
        FrameLayout.LayoutParams hp = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        hp.bottomMargin = (int) s(14);
        root.addView(hint, hp);

        refreshControls();
    }

    private GlassCard chip() {
        GlassCard g = new GlassCard(ctx, s(18), Math.max(1.2f, s(1f)));
        TextView t = tv(11, Color.WHITE, true, Gravity.CENTER);
        t.setPadding((int) s(14), (int) s(7), (int) s(14), (int) s(7));
        g.addView(t, new FrameLayout.LayoutParams(-2, -2));
        return g;
    }

    private android.graphics.drawable.Drawable pillBg() {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(0x33FFFFFF);
        d.setCornerRadius(s(18));
        return d;
    }

    private void refreshControls() {
        for (int i = 0; i < 11; i++) {
            icons[i].focused = i == focus;
            icons[i].active = (i == 0 && repeat) || (i == 9 && showTafsir);
            if (i == 2) icons[i].type = wantPlay ? IconView.PAUSE : IconView.PLAY;
            icons[i].invalidate();
        }
        String state = playing ? "▶  " : (wantPlay ? "…  " : "");
        chipLeftText.setText(state + RECITERS[reciter][1]);
        chipRightText.setText("الخلفية: " + CATEGORIES[category][1]);
    }

    private void applyTafsir() {
        tafsirView.setVisibility(showTafsir ? View.VISIBLE : View.GONE);
        updatePrayerVisibility();
        refreshControls();
    }

    /** The prayer card steps aside while the tafsir, a menu or the about card needs the space. */
    private void updatePrayerVisibility() {
        prayerCard.animate().alpha(showTafsir || menuOpen || aboutOpen ? 0f : 1f).setDuration(250).start();
        ayahCard.animate().alpha(aboutOpen ? 0f : 1f).setDuration(250).start();
    }

    private void openMenu(int type) {
        menuType = type;
        if (type == MENU_RECITER) {
            menuItems = new String[RECITERS.length];
            for (int i = 0; i < menuItems.length; i++) menuItems[i] = RECITERS[i][1];
            menuCurrent = reciter;
        } else if (type == MENU_SURAH) {
            menuItems = new String[114];
            for (int i = 0; i < 114; i++) menuItems[i] = (i + 1) + ".  " + SURAH_NAMES[i];
            menuCurrent = cur != null ? cur.surah - 1 : 0;
        } else {
            menuItems = new String[CITY_DATA.length + 1];
            menuItems[0] = "تلقائي (حسب الإنترنت)";
            for (int i = 0; i < CITY_DATA.length; i++) {
                String[] r = CITY_DATA[i].split("\\|");
                menuItems[i + 1] = r[0] + " — " + r[1];
            }
            menuCurrent = cityIdx + 1;
        }
        menuIdx = menuCurrent;
        menuOpen = true;
        int[] loc = new int[2];
        View anchor = icons[type == MENU_RECITER ? 5 : type == MENU_SURAH ? 6 : 7];
        anchor.getLocationInWindow(loc);
        float cx = loc[0] + anchor.getWidth() / 2f;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) menuCard.getLayoutParams();
        int w = (int) s(266);
        lp.leftMargin = (int) Math.max(s(10), Math.min(root.getWidth() - w - s(10), cx - w / 2f));
        lp.topMargin = ayahCard.getBottom() + (int) s(8);
        menuCard.setLayoutParams(lp);
        menuCard.setVisibility(View.VISIBLE);
        hint.animate().cancel();
        hint.setAlpha(0f);
        renderMenu();
        updatePrayerVisibility();
    }

    private void closeMenu() {
        menuOpen = false;
        menuCard.setVisibility(View.GONE);
        updatePrayerVisibility();
        shownAt = System.currentTimeMillis();
    }

    private void renderMenu() {
        int n = menuItems.length;
        int start = Math.max(0, Math.min(n - MENU_ROWS, menuIdx - MENU_ROWS / 2));
        for (int i = 0; i < MENU_ROWS; i++) {
            int idx = start + i;
            TextView t = menuRows[i];
            t.setText((idx == menuCurrent ? "✓  " : "") + menuItems[idx]);
            boolean sel = idx == menuIdx;
            t.setTextColor(idx == menuCurrent ? 0xFFE8C872 : Color.WHITE);
            t.setTypeface(Typeface.DEFAULT, sel ? Typeface.BOLD : Typeface.NORMAL);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setColor(sel ? 0x55FFFFFF : 0x00000000);
            bg.setCornerRadius(s(8));
            t.setBackground(bg);
        }
        menuCard.invalidate();
    }

    private void menuKey(int code) {
        int n = menuItems.length;
        switch (code) {
            case KeyEvent.KEYCODE_DPAD_UP:
                menuIdx = (menuIdx - 1 + n) % n;
                renderMenu();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                menuIdx = (menuIdx + 1) % n;
                renderMenu();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (menuType != MENU_RECITER) { // page through the long lists
                    int d = code == KeyEvent.KEYCODE_DPAD_RIGHT ? MENU_ROWS : -MENU_ROWS;
                    menuIdx = Math.max(0, Math.min(n - 1, menuIdx + d));
                    renderMenu();
                } else {
                    closeMenu();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (menuType == MENU_RECITER) {
                    if (menuIdx != reciter) {
                        reciter = menuIdx;
                        prefs.edit().putInt("reciter", reciter).apply();
                        clearAudioCache();
                        if (wantPlay) { playAudio(); prefetchAhead(); }
                    }
                    closeMenu();
                    refreshControls();
                } else if (menuType == MENU_CITY) {
                    selectCity(menuIdx);
                } else {
                    int target = menuIdx + 1;
                    closeMenu();
                    if (cur == null || cur.surah != target) {
                        manualHold = true;
                        go(new int[]{target, 1}, true);
                    }
                }
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                closeMenu();
                break;
            default:
                break;
        }
    }

    private void openAbout() {
        aboutOpen = true;
        aboutCard.setVisibility(View.VISIBLE);
        hint.animate().cancel();
        hint.setAlpha(0f);
        updatePrayerVisibility();
    }

    private void closeAbout() {
        aboutOpen = false;
        aboutCard.setVisibility(View.GONE);
        updatePrayerVisibility();
        shownAt = System.currentTimeMillis();
    }

    private void addAbout(LinearLayout col, String text, float px, int color, boolean bold, float topMargin) {
        TextView t = tv(px, color, bold, Gravity.CENTER);
        t.setMaxWidth((int) s(560));
        t.setLineSpacing(0, 1.25f);
        t.setText(text);
        col.addView(t, lp(-2, -2, topMargin));
    }

    // ---------- keys ----------
    public boolean dispatchKey(KeyEvent e) {
        if (e.getAction() != KeyEvent.ACTION_DOWN) return true;
        if (aboutOpen) {
            closeAbout();
            return true;
        }
        if (menuOpen) {
            menuKey(e.getKeyCode());
            return true;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                host.finish();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                focus = Math.max(0, focus - 1);
                refreshControls();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                focus = Math.min(10, focus + 1);
                refreshControls();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                nextAyah();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                prevAyah();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                togglePlay();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                activate(focus);
                return true;
            default:
                return true;
        }
    }

    private void activate(int i) {
        switch (i) {
            case 0: repeat = !repeat; refreshControls(); break;
            case 1: prevAyah(); break;
            case 2: togglePlay(); break;
            case 3: nextAyah(); break;
            case 4:
                speedIdx = (speedIdx + 1) % SPEEDS.length;
                prefs.edit().putInt("speed", speedIdx).apply();
                applySpeed();
                refreshControls();
                break;
            case 5:
                openMenu(MENU_RECITER);
                break;
            case 6:
                openMenu(MENU_SURAH);
                break;
            case 7:
                openMenu(MENU_CITY);
                break;
            case 8:
                category = (category + 1) % CATEGORIES.length;
                prefs.edit().putInt("category", category).apply();
                nextBackground();
                refreshControls();
                break;
            case 9:
                showTafsir = !showTafsir;
                applyTafsir();
                break;
            case 10:
                openAbout();
                break;
            default: break;
        }
    }

    private void togglePlay() {
        wantPlay = !wantPlay;
        if (wantPlay) {
            playAudio();
            prefetchAhead();
        } else {
            prefetchGen++;
            releasePlayer();
            playing = false;
            shownAt = System.currentTimeMillis();
            refreshControls();
        }
    }

    // ---------- backgrounds ----------
    private List<String> bgFiles() {
        List<String> out = new ArrayList<>();
        try {
            String cat = CATEGORIES[category][0];
            String[] dirs = cat.isEmpty() ? new String[]{"islamic", "nature", "civilization"} : new String[]{cat};
            for (String d : dirs) {
                String[] fs = ctx.getAssets().list("bg/" + d);
                if (fs != null) for (String f : fs) out.add("bg/" + d + "/" + f);
            }
        } catch (Exception ignored) { }
        return out;
    }

    private void nextBackground() {
        lastBgChange = System.currentTimeMillis();
        final int gen = ++bgGen;
        final List<String> files = bgFiles();
        if (files.isEmpty()) return;
        final String path = files.get(rnd.nextInt(files.size()));
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final Bitmap bmp = decodeForDisplay(path);
                    final Bitmap blur = makeBlur(bmp);
                    handler.post(new Runnable() {
                        @Override public void run() {
                            if (gen == bgGen) crossfade(bmp, blur);
                        }
                    });
                } catch (Exception ignored) { }
            }
        }).start();
    }

    /** Decodes a (up to 4K) wallpaper at the display's resolution: sharp, but light on the 2 GB TV's memory. */
    private Bitmap decodeForDisplay(String path) throws Exception {
        int targetW = Math.max(1920, ctx.getResources().getDisplayMetrics().widthPixels);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        InputStream in = ctx.getAssets().open(path);
        BitmapFactory.decodeStream(in, null, o);
        in.close();
        int sample = 1;
        while (o.outWidth / (sample * 2) >= targetW) sample *= 2;
        o = new BitmapFactory.Options();
        o.inSampleSize = sample;
        in = ctx.getAssets().open(path);
        Bitmap b = BitmapFactory.decodeStream(in, null, o);
        in.close();
        if (b.getWidth() > targetW * 1.1f) {
            Bitmap s2 = Bitmap.createScaledBitmap(b, targetW, Math.round(b.getHeight() * targetW / (float) b.getWidth()), true);
            if (s2 != b) b.recycle();
            b = s2;
        }
        return b;
    }

    private Bitmap makeBlur(Bitmap src) {
        Bitmap a = Bitmap.createScaledBitmap(src, 240, 135, true);
        Bitmap b = Bitmap.createScaledBitmap(a, 60, 34, true);
        Bitmap c = Bitmap.createScaledBitmap(b, 15, 9, true);
        Bitmap up = Bitmap.createScaledBitmap(c, 60, 34, true);
        up = Bitmap.createScaledBitmap(up, 240, 135, true);
        return Bitmap.createScaledBitmap(up, 480, 270, true);
    }

    private void crossfade(Bitmap bmp, final Bitmap blur) {
        bgTop.setImageBitmap(bmp);
        bgTop.setAlpha(0f);
        bgTop.animate().alpha(1f).setDuration(1500).setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator a) {
                bgBottom.setImageDrawable(bgTop.getDrawable());
                bgTop.setAlpha(0f);
                bgTop.animate().setListener(null);
            }
        }).start();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                GlassCard.blurred = blur;
                ayahCard.invalidate();
                prayerCard.invalidate();
                chipLeft.invalidate();
                chipRight.invalidate();
            }
        }, 700);
    }

    // ---------- ayah engine ----------
    private static int[] successor(int s, int a) {
        if (a < AYAH_COUNT[s - 1]) return new int[]{s, a + 1};
        return new int[]{s % 114 + 1, 1};
    }

    private int[] randomRef() {
        int n = 1 + rnd.nextInt(TOTAL_AYAHS);
        for (int s = 1; s <= 114; s++) {
            if (n <= AYAH_COUNT[s - 1]) return new int[]{s, n};
            n -= AYAH_COUNT[s - 1];
        }
        return new int[]{1, 1};
    }

    /** Random while idle; the next ayah in mushaf order while the reciter is playing. */
    private void nextAyah() {
        int[] ref;
        if (wantPlay && cur != null) {
            ref = successor(cur.surah, cur.ayah);
        } else if (!wantPlay && histPos >= 0 && histPos < history.size() - 1) {
            histPos++;
            go(history.get(histPos), false);
            return;
        } else {
            ref = randomRef();
        }
        go(ref, true);
    }

    private void prevAyah() {
        if (histPos > 0) {
            histPos--;
            go(history.get(histPos), false);
        }
    }

    private void go(final int[] ref, final boolean push) {
        final int gen = ++loadGen;
        Ayah[] sd = surahCache.get(ref[0]);
        if (sd != null) {
            if (push) {
                while (history.size() > histPos + 1) history.remove(history.size() - 1);
                history.add(ref);
                if (history.size() > 60) history.remove(0);
                histPos = history.size() - 1;
            }
            show(sd[ref[1] - 1]);
            return;
        }
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    loadSurah(ref[0]);
                    handler.post(new Runnable() {
                        @Override public void run() { if (gen == loadGen) go(ref, push); }
                    });
                } catch (Exception ex) {
                    Log.w(TAG, "surah load failed", ex);
                    handler.postDelayed(new Runnable() {
                        @Override public void run() { if (gen == loadGen) go(ref, push); }
                    }, 5000);
                }
            }
        }).start();
    }

    /** Fetches a whole surah (uthmani text + muyassar tafsir) into the cache; blocking. */
    private Ayah[] loadSurah(int s) throws Exception {
        Ayah[] have = surahCache.get(s);
        if (have != null) return have;
        JSONArray data = new JSONObject(http("https://api.alquran.cloud/v1/surah/" + s
                + "/editions/quran-uthmani,ar.muyassar")).getJSONArray("data");
        JSONObject e0 = data.getJSONObject(0);
        JSONArray a0 = e0.getJSONArray("ayahs");
        JSONArray a1 = data.getJSONObject(1).getJSONArray("ayahs");
        Ayah[] out = new Ayah[a0.length()];
        for (int i = 0; i < out.length; i++) {
            Ayah y = new Ayah();
            y.text = a0.getJSONObject(i).getString("text");
            if (i == 0 && s != 1 && s != 9 && y.text.startsWith("بِسْمِ")) {
                String[] w = y.text.split(" ", 5); // drop the 4-word basmala the API prepends
                if (w.length == 5) y.text = w[4];
            }
            y.tafsir = a1.getJSONObject(i).getString("text");
            y.surahName = e0.getString("name");
            y.type = e0.getString("revelationType");
            y.surah = s;
            y.ayah = i + 1;
            y.count = out.length;
            out[i] = y;
        }
        surahCache.put(s, out);
        return out;
    }

    private static String arabicDigits(int n) {
        StringBuilder sb = new StringBuilder();
        for (char ch : String.valueOf(n).toCharArray()) sb.append((char) ('٠' + (ch - '0')));
        return sb.toString();
    }

    private void show(Ayah y) {
        Log.d(TAG, "show " + y.surah + ":" + y.ayah + " play=" + wantPlay);
        cur = y;
        releasePlayer();
        playing = false;
        surahHead.setText(String.valueOf((char) SURAH_GLYPH[y.surah - 1]));
        pill.setText(("Meccan".equals(y.type) ? "مكية" : "مدنية") + " • " + y.count + " آيات");
        int len = y.text.length();
        ayahView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                s(len < 70 ? 32 : len < 140 ? 26.5f : len < 260 ? 21.5f : len < 420 ? 17.5f : 14.5f));
        ayahView.setText(y.text + " " + arabicDigits(y.ayah));
        tafsirView.setText(y.tafsir);
        shownAt = System.currentTimeMillis();
        if (wantPlay) {
            playAudio();
            prefetchAhead();
        } else {
            refreshControls();
        }
    }

    private String http(String u) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(u).openConnection();
        c.setConnectTimeout(8000);
        c.setReadTimeout(15000);
        BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) sb.append(line);
        r.close();
        return sb.toString();
    }

    // ---------- audio + look-ahead buffer ----------
    private File audioFile(int reciterIdx, int s, int a) {
        return new File(audioDir, reciterIdx + "_" + String.format(Locale.US, "%03d%03d", s, a) + ".mp3");
    }

    private String audioUrl(int reciterIdx, int s, int a) {
        return "https://everyayah.com/data/" + RECITERS[reciterIdx][0] + "/"
                + String.format(Locale.US, "%03d%03d", s, a) + ".mp3";
    }

    /** Downloads text + audio for the next LOOKAHEAD ayahs so playback never waits on the network. */
    private void prefetchAhead() {
        if (cur == null) return;
        final int gen = ++prefetchGen;
        final int rIdx = reciter;
        final int s0 = cur.surah, a0 = cur.ayah;
        new Thread(new Runnable() {
            @Override public void run() {
                int s = s0, a = a0;
                for (int i = 0; i < LOOKAHEAD; i++) {
                    if (gen != prefetchGen) return;
                    int[] nx = successor(s, a);
                    s = nx[0];
                    a = nx[1];
                    try {
                        loadSurah(s);
                        File f = audioFile(rIdx, s, a);
                        if (!f.exists()) download(audioUrl(rIdx, s, a), f);
                        Log.d(TAG, "buffered " + s + ":" + a);
                    } catch (Exception ex) {
                        Log.w(TAG, "prefetch failed " + s + ":" + a, ex);
                        i--; // retry same ayah
                        try { Thread.sleep(2000); } catch (InterruptedException ignored) { return; }
                    }
                }
            }
        }).start();
    }

    private void download(String u, File dest) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(u).openConnection();
        c.setConnectTimeout(8000);
        c.setReadTimeout(15000);
        File tmp = new File(dest.getPath() + ".part");
        InputStream in = c.getInputStream();
        FileOutputStream out = new FileOutputStream(tmp);
        byte[] buf = new byte[16384];
        int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        out.close();
        in.close();
        if (!tmp.renameTo(dest)) throw new java.io.IOException("rename failed");
    }

    private void releasePlayer() {
        if (player != null) {
            try { player.release(); } catch (Exception ignored) { }
            player = null;
        }
    }

    private void applySpeed() {
        if (player != null && playing) {
            try { player.setPlaybackParams(new PlaybackParams().setSpeed(SPEEDS[speedIdx])); } catch (Exception ignored) { }
        }
    }

    private void playAudio() {
        releasePlayer();
        playing = false;
        refreshControls();
        if (cur == null) return;
        final Ayah y = cur;
        File f = audioFile(reciter, y.surah, y.ayah);
        try {
            final MediaPlayer mp = new MediaPlayer();
            player = mp;
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build());
            mp.setDataSource(f.exists() ? f.getPath() : audioUrl(reciter, y.surah, y.ayah));
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override public void onPrepared(MediaPlayer m) {
                    if (m != player) return;
                    m.start();
                    playing = true;
                    applySpeed();
                    refreshControls();
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override public void onCompletion(MediaPlayer m) {
                    if (m != player) return;
                    playing = false;
                    audioFile(reciter, y.surah, y.ayah).delete();
                    if (repeat) playAudio(); else nextAyah();
                }
            });
            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override public boolean onError(MediaPlayer m, int w, int x) {
                    if (m != player) return true;
                    playing = false;
                    chipLeftText.setText("تعذّر تحميل الصوت");
                    return true;
                }
            });
            mp.prepareAsync();
        } catch (Exception e) {
            chipLeftText.setText("تعذّر تشغيل الصوت");
        }
    }

    // ---------- clock + prayer times ----------
    private String today() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        f.setTimeZone(tz);
        return f.format(new Date());
    }

    /** Resolves the selected city (or the auto-detected one) into the active time zone + label. */
    private void applyCity() {
        String rec = cityIdx >= 0 ? CITY_DATA[cityIdx] : prefs.getString("auto", CITY_DATA[0]);
        String[] r = rec.split("\\|");
        if (r.length < 6) r = CITY_DATA[0].split("\\|");
        cityRec = r;
        tz = TimeZone.getTimeZone(r[3]);
        if (cityLabel != null) cityLabel.setText(r[0] + " • " + r[1]);
    }

    private static int methodFor(String cc) {
        switch (cc) {
            case "SA": return 4;
            case "EG": case "SD": case "LY": case "SY": case "IQ": case "LB": case "PS": case "YE": return 5;
            case "KW": return 9;
            case "QA": return 10;
            case "AE": return 16;
            case "BH": case "OM": return 8;
            case "JO": return 23;
            case "TR": return 13;
            case "PK": return 1;
            case "MA": return 21;
            case "DZ": return 19;
            case "TN": return 18;
            case "ID": return 20;
            case "MY": return 17;
            case "FR": return 12;
            case "US": case "CA": return 2;
            default: return 3;
        }
    }

    private String prayerKey() {
        return today() + "|" + cityRec[0] + "|" + cityRec[4] + "," + cityRec[5];
    }

    private void loadPrayerCache() {
        if (prefs.getString("pkey", "").equals(prayerKey())) {
            for (int i = 0; i < 5; i++) ptimes[i] = prefs.getString(PKEYS[i], null);
            loadedDay = today();
        } else {
            fetchPrayer();
        }
    }

    private void fetchPrayer() {
        final String day = today();
        final String key = prayerKey();
        final String[] c = cityRec;
        loadedDay = day;
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    String[] d = day.split("-");
                    JSONObject t = new JSONObject(http("https://api.aladhan.com/v1/timings/"
                            + d[2] + "-" + d[1] + "-" + d[0]
                            + "?latitude=" + c[4] + "&longitude=" + c[5]
                            + "&method=" + methodFor(c[2])
                            + "&timezonestring=" + java.net.URLEncoder.encode(c[3], "UTF-8")))
                            .getJSONObject("data").getJSONObject("timings");
                    SharedPreferences.Editor e = prefs.edit().putString("pkey", key);
                    final String[] got = new String[5];
                    for (int i = 0; i < 5; i++) {
                        got[i] = t.getString(PKEYS[i]).split(" ")[0];
                        e.putString(PKEYS[i], got[i]);
                    }
                    e.apply();
                    handler.post(new Runnable() {
                        @Override public void run() { System.arraycopy(got, 0, ptimes, 0, 5); }
                    });
                } catch (Exception ex) {
                    handler.postDelayed(new Runnable() {
                        @Override public void run() { loadedDay = ""; }
                    }, 60000);
                }
            }
        }).start();
    }

    /** "Auto" city: ask a public IP-geolocation service where this connection is. */
    private void detectLocation() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    JSONObject j = new JSONObject(http("https://ipwho.is/"));
                    if (!j.optBoolean("success", true)) return;
                    final String rec = j.getString("city") + "|" + j.getString("country") + "|"
                            + j.getString("country_code") + "|" + j.getJSONObject("timezone").getString("id") + "|"
                            + j.getDouble("latitude") + "|" + j.getDouble("longitude");
                    handler.post(new Runnable() {
                        @Override public void run() {
                            prefs.edit().putString("auto", rec).apply();
                            if (cityIdx == -1) { applyCity(); loadedDay = ""; }
                        }
                    });
                } catch (Exception ex) {
                    Log.w(TAG, "location detect failed", ex);
                }
            }
        }).start();
    }

    private void selectCity(int menuPos) {
        cityIdx = menuPos - 1; // position 0 is "auto"
        prefs.edit().putInt("city", cityIdx).apply();
        if (cityIdx == -1) detectLocation();
        applyCity();
        loadedDay = ""; // update() refetches the times for the new place
        closeMenu();
    }

    private int minutes(String hhmm) {
        String[] p = hhmm.split(":");
        return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
    }

    private String to12(String hhmm) {
        int m = minutes(hhmm);
        int h = m / 60, mm = m % 60;
        int h12 = h % 12 == 0 ? 12 : h % 12;
        return h12 + ":" + (mm < 10 ? "0" : "") + mm + (h < 12 ? " ص" : " م");
    }

    private void update() {
        Calendar now = Calendar.getInstance(tz, Locale.US);
        if (!loadedDay.equals(today())) fetchPrayer();

        int hh = now.get(Calendar.HOUR_OF_DAY);
        int h12 = hh % 12 == 0 ? 12 : hh % 12;
        int mi = now.get(Calendar.MINUTE);
        clock.setText(h12 + ":" + (mi < 10 ? "0" : "") + mi);
        ampm.setText(hh < 12 ? "ص" : "م");
        dateG.setText(now.get(Calendar.DAY_OF_MONTH) + " " + G_MONTHS[now.get(Calendar.MONTH)] + " " + now.get(Calendar.YEAR));
        try {
            HijrahDate hd = HijrahDate.from(LocalDate.now(ZoneId.of(tz.getID())));
            dateH.setText(hd.get(ChronoField.DAY_OF_MONTH) + " " + H_MONTHS[hd.get(ChronoField.MONTH_OF_YEAR) - 1]
                    + " " + hd.get(ChronoField.YEAR) + " هـ");
        } catch (Exception ignored) { }

        if (ptimes[0] != null) {
            int cm = hh * 60 + mi;
            int idx = -1;
            for (int i = 0; i < 5; i++) if (minutes(ptimes[i]) > cm) { idx = i; break; }
            int target = idx >= 0 ? minutes(ptimes[idx]) : minutes(ptimes[0]) + 1440;
            int prev = idx > 0 ? minutes(ptimes[idx - 1]) : (idx == 0 ? minutes(ptimes[4]) - 1440 : minutes(ptimes[4]));
            int diff = target - cm;
            int ni = idx >= 0 ? idx : 0;
            nextName.setText(PNAMES[ni]);
            remaining.setText((diff / 60) + " س " + (diff % 60) + " د");
            progress = Math.max(0f, Math.min(1f, (cm - prev) / (float) Math.max(1, target - prev)));
            progressBar.invalidate();
            for (int i = 0; i < 5; i++) {
                pTime[i].setText(to12(ptimes[i]));
                boolean hi = i == ni;
                pName[i].setTextColor(hi ? 0xFFE8C872 : Color.WHITE);
                pTime[i].setTextColor(hi ? 0xFFE8C872 : Color.WHITE);
            }
        }

        long nowMs = System.currentTimeMillis();
        if (nowMs - lastBgChange > BG_MS) nextBackground();
        // idle: a new random ayah every 10 seconds (not while playing or while the menu is open)
        if (menuOpen) shownAt = nowMs;
        else if (!wantPlay && shownAt > 0 && nowMs - shownAt > (manualHold ? 60000 : IDLE_MS)) {
            manualHold = false;
            nextAyah();
        }
    }
}
