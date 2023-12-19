package com.mococo.microstrategy.sdk.prompt.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebPrompt;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * MSTR 프롬프트 API를 이용한 DAO의 베이스 클래스
 * 
 * @author hyoungilpark
 *
 * @param <T>
 */
public class MstrPromptDao<T extends WebPrompt> implements PromptDao {
    private static final Logger logger = LoggerFactory.getLogger(PromptDao.class);

    private final T webPrompt;

    protected MstrPromptDao(T webPrompt) {
        if (webPrompt == null) {
            throw new SdkRuntimeException("Prompt is null.");
        }

        this.webPrompt = webPrompt;
        try {
            this.webPrompt.populate();
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }
    }

    public T getPrompt() {
        return webPrompt;
    }

    @Override
    public int getPin() {
        return webPrompt.getPIN();
    }

    @Override
    public String getMin() {
        return webPrompt.getMin();
    }

    @Override
    public String getMax() {
        return webPrompt.getMax();
    }

    @Override
    public boolean isSingle() {
        String property = null;

        try {
            property = webPrompt.getPropertySets().getItemByName("WebProperties").getItemByName("PSXSL").getValue()
                    .toLowerCase();
        } catch (IllegalArgumentException | WebObjectsException e) {
            logger.error("!!! get prompt[{}] property error", webPrompt, e);
        }

        return (StringUtils.isNotEmpty(property) && property
                .matches("promptelement_pulldown.xsl|promptelement_radio.xsl|promptelement_singleselect_listbox.xsl"));
    }

    @Override
    public boolean isRequired() {
        return webPrompt.isRequired();
    }

    @Override
    public String getDefaultAnswer() {
        return null;
    }

    @Override
    public List<PromptElement> getDefaultAnswers() {
        return null;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers() {
        return null;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers(int level, String selectedElemId) {
        return null;
    }

    @Override
    public String getControlType() {
        String controlType = null;

        // 반환값 : Text box, Option button (radio button), CheckBox, List, Cart, Tree
        try {
            if (webPrompt.isEmbedded()) {
                controlType = webPrompt.getDisplayProperties().getByName("DisplayStyle").getValue();
            } else {
                controlType = webPrompt.getPropertySets().getItemByName("PromptLayoutProperties")
                        .getItemByName("DisplayStyle").getValue();
            }
        } catch (WebObjectsException e) {
            logger.debug("!!! error", e);
        } catch (Exception e) {
            logger.debug("!!! error", e);
        }

        return controlType;
    }
    
    
    @Override
    public int getPromptType() {
        return webPrompt.getType();
    }
    
    @Override
    public int getPromptSubType() {
        return webPrompt.getSubType();
    }

}
