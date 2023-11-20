package com.ic.myshop.push;

import com.ic.myshop.constant.InputParam;

import java.util.HashMap;
import java.util.Map;

public class MessagePush {

    public static Map<String, String> getParams(String userId, String orderId, int status) {
        Map<String, String> params = new HashMap<>();
        params.put(InputParam.USER_ID, userId);
        params.put(InputParam.ORDER_ID, orderId);
        params.put(InputParam.STATUS, String.valueOf(status));
        return params;
    }
}
