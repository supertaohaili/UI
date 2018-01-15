# ui介绍

提供android 折现图、图形图，扇形图，渐变图等常用报表ui，功能强大，一个报表一个类，代码十分简单，易修改易维护，绘制性能高效，库没有导入其他的第三方包，干净整洁。
主要是：
代码简单、代码简单、代码简单，易修改易维护


# 效果图

 <img src="https://github.com/supertaohaili/UI/blob/master/TIM图片20180115113215.jpg" width="300">

apk下载链接
<a href="https://github.com/supertaohaili/UI/blob/master/app-debug.apk">https://github.com/supertaohaili/UI/blob/master/app-debug.apk</a>

# 使用
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
        compile 'com.github.supertaohaili:UI:1.0.0'
}
```

示例代码:
``` java


       //扇形
       mRingViewOne.setProgress(progress);


       //条形
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



        //折现图
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
```



### Known Issues
If you have any questions/queries/Bugs/Hugs please mail @
taohailili@gmail.com
