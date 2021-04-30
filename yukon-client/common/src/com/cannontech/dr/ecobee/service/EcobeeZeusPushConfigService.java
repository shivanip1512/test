package com.cannontech.dr.ecobee.service;

import java.util.Map;

public interface EcobeeZeusPushConfigService {
    /**
     * Create Push API Configuration
     */
    String createPushApiConfig();

    /**
     * Show Push API Configuration
     */
    Map<String, String> showPushApiConfig();
}
