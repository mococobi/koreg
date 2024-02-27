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
 * @author mococo
 */
public class SimpleKeyValuePromptDao extends CustomPromptDao<Map<String, Object>, List<Map<String, Object>>> {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(SimpleKeyValuePromptDao.class);
    
    
    /**
     * SimpleKeyValuePromptDao
     * @param param1
     * @param param2
     */
    public SimpleKeyValuePromptDao(final Map<String, Object> param1, final List<Map<String, Object>> param2) {
        super(param1, param2);
    }
    
    
    /**
     * getDefaultAnswers
     */
    @Override
    public List<PromptElement> getDefaultAnswers() {
    	final List<PromptElement> promptElementList = new ArrayList<>();

        final List<Map<String, Object>> paramList = getParam2();
        for (final Map<String, Object> param : paramList) {
            if (param.get("isDefault") != null && (Boolean) param.get("isDefault")) {
                promptElementList.add(new PromptElement((String) param.get("key"), (String) param.get("value")));
            }
        }
        logger.debug("==> promptElementList: [{}]", promptElementList);
        return promptElementList;
    }
    
    
    /**
     * getDefaultAnswer
     */
    @Override
    public String getDefaultAnswer() {
        return null;
    }
    
    
    /**
     * getSuggestedAnswers
     */
    @Override
    public List<PromptElement> getSuggestedAnswers() {
    	final List<PromptElement> promptElementList = new ArrayList<>();

        final List<Map<String, Object>> paramList = getParam2();
        for (final Map<String, Object> param : paramList) {
            promptElementList.add(new PromptElement((String) param.get("key"), (String) param.get("value")));
        }
        logger.debug("==> promptElementList: [{}]", promptElementList);
        return promptElementList;
    }
    
    
    /**
     * getSuggestedAnswers
     */
    @Override
    public List<PromptElement> getSuggestedAnswers(int level, String selectedElemId) {
//        return null;
        return new ArrayList<>();
    }
    
    
    /**
     * isSingle
     */
    @Override
    public boolean isSingle() {
    	Boolean rtnCheck;
    	
    	final Map<String, Object> param1 = getParam1();
    	final Boolean isSingle = (Boolean) param1.get("isSingle");
        
        if(isSingle == null) {
    		rtnCheck = true;
    	} else {
    		rtnCheck = isSingle;
    	}
    	
        return rtnCheck;
    }
    
    
    /**
     * isRequired
     */
    @Override
    public boolean isRequired() {
    	Boolean rtnCheck;
    	final Map<String, Object> param1 = getParam1();
    	final Boolean isRequired = (Boolean) param1.get("isRequired");
    	
    	if(isRequired == null) {
    		rtnCheck = false;
    	} else {
    		rtnCheck = isRequired;
    	}
    	
        return rtnCheck;
    }
    
    
    /**
     * getPin
     */
    @Override
    public int getPin() {
    	final Map<String, Object> param1 = getParam1();
    	final Integer pin = (Integer) param1.get("pin");
        return pin == null ? -1 : pin;
    }
    
    
    /**
     * getMin
     */
    @Override
    public String getMin() {
    	final Map<String, Object> param1 = getParam1();
        return (String) param1.get("min");
    }
    
    
    /**
     * getMax
     */
    @Override
    public String getMax() {
    	final Map<String, Object> param1 = getParam1();
        return (String) param1.get("max");
    }
}
