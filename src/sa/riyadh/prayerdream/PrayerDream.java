package sa.riyadh.prayerdream;

import android.service.dreams.DreamService;
import android.view.KeyEvent;

/** Screensaver entry point: hosts the shared Quran TV screen. */
public class PrayerDream extends DreamService implements QuranScreen.Host {
    private QuranScreen screen;

    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(true);
        setFullscreen(true);
        screen = new QuranScreen(this, this);
        setContentView(screen.getRoot());
    }

    @Override public void onDreamingStarted() {
        super.onDreamingStarted();
        screen.start();
    }

    @Override public void onDreamingStopped() {
        screen.stop();
        super.onDreamingStopped();
    }

    @Override public boolean dispatchKeyEvent(KeyEvent e) {
        return screen.dispatchKey(e);
    }
}
