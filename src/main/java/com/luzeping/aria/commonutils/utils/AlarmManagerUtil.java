package com.luzeping.aria.commonutils.utils;

import android.app.AlarmManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AlarmManagerUtil {

    private static final String ACTION = "ALARM_MANAGER_UTIL_ACTION";

    private static final BroadcastReceiver recevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public static void init(Application application) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        application.registerReceiver(recevier, intentFilter);
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static void alarm(Context context, int requestCode, int millTime) {

    }

}
