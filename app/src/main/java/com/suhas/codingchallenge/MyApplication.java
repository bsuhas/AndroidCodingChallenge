package com.suhas.codingchallenge;

import android.app.Application;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.signed.Signature;
import com.cloudinary.android.signed.SignatureProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SUHAS on 14/04/2019.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Map config = new HashMap();
        config.put("cloud_name", "bsuhascloud");
        MediaManager.init(this, config);

    }
}
