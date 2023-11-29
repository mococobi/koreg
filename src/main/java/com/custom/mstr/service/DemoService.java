package com.custom.mstr.service;

import java.util.List;
import java.util.Map;

public interface DemoService {
    List<Map<String, Object>> list(Map<String, Object> param);

    List<Map<String, Object>> select(Map<String, Object> param);

    int dummyInsert(Map<String, Object> param);

    void errorInMultiInsert(List<Map<String, Object>> param);

    void dummyBatchUpdate(List<Map<String, Object>> param);
}
