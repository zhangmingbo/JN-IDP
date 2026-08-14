package com.egoo.idp.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class TimeUtil {


    public static String getNowDateStr(){
        String promptDate = null;
        try {
            Date date = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日");
            promptDate = fmt.format(date);
        } catch (Exception e) {
            log.error("转换时间格式异常",e);
        }
        return promptDate;
    }

    public static String getDay() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return now.format(formatter);
    }

    public static String getDayAgo(int day) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return thirtyDaysAgo.format(formatter);
    }


    public static String getMmddhhmmss() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        return now.format(formatter);
    }
}
