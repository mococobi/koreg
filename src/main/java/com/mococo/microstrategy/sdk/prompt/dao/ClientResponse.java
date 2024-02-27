package com.mococo.microstrategy.sdk.prompt.dao;

/**
 * ClientResponse
 * @author mococo
 */
public interface ClientResponse<A, B> {
	
	/**
	 * getClientResponse
	 * @param param
	 * @return
	 */
    B getClientResponse(A param);

}
