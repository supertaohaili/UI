package www.thl.com.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import www.thl.com.ui.utils.DensityUtil;
import www.thl.com.ui.utils.ScreenUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * 区域折线图
 */
@SuppressLint({"DrawAllocation", "ViewConstructor"})
public class LineChartView extends View {
    private List<List<Float>> datas;
    private String title;
    private boolean isBezier;
    private List<String> xnames;
    private float maxvalue;
    private float minvalue;
    private DecimalFormat df;
    private DecimalFormat df2;
    private String[] series;
    private int[] colors;
    private List<Paint> paintlist;
    private List<LineChartView.MRegion> regionlist;
    private int regionwidth;
    private float intervalY;
    private float intervalX;
    private float Y1ToScreen;
    private float intervalY_values;
    boolean flag;
    private int Ycount;
    private String x_unit;
    private int[] touchitem;

    private Paint paintline;
    private Paint paintwhite;
    private Paint paintValue;
    private Paint painttest;
    private Paint paint_select;
    private TextPaint textpaint;
    private TextPaint sericeTextpaint;

    private List<List<LineChartView.MPoint>> pointslist;

    private int width;
    private int height;
    private float dx;
    private float dy;
    private float dscale;
    private float maxY;
    private float maxX;
    private float minY;
    private float minX;
    private int x_count_max;
    private boolean canDrag;
    private boolean showMaxMin;
    private int intervalTime;
    private int duration;
    private int time;
    private int timeSum;
    private Handler handler;
    private Bitmap bitmap_fullScreen;
    private Rect dst_fullScreen;
    private boolean canFullScreen;
    private List<LineChartView.MRegion> seriesRegionList;
    float x_down;
    float y_down;
    private PointF start;
    private PointF mid;
    float oldDist;
    float oldRotation;
    Matrix matrix;
    Matrix matrix1;
    Matrix savedMatrix;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode;

    private boolean isShowSeries = false;
    private boolean isShowSeriesTip = false;
    private boolean isShowX = false;

    public LineChartView(Context context) {
        this(context, (AttributeSet) null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.datas = new ArrayList();
        this.isBezier = false;
        this.isShowSeries = true;
        this.xnames = new ArrayList();
        this.maxvalue = 1.4E-45F;
        this.minvalue = 3.4028235E38F;
        this.df = new DecimalFormat("##0.00");
        this.df2 = new DecimalFormat("##0");
        this.series = null;
        this.paintlist = new ArrayList();
        this.regionlist = new ArrayList();
        this.regionwidth = 40;
        this.intervalY = 0.0F;
        this.intervalX = 0.0F;
        this.Y1ToScreen = 0.0F;
        this.intervalY_values = 0.0F;
        this.flag = false;
        this.Ycount = -1;
        this.x_unit = "";
        this.touchitem = new int[]{-1, -1};
        this.pointslist = new ArrayList();
        this.dx = 0.0F;
        this.dy = 0.0F;
        this.dscale = 1.0F;
        this.x_count_max = 15;
        this.canDrag = false;
        this.showMaxMin = false;
        this.intervalTime = 25;
        this.duration = 500;
        this.time = this.duration / this.intervalTime;
        this.timeSum = this.duration / this.intervalTime;
        this.handler = new Handler();
        this.dst_fullScreen = new Rect();
        this.canFullScreen = false;
        this.seriesRegionList = new ArrayList();
        this.x_down = 0.0F;
        this.y_down = 0.0F;
        this.start = new PointF();
        this.mid = new PointF();
        this.oldDist = 1.0F;
        this.oldRotation = 0.0F;
        this.matrix = new Matrix();
        this.matrix1 = new Matrix();
        this.savedMatrix = new Matrix();
        this.mode = 0;

        this.textpaint = new TextPaint();
        this.textpaint.setStyle(Paint.Style.FILL);
        this.textpaint.setColor(Color.WHITE);
        this.textpaint.setAntiAlias(true);
        this.textpaint.setTextAlign(Paint.Align.CENTER);
        this.textpaint.setTextSize(DensityUtil.dip2pxf(this.getContext(), 9));

        this.sericeTextpaint = new TextPaint();
        this.sericeTextpaint.setStyle(Paint.Style.FILL);
        this.sericeTextpaint.setColor(Color.WHITE);
        this.sericeTextpaint.setAntiAlias(true);
        this.sericeTextpaint.setTextAlign(Paint.Align.CENTER);
        this.sericeTextpaint.setTextSize(DensityUtil.dip2pxf(this.getContext(), 11));
        if (this.isInEditMode()) {
            this.test();
        }

    }

    private void test() {
        this.showMaxMin = true;
        String[] series = new String[]{"Series1"};
        ArrayList datas = new ArrayList();
        ArrayList xnames = new ArrayList();

        int i;
        for (i = 0; i < 20; ++i) {
            xnames.add("X" + (i + 1));
        }

        for (i = 0; i < series.length; ++i) {
            ArrayList tmpList = new ArrayList();

            for (int j = 0; j < xnames.size(); ++j) {
                tmpList.add(Float.valueOf((float) (new Random()).nextInt(1000) / 50.0F));
            }

            datas.add(tmpList);
        }
        this.setShowSeries(false);
        this.initData(series, datas, "曲线图例子", xnames, "m");
    }

    public void initData(String[] series, int[] colors, List<List<Float>> datas, String title, List<String> xnames, float maxvalue, float minvalue, String x_unit) {
        this.datas = datas;
        this.title = title;
        this.xnames = xnames;
        this.maxvalue = maxvalue;
        this.minvalue = minvalue;
        this.series = series;
        this.colors = colors;
        this.x_unit = x_unit;
        this.init();
    }

    public void initData(int[] colors, List<List<Float>> datas, List<String> xnames) {
        this.datas = datas;
        this.xnames = xnames;
        this.colors = colors;
        this.maxvalue = 1.4E-45F;
        this.minvalue = 3.4028235E38F;
        this.init();
    }

    public void initData(String[] series, List<List<Float>> datas, String title, List<String> xnames, float maxvalue, float minvalue, String x_unit) {
        this.datas = datas;
        this.title = title;
        this.xnames = xnames;
        this.maxvalue = maxvalue;
        this.minvalue = minvalue;
        this.series = series;
        this.x_unit = x_unit;
        this.init();
    }

    public void initData(String[] series, List<List<Float>> datas, String title, List<String> xnames, String x_unit) {
        this.datas = datas;
        this.title = title;
        this.xnames = xnames;
        this.series = series;
        this.x_unit = x_unit;
        this.maxvalue = 1.4E-45F;
        this.minvalue = 3.4028235E38F;
        this.init();
    }


    public void initData(String[] series, int[] colors, List<List<Float>> datas, List<String> xnames) {
        this.datas = datas;
        this.xnames = xnames;
        this.series = series;

        this.maxvalue = 1.4E-45F;
        this.minvalue = 3.4028235E38F;
        this.init();
    }

    public void initData(String[] series, List<List<Float>> datas, List<String> xnames) {
        this.datas = datas;
        this.xnames = xnames;
        this.colors = colors;
        this.series = series;
        int[] colors = {Color.parseColor("#7139ff"), Color.parseColor("#44a4fc"), Color.parseColor("#12f1ff")};
        this.colors = colors;
        this.maxvalue = 1.4E-45F;
        this.minvalue = 3.4028235E38F;
        this.init();
    }

    public void initData(String[] series, List<List<Float>> datas, String title, List<String> xnames, String x_unit, float maxvalue, float minvalue) {
        this.datas = datas;
        this.title = title;
        this.xnames = xnames;
        this.series = series;
        this.x_unit = x_unit;
        this.maxvalue = maxvalue;
        this.minvalue = minvalue;
        this.init();
    }

    public void initData(String[] series, List<List<Float>> datas, String title, List<String> xnames, String x_unit, float intervalX, float intervalY, float intervalY_values) {
        this.datas = datas;
        this.title = title;
        this.xnames = xnames;
        this.series = series;
        this.x_unit = x_unit;
        this.intervalY = intervalY;
        this.intervalX = intervalX;
        this.Y1ToScreen = intervalY / intervalY_values;
        this.intervalY_values = intervalY_values;
        this.canDrag = true;
        this.flag = true;
        this.maxvalue = this.getMinOrMaxValues(datas, true);
        this.minvalue = this.getMinOrMaxValues(datas, false);
        if (this.fMod(this.maxvalue, intervalY_values) != 0.0F) {
            this.maxvalue += intervalY_values - this.fMod(this.maxvalue, intervalY_values);
        }

        if (this.fMod(this.minvalue, intervalY_values) != 0.0F) {
            this.minvalue -= this.fMod(this.minvalue, intervalY_values);
        }

        this.init();
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                LineChartView.this.dy = (float) LineChartView.this.getHeight() - (LineChartView.this.maxvalue - LineChartView.this.minvalue) * LineChartView.this.Y1ToScreen - (float) DensityUtil.dip2px(LineChartView.this.getContext(), 55.0F) - (float) (LineChartView.this.series.length / 2 * DensityUtil.dip2px(LineChartView.this.getContext(), 10.0F));
                LineChartView.this.matrix.postTranslate(0.0F, LineChartView.this.dy);
                LineChartView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void init() {
        this.pointslist.clear();
        if (this.colors == null) {
            int[] colors = {Color.parseColor("#7139ff"), Color.parseColor("#44a4fc"), Color.parseColor("#12f1ff"), Color.parseColor("#44a4fc"), Color.parseColor("#12f1ff")};
            this.colors = colors;
        }
        int i;
        if (this.maxvalue == 1.4E-45F) {
            this.maxvalue = this.getMinOrMaxValues(this.datas, true);
            this.minvalue = this.getMinOrMaxValues(this.datas, false);
            if (this.maxvalue - this.minvalue >= 5.0F) {
                this.maxvalue = (float) Math.ceil((double) this.maxvalue);
                this.minvalue = (float) Math.floor((double) this.minvalue);
                i = 0;

                for (int[] paint = new int[]{7, 6, 5, 4, 3}; i < 100; ++i) {
                    for (int j = 0; j < paint.length; ++j) {
                        if ((this.maxvalue - this.minvalue) % (float) paint[j] == 0.0F) {
                            this.Ycount = paint[j];
                            i = 100;
                            break;
                        }
                    }

                    if (i % 2 == 1) {
                        ++this.maxvalue;
                    } else if (this.minvalue != 0.0F) {
                        --this.minvalue;
                    }
                }
            }
        }

        for (i = 0; i < this.datas.size(); ++i) {
            Paint var4 = new Paint();
            var4.setStyle(Paint.Style.STROKE);
            var4.setAntiAlias(true);
            var4.setColor(this.colors[i]);
            var4.setTextAlign(Paint.Align.CENTER);
            var4.setStrokeWidth((float) DensityUtil.dip2px(this.getContext(), 1.0F));
            this.paintlist.add(var4);
        }

        //选择的画笔设置
        this.paint_select = new Paint();
        this.paint_select.setStyle(Paint.Style.STROKE);
        this.paint_select.setAntiAlias(true);
        this.paint_select.setColor(Color.rgb(0, 155, 0));
        this.paint_select.setStrokeWidth((float) DensityUtil.dip2px(this.getContext(), 2.0F));

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
        this.paintwhite.setTextAlign(Paint.Align.LEFT);

        this.painttest = new Paint();
        this.painttest.setStyle(Paint.Style.FILL);
        this.painttest.setAntiAlias(true);
        this.painttest.setColor(Color.WHITE);
        this.bitmap_fullScreen = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_func_myworker);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                LineChartView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                LineChartView.this.invalidate();
            }
        });
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.datas.size() != 0) {
            canvas.save();
            this.width = this.getWidth();
            this.height = this.getHeight();
            this.dst_fullScreen.left = this.getWidth() - DensityUtil.dip2px(this.getContext(), 36.0F);
            this.dst_fullScreen.top = this.getHeight() - DensityUtil.dip2px(this.getContext(), 23.0F);
            this.dst_fullScreen.right = this.getWidth() - DensityUtil.dip2px(this.getContext(), 8.0F);
            this.dst_fullScreen.bottom = this.getHeight() - DensityUtil.dip2px(this.getContext(), 3.0F);
            this.maxY = (float) DensityUtil.dip2px(this.getContext(), 15.0F);
            if (this.series != null) {
                this.minY = (float) (this.height - DensityUtil.dip2px(this.getContext(), 30.0F) - this.series.length / 3 * DensityUtil.dip2px(this.getContext(), 10.0F) - (this.series.length % 3 == 0 ? 0 : 1) * DensityUtil.dip2px(this.getContext(), 10.0F));
            } else {
                this.minY = (float) (this.height - DensityUtil.dip2px(this.getContext(), 30.0F));
            }

            if (this.flag) {
                this.Ycount = (int) ((this.maxvalue - this.minvalue) / this.intervalY_values);
            } else {
                if (this.Ycount == -1) {
                    this.Ycount = 5;
                }

                if (Math.abs(this.maxvalue - this.minvalue) < 5.0F) {
                    this.Ycount = (int) Math.abs(this.maxvalue - this.minvalue) + 1;
                }
                this.intervalY = (this.minY - this.maxY) / (float) this.Ycount;
                this.Y1ToScreen = (this.minY - this.maxY) / (this.maxvalue - this.minvalue);
            }

            this.maxX = (float) (this.width - DensityUtil.dip2px(this.getContext(), 10.0F));
            this.minX = (float) ScreenUtils.gettextwidth(this.getContext(), this.df.format((double) this.maxvalue), 8);
            if (!this.flag) {
                this.intervalX = (this.maxX - this.minX - (float) DensityUtil.dip2px(this.getContext(), 10.0F)) / (float) ((List) this.datas.get(0)).size();
            }
            float rectw = this.intervalX * 2.0F / 3.0F;
            float startx = this.intervalX * 1.0F / 6.0F + (float) DensityUtil.dip2px(this.getContext(), 5.0F);
            this.regionwidth = this.intervalY > this.intervalX ? (int) this.intervalX : (int) this.intervalY;
            canvas.concat(this.matrix);

            int touchpoint;
            if (isShowX) {
                for (touchpoint = 0; touchpoint < this.Ycount + 1; ++touchpoint) {
                    //画x轴
                    canvas.drawLine(this.minX, this.maxY + this.intervalY * (float) touchpoint, this.maxX, this.maxY + this.intervalY * (float) touchpoint, this.paintline);
                }
            }

            int isEmpty;
            int var12;
            if (this.pointslist.size() == 0) {
                LineChartView.MPoint i;
                for (touchpoint = 0; touchpoint < this.datas.size(); ++touchpoint) {
                    ArrayList ii = new ArrayList();

                    for (isEmpty = 0; isEmpty < ((List) this.datas.get(touchpoint)).size(); ++isEmpty) {
                        if (((Float) ((List) this.datas.get(touchpoint)).get(isEmpty)).floatValue() >= this.minvalue) {
                            i = new LineChartView.MPoint(this.minX + (float) isEmpty * this.intervalX + startx + rectw / 2.0F, this.maxY + (this.maxvalue - ((Float) ((List) this.datas.get(touchpoint)).get(isEmpty)).floatValue()) * this.Y1ToScreen);
                            ii.add(i);
                        }
                    }
                    this.pointslist.add(ii);
                }

                for (touchpoint = 0; touchpoint < this.pointslist.size(); ++touchpoint) {
                    if (((List) this.pointslist.get(touchpoint)).size() > 1) {
                        for (var12 = 1; var12 < ((List) this.pointslist.get(touchpoint)).size(); ++var12) {
                            if (var12 >= 0) {
                                LineChartView.MPoint var14 = (LineChartView.MPoint) ((List) this.pointslist.get(touchpoint)).get(var12);
                                if (var12 == 0) {
                                    i = (LineChartView.MPoint) ((List) this.pointslist.get(touchpoint)).get(var12 + 1);
                                    var14.dx = (i.x - var14.x) / 5.0F;
                                    var14.dy = (i.y - var14.y) / 5.0F;
                                } else if (var12 == ((List) this.pointslist.get(touchpoint)).size() - 1) {
                                    i = (LineChartView.MPoint) ((List) this.pointslist.get(touchpoint)).get(var12 - 1);
                                    var14.dx = (var14.x - i.x) / 5.0F;
                                    var14.dy = (var14.y - i.y) / 5.0F;
                                } else {
                                    i = (LineChartView.MPoint) ((List) this.pointslist.get(touchpoint)).get(var12 + 1);
                                    LineChartView.MPoint modeNum = (LineChartView.MPoint) ((List) this.pointslist.get(touchpoint)).get(var12 - 1);
                                    var14.dx = (i.x - modeNum.x) / 5.0F;
                                    var14.dy = (i.y - modeNum.y) / 5.0F;
                                }
                            }
                        }
                    }
                }
            }

            this.regionlist.clear();
            LineChartView.TPoint var13 = null;

            for (var12 = 0; var12 < this.datas.size(); ++var12) {
                for (isEmpty = 0; isEmpty < ((List) this.datas.get(var12)).size(); ++isEmpty) {
                    if (((Float) ((List) this.datas.get(var12)).get(isEmpty)).floatValue() >= this.minvalue) {
                        PointF var17 = new PointF(this.minX + (float) isEmpty * this.intervalX + startx + rectw / 2.0F, this.maxY + (this.maxvalue - ((Float) ((List) this.datas.get(var12)).get(isEmpty)).floatValue()) * this.Y1ToScreen);
                        if (this.touchitem[0] == var12 && this.touchitem[1] == isEmpty) {
                            var13 = new LineChartView.TPoint();
                            var13.x = var17.x;
                            var13.y = var17.y;
                            var13.setIndex(var12);
                            var13.value = ((Float) ((List) this.datas.get(var12)).get(isEmpty)).floatValue();
                        }

                        LineChartView.MRegion var18 = new LineChartView.MRegion();
                        var18.set((int) ((this.minX + (float) isEmpty * this.intervalX + startx + rectw / 2.0F - (float) (this.regionwidth / 2)) * this.dscale + this.dx), (int) ((this.maxY + (this.maxvalue - ((Float) ((List) this.datas.get(var12)).get(isEmpty)).floatValue()) * this.Y1ToScreen - (float) (this.regionwidth / 2)) * this.dscale + this.dy), (int) ((this.minX + (float) isEmpty * this.intervalX + startx + rectw / 2.0F + (float) (this.regionwidth / 2)) * this.dscale + this.dx), (int) ((this.maxY + (this.maxvalue - ((Float) ((List) this.datas.get(var12)).get(isEmpty)).floatValue()) * this.Y1ToScreen + (float) (this.regionwidth / 2)) * this.dscale + this.dy));
                        var18.setValue(((Float) ((List) this.datas.get(var12)).get(isEmpty)).floatValue());
                        var18.setIJ(var12, isEmpty);
                        this.regionlist.add(var18);
                    }
                }
            }

            this.drawData(canvas);
            if (var13 != null) {
                if (this.seriesRegionList != null && this.seriesRegionList.size() != 0) {
                    var12 = var13.getIndex();
                    Iterator var15 = this.seriesRegionList.iterator();

                    while (var15.hasNext()) {
                        LineChartView.MRegion var19 = (LineChartView.MRegion) var15.next();
                        if (var19.getI() == var12 && var19.isShow()) {
                            this.drawTouchValue(canvas, var13);
                        }
                    }
                } else {
                    this.drawTouchValue(canvas, var13);
                }
            }
            canvas.restore();
            if (this.title != null) {
                //绘制标题
                canvas.drawText(this.title, (float) (this.width / 2), ScreenUtils.getTextHeight(this.textpaint.getTextSize()), this.textpaint);
            }
            for (var12 = 0; var12 < this.Ycount + 1; ++var12) {
                this.textpaint.setTextAlign(Paint.Align.RIGHT);
                String var16;
                if ((int) Math.abs(this.maxvalue - this.minvalue) < 5) {
                    var16 = this.df.format((double) (this.maxvalue - (float) var12 * ((this.maxvalue - this.minvalue) * 1.0F / (float) this.Ycount)));
                } else {
                    var16 = this.df2.format((double) (this.maxvalue - (float) var12 * ((this.maxvalue - this.minvalue) * 1.0F / (float) this.Ycount)));
                }
                canvas.drawText(var16, this.minX - (float) DensityUtil.dip2px(this.getContext(), 2.0F), (this.maxY + this.intervalY * (float) var12) * this.dscale + this.dy, this.textpaint);
            }

            this.textpaint.setTextAlign(Paint.Align.CENTER);
            var12 = this.xnames.size() / (this.x_count_max / 2 + 1);
            var12 = var12 == 0 ? 1 : var12;

            for (isEmpty = 0; isEmpty < this.xnames.size(); isEmpty += var12) {
                canvas.drawText((String) this.xnames.get(isEmpty), (this.minX + (float) isEmpty * this.intervalX + startx + rectw / 2.0F) * this.dscale + this.dx, this.minY + (float) DensityUtil.dip2px(this.getContext(), 10.0F), this.textpaint);
            }

            this.textpaint.setTextAlign(Paint.Align.LEFT);
            if (this.isShowSeries) {
                canvas.drawText("横坐标：" + this.x_unit, (float) DensityUtil.dip2px(this.getContext(), 15.0F), this.minY + (float) DensityUtil.dip2px(this.getContext(), 25.0F), this.textpaint);
                canvas.drawText("纵坐标：", (float) DensityUtil.dip2px(this.getContext(), 15.0F), this.minY + (float) DensityUtil.dip2px(this.getContext(), 37.0F), this.textpaint);
            }

            boolean var22 = this.seriesRegionList.isEmpty();
            if (this.series != null) {
                for (int var21 = 0; var21 < this.series.length; ++var21) {
                    int var20 = var21 % 3;
                    float move = var20 > 0 ? (float) ((this.width - DensityUtil.dip2px(this.getContext(), 50.0F)) / 3) : 0.0F;
                    float y = this.minY + (float) DensityUtil.dip2px(this.getContext(), (float) (var21 / 3 * 10 + 35));
                    if (isShowSeriesTip) {

                        canvas.drawText(this.series[var21], (float) DensityUtil.dip2px(this.getContext(), 71.0F) + move * (float) var20, y + (float) DensityUtil.dip2px(this.getContext(), 2.0F), this.sericeTextpaint);

                        Paint mPaint = this.paintlist.get(var21);
                        mPaint.setStyle(Paint.Style.FILL);
                        //绘制点
                        canvas.drawCircle((float) DensityUtil.dip2px(this.getContext(), 40.0F) + move * (float) var20, y, (float) DensityUtil.dip2px(this.getContext(), 3.5F), mPaint);
                        canvas.drawCircle((float) DensityUtil.dip2px(this.getContext(), 40.0F) + move * (float) var20, y, (float) DensityUtil.dip2px(this.getContext(), 3.0F), paintwhite);
                        canvas.drawCircle((float) DensityUtil.dip2px(this.getContext(), 40.0F) + move * (float) var20, y, (float) DensityUtil.dip2px(this.getContext(), 1.5F), mPaint);
                        mPaint.setStyle(Paint.Style.STROKE);
                    }
                    if (var22) {
                        LineChartView.MRegion mRegion = new LineChartView.MRegion();
                        mRegion.set((int) ((float) DensityUtil.dip2px(this.getContext(), 50.0F) + move * (float) var20), (int) (y - (float) DensityUtil.dip2px(this.getContext(), 5.0F)), (int) ((float) DensityUtil.dip2px(this.getContext(), 70.0F) + move * (float) var20), (int) (y + (float) DensityUtil.dip2px(this.getContext(), 5.0F)));
                        mRegion.setIJ(var21, 0);
                        mRegion.setShow(true);
                        this.seriesRegionList.add(mRegion);
                    }
                }
            }

            if (this.pointslist.size() > 0 && this.timeSum > this.time) {
                ++this.time;
                this.handler.postDelayed(new Runnable() {
                    public void run() {
                        LineChartView.this.invalidate();
                    }
                }, (long) this.intervalTime);
            }

            if (this.canFullScreen) {
                canvas.drawBitmap(this.bitmap_fullScreen, (Rect) null, this.dst_fullScreen, (Paint) null);
            }

        }
    }

    private void drawTouchValue(Canvas canvas, LineChartView.TPoint tPoint) {
        String format = "%.0f";
        if ((float) ((int) tPoint.getValue()) == tPoint.getValue()) {
            format = "%.0f";
        } else {
            format = "%.2f";
        }

        //提示字体大小
        paintwhite.setTextSize(DensityUtil.sp2px(this.getContext(), 10));

        int textLength = this.getTextWidth(this.paintwhite, String.format(format, new Object[]{Float.valueOf(tPoint.getValue())}));
        int textHeight = this.getFontHeight(this.paintwhite);
        float ascent = Math.abs(this.paintwhite.getFontMetrics().ascent);
        int dp2 = DensityUtil.dip2px(this.getContext(), 2.0F);
        Path path;
        if (tPoint.x + (float) textLength + (float) (2 * dp2) > (float) this.width) {
            if (tPoint.y - (float) textHeight - (float) (2 * dp2) - (float) (3 * dp2) < 0.0F) {
                path = new Path();
                path.moveTo(tPoint.x, tPoint.y);
                path.lineTo(tPoint.x - (float) (2 * dp2), tPoint.y + (float) (3 * dp2));
                path.lineTo(tPoint.x - (float) (2 * dp2) - (float) textLength, tPoint.y + (float) (3 * dp2));
                path.lineTo(tPoint.x - (float) (2 * dp2) - (float) textLength, tPoint.y + (float) (3 * dp2) + (float) (2 * dp2) + (float) textHeight);
                path.lineTo(tPoint.x, tPoint.y + (float) (3 * dp2) + (float) (2 * dp2) + (float) textHeight);
                path.close();
                canvas.drawPath(path, this.paintValue);
                canvas.drawText(String.format(format, new Object[]{Float.valueOf(tPoint.getValue())}), tPoint.x - (float) dp2 - (float) textLength, tPoint.y + (float) (3 * dp2) + (float) dp2 + ascent, this.paintwhite);
            } else {
                path = new Path();
                path.moveTo(tPoint.x, tPoint.y);
                path.lineTo(tPoint.x - (float) (2 * dp2), tPoint.y - (float) (3 * dp2));
                path.lineTo(tPoint.x - (float) (2 * dp2) - (float) textLength, tPoint.y - (float) (3 * dp2));
                path.lineTo(tPoint.x - (float) (2 * dp2) - (float) textLength, tPoint.y - (float) (3 * dp2) - (float) (2 * dp2) - (float) textHeight);
                path.lineTo(tPoint.x, tPoint.y - (float) (3 * dp2) - (float) (2 * dp2) - (float) textHeight);
                path.close();
                canvas.drawPath(path, this.paintValue);
                canvas.drawText(String.format(format, new Object[]{Float.valueOf(tPoint.getValue())}), tPoint.x - (float) dp2 - (float) textLength, tPoint.y - (float) (3 * dp2) - (float) dp2 - (float) textHeight + ascent, this.paintwhite);
            }
        } else if (tPoint.y - (float) textHeight - (float) (2 * dp2) - (float) (3 * dp2) < 0.0F) {
            path = new Path();
            path.moveTo(tPoint.x, tPoint.y);
            path.lineTo(tPoint.x + (float) (2 * dp2), tPoint.y + (float) (3 * dp2));
            path.lineTo(tPoint.x + (float) (2 * dp2) + (float) textLength, tPoint.y + (float) (3 * dp2));
            path.lineTo(tPoint.x + (float) (2 * dp2) + (float) textLength, tPoint.y + (float) (3 * dp2) + (float) (2 * dp2) + (float) textHeight);
            path.lineTo(tPoint.x, tPoint.y + (float) (3 * dp2) + (float) (2 * dp2) + (float) textHeight);
            path.close();
            canvas.drawPath(path, this.paintValue);
            canvas.drawText(String.format(format, new Object[]{Float.valueOf(tPoint.getValue())}), tPoint.x + (float) dp2, tPoint.y + (float) (3 * dp2) + (float) dp2 + ascent, this.paintwhite);
        } else {
            path = new Path();
            path.moveTo(tPoint.x, tPoint.y);
            path.lineTo(tPoint.x + (float) (2 * dp2), tPoint.y - (float) (3 * dp2));
            path.lineTo(tPoint.x + (float) (2 * dp2) + (float) textLength, tPoint.y - (float) (3 * dp2));
            path.lineTo(tPoint.x + (float) (2 * dp2) + (float) textLength, tPoint.y - (float) (3 * dp2) - (float) (2 * dp2) - (float) textHeight);
            path.lineTo(tPoint.x, tPoint.y - (float) (3 * dp2) - (float) (2 * dp2) - (float) textHeight);
            path.close();
            canvas.drawPath(path, this.paintValue);
            canvas.drawText(String.format(format, new Object[]{Float.valueOf(tPoint.getValue())}), tPoint.x + (float) dp2, tPoint.y - (float) (3 * dp2) - (float) dp2 - (float) textHeight + ascent, this.paintwhite);
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

    private void drawData(Canvas canvas) {
        float time = (float) this.time * 1.0F / (float) this.timeSum * (float) ((List) this.pointslist.get(0)).size();

        int i;
        LineChartView.MPoint point;
        for (i = 0; i < this.pointslist.size(); ++i) {
//        for (i = this.pointslist.size()-1; i >=0 ; --i) {
            if ((this.seriesRegionList.isEmpty() || ((LineChartView.MRegion) this.seriesRegionList.get(i)).isShow()) && ((List) this.pointslist.get(i)).size() > 0) {
                Path max = null;
                Path max2 = null;
                boolean min = true;
                float mMaxX = 0;

                for (int isDrawMax = 0; (float) isDrawMax < time; ++isDrawMax) {
                    LineChartView.MPoint isDrawMin = (LineChartView.MPoint) ((List) this.pointslist.get(i)).get(isDrawMax);
                    float j = this.minY - isDrawMin.y - (float) this.time * 1.0F / (float) this.timeSum * (this.minY - isDrawMin.y);

                    if (isDrawMax == 0) {
                        max2 = new Path();
                        max2.moveTo(isDrawMin.x, this.minY);
                    }
                    max2.lineTo(isDrawMin.x, isDrawMin.y);

                    if (min) {
                        max = new Path();
                        min = false;
                        max.moveTo(isDrawMin.x, isDrawMin.y + j);
                    } else if (((Float) ((List) this.datas.get(i)).get(isDrawMax)).floatValue() == 1.4E-45F) {
                        canvas.drawPath(max, (Paint) this.paintlist.get(i));
                        min = true;
                        --isDrawMax;
                    } else if (this.isBezier) {
                        point = (LineChartView.MPoint) ((List) this.pointslist.get(i)).get(isDrawMax - 1);
                        max.cubicTo(point.x + point.dx, point.y + point.dy, isDrawMin.x - isDrawMin.dx, isDrawMin.y - isDrawMin.dy, isDrawMin.x, isDrawMin.y);
                    } else {
                        max.lineTo(isDrawMin.x, isDrawMin.y + j);
                    }
                    mMaxX = isDrawMin.x;
                }

                //绘制线条
                canvas.drawPath(max, (Paint) this.paintlist.get(i));
                max2.lineTo(mMaxX, this.minY);

                this.paintlist.get(i).setStyle(Paint.Style.FILL);
                if (i == 0) {
                    this.paintlist.get(i).setAlpha(77);
                } else if (i == 1) {
                    this.paintlist.get(i).setAlpha(128);
                } else if (i == 2) {
                    this.paintlist.get(i).setAlpha(55);
                } else {
                    this.paintlist.get(i).setAlpha(80);
                }
                canvas.drawPath(max2, (Paint) (Paint) this.paintlist.get(i));
                //恢复原来的配置
                this.paintlist.get(i).setStyle(Paint.Style.STROKE);
                this.paintlist.get(i).setAlpha(255);
            }

            //分层次显示点还是全部显示在第一层
//        }
//        for (i = 0; i < this.pointslist.size(); ++i) {

            if ((this.seriesRegionList.isEmpty() || ((LineChartView.MRegion) this.seriesRegionList.get(i)).isShow()) && ((List) this.pointslist.get(i)).size() > 0) {
                float var13 = 3.4028235E38F;
                float var14 = 1.4E-45F;
                if (this.showMaxMin) {
                    var13 = this.getMaxValue((List) this.datas.get(i));
                    var14 = this.getMinValue((List) this.datas.get(i));
                }

                boolean var15 = false;
                boolean var16 = false;
                for (int var17 = 0; (float) var17 < time; ++var17) {
                    point = (LineChartView.MPoint) ((List) this.pointslist.get(i)).get(var17);
                    float offsetY = this.minY - point.y - (float) this.time * 1.0F / (float) this.timeSum * (this.minY - point.y);
                    if (this.showMaxMin) {
                        String format;
                        float strokewidth;
                        if (var13 != var14) {
                            if (!var15 && ((Float) ((List) this.datas.get(i)).get(var17)).floatValue() == var13) {
                                format = "%.0f";
                                if ((float) ((Float) ((List) this.datas.get(i)).get(var17)).intValue() == ((Float) ((List) this.datas.get(i)).get(var17)).floatValue()) {
                                    format = "%.0f";
                                } else {
                                    format = "%.2f";
                                }

                                strokewidth = ((Paint) this.paintlist.get(i)).getStrokeWidth();
                                ((Paint) this.paintlist.get(i)).setStrokeWidth(1.0F);
                                canvas.drawText(String.format(format, new Object[]{((List) this.datas.get(i)).get(var17)}), point.x, point.y - 5.0F, (Paint) this.paintlist.get(i));
                                ((Paint) this.paintlist.get(i)).setStrokeWidth(strokewidth);
                                ((Paint) this.paintlist.get(i)).setStyle(Paint.Style.FILL);
                                canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 2.0F), this.touchitem[0] == i && this.touchitem[1] == var17 ? this.paint_select : (Paint) this.paintlist.get(i));
                                ((Paint) this.paintlist.get(i)).setStyle(Paint.Style.STROKE);
                                var15 = true;
                            } else if (!var16 && ((Float) ((List) this.datas.get(i)).get(var17)).floatValue() == var14) {
                                format = "%.0f";
                                if ((float) ((Float) ((List) this.datas.get(i)).get(var17)).intValue() == ((Float) ((List) this.datas.get(i)).get(var17)).floatValue()) {
                                    format = "%.0f";
                                } else {
                                    format = "%.2f";
                                }

                                strokewidth = ((Paint) this.paintlist.get(i)).getStrokeWidth();
                                ((Paint) this.paintlist.get(i)).setStrokeWidth(1.0F);
                                canvas.drawText(String.format(format, new Object[]{((List) this.datas.get(i)).get(var17)}), point.x, point.y - 5.0F, (Paint) this.paintlist.get(i));
                                ((Paint) this.paintlist.get(i)).setStrokeWidth(strokewidth);
                                ((Paint) this.paintlist.get(i)).setStyle(Paint.Style.FILL);
                                canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 2.0F), this.touchitem[0] == i && this.touchitem[1] == var17 ? this.paint_select : (Paint) this.paintlist.get(i));
                                ((Paint) this.paintlist.get(i)).setStyle(Paint.Style.STROKE);
                                var16 = true;
                            } else {
                                canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 1.0F), this.paintwhite);
                                canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 2.0F), this.touchitem[0] == i && this.touchitem[1] == var17 ? this.paint_select : (Paint) this.paintlist.get(i));
                            }
                        } else if (!var16 && !var15) {
                            format = "%.0f";
                            if ((float) ((Float) ((List) this.datas.get(i)).get(var17)).intValue() == ((Float) ((List) this.datas.get(i)).get(var17)).floatValue()) {
                                format = "%.0f";
                            } else {
                                format = "%.2f";
                            }

                            strokewidth = ((Paint) this.paintlist.get(i)).getStrokeWidth();
                            ((Paint) this.paintlist.get(i)).setStrokeWidth(1.0F);
                            canvas.drawText(String.format(format, new Object[]{((List) this.datas.get(i)).get(var17)}), point.x, point.y - 5.0F, (Paint) this.paintlist.get(i));
                            ((Paint) this.paintlist.get(i)).setStrokeWidth(strokewidth);
                            ((Paint) this.paintlist.get(i)).setStyle(Paint.Style.FILL);
                            canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 2.0F), this.touchitem[0] == i && this.touchitem[1] == var17 ? this.paint_select : (Paint) this.paintlist.get(i));
                            ((Paint) this.paintlist.get(i)).setStyle(Paint.Style.STROKE);
                            var16 = true;
                            var15 = true;
                        } else {
                            canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 1.0F), this.paintwhite);
                            canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 2.0F), this.touchitem[0] == i && this.touchitem[1] == var17 ? this.paint_select : (Paint) this.paintlist.get(i));
                        }
                    } else {
                        //绘制点
                        Paint mPaint = this.touchitem[0] == i && this.touchitem[1] == var17 ? this.paint_select : (Paint) this.paintlist.get(i);
                        mPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 3.5F), mPaint);
                        canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 3.0F), paintwhite);
                        canvas.drawCircle(point.x, point.y + offsetY, (float) DensityUtil.dip2px(this.getContext(), 1.5F), mPaint);
                        mPaint.setStyle(Paint.Style.STROKE);
                    }
                }
            }
        }
    }

    private float getMaxValue(List<Float> values) {
        float max = 1.4E-45F;

        for (int i = 0; i < values.size(); ++i) {
            if (((Float) values.get(i)).floatValue() > max) {
                max = ((Float) values.get(i)).floatValue();
            }
        }

        return max;
    }

    private float getMinValue(List<Float> values) {
        float min = 3.4028235E38F;

        for (int i = 0; i < values.size(); ++i) {
            if (((Float) values.get(i)).floatValue() < min) {
                min = ((Float) values.get(i)).floatValue();
            }
        }

        return min;
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

            max = max == 1.4E-45F ? 0.0F : max * 1.1F;
            min = min == 3.4028235E38F ? 0.0F : min * 0.9F;
            return isMax ? max : min;
        } else {
            return 0.0F;
        }
    }

    private float fMod(float f1, float f2) {
        int i1 = (int) (f1 * 100.0F);
        int i2 = (int) (f2 * 100.0F);
        return (float) ((double) (i1 % i2) / 100.0D);
    }

    boolean isCanOnTouch = false;

    public boolean onTouchEvent(MotionEvent event) {
        if (isCanOnTouch) {
            switch (event.getAction() & 255) {
                case 0:
                    this.mode = 1;
                    this.x_down = event.getX();
                    this.y_down = event.getY();
                    this.savedMatrix.set(this.matrix);
                    boolean showPoint = false;

                    for (int var6 = 0; var6 < this.regionlist.size(); ++var6) {
                        if (((LineChartView.MRegion) this.regionlist.get(var6)).contains((int) event.getX(), (int) event.getY())) {
                            this.touchitem[0] = ((LineChartView.MRegion) this.regionlist.get(var6)).getI();
                            this.touchitem[1] = ((LineChartView.MRegion) this.regionlist.get(var6)).getJ();
                            showPoint = true;
                            this.invalidate();
                            break;
                        }
                    }

                    Iterator var7 = this.seriesRegionList.iterator();

                    while (var7.hasNext()) {
                        LineChartView.MRegion var8 = (LineChartView.MRegion) var7.next();
                        if (var8.contains((int) this.x_down, (int) this.y_down)) {
                            var8.setShow(!var8.isShow());
                            this.invalidate();
                            break;
                        }
                    }

                    //点击跳转至大屏幕的
                    if (this.canFullScreen && this.dst_fullScreen.contains((int) event.getX(), (int) event.getY())) {
//                        Bundle var9 = new Bundle();
//                        var9.putSerializable("datas", (ArrayList) this.datas);
//                        var9.putStringArrayList("xnames", (ArrayList) this.xnames);
//                        var9.putStringArray("series", this.series);
//                        var9.putFloat("maxvalue", this.maxvalue);
//                        var9.putFloat("minvalue", this.minvalue);
//                        var9.putString("x_unit", this.x_unit);
//                        var9.putString("chartType", "曲线图");
//                        Intent var10 = new Intent(this.getContext(), MainActivity.class);
//                        var10.putExtras(var9);
//                        this.getContext().startActivity(var10);
                    }
                    break;
                case 1:
                case 6:
                    this.mode = 0;
                    break;
                case 2:
                    if (this.mode == 2) {
                        this.matrix1.set(this.savedMatrix);
                        float rotation = this.rotation(event) - this.oldRotation;
                        float newDist = this.spacing(event);
                        float var10000 = newDist / this.oldDist;
                        if (!this.matrixCheck()) {
                            this.matrix.set(this.matrix1);
                            this.invalidate();
                        }
                    } else if (this.mode == 1 && this.canDrag) {
                        this.matrix1.set(this.savedMatrix);
                        this.matrix1.postTranslate(event.getX() - this.x_down, event.getY() - this.y_down);
                        if (!this.matrixCheck()) {
                            this.matrix.set(this.matrix1);
                            this.invalidate();
                        }
                    }
                case 3:
                case 4:
                default:
                    break;
                case 5:
                    this.mode = 2;
                    this.oldDist = this.spacing(event);
                    this.oldRotation = this.rotation(event);
                    this.savedMatrix.set(this.matrix);
                    this.midPoint(this.mid, event);
            }
        }
        return true;
    }

    private boolean matrixCheck() {
        float[] f = new float[9];
        this.matrix1.getValues(f);
        float x1 = f[0] * 0.0F + f[1] * 0.0F + f[2];
        float y1 = f[3] * 0.0F + f[4] * 0.0F + f[5];
        float x2 = f[0] * (float) this.width + f[1] * 0.0F + f[2];
        float y2 = f[3] * (float) this.width + f[4] * 0.0F + f[5];
        float x3 = f[0] * 0.0F + f[1] * (float) this.height + f[2];
        float y3 = f[3] * 0.0F + f[4] * (float) this.height + f[5];
        float x4 = f[0] * (float) this.width + f[1] * (float) this.height + f[2];
        float y4 = f[3] * (float) this.width + f[4] * (float) this.height + f[5];
        double width = Math.sqrt((double) ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
        this.dscale = (float) (width / (double) this.width);
        if (width >= width / 3.0D && width <= (double) (this.height * 3)) {
            float miny;
            if (this.intervalX * (float) this.xnames.size() + (float) DensityUtil.dip2px(this.getContext(), 20.0F) > (float) this.width) {
                miny = (float) this.width - this.intervalX * (float) this.xnames.size() - (float) DensityUtil.dip2px(this.getContext(), 20.0F);
                if (x1 > 0.0F) {
                    x1 = 0.0F;
                } else if (x1 < miny) {
                    x1 = miny;
                }
            } else {
                x1 = 0.0F;
            }

            if (this.intervalY * (this.maxvalue - this.minvalue) > (float) this.height) {
                miny = (float) this.height - (this.maxvalue - this.minvalue) * this.Y1ToScreen - (float) DensityUtil.dip2px(this.getContext(), 55.0F) - (float) (this.series != null ? this.series.length / 2 * DensityUtil.dip2px(this.getContext(), 10.0F) : 0);
                if (y1 < miny) {
                    y1 = miny;
                } else if (y1 > 0.0F) {
                    y1 = 0.0F;
                }
            } else {
                y1 = this.dy;
            }

            this.dx = x1;
            this.dy = y1;
            this.matrix1.setTranslate(x1, y1);
            return false;
        } else {
            return true;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2.0F, y / 2.0F);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (double) (event.getX(0) - event.getX(1));
        double delta_y = (double) (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public void setBezier(boolean isBezier) {
        this.isBezier = isBezier;
        this.invalidate();
    }

    public void setTextColor(int color) {
        this.textpaint.setColor(color);
    }

    public void setTextSize(float size) {
        this.textpaint.setTextSize(size);
    }

    public void setSericeTextColor(int color) {
        this.sericeTextpaint.setColor(color);
    }

    public void setSericeTextSize(int color) {
        this.sericeTextpaint.setTextSize(color);
    }

    public void setShowSeriesTip(boolean showSeriesTip) {
        isShowSeriesTip = showSeriesTip;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }

    public void setShowMaxMin(boolean showMaxMin) {
        this.showMaxMin = showMaxMin;
    }

    public void setAnimation(boolean animation) {
        this.time = animation ? 1 : this.timeSum;
    }

    public void setShowSeries(boolean isShowSeries) {
        this.isShowSeries = isShowSeries;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        this.timeSum = duration / this.intervalTime;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.pointslist.clear();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setCanOnTouch(boolean canOnTouch) {
        isCanOnTouch = canOnTouch;
    }

    public void setShowX(boolean showX) {
        isShowX = showX;
    }

    public void setCanFullScreen(boolean canFullScreen) {
        this.canFullScreen = canFullScreen;
    }

    class TPoint extends PointF {
        private float value;
        private int index;

        TPoint() {
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public float getValue() {
            return this.value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }

    class MRegion extends Region {
        private float value;
        private int i;
        private int j;
        private boolean isShow;

        MRegion() {
        }

        public int getI() {
            return this.i;
        }

        public int getJ() {
            return this.j;
        }

        public void setIJ(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public float getValue() {
            return this.value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public boolean isShow() {
            return this.isShow;
        }

        public void setShow(boolean isShow) {
            this.isShow = isShow;
        }
    }

    class MPoint {
        float x;
        float y;
        float dx;
        float dy;

        public MPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "x:" + this.x + ", y:" + this.y + " dx:" + this.dx + " dy:" + this.dy;
        }
    }
}
