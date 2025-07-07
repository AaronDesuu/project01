package com.blemeterkai.meterkai.utils;

import android.os.Handler;
import android.os.Message;

import com.blemeterkai.meterkai.MainActivity;

import java.lang.ref.WeakReference;

    public class AppHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public AppHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
