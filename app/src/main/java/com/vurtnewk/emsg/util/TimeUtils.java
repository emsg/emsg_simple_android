package com.vurtnewk.emsg.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间戳工具
 * Created by Administrator on 2015/10/10.
 */
public class TimeUtils {

    /**
     * 时间戳格式转换
     */
    public static String getChatTime(long timesamp) {
        String result = "";
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = weekDays[(w + 2) % weekDays.length];
                break;
            case 3:
                result = weekDays[(w + 3) % weekDays.length];
                break;
            case 4:
                result = weekDays[(w + 4) % weekDays.length];
                break;
            case 5:
                result = weekDays[(w + 5) % weekDays.length];
                break;
            case 6:
                result = weekDays[(w + 6) % weekDays.length];
                break;

            default:
                result = getTime(timesamp);
                break;
        }
        return result;
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 把日期转换为毫秒值
     *
     * @param dateStr
     * @return
     */
    public static long getSecondsFromDate(String dateStr) {
        Date d = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            d = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d.getTime();
    }


}
