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
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;
import com.mococo.microstrategy.sdk.prompt.config.ConfigHandler;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;

/**
 * MSTR 객체의 긴주석을 이용한 확장 속성 처리
 * @author mococo
 *
 */
public class LongDescConfigHandler extends ConfigHandler {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(LongDescConfigHandler.class);
    
    
    /**
     * LongDescConfigHandler
     */
    public LongDescConfigHandler() {
    	super();
    	logger.debug("LongDescConfigHandler");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("LongDescConfigHandler");
    }
    
    
    @Override
    public void initConfigObjectList() {
    	logger.debug("initConfigObjectList");
    }
    
    
    @Override
    public ObjectConfig getObjectConfig(String project, String objectId) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mocomsys.microstrategy.sdk.config.ConfigHandler#getObjectConfig(com.
     * microstrategy.web.objects.WebObjectInfo)
     */
    @Override
    public ObjectConfig getObjectConfig(final WebObjectInfo object, final WebIServerSession session) {
    	final WebObjectSource source = session.getFactory().getObjectSource();
        source.setFlags(source.getFlags() | EnumDSSXMLObjectFlags.DssXmlObjectComments);

        // 파라미터 prompt의 긴주석 속성에서 확장 속성을 조회한다. 주석내용은 Map으로 변환 가능한 json 형식의 문자열이다.
        Map<String, Object> configInfo = null;
        if (object != null) {
            try {
            	final WebObjectInfo info = source.getObject(object.getID(), object.getType(), true);

                if (info.getComments() != null && info.getComments().length > 0) {
                	final String json = info.getComments()[0];
                	final String logTmp1 = json.replaceAll("[\r\n]","");
                    logger.debug("=> comment:[{}]", logTmp1);

                    if (StringUtils.isNotEmpty(json)) {
                        configInfo = new ObjectMapper().readValue(json, new TypeReference<>() {});
                    }
                }
            } catch (WebObjectsException | IllegalArgumentException e) {
                logger.error("!!! error", e);
            } catch (IOException e) {
                logger.error("!!! json [{}] parsing error", e);
            }

        }

        return new ObjectConfig(configInfo);
    }

}
