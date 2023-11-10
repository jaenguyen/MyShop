package com.ic.myshop.helper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ConversionHelper {

    public static String formatNumber(long number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number);
    }

    public static String formatDate(long timestamp) {
        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return sdf.format(date);
    }

    public static void main(String[] args) {
        System.out.println(formatDate(System.currentTimeMillis()));
    }
}
