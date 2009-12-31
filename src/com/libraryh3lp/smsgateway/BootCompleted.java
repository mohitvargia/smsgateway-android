package com.libraryh3lp.smsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleted extends BroadcastReceiver {
    private final static String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (! intent.getAction().equals(ACTION)) {
            return;
        }

        if (Settings.getStartup(context)) {
            Intent newIntent = new Intent(context, BOSHConnection.class);
            context.startService(newIntent);
        }
    }
}
