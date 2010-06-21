package com.nubgames.smsgateway;

/**
 * An interface for stateful transitions.
 */
public interface Status {
    public static final int FAILED_AUTH    = -3;
    public static final int FAILED_CONNECT = -2;
    public static final int NOT_RUNNING    =  0;
    public static final int DISCONNECTED   =  1;
    public static final int CONNECTING     =  2;
    public static final int AUTHORIZING    =  3;
    public static final int CONNECTED      =  4;
    public static final int RETRYING       =  5;

    public void update(int flag);
    public boolean isCritical(int flag);
}
