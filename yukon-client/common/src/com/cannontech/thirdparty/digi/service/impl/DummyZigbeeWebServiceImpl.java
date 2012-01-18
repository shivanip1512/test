package com.cannontech.thirdparty.digi.service.impl;

import java.util.Map;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.model.CancelZigbeeText;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.service.errors.ZigbeePingResponse;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.exception.ZigbeeCommissionException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;
import com.cannontech.thirdparty.service.ZigbeeWebService;

public class DummyZigbeeWebServiceImpl implements ZigbeeWebService, ZigbeeStateUpdaterService{

    @Override
    public Map<PaoIdentifier, ZigbeePingResponse> updateAllGatewayStatuses() {
        return null;
    }

    @Override
    public Map<PaoIdentifier, ZigbeePingResponse> updateAllEndPointStatuses() {
        return null;
    }
    
    @Override
    public ZigbeePingResponse updateGatewayStatus(ZigbeeDevice Gateway) {
        String notConfigured = "yukon.web.modules.operator.hardware.zigbeeNotEnabled";
        MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(notConfigured);
        ZigbeePingResponse response = new ZigbeePingResponse(false,
                                                                                      Commissioned.DECOMMISSIONED,
                                                                                      resolvable);
        
        return response;
    }

    @Override
    public ZigbeePingResponse updateEndPointStatus(ZigbeeDevice endPoint) {
        String notConfigured = "yukon.web.modules.operator.hardware.zigbeeNotEnabled";
        MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(notConfigured);
        ZigbeePingResponse response = new ZigbeePingResponse(false,
                                                                                      Commissioned.DECOMMISSIONED,
                                                                                      resolvable);
        return response;
    }

    @Override
    public void activateSmartPolling(ZigbeeDevice device) {
    }

    @Override
    public void installGateway(int gatewayId) throws ZigbeeCommissionException {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void removeGateway(int gatewayId) {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void installEndPoint(int gatewayId, int deviceId) {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void uninstallEndPoint(int gatewayId, int deviceId) {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void sendLoadGroupAddressing(int deviceId, Map<DRLCClusterAttribute, Integer> attributes) {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void sendTextMessage(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException,
            DigiWebServiceException {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void sendManualAdjustment(ZigbeeTextMessage message)
            throws ZigbeeClusterLibraryException, DigiWebServiceException {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void sendSEPControlMessage(int eventId, SepControlMessage controlMessage) {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage) {
        throw new DigiWebServiceException("ZigBee Web Services is not enabled.");
    }

    @Override
    public void cancelTextMessage(CancelZigbeeText cancelZigbeeText) throws ZigbeeClusterLibraryException, DigiWebServiceException {
    }

}
