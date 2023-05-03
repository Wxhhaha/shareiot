package com.wxh.shareiot.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
    public static String genSecret(String mac) {
        return mac.replace(":", "");
    }

    public static String formatFloatOneNum(String number) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("##0.0");
        Double temp = Double.valueOf(number);
        return df.format(temp);
    }

    public static String formatFloatThreeNum(float number) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("##0.000");
        return df.format(number);
    }

    public static String getCurrentDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static final String byteToString(int b) {
        String stmp = Integer.toHexString(b);
        if (stmp.length() % 2 != 0) {
            return "0" + stmp;
        } else {
            return stmp;
        }
    }
}
