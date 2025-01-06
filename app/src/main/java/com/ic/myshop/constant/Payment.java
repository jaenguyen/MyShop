package com.ic.myshop.constant;

public enum Payment {

    COD(0),
    IMMEDIATELY(1);
    private final int value;

    private Payment(int value) {
        this.value = value;
    }

    public static Payment get(int payment) {
        switch (payment) {
            case 0:
                return COD;
            case 1:
                return IMMEDIATELY;
        }
        return null;
    }

    public int valueOf() {
        return value;
    }

    public static String valueOf(int value) {
        switch (value) {
            case 0:
                return "Thanh toán khi nhận hàng";
            case 1:
                return "Thanh toán bằng ZaloPay";
        }
        return "UNKNOWN";
    }
}
