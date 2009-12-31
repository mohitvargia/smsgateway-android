package com.libraryh3lp.smsgateway;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class SMSGateway extends Activity implements OnClickListener, ServiceConnection {
    // public final static String TAG = "SMSGateway";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.stop_button).setOnClickListener(this);
        findViewById(R.id.about_button).setOnClickListener(this);
        
        status = (StatusTextView) findViewById(R.id.status);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start_button:
            //bindService(new Intent(this, BOSHConnection.class), this, Context.BIND_AUTO_CREATE);
            startService(new Intent(this, BOSHConnection.class));
            if (status != null) {
            	status.update(Status.CONNECTED);
            }
            break;
        case R.id.stop_button:
            //if (service != null) {
            //    unbindService(this);
            //}
            stopService(new Intent(this, BOSHConnection.class));
            if (status != null) {
            	status.update(Status.DISCONNECTED);
            }
            break;
        case R.id.about_button:
            startActivity(new Intent(this, About.class));
            break;
        }
    }

    @Override 
    public boolean onCreateOptionsMenu(Menu menu) { 
        super.onCreateOptionsMenu(menu); 
        getMenuInflater().inflate(R.menu.menu, menu); 
        return true; 
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem item) { 
        switch (item.getItemId()) { 
        case R.id.settings: 
            startActivity(new Intent(this, Settings.class)); 
            return true; 
        } 
        return false; 
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        //this.service = (BOSHConnection.LocalBinder) service;
    }

    public void onServiceDisconnected(ComponentName name) {
        //this.service = null;
    }

    //private BOSHConnection.LocalBinder service = null;
    private StatusTextView status = null;
}
