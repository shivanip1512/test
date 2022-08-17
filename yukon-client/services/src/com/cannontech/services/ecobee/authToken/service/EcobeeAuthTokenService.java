package com.cannontech.services.ecobee.authToken.service;

import javax.jms.JMSException;

import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenResponse;

public interface EcobeeAuthTokenService {

    /**
     * Receives from jmsqueue and responds with auth token value. 
     */
    EcobeeAuthTokenResponse handle(EcobeeAuthTokenRequest request) throws JMSException;

}
