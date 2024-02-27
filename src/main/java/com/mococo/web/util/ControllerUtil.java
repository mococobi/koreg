package com.mococo.web.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ControllerUtil
 * @author mococo
 *
 */
public class ControllerUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(FileUtil.class);
	
	
    /**
     * ControllerUtil
     */
    public ControllerUtil() {
    	logger.debug("FileUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("ControllerUtil");
    }
	
	
	/**
	 * getSuccessMap
	 * @return
	 */
    public static Map<String, Object> getSuccessMap() {
    	final Map<String, Object> map = new ConcurrentHashMap<>();
        // map.put("message", "정상처리되었습니다.");

    	final String errorCode = "success";
        map.put(PortalCodeUtil.errorCode, errorCode);
        map.put(PortalCodeUtil.errorMessage, SpringUtil.getMessage(errorCode));

        return map;
    }
    
    
    /**
     * getFailMap
     * @return
     */
    public static Map<String, Object> getFailMap() {
    	final Map<String, Object> map = new ConcurrentHashMap<>();
        // map.put("message", "처리 중 오류가 발생하였습니다.");

    	final String errorCode = "fail";
        map.put(PortalCodeUtil.errorCode, errorCode);
        map.put(PortalCodeUtil.errorMessage, SpringUtil.getMessage(errorCode));

        return map;
    }
    
    
    /**
     * getFailMap
     * @param errorCode
     * @return
     */
	public static Map<String, Object> getFailMap(final String errorCode) {
		final Map<String, Object> map = new ConcurrentHashMap<>();
		
		map.put(PortalCodeUtil.errorCode, errorCode);
		map.put(PortalCodeUtil.errorMessage, SpringUtil.getMessage(errorCode));
		
		return map;
	}
	
	
	/**
	 * getFailMapMessage
	 * @param errorMessage
	 * @return
	 */
	public static Map<String, Object> getFailMapMessage(final String errorMessage) {
		final Map<String, Object> map = new ConcurrentHashMap<>();
		
		final String errorCode = "fail";
		map.put(PortalCodeUtil.errorCode, errorCode);
		map.put(PortalCodeUtil.errorMessage, errorMessage);
		
		return map;
	}
}
