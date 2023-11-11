package com.ic.myshop.helper;

import com.ic.myshop.model.Statistics;
import com.ic.myshop.output.StatisticsGroup;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ConversionHelper {

    public static String formatNumber(long number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number);
    }

    public static String formatDateTime(long timestamp) {
        Date date = new Date(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return dateFormat.format(date);
    }

    public static String formatDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return dateFormat.format(date);
    }

    public static List<StatisticsGroup> getStatisticsGroupList(List<Statistics> statisticsList) {
        List<StatisticsGroup> result = new ArrayList<>();
        Map<String, List<Statistics>> map = new LinkedHashMap<>();

        for (Statistics statistics : statisticsList) {
            String formattedDate = formatDate(statistics.getTimestamp());
            if (!map.containsKey(formattedDate)) {
                map.put(formattedDate, new ArrayList<>());
            }
            map.get(formattedDate).add(statistics);
        }
        for (String date : map.keySet()) {
            result.add(new StatisticsGroup(date, map.get(date)));
        }
        return result;
    }

    public static long monthToMillisecond(int month) {
        return month * (30L * 24L * 60L * 60L * 1000L);
    }
}
