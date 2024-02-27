package com.mococo.web.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JsonUtil
 * @author mococo
 *
 */
public class JsonUtil {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    
    /**
     * JsonUtil
     */
    public JsonUtil() {
    	logger.debug("JsonUtil");
    }
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("JsonUtil");
    }
	
	/**
	 * toJsonString
	 * @param <T>
	 * @param c
	 * @return
	 */
    public static <T> String toJsonString(final Collection<T> collect) throws JsonGenerationException, JsonMappingException, IOException {
        final StringWriter writer = new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(writer, collect);

        return writer.toString();
    }

}
