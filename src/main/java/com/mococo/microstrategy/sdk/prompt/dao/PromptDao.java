package com.mococo.microstrategy.sdk.prompt.dao;

import java.util.List;

import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * 프롬프트 구성 항목 및 프롬프트 기본 속성을 반환하는 DAO Interface
 * 
 * @author hyoungilpark
 *
 */
public interface PromptDao {

    public int getPin();

    public String getMin();

    public String getMax();

    public boolean isSingle();

    public boolean isRequired();

    public String getDefaultAnswer();

    public List<PromptElement> getDefaultAnswers();

    public List<PromptElement> getSuggestedAnswers();

    public List<PromptElement> getSuggestedAnswers(int level, String selectedElemId);

    public String getControlType();

	int getPromptType();

	int getPromptSubType();

}
