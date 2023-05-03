package com.wxh.shareiot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String between(String start, String end) {
        SimpleDateFormat formater = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        try {
            Date startDate = formater.parse(start);
            Date endDate = formater.parse(end);
            long between = endDate.getTime() - startDate.getTime();
            long day = between / (24 * 60 * 60 * 1000);
            long hour = between / (60 * 60 * 1000) - day * 24;
            long min = between / (60 * 1000) - day * 24 * 60 - hour * 60;
            long s = between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
            if (day > 0) {
                return day + "d" + hour + "h" + min + "min" + s + "s";
            } else if (hour > 0) {
                return hour + "h" + min + "min" + s + "s";
            } else {
                return min + "min" + s + "s";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
