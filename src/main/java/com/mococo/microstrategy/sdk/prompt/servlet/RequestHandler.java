package com.mococo.microstrategy.sdk.prompt.servlet;

import java.util.Map;

import com.mococo.microstrategy.sdk.prompt.config.MstrSessionProvider;

public interface RequestHandler<T> {

    public T GetResponse(MstrSessionProvider session, Map<String, Object> param);

}
