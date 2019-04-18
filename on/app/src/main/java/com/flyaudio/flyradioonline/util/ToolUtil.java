/**
 * ToolUtil.java
 * com.chad.android.library.util
 * <p/>
 * <p/>
 * ver     date      		author
 * ---------------------------------------
 * 2015-4-9 		chadwii
 * <p/>
 * Copyright (c) 2015, chadwii All Rights Reserved.
 */

package com.flyaudio.flyradioonline.util;

import android.content.Context;
import android.text.TextUtils;

import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ClassName:ToolUtil
 *
 * @author chadwii
 * @version
 * @since Ver 1.1
 * @Date 2015-4-9  5:17:32
 *
 * @see
 */
public class ToolUtil {
    /**one hour in ms*/
    private static final int ONE_HOUR = 1 * 60 * 60 * 1000;
    /**one minute in ms*/
    private static final int ONE_MIN = 1 * 60 * 1000;
    /**one second in ms*/
    private static final int ONE_SECOND = 1 * 1000;

    private static int sScreenWidth;
    private static int sScreenHeight;
    private static float sDensity;

    public static int dp2px(Context ctx, int dp) {
        if (sDensity == 0) {
            sDensity = ctx.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * sDensity + 0.5f);
    }

    public static int px2dp(Context ctx, int px) {
        if (sDensity == 0) {
            sDensity = ctx.getResources().getDisplayMetrics().density;
        }
        return (int) (px / sDensity + 0.5f);
    }

    public static int getScreenWidth(Context ctx) {
        if (sScreenWidth == 0) {
            sScreenWidth = ctx.getResources().getDisplayMetrics().widthPixels;
        }
        return sScreenWidth;
    }

    public static int getScreenHeight(Context ctx) {
        if (sScreenHeight == 0) {
            sScreenHeight = ctx.getResources().getDisplayMetrics().heightPixels;
        }
        return sScreenHeight;
    }

    /**HH:mm:ss*/
    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().equals("") || str.trim().equals("null")) {
            return true;
        }
        return false;
    }

    public static int isInTime(String time) throws IllegalArgumentException {
        if (TextUtils.isEmpty(time) || !time.contains("-")
                || !time.contains(":")) {
            if(ConstantsOpenSdk.isDebug) {
                throw new IllegalArgumentException("Illegal Argument arg:" + time);
            }else {
                return -2;
            }
        }
        String[] args = time.split("-");
        boolean onlyHasHour = (args[0].split(":")).length == 2;
        boolean hasDay = (args[0].split(":")).length == 3;
        boolean hasYear = (args[0].split(":")).length == 5;
        SimpleDateFormat sdf = null;
        if (hasDay) {
            sdf = new SimpleDateFormat("dd:HH:mm", Locale.getDefault());
        } else if (hasYear) {
            sdf = new SimpleDateFormat("yy:MM:dd:HH:mm", Locale.getDefault());
        } else if (onlyHasHour) {
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }
        if (sdf != null) {
            String nowStr = sdf.format(new Date(System.currentTimeMillis()));
            try {
                long now = sdf.parse(nowStr).getTime();
                long start = sdf.parse(args[0]).getTime();
                if (args[1].contains("00:00") && hasDay) {
                    args[1] = (args[1].split(":"))[0] + ":" + "23:59";
                } else if (args[1].contains("00:00") && hasYear) {
                    args[1] = (args[1].split(":"))[0] + ":"
                            + (args[1].split(":"))[1] + ":"
                            + (args[1].split(":"))[2] + ":" + "23:59";
                } else if (args[1].contains("00:00") && onlyHasHour) {
                    args[1] = "23:59";
                }
                long end = sdf.parse(args[1]).getTime();
                if (now >= end) {
                    return -1;
                } else if (now >= start && now < end) {
                    return 0;
                } else {
                    return 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                if(ConstantsOpenSdk.isDebug) {
                    throw new IllegalArgumentException("Illegal Argument arg:"
                            + time);
                }else {
                    return -2;
                }
            }
        }
        return -2;
    }

    //设置时间显示格式
    public static String updateTime(int second) {
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String time = null;
        if (0 != hh) {
            time = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            time = String.format("%02d:%02d", mm, ss);
        }
        Flog.e("jjj", "updateTime//"+time);
        return time;

    }

    private static long parseTime(String time){
        Flog.e("jjj", "parseTime//"+time);
        try {
            if(!TextUtils.isEmpty(time)){
                long hourDur = 0;
                long minDur = 0;
                long secondDur = 0;
                String[] timeArray = time.split(":");
                long hour = Long.parseLong(timeArray[0]);
                long min = Long.parseLong(timeArray[1]);
                long second = 0;
                if(hour > 0){
                    hourDur = ((hour * 60) * 60) * 1000;
                }
                if(min > 0){
                    minDur = min * 60 * 1000;
                }

                if(timeArray.length >= 3){
                    second = Long.parseLong(timeArray[2]);
                    if(second > 0){
                        secondDur = secondDur * 1000;
                    }
                }
                return hourDur + minDur + secondDur;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static long getCurrent(long startTime){
        String currentStr = getCurrentTime();
        String[] currentArray = currentStr.split(":");
        String hourStr = null;
        String minStr = null;
        String secondStr = null;
        for(int i = 0; i < currentArray.length; i ++){
            if(i == 3){
                int hour = Integer.parseInt(currentArray[i]);
                if(hour > 0 && hour < 10){
                    hourStr = "0" + hour;
                }else{
                    hourStr = "" + hour;
                }
            }else if(i == 4){
                int min = Integer.parseInt(currentArray[i]);
                if(min > 0 && min < 10){
                    minStr = "0" + min;
                }else{
                    minStr = "" + min;
                }
            }else if(i == 5){
                int second = Integer.parseInt(currentArray[i]);
                if(second > 0 && second < 10){
                    secondStr = "0" + second;
                }else{
                    secondStr = "" + second;
                }
            }
        }
        String betweenTime = hourStr + ":" + minStr + ":" +secondStr;
        long currentTime = parseTime(betweenTime);
        return currentTime - startTime;
    }

    public static long getDuration(Schedule schedule){
        try {
            String[] endTimeArray = schedule.getEndTime().split(":");
            String[] startTimeArray = schedule.getStartTime().split(":");
            String hourStr = null;
            String minStr = null;
            int hour = -1;
            int min = -1;
            for(int i = 0; i < startTimeArray.length; i++){
                if(i == 3){
                    Flog.e("jjj", "getDuration//"+endTimeArray[i]+"//"+startTimeArray[i]);
                    if(Integer.parseInt(endTimeArray[i]) == 0){
                        hour = 24 - Integer.parseInt(startTimeArray[i]);
                    }else{
                        hour = Integer.parseInt(endTimeArray[i]) - Integer.parseInt(startTimeArray[i]);
                    }

                }else if(i == 4){
                    Flog.e("jjj", "getDuration//"+endTimeArray[i]+"//"+startTimeArray[i]);
                    min = Integer.parseInt(endTimeArray[i]) - Integer.parseInt(startTimeArray[i]);
                }
            }
            if(hour >= 0 && hour < 10){
                hourStr = "" + 0 + hour;
            }else{
                hourStr = hour + "";
            }

            if(min >= 0 && min < 10){
                minStr = "" + 0 + min;
            }else{
                minStr = min + "";
            }
            String betweenTime = hourStr + ":" + minStr;
            long duration = Math.abs(parseTime(betweenTime));
            Flog.e("jjj", "getDuration//"+duration);
            return duration;
        }catch (Exception e){
            Flog.e("jjj", "getDuration//Exception//"+e.getMessage().toString());
        }
        return -1;
    }

    public static String getProgramStatus(Schedule schedule){
        String[] endTimeArray = schedule.getEndTime().split(":");
        String[] startTimeArray = schedule.getStartTime().split(":");
        String[] currentArray = getCurrentTime().split(":");
        for(int i = 0; i < endTimeArray.length; i ++){
            Flog.e("jjj", schedule.getRelatedProgram().getProgramName()+"//"+endTimeArray[i]+"//"+currentArray[i]);
            if((i == endTimeArray.length -2) && Integer.parseInt(endTimeArray[i]) == 0){
                break;
            }
            if(Integer.parseInt(endTimeArray[i]) < Integer.parseInt(currentArray[i])){
                return "回听";
            }else if(Integer.parseInt(endTimeArray[i]) > Integer.parseInt(currentArray[i])){
                break;
            }
        }
        for(int i = 0; i < startTimeArray.length; i++){
            if(Integer.parseInt(startTimeArray[i]) > Integer.parseInt(currentArray[i])){
                return "未开始";
            }else if(Integer.parseInt(startTimeArray[i]) < Integer.parseInt(currentArray[i])){
                break;
            }
        }

        return "直播";
    }

    public static String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        String mYear = (c.get(Calendar.YEAR) + "").substring(2); //年份
        int mMonth = c.get(Calendar.MONTH) + 1;//月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);//日期
        int mHour = c.get(Calendar.HOUR_OF_DAY);//时
        int mMinute = c.get(Calendar.MINUTE);//分
        int mSecond = c.get(Calendar.SECOND);//秒
        return mYear + ":" + mMonth + ":" +mDay + ":" + mHour + ":" + mMinute + ":" +mSecond;
    }

    public static String getLastDay(){
        String currentDay = getWeek();
        if(!TextUtils.isEmpty(currentDay)){
            switch (currentDay){
                case "星期日":
                    return "6";
                case "星期一":
                    return "0";
                case "星期二":
                    return "1";
                case "星期三":
                    return "2";
                case "星期四":
                    return "3";
                case "星期五":
                    return "4";
                case "星期六":
                    return "5";
                case "":
                    return "-1";
                default:
                    break;
            }
        }
        return "-1";
    }

    public static String getToDay(){
        String currentDay = getWeek();
        if(!TextUtils.isEmpty(currentDay)){
            switch (currentDay){
                case "星期日":
                    return "0";
                case "星期一":
                    return "1";
                case "星期二":
                    return "2";
                case "星期三":
                    return "3";
                case "星期四":
                    return "4";
                case "星期五":
                    return "5";
                case "星期六":
                    return "6";
                case "":
                    return "-1";
                default:
                    break;
            }
        }
        return "-1";
    }

    public static String getNextDay(){
        String currentDay = getWeek();
        if(!TextUtils.isEmpty(currentDay)){
            switch (currentDay){
                case "星期日":
                    return "1";
                case "星期一":
                    return "2";
                case "星期二":
                    return "3";
                case "星期三":
                    return "4";
                case "星期四":
                    return "5";
                case "星期五":
                    return "6";
                case "星期六":
                    return "0";
                case "":
                    return "-1";
                default:
                    break;
            }
        }
        return "-1";
    }

    public static String getWeek(){
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }
}

