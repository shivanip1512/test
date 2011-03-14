package com.cannontech.stars.dr.thirdparty.digi.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionServiceImpl;
import com.cannontech.stars.dr.thirdparty.digi.model.DigiGateway;
import com.cannontech.stars.dr.thirdparty.digi.service.impl.DigiWebServiceImpl;

public class DigiDummyWebServiceImpl implements ZigbeeWebService {

    private static final Log logger = LogFactory.getLog(DigiWebServiceImpl.class);
    
    private RestOperations restTemplate;
    
	@Override
	public String getAllDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void installGateway(int deviceId) {
////	    DigiGateway digiGateway= zigbeeDeviceService.getDigiGateway(deviceId);
//        logger.info("-- CommissionNewConnectPort Start --");
//
//        String xml = "<DeviceCore>" + "<devMac>"+digiGateway.getMacAddress()+"</devMac>" + "</DeviceCore>";
//        
//        String response = restTemplate.postForObject("http://developer.idigi.com/ws/DeviceCore", xml, String.class);
//
//        logger.info(response);
//        
//        logger.info("-- CommissionNewConnectPort End --");
        
//        return response;
	}

	@Override
	public void removeGateway(int deviceId) {
//        logger.info("-- DecommissionNewConnectPort Start --");
////        DigiGateway digiGateway= zigbeeDeviceService.getDigiGateway(deviceId);
//        
//        restTemplate.delete("http://developer.idigi.com/ws/DeviceCore/" + digiGateway.getDigiId());
//        
//        logger.info("Deleted " + digiGateway.getDigiId() + " from Digi.");
//        
//        logger.info("-- DecommissionNewConnectPort End --");

	}

	@Override
	public void installStat(int deviceId, int gatewayId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallStat(int deviceId, int gatewayId) {
	   
	}

	@Override
	public void sendTextMessage(int deviceId, int gatewayId,  String message) {
		// TODO Auto-generated method stub
		
	}
    @Autowired
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

}
