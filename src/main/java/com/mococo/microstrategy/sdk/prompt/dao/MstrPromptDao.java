package com.mococo.microstrategy.sdk.prompt.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebPrompt;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;

/**
 * MSTR 프롬프트 API를 이용한 DAO의 베이스 클래스
 * @author mococo
 */
public class MstrPromptDao<T extends WebPrompt> implements PromptDao {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(PromptDao.class);
	
	/**
	 * webPrompt
	 */
    private final T webPrompt;
    
    
    /**
     * MstrPromptDao
     * @param webPrompt
     */
    protected MstrPromptDao(final T webPrompt) {
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
    
    
    /**
     * getPrompt
     * @return
     */
    public T getPrompt() {
        return webPrompt;
    }
    
    
    /**
     * getPin
     */
    @Override
    public int getPin() {
        return webPrompt.getPIN();
    }
    
    
    /**
     * getMin
     */
    @Override
    public String getMin() {
        return webPrompt.getMin();
    }
    
    
    /**
     * getMax
     */
    @Override
    public String getMax() {
        return webPrompt.getMax();
    }
    
    
    /**
     * isSingle
     */
    @Override
    public boolean isSingle() {
        String property = null;

        try {
            property = webPrompt.getPropertySets().getItemByName("WebProperties").getItemByName("PSXSL").getValue().toLowerCase(Locale.KOREA);
        } catch (IllegalArgumentException | WebObjectsException e) {
        	logger.error("!!! isSingle Exception", e);
        }
        
        final Boolean rtnCheck1 = StringUtils.isNotEmpty(property);
        final Boolean rtnCheck2 = property.matches("promptelement_pulldown.xsl|promptelement_radio.xsl|promptelement_singleselect_listbox.xsl");

        return rtnCheck1 && rtnCheck2;
    }
    
    
    /**
     * isRequired
     */
    @Override
    public boolean isRequired() {
        return webPrompt.isRequired();
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
    public List<PromptElement> getSuggestedAnswers() {
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
     * getControlType
     */
    @Override
    public String getControlType() {
        String controlType = null;

        // 반환값 : Text box, Option button (radio button), CheckBox, List, Cart, Tree
        try {
            if (webPrompt.isEmbedded()) {
                controlType = webPrompt.getDisplayProperties().getByName("DisplayStyle").getValue();
            } else {
                controlType = webPrompt.getPropertySets().getItemByName("PromptLayoutProperties").getItemByName("DisplayStyle").getValue();
            }
        } catch (WebObjectsException e) {
        	logger.debug("!!! error", e);
        }

        return controlType;
    }
    
    
    /**
     * getPromptType
     */
    @Override
    public int getPromptType() {
        return webPrompt.getType();
    }
    
    
    /**
     * getPromptSubType
     */
    @Override
    public int getPromptSubType() {
        return webPrompt.getSubType();
    }
    
    
    /**
     * getMeaning
     */
	@Override
	public String getMeaning() {
		return webPrompt.getMeaning();
	}

}
