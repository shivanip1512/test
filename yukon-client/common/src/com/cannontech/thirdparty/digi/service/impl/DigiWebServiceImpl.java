package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiEmptyDeviceCoreResultException;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.model.DevConnectwareId;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.digi.service.errors.ZigbeePingResponse;
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

    @Autowired private @Qualifier("digi") RestOperations restTemplate;
    @Autowired private GatewayDeviceDao gatewayDeviceDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ZigbeeDeviceDao zigbeeDeviceDao;
    @Autowired private DigiXMLBuilder digiXMLBuilder;
    @Autowired private DigiResponseHandler digiResponseHandler;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private ZigbeeServiceHelper zigbeeServiceHelper;

    private static String digiBaseUrl;
    private static String digiEndPointReadUrl;
    private YukonJmsTemplate jmsTemplate;

    /**
     * All public methods in this class must check that Digi is enabled (the cparm
     * DIGI_ENABLED is set to TRUE) in order to guarantee that we aren't making lots
     * of unnecessary web service calls to Digi's servers from unconfigured systems.
     * This can be accomplished by calling verifyDigiEnabled() at the top of public methods.
     */
    private static boolean digiEnabled = false;

    @PostConstruct
    public void initialize() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.ZIGBEE_SMART_UPDATE);
        digiEnabled = configurationSource.getBoolean(MasterConfigBoolean.DIGI_ENABLED);
        if (digiEnabled) {
            digiBaseUrl = configurationSource.getString("DIGI_WEBSERVICE_URL", "http://my.idigi.com/");
            digiEndPointReadUrl = digiBaseUrl + "ws/XbeeCore";
        }
    }

    /**
     * verifyDigiEnabled() must be called at the beginning of all public methods within this
     * class to guarantee that the system has been configured to support Digi web service calls.
     */
    private static void verifyDigiEnabled() {
        if (!digiEnabled) {
            throw new DigiNotConfiguredException("SEP not configured.");
        }
    }

    public static String getDigiBaseUrl() {
        return digiBaseUrl;
    }

    @Override
    public void installGateway(int gatewayId) {
        verifyDigiEnabled();
        log.debug("Install Gateway Start");

        DigiGateway digiGateway = gatewayDeviceDao.getDigiGateway(gatewayId);

        try{
            Integer digiId = commissionNewConnectPort(digiGateway.getMacAddress());

            //Update the Commissioned Point State
            zigbeeServiceHelper.sendPointStatusUpdate(digiGateway,
                                                      BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                      Commissioned.DISCONNECTED);

            gatewayDeviceDao.updateDigiId(digiGateway.getPaoIdentifier(), digiId);

            log.debug("Install Gateway End");
        } catch (RuntimeException e) {
            log.error("Install Gateway End With Exceptions");
            throw e;
        }
    }

    @Override
    public void removeGateway(int gatewayId) {
        verifyDigiEnabled();
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
                    resolvable = YukonMessageSourceResolvable.createDefaultWithArguments("yukon.web.modules.operator.hardware.commandFailed", error, error);
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
        verifyDigiEnabled();
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

    @Override
    public void uninstallEndPoint(int gatewayId, int deviceId) {
        verifyDigiEnabled();
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
        verifyDigiEnabled();
        jmsTemplate.convertAndSend(new SmartUpdateRequestMessage(device.getPaoIdentifier()));
    }

    @Override
    public void readLoadGroupAddressing(ZigbeeDevice endPoint) {
        verifyDigiEnabled();
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

        digiResponseHandler.handleLoadGroupAddressingRead(response,endPoint,gateway);
    }

    @Override
    public void sendLoadGroupAddressing(int deviceId, Map<DRLCClusterAttribute,Integer> attributes) {
        verifyDigiEnabled();
        Integer gatewayId = gatewayDeviceDao.findGatewayIdForDeviceId(deviceId);
        Validate.notNull(gatewayId, "Device not assigned to a gateway, cannot send addressing configs.");
        ZigbeeDevice endPoint = zigbeeDeviceDao.getZigbeeDevice(deviceId);
        ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);

        String xml = digiXMLBuilder.buildWriteLMAddressing(gateway, endPoint, attributes);

        try{
            log.debug(xml);
            String response = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
            log.debug(response);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }
    }

    @Override
    public ZigbeePingResponse updateGatewayStatus(ZigbeeDevice gateway) {
        verifyDigiEnabled();
        log.debug("Refresh Gateway start");

        DevConnectwareId connectwareId = new DevConnectwareId(gateway.getZigbeeMacAddress());

        //URL for single gateway. NOT using digiId in case it has not been commissioned properly
        String url = digiBaseUrl + "ws/DeviceCore/?condition=devConnectwareId='" + connectwareId.getDevConnectwareId() +"'";

        List<ZigbeeDevice> expected = Lists.newArrayList();
        expected.add(gateway);

        Map<PaoIdentifier,ZigbeePingResponse> pingResponses;
        try {
            pingResponses = refreshDeviceCore(url,expected);
        } catch (DigiEmptyDeviceCoreResultException e) {
            String errorMsg = "Gateway not found on iDigi Account. This usually means the device is not commissioned properly.";
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
        verifyDigiEnabled();
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
    public Map<PaoIdentifier,ZigbeePingResponse> updateAllEndPointStatuses() {
        verifyDigiEnabled();
        log.debug("Refresh ALL End Point start");

        List<ZigbeeDevice> allEndPoints = zigbeeDeviceDao.getAllEndPoints();

        Map<PaoIdentifier,ZigbeePingResponse> pingResponses = Maps.newHashMap();

        try {
            //NodeType of 1 is router. All Upros are routers. This might pull in other things that are not YUKON related.
            //4171 is Cooper. This might limit us too much if we want to talk with other companies devices.
            //4126 is a new mfgId. We are no longer filtering based on mfgId.
            pingResponses = refreshEndPoints(digiEndPointReadUrl + "?condition=xpNodeType=1",allEndPoints);
        } catch (DigiEmptyDeviceCoreResultException e) {
            //eat the Error.. It could be the case there is no endpoints.
        }

        log.debug("Refresh ALL End Point end");
        return pingResponses;
    }

    @Override
    public ZigbeePingResponse updateEndPointStatus(ZigbeeDevice endPoint) {
        verifyDigiEnabled();
        List<ZigbeeDevice> expected = Lists.newArrayList();
        expected.add(endPoint);

        Map<PaoIdentifier,ZigbeePingResponse> pingResponses = Maps.newHashMap();

        try {
            pingResponses = refreshEndPoints(digiEndPointReadUrl + "/" + endPoint.getZigbeeMacAddress(),expected);
        } catch (DigiEmptyDeviceCoreResultException e) {
            String errorMsg = "Device not found on iDigi Account. This usually means the device is not commissioned properly.";
            log.error(errorMsg);
            throw new DigiWebServiceException(errorMsg);
        }

        if (pingResponses.size() > 1) {
            throw new DigiWebServiceException("Ping returned more than one result.");
        }

        return pingResponses.get(endPoint.getPaoIdentifier());
    }

    private Map<PaoIdentifier,ZigbeePingResponse> refreshEndPoints(String url, List<ZigbeeDevice> expected) {
        String xml;

        try {
            xml = restTemplate.getForObject(url,String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        Map<PaoIdentifier,ZigbeePingResponse> updated = digiResponseHandler.handleXbeeCoreResponse(xml,expected);

        //If there were no results, toss an exception to alert this.
        if (updated.isEmpty()) {
            throw new DigiEmptyDeviceCoreResultException("0 results from XbeeCore query.");
        }

        return updated;
    }

    @Override
    public void sendManualAdjustment(YukonTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        verifyDigiEnabled();
        log.debug("Sending adjusment message to Gateways. Message: \"" + message.getMessage() +"\"");

        Set<Integer> inventoryIds = message.getInventoryIds();
        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForInventoryIds(inventoryIds);

        //Hijacking the text message for smuggling. This will change to Private Extensions in future revisions.
        message.setMessageId(nextValueHelper.getNextValue("ExternalToYukonMessageIdMapping"));
        sendTextMessage(gateways,message);
    }

    @Override
    public void sendTextMessage(YukonTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        verifyDigiEnabled();
        log.debug("Sending text message to Gateways. Message: \"" + message.getMessage() +"\"");

        Set<Integer> inventoryIds = message.getInventoryIds();
        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForInventoryIds(inventoryIds);

        sendTextMessage(gateways,message);
    }

    private void sendTextMessage(List<ZigbeeDevice> gateways, YukonTextMessage message) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        String xml = digiXMLBuilder.buildTextMessage(gateways, message);
        String source;

        try {
            log.debug(xml);
            source = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);

        log.debug(source);

        //TODO: This error handling is still assuming we only sent to one gateway
//        Node errorNode = template.evaluateAsNode("//error");
//        if (errorNode != null) {
//            log.error(" Sending Text message was unsuccessful.");
//
//            String errorText = template.evaluateAsString("//desc");
//            throw new DigiWebServiceException(errorText);
//        } else {
//            String statusText = template.evaluateAsString("//status");
//
//            Validate.notNull(statusText);
//
//            int status = Integer.decode(statusText);
//            if (status != 0) {
//                ZCLStatus zclStatus = ZCLStatus.getByValue(status);
//                log.error(" Sending Text message was unsuccessful: " + zclStatus.getDescription());
//                //TODO HERE
//                log.debug(" Gateway MAC: " + gateways.get(0).getZigbeeMacAddress());
//                throw new ZigbeeClusterLibraryException(zclStatus);
//            }
//        }
    }

    @Override
    public void cancelTextMessage(YukonCancelTextMessage cancelZigbeeText) throws ZigbeeClusterLibraryException, DigiWebServiceException {
        verifyDigiEnabled();
        Set<Integer> inventoryIds = cancelZigbeeText.getInventoryIds();
        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForInventoryIds(inventoryIds);

        cancelTextMessage(gateways,cancelZigbeeText);
    }

    private void cancelTextMessage(List<ZigbeeDevice> devices, YukonCancelTextMessage cancelZigbeeText) {
        String xml = digiXMLBuilder.buildCancelMessageEvent(devices, cancelZigbeeText);
        String source;

        try {
            log.debug(xml);
            source = restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);

        log.debug(source);

        //TODO: This error handling is still assuming we only sent to one gateway
//        Node errorNode = template.evaluateAsNode("//error");
//        if (errorNode != null) {
//            log.error(" Sending Text message was unsuccessful.");
//
//            String errorText = template.evaluateAsString("//desc");
//            throw new DigiWebServiceException(errorText);
//        } else {
//            String statusText = template.evaluateAsString("//status");
//
//            Validate.notNull(statusText);
//
//            int status = Integer.decode(statusText);
//            if (status != 0) {
//                ZCLStatus zclStatus = ZCLStatus.getByValue(status);
//                log.error(" Sending Text message was unsuccessful: " + zclStatus.getDescription());
//                //TODO HERE
//                log.debug(" Gateway MAC: " + gateways.get(0).getZigbeeMacAddress());
//                throw new ZigbeeClusterLibraryException(zclStatus);
//            }
//        }
    }

    @Override
    public void sendSEPControlMessage(int eventId, SepControlMessage controlMessage) {
        verifyDigiEnabled();
        log.debug("Sending SEP Control Message Start");

        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForGroupId(controlMessage.getGroupId());

        String xmlSEPMessage = digiXMLBuilder.buildSEPControlMessage(eventId, gateways, controlMessage);
        log.debug(xmlSEPMessage);

        try {
            restTemplate.postForObject(digiBaseUrl + "ws/sci", xmlSEPMessage, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        log.debug("Sending SEP Control Message End");
    }

    @Override
    public void sendSEPRestoreMessage(int eventId, SepRestoreMessage restoreMessage) {
        verifyDigiEnabled();
        log.debug("Sending SEP Restore Message Start");

        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForGroupId(restoreMessage.getGroupId());

        String xml = digiXMLBuilder.buildSepRestore(eventId, gateways, restoreMessage);

        try {
            log.debug(xml);
            restTemplate.postForObject(digiBaseUrl + "ws/sci", xml, String.class);
        } catch (RestClientException e) {
            throw new DigiWebServiceException(e);
        }

        log.debug("Sending SEP Restore Message End");
        return;
    }

}
