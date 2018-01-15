package www.thl.com.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Shader;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import  www.thl.com.ui.utils.DensityUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


import www.thl.com.ui.utils.ScreenUtils;

/**
 * 条形图
 */
@SuppressLint({"DrawAllocation"})
public class BarChartView extends View {
    private List<List<Float>> datas = new ArrayList();
    private List<String> xnames = new ArrayList();
    private String title;
    private int[] colors = {Color.parseColor("#6accff"), Color.parseColor("#ffcb8a"), Color.parseColor("#6accff")};
    private float maxvalue = 1.4E-45F;
    private float minvalue = 3.4028235E38F;
    private float intervalY = 0.0F;
    private float intervalX = 0.0F;
    private float intervalValue = 0.0F;
    private float Y1ToScreen = 0.0F;
    private int Ycount = -1;
    private float zeroY;
    private String[] series;
    private float maxY;
    private float minY;
    private int intervalGroup;
    private Paint paintline;
    private Paint paintRect;
    private Paint paintwhite;
    private Paint paintValue;
    private TextPaint textpaint;
    private DecimalFormat df = new DecimalFormat("##0");
    private DecimalFormat df2 = new DecimalFormat("##0.00");
    private int intervalTime = 35;
    private int duration = 500;
    private int time;
    private int timeSum;
    private Handler handler;
    private float x_down;
    private float y_down;
    private boolean isCanTouch = false;
    private List<BarChartView.MRect> rectList;
    private BarChartView.MRect seletedMrect;

    public BarChartView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.time = this.duration / this.intervalTime;
        this.timeSum = this.duration / this.intervalTime;
        this.handler = new Handler();
        this.x_down = 0.0F;
        this.y_down = 0.0F;
        this.rectList = new ArrayList();
        this.seletedMrect = null;
        this.textpaint = new TextPaint();
        this.textpaint.setStyle(Paint.Style.FILL);
        this.textpaint.setColor(Color.GRAY);
        this.textpaint.setAntiAlias(true);
        this.textpaint.setTextSize(DensityUtil.dip2pxf(this.getContext(), 9));
        if (this.isInEditMode()) {
            this.test();
        }
    }

    private void test() {
        this.setTextColor(Color.parseColor("#bfbccf"));
        this.setCanTouch(true);
        List<String> xnames = new ArrayList<>();
        xnames.add("1月");
        xnames.add("2月");
        xnames.add("3月");
        xnames.add("4月");
        xnames.add("5月");
        xnames.add("6月");
        List<List<Float>> datas = new ArrayList<>();
        List<Float> list = new ArrayList<>();
        for (int j = 0; j < xnames.size(); ++j) {
            list.add(Float.valueOf((float) (new Random()).nextInt(1000) / 10.0F));
        }
        List<Float> list2 = new ArrayList<>();
        for (int j = 0; j < xnames.size(); ++j) {
            list2.add(Float.valueOf((float) (new Random()).nextInt(1000) / 10.0F));
        }
        datas.add(list);
        datas.add(list2);
        int[] colors = {Color.parseColor("#3285ff"), Color.YELLOW};

        float maxvalue = 100f;
        String[] series = {};
        String title = "";
        this.initData(datas, colors, xnames, maxvalue, series, title);
    }

    public void initData(List<List<Float>> datas, List<String> xnames) {
        this.datas = datas;
        this.xnames = xnames;
        String[] series = {};
        this.series = series;
        this.init();
    }

    public void initData(List<List<Float>> datas, List<String> xnames, String[] series) {
        this.datas = datas;
        this.xnames = xnames;
        this.series = series;
        this.init();
    }

    public void initData(List<List<Float>> datas, int[] colors, List<String> xnames, float maxvalue, String[] series) {
        this.datas = datas;
        this.colors = colors;
        this.xnames = xnames;
        this.maxvalue = maxvalue;
        this.series = series;
        this.init();
    }

    public void initData(List<List<Float>> datas, List<String> xnames, String[] series, String title) {
        this.datas = datas;
        this.xnames = xnames;
        this.series = series;
        this.title = title;
        this.init();
    }

    public void initData(List<List<Float>> datas, int[] colors, List<String> xnames, float maxvalue, String[] series, String title) {
        this.datas = datas;
        this.colors = colors;
        this.xnames = xnames;
        this.maxvalue = maxvalue;
        this.series = series;
        this.title = title;
        this.init();
    }

    private void init() {
        //提示框的画笔
        this.paintValue = new Paint();
        this.paintValue.setStyle(Paint.Style.FILL);
        this.paintValue.setAntiAlias(true);
        this.paintValue.setColor(getResources().getColor(R.color.theme_color));
        this.paintValue.setStrokeWidth((float) DensityUtil.dip2px(this.getContext(), 2.0F));

        this.paintline = new Paint();
        this.paintline.setStyle(Paint.Style.STROKE);
        this.paintline.setAntiAlias(true);
        this.paintline.setColor(Color.parseColor("#ececec"));
        this.paintline.setStrokeWidth(1.0F);
        this.paintwhite = new Paint();
        this.paintwhite.setStyle(Paint.Style.FILL);
        this.paintwhite.setAntiAlias(true);
        this.paintwhite.setColor(Color.WHITE);
        this.paintwhite.setStrokeWidth((float) DensityUtil.dip2px(this.getContext(), 1.0F));
        this.paintwhite.setTextAlign(Paint.Align.CENTER);
        this.paintRect = new Paint();
        this.paintRect.setStyle(Paint.Style.FILL);
        this.paintRect.setAntiAlias(true);
        this.maxvalue = this.maxvalue == 1.4E-45F ? this.getMinOrMaxValues(this.datas, true) : this.maxvalue;
        this.minvalue = this.minvalue == 3.4028235E38F ? this.getMinOrMaxValues(this.datas, false) : this.minvalue;
        this.intervalGroup = DensityUtil.dip2px(this.getContext(), 5.0F);
        this.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.datas.size() != 0) {
            int width = this.getWidth();
            int height = this.getHeight();
            this.maxY = (float) DensityUtil.dip2px(this.getContext(), 20.0F);
            this.minY = (float) ((double) (height - DensityUtil.dip2px(this.getContext(), 15.0F)) - (double) DensityUtil.dip2px(this.getContext(), 10.0F) * Math.ceil((double) this.series.length / 2.0D));
            if (this.Ycount == -1) {
                this.cal(this.maxvalue, this.minvalue);
                this.intervalY = (this.minY - this.maxY) / (float) this.Ycount;
            }

            float maxX = (float) (width - DensityUtil.dip2px(this.getContext(), 15.0F));
            float minX = (float) DensityUtil.dip2px(this.getContext(), 30.0F);
            this.textpaint.setTextAlign(Paint.Align.CENTER);
            if (this.title != null && !this.title.equals("")) {
                canvas.drawText(this.title, (float) (width / 2), ScreenUtils.getTextHeight(this.textpaint.getTextSize()), this.textpaint);
            }
            this.textpaint.setTextAlign(Paint.Align.RIGHT);

            for (int rectw = 0; rectw < this.Ycount + 1; ++rectw) {

                //绘制虚线
                Path path = new Path();
                path.moveTo(minX, this.maxY + this.intervalY * (float) rectw);
                path.lineTo(maxX, this.maxY + this.intervalY * (float) rectw);
                PathEffect effects = new DashPathEffect(new float[]{25, 3, 25, 3}, 0);
                this.paintline.setPathEffect(effects);
                canvas.drawPath(path, this.paintline);
                //绘制实线
//                canvas.drawLine(minX, this.maxY + this.intervalY * (float) rectw, maxX, this.maxY + this.intervalY * (float) rectw, this.paintline);
                if ((double) Math.abs(this.maxvalue - (float) rectw * this.intervalValue) < 1.0E-4D) {
                    this.zeroY = this.maxY + this.intervalY * (float) rectw;
                    canvas.drawText("0", minX - (float) DensityUtil.dip2px(this.getContext(), 5.0F), this.maxY + this.intervalY * (float) rectw, this.textpaint);
                } else if ((int) Math.abs(this.intervalValue) < 1) {
                    canvas.drawText(this.df2.format((double) (this.maxvalue - (float) rectw * this.intervalValue)) + "", minX - (float) DensityUtil.dip2px(this.getContext(), 5.0F), this.maxY + this.intervalY * (float) rectw, this.textpaint);
                } else {
                    canvas.drawText(this.df.format((double) (this.maxvalue - (float) rectw * this.intervalValue)) + "", minX - (float) DensityUtil.dip2px(this.getContext(), 5.0F), this.maxY + this.intervalY * (float) rectw, this.textpaint);
                }
            }

            this.textpaint.setTextAlign(Paint.Align.CENTER);
            this.intervalX = (maxX - minX - (float) DensityUtil.dip2px(this.getContext(), 10.0F)) / (float) ((List) this.datas.get(0)).size();

            //条形图的宽度
            float var11 = (this.intervalX - (float) this.intervalGroup) / (float) this.datas.size() - DensityUtil.dip2px(this.getContext(), 42) / this.datas.get(0).size();
            float startx = this.intervalX * 1.0F / 6.0F + (float) DensityUtil.dip2px(this.getContext(), 5.0F);
            this.drawData(canvas, minX, startx, var11);

            int i;
            for (i = 0; i < this.xnames.size(); ++i) {
                canvas.drawText((String) this.xnames.get(i), minX + (float) i * this.intervalX + startx + var11 * (float) this.datas.size() / 2.0F, this.zeroY + (float) DensityUtil.dip2px(this.getContext(), 10.0F), this.textpaint);
            }

            if (this.seletedMrect != null) {

                //Canvas canvas,String var,float x,float y
                //冒泡弹出提示
                drawTouchValue(canvas, this.seletedMrect.getValue(), this.seletedMrect.getxCenter(), this.seletedMrect.getTop());

                //提示框
                //       Bitmap var12 = ScreenUtils.getBitmap(this.getContext(), this.seletedMrect.getValue() + "", cn.mastercom.ui.R.drawable.mt_popup_blue, 13, -1);
                //      canvas.drawBitmap(var12, this.seletedMrect.getxCenter() - (float) (var12.getWidth() / 2), this.seletedMrect.getTop() - (float) var12.getHeight(), this.paintwhite);
            }

            this.textpaint.setTextAlign(Paint.Align.LEFT);

            for (i = 0; i < this.series.length; ++i) {
                this.paintRect.setColor(this.colors[i]);
                float xt = i % 2 > 0 ? (float) (width / 2 - DensityUtil.dip2px(this.getContext(), 20.0F)) : 0.0F;
                float yt = (float) (i / 2 * DensityUtil.dip2px(this.getContext(), 10.0F));
                canvas.drawRect((float) DensityUtil.dip2px(this.getContext(), 20.0F) + xt, this.minY + (float) DensityUtil.dip2px(this.getContext(), 12.0F) + yt, (float) DensityUtil.dip2px(this.getContext(), 40.0F) + xt, this.minY + (float) DensityUtil.dip2px(this.getContext(), 20.0F) + yt, this.paintRect);
                canvas.drawText(this.series[i], (float) DensityUtil.dip2px(this.getContext(), 42.0F) + xt, this.minY + (float) DensityUtil.dip2px(this.getContext(), 18.0F) + yt, this.textpaint);
            }

            if (this.datas.size() > 0 && this.timeSum > this.time) {
                ++this.time;
                this.handler.postDelayed(new Runnable() {
                    public void run() {
                        BarChartView.this.invalidate();
                    }
                }, (long) this.intervalTime);
            }

        }
    }

    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);

            for (int j = 0; j < len; ++j) {
                iRet += (int) Math.ceil((double) widths[j]);
            }
        }
        return iRet;
    }

    private int getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil((double) (fm.descent - fm.ascent));
    }


    private void drawTouchValue(Canvas canvas, float var, float x, float y) {
        String format = "%.0f";
        if ((float) ((int) var) == var) {
            format = "%.0f";
        } else {
            format = "%.2f";
        }
        //提示字体大小
        paintwhite.setTextSize(DensityUtil.sp2px(this.getContext(), 10));
        int textLength = this.getTextWidth(this.paintwhite, String.format(format, new Object[]{Float.valueOf(var)}));
        int textHeight = this.getFontHeight(this.paintwhite);
        float ascent = Math.abs(this.paintwhite.getFontMetrics().ascent);
        int dp2 = DensityUtil.dip2px(this.getContext(), 2.0F);
        Path path;
        path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + (float) (2 * dp2), y - (float) (3 * dp2));

        path.lineTo(x + (float) (2 * dp2) + (float) textLength / 2, y - (float) (3 * dp2));
        path.lineTo(x + (float) (2 * dp2) + (float) textLength / 2, y - (float) (3 * dp2) - (float) (2 * dp2) - (float) textHeight);
        path.lineTo(x - (float) (2 * dp2) - (float) textLength / 2, y - (float) (3 * dp2) - (float) (2 * dp2) - (float) textHeight);
        path.lineTo(x - (float) (2 * dp2) - (float) textLength / 2, y - (float) (3 * dp2));

        path.lineTo(x - (float) (2 * dp2), y - (float) (3 * dp2));
        path.close();
        canvas.drawPath(path, this.paintValue);
        canvas.drawText(String.format(format, new Object[]{Float.valueOf(var)}), x, y - (float) (3 * dp2) - (float) dp2 - (float) textHeight + ascent, this.paintwhite);
    }

    private boolean isShowSeriesTip = true;

    private void drawData(Canvas canvas, float minX, float startx, float rectw) {
        boolean isEmpty = this.rectList.isEmpty();

        for (int i = 0; i < this.datas.size(); ++i) {
            for (int j = 0; j < ((List) this.datas.get(i)).size(); ++j) {
                float y = (this.maxvalue - ((Float) ((List) this.datas.get(i)).get(j)).floatValue()) * this.Y1ToScreen + this.maxY;
                float offsetY = this.zeroY - y - (float) this.time * 1.0F / (float) this.timeSum * (this.zeroY - y);
                if (((Float) ((List) this.datas.get(i)).get(j)).floatValue() > 0.0F) {
                    //添加渐变效果
                    if (i == 0) {
                        LinearGradient mLinearGradient = new LinearGradient(minX + (float) j * this.intervalX + startx + (float) i * rectw, y + offsetY, minX + (float) j * this.intervalX + startx + (float) i * rectw, this.zeroY, Color.parseColor("#6accff"), Color.parseColor("#2f81ff"), Shader.TileMode.CLAMP);
                        this.paintRect.setShader(mLinearGradient);
                    } else if (i == 1) {
                        LinearGradient mLinearGradient = new LinearGradient(minX + (float) j * this.intervalX + startx + (float) i * rectw, y + offsetY, minX + (float) j * this.intervalX + startx + (float) i * rectw, this.zeroY, Color.parseColor("#ffcb8a"), Color.parseColor("#ff9b1d"), Shader.TileMode.CLAMP);
                        this.paintRect.setShader(mLinearGradient);
                    } else {
                        this.paintRect.setColor(this.colors[i]);
                    }
                    //绘制矩形
                    canvas.drawRect(minX + (float) j * this.intervalX + startx + (float) i * rectw, y + offsetY, minX + (float) j * this.intervalX + startx + (float) i * rectw + rectw, this.zeroY, this.paintRect);
                    if (isEmpty) {
                        BarChartView.MRect rect = new BarChartView.MRect(minX + (float) j * this.intervalX + startx + (float) i * rectw, y + offsetY, minX + (float) j * this.intervalX + startx + (float) i * rectw + rectw, this.zeroY, (Float) ((List) this.datas.get(i)).get(j));
                        this.rectList.add(rect);
                    }
                } else {
                    canvas.drawRect(minX + (float) j * this.intervalX + startx + (float) i * rectw, this.zeroY, minX + (float) j * this.intervalX + startx + (float) i * rectw + rectw, y + offsetY, this.paintRect);
                }
                this.paintRect.setColor(this.colors[i]);
            }
        }
    }

    private float getMinOrMaxValues(List<List<Float>> ls, boolean isMax) {
        float max = 1.4E-45F;
        float min = 3.4028235E38F;
        if (ls != null && ls.size() != 0) {
            for (int i = 0; i < ls.size(); ++i) {
                for (int j = 0; j < ((List) ls.get(i)).size(); ++j) {
                    if (((Float) ((List) ls.get(i)).get(j)).floatValue() != 1.4E-45F) {
                        if (((Float) ((List) ls.get(i)).get(j)).floatValue() > max) {
                            max = ((Float) ((List) ls.get(i)).get(j)).floatValue();
                        }

                        if (((Float) ((List) ls.get(i)).get(j)).floatValue() < min) {
                            min = ((Float) ((List) ls.get(i)).get(j)).floatValue();
                        }
                    }
                }
            }

            max = max == 1.4E-45F ? 0.0F : max;
            min = min == 3.4028235E38F ? 0.0F : min;
            return isMax ? max : min;
        } else {
            return 0.0F;
        }
    }

    private boolean isPrime(int n) {
        if (n < 2) {
            return false;
        } else if (n == 2) {
            return true;
        } else if (n % 2 == 0) {
            return false;
        } else {
            for (int i = 3; i * i <= n; i += 2) {
                if (n % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    private int getYCount(int values) {
        int[] num = new int[]{7, 6, 5, 4, 3, 2};

        for (int i = 0; i < num.length; ++i) {
            if (values % num[i] == 0) {
                return num[i];
            }
        }
        return 5;
    }

    private void cal(float maxValue, float minValue) {
        this.Ycount = 0;
        if (maxValue == 0.0F && minValue == 0.0F) {
            this.Ycount = 3;
            this.Y1ToScreen = (this.minY - this.maxY) / (float) this.Ycount;
            this.maxvalue = 3.0F;
            this.minvalue = 0.0F;
            this.intervalValue = 1.0F;
        }

        float[] e = new float[]{1.0F, 2.0F, 5.0F};
        float maxAbs = Math.abs(maxValue) > Math.abs(minValue) ? Math.abs(maxValue) : Math.abs(minValue);
        float minAbs = Math.abs(maxValue) < Math.abs(minValue) ? Math.abs(maxValue) : Math.abs(minValue);
        float multiple = 0.0F;

        float tmp;
        for (tmp = 0.001F; tmp < maxAbs; tmp *= 10.0F) {
            ;
        }

        multiple = tmp / 100.0F;

        for (int count = 0; (this.Ycount > 7 || this.Ycount < 3) && count < 100; multiple *= 10.0F) {
            ++count;

            for (int i = 0; i < e.length; ++i) {
                this.Ycount = (int) Math.ceil((double) (maxAbs / (e[i] * multiple)));
                if (maxValue > 0.0F && minValue < 0.0F) {
                    this.Ycount += (int) Math.ceil((double) (minAbs / (e[i] * multiple)));
                }

                if (this.Ycount <= 7) {
                    this.Y1ToScreen = (this.minY - this.maxY) / ((float) this.Ycount * e[i] * multiple);
                    this.intervalValue = e[i] * multiple;
                    if (maxValue <= 0.0F) {
                        this.maxvalue = 0.0F;
                        this.minvalue = (float) (-this.Ycount) * e[i] * multiple;
                    } else if (minValue >= 0.0F) {
                        this.maxvalue = (float) this.Ycount * e[i] * multiple;
                        this.minvalue = 0.0F;
                    } else {
                        this.maxvalue = (float) ((int) Math.ceil((double) (maxAbs / (e[i] * multiple)))) * e[i] * multiple;
                        this.minvalue = (float) ((int) Math.ceil((double) (minAbs / (e[i] * multiple)))) * e[i] * multiple;
                    }
                    break;
                }
            }
        }

    }

    public void setAnimation(boolean animation) {
        this.time = animation ? 1 : this.timeSum;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        this.timeSum = duration / this.intervalTime;
    }

    public void setTextColor(int color) {
        this.textpaint.setColor(color);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (isCanTouch) {
            switch (event.getAction() & 255) {
                case 0:
                    this.x_down = event.getX();
                    this.y_down = event.getY();
                    this.isContainMrect(this.x_down, this.y_down);
                    this.invalidate();
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                default:
            }
        }
        return true;
    }

    public void setCanTouch(boolean canTouch) {
        isCanTouch = canTouch;
    }

    private boolean isContainMrect(float x_down, float y_down) {
        Iterator var3 = this.rectList.iterator();

        BarChartView.MRect rect;
        do {
            if (!var3.hasNext()) {
                this.seletedMrect = null;
                return false;
            }

            rect = (BarChartView.MRect) var3.next();
        }
        while (x_down <= rect.getLeft() || x_down >= rect.getRight() || y_down >= rect.getBttomm());

        this.seletedMrect = rect;
        return true;
    }

    class MRect {
        private float left;
        private float top;
        private float right;
        private float bttomm;
        private float xCenter;
        private Float value;

        public MRect(float left, float top, float right, float bttomm, Float value) {
            this.left = left;
            this.right = right;
            this.bttomm = bttomm;
            this.top = top;
            this.value = value;
            this.xCenter = (left + right) / 2.0F;
        }

        public float getLeft() {
            return this.left;
        }

        public void setLeft(float left) {
            this.left = left;
        }

        public float getTop() {
            return this.top;
        }

        public void setTop(float top) {
            this.top = top;
        }

        public float getRight() {
            return this.right;
        }

        public void setRight(float right) {
            this.right = right;
        }

        public float getBttomm() {
            return this.bttomm;
        }

        public void setBttomm(float bttomm) {
            this.bttomm = bttomm;
        }

        public Float getValue() {
            return this.value;
        }

        public void setValue(Float value) {
            this.value = value;
        }

        public float getxCenter() {
            return this.xCenter;
        }

        public void setxCenter(float xCenter) {
            this.xCenter = xCenter;
        }
    }
}
