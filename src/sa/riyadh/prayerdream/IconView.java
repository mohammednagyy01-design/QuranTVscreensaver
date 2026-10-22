package sa.riyadh.prayerdream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/** Hand-drawn control icons (no icon font / image assets needed). */
public class IconView extends View {
    static final int REPEAT = 0, PREV = 1, PLAY = 2, PAUSE = 3, NEXT = 4, SPEED = 5, MIC = 6, IMAGE = 7, BOOK = 8, LIST = 9, INFO = 10, PIN = 11;

    int type;
    boolean focused;
    boolean active;
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint halo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloEdge = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path p = new Path();
    private final RectF r = new RectF();

    public IconView(Context c, int type, float strokePx) {
        super(c);
        this.type = type;
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(strokePx);
        line.setStrokeCap(Paint.Cap.ROUND);
        line.setStrokeJoin(Paint.Join.ROUND);
        fill.setStyle(Paint.Style.FILL);
        halo.setColor(0x40FFFFFF);
        haloEdge.setStyle(Paint.Style.STROKE);
        haloEdge.setColor(0xAAFFFFFF);
        haloEdge.setStrokeWidth(strokePx * 0.8f);
    }

    private void tri(Canvas c, float x1, float y1, float x2, float y2, float x3, float y3) {
        p.reset();
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
        p.lineTo(x3, y3);
        p.close();
        c.drawPath(p, fill);
    }

    @Override protected void onDraw(Canvas c) {
        float w = getWidth(), h = getHeight();
        if (focused) {
            c.drawCircle(w / 2, h / 2, Math.min(w, h) / 2f - 1, halo);
            c.drawCircle(w / 2, h / 2, Math.min(w, h) / 2f - 1, haloEdge);
        }
        int col = active ? 0xFFE8C872 : 0xFFFFFFFF;
        line.setColor(col);
        fill.setColor(col);
        float s = Math.min(w, h) * 0.8f; // glyph sits a little smaller inside its halo
        float ox = (w - s) / 2, oy = (h - s) / 2;
        switch (type) {
            case PLAY:
                tri(c, ox + s * .36f, oy + s * .26f, ox + s * .36f, oy + s * .74f, ox + s * .74f, oy + s * .5f);
                break;
            case PAUSE:
                c.drawRoundRect(ox + s * .33f, oy + s * .27f, ox + s * .45f, oy + s * .73f, 3, 3, fill);
                c.drawRoundRect(ox + s * .55f, oy + s * .27f, ox + s * .67f, oy + s * .73f, 3, 3, fill);
                break;
            case PREV:
                c.drawLine(ox + s * .3f, oy + s * .3f, ox + s * .3f, oy + s * .7f, line);
                tri(c, ox + s * .7f, oy + s * .28f, ox + s * .7f, oy + s * .72f, ox + s * .38f, oy + s * .5f);
                break;
            case NEXT:
                c.drawLine(ox + s * .7f, oy + s * .3f, ox + s * .7f, oy + s * .7f, line);
                tri(c, ox + s * .3f, oy + s * .28f, ox + s * .3f, oy + s * .72f, ox + s * .62f, oy + s * .5f);
                break;
            case REPEAT:
                p.reset();
                p.moveTo(ox + s * .25f, oy + s * .52f);
                p.lineTo(ox + s * .25f, oy + s * .42f);
                p.quadTo(ox + s * .25f, oy + s * .3f, ox + s * .37f, oy + s * .3f);
                p.lineTo(ox + s * .72f, oy + s * .3f);
                p.moveTo(ox + s * .75f, oy + s * .48f);
                p.lineTo(ox + s * .75f, oy + s * .58f);
                p.quadTo(ox + s * .75f, oy + s * .7f, ox + s * .63f, oy + s * .7f);
                p.lineTo(ox + s * .28f, oy + s * .7f);
                c.drawPath(p, line);
                tri(c, ox + s * .68f, oy + s * .21f, ox + s * .68f, oy + s * .39f, ox + s * .8f, oy + s * .3f);
                tri(c, ox + s * .32f, oy + s * .61f, ox + s * .32f, oy + s * .79f, ox + s * .2f, oy + s * .7f);
                break;
            case SPEED:
                r.set(ox + s * .2f, oy + s * .26f, ox + s * .8f, oy + s * .86f);
                c.drawArc(r, 180, 180, false, line);
                c.drawLine(ox + s * .2f, oy + s * .56f, ox + s * .2f, oy + s * .56f, line);
                c.drawLine(ox + s * .5f, oy + s * .56f, ox + s * .64f, oy + s * .38f, line);
                c.drawCircle(ox + s * .5f, oy + s * .56f, s * .04f, fill);
                break;
            case MIC: // headphones
                r.set(ox + s * .2f, oy + s * .18f, ox + s * .8f, oy + s * .78f);
                c.drawArc(r, 180, 180, false, line);
                c.drawLine(ox + s * .2f, oy + s * .48f, ox + s * .2f, oy + s * .6f, line);
                c.drawLine(ox + s * .8f, oy + s * .48f, ox + s * .8f, oy + s * .6f, line);
                r.set(ox + s * .14f, oy + s * .56f, ox + s * .32f, oy + s * .82f);
                c.drawRoundRect(r, s * .06f, s * .06f, fill);
                r.set(ox + s * .68f, oy + s * .56f, ox + s * .86f, oy + s * .82f);
                c.drawRoundRect(r, s * .06f, s * .06f, fill);
                break;
            case IMAGE:
                r.set(ox + s * .2f, oy + s * .24f, ox + s * .8f, oy + s * .76f);
                c.drawRoundRect(r, s * .07f, s * .07f, line);
                p.reset();
                p.moveTo(ox + s * .22f, oy + s * .68f);
                p.lineTo(ox + s * .42f, oy + s * .48f);
                p.lineTo(ox + s * .56f, oy + s * .62f);
                p.lineTo(ox + s * .64f, oy + s * .54f);
                p.lineTo(ox + s * .78f, oy + s * .68f);
                c.drawPath(p, line);
                c.drawCircle(ox + s * .66f, oy + s * .38f, s * .05f, fill);
                break;
            case BOOK:
                p.reset();
                p.moveTo(ox + s * .5f, oy + s * .32f);
                p.quadTo(ox + s * .36f, oy + s * .22f, ox + s * .2f, oy + s * .28f);
                p.lineTo(ox + s * .2f, oy + s * .72f);
                p.quadTo(ox + s * .36f, oy + s * .66f, ox + s * .5f, oy + s * .76f);
                p.quadTo(ox + s * .64f, oy + s * .66f, ox + s * .8f, oy + s * .72f);
                p.lineTo(ox + s * .8f, oy + s * .28f);
                p.quadTo(ox + s * .64f, oy + s * .22f, ox + s * .5f, oy + s * .32f);
                p.lineTo(ox + s * .5f, oy + s * .76f);
                c.drawPath(p, line);
                break;
            case LIST:
                for (int k = 0; k < 3; k++) {
                    float y = oy + s * (.3f + k * .2f);
                    c.drawLine(ox + s * .4f, y, ox + s * .8f, y, line);
                    c.drawCircle(ox + s * .24f, y, s * .035f, fill);
                }
                break;
            case INFO:
                c.drawCircle(ox + s * .5f, oy + s * .5f, s * .32f, line);
                c.drawCircle(ox + s * .5f, oy + s * .36f, s * .035f, fill);
                c.drawLine(ox + s * .5f, oy + s * .47f, ox + s * .5f, oy + s * .66f, line);
                break;
            case PIN:
                c.drawCircle(ox + s * .5f, oy + s * .4f, s * .24f, line);
                c.drawLine(ox + s * .33f, oy + s * .56f, ox + s * .5f, oy + s * .84f, line);
                c.drawLine(ox + s * .67f, oy + s * .56f, ox + s * .5f, oy + s * .84f, line);
                c.drawCircle(ox + s * .5f, oy + s * .4f, s * .07f, fill);
                break;
            default:
                break;
        }
    }
}
