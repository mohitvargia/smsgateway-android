package com.libraryh3lp.smsgateway;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class StatusTextView extends TextView implements Status {
    public LinkedList<Button> disabledOnDisconnect = new LinkedList<Button>();
    public LinkedList<Button> disabledOnConnect = new LinkedList<Button>();

    public StatusTextView(Context context) {
        this(context, null);
    }

    public StatusTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Build a text HashMap lookup table.
        // Note: need context to getText, and it's here in the constructor.
        texts.put(FAILED_AUTH,    (String) context.getText(R.string.status_failed_auth));
        texts.put(FAILED_CONNECT, (String) context.getText(R.string.status_failed_connect));
        texts.put(NOT_RUNNING,    (String) context.getText(R.string.status_not_running));
        texts.put(DISCONNECTED,   (String) context.getText(R.string.status_disconnected));
        texts.put(CONNECTING,     (String) context.getText(R.string.status_connecting));
        texts.put(AUTHORIZING,    (String) context.getText(R.string.status_authorizing));
        texts.put(CONNECTED,      (String) context.getText(R.string.status_connected));
        texts.put(RETRYING,       (String) context.getText(R.string.status_retrying));

        // Build a color HashMap lookup table.
        // Note: RED is left as an exercise using the isCritical method.
        colors.put(NOT_RUNNING,   Color.GRAY);
        colors.put(CONNECTING,    Color.YELLOW);
        colors.put(AUTHORIZING,   Color.YELLOW);
        colors.put(CONNECTED,     Color.GREEN);
        colors.put(RETRYING,      Color.YELLOW);
    }

    public boolean isCritical(int flag) {
        return flag <= DISCONNECTED;
    }

    public void update(int flag) {
        // Set default text color for critical flags.
        if (isCritical(flag)) {
            setTextColor(Color.RED);
            disconnected();
        }
        else {
            connecting();
        }

        // Set text.
        String text = texts.get(flag);
        if (text == null) {
            text = "";
        }
        setText(text);

        // Set text color.
        if (colors.containsKey(flag)) {
            setTextColor(colors.get(flag));
        }
    }

    private void disconnected() {
        swapButtons(disabledOnDisconnect, disabledOnConnect);
    }

    private void connecting() {
        swapButtons(disabledOnConnect, disabledOnDisconnect);
    }

    private void swapButtons(LinkedList<Button> disable, LinkedList<Button> enable) {
        for(Button button : disable) {
            button.setEnabled(false);
        }
        for(Button button : enable) {
            button.setEnabled(true);
        }
    }
    
    private HashMap<Integer, String>  texts  = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();
}
