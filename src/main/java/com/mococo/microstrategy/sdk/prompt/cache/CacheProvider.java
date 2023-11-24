package com.mococo.microstrategy.sdk.prompt.cache;

/**
 * 
 * @author hyoungilpark
 *
 */
public interface CacheProvider {

    public void init();

    public <T1> void setCache(String itemId, T1 item);

    public <T2> T2 getCache(String itemId);

}
