package com.nubgames.smsgateway;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class TextRouter extends Service implements ServiceConnection {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Bundle bundle = intent.getExtras();
        from    = bundle.getString("from");
        message = bundle.getString("message");
        bindService(new Intent(this, BOSHConnection.class), this, Context.BIND_AUTO_CREATE);
    }

    public void onServiceConnected(ComponentName name, IBinder binder) {
        BOSHConnection.LocalBinder service = (BOSHConnection.LocalBinder) binder;
        service.enqueue(from, message);
        this.unbindService(this);
   }

    public void onServiceDisconnected(ComponentName name) {
        stopSelf();
    }

    private String from;
    private String message;
}
