package com.mococo.biz.common.dao;

import java.util.List;
import java.util.Map;

public interface SimpleBizDao {
    List<Map<String, Object>> list(String mapperId, Map<String, Object> param);

    List<Map<String, Object>> list(String mapperId, String param);

    Map<String, Object> select(String mapperId, Map<String, Object> param);

    Map<String, Object> select(String mapperId, String param);

    String newSeq(String mapperId);

    int insert(String mapperId, Map<String, Object> param);

    int update(String mapperId, Map<String, Object> param);

    int delete(String mapperId, Map<String, Object> param);
}
