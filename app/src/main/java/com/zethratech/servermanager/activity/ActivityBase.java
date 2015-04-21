package com.zethratech.servermanager.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.zethratech.servermanager.logger.Log;
import com.zethratech.servermanager.logger.LogWrapper;

/**
 * Created by bgoldberg on 4/21/2015.
 */
public class ActivityBase extends FragmentActivity {

    public static final String TAG = "ActivityBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onStart() {
        super.onStart();
        initializeLogging();
    }

    /** Set up targets to receive log data */
    public void initializeLogging() {
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        Log.i(TAG, "Ready");
    }
}
