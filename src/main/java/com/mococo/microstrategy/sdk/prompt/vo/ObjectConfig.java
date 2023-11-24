package com.mococo.microstrategy.sdk.prompt.vo;

import java.util.Map;
import java.util.Set;

public final class ObjectConfig {

    private final Map<String, Object> config;

    public ObjectConfig(Map<String, Object> config) {
        this.config = config;
    }

    public <P> P get(String key) {
        return config != null ? (P) config.get(key) : null;
    }

    public Set<String> keySet() {
        return config != null ? config.keySet() : null;
    }
}
