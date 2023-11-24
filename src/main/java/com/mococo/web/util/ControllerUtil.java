package com.mococo.web.util;

import java.util.HashMap;
import java.util.Map;

public class ControllerUtil {

    public static Map<String, Object> getSuccessMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        // map.put("message", "정상처리되었습니다.");

        String errorCode = "success";
        map.put("errorCode", errorCode);
        map.put("errorMessage", SpringUtil.getMessage(errorCode));

        return map;
    }
    
    
    public static Map<String, Object> getFailMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        // map.put("message", "처리 중 오류가 발생하였습니다.");

        String errorCode = "fail";
        map.put("errorCode", errorCode);
        map.put("errorMessage", SpringUtil.getMessage(errorCode));

        return map;
    }
    
    
	public static Map<String, Object> getFailMap(String errorCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("errorCode", errorCode);
		map.put("errorMessage", SpringUtil.getMessage(errorCode));
		
		return map;
	}
	
	
	public static Map<String, Object> getFailMapMessage(String errorMessage) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String errorCode = "fail";
		map.put("errorCode", errorCode);
		map.put("errorMessage", errorMessage);
		
		return map;
	}
}
