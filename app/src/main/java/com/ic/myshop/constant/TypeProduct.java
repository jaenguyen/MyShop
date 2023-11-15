package com.ic.myshop.constant;

public enum TypeProduct {

    ALL(0, "Tất cả"),
    FASHION(1, "Thời trang"),
    ELECTRONIC(2, "Điện tử"),
    HEALTHY(3, "Sức khỏe"),
    BEAUTY(4, "Sắc đẹp"),
    SPORT(5, "Thể thao, du lịch"),
    OTHERS(6, "Khác");

    private int code;
    private String name;
    private TypeProduct(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(TypeProduct typeProduct) {
        switch (typeProduct) {
            case ALL:
                return "Tất cả";
            case FASHION:
                return "Thời trang";
            case ELECTRONIC:
                return "Điện tử";
            case HEALTHY:
                return "Sức khỏe";
            case BEAUTY:
                return "Sắc đẹp";
            case SPORT:
                return "Thể thao, du lịch";
            case OTHERS:
                return "Khác";
        }
        return null;
    }

    public static TypeProduct getTypeProduct(int code) {
        switch (code) {
            case 0:
                return ALL;
            case 1:
                return FASHION;
            case 2:
                return ELECTRONIC;
            case 3:
                return HEALTHY;
            case 4:
                return BEAUTY;
            case 5:
                return SPORT;
            case 6:
                return OTHERS;
        }
        return null;
    }

    public static String getName(int code) {
        switch (code) {
            case 0:
                return "Tất cả";
            case 1:
                return "Thời trang";
            case 2:
                return "Điện tử";
            case 3:
                return "Sức khỏe";
            case 4:
                return "Sắc đẹp";
            case 5:
                return "Thể thao, du lịch";
            case 6:
                return "KHác";
        }
        return null;
    }
}
