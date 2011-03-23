package com.cannontech.stars.dr.thirdparty.digi.service.impl;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.stars.dr.thirdparty.digi.service.ZigbeeWebService;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.digi.model.ZigbeeThermostat;

public class DigiWebServiceImpl implements ZigbeeWebService {

    private static final Log logger = LogFactory.getLog(DigiWebServiceImpl.class);
    
    private RestOperations restTemplate;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    
    @Override
    public void installGateway(int deviceId) {
        logger.info("-- Install Gateway Start --");
        
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(deviceId);
        Integer digiId = commissionNewConnectPort(digiGateway.getMacAddress());
        
        Validate.notNull(digiId,"Digi ID was null, installation failed.");
        
        digiGateway.setDigiId(digiId);
        
        //Update the database with the DigiId we got assigned.
        gatewayDeviceDao.updateDigiGateway(digiGateway);
        
        logger.info("-- Install Gateway End --");
    }
    
    @Override
    public void removeGateway(int deviceId) {
        logger.info("-- Remove Gateway Start --");
        
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(deviceId);
        decommissionConnectPort(digiGateway.getDigiId());

        logger.info("-- Remove Gateway Stop --");
    }
    
    /**
     * Commissions the gateway with the MAC Address.
     * 
     * returns the value of the Digi ID if successful, otherwise null.
     * 
     * @param macAddress
     * @return
     */
    private Integer commissionNewConnectPort(String macAddress) {
        logger.info("-- CommissionNewConnectPort Start --");
        String xml = "<DeviceCore>" + "<devMac>"+macAddress+"</devMac>" + "</DeviceCore>";
        
        StreamSource response = restTemplate.postForObject("http://developer.idigi.com/ws/DeviceCore", xml, StreamSource.class);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(response);
        
        String location = template.evaluateAsString("//location");
        
        if (location == null) {
            String error = template.evaluateAsString("//error");
            if (error != null) {
                logger.error("Failed to Provision device with MAC Address: " + macAddress);
                return null;
            }
        }
        
        String [] locationInfo = location.split("/");
        
        int deviceId = Integer.parseInt(locationInfo[1]);

        logger.info("Device successfully added with Digi Id: " + deviceId);
        
        logger.info("-- CommissionNewConnectPort End --");
        
        return deviceId;
    }

    private void decommissionConnectPort(int digiId) {
        logger.info("-- DecommissionNewConnectPort Start --");

        restTemplate.delete("http://developer.idigi.com/ws/DeviceCore/" + digiId);

        logger.info("Deleted " + digiId + " from Digi.");
        
        logger.info("-- DecommissionNewConnectPort End --");
    }

    @Override
    public void installStat(int deviceId, int gatewayId) {
        logger.info("-- InstallStat Start --");
        
        ZigbeeThermostat stat= zigbeeDeviceDao.getZigbeeUtilPro(deviceId);
        DigiGateway gateway = gatewayDeviceDao.getDigiGateway(gatewayId);
        
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
    }
    
    public void uninstallStat(int deviceId, int gatewayId) {
        
        ZigbeeThermostat stat= zigbeeDeviceDao.getZigbeeUtilPro(deviceId);
        DigiGateway gateway = gatewayDeviceDao.getDigiGateway(gatewayId);
        
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
        

    }
    @Override
    public String getAllDevices() {
        logger.info("-- GetAllDevices start --");
        DigiGateway gateway = gatewayDeviceDao.getDigiGateway(2861);
        
        String macAddress = convertMacAddresstoDigi(gateway.getMacAddress());
        
        String html = restTemplate.getForObject("http://developer.idigi.com/ws/data/",String.class);
        
        logger.info(html);
        
        logger.info("-- GetAllDevices done --");
        
        return html;
    }

    @Override
    public void sendTextMessage(int deviceId, int gatewayId, String message) {

        DigiGateway gateway = gatewayDeviceDao.getDigiGateway(gatewayId);
           
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
            + "            <message_id>0x1478</message_id>" //Generate Random Id
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

        String source = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
        
        //SimpleXPathTemplate template = new SimpleXPathTemplate();
        //template.setContext(source);
        
        //template.evaluateAsInt("");
        
        logger.info(source);
        
        //logger.info(response);
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
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
}
