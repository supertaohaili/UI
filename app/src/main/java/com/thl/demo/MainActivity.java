package com.thl.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import www.thl.com.ui.BarChartView;
import www.thl.com.ui.LineChartView;
import www.thl.com.ui.RingView;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private RingView mRingViewOne;
    private RingView mRingViewTwo;
    private BarChartView mBarChartView;
    private LineChartView mLineChartView;

    private TextView tvWeiChaoShiOne;
    private TextView tvChaoShiOne;
    private TextView tvWeiChaoShiTwo;
    private TextView tvChaoShiTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.tv_title)).setText("uidemo");
        initView();
        initListen();

        initRingViewData();
        initBarChartViewData();
        initLineChartViewdata();
    }

    private void initView() {

        tvWeiChaoShiOne = (TextView) findViewById(R.id.tv_weichaoshi_one);
        tvChaoShiOne = (TextView) findViewById(R.id.tv_chaoshi_one);
        tvWeiChaoShiTwo = (TextView) findViewById(R.id.tv_weichaoshi_two);
        tvChaoShiTwo = (TextView) findViewById(R.id.tv_chaoshi_two);

        mRingViewOne = (RingView) findViewById(R.id.ringview_one);
        mRingViewOne.setBgColor(Color.parseColor("#ffdeb2"));
        mRingViewOne.setProgressColor(Color.parseColor("#ff9d1d"));

        mRingViewTwo = (RingView) findViewById(R.id.ringview_two);
        mRingViewTwo.setBgColor(Color.parseColor("#7ed4fe"));
        mRingViewTwo.setProgressColor(Color.parseColor("#28b0ff"));

        mBarChartView = (BarChartView) findViewById(R.id.bcv3_yonghushu);
        mBarChartView.setTextColor(Color.parseColor("#bfbccf"));
        mBarChartView.setCanTouch(true);

        mLineChartView = (LineChartView) findViewById(R.id.lcv);
        mLineChartView.setShowSeries(false);
        mLineChartView.setTextColor(Color.parseColor("#bfbccf"));
        mLineChartView.setSericeTextColor(Color.parseColor("#706c8d"));
        mLineChartView.setShowMaxMin(false);
        mLineChartView.setCanDrag(false);
        mLineChartView.setShowSeriesTip(true);
        mLineChartView.setCanOnTouch(true);
    }

    private void initListen() {
        findViewById(R.id.tv_ziyuan).setOnClickListener(this);
        findViewById(R.id.tv_guzhang).setOnClickListener(this);
        findViewById(R.id.tv_quanyewu).setOnClickListener(this);
        findViewById(R.id.tv_yunwei).setOnClickListener(this);
        findViewById(R.id.tv_wangyou).setOnClickListener(this);
    }

    private void initRingViewData() {
        Integer progress = Integer.valueOf(new Random().nextInt(100));
        tvWeiChaoShiOne.setText("未超时" + progress + "%");
        tvChaoShiOne.setText("超时" + (100 - progress) + "%");
        mRingViewOne.setProgress(progress);

        Integer progress2 = Integer.valueOf(new Random().nextInt(100));
        tvWeiChaoShiTwo.setText("未超时" + progress2 + "%");
        tvChaoShiTwo.setText("超时" + (100 - progress2) + "%");
        mRingViewTwo.setProgress(progress2);
    }

    private void initBarChartViewData() {
        List<String> xnames = new ArrayList<>();
        xnames.add("1月");
        xnames.add("2月");
        xnames.add("3月");
        xnames.add("4月");
        xnames.add("5月");
        xnames.add("6月");
        List<List<Float>> datas = new ArrayList<>();
        List<Float> list = new ArrayList<>();
        List<Float> list2 = new ArrayList<>();
        for (int j = 0; j < xnames.size(); ++j) {
            list.add(Float.valueOf((float) (new Random()).nextInt(5000) / 50.0F));
            list2.add(Float.valueOf((float) (new Random()).nextInt(5000) / 50.0F));
        }
        datas.add(list);
        datas.add(list2);
        mBarChartView.initData(datas, xnames);
    }

    private void initLineChartViewdata() {
        String[] series = {"满意", "良好", "一般"};
        ArrayList datas = new ArrayList();
        ArrayList xnames = new ArrayList();

        for (int i = 0; i < 12; ++i) {
            xnames.add((i + 1) + "月");
        }
        for (int i = 0; i < series.length; ++i) {
            ArrayList tmpList = new ArrayList();
            for (int j = 0; j < xnames.size(); ++j) {
                tmpList.add(Float.valueOf((float) (new Random()).nextInt(10000) / 50.0F));
            }
            datas.add(tmpList);
        }
        mLineChartView.initData(series, datas, xnames);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_ziyuan:
                break;
            case R.id.tv_guzhang:
                break;
            case R.id.tv_quanyewu:
                break;
            case R.id.tv_yunwei:
                break;
            case R.id.tv_wangyou:
                break;
        }

        if (view instanceof TextView) {
            Toast.makeText(this, ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
        }
    }
}