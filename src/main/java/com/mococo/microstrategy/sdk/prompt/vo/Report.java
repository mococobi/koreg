package com.mococo.microstrategy.sdk.prompt.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Report
 * @author mococo
 *
 */
public class Report {
	
	/**
	 * id
	 */
    private final String reportId;
    
    /**
     * type
     */
    private final int type;
    
    /**
     * title
     */
    private final String title;
    
    /**
     * promptList
     */
    private final List<Prompt> promptList = new ArrayList<>();
    
    
    /**
     * Report
     * @param id
     * @param type
     * @param title
     */
    public Report(final String promptId, final int type, final String title) {
        this.reportId = promptId;
        this.type = type;
        this.title = title;
    }
    
    
    /**
     * addPrompt
     * @param prompt
     */
    public void addPrompt(final Prompt prompt) {
        this.promptList.add(prompt);
    }
    
    
    /**
     * getId
     * @return
     */
    public String getId() {
        return reportId;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public List<Prompt> getPromptList() {
        return promptList;
    }

    @Override
    public String toString() {
        return new StringBuilder()
        		.append('{')
        		.append(" id: ").append(reportId)
        		.append(", type: ").append(type)
        		.append(", title: ").append(title)
        		.append(", promptList: ").append(promptList)
        		.append('}')
        		.toString();
    }

}
