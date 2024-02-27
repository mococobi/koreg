package com.mococo.microstrategy.sdk.prompt.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.mococo.microstrategy.sdk.prompt.prop.PropManager;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;

/**
 * ConfigManager
 * @author mococo
 *
 */
public class ConfigManager {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    
    /**
     * configHandlerMap
     */
    private final Map<String, ConfigHandler> configHandlerMap = new ConcurrentHashMap<>();
    
    /**
     * cofManager
     */
    private static ConfigManager cofManager = new ConfigManager();
    
    
    /**
     * getInstance
     * @return
     */
    public static ConfigManager getInstance() {
        return cofManager;
    }
    
    /**
     * "configHandler":{ "config1":{ "className":
     * "com.mocomsys.microstrategy.sdk.config.impl.JsonConfigHandler", "param":{
     * "filePath":"mstr-sdk-prompt-config.json" } }, "config2":{ "className":
     * "com.mocomsys.microstrategy.sdk.config.impl.JsonConfigHandler", "param":{
     * "filePath":"mstr-sdk-custom-config.json" } } } ... }
     */
    
    /**
     * 싱글톤으로 구현되어 클래스 로딩 시 1회 수행된다. sdk 환경설정 파일에서 구현클래스명/파라미터를 조회하여 이를 이용하여 인스턴스틀
     * 생성하고, 초기화 메소스 initConfigObjectList를 호출한다.
     * sdk 환경설정 파일 중 configHandler의 구현클래스명 해당 클래스의 인스턴스를 생성할때 전달될 파라미터를 기재한다. { ...
     */
    public ConfigManager() {
//    	final BufferedReader reader = null;

        try {
        	final Map<String, Map<String, Object>> handlerConfig = PropManager.<Map<String, Object>>getProp("configHandler");

        	final Set<String> handlerNameSet = handlerConfig.keySet();
            for (final String handlerName : handlerNameSet) {
            	final Map<String, Object> map = handlerConfig.get(handlerName);

                final String handlerClassName = (String) map.get("className");
                final Map<String, Object> handlerParam = (Map<String, Object>) map.get("param");
//                logger.debug("==> handler param: [{}]", handlerParam);
                
                final String logTmp1 = handlerName.replaceAll("[\r\n]","");
                logger.debug("==> handler name: [{}]", logTmp1);
                
                final String logTmp2 = handlerClassName.replaceAll("[\r\n]","");
                logger.debug("==> handler class name: [{}]", logTmp2);
                
                final ClassLoader loader = Thread.currentThread().getContextClassLoader();
                final Class<?> clazz = loader.loadClass(handlerClassName);
                
                final ConfigHandler configHandler = (ConfigHandler) clazz.newInstance();
                configHandler.setParam(handlerParam);
                
//                logger.debug("==> handler getParam: [{}]", configHandler.getParam());
                configHandler.initConfigObjectList();

                configHandlerMap.put(handlerName, configHandler);
            }
            
//            logger.debug("==> configHandlerMap.keySet: [{}]", configHandlerMap.keySet());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("!!! resource loading error", e);
        }
    }
    
    
    /**
     * getObjectConfig
     * @param project
     * @param objectId
     * @return
     */
    public ObjectConfig getObjectConfig(final String project, final String objectId) {
        ObjectConfig config = null;

        for (final String key : configHandlerMap.keySet()) {
        	final ConfigHandler handler = configHandlerMap.get(key);

            config = handler.getObjectConfig(project, objectId);
            if (config != null && config.keySet() != null) {
                break;
            }
        }

        return config;
    }
    
    
    /**
     * getObjectConfig
     * @param info
     * @param session
     * @return
     */
    public ObjectConfig getObjectConfig(final WebObjectInfo info, final WebIServerSession session) {
        ObjectConfig config = null;

        for (final String key : configHandlerMap.keySet()) {
        	final ConfigHandler handler = configHandlerMap.get(key);

            config = handler.getObjectConfig(info, session);
            if (config != null && config.keySet() != null) {
                break;
            }
        }

        return config;
    }
}
