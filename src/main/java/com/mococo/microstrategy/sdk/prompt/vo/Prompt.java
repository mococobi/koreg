package com.mococo.microstrategy.sdk.prompt.vo;

import java.util.List;

public class Prompt {
    // private static final Logger logger = LoggerFactory.getLogger(Prompt.class);

    private String id = null;
    private String title = null;
    private int type = -1;
    private int pin = -1;
    private String min = null;
    private String max = null;
    private String defaultAnswer = null;
    private List<PromptElement> defaultAnswers = null;
    private List<PromptElement> suggestedAnswers = null;
    private boolean required = false;
    private String controlType = null;

    private String exUiType = null;
    private Object exExtUiType = null;
    private Object exAction = null;
    private Object exValidation = null;

    private String daoClassName = null;
    
    private int promptType = -1;
	private int promptSubType = -1;
	
	private String meaning = null;

	public Prompt(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Prompt(String id, String title, int type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public void setExProp(String exUiType, Object exExtUiType, Object exAction, Object exValidation) {
        this.exUiType = exUiType;
        this.exExtUiType = exExtUiType;
        this.exAction = exAction;
        this.exValidation = exValidation;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getDefaultAnswer() {
        return defaultAnswer;
    }

    public void setDefaultAnswer(String defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }

    public List<PromptElement> getDefaultAnswers() {
        return defaultAnswers;
    }

    public void setDefaultAnswers(List<PromptElement> defaultAnswers) {
        this.defaultAnswers = defaultAnswers;
    }

    public List<PromptElement> getSuggestedAnswers() {
        return suggestedAnswers;
    }

    public void setSuggestedAnswers(List<PromptElement> suggestedAnswers) {
        this.suggestedAnswers = suggestedAnswers;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getExUiType() {
        return exUiType;
    }

    public void setExUiType(String exUiType) {
        this.exUiType = exUiType;
    }

    public Object getExExtUiType() {
        return exExtUiType;
    }

    public void setExExtUiType(Object exExtUiType) {
        this.exExtUiType = exExtUiType;
    }

    public Object getExAction() {
        return exAction;
    }

    public void setExAction(Object exAction) {
        this.exAction = exAction;
    }

    public Object getExValidation() {
        return exValidation;
    }

    public void setExValidation(Object exValidation) {
        this.exValidation = exValidation;
    }

    public String getDaoClassName() {
        return daoClassName;
    }

    public void setDaoClassName(String daoClassName) {
        this.daoClassName = daoClassName;
    }
    
    public int getPromptType() {
		return promptType;
	}

	public void setPromptType(int promptType) {
		this.promptType = promptType;
	}

	public int getPromptSubType() {
		return promptSubType;
	}

	public void setPromptSubType(int promptSubType) {
		this.promptSubType = promptSubType;
	}
	
    public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

    @Override
    public String toString() {
        return new StringBuilder()
        	.append("{")
			.append("  id: ").append(id)
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
	        .append("}").toString();
    }

}
