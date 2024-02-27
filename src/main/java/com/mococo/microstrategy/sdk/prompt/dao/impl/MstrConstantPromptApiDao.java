package com.mococo.microstrategy.sdk.prompt.dao.impl;

import com.microstrategy.web.objects.WebConstantPrompt;
import com.mococo.microstrategy.sdk.prompt.dao.MstrPromptDao;

/**
 * MSTR 문자열프롬프트의 API를 이용한 프롬프트 목록 DAO
 * @author mococo
 *
 */
public class MstrConstantPromptApiDao extends MstrPromptDao<WebConstantPrompt> {
	
	/**
	 * MstrConstantPromptApiDao
	 * @param prompt
	 */
    public MstrConstantPromptApiDao(final WebConstantPrompt prompt) {
        super(prompt);
    }
    
    
    /**
     * getDefaultAnswer
     */
    @Override
    public String getDefaultAnswer() {
        return getPrompt().getDefaultAnswer();
    }

}
