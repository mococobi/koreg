package com.mococo.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Properties관리 class에서 초기화시 수행될 method의 전달 기능 수행 <br/>
 * <b>History</b><br/>
 * 
 * <pre>
 * 2012. 2. 24. 최초작성
 * </pre>
 * 
 * @author hipark
 * @version 1.0
 * @param <T>
 */
public class PropertiesHolder<T> {

    private transient Properties properties;
    final private transient T holder;
    final private transient String defaultValue;
    final private AbstractPropertiesLoader loader;

    /**
     * @param holder       호출 instance
     * @param mholder      초기화시 수행될 method 정보
     * @param DefaultValue Property 검색 실패시 반환값
     * @return
     * @throws IOException
     * @throws InvalidPropertiesFormatException
     */
    public PropertiesHolder(final T holder, final AbstractPropertiesLoader loader, final String DefaultValue) {
        this.holder = holder;
        this.loader = loader;
        this.defaultValue = DefaultValue;
        load();
    }

    public void load() {
        properties = loader.load();
    }

    public T getHolder() {
        return holder;
    }

    /**
     * .properties 형태로 저장된 property의 loading을 수행할 때 사용
     * 
     * @param fileName
     * @return
     * @throws IOException
     * @parem
     * @return
     */
    public static Properties load(final String fileName) {
        final InputStream stream = PropertiesHolder.class.getResourceAsStream(fileName);
        final Properties props = new Properties();
        try {
            props.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props;
    }

    /**
     * .xml 형태로 저장된 property의 loading을 수행할 때 사용
     * 
     * @param fileName
     * @return
     * @throws InvalidPropertiesFormatException
     * @throws IOException
     * @parem
     * @return
     */
    public static Properties loadFromXml(final String fileName) {
        final InputStream stream = PropertiesHolder.class.getResourceAsStream(fileName);
        final Properties props = new Properties();
        try {
            props.loadFromXML(stream);
        } catch (InvalidPropertiesFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props;
    }

    public String getString(final String pid) {
        String result = "";

        if (properties != null) {
            result = properties.getProperty(pid, defaultValue);
        }

        return result;
    }

    public Properties getProperties() {
        return properties;
    }

    public abstract static class AbstractPropertiesLoader {
        public abstract Properties load();
    }
}
