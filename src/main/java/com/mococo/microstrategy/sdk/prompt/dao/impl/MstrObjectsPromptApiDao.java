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

public class MstrObjectsPromptApiDao extends MstrPromptDao<WebObjectsPrompt> {

    /**
     * MSTR 오브젝트프롬프트의 API를 이용한 프롬프트 목록 DAO
     * 
     * @param prompt
     * @throws WebObjectsException
     */
    public MstrObjectsPromptApiDao(WebObjectsPrompt prompt) {
        super(prompt);
    }

    @Override
    public List<PromptElement> getDefaultAnswers() {
        WebFolder folder = getPrompt().getDefaultAnswer();

        List<PromptElement> elementList = new ArrayList<PromptElement>();
        for (int i = 0; folder != null && i < folder.size(); i++) {
            WebObjectInfo info = folder.get(i);
            elementList.add(new PromptElement(info.getID(), info.getDisplayName()));
        }

        return elementList;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers() {
        WebFolder folder = null;
        WebSearch search = getPrompt().getSearchRestriction();

        // 검색결과를 항목으로 표시할 경우
        if (search != null) {
            try {
                search.populate();
                search.setAsync(false);
                search.submit();
                folder = search.getResults();
            } catch (WebObjectsException e) {
                throw new SdkRuntimeException("Search fail");
            }
            // 사전정의된 오브젝트 목록을 표시할 경우
        } else {
            folder = getPrompt().getSuggestedAnswers();
        }

        List<PromptElement> elementList = new ArrayList<PromptElement>();
        for (int i = 0; folder != null && i < folder.size(); i++) {
            WebObjectInfo info = folder.get(i);
            elementList.add(new PromptElement(info.getID(), info.getDisplayName()));
        }

        return elementList;
    }

}
