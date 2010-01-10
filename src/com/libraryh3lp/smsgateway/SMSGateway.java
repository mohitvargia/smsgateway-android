package com.libraryh3lp.smsgateway;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class SMSGateway extends Activity implements OnClickListener, ServiceConnection {
	private final BroadcastReceiver updater = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("gw", "receiving " + intent.getDataString());
			status.update(Integer.parseInt(intent.getData().getSchemeSpecificPart()));
		}
	};

	private void doRegister() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_VIEW);
		filter.addDataScheme("libraryh3lp");
		registerReceiver(updater, filter);
		status.update(Status.NOT_RUNNING);
        bindService(new Intent(this, BOSHConnection.class), this, 0);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(updater);
	}

	@Override
	public void onResume() {
		super.onResume();
		doRegister();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.stop_button).setOnClickListener(this);

        status = (StatusTextView) findViewById(R.id.status);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start_button:
            startService(new Intent(this, BOSHConnection.class));
            break;
        case R.id.stop_button:
            stopService(new Intent(this, BOSHConnection.class));
        	status.update(Status.DISCONNECTED);
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
        case R.id.password:
        	startActivity(new Intent(this, Password.class));
        }
        return false; 
    }

    public void onServiceConnected(ComponentName name, IBinder binder) {
        BOSHConnection.LocalBinder service = (BOSHConnection.LocalBinder) binder;
        status.update(service.getStatus());
        this.unbindService(this);
    }

    public void onServiceDisconnected(ComponentName name) {
    }

    private StatusTextView status = null;
}
