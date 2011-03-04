package com.cannontech.stars.dr.hardware.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.common.model.DigiGateway;
import com.cannontech.stars.dr.hardware.service.ZigbeeWebService;

public class DigiDummyWebServiceImpl implements ZigbeeWebService {

    private static final Log logger = LogFactory.getLog(DigiWebServiceImpl.class);
    
    private RestOperations restTemplate;
    private ZigbeeDeviceService zigbeeDeviceService;
    
	@Override
	public String getAllDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String installGateway(int deviceId) {
	    DigiGateway digiGateway= zigbeeDeviceService.getDigiGateway(deviceId);
        logger.info("-- CommissionNewConnectPort Start --");

        String xml = "<DeviceCore>" + "<devMac>"+digiGateway.getMacAddress()+"</devMac>" + "</DeviceCore>";
        
        String response = restTemplate.postForObject("http://developer.idigi.com/ws/DeviceCore", xml, String.class);

        logger.info(response);
        
        logger.info("-- CommissionNewConnectPort End --");
        
        return response;
	}

	@Override
	public String removeGateway(int deviceId) {
        logger.info("-- DecommissionNewConnectPort Start --");
        DigiGateway digiGateway= zigbeeDeviceService.getDigiGateway(deviceId);
        
        restTemplate.delete("http://developer.idigi.com/ws/DeviceCore/" + digiGateway.getDigiId());
        
        logger.info("Deleted " + digiGateway.getDigiId() + " from Digi.");
        
        logger.info("-- DecommissionNewConnectPort End --");
        
        return null;
	}

	@Override
	public String installStat(int deviceId, int gatewayId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String uninstallStat(int deviceId, int gatewayId) {
	    return null;
	}

	@Override
	public String sendTextMessage(int deviceId, int gatewayId,  String message) {
		// TODO Auto-generated method stub
		return null;
	}
    @Autowired
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setZigbeeDeviceService(ZigbeeDeviceService zigbeeDeviceService) {
        this.zigbeeDeviceService = zigbeeDeviceService;
    }
}
