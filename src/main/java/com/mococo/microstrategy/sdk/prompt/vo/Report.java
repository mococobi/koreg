package com.mococo.microstrategy.sdk.prompt.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {
    private final String id;
    private final int type;
    private final String title;
    private List<Prompt> promptList = new ArrayList<Prompt>();
    private List<Map<String, Object>> customPromptList = new ArrayList<Map<String, Object>>();

    public Report(String id, int type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public void addPrompt(Prompt prompt) {
        this.promptList.add(prompt);
    }

    public String getId() {
        return id;
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
        return new StringBuilder().append("{id: ").append(id).append(", type: ").append(type).append(", title: ")
                .append(title).append(", promptList: ").append(promptList).append("}").toString();
    }

}
