package com.mococo.microstrategy.sdk.prompt.dao;

import java.util.ArrayList;
import java.util.List;

import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * MSTR 프롬프트 API를 이용하지 않는 DAO의 베이스 클래스
 * @author mococo
 */
public class CustomPromptDao<A, B> implements PromptDao {
	

	/**
	 * param1
	 */
    private final A param1;
    
    /**
     * param2
     */
    private final B param2;
    
    
    /**
     * CustomPromptDao
     * @param param1
     * @param param2
     */
    public CustomPromptDao(final A param1, final B param2) {
        this.param1 = param1;
        this.param2 = param2;
    }
    
    
    protected A getParam1() {
        return param1;
    }
    
    
    protected B getParam2() {
        return param2;
    }
    
    
    /**
     * getPin
     */
    @Override
    public int getPin() {
        return -1;
    }
    
    
    /**
     * getMin
     */
    @Override
    public String getMin() {
        return null;
    }
    
    
    /**
     * getMax
     */
    @Override
    public String getMax() {
        return null;
    }
    
    
    /**
     * isSingle
     */
    @Override
    public boolean isSingle() {
        return true;
    }
    
    
    /**
     * isRequired
     */
    @Override
    public boolean isRequired() {
        return false;
    }
    
    
    /**
     * getDefaultAnswer
     */
    @Override
    public String getDefaultAnswer() {
        return null;
    }
    
    
    /**
     * getDefaultAnswers
     */
    @Override
    public List<PromptElement> getDefaultAnswers() {
    	return new ArrayList<>();
    }
    
    
    /**
     * getSuggestedAnswers
     */
    @Override
    public List<PromptElement> getSuggestedAnswers(int level, String selectedElemId) {
    	return new ArrayList<>();
    }
    
    
    /**
     * getSuggestedAnswers
     */
    @Override
    public List<PromptElement> getSuggestedAnswers() {
    	return new ArrayList<>();
    }
    
    
    /**
     * getControlType
     */
    @Override
    public String getControlType() {
        return null;
    }
    
    
    /**
     * getPromptType
     */
	@Override
	public int getPromptType() {
		return -1;
	}
	
	
	/**
	 * getPromptSubType
	 */
	@Override
	public int getPromptSubType() {
		return -1;
	}
	
	
	/**
	 * getMeaning
	 */
	@Override
	public String getMeaning() {
		return null;
	}

}
