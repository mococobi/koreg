package com.mococo.microstrategy.sdk.prompt.config;

import com.microstrategy.web.objects.WebIServerSession;

/**
 * MstrSessionProvider
 * @author mococo
 *
 */
public interface MstrSessionProvider {
	
	/**
	 * getSession
	 * @return
	 * @throws Exception
	 */
    WebIServerSession getSession();

}
