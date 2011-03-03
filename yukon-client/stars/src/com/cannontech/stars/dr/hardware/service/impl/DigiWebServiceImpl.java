package com.cannontech.stars.dr.hardware.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.stars.dr.hardware.service.ZigbeeWebService;

public class DigiWebServiceImpl implements ZigbeeWebService {

	private static final Log logger = LogFactory.getLog(DigiWebServiceImpl.class);
	
    private RestOperations restTemplate;

	//Constants that will be somewhere else later. For testing.
	final public String x2Mac = "00409d3d7157";
	final public String spareX2Mac = "00409d3d70AD";
	
	final public String statsMac = "000CC1002719C4BF";
	final public String statLinkKey = "000CC1002719c4BFFE10";
	
	final public String x2DigiId = "2861";
	final public String spareX2DigiId = "827";
    
	@Override
	public String installGateway(int deviceId) {
		String macAddress = "";
		
		//get macaddress for digi gateway
		
		return commissionNewConnectPort(macAddress);
	}
	
	@Override
	public String removeGateway(int deviceId) {
		int digiId = 0;
		//Get digiId
		return decommissionConnectPort(digiId);
	}
	
	private String commissionNewConnectPort(String macAddress) {
		logger.info("-- Install Commission start --");
		//This needs to be passed in...
    	//Installer Provided or have it on the Pao?

    	String xml = "<DeviceCore>" + "<devMac>"+macAddress+"</devMac>" + "</DeviceCore>";
    	
    	String response = restTemplate.postForObject("http://developer.idigi.com/ws/DeviceCore", xml, String.class);
    	logger.info(response);
    	
    	return response;
	}


	private String decommissionConnectPort(int deviceId) {
		logger.info("-- Decommision X2 start --");
		//We will have the MAC.. will we have the Digi Id? If so we just use that.. otherwise gotta discover the Digi Id.
		
    	//This needs to be an html DELETE.
    	restTemplate.delete("http://developer.idigi.com/ws/DeviceCore/" + deviceId);
    	
    	logger.info("Deleted " + deviceId + "from Digi.");
    	
    	return null;
	}



	@Override
	public String installStat(int deviceId) {
		logger.info("-- Install Stat start --");
		
		//What do we need for this?
    	
    	//Need Digi id from device... Installer Provided or have it on the Pao?
    	//Need eui
    	//Needlink key
    	
    	String reformedMac = "00000000-00000000-" + x2Mac.substring(0,6) + "FF-FF" + x2Mac.substring(6);
    	
    	String xml = "<sci_request version=\"1.0\">" +
				        "<send_message>" +
				            "<targets>" +
				                "<device id=\"" + reformedMac + "\"/>" +
				            "</targets>" +
				            "<rci_request version=\"1.1\">" +
				                "<do_command target=\"RPC_request\">" +
				                    "<add_device synchronous=\"true\">" +
				                        "<device_address type=\"MAC\">" +
				                        statsMac +
				                        "</device_address>" +
				                        "<join_time>900</join_time>" +
				                        "<link_key>" +
				                            "0x" + statLinkKey +
				                        "</link_key>" +
				                    "</add_device>" +
				                "</do_command>" +
				            "</rci_request>" +
				        "</send_message>" +
				    "</sci_request>";
    	
    	String response = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
    	logger.info(response);
    	return response;
	}

	@Override
	public String getAllDevices() {
    	logger.info("-- gettAllDevices start --");
    	
    	String html = restTemplate.getForObject("http://developer.idigi.com/ws/DeviceCore",String.class);
    	
    	logger.info(html);
    	
    	logger.info("-- gettAllDevices done --");
    	
    	return html;
	}

	@Override
	public String sendTextMessage(int deviceId, String message) {
		// TODO Auto-generated method stub\
		
		String xml = "<create_message_event synchronous=\"true\">"
			+ "<record type=\"DisplayMessageRecord\">"
			+ "<message_id>0x1234</message_id>"
			+ "<message_control>0</message_control>"
			+ "<start_time>0</start_time>"
			+ "<duration_in_minutes>2</duration_in_minutes>"
			+ "<message type=\"string\">Hello World!</message>"
			+ "</record>"
			+ "</create_message_event>";

		String response = restTemplate.postForObject("http://developer.idigi.com/ws/DeviceInterface", xml, String.class);
    	logger.info(response);
    	return response;
	}

    
    @Autowired
	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

}
