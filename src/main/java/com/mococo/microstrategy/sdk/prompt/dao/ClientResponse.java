package com.mococo.microstrategy.sdk.prompt.dao;

public interface ClientResponse<R1, R2> {

    public R2 getClientResponse(R1 param);

}
