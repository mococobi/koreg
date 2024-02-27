package com.mococo.microstrategy.sdk.prompt.cache;

/**
 * CacheProvider
 * @author mococo
 *
 */
public interface CacheProvider {
	
	/**
	 * init
	 */
   void init();
    
    
    /**
     * setCache
     * @param <T1>
     * @param itemId
     * @param item
     */
    <A> void setCache(String itemId, A item);
    
    
    /**
     * getCache
     * @param <T2>
     * @param itemId
     * @return
     */
    <B> B getCache(String itemId);

}
