package com.libraryh3lp.smsgateway;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

import com.calclab.emite.core.client.EmiteCoreModule;
import com.calclab.emite.core.client.bosh.BoshSettings;
import com.calclab.emite.core.client.bosh.Connection;
import com.calclab.emite.core.client.packet.IPacket;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.InstantMessagingModule;
import com.calclab.emite.im.client.chat.Chat;
import com.calclab.emite.im.client.chat.ChatManager;
import com.calclab.suco.client.Suco;
import com.calclab.suco.client.events.Listener;

public class BOSHConnection extends Service {
    {
    	Suco.install(new EmiteCoreModule());
    	Suco.install(new InstantMessagingModule());
    }

    /** Public interface. */
    public class LocalBinder extends Binder {
        /** Enqueue an SMS message to send to the server. */
        public void enqueue(String from, String body) {
        	BOSHConnection.this.enqueue(from, body);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        // Keep the CPU from going to sleep.  OK to turn off the screen.
        wakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SMSGateway");
        wakelock.acquire();
    }

    @Override
    public void onDestroy() {
    	connection.disconnect();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        connect();
    }

    public BOSHConnection() {
    	Log.i("gw", "BoshConnection()");
    	initConnection();
    }

    private void initConnection() {
    	Log.i("gw", "initConnection()");
        session.onStateChanged(new Listener<Session.State>() {
            public void onEvent(Session.State state) {
                switch (state) {
                case notAuthorized:
                	Log.i("gw", "notAuthorized");
                	authFailed = true;
                    break;
                case loggedIn:
                	Log.i("gw", "loggedIn");
                	authFailed = false;
                	break;
                case ready:
                	Log.i("gw", "ready");
                    handleReady();
                    break;
                case disconnected:
                	Log.i("gw", "disconnected");
                	break;
                }
            }
        });

        chatManager.onChatCreated(new Listener<Chat>() {
            public void onEvent(Chat chat) {
                handleChatCreated(chat);
            }
        });

        connection.setSettings(new BoshSettings("http://10.0.2.2/http-bind/", "localhost", "1.6", 300, 1, 2));
        connection.onError(new Listener<String> () {
            public void onEvent(String msg) {
            	handleError();
            }
        });
    }

    /** Deal with errors using progressively-delayed reconnection attempts. */
    private void handleError() {
    	Log.i("gw", "handleError()");
    	// Don't continue trying to log in with bad credentials.
    	if (authFailed) {
    		Log.i("gw", "auth failed error");
    		return;
    	}

    	Log.i("gw", "schedling retry in " + delays[delay] + " seconds");
    	TimerTask task = new TimerTask() {
    		public void run() {
    			Log.i("gw", "retrying");
    			connect();
    		}
    	};
    	new Timer().schedule(task, delays[delay]*1000);
    	delay = Math.min(delays.length-1, delay+1);
    }

    private void handleChatCreated(Chat chat) {
    	Log.i("gw", "handleChatCreated()");
    	chat.onMessageReceived(new Listener<Message>() {
    		public void onEvent(Message message) {
    			handleMessage(message);
    		}
    	});
    }

    /** Handle an outgoing SMS message. */
    private void handleMessage(Message message) {
    	Log.i("gw", "handleMessage()");
    	try {
	    	IPacket body = message.getFirstChild("body");
	    	String  to   = body.getAttribute("to");
	    	String  msg  = body.getText();
	
	    	SmsManager manager = SmsManager.getDefault();
	    	manager.sendMultipartTextMessage(to, null, manager.divideMessage(msg), null, null);
    	} catch (Exception e) {
    	}
    }

    /** Send any queued messages. */
    private void handleReady() {
    	Log.i("gw", "handleReady()");
    	delay = 0;
    	Chat chat = chatManager.open(XmppURI.uri("android-sms.localhost"));

    	while (! incoming.isEmpty()) {
    		Log.i("gw", "running spool");
    		Message message = new Message(null, chat.getURI(), null);
    		IPacket body    = message.addChild("body", null);

    		QMsg msg = incoming.remove();
    		body.setAttribute("from", msg.from);
    		body.setAttribute("serial", ""+msg.serial);
    		body.setText(msg.body);

    		chat.send(message);
    	}
    }

    private void connect() {
    	Log.i("gw", "connect()");
    	if (connection.isConnected()) {
    		Log.i("gw", "already connected");
    		return;
    	}

    	String queue    = Settings.getQueueName(this);
    	String password = Settings.getPhoneID(this.getContentResolver());
    	if (queue == null || password == null || queue.length() == 0) {
    		Log.i("gw", "missing credentials");
    		authFailed = true;
    		return;
    	}
    	session.login(XmppURI.uri(queue, "localhost", "android"), password);
    }

    private void enqueue(String from, String body) {
        // Only enqueue messages from other phones, so that we can use email as a "wake-up".
        if (from.matches(".*\\d{9,}")) {
        	Log.i("gw", "spooling message");
            incoming.add(new QMsg(from, body));
        }

        if (session.isLoggedIn()) {
        	Log.i("gw", "sending messages");
        	handleReady();
        }
        if (! connection.isConnected()) {
        	connect();
        }
    }

    private static final int[] delays = new int[]{5, 10, 15, 30, 60, 120, 300, 900};
    private static       int   serial = 0;

    private final LocalBinder binder      = new LocalBinder();
    private final Connection  connection  = Suco.get(Connection.class);
    private final Session     session     = Suco.get(Session.class);
    private final ChatManager chatManager = Suco.get(ChatManager.class);

    private PowerManager.WakeLock wakelock;
    private int                   delay      = 0;
    private boolean               authFailed = false;

    private class QMsg {
        public QMsg(String from, String body) {
            this.serial = BOSHConnection.serial++;
            this.from   = from;
            this.body   = body;
        }

        public int    serial;
        public String from;
        public String body;
    };

    private Queue<QMsg> incoming = new LinkedList<QMsg>();
}
