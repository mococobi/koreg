package com.mococo.web.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static <T> String toJsonString(Collection<T> c)
            throws JsonGenerationException, JsonMappingException, IOException {
        final StringWriter writer = new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(writer, c);

        return writer.toString();
    }

}
