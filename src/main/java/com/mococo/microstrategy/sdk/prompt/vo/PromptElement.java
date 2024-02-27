package com.mococo.microstrategy.sdk.prompt.vo;

/**
 * PromptElement
 * @author mococo
 *
 */
public class PromptElement {
	
	/**
	 * id
	 */
    private final String promptElemid;
    
    /**
     * displayName
     */
    private final String displayName;
    
    /**
     * parentId
     */
    private final String parentId;
    
    
    /**
     * PromptElement
     * @param id
     * @param displayName
     */
    public PromptElement(final String promptId, final String displayName) {
        this(promptId, displayName, null);
    }
    
    
    /**
     * PromptElement
     * @param id
     * @param displayName
     * @param parentId
     */
    public PromptElement(final String promptId, final String displayName, final String parentId) {
        this.promptElemid = promptId;
        this.displayName = displayName;
        this.parentId = parentId;
    }
    
    
    /**
     * getId
     * @return
     */
    public String getId() {
        return promptElemid;
    }
    
    
    public String getDisplayName() {
        return displayName;
    }
    
    
    public String getParentId() {
        return parentId;
    }
    
    
    @Override
    public String toString() {
        return new StringBuilder()
			.append('{')
			.append(" id: ").append(promptElemid)
			.append(", displayName: ").append(displayName)
	        .append(", parentId: ").append(parentId)
	        .append('}')
	        .toString();
    }
}
