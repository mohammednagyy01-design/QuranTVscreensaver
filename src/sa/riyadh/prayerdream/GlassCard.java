package sa.riyadh.prayerdream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.FrameLayout;

/** "Liquid glass" panel: blurred copy of the wallpaper behind it, light tint, specular edge. */
public class GlassCard extends FrameLayout {
    /** Pre-blurred full-window wallpaper, shared by all cards. */
    static Bitmap blurred;

    private final float radius;
    private final float stroke;
    private final Paint bgPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Paint tint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint scrim = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean useScrim = false;

    /** Extra darkening behind text-heavy panels (menus, about) so they stay readable on bright photos. */
    public GlassCard scrim(int argb) {
        scrim.setColor(argb);
        useScrim = true;
        return this;
    }
    private final Paint sheen = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint edge = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clip = new Path();
    private final RectF rect = new RectF();
    private final int[] loc = new int[2];
    private final int[] rootLoc = new int[2];

    public GlassCard(Context c, float radiusPx, float strokePx) {
        super(c);
        radius = radiusPx;
        stroke = strokePx;
        setWillNotDraw(false);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(1.35f);
        // slight darkening keeps white text legible on bright photos
        cm.postConcat(new ColorMatrix(new float[]{
                0.78f, 0, 0, 0, 0,
                0, 0.78f, 0, 0, 0,
                0, 0, 0.78f, 0, 0,
                0, 0, 0, 1, 0}));
        bgPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        tint.setColor(0x26FFFFFF);
        edge.setStyle(Paint.Style.STROKE);
        edge.setStrokeWidth(strokePx);
    }

    @Override protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        rect.set(0, 0, w, h);
        clip.reset();
        clip.addRoundRect(rect, radius, radius, Path.Direction.CW);
        sheen.setShader(new LinearGradient(0, 0, w * 0.35f, h, 0x55FFFFFF, 0x05FFFFFF, Shader.TileMode.CLAMP));
        edge.setShader(new LinearGradient(0, 0, w, h, new int[]{0xB3FFFFFF, 0x1FFFFFFF, 0x80FFFFFF},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP));
    }

    @Override protected void onDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(clip);
        Bitmap b = blurred;
        android.view.View root = getRootView();
        if (b != null && root != null) {
            getLocationInWindow(loc);
            root.getLocationInWindow(rootLoc);
            float dx = rootLoc[0] - loc[0];
            float dy = rootLoc[1] - loc[1];
            canvas.drawBitmap(b, null, new RectF(dx, dy, dx + root.getWidth(), dy + root.getHeight()), bgPaint);
        }
        if (useScrim) canvas.drawRect(rect, scrim);
        canvas.drawRect(rect, tint);
        canvas.drawRect(rect, sheen);
        canvas.restoreToCount(save);
        RectF e = new RectF(stroke / 2, stroke / 2, getWidth() - stroke / 2, getHeight() - stroke / 2);
        canvas.drawRoundRect(e, radius, radius, edge);
    }
}
