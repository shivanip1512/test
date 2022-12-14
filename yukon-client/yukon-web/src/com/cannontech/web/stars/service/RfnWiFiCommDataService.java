package com.cannontech.web.stars.service;

import java.util.List;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;

public interface RfnWiFiCommDataService {

    /**
     * @return WiFiMeterCommData Objects for a list of gateway Ids;
     * @param gatewayIds - A list of GatewayIds
     */
    List<WiFiMeterCommData> getWiFiMeterCommDataForGateways(List<Integer> gatewayIds);
    
    /**
     * @return WiFiMeterCommData Object for a Wi-Fi Device
     * @param RfnDevice - a Wi-Fi device
     */
    WiFiMeterCommData buildWiFiMeterCommDataObject(RfnDevice rfnDevice);


    /**
     * This method will send a "getstatus wifi" command to devices of paoType WiFi
     * 
     * @param paoIdentifiers - A list of PaoIdentifiers
     * @param user - LiteYukonUser object
     */
    void refreshWiFiMeterConnection(List<PaoIdentifier> paoIdentifiers, LiteYukonUser user);
    
}
