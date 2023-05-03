package com.wxh.shareiot;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
       // assertEquals("com.wxh.shareiot", appContext.getPackageName());
       // WechatUtil.sign(appContext,"wxd349a67623f92382","1673675352","00edbbefefa24079b144cd1bb2c8633b","wx14134912240123bab69a109bf4c8cd0000","apiclient_key.pem");

    }
}