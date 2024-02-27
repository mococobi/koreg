package com.mococo.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.mococo.biz.exception.BizException;

/**
 * Properties관리 class에서 초기화시 수행될 method의 전달 기능 수행
 * @author mococo
 *
 * @param <T>
 */
public class PropertiesHolder<T> {
	
	/**
	 * properties
	 */
    private transient Properties properties;
    
    /**
     * holder
     */
    final private transient T holder;
    
    /**
     * defaultValue
     */
    final private transient String defaultValue;
    
    /**
     * loader
     */
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
    
    
    /**
     * load
     */
    private void load() {
        properties = loader.load();
    }
    
    
    public T getHolder() {
        return holder;
    }
    
    
    /**
     * .properties 형태로 저장된 property의 loading을 수행할 때 사용
     * @param fileName
     * @return
     */
    public static Properties load(final String fileName) {
        final Properties props = new Properties();
        
        try (InputStream stream = PropertiesHolder.class.getResourceAsStream(fileName)) {
            props.load(stream);
        } catch (IOException e) {
            throw new BizException(e);
        }
        
        return props;
    }

    
    /**
     * .xml 형태로 저장된 property의 loading을 수행할 때 사용
     * @param fileName
     * @return
     */
    public static Properties loadFromXml(final String fileName) {
        final Properties props = new Properties();
        
        try (InputStream stream = PropertiesHolder.class.getResourceAsStream(fileName)) {
            props.loadFromXML(stream);
        } catch (IOException e) {
            throw new BizException(e);
        }
        return props;
    }
    
    
    /**
     * getString
     * @param pid
     * @return
     */
    public String getString(final String pid) {
        String result = "";

        if (properties != null) {
            result = properties.getProperty(pid, defaultValue);
        }

        return result;
    }
    
    
    /**
     * getProperties
     * @return
     */
    public Properties getProperties() {
        return properties;
    }
    
    
    /**
     * AbstractPropertiesLoader
     * @author mococo
     *
     */
    public abstract static class AbstractPropertiesLoader {
    	
    	/**
    	 * load
    	 * @return
    	 */
        public abstract Properties load();
    }
}
