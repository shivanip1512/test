package com.cannontech.services.pxmw.authToken.service;

import javax.jms.JMSException;

import com.cannontech.services.pxmw.authToken.message.PxMWAuthTokenRequest;
import com.cannontech.services.pxmw.authToken.message.PxMWAuthTokenResponse;

public interface PxMWAuthTokenService {

    /**
     * Receives from jmsqueue and responds with auth token value. 
     */
    PxMWAuthTokenResponse handle(PxMWAuthTokenRequest request) throws JMSException;

}
