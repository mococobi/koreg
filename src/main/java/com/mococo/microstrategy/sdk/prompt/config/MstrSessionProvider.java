package com.mococo.microstrategy.sdk.prompt.config;

import com.microstrategy.web.objects.WebIServerSession;

/**
 * 
 * @author hyoungilpark
 *
 */
public interface MstrSessionProvider {

    public WebIServerSession getSession() throws Exception;

}
