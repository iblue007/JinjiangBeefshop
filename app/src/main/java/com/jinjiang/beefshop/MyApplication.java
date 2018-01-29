package com.jinjiang.beefshop;

import android.app.Application;

/**
 * Created by HLSY-Android on 2017/2/25.
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static MyApplication getIntance(){
        return mInstance;
    }

}
