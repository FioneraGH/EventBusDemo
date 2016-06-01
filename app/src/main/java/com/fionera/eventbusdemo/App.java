package com.fionera.eventbusdemo;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by fionera on 16-6-1.
 */

public class App extends Application {

    public static EventBus eventBus;

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus = EventBus.getDefault();
    }
}
