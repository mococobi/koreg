package com.mococo.microstrategy.sdk.prompt.vo;

import java.util.List;


/**
 * Prompt
 * @author mococo
 *
 */
public class Prompt {
	
	/**
	 * id
	 */
    private final String promptId;
    
    /**
     * title
     */
    private final String title;
    
    /**
     * type
     */
    private int type = -1;
    
    /**
     * pin
     */
    private int pin = -1;
    
    /**
     * min
     */
    private String min;
    
    /**
     * max
     */
    private String max;
    
    /**
     * defaultAnswer
     */
    private String defaultAnswer;
    
    /**
     * defaultAnswers
     */
    private List<PromptElement> defaultAnswers;
    
    /**
     * suggestedAnswers
     */
    private List<PromptElement> suggestedAnswers;
    
    /**
     * required
     */
    private boolean required;
    
    /**
     * controlType
     */
    private String controlType;
    
    /**
     * exUiType
     */
    private String exUiType;
    
    /**
     * exExtUiType
     */
    private Object exExtUiType;
    
    /**
     * exAction
     */
    private Object exAction;
    
    /**
     * exValidation
     */
    private Object exValidation;
    
    /**
     * daoClassName
     */
    private String daoClassName;
    
    /**
     * promptType
     */
    private int promptType = -1;
    
    /**
     * promptSubType
     */
	private int promptSubType = -1;
	
	/**
	 * meaning
	 */
	private String meaning;
	
	
	/**
	 * Prompt
	 * @param id
	 * @param title
	 */
	public Prompt(final String promptId, final String title) {
        this.promptId = promptId;
        this.title = title;
    }
	
	
	/**
	 * Prompt
	 * @param id
	 * @param title
	 * @param type
	 */
    public Prompt(final String promptId, final String title, final int type) {
        this.promptId = promptId;
        this.title = title;
        this.type = type;
    }
    
    
    /**
     * setExProp
     * @param exUiType
     * @param exExtUiType
     * @param exAction
     * @param exValidation
     */
    public void setExProp(final String exUiType, final Object exExtUiType, final Object exAction, final Object exValidation) {
        this.exUiType = exUiType;
        this.exExtUiType = exExtUiType;
        this.exAction = exAction;
        this.exValidation = exValidation;
    }
    
    
    /**
     * getId
     * @return
     */
    public String getId() {
        return promptId;
    }
    
    
    /**
     * getTitle
     * @return
     */
    public String getTitle() {
        return title;
    }
    
    
    /**
     * getType
     * @return
     */
    public int getType() {
        return type;
    }
    
    
    /**
     * setType
     * @param type
     */
    public void setType(final int type) {
        this.type = type;
    }
    
    
    /**
     * getPin
     * @return
     */
    public int getPin() {
        return pin;
    }
    
    
    /**
     * setPin
     * @param pin
     */
    public void setPin(final int pin) {
        this.pin = pin;
    }
    
    
    /**
     * getMin
     * @return
     */
    public String getMin() {
        return min;
    }
    
    
    /**
     * setMin
     * @param min
     */
    public void setMin(final String min) {
        this.min = min;
    }
    
    
    /**
     * getMax
     * @return
     */
    public String getMax() {
        return max;
    }
    
    
    /**
     * setMax
     * @param max
     */
    public void setMax(final String max) {
        this.max = max;
    }
    
    
    /**
     * getDefaultAnswer
     * @return
     */
    public String getDefaultAnswer() {
        return defaultAnswer;
    }
    
    
    /**
     * setDefaultAnswer
     * @param defaultAnswer
     */
    public void setDefaultAnswer(final String defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }
    
    
    /**
     * getDefaultAnswers
     * @return
     */
    public List<PromptElement> getDefaultAnswers() {
        return defaultAnswers;
    }
    
    
    /**
     * setDefaultAnswers
     * @param defaultAnswers
     */
    public void setDefaultAnswers(final List<PromptElement> defaultAnswers) {
        this.defaultAnswers = defaultAnswers;
    }
    
    
    /**
     * getSuggestedAnswers
     * @return
     */
    public List<PromptElement> getSuggestedAnswers() {
        return suggestedAnswers;
    }
    
    
    /**
     * setSuggestedAnswers
     * @param suggestedAnswers
     */
    public void setSuggestedAnswers(final List<PromptElement> suggestedAnswers) {
        this.suggestedAnswers = suggestedAnswers;
    }
    
    
    /**
     * isRequired
     * @return
     */
    public boolean isRequired() {
        return required;
    }
    
    
    /**
     * setRequired
     * @param required
     */
    public void setRequired(final boolean required) {
        this.required = required;
    }
    
    
    /**
     * getControlType
     * @return
     */
    public String getControlType() {
        return controlType;
    }
    
    
    /**
     * setControlType
     * @param controlType
     */
    public void setControlType(final String controlType) {
        this.controlType = controlType;
    }
    
    
    /**
     * getExUiType
     * @return
     */
    public String getExUiType() {
        return exUiType;
    }
    
    
    /**
     * setExUiType
     * @param exUiType
     */
    public void setExUiType(final String exUiType) {
        this.exUiType = exUiType;
    }
    
    
    /**
     * getExExtUiType
     * @return
     */
    public Object getExExtUiType() {
        return exExtUiType;
    }
    
    
    /**
     * setExExtUiType
     * @param exExtUiType
     */
    public void setExExtUiType(final Object exExtUiType) {
        this.exExtUiType = exExtUiType;
    }
    
    
    /**
     * getExAction
     * @return
     */
    public Object getExAction() {
        return exAction;
    }
    
    
    /**
     * setExAction
     * @param exAction
     */
    public void setExAction(final Object exAction) {
        this.exAction = exAction;
    }
    
    
    /**
     * getExValidation
     * @return
     */
    public Object getExValidation() {
        return exValidation;
    }
    
    
    /**
     * setExValidation
     * @param exValidation
     */
    public void setExValidation(final Object exValidation) {
        this.exValidation = exValidation;
    }
    
    
    /**
     * getDaoClassName
     * @return
     */
    public String getDaoClassName() {
        return daoClassName;
    }
    
    
    /**
     * setDaoClassName
     * @param daoClassName
     */
    public void setDaoClassName(final String daoClassName) {
        this.daoClassName = daoClassName;
    }
    
    
    /**
     * getPromptType
     * @return
     */
    public int getPromptType() {
		return promptType;
	}
    
    
    /**
     * setPromptType
     * @param promptType
     */
	public void setPromptType(final int promptType) {
		this.promptType = promptType;
	}
	
	
	/**
	 * getPromptSubType
	 * @return
	 */
	public int getPromptSubType() {
		return promptSubType;
	}
	
	
	/**
	 * setPromptSubType
	 * @param promptSubType
	 */
	public void setPromptSubType(final int promptSubType) {
		this.promptSubType = promptSubType;
	}
	
	
	/**
	 * getMeaning
	 * @return
	 */
    public String getMeaning() {
		return meaning;
	}
    
    
    /**
     * setMeaning
     * @param meaning
     */
	public void setMeaning(final String meaning) {
		this.meaning = meaning;
	}
	
	
	/**
	 * toString
	 */
    @Override
    public String toString() {
        return new StringBuilder()
        	.append('{')
			.append("  id: ").append(promptId)
			.append(", title: ").append(title)
			.append(", type: ").append(type)
			.append(", pin: ").append(pin)
			.append(", min: ").append(min)
			.append(", max: ").append(max)
	        .append(", defaultAnswer: ").append(defaultAnswer)
	        .append(", defaultAnswers: ").append(defaultAnswers)
	        .append(", suggestedAnswers: ").append(suggestedAnswers)
	        .append(", required: ").append(required)
	        .append(", controlType: ").append(controlType)
	        .append(", exUiType: ").append(exUiType)
	        .append(", exExtUiType: ").append(exExtUiType)
	        .append(", exAction: ").append(exAction)
	        .append(", exValidation: ").append(exValidation)
	        .append(", promptType: ").append(promptType)
	        .append(", promptSubType: ").append(promptSubType)
	        .append(", meaning: ").append(meaning)
	        .append('}').toString();
    }

}
