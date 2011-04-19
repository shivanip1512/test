package com.cannontech.thirdparty.service.impl;

import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;
import org.springframework.xml.xpath.NodeMapper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.model.DeviceResponse;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.exception.GatewayCommissionException;
import com.cannontech.thirdparty.exception.ZCLStatus;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeGateway;
import com.cannontech.thirdparty.model.ZigbeeText;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.cannontech.thirdparty.service.DeviceResponseHandler;
import com.cannontech.thirdparty.service.ZigbeeWebService;

public class DigiWebServiceImpl implements ZigbeeWebService {

    private static final Logger logger = YukonLogManager.getLogger(DigiWebServiceImpl.class);

    private RestOperations restTemplate;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private DigiXMLBuilder digiXMLBuilder;
    
    @Override
    public void installGateway(int gatewayId) throws GatewayCommissionException {
        logger.info("-- Install Gateway Start --");
        
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(gatewayId);
        
        try{
            Integer digiId = commissionNewConnectPort(digiGateway.getMacAddress());
            
            //Update the database with the DigiId we got assigned.
            digiGateway.setDigiId(digiId);
            gatewayDeviceDao.updateDigiGateway(digiGateway);
        } catch (GatewayCommissionException e) {
            // TODO figure out what happened specifically to log.
            logger.error("Caught exception in the commissioning process", e);
            //re throw
            throw e;
        }
        
        logger.info("-- Install Gateway End --");
    }
    
    @Override
    public void removeGateway(int gatewayId) {
        logger.info("-- Remove Gateway Start --");
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(gatewayId);
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
    private Integer commissionNewConnectPort(String macAddress) throws GatewayCommissionException {
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
                throw new GatewayCommissionException(error);
            }
        }
        
        String [] locationInfo = location.split("/");
        
        int digiId = Integer.parseInt(locationInfo[1]);
        
        logger.info("Device successfully added with Digi Id: " + digiId);
        
        logger.info("-- CommissionNewConnectPort End --");
        
        return digiId;
    }

    private void decommissionConnectPort(int digiId) {
        logger.info("-- DecommissionNewConnectPort Start --");

        restTemplate.delete("http://developer.idigi.com/ws/DeviceCore/" + digiId);

        logger.info("Deleted " + digiId + " from Digi.");
        
        logger.info("-- DecommissionNewConnectPort End --");
    }

    @Override
    public void installStat(int gatewayId, int deviceId) {
        logger.info("-- InstallStat Start --");
        ZigbeeGateway gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeThermostat stat= zigbeeDeviceDao.getZigbeeUtilPro(deviceId);
        
        String xml = digiXMLBuilder.buildInstallStatMessage(gateway,stat);
        
        String response = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
        
        logger.info(response);
        
        logger.info("-- InstallStat End --");
    }
    
    public void uninstallStat(int gatewayId, int deviceId) {
        ZigbeeGateway gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeDevice device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
        
        String xml = digiXMLBuilder.buildUninstallStatMessage(gateway, device);
        
        String response = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
        
        logger.info(response);
    }

    @Override
    public String getAllDevices() {
        logger.info("-- GetAllDevices start --");

        String html = restTemplate.getForObject("http://developer.idigi.com/ws/data/",String.class);
        
        logger.info(html);
        
        logger.info("-- GetAllDevices done --");
        
        return html;
    }

    @Override
    public void sendTextMessage(int gatewayId, ZigbeeText zigbeeText) throws ZigbeeClusterLibraryException {
        ZigbeeGateway gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        
        String xml = digiXMLBuilder.buildTextMessage(gateway,zigbeeText);

        StreamSource source = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, StreamSource.class);
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        String statusText = template.evaluateAsString("//status");
        
        int status = Integer.parseInt(statusText.substring(2), 16);
        
        if (status != 0) {
            logger.error(" Sending Text message was unsuccessful.");
            ZCLStatus zclStatus = ZCLStatus.getByValue(status);
            throw new ZigbeeClusterLibraryException(zclStatus);
        }
        
        logger.info ("  Sending Text message was successful.");
    }

    @Override
    public int sendSEPControlMessage(int eventId, SepControlMessage controlMessage, DeviceResponseHandler responseHandler) {

        List<ZigbeeGateway> gateways = gatewayDeviceDao.getZigbeeGatewaysForGroupId(controlMessage.getGroupId());
        String xmlSEPMessage = digiXMLBuilder.buildSEPControlMessage(eventId, gateways, controlMessage);
        
        StreamSource source = restTemplate.postForObject("http://developer.idigi.com/ws/sci", xmlSEPMessage, StreamSource.class);
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
            
        NodeMapper deviceReplyNodeMapper = new NodeMapper() {

            @Override
            public Object mapNode(Node node, int nodeNum) throws DOMException {                
                DeviceResponse response = new DeviceResponse();
                SimpleXPathTemplate template =  YukonXml.getXPathTemplateForNode(node);
                
                String deviceId = node.getAttributes().getNamedItem("id").getNodeValue();
                response.setDeviceId(deviceId);
                
                Node errorNode = template.evaluateAsNode("error");
                if (errorNode != null) {
                    String errorId = errorNode.getAttributes().getNamedItem("id").getNodeValue();
                    String errorStr = template.evaluateAsString("error");
                    
                    response.setErrorId(Integer.parseInt(errorId));
                    response.setErrorDescription(errorStr);
                } else {
                    String statusStr = template.evaluateAsString("rci_reply/do_command/responses/create_DRLC_event_response/status");
                    List<String> zbDeviceIds = template.evaluateAsStringList("rci_reply/do_command/responses/create_DRLC_event_response/device_list/item");
                    response.setDeviceList(zbDeviceIds);
                    response.setStatus(Integer.parseInt(statusStr.substring(2),16));
                }
                
                return response;
            }
        };
        
        @SuppressWarnings("unchecked")
        List<DeviceResponse> deviceResponses = template.evaluate("//device", deviceReplyNodeMapper);
        responseHandler.handleResponses(deviceResponses);
        
        return gateways.size();
    }
    
    @Override
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage, DeviceResponseHandler responseHandler) {

        String xml = digiXMLBuilder.buildSepRestore(eventId, restoreMessage);
       
        restTemplate.postForObject("http://developer.idigi.com/ws/sci", xml, String.class);
        
        //handleResponses
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
    
    @Autowired
    public void setDigiXMLBuilder(DigiXMLBuilder digiXMLBuilder) {
        this.digiXMLBuilder = digiXMLBuilder;
    }

}
