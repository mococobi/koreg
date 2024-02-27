package com.mococo.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectsFactory;

/**
 * ParamUtil
 * @author mococo
 *
 */
public class DemeterUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(DemeterUtil.class);
	
	
    /**
     * DemeterUtil
     */
    public DemeterUtil() {
    	logger.debug("DemeterUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("DemeterUtil");
    }
	
	
	/**
	 * getDemeter
	 * @param value1
	 * @return
	 */
    public static HttpServletRequest getDemeter(final ServletRequestAttributes value1) {
    	return value1.getRequest();
    }
    

    /**
     * getDemeter
     * @param value1
     * @return
     */
    public static WebObjectsFactory getDemeter(final WebIServerSession value1) {
    	return value1.getFactory();
    }
    
    
    /**
     * getDemeter
     * @param prompt
     * @return
     */
    public static WebObjectsFactory getDemeter(final WebObjectInfo value1) {
    	return value1.getFactory();
    }
    
    
    /**
     * 
     * @param value1
     * @param value2
     * @return
     */
    public static Object getDemeter2(final HttpSession value1, final String value2) {
    	return value1.getAttribute(value2);
    }
}
