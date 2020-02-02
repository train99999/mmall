package com.quan.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.util.Date;

public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // 字符串转日期
    public static Date strToDate(String dateTimeStr,String formatStr){
        //定义日期格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        System.out.println("dateTime="+dateTime);
        return dateTime.toDate();
    }

    // 日期转字符串
    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime  = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    // 字符串转日期
    public static Date strToDate(String dateTimeStr){
        //定义日期格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    // 日期转字符串
    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime  = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
