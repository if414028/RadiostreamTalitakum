package com.radiostream.talitakum.activities;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

public class MyApplication extends Application {
    private static FirebaseAnalytics firebaseAnalytics;

    public void onCreate() {
        super.onCreate();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        OneSignal.initWithContext(this);
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
