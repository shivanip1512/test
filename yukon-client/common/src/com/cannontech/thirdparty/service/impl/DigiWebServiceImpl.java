package com.cannontech.thirdparty.service.impl;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;
import javax.annotation.PostConstruct;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.events.loggers.ZigbeeEventLogService;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.exception.DigiEmptyDeviceCoreResultException;
import com.cannontech.thirdparty.exception.DigiEndPointDisconnectedException;
import com.cannontech.thirdparty.exception.DigiGatewayDisconnectedException;
import com.cannontech.thirdparty.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.GatewayCommissionException;
import com.cannontech.thirdparty.exception.ZCLStatus;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.cannontech.thirdparty.service.ZigbeeServiceHelper;
import com.cannontech.thirdparty.service.ZigbeeWebService;

public class DigiWebServiceImpl implements ZigbeeWebService {

    private static final Logger logger = YukonLogManager.getLogger(DigiWebServiceImpl.class);

    private RestOperations restTemplate;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private DigiXMLBuilder digiXMLBuilder;
    private DigiResponseHandler digiResponseHandler;
    private ConfigurationSource configurationSource;
    private ZigbeeServiceHelper zigbeeServiceHelper;
    private ZigbeeEventLogService zigbeeEventLogService;
    private PaoDao paoDao;
    
    private static String digiBaseUrl;
    
    @PostConstruct
    public void initialize() {
        digiBaseUrl = configurationSource.getString("DIGI_WEBSERVICE_URL", "http://developer.idigi.com/");
    }
    
    @Override
    public void installGateway(int gatewayId) throws GatewayCommissionException {
        logger.debug("-- Install Gateway Start --");
        
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(gatewayId);
        
        try{
            Integer digiId = commissionNewConnectPort(digiGateway.getMacAddress());
            
            //Update the Commissioned Point State
            zigbeeServiceHelper.sendPointStatusUpdate(digiGateway, 
                                                      BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                      Commissioned.COMMISSIONED);
            
            //Update the Connection Point State
            zigbeeServiceHelper.sendPointStatusUpdate(digiGateway, 
                                                      BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                      CommStatusState.CONNECTED);
            
            //Update the database with the DigiId we got assigned.
            digiGateway.setDigiId(digiId);
            gatewayDeviceDao.updateDigiGateway(digiGateway);
            
        } catch (GatewayCommissionException e) {
            logger.error("Caught exception in the commissioning process", e);
            //re throw
            throw e;
        }

        zigbeeEventLogService.zigbeeDeviceCommissioned(digiGateway.getName());
        
        logger.debug("-- Install Gateway End --");
    }
    
    @Override
    public void removeGateway(int gatewayId) {
        logger.debug("-- Remove Gateway Start --");
        DigiGateway digiGateway= gatewayDeviceDao.getDigiGateway(gatewayId);
        decommissionConnectPort(digiGateway.getDigiId());

        //Update the Commissioned Point State
        zigbeeServiceHelper.sendPointStatusUpdate(digiGateway, 
                                                  BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                  Commissioned.DECOMMISSIONED);
        
        //When decommission a device, change it's connection status to disconnected as well.
        zigbeeServiceHelper.sendPointStatusUpdate(digiGateway, 
                                                  BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                  CommStatusState.DISCONNECTED);
        
        zigbeeEventLogService.zigbeeDeviceDecommissioned(digiGateway.getName());
        
        logger.debug("-- Remove Gateway Stop --");
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
        logger.debug("CommissionNewConnectPort Start");
        String xml = "<DeviceCore>" + "<devMac>"+macAddress+"</devMac>" + "</DeviceCore>";
        
        StreamSource response;
        try {
            response = restTemplate.postForObject(digiBaseUrl + "ws/DeviceCore", xml, StreamSource.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

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
        
        logger.debug("CommissionNewConnectPort End");
        
        return digiId;
    }

    private void decommissionConnectPort(int digiId) {
        logger.debug("DecommissionNewConnectPort Start");
        
        try {
            restTemplate.delete(digiBaseUrl + "ws/DeviceCore/" + digiId);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        logger.info("Deleted " + digiId + " from Digi.");
        
        logger.debug("DecommissionNewConnectPort End");
    }

    @Override
    public void installStat(int gatewayId, int deviceId) {
        logger.debug("InstallStat Start");
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeThermostat stat= zigbeeDeviceDao.getZigbeeUtilPro(deviceId);
        
        String xml = digiXMLBuilder.buildInstallStatMessage(gateway,stat);
        StreamSource response;
        
        try {
            response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, StreamSource.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        //Throws a DigiWebServiceException if it failed.
        digiResponseHandler.evaluateAddDevice(response);
        
        zigbeeEventLogService.zigbeeDeviceCommissioned(stat.getName());
        
        logger.debug("InstallStat End");
    }
    
    public void uninstallStat(int gatewayId, int deviceId) {
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        ZigbeeDevice stat = zigbeeDeviceDao.getZigbeeDevice(deviceId);
        
        String xml = digiXMLBuilder.buildUninstallStatMessage(gateway, stat);
        
        String response;
        try {
            response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
                
        //When decommissioning a device, change it's connection status to disconnected 
        //since we will no longer get updates for it, prevent it from appearing connected
        zigbeeServiceHelper.sendPointStatusUpdate(stat, 
                                                  BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                  CommStatusState.DISCONNECTED);
        zigbeeServiceHelper.sendPointStatusUpdate(stat, 
                                                  BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                  Commissioned.DECOMMISSIONED);
        
        zigbeeEventLogService.zigbeeDeviceDecommissioned(stat.getName());
        
        logger.debug(response);
    }

    @Override
    public void refreshDeviceStatuses(ZigbeeDevice device) {
        PaoType type = device.getPaoIdentifier().getPaoType();
        
        switch(type) {
            case ZIGBEEUTILPRO: {
                int gatewayId = gatewayDeviceDao.findGatewayIdForDeviceId(device.getZigbeeDeviceId());
                ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
                CommStatusState endPointState = CommStatusState.CONNECTED;
                CommStatusState gatewayState = CommStatusState.CONNECTED;
                
                try {
                    pingEndPoint(device);
                } catch (DigiEndPointDisconnectedException e) {
                    logger.error("Caught Exception while attempting to ping " +type + " with id: " + device.getZigbeeDeviceId(),e);
                    endPointState = CommStatusState.DISCONNECTED;
                    throw e;
                } catch (DigiGatewayDisconnectedException e) {
                    logger.error("Caught Exception while attempting to ping " +type + " with id: " + device.getZigbeeDeviceId(),e);
                    endPointState = CommStatusState.DISCONNECTED;
                    gatewayState = CommStatusState.DISCONNECTED;
                    throw e;
                } finally {
                    
                    zigbeeServiceHelper.sendPointStatusUpdate(device, 
                                                              BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                              endPointState);
                    zigbeeServiceHelper.sendPointStatusUpdate(gateway, 
                                                              BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                              gatewayState);
                }
                
                break;
            }
            case DIGIGATEWAY: {
                try {
                    //This call will send the point status values for us.
                    refreshGateway(device);
                } catch(DigiEmptyDeviceCoreResultException e) {
                    String errorMsg = "Gateway not found on iDigi Account. Possibly need to re-commission.";
                    logger.error(errorMsg);
                    
                    zigbeeServiceHelper.sendPointStatusUpdate(device, 
                                                              BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                              CommStatusState.DISCONNECTED);
                    zigbeeServiceHelper.sendPointStatusUpdate(device, 
                                                              BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                              Commissioned.DECOMMISSIONED);
                    throw new DigiWebServiceException(errorMsg);
                }
                break;
            }
            default: {
                throw new DigiWebServiceException("Refresh of " + type.getDbString() + " is not supported.");
            }
        }
        
        zigbeeEventLogService.zigbeeDeviceRefreshed(device.getName());
    }
    
    private boolean pingEndPoint(ZigbeeDevice endPoint) {
        Integer gatewayId = gatewayDeviceDao.findGatewayIdForDeviceId(endPoint.getPaoIdentifier().getPaoId());
        Validate.notNull(gatewayId, "Device not assigned to a gateway, cannot send addressing configs.");
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
        
        String xml = digiXMLBuilder.buildReadLMAddressingForEndPoint(gateway, endPoint);
        
        StreamSource response;
        try {
            response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, StreamSource.class);
        } catch(RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        //Throws a web service exception if it failed.
        digiResponseHandler.evaluatePingResponse(response);

        return true;
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
        
        zigbeeEventLogService.zigbeeDeviceLoadGroupAddressingSent(endPoint.getName());
    }
    
    @Override
    public void refreshGateway(ZigbeeDevice gateway) {
        logger.debug("Refresh Gateway start");
        String convertedMac = DigiXMLBuilder.convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
        
        //URL for single gateway. NOT using digiId in case it has not been commissioned properly
        String url = digiBaseUrl + "ws/DeviceCore/?condition=devConnectwareId='" + convertedMac +"'";

        refreshDeviceCore(url);
        
        logger.debug("Refresh Gateway done");
    }
    
    @Override
    public void refreshAllGateways() {
        logger.debug("Refresh ALL Gateway start");
        
        //URL to load all gateways the account.
        String url = digiBaseUrl + "ws/DeviceCore/";
        
        try {
            refreshDeviceCore(url);
        } catch (DigiEmptyDeviceCoreResultException e) {
            //eat the Error.. It is not REQUIRED to have a gateway
        }
        
        zigbeeEventLogService.zigbeeRefreshedAllGateways();
        
        logger.debug("Refresh ALL Gateway done");
    }
    
    private void refreshDeviceCore(String url) {
        StreamSource xml;
        
        try {
            xml = restTemplate.getForObject(url,StreamSource.class);
        } catch(RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        digiResponseHandler.handleDeviceCoreResponse(xml);
    }
    
    @Override
    public void sendManualAdjustment(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        logger.debug("Sending adjusment message to Gateway with Id: " + message.getGatewayId() 
                     +" message: \"" + message.getMessage() +"\"");

        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(message.getGatewayId());
        
        //Hijacking the text message for smuggling. This will change to Private Extensions in future revisions.
        sendTextMessage(gateway,message);

        zigbeeEventLogService.zigbeeSentManualAdjustment(gateway.getName());
    }
    
    @Override
    public void sendTextMessage(ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(message.getGatewayId());
        
        sendTextMessage(gateway,message);
        
        zigbeeEventLogService.zigbeeSentText(gateway.getName(),message.getMessage());
    }
    
    private void sendTextMessage(ZigbeeDevice gateway, ZigbeeTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        String xml = digiXMLBuilder.buildTextMessage(gateway, message);
        StreamSource source;
        
        try {
            source = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, StreamSource.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        Node errorNode = template.evaluateAsNode("//error");
        if (errorNode != null) {
            logger.error(" Sending Text message was unsuccessful.");
            
            String errorText = template.evaluateAsString("//desc");
            throw new DigiWebServiceException(errorText);
        } else {
            String statusText = template.evaluateAsString("//status");
            
            Validate.notNull(statusText);
            
            int status = Integer.decode(statusText);
            if (status != 0) {
                ZCLStatus zclStatus = ZCLStatus.getByValue(status);
                logger.error(" Sending Text message was unsuccessful: " + zclStatus.getDescription());
                logger.debug(" Gateway MAC: " + gateway.getZigbeeMacAddress());
                throw new ZigbeeClusterLibraryException(zclStatus);
            }
        }
    }

    @Override
    public void sendSEPControlMessage(int eventId, SepControlMessage controlMessage) {
        logger.debug("Sending SEP Control Message Start");
        
        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForGroupId(controlMessage.getGroupId());
        
        String xmlSEPMessage = digiXMLBuilder.buildSEPControlMessage(eventId, gateways, controlMessage);
        logger.debug(xmlSEPMessage);
        
        String response;
        
        try {
            response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xmlSEPMessage, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(controlMessage.getGroupId());
        zigbeeEventLogService.zigbeeSentSepControl(pao.getPaoName());
        
        logger.debug("XML Response to SEP Control \n " + response);
        
        logger.debug("Sending SEP Control Message End");
    }
    
    @Override
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage) {
        logger.debug("Sending SEP Restore Message Start");
        String xml = digiXMLBuilder.buildSepRestore(eventId, restoreMessage);
       
        String response;
        try {
            response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(restoreMessage.getGroupId());
        zigbeeEventLogService.zigbeeSentSepRestore(pao.getPaoName());
        
        logger.debug("XML Response to SEP Restore \n " + response);
        
        logger.debug("Sending SEP Restore Message Start");
        return;
    }
    
    @Override
    public void processAllDeviceNotificationsOnGateway(ZigbeeDevice gateway) throws SocketTimeoutException{
        String zbDeviceId = DigiXMLBuilder.convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
        String folderUrl = digiBaseUrl + "ws/data/~/" + zbDeviceId;
        
        StreamSource fileNameSource;
        try {
            fileNameSource = restTemplate.getForObject(folderUrl + "?recursive=no", StreamSource.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
        List<String> files = digiResponseHandler.handleFolderListingResponse(fileNameSource);
        
        for (String filename:files) {
            try {
                StreamSource deviceNotification;
                
                try {
                    deviceNotification = restTemplate.getForObject(folderUrl +"/" + filename, StreamSource.class);
                } catch (RestClientException e) {
                    throw new DigiWebServiceException(e);
                }
                
                digiResponseHandler.handleDeviceNotification(deviceNotification);
            } catch (UnsupportedDataTypeException e) {
                logger.error(e.getMessage());
            } finally {
                restTemplate.delete(folderUrl +"/" + filename);
            }
        }
        
        zigbeeEventLogService.zigbeePolledGateway(gateway.getName());
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
    public void setZigbeeEventLogService(ZigbeeEventLogService zigbeeEventLogService) {
        this.zigbeeEventLogService = zigbeeEventLogService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
