package sa.riyadh.prayerdream;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

/** Launcher entry point (Android TV home shortcut): same screen as the screensaver, full screen. */
public class QuranActivity extends Activity implements QuranScreen.Host {
    private QuranScreen screen;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        screen = new QuranScreen(this, this);
        setContentView(screen.getRoot());
    }

    @Override protected void onStart() {
        super.onStart();
        screen.start();
    }

    @Override protected void onStop() {
        screen.stop();
        super.onStop();
    }

    @Override public boolean dispatchKeyEvent(KeyEvent e) {
        return screen.dispatchKey(e);
    }
}
