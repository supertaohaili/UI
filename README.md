# fingerprint介绍

android指纹解锁,支持Android原生6.0及以上+魅族+三星,借鉴github上的写法，代码简单，几个类，库没有导入其他的第三方包，干净整洁。


# 效果图

 <img src="https://github.com/supertaohaili/fingerprint/blob/master/Screenshot_20180112-102257.png" width="300"><img src="https://github.com/supertaohaili/fingerprint/blob/master/Screenshot_20180112-102323.png" width="300">

apk下载链接
<a href="https://github.com/supertaohaili/fingerprint/blob/master/app-debug.apk">https://github.com/supertaohaili/fingerprint/blob/master/app-debug.apk</a>

# 使用
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
     compile 'com.github.supertaohaili:fingerprint:1.0.0'
}
```

示例代码:
``` java
 mFingerprintIdentify = new FingerprintIdentify(this, null);
  mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.FingerprintIdentifyListener() {
             @Override
             public void onSucceed() {
                 Toast.makeText(MainActivity.this, "解锁成功", Toast.LENGTH_SHORT).show();
                 tvMsg.setTextColor(Color.parseColor("#ff333333"));
                 tvMsg.setText("解锁成功");
             }

             @Override
             public void onNotMatch(int availableTimes) {
                 Log.e("Fingerprint", "onNotMatch");
                 tvMsg.setTextColor(Color.parseColor("#ffff0101"));
                 tvMsg.setText("密码错了，还可输入" + availableTimes + "次");
                 translate(ivZhiwen);
             }

             @Override
             public void onFailed(boolean isDeviceLocked) {
                 tvMsg.setTextColor(Color.parseColor("#ffff0101"));
                 tvMsg.setText("指纹验证太过频繁，请稍后重试或者输入密码登录");
                 mTimeCount.start();
                 translate(ivZhiwen);
             }

             @Override
             public void onStartFailedByDeviceLocked() {
                 tvMsg.setTextColor(Color.parseColor("#ffff0101"));
                 tvMsg.setText("指纹验证太过频繁，请稍后重试或者输入密码登录");
                 mTimeCount.start();
                 translate(ivZhiwen);
             }
         });

    @Override
    public void onPause() {
        super.onPause();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.resumeIdentify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFingerprintIdentify != null) {
            mFingerprintIdentify.cancelIdentify();
        }
    }

```

混淆文件
```java
# MeiZuFingerprint
-keep class com.fingerprints.service.** { *; }

# SmsungFingerprint
-keep class com.samsung.android.sdk.** { *; }
```

### Known Issues
If you have any questions/queries/Bugs/Hugs please mail @
taohailili@gmail.com
