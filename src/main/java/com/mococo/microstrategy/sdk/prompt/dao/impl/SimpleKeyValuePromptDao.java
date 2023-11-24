package com.mococo.microstrategy.sdk.prompt.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mococo.microstrategy.sdk.prompt.dao.CustomPromptDao;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * 프롬프트 DAO 중 단순 키,값 및 기본선택값 목록을 반환하는 DAO
 * 
 * 확장 속성 예시 ... { "project":"*", ... "objectId":"V111", ... "elementSource":{
 * "className":"com.mocomsys.microstrategy.sdk.dao.impl.SimpleKeyValuePromptDao",
 * "param1":{ "pin":1, "min":"5", "max":"10", "isSingle":true, "isRequired":true
 * }, "param2":[ {"key":"1", "value":"report 1", "isDefault": true}, {"key":"2",
 * "value":"report 2"}, {"key": "3", "value": "report 3"} ] }, ... }, ...
 * 
 * @author hyoungilpark
 *
 */
public class SimpleKeyValuePromptDao extends CustomPromptDao<Map<String, Object>, List<Map<String, Object>>> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleKeyValuePromptDao.class);

    public SimpleKeyValuePromptDao(Map<String, Object> param1, List<Map<String, Object>> param2) {
        super(param1, param2);
    }

    @Override
    public List<PromptElement> getDefaultAnswers() {
        List<PromptElement> promptElementList = new ArrayList<PromptElement>();

        List<Map<String, Object>> paramList = getParam2();
        for (Map<String, Object> param : paramList) {
            if (param.get("isDefault") != null && (Boolean) param.get("isDefault")) {
                promptElementList.add(new PromptElement((String) param.get("key"), (String) param.get("value")));
            }
        }
        logger.debug("==> promptElementList: [{}]", promptElementList);
        return promptElementList;
    }

    @Override
    public String getDefaultAnswer() {
        return null;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers() {
        List<PromptElement> promptElementList = new ArrayList<PromptElement>();

        List<Map<String, Object>> paramList = getParam2();
        for (Map<String, Object> param : paramList) {
            promptElementList.add(new PromptElement((String) param.get("key"), (String) param.get("value")));
        }
        logger.debug("==> promptElementList: [{}]", promptElementList);
        return promptElementList;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers(int level, String selectedElemId) {
        return null;
    }

    @Override
    public boolean isSingle() {
        Map<String, Object> param1 = getParam1();
        Boolean isSingle = (Boolean) param1.get("isSingle");
        return isSingle == null ? true : isSingle;
    }

    @Override
    public boolean isRequired() {
        Map<String, Object> param1 = getParam1();
        Boolean isRequired = (Boolean) param1.get("isRequired");
        return isRequired == null ? false : isRequired;
    }

    @Override
    public int getPin() {
        Map<String, Object> param1 = getParam1();
        Integer pin = (Integer) param1.get("pin");
        return pin == null ? -1 : pin;
    }

    @Override
    public String getMin() {
        Map<String, Object> param1 = getParam1();
        return (String) param1.get("min");
    }

    @Override
    public String getMax() {
        Map<String, Object> param1 = getParam1();
        return (String) param1.get("max");
    }
}
