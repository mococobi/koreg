package com.mococo.microstrategy.sdk.prompt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.microstrategy.web.objects.WebElement;
import com.microstrategy.web.objects.WebElements;
import com.microstrategy.web.objects.WebElementsPrompt;
import com.microstrategy.web.objects.WebObjectsException;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.dao.MstrPromptDao;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * MSTR 엘리먼트프롬프트의 API를 이용한 프롬프트 목록 DAO
 * 
 * @author hyoungilpark
 *
 */
public class MstrElementsPromptApiDao extends MstrPromptDao<WebElementsPrompt> {

    public MstrElementsPromptApiDao(WebElementsPrompt prompt) {
        super(prompt);
    }

    @Override
    public List<PromptElement> getDefaultAnswers() {
        List<PromptElement> elementList = new ArrayList<PromptElement>();
        WebElements elements = getPrompt().getDefaultAnswer();
        for (int i = 0; elements != null && i < elements.size(); i++) {
            WebElement element = elements.get(i);
            elementList.add(new PromptElement(element.getID(), element.getDisplayName()));
        }

        return elementList;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers() {
        List<PromptElement> elementList = new ArrayList<PromptElement>();

        WebElements elements = getPrompt().getSuggestedAnswers();

        if (elements == null || elements.size() == 0) {
            try {
                elements = getPrompt().getOrigin().getElementSource().getElements();
            } catch (WebObjectsException e) {
                throw new SdkRuntimeException(e);
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            WebElement element = elements.get(i);
            elementList.add(new PromptElement(element.getID(), element.getDisplayName()));
        }

        return elementList;
    }

}
