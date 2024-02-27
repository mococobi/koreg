package com.mococo.microstrategy.sdk.prompt.prop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PropManager
 * @author mococo
 *
 */
public class PropManager {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(PropManager.class);
	
    /**
     * 루트 클래스패스에 포함된 SDK 환경설정 파일명
     */
    private static final String CONFIG_PATH = "mstr-sdk.json";
    
    /**
     * propMap
     */
    private static final Map<String, Map<String, Object>> propMap = load();
    
    
    /**
     * PropManager
     */
    public PropManager() {
    	logger.debug("PropManager");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("PropManager");
    }
	
    
    /**
     * SDK 환경설정 파일명에서 JSON 문자열 조회, Map<String, Map<String, Object>>> 형태의 컬렉션에 보관
     * @return
     */
	/* default */ final static Map<String, Map<String, Object>> load() {
        Map<String, Map<String, Object>> prop = null;
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        
        try (
    		InputStream inputStream = loader.getResourceAsStream(CONFIG_PATH);
    		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        ){
            prop = new ObjectMapper().readValue(reader, new TypeReference<>() {});
        } catch (IOException e) {
        	logger.error("load Exception", e);
        }

        return prop;
    }
    
    
    /**
     * getProp
     * @param <T>
     * @param key
     * @return
     */
    public static <T> Map<String, T> getProp(final String key) {
        return (Map<String, T>) propMap.get(key);
    }
}
