package com.mococo.web.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ParamUtil
 * @author mococo
 *
 */
public class ParamUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(ParamUtil.class);
	
	
    /**
     * ParamUtil
     */
    public ParamUtil() {
    	logger.debug("ParamUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("ParamUtil");
    }
	
	
	/**
	 * getBigDecimal
	 * @param object
	 * @return
	 */
    public static final BigDecimal getBigDecimal(final Object object) {
        BigDecimal result = null;

        if (object != null) {
            if (object instanceof BigDecimal) {
                result = (BigDecimal) object;
            } else if (object instanceof String) {
                result = new BigDecimal((String) object);
            } else if (object instanceof BigInteger) {
                result = new BigDecimal((BigInteger) object);
            } else if (object instanceof Number) {
                result = new BigDecimal(object.toString());
            }
        }

        return result;
    }
}
