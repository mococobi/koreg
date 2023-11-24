package com.mococo.microstrategy.sdk.prompt.cache;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.cache.CacheManager.CacheObjectType;
import com.mococo.microstrategy.sdk.prompt.prop.PropManager;

public class CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(PropManager.class);

    private static final CacheProvider load() {
        Map<String, String> cacheProviderConfig = PropManager.<String>getProp("cacheProvider");

        CacheProvider instance = null;
        if (cacheProviderConfig != null) {
            String className = cacheProviderConfig.get("className");
            Class<?> clazz = null;
            try {
                clazz = CacheManager.class.getClassLoader().loadClass(className);
                instance = (CacheProvider) clazz.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("!!! CacheProvider [{}] create instance error", className, e);
                throw new SdkRuntimeException("create instance error");
            }
        }

        return instance;
    }

    private static final CacheProvider provider = load();

    public static <T1> void setCache(String itemId, T1 item) {
        if (provider != null) {
            provider.<T1>setCache(itemId, item);
        }
    }

    public static <T2> T2 getCache(String itemId) {
        if (provider != null) {
            return provider.<T2>getCache(itemId);
        } else {
            return null;
        }
    }

    public static enum CacheObjectType {
        REQUEST_HANDLER, PROMPT
    }

    public static final String getCacheItemId(CacheObjectType objectType, String... args) {
        return objectType + "." + StringUtils.join(args, ".");
    }

}
