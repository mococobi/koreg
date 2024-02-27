package com.mococo.microstrategy.sdk.prompt.cache;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.prop.PropManager;

/**
 * CacheManager
 * @author mococo
 *
 */
public class CacheManager {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);
    
    /**
     * provider
     */
    private static final CacheProvider provider = load();
    
    
    /**
     * CacheManager
     */
    public CacheManager() {
    	logger.debug("CacheManager");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("CacheManager");
    }
	
    
    /**
     * load
     * @return
     */
	public static CacheProvider load() {
    	final Map<String, String> cacheConfig = PropManager.<String>getProp("cacheProvider");
        CacheProvider instance = null;
        
        if (cacheConfig != null) {
        	final String className = cacheConfig.get("className");
            Class<?> clazz;
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            
            try {
                clazz = loader.loadClass(className);
                instance = (CacheProvider) clazz.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            	logger.error("!!! load Exception", e);
                throw new SdkRuntimeException(e);
            }
        }

        return instance;
    }
    
    
    /**
     * setCache
     * @param <T1>
     * @param itemId
     * @param item
     */
    public static <A> void setCache(final String itemId, final A item) {
        if (provider != null) {
            provider.<A>setCache(itemId, item);
        }
    }
    
    
    /**
     * getCache
     * @param <T2>
     * @param itemId
     * @return
     */
    public static <B> B getCache(final String itemId) {
    	Object mksong = null;
    	
        if (provider != null) {
        	mksong = provider.<B>getCache(itemId);
        }
        
		return (B) mksong;
    }
    
    
    /**
     * CacheObjectType
     * @author mococo
     *
     */
    public enum CacheObjectType {
        REQUEST_HANDLER, PROMPT
    }
    
    
    /**
     * getCacheItemId
     * @param objectType
     * @param args
     * @return
     */
    public static final String getCacheItemId(final CacheObjectType objectType, final String... args) {
        return objectType + "." + StringUtils.join(args, ".");
    }

}
