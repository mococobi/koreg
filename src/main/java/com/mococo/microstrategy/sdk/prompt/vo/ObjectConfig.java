package com.mococo.microstrategy.sdk.prompt.vo;

import java.util.Map;
import java.util.Set;

/**
 * ObjectConfig
 * @author mococo
 *
 */
public final class ObjectConfig {
	
	/**
	 * config
	 */
    private final Map<String, Object> config;
    
    
    /**
     * ObjectConfig
     * @param config
     */
    public ObjectConfig(final Map<String, Object> config) {
        this.config = config;
    }
    
    
    /**
     * get
     * @param <P>
     * @param key
     * @return
     */
    public <P> P get(final String key) {
        return config != null ? (P) config.get(key) : null;
    }
    
    
    /**
     * keySet
     * @return
     */
    public Set<String> keySet() {
        return config != null ? config.keySet() : null;
    }
}
