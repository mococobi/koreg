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
 * 
 * @author hyoungilpark
 *
 */
public class DescConfigHandler extends ConfigHandler {
    private static final Logger logger = LoggerFactory.getLogger(DescConfigHandler.class);

    @Override
    public void initConfigObjectList() {
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
    public ObjectConfig getObjectConfig(WebObjectInfo object, WebIServerSession session) {
        // 파라미터 prompt의 주석 속성에서 확장 속성을 조회한다. 주석내용은 Map으로 변환 가능한 json 형식의 문자열이다.
        Map<String, Object> configInfo = null;
        if (object != null) {
            String json = object.getDescription();
            logger.debug("=> json:[{}]", json);
            if (StringUtils.isNotEmpty(json)) {
                try {
                    configInfo = new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {
                    });
                } catch (IOException e) {
                    logger.error("!!! json [{}] parsing error", json, e);
                }
            }
        }

        return new ObjectConfig(configInfo);
    }

}
