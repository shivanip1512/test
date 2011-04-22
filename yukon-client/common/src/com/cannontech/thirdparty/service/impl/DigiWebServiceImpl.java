package com.cannontech.thirdparty.service.impl;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;
import javax.annotation.PostConstruct;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.GatewayCommissionException;
import com.cannontech.thirdparty.exception.ZCLStatus;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeGateway;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.cannontech.thirdparty.service.ZigbeeWebService;

public class DigiWebServiceImpl implements ZigbeeWebService {

    private static final Logger logger = YukonLogManager.getLogger(DigiWebServiceImpl.class);

    private RestOperations restTemplate;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private DigiXMLBuilder digiXMLBuilder;
    private DigiResponseHandler digiResponseHandler;
    private ConfigurationSource configurationSource;
    private SimplePointAccessDao simplePointAccessDao;
    private AttributeService attributeService;
    
    private static String digiBaseUrl;
    
    @PostConstruct
    public void initialize() {
        digiBaseUrl = configurationSource.getString("DIGI_WEBSERVICE_URL", "http://developer.idigi.com/");
    }
    
    @Override
    public void installGateway(int gatewayId) throws GatewayCommissionException {
        logger.info("-- Install Gateway Start --");
        
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(gatewayId);
        
        try{
            Integer digiId = commissionNewConnectPort(digiGateway.getMacAddress());
            
            //Update the Commissioned Point State
            LitePoint point = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
            simplePointAccessDao.setPointValue(point, Commissioned.COMMISSIONED);
            
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

        //Update the Commissioned Point State
        LitePoint point = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        simplePointAccessDao.setPointValue(point, Commissioned.DECOMMISSIONED);
        
        //Update the Connection Point State
        point = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.CONNECTION_STATUS);
        simplePointAccessDao.setPointValue(point, CommStatusState.DISCONNECTED);
        
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
        
        StreamSource response = restTemplate.postForObject(digiBaseUrl + "ws/DeviceCore", xml, StreamSource.class);

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

        restTemplate.delete(digiBaseUrl + "ws/DeviceCore/" + digiId);

        logger.info("Deleted " + digiId + " from Digi.");
        
        logger.info("-- DecommissionNewConnectPort End --");
    }

    @Override
    public void installStat(int gatewayId, int deviceId) {
        logger.info("-- InstallStat Start --");
        ZigbeeGateway gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeThermostat stat= zigbeeDeviceDao.getZigbeeUtilPro(deviceId);
        
        String xml = digiXMLBuilder.buildInstallStatMessage(gateway,stat);
        
        String response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        
        logger.debug(response);
        
        logger.info("-- InstallStat End --");
    }
    
    public void uninstallStat(int gatewayId, int deviceId) {
        ZigbeeGateway gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeDevice device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
        
        String xml = digiXMLBuilder.buildUninstallStatMessage(gateway, device);
        
        String response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        
        logger.debug(response);
    }

    @Override
    public String getAllDevices() {
        logger.info("-- GetAllDevices start --");

        String html = restTemplate.getForObject(digiBaseUrl + "ws/data/",String.class);
        
        logger.info(html);
        
        logger.info("-- GetAllDevices done --");
        
        return html;
    }

    @Override
    public void sendTextMessage(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        ZigbeeGateway gateway = gatewayDeviceDao.getZigbeeGateway(message.getGatewayId());
        
        String xml = digiXMLBuilder.buildTextMessage(gateway, message);

        StreamSource source = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, StreamSource.class);
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        String statusText = template.evaluateAsString("//status");
        int status = Integer.decode(statusText);
        
        if (status != 0) {
            logger.error(" Sending Text message was unsuccessful.");
            ZCLStatus zclStatus = ZCLStatus.getByValue(status);
            throw new ZigbeeClusterLibraryException(zclStatus);
        }
        
        logger.info ("  Sending Text message was successful.");
            
    }

    @Override
    public int sendSEPControlMessage(int eventId, SepControlMessage controlMessage) {

        List<ZigbeeGateway> gateways = gatewayDeviceDao.getZigbeeGatewaysForGroupId(controlMessage.getGroupId());
        String xmlSEPMessage = digiXMLBuilder.buildSEPControlMessage(eventId, gateways, controlMessage);
        
        StreamSource source = restTemplate.postForObject(digiBaseUrl + "ws/sci", xmlSEPMessage, StreamSource.class);
        
        digiResponseHandler.handleSEPControlResponses(source);
        
        return gateways.size();
    }
    
    @Override
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage) {

        String xml = digiXMLBuilder.buildSepRestore(eventId, restoreMessage);
       
        restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        
        //handleResponses
    }
    
    @Override
    public void processAllDeviceNotificationsOnGateway(ZigbeeGateway gateway) throws SocketTimeoutException{
        String zbDeviceId = DigiXMLBuilder.convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
        
        String folderUrl = digiBaseUrl + "ws/data/~/" + zbDeviceId;
        
        StreamSource fileNameSource = restTemplate.getForObject(folderUrl + "?recursive=no", StreamSource.class);
        List<String> files = digiResponseHandler.handleFolderListingResponse(fileNameSource);
        
        for (String filename:files) {
            try {
                StreamSource deviceNotification = restTemplate.getForObject(folderUrl +"/" + filename, StreamSource.class);
                
                digiResponseHandler.handleDeviceNotification(deviceNotification);
            } catch (UnsupportedDataTypeException e) {
                logger.error(e.getMessage());
            } finally {
                restTemplate.delete(folderUrl +"/" + filename);
            }
        }

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

    @Autowired
    public void setDigiResponseHandler(DigiResponseHandler digiResponseHandler) {
        this.digiResponseHandler = digiResponseHandler;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @Autowired
    public void setSimplePointAccessDao(SimplePointAccessDao simplePointAccessDao) {
        this.simplePointAccessDao = simplePointAccessDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
