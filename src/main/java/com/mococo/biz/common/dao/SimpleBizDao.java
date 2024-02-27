package com.mococo.biz.common.dao;

import java.util.List;
import java.util.Map;

/**
 * SimpleBizDao
 * @author mococo
 *
 */
public interface SimpleBizDao {
	
	/**
	 * list
	 * @param mapperId
	 * @param param
	 * @return
	 */
    List<Map<String, Object>> list(String mapperId, Map<String, Object> param);
    
    /**
     * list
     * @param mapperId
     * @param param
     * @return
     */
    List<Map<String, Object>> list(String mapperId, String param);
    
    /**
     * select
     * @param mapperId
     * @param param
     * @return
     */
    Map<String, Object> select(String mapperId, Map<String, Object> param);
    
    /**
     * select
     * @param mapperId
     * @param param
     * @return
     */
    Map<String, Object> select(String mapperId, String param);
    
    /**
     * newSeq
     * @param mapperId
     * @return
     */
    String newSeq(String mapperId);
    
    /**
     * insert
     * @param mapperId
     * @param param
     * @return
     */
    int insert(String mapperId, Map<String, Object> param);
    
    /**
     * update
     * @param mapperId
     * @param param
     * @return
     */
    int update(String mapperId, Map<String, Object> param);
    
    /**
     * delete
     * @param mapperId
     * @param param
     * @return
     */
    int delete(String mapperId, Map<String, Object> param);
}
