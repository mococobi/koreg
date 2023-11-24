package com.mococo.microstrategy.sdk.exception;

public class SdkRuntimeException extends RuntimeException {
    static final long serialVersionUID = 6046148325588116328L;

    public SdkRuntimeException() {
    }

    public SdkRuntimeException(String var1) {
        super(var1);
    }

    public SdkRuntimeException(String var1, Throwable var2) {
        super(var1, var2);
    }

    public SdkRuntimeException(Throwable var1) {
        super(var1);
    }
}
