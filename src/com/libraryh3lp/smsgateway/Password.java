package com.libraryh3lp.smsgateway;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Password extends Activity {
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.password); 
        
        TextView password = (TextView) findViewById(R.id.password_display);
        password.setText(Settings.getPhoneID(this.getContentResolver()));
    }
}
