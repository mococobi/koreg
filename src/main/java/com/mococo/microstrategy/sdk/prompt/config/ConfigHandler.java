package com.mococo.microstrategy.sdk.prompt.config;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;

/**
 * MSTR 객체의 확장 속성을 처리하기 위한 클래스
 * @author mococo
 *
 */
abstract public class ConfigHandler {

	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(ConfigHandler.class);
	
    /**
     * 각 객체별 확장 속성의 리스트
     */
    private List<Map<String, Object>> configObjectList;

    /**
     * 초기화 등 기능구현를 위해 설정리소스로부터 전달받을 파라미터
     */
    private Map<String, Object> param;
    
    
    /**
     * ConfigHandler
     */
    public ConfigHandler() {
    	logger.debug("ConfigHandler");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("ConfigHandler");
    }
    
    
    protected final void setParam(final Map<String, Object> param) {
        this.param = param;
    }
    
    
    protected final Map<String, Object> getParam() {
        return param;
    }

    /**
     * 초기화, configObjectList의 생성 등 수행
     */
    abstract protected void initConfigObjectList();
    
    
    protected final void setConfigObjectList(final List<Map<String, Object>> configObjectList) {
        this.configObjectList = configObjectList;
    }

    
    protected final List<Map<String, Object>> getConfigObjectList() {
    	return configObjectList;
    }

    
    /**
     * 개별 객체에 대한 확장 속성 조회
     * @param project
     * @param objectId
     * @return
     */
    abstract public ObjectConfig getObjectConfig(String project, String objectId);
    
    
    /**
     * 개별 객체에 대한 확장 속성 조회
     * @param object
     * @param session
     * @return
     */
    abstract public ObjectConfig getObjectConfig(WebObjectInfo object, WebIServerSession session);

}
