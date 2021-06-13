package com.kunai.chatappproject;

import android.app.Application;

import com.onesignal.OneSignal;

public class OneSignalDetail extends Application {
    private static final String ONESIGNAL_APP_ID = "e04d39b3-ad58-4ddd-ab42-891e0d840fa9";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
