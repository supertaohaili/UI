package www.thl.com.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import www.thl.com.ui.utils.DensityUtil;
/**
 * 绘制圆环
 */
public class RingView extends View {

    //默认值
    public final static int DIRECTION_T = 0; //顺时针
    public final static int DIRECTION_F = 1; //逆时针

    private int ringWidth = 0;
    private int max = 100;
    private int progress = 0;
    private int bgColor = Color.parseColor("#4897fe");
    private int progressColor = Color.parseColor("#9fc8ff");
    private int startAngle = 270;                   //起始角度
    private int direction = DIRECTION_T;

    private Paint paint;
    private Paint whitePaint;
    private Paint progressPaint;

    public RingView(Context context) {
        this(context, null);
        init(context);
    }

    public RingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(Color.BLUE);

        this.whitePaint = new Paint();
        this.whitePaint.setAntiAlias(true);
        this.whitePaint.setStyle(Paint.Style.FILL);
        this.whitePaint.setColor(Color.WHITE);

        this.progressPaint = new Paint();
        this.progressPaint.setAntiAlias(true);
        this.progressPaint.setStyle(Paint.Style.FILL);
        this.progressPaint.setColor(Color.YELLOW);

        if (ringWidth == 0) {
            ringWidth = DensityUtil.dip2px(context, 15);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = getMeasuredWidth() / 2;
        int y = getMeasuredWidth() / 2;

        //绘制背景圆环
        this.paint.setColor(bgColor);
        this.paint.setStrokeWidth(ringWidth);
        canvas.drawCircle(x, y, x - ringWidth / 2, this.paint);

        //绘制弧线
        this.progressPaint.setColor(progressColor);
        RectF oval = new RectF();
        oval.left = 0;
        oval.top = 0;
        oval.right = 2 * x;
        oval.bottom = 2 * x;
        float numAngle = ((float) progress / (float) max) * 360;
        if (direction == DIRECTION_F) {
            numAngle = -numAngle;
        }
        canvas.drawArc(oval, startAngle, numAngle, true, progressPaint);    //绘制圆弧

        //绘制白色实心背景圆环
        canvas.drawCircle(x, y, x - ringWidth, this.whitePaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public int getProgress() {
        return progress;
    }

    public void setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }
}