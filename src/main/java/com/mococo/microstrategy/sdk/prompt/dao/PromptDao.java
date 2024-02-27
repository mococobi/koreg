package com.mococo.microstrategy.sdk.prompt.dao;

import java.util.List;

import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * 프롬프트 구성 항목 및 프롬프트 기본 속성을 반환하는 DAO Interface
 * @author mococo
 *
 */
public interface PromptDao {
	
	/**
	 * getPin
	 * @return
	 */
    int getPin();
    
    
    /**
     * getMin
     * @return
     */
    String getMin();
    
    
    /**
     * getMax
     * @return
     */
    String getMax();
    
    
    /**
     * isSingle
     * @return
     */
    boolean isSingle();
    
    
    /**
     * isRequired
     * @return
     */
    boolean isRequired();
    
    
    /**
     * getDefaultAnswer
     * @return
     */
    String getDefaultAnswer();
    
    
    /**
     * getDefaultAnswers
     * @return
     */
    List<PromptElement> getDefaultAnswers();
    
    
    /**
     * getSuggestedAnswers
     * @return
     */
    List<PromptElement> getSuggestedAnswers();
    
    
    /**
     * getSuggestedAnswers
     * @param level
     * @param selectedElemId
     * @return
     */
    List<PromptElement> getSuggestedAnswers(int level, String selectedElemId);
    
    
    /**
     * getControlType
     * @return
     */
    String getControlType();
    
    
    /**
     * getPromptType
     * @return
     */
	int getPromptType();
	
	
	/**
	 * getPromptSubType
	 * @return
	 */
	int getPromptSubType();
	
	
	/**
	 * getMeaning
	 * @return
	 */
	String getMeaning();

}
