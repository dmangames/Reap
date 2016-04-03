package dmangames.team4.reap.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import dmangames.team4.reap.R;
import dmangames.team4.reap.util.SecondTimer;

import static dmangames.team4.reap.util.SecondTimer.Type.COUNT_DOWN;

/**
 * Created by brian on 4/3/16.
 */
public class TimerIndicatorView extends ImageView implements SecondTimer.SecondListener {
    private static final float STROKE_WIDTH = 8f;

    SecondTimer timer;
    Paint linePaint;
    int lineColor;

    float degrees;
    RectF viewBounds;

    public TimerIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(STROKE_WIDTH);
        setLineColor(context.getResources().getColor(R.color.timer_line));

        viewBounds = new RectF(0, 0, getWidth(), getHeight());
    }

    public void setTimer(SecondTimer timer) {
        this.timer = timer;
        timer.addListener(this);
    }

    public void setLineColor(int color) {
        this.lineColor = color;
        linePaint.setColor(color);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewBounds.set(STROKE_WIDTH, STROKE_WIDTH,
                getWidth() - STROKE_WIDTH, getHeight() - STROKE_WIDTH);
        canvas.drawArc(viewBounds, 270, degrees, false, linePaint);
    }

    @Override public void onTimerTick(long secs) {
        degrees = (float) secs / timer.getTotalSeconds() * 360;
        if (timer.getType() == COUNT_DOWN)
            degrees = 360 - degrees;
        invalidate();
    }

    @Override public void onTimerFinish() {
        degrees = 0;
    }
}
