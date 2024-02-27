package com.mococo.microstrategy.sdk.prompt.config.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.mococo.microstrategy.sdk.prompt.config.ConfigHandler;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;

/**
 * JsonConfigHandler
 * @author mococo
 *
 */
public class JsonConfigHandler extends ConfigHandler {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(JsonConfigHandler.class);
	
	
    /**
     * JsonConfigHandler
     */
    public JsonConfigHandler() {
    	super();
    	logger.debug("JsonConfigHandler");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("JsonConfigHandler");
    }
	

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mocomsys.microstrategy.sdk.config.ConfigHandler#initConfigObjectList()
     */
    @Override
    public void initConfigObjectList() {
        // param의 filePath애트리뷰트에 지정된 경로의 json 파일을 읽어 각 객체별 확장 속성의 리스트로 변환한다.
        // json 파일은 List<Map<String, Object>>로 변환가능한 구조이다.
    	final Map<String, Object> param = getParam();
        final String filePath = (String) param.get("filePath");
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        
        try (
			InputStream inputStream = loader.getResourceAsStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        ){
            final List<Map<String, Object>> configObjectList = new ObjectMapper().readValue(reader,new TypeReference<>() {});
            setConfigObjectList(configObjectList);

//            logger.debug("==> configList: [{}]", getConfigObjectList());
        } catch (IOException e) {
        	logger.error("initConfigObjectList Exception", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mocomsys.microstrategy.sdk.config.ConfigHandler#getObjectConfig(java.lang
     * .String, java.lang.String)
     */
    @Override
    public ObjectConfig getObjectConfig(final String project, final String objectId) {
        Map<String, Object> match = null;

        final List<Map<String, Object>> list = getConfigObjectList();

        // project, objectId로 매칭되는 configObjectList의 엔트리를 조회하여 반환한다.
        if (list != null) {
            for (final Map<String, Object> map : list) {
                if (StringUtils.equals((String) map.get("objectId"), objectId)
                        && (StringUtils.equals("*", (String) map.get("project"))
                                || StringUtils.equals(project, (String) map.get("project")))) {
                    match = map;
                }
            }
        }

        return new ObjectConfig(match);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mocomsys.microstrategy.sdk.config.ConfigHandler#getObjectConfig(com.
     * microstrategy.web.objects.WebObjectInfo)
     */
    @Override
    public ObjectConfig getObjectConfig(final WebObjectInfo prompt, final WebIServerSession session) {
        String project = null;
        String objId = null;

        if (prompt != null) {
            project = prompt.getFactory().getIServerSession().getProjectName();
            objId = prompt.getID();
        }

        // prompt의 project, objectId로 매칭되는 configObjectList의 엔트리를 조회하여 반환한다.
        return getObjectConfig(project, objId);
    }

}
