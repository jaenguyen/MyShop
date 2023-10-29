package com.ic.myshop.helper;

import java.text.DecimalFormat;

public class ConversionHelper {

    public static String formatNumber(long number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number);
    }
}
