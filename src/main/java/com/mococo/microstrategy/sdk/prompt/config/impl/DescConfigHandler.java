package com.mococo.microstrategy.sdk.prompt.config.impl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microstrategy.utils.StringUtils;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.mococo.microstrategy.sdk.prompt.config.ConfigHandler;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;

/**
 * MSTR 객체의 긴주석을 이용한 확장 속성 처리
 * @author mococo
 *
 */
public class DescConfigHandler extends ConfigHandler {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(DescConfigHandler.class);
	
	
    /**
     * DescConfigHandler
     */
    public DescConfigHandler() {
    	super();
    	logger.debug("DescConfigHandler");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("DescConfigHandler");
    }
	
	
	/**
	 * initConfigObjectList
	 */
    @Override
    public void initConfigObjectList() {
    	logger.debug("initConfigObjectList");
    }
    
    
    /**
     * getObjectConfig
     */
    @Override
    public ObjectConfig getObjectConfig(String project, String objectId) {
        return null;
    }
    
    
    /**
     * getObjectConfig
     * @see com.mocomsys.microstrategy.sdk.config.ConfigHandler#getObjectConfig
     * (com.microstrategy.web.objects.WebObjectInfo)
     */
    @Override
    public ObjectConfig getObjectConfig(final WebObjectInfo object, final WebIServerSession session) {
        // 파라미터 prompt의 주석 속성에서 확장 속성을 조회한다. 주석내용은 Map으로 변환 가능한 json 형식의 문자열이다.
        Map<String, Object> configInfo = null;
        if (object != null) {
        	final String json = object.getDescription().replaceAll("[\r\n]","");
            logger.debug("=> json:[{}]", json);
            if (StringUtils.isNotEmpty(json)) {
                try {
                    configInfo = new ObjectMapper().readValue(json, new TypeReference<>() {
                    });
                } catch (IOException e) {
                	logger.error("!!! getObjectConfig IOException", e);
                }
            }
        }

        return new ObjectConfig(configInfo);
    }

}
