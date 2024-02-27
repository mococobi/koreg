package com.mococo.microstrategy.sdk.prompt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsPrompt;
import com.microstrategy.web.objects.WebSearch;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.dao.MstrPromptDao;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * MstrObjectsPromptApiDao
 * @author mococo
 *
 */
public class MstrObjectsPromptApiDao extends MstrPromptDao<WebObjectsPrompt> {

    /**
     * MSTR 오브젝트프롬프트의 API를 이용한 프롬프트 목록 DAO
     * @param prompt
     */
    public MstrObjectsPromptApiDao(final WebObjectsPrompt prompt) {
        super(prompt);
    }
    
    
    /**
     * getDefaultAnswers
     */
    @Override
    public List<PromptElement> getDefaultAnswers() {
    	final WebFolder folder = getPrompt().getDefaultAnswer();

        final List<PromptElement> elementList = new ArrayList<>();
        for (int i = 0; folder != null && i < folder.size(); i++) {
            final WebObjectInfo info = folder.get(i);
            elementList.add(new PromptElement(info.getID(), info.getDisplayName()));
        }

        return elementList;
    }
    
    
    /**
     * getSuggestedAnswers
     */
    @Override
    public List<PromptElement> getSuggestedAnswers() {
        WebFolder folder;
        final WebSearch search = getPrompt().getSearchRestriction();

        // 검색결과를 항목으로 표시할 경우
        if (search != null) {
            try {
                search.populate();
                search.setAsync(false);
                search.submit();
                folder = search.getResults();
            } catch (WebObjectsException e) {
                throw new SdkRuntimeException(e);
            }
            // 사전정의된 오브젝트 목록을 표시할 경우
        } else {
            folder = getPrompt().getSuggestedAnswers();
        }

        final List<PromptElement> elementList = new ArrayList<>();
        for (int i = 0; folder != null && i < folder.size(); i++) {
        	final WebObjectInfo info = folder.get(i);
            elementList.add(new PromptElement(info.getID(), info.getDisplayName()));
        }

        return elementList;
    }

}
