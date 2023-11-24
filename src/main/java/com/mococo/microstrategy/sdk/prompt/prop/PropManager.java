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

public class PropManager {
    private static final Logger logger = LoggerFactory.getLogger(PropManager.class);
    /**
     * 루트 클래스패스에 포함된 SDK 환경설정 파일명
     */
    private static final String CONFIG_PATH = "mstr-sdk.json";

    /**
     * SDK 환경설정 파일명에서 JSON 문자열 조회, Map<String, Map<String, Object>>> 형태의 컬렉션에 보관
     * 
     * @return
     */
    private static final Map<String, Map<String, Object>> load() {
        BufferedReader reader = null;
        Map<String, Map<String, Object>> prop = null;

        try {
            InputStream inputStream = PropManager.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            prop = new ObjectMapper().readValue(reader, new TypeReference<Map<String, Map<String, Object>>>() {
            });
        } catch (Exception e) {
            logger.error("!!! resource [{}] loading error", CONFIG_PATH, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("!!! resource close error", e);
            }
        }

        return prop;
    }

    private static final Map<String, Map<String, Object>> propMap = load();

    public static <T> Map<String, T> getProp(String key) {
        return (Map<String, T>) propMap.get(key);
    }
}
