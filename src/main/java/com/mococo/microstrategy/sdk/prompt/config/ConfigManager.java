package com.mococo.microstrategy.sdk.prompt.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.mococo.microstrategy.sdk.prompt.prop.PropManager;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private Map<String, ConfigHandler> configHandlerMap = new HashMap<String, ConfigHandler>();

    private static ConfigManager configManager = new ConfigManager();

    public static ConfigManager getInstance() {
        return configManager;
    }

    /**
     * 싱글톤으로 구현되어 클래스 로딩 시 1회 수행된다. sdk 환경설정 파일에서 구현클래스명/파라미터를 조회하여 이를 이용하여 인스턴스틀
     * 생성하고, 초기화 메소스 initConfigObjectList를 호출한다.
     * 
     * sdk 환경설정 파일 중 configHandler의 구현클래스명 해당 클래스의 인스턴스를 생성할때 전달될 파라미터를 기재한다. { ...
     * "configHandler":{ "config1":{ "className":
     * "com.mocomsys.microstrategy.sdk.config.impl.JsonConfigHandler", "param":{
     * "filePath":"mstr-sdk-prompt-config.json" } }, "config2":{ "className":
     * "com.mocomsys.microstrategy.sdk.config.impl.JsonConfigHandler", "param":{
     * "filePath":"mstr-sdk-custom-config.json" } } } ... }
     */
    private ConfigManager() {
        BufferedReader reader = null;

        try {
            Map<String, Map<String, Object>> configHandlerConfig = PropManager
                    .<Map<String, Object>>getProp("configHandler");

            Set<String> handlerNameSet = configHandlerConfig.keySet();
            for (String handlerName : handlerNameSet) {
                Map<String, Object> map = configHandlerConfig.get(handlerName);

                String handlerClassName = (String) map.get("className");
                Map<String, Object> handlerParam = (Map<String, Object>) map.get("param");
                logger.debug("==> handler name: [{}]", handlerName);
                logger.debug("==> handler class name: [{}]", handlerClassName);
                logger.debug("==> handler param: [{}]", handlerParam);

                Class<?> clazz = ConfigManager.class.getClassLoader().loadClass(handlerClassName);
                ConfigHandler configHandler = (ConfigHandler) clazz.newInstance();
                configHandler.setParam(handlerParam);
                logger.debug("==> handler getParam: [{}]", configHandler.getParam());
                configHandler.initConfigObjectList();

                configHandlerMap.put(handlerName, configHandler);
            }
            logger.debug("==> configHandlerMap.keySet: [{}]", configHandlerMap.keySet());
        } catch (Exception e) {
            logger.error("!!! resource loading error", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("!!! resource close error", e);
            }
        }
    }

    public ObjectConfig getObjectConfig(String project, String objectId) {
        ObjectConfig config = null;

        for (String key : configHandlerMap.keySet()) {
            ConfigHandler handler = configHandlerMap.get(key);

            config = handler.getObjectConfig(project, objectId);
            if (config != null && config.keySet() != null) {
                break;
            }
        }

        return config;
    }

    public ObjectConfig getObjectConfig(WebObjectInfo info, WebIServerSession session) {
        ObjectConfig config = null;

        for (String key : configHandlerMap.keySet()) {
            ConfigHandler handler = configHandlerMap.get(key);

            config = handler.getObjectConfig(info, session);
            if (config != null && config.keySet() != null) {
                break;
            }
        }

        return config;
    }
}
