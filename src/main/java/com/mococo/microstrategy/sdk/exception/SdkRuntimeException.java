package com.mococo.microstrategy.sdk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SdkRuntimeException
 * @author mococo
 *
 */
public class SdkRuntimeException extends RuntimeException {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(SdkRuntimeException.class);
	
	/**
	 * serialVersionUID
	 */
    private static final long serialVersionUID = 6046148325588116328L;
    
    
    /**
     * SdkRuntimeException
     */
    public SdkRuntimeException() {
    	super();
    	logger.debug("SdkRuntimeException");
    }
    
    
    /**
     * SdkRuntimeException
     * @param var1
     */
    public SdkRuntimeException(final String var1) {
        super(var1);
    }
    
    
    /**
     * SdkRuntimeException
     * @param var1
     * @param var2
     */
    public SdkRuntimeException(final String var1, final Throwable var2) {
        super(var1, var2);
    }
    
    
    /**
     * SdkRuntimeException
     * @param var1
     */
    public SdkRuntimeException(final Throwable var1) {
        super(var1);
    }
}
