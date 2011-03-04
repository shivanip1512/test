package com.cannontech.stars.dr.hardware.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.common.model.DigiGateway;
import com.cannontech.common.model.ZigbeeThermostat;
import com.cannontech.stars.dr.hardware.service.ZigbeeWebService;

public class DigiWebServiceImpl implements ZigbeeWebService {

    private static final Log logger = LogFactory.getLog(DigiWebServiceImpl.class);
    
    private RestOperations restTemplate;
    private ZigbeeDeviceService zigbeeDeviceService;

    @Override
    public String installGateway(int deviceId) {
        logger.info("-- Install Gateway Start --");
        DigiGateway digiGateway= zigbeeDeviceService.getDigiGateway(deviceId);
        logger.info("-- Install Gateway End --");
        return commissionNewConnectPort(digiGateway.getMacAddress());
    }
    
    @Override
    public String removeGateway(int deviceId) {
        logger.info("-- Remove Gateway Start --");
        DigiGateway digiGateway= zigbeeDeviceService.getDigiGateway(deviceId);
        logger.info("-- Remove Gateway Start --");
        return decommissionConnectPort(digiGateway.getDigiId());
    }
    
    private String commissionNewConnectPort(String macAddress) {
        logger.info("-- CommissionNewConnectPort Start --");

        String xml = "<DeviceCore>" + "<devMac>"+macAddress+"</devMac>" + "</DeviceCore>";
        
        String response = restTemplate.postForObject("http://developer.idigi.com/ws/DeviceCore", xml, String.class);

        logger.info(response);
        
        logger.info("-- CommissionNewConnectPort End --");
        
        return response;
    }


    private String decommissionConnectPort(int digiId) {
        logger.info("-- DecommissionNewConnectPort Start --");

        restTemplate.delete("http://developer.idigi.com/ws/DeviceCore/" + digiId);
        
        logger.info("Deleted " + digiId + " from Digi.");
        
        logger.info("-- DecommissionNewConnectPort End --");
        
        return null;
    }



    @Override
    public String installStat(int deviceId, int gatewayId) {
        logger.info("-- InstallStat Start --");
        
        ZigbeeThermostat stat= zigbeeDeviceService.getZigbeeThermostat(deviceId);
        DigiGateway gateway = zigbeeDeviceService.getDigiGateway(gatewayId);
        
        String macAddress = convertMacAddresstoDigi(gateway.getMacAddress());
        
        //TODO Get this from the system
        String thermostatMac = "000CC1002719C4BF";
        
        String xml = "<sci_request version=\"1.0\">" +
                        "<send_message>" +
                            "<targets>" +
                                "<device id=\"" + macAddress + "\"/>" +
                            "</targets>" +
                            "<rci_request version=\"1.1\">" +
                                "<do_command target=\"RPC_request\">" +
                                    "<add_device synchronous=\"true\">" +
                                        "<device_address type=\"MAC\">" +
                                        thermostatMac +
                                        "</device_address>" +
                                        "<join_time>900</join_time>" +
                                        "<installation_code type=\"string\">" +
                                            stat.getInstallCode() +
                                        "</installation_code>" +
                                    "</add_device>" +
                                "</do_command>" +
                            "</rci_request>" +
                        "</send_message>" +
                    "</sci_request>";
        
        String response = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
        
        logger.info(response);
        
        logger.info("-- InstallStat End --");
        
        return response;
    }
    
    public String uninstallStat(int deviceId, int gatewayId) {
        
        ZigbeeThermostat stat= zigbeeDeviceService.getZigbeeThermostat(deviceId);
        DigiGateway gateway = zigbeeDeviceService.getDigiGateway(gatewayId);
        
        String macAddress = convertMacAddresstoDigi(gateway.getMacAddress());
        
        //TODO Get this from the system
        String thermostatMac = "000CC1002719C4BF";
        
        String xml = 
               "<sci_request version=\"1.0\">"
            + "  <send_message>"
            + "    <targets>"
            + "      <device id=\"" + macAddress + "\"/>"
            + "    </targets>"
            + "    <rci_request version=\"1.1\">"
            + "      <do_command target=\"RPC_request\">"
            + "        <remove_device synchronous=\"true\">"
            + "          <device_address type=\"MAC\">" + thermostatMac + "</device_address>"
            + "        </remove_device>"
            + "      </do_command>"
            + "    </rci_request>"
            + "  </send_message>"
            + "</sci_request>";
        
        String response = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
        
        return response;
    }
    @Override
    public String getAllDevices() {
        logger.info("-- GetAllDevices start --");
        
        String html = restTemplate.getForObject("http://developer.idigi.com/ws/DeviceCore",String.class);
        
        logger.info(html);
        
        logger.info("-- GetAllDevices done --");
        
        return html;
    }

    @Override
    public String sendTextMessage(int deviceId, int gatewayId, String message) {

        DigiGateway gateway = zigbeeDeviceService.getDigiGateway(gatewayId);
           
        String macAddress = convertMacAddresstoDigi(gateway.getMacAddress());
        
        String xml = 
               "<sci_request version=\"1.0\">"
            + "  <send_message>"
            + "    <targets>"
            + "      <device id=\"" + macAddress + "\"/>"
            + "    </targets>"
            + "    <rci_request version=\"1.1\">"
            + "      <do_command target=\"RPC_request\">"
            + "        <create_message_event synchronous=\"true\">"
            + "          <record type=\"DisplayMessageRecord\">"
            + "            <message_id>0x1234</message_id>" //Generate Random Id
            + "            <message_control>0</message_control>"
            + "            <start_time>0</start_time>"
            + "            <duration_in_minutes>2</duration_in_minutes>"
            + "            <message type=\"string\">" + message + "</message>"
            + "          </record>"
            + "        </create_message_event>"
            + "      </do_command>"
            + "    </rci_request>"
            + "  </send_message>"
            + "</sci_request>";

        String response = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
        logger.info(response);
        return response;
    }

    private String convertMacAddresstoDigi(String mac) {
        String digiMac = mac.replaceAll(":","");
        
        digiMac = "00000000-00000000-" + digiMac.substring(0,6) + "FF-FF" + digiMac.substring(6);
        
        return digiMac;
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
