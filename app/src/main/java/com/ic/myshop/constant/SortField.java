package com.ic.myshop.constant;

public enum SortField {


    NEWEST_ARRIVALS(0, "createdTime", 1),
    BEST_SELLERS(1, "soldNumber", 1),
    MOST_LIKES(2, "likes", 1),
    PRICE_LOW(3, "price", 0),
    PRICE_HIGH(4, "price", 1);

    private int code;
    private String field;
    private int sortType;

    private SortField(int code, String field, int sortType) {
        this.code = code;
        this.field = field;
        this.sortType = sortType;
    }

    public static String getField(SortField sortField) {
        return sortField.field;
    }

    public static int getSortType(SortField sortField) {
        return sortField.sortType;
    }

    public static int getCode(SortField sortField) {
        return sortField.code;
    }

    public static SortField getSortField(int code) {
        switch (code) {
            case 0:
                return NEWEST_ARRIVALS;
            case 1:
                return BEST_SELLERS;
            case 2:
                return MOST_LIKES;
            case 3:
                return PRICE_LOW;
            case 4:
                return PRICE_HIGH;
        }
        return NEWEST_ARRIVALS;
    }
}
