package com.pape.timetodo.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private final static String DEFAULT_LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime stringToLocalDateTime(String target){

        return LocalDateTime.parse(target, DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_TIME_FORMAT));
    }   

    public static LocalDateTime stringToLocalDateTime(String target, String format){

        return LocalDateTime.parse(target, DateTimeFormatter.ofPattern(format));
    }

    public static String localDateTimeToString(LocalDateTime target){
        return target.format(DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_TIME_FORMAT));
    }

    public static String localDateTimeToString(LocalDateTime localDate, String format){
        return localDate.format(DateTimeFormatter.ofPattern(format));
    }

    public static String nowToString(){

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_TIME_FORMAT));
    }

    public static String nowToString(String format){

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }

}
