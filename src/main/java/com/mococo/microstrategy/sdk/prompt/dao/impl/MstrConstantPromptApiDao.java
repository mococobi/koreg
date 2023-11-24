package com.mococo.microstrategy.sdk.prompt.dao.impl;

import com.microstrategy.web.objects.WebConstantPrompt;
import com.mococo.microstrategy.sdk.prompt.dao.MstrPromptDao;

/**
 * MSTR 문자열프롬프트의 API를 이용한 프롬프트 목록 DAO
 * 
 * @author hyoungilpark
 *
 */
public class MstrConstantPromptApiDao extends MstrPromptDao<WebConstantPrompt> {

    public MstrConstantPromptApiDao(WebConstantPrompt prompt) {
        super(prompt);
    }

    @Override
    public String getDefaultAnswer() {
        return getPrompt().getDefaultAnswer();
    }

}
