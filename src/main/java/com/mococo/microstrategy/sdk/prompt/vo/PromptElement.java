package com.mococo.microstrategy.sdk.prompt.vo;

public class PromptElement {
    // private static final Logger logger =
    // LoggerFactory.getLogger(PromptElement.class);

    private final String id;
    private final String displayName;
    private final String parentId;

    public PromptElement(String id, String displayName) {
        this(id, displayName, null);
    }

    public PromptElement(String id, String displayName, String parentId) {
        this.id = id;
        this.displayName = displayName;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("{id: ").append(id).append(", displayName: ").append(displayName)
                .append(", parentId: ").append(parentId).append("}").toString();
    }
}
