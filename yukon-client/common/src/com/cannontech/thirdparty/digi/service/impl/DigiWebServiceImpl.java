package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiEmptyDeviceCoreResultException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.digi.service.errors.ZigbeePingResponse;
import com.cannontech.thirdparty.exception.ZCLStatus;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.exception.ZigbeeCommissionException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.messaging.SmartUpdateRequestMessage;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;
import com.cannontech.thirdparty.service.ZigbeeServiceHelper;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DigiWebServiceImpl implements ZigbeeWebService, ZigbeeStateUpdaterService {

    private static final Logger log = YukonLogManager.getLogger(DigiWebServiceImpl.class);

    private RestOperations restTemplate;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private DigiXMLBuilder digiXMLBuilder;
    private DigiResponseHandler digiResponseHandler;
    private ConfigurationSource configurationSource;
    private ZigbeeServiceHelper zigbeeServiceHelper;

    private JmsTemplate jmsTemplate;
    
    private static String digiBaseUrl;
    
    @PostConstruct
    public void initialize() {
        digiBaseUrl = configurationSource.getString("DIGI_WEBSERVICE_URL", "http://developer.idigi.com/");
    }
    
    public static String getDigiBaseUrl() {
        return digiBaseUrl;
    }
    
    @Override
    public void installGateway(int gatewayId) {
        log.debug("Install Gateway Start");
        
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(gatewayId);
        
        try{
            Integer digiId = commissionNewConnectPort(digiGateway.getMacAddress());
            
            //Update the Commissioned Point State
            zigbeeServiceHelper.sendPointStatusUpdate(digiGateway, 
                                                      BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                      Commissioned.DISCONNECTED);
            
            //Update the database with the DigiId we got assigned.
            digiGateway.setDigiId(digiId);
            gatewayDeviceDao.updateDigiGateway(digiGateway);
            log.debug("Install Gateway End");
        } catch (RuntimeException e) {
            log.error("Install Gateway End With Exceptions");
            throw e;
        } finally {
        }
    }
    
    @Override
    public void removeGateway(int gatewayId) {
        log.debug("Remove Gateway Start");
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(gatewayId);
        decommissionConnectPort(digiGateway.getDigiId());

        //Update the Commissioned Point State
        zigbeeServiceHelper.sendPointStatusUpdate(digiGateway, 
                                                  BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                  Commissioned.DECOMMISSIONED);
        
        log.debug("Remove Gateway Stop");
    }
    
    /**
     * Commissions the gateway with the MAC Address.
     * 
     * returns the value of the Digi ID if successful, otherwise null.
     * 
     * @param macAddress
     * @return
     */
    private Integer commissionNewConnectPort(String macAddress) throws ZigbeeCommissionException {
        log.debug("CommissionNewConnectPort Start");
        String xml = "<DeviceCore>" + "<devMac>"+macAddress+"</devMac>" + "</DeviceCore>";

        log.debug(xml);
        String response = restTemplate.postForObject(digiBaseUrl + "ws/DeviceCore", xml, String.class);
        log.debug(response);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(response);
        
        String location = template.evaluateAsString("//location");
        
        if (location == null) {
            String error = template.evaluateAsString("//error");
            if (error != null) {
                MessageSourceResolvable resolvable;
                if (error.contains("already provisioned")) {
                    resolvable = YukonMessageSourceResolvable.createDefault("yukon.web.modules.operator.hardware.commandFailed.provisioned", error);
                } else if (error.contains("already exists")){
                    resolvable = YukonMessageSourceResolvable.createDefault("yukon.web.modules.operator.hardware.commandFailed.exists", error);
                } else {
                    resolvable = YukonMessageSourceResolvable.createDefault("yukon.web.modules.operator.hardware.commandFailed", error);
                }
                log.debug(error);
                log.error("Failed to Provision device with MAC Address: " + macAddress);
                throw new ZigbeeCommissionException(resolvable);
            }
        }
        
        String [] locationInfo = location.split("/");
        
        int digiId = Integer.parseInt(locationInfo[1]);
        
        log.info("Device successfully added with Digi Id: " + digiId);
        
        log.debug("CommissionNewConnectPort End");
        
        return digiId;
    }

    private void decommissionConnectPort(int digiId) {
        log.debug("DecommissionNewConnectPort Start");
        
        try {
            restTemplate.delete(digiBaseUrl + "ws/DeviceCore/" + digiId);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        log.info("Deleted " + digiId + " from Digi.");
        
        log.debug("DecommissionNewConnectPort End");
    }

    @Override
    public void installEndPoint(int gatewayId, int deviceId) throws ZigbeeCommissionException {
        log.debug("InstallEndPoint Start");
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeEndpoint stat= zigbeeDeviceDao.getZigbeeEndPoint(deviceId);
        
        String xml = digiXMLBuilder.buildInstallEndPointMessage(gateway,stat);
        String response;
        
        try {
            log.debug(xml);
            response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
            log.debug(response);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        //Throws a DigiWebServiceException if it failed.
        digiResponseHandler.evaluateAddDevice(response);
        
        log.debug("InstallEndPoint End");
    }
    
    public void uninstallEndPoint(int gatewayId, int deviceId) {
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeDevice endpoint = zigbeeDeviceDao.getZigbeeDevice(deviceId);
        
        String xml = digiXMLBuilder.buildUninstallEndPointMessage(gateway, endpoint);
        
        try {
            log.debug(xml);
            restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        //Set to decommissioned
        zigbeeServiceHelper.sendPointStatusUpdate(endpoint, 
                                                  BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                  Commissioned.DECOMMISSIONED);
    }
    
    @Override
    public void activateSmartPolling(ZigbeeDevice device) {
        jmsTemplate.convertAndSend("yukon.notif.obj.dr.smartUpdateRequest", new SmartUpdateRequestMessage(device.getPaoIdentifier()));
    }
    
    @Override
    public ZigbeePingResponse updateEndPointStatus(ZigbeeDevice endPoint) {
        Integer gatewayId = gatewayDeviceDao.findGatewayIdForDeviceId(endPoint.getPaoIdentifier().getPaoId());
        Validate.notNull(gatewayId, "Device not assigned to a gateway, cannot send addressing configs.");
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        
        String xml = digiXMLBuilder.buildReadLMAddressingForEndPoint(gateway, endPoint);
        
        String response;
        try {
            log.debug(xml);
            response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
            log.debug(response);
        } catch(RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        ZigbeePingResponse pingResponse = digiResponseHandler.handlePingResponse(response,endPoint,gateway);
        
        return pingResponse;
    }
    
    @Override
    public void sendLoadGroupAddressing(int deviceId, Map<DRLCClusterAttribute,Integer> attributes) {
        Integer gatewayId = gatewayDeviceDao.findGatewayIdForDeviceId(deviceId);
        Validate.notNull(gatewayId, "Device not assigned to a gateway, cannot send addressing configs.");
        ZigbeeDevice endPoint = zigbeeDeviceDao.getZigbeeDevice(deviceId);
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        
        String xml = digiXMLBuilder.buildWriteLMAddressing(gateway, endPoint, attributes);
        
        try{
            restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
    }
    
    @Override
    public ZigbeePingResponse updateGatewayStatus(ZigbeeDevice gateway) {
        log.debug("Refresh Gateway start");
        String convertedMac = DigiXMLBuilder.convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
        
        //URL for single gateway. NOT using digiId in case it has not been commissioned properly
        String url = digiBaseUrl + "ws/DeviceCore/?condition=devConnectwareId='" + convertedMac +"'";

        List<ZigbeeDevice> expected = Lists.newArrayList();
        expected.add(gateway);
        
        Map<PaoIdentifier,ZigbeePingResponse> pingResponses;
        try {
            pingResponses = refreshDeviceCore(url,expected);
        } catch (DigiEmptyDeviceCoreResultException e) {
            String errorMsg = "Gateway not found on iDigi Account. Possibly need to re-commission.";
            log.error(errorMsg);
            throw new DigiWebServiceException(errorMsg);
        }

        if (pingResponses.size() > 1) {
            throw new DigiWebServiceException("Ping returned more than one result.");
        }
        
        log.debug("Refresh Gateway done");
        
        return pingResponses.get(gateway.getPaoIdentifier());
    }
    
    @Override
    public Map<PaoIdentifier,ZigbeePingResponse> updateAllGatewayStatuses() {
        log.debug("Refresh ALL Gateway start");
        
        //URL to load all gateways the account.
        String url = digiBaseUrl + "ws/DeviceCore/";
        List<ZigbeeDevice> expected = gatewayDeviceDao.getAllGateways();
        Map<PaoIdentifier,ZigbeePingResponse> pingResponses = Maps.newHashMap();
        
        try {
            pingResponses = refreshDeviceCore(url,expected);
        } catch (DigiEmptyDeviceCoreResultException e) {
            //eat the Error.. It is not REQUIRED to have a gateway
        }
        
        log.debug("Refresh ALL Gateway done");
        
        return pingResponses;
    }
    
    private Map<PaoIdentifier,ZigbeePingResponse> refreshDeviceCore(String url, List<ZigbeeDevice> expected) {
        String xml;
        
        try {
            xml = restTemplate.getForObject(url,String.class);
        } catch(RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        Map<PaoIdentifier,ZigbeePingResponse> updated = digiResponseHandler.handleDeviceCoreResponse(xml,expected);
        
        //If there were no results, toss an exception. We want this at the end so we decommission the gateway and devices.
        if (updated.isEmpty()) {
            throw new DigiEmptyDeviceCoreResultException("0 results from DeviceCore query.");
        }
        
        return updated;
    }
    
    @Override
    public void sendManualAdjustment(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        log.debug("Sending adjusment message to Gateway with Id: " + message.getGatewayId() 
                     +" message: \"" + message.getMessage() +"\"");

        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(message.getGatewayId());
        
        //Hijacking the text message for smuggling. This will change to Private Extensions in future revisions.
        sendTextMessage(gateway,message);
    }
    
    @Override
    public void sendTextMessage(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(message.getGatewayId());
        
        sendTextMessage(gateway,message);
    }
    
    private void sendTextMessage(ZigbeeDevice gateway, ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        String xml = digiXMLBuilder.buildTextMessage(gateway, message);
        String source;
        
        try {
            log.debug(xml);
            source = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        Node errorNode = template.evaluateAsNode("//error");
        if (errorNode != null) {
            log.error(" Sending Text message was unsuccessful.");
            
            String errorText = template.evaluateAsString("//desc");
            throw new DigiWebServiceException(errorText);
        } else {
            String statusText = template.evaluateAsString("//status");
            
            Validate.notNull(statusText);
            
            int status = Integer.decode(statusText);
            if (status != 0) {
                ZCLStatus zclStatus = ZCLStatus.getByValue(status);
                log.error(" Sending Text message was unsuccessful: " + zclStatus.getDescription());
                log.debug(" Gateway MAC: " + gateway.getZigbeeMacAddress());
                throw new ZigbeeClusterLibraryException(zclStatus);
            }
        }
    }

    @Override
    public void sendSEPControlMessage(int eventId, SepControlMessage controlMessage) {
        log.debug("Sending SEP Control Message Start");
        
        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForGroupId(controlMessage.getGroupId());
        
        String xmlSEPMessage = digiXMLBuilder.buildSEPControlMessage(eventId, gateways, controlMessage);
        log.debug(xmlSEPMessage);
        
        try {
            log.debug(xmlSEPMessage);
            restTemplate.postForObject(digiBaseUrl + "ws/sci", xmlSEPMessage, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        log.debug("Sending SEP Control Message End");
    }
    
    @Override
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage) {
        log.debug("Sending SEP Restore Message Start");
        String xml = digiXMLBuilder.buildSepRestore(eventId, restoreMessage);
       
        try {
            log.debug(xml);
            restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        log.debug("Sending SEP Restore Message End");
        return;
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
    public void setZigbeeServiceHelper(ZigbeeServiceHelper zigbeeServiceHelper) {
        this.zigbeeServiceHelper = zigbeeServiceHelper;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
    }
}
