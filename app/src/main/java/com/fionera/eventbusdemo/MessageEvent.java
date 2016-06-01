package com.fionera.eventbusdemo;

/**
 * Created by fionera on 16-6-1.
 */

public class MessageEvent {
    private String param;

    public String getParam() {
        return param;
    }

    public MessageEvent setParam(String param) {
        this.param = param;
        return this;
    }
}
