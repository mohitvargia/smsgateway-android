package com.nubgames.smsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	private final static String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(! intent.getAction().equals(ACTION)) {
			return;
		}

		// Log.d(SMSGateway.TAG, "SMSReceiver.onReceive");
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
		    SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
		    Intent newIntent = new Intent(context, TextRouter.class);
		    newIntent.putExtra("from", msg.getDisplayOriginatingAddress());
		    newIntent.putExtra("message", msg.getDisplayMessageBody());
		    context.startService(newIntent);
		}
	}
}
