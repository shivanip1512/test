package com.cannontech.services.ecobee.authToken.service;

import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenResponse;

public interface EcobeeZeusAuthTokenService {

    /**
     * Receives from JmsQueue and responds with auth token value.
     */
    ZeusEcobeeAuthTokenResponse authenticate();

}
