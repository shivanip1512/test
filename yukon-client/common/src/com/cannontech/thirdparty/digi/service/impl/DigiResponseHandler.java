package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;

import org.apache.log4j.Logger;
import org.jdom.Namespace;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.model.DeviceCore;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.digi.service.errors.ZigbeePingResponse;
import com.cannontech.thirdparty.exception.ZigbeeCommissionException;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndPoint;
import com.cannontech.thirdparty.model.ZigbeeEventAction;
import com.cannontech.thirdparty.service.ZigbeeServiceHelper;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DigiResponseHandler {
  
    private static final Logger log = YukonLogManager.getLogger(DigiResponseHandler.class);
    
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeControlEventDao zigbeeControlEventDao;
    private ZigbeeServiceHelper zigbeeServiceHelper;
    private ZigbeeStateUpdaterService zigbeeStateUpdaterService;
    
    private AttributeDynamicDataSource attributeDynamicDataSource;
    
    private static String regexForMac = "([\\da-fA-F]{2}:){7}[\\da-fA-F]{2}";
    private static Pattern macPattern = Pattern.compile(regexForMac);
    private static String regexNodeId = "NWK: ([\\da-fA-F]{4})";
    private static Pattern nodeAddrPattern = Pattern.compile(regexNodeId);
    
    private static final Namespace existNamespace = Namespace.getNamespace("exist", "http://exist.sourceforge.net/NS/exist");
    private static Properties existProperties = new Properties();
    static {
        existProperties.put(existNamespace.getPrefix(), existNamespace.getURI());
    }
    
    private static ObjectMapper<Node, String> digiFileListingNodeMapper = new ObjectMapper<Node,String>() {

        @Override
        public String map(Node node) throws DOMException {                            
            String fileName = node.getAttributes().getNamedItem("name").getNodeValue();
            String folderName = node.getParentNode().getAttributes().getNamedItem("name").getNodeValue();
            
            return folderName + "/" + fileName;
        }
    };
    
    private static ObjectMapper<Node, DeviceCore> digiDeviceCoreNodeMapper = new ObjectMapper<Node,DeviceCore>() {

        @Override
        public DeviceCore map(Node node) throws DOMException {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);

            int devId = template.evaluateAsInt("id/devId");
            String devMac = template.evaluateAsString("devMac");

            int connectionStatus = template.evaluateAsInt("dpConnectionStatus");
            boolean connected = connectionStatus==1?true:false;
            
            String devFirmware = template.evaluateAsString("dpFirmwareLevelDesc");
            String lastTimeStr = template.evaluateAsString("dpLastConnectTime");
            String commissionTime = template.evaluateAsString("devEffectiveStartDate");
            
            //If the gateway has not been connected yet, this field may be null
            if (devFirmware == null) {
                devFirmware = CtiUtilities.STRING_NONE;
            }
            
            //Even if lastTimeStr is null, this will just create an Instant to now.
            Instant lastTimeInstant = new Instant(lastTimeStr);
            Instant commissionInstant = new Instant(commissionTime);
            
            return new DeviceCore(devId,devMac,devFirmware,connected,lastTimeInstant,commissionInstant);
        }
    };

    /**
     * Returns a list of paths to DeviceNotifications that are stored on the gateway. 
     * 
     * @return
     */
    public List<String> handleFolderListingResponse(String source) {
        List<String> fileNames = Lists.newArrayList();
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        if (template.evaluateAsString("/wsError") != null) {
           int code = template.evaluateAsInt("/wsError/error/code");
           String message = template.evaluateAsString("/wsError/error/message");
           String hint = template.evaluateAsString("/wsError/error/hint");
           log.error("Error Polling Gateway: " + code + " " + hint + ". " + message);
        } else { 
            template.setNamespaces(existProperties);
            fileNames.addAll(template.evaluate("//exist:resource", digiFileListingNodeMapper));
        }
        return fileNames;
    }
    
    /**
     * Processes the DeviceNotification contained in the response.
     * 
     * @param source
     * @throws UnsupportedDataTypeException 
     */
    public void handleDeviceNotification(String source) throws UnsupportedDataTypeException {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        //Figure out what this file is
        Node tester = template.evaluateAsNode("/received_report_event_status");
        if (tester != null) {
            log.info("Processing XML file of type: 'received_report_event_status message'.");
            processReportEventStatus(template);
            return;
        }

        tester = template.evaluateAsNode("/message");
        if (tester != null) {
            log.info("Processing XML file of type: 'message'.");
            
            Long seconds = template.evaluateAsLong("//@timestamp");
            Instant time = new Instant(seconds*1000);
            
            String description = template.evaluateAsString("//description");
            log.info("RECV at "+ time + " from iDigi: " + description);
            parseMessageForAction(time,description);
            return;
        }
        
        log.error(source);
        throw new UnsupportedDataTypeException("Unsupported XML file type returned from iDigi.");
    }

    /**
     * Parse for the connection status of the gateway contained in source.
     * 
     * returns a list of PaoIds that were update.
     * 
     * @param source
     */
    public Map<PaoIdentifier,ZigbeePingResponse> handleDeviceCoreResponse(String source, List<ZigbeeDevice> expected) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        String successKey = "yukon.web.modules.operator.hardware.refreshSuccessful";
        List<DeviceCore> cores = template.evaluate("/result/DeviceCore", digiDeviceCoreNodeMapper);
        
        Map<PaoIdentifier,ZigbeePingResponse> pingResponses = Maps.newHashMap();
        for (DeviceCore core : cores) {
            DigiGateway digiGateway =  gatewayDeviceDao.getDigiGateway(core.getDevMac());        
            digiGateway.setDigiId(core.getDevId());
            digiGateway.setFirmwareVersion(core.getDevFirmware());
            gatewayDeviceDao.updateDigiGateway(digiGateway);

            Commissioned state;
            
            //We are commissioned if we got here. So set connected / disconnected accordingly
            if (core.isConnected()) {
                state = Commissioned.CONNECTED;
                connectGateway(digiGateway);
            } else {
                state = Commissioned.DISCONNECTED;
                disconnectGateway(digiGateway);
            }
            
            MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(successKey);
            ZigbeePingResponse response = new ZigbeePingResponse(true,
                                                                                          state,
                                                                                          resolvable);
            pingResponses.put(digiGateway.getPaoIdentifier(), response);
        }
        
        //Set Decommissioned to anything we did not get a response from.
        for (ZigbeeDevice gateway : expected) {
            if (pingResponses.keySet().contains(gateway.getPaoIdentifier())) {
                continue;
            }
            MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(successKey);
            ZigbeePingResponse response = new ZigbeePingResponse(false,
                                                                                          Commissioned.DECOMMISSIONED,
                                                                                          resolvable);
            pingResponses.put(gateway.getPaoIdentifier(),response);
            decommissionGateway(gateway);
        }
        
        return pingResponses;
    }
    
    /**
     * The messages we get from digi have no Type indicators. It is just a raw String.. making due with what we get..
     * 
     * @param message
     */
    private void parseMessageForAction(Instant time, String message) {
        //Try to determine what this message is
        String commissionStr = " registered with XBee";
        String decommissionStr = " unregistered from XBee";
        String connectStr = "Received device announce message from known device";
        String connectStr2 = "detected and marked as active";
        String disconnectStr = "marked as inactive";
        
        Matcher m = macPattern.matcher(message);
       
        if (m.find()) {
            String macAddress = m.group();
            ZigbeeEndPoint utilPro = zigbeeDeviceDao.getZigbeeEndPointByMACAddress(macAddress);
            //We found a MAC, so lets do something..
            if (message.contains(commissionStr)) {
                // Registered with the gateway
                log.debug("Device: " + macAddress + " has been registered.");
                return;
            } else if ( message.contains(decommissionStr)) {
                //DeCommissioned
                log.debug("Device: " + macAddress + " was decommissioned.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                                              Commissioned.DECOMMISSIONED);
                return;
                
            } else if(message.contains(connectStr)) {
                //Has been commissioned, set to disconnected until we receive the marked active message.
                log.debug("Device: " + macAddress + " has been commissioned.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                          Commissioned.DISCONNECTED);
                
                //"Received device announce message from known device 00:0C:C1:00:27:19:C4:D1 (NWK: E0BB)"
                m = nodeAddrPattern.matcher(message);
                
                if (m.find()) {
                    int nodeId = Integer.parseInt(m.group(1), 16);
                    
                    utilPro.setNodeId(nodeId);
                    zigbeeDeviceDao.updateZigbeeEndPoint(utilPro);
                } else {
                    log.warn("NodeId was not in the reponse.");
                }
                
                return;
            } else if ( message.contains(connectStr) || message.contains(connectStr2)) {
                //Connected
                log.debug("Device: " + macAddress + " has connected.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                                              Commissioned.CONNECTED);
                return;
                
            } else if ( message.contains(disconnectStr)) {
                //Disconnected
                log.debug("Device: " + macAddress + " has disconnected.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                                              Commissioned.DISCONNECTED);
                return;
            }
            
            log.debug("No Action found in current message for device: " + macAddress);
            return;
        }
        
        log.debug("No device detected in Message. No actions to performed");
    }
    
    private void processReportEventStatus(SimpleXPathTemplate template) {        
        String temp = template.evaluateAsString("//event_status_time");
        long seconds = Long.decode(temp);
        Instant statusTime = TimeUtil.convertUtc2000ToInstant(seconds);
        
        String macAddress = template.evaluateAsString("//source_address");
        int deviceId = zigbeeDeviceDao.getDeviceIdForMACAddress(macAddress);

        temp = template.evaluateAsString("//issuer_event_id");
        int eventId = Integer.decode(temp);
        
        temp = template.evaluateAsString("//event_status");
        int eventStatus = Integer.decode(temp);
        ZigbeeEventAction action = ZigbeeEventAction.getForEventStatus(eventStatus);        
        log.info("event_status: " + action.name());
        
        switch (action) {
            case THERMOSTAT_ACK: {
                zigbeeControlEventDao.updateDeviceAck(true, eventId, deviceId);
                break;
            }
            case EVENT_START: {
                zigbeeControlEventDao.updateDeviceStartTime(statusTime, eventId, deviceId);
                break;
            }
            case EVENT_COMPLETE: {
                zigbeeControlEventDao.updateDeviceStopTime(statusTime, eventId, deviceId, false);
                break;
            }
            case THERMOSTAT_EVENT_CANCELED: {
                zigbeeControlEventDao.updateDeviceStopTime(statusTime, eventId, deviceId, true);
                break;
            }
            case THERMOSTAT_EVENT_SUPERSEDED: {
                zigbeeControlEventDao.updateDeviceStopTime(statusTime, eventId, deviceId, true);
            }
            case INVALID_CANCEL_COMMAND_BAD_EVENT: {
                log.error("Invalid event cancel command was sent to device: "+ deviceId + " Bad event Id: " + eventId);
            }
            default: {
                log.error("Unhandled event_status from ZigBee: " + action.name());
            }
        }
    }

    /**
     * Determines the success of the ping.
     * 
     * @param source
     */
    public ZigbeePingResponse handlePingResponse(String source, ZigbeeDevice endPoint, ZigbeeDevice gateway) {
        Commissioned endPointState = Commissioned.CONNECTED;
        boolean success = true;
        String key = "yukon.web.modules.operator.hardware.refreshSuccessful";
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        String readResponse = template.evaluateAsString("//read_attributes_response");
        
        //Error Case
        if (readResponse == null) {
            success = false;
            String error = template.evaluateAsString("//desc");
            
            if( error == null) {
                error = template.evaluateAsString("//description");
                log.error("Error Communicating with ZigBee EndPoint: " + error);
                if (error.contains("Key not authorized")) {
                    //This error means the encryption key has gotten out of sync. Need a recommission to fix.
                    endPointState = Commissioned.DECOMMISSIONED;
                    key = "yukon.web.modules.operator.hardware.commandFailed.notAuthorized";
                } else {
                    key = "yukon.web.modules.operator.hardware.commandFailed.timeout.endPoint";
                    endPointState = Commissioned.DISCONNECTED;
                }
            } else {
                log.error("Error Communicating with Gateway: " + error);
                //Disconnect gateway (This will also disconnect the end points attached)
                disconnectGateway(gateway);
                key = "yukon.web.modules.operator.hardware.commandFailed.timeout.gateway";
            }
        }
        
        zigbeeServiceHelper.sendPointStatusUpdate(endPoint, 
                                                  BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                  endPointState);
        
        MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(key);
        ZigbeePingResponse response = new ZigbeePingResponse(success,
                                                                                      endPointState,
                                                                                      resolvable);
        
        return response;
    }
    
    /**
     * Determines the success of the add device call.
     * 
     * @param source
     */
    public void evaluateAddDevice(String source) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        String description = template.evaluateAsString("//description");
        
        //Error case
        if (description != null) {
            MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createDefault("yukon.web.modules.operator.hardware.commandFailed", description);
            throw new ZigbeeCommissionException(resolvable);
        }
        
        String desc = template.evaluateAsString("//desc");

        //Error case
        if (desc != null) {
            MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createDefault("yukon.web.modules.operator.hardware.commandFailed", desc);
            throw new ZigbeeCommissionException(resolvable);
        }
    }
    
    /**
     * Sets gateway to connected and sets all EndPoint attached to this gateway to smart poll for their state.
     * 
     * @param gateway
     */
    private void connectGateway(ZigbeeDevice gateway) { 
        //Check to see if we changed state
        PointValueHolder pvh = attributeDynamicDataSource.getPointValue(gateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        Commissioned oldState = PointStateHelper.decodeRawState(Commissioned.class, pvh.getValue());
        
        zigbeeServiceHelper.sendPointStatusUpdate(gateway,
                                                  BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                  Commissioned.CONNECTED);

        if (oldState == Commissioned.DISCONNECTED) {
            //Do this only if we were disconnected before. If no change, do not call.
            List<ZigbeeDevice> devices = gatewayDeviceDao.getAssignedZigbeeDevices(gateway.getZigbeeDeviceId());
            for (ZigbeeDevice device : devices) {
                pvh = attributeDynamicDataSource.getPointValue(device, BuiltInAttribute.ZIGBEE_LINK_STATUS);
                Commissioned devOldState = PointStateHelper.decodeRawState(Commissioned.class, pvh.getValue());
                
                if (devOldState != Commissioned.DECOMMISSIONED) {
                    //Poll is commissioned
                    zigbeeStateUpdaterService.activateSmartPolling(device);
                }
            }
        }
    }
    
    /**
     * Sets gateway to disconnect and also sets disconnect to all EndPoints attached to this gateway.
     * 
     * @param gateway
     */
    private void disconnectGateway(ZigbeeDevice gateway) {
        //Check to see if we changed state
        PointValueHolder pvh = attributeDynamicDataSource.getPointValue(gateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        Commissioned oldState = PointStateHelper.decodeRawState(Commissioned.class, pvh.getValue());
        
        //Only change devices to disconnected if we were connected prior.
        //If we were decommissioned, the devices are not commissioned yet and this would mess up their state.
        boolean changeDevices = oldState == Commissioned.CONNECTED;
        changeGatewayStateWithEndPoints(gateway, Commissioned.DISCONNECTED, changeDevices);
    }
    
    /**
     * Sets gateway to decommissioned and also sets decommissioned to all EndPoints attached to this gateway.
     * 
     * @param gateway
     */
    private void decommissionGateway(ZigbeeDevice gateway) {
        changeGatewayStateWithEndPoints(gateway, Commissioned.DECOMMISSIONED, false);
    }
    
    private void changeGatewayStateWithEndPoints(ZigbeeDevice gateway, Commissioned state, boolean updateStateOfDevices) {
        zigbeeServiceHelper.sendPointStatusUpdate(gateway,
                                                  BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                  state);
        
        if (updateStateOfDevices) {
            List<ZigbeeDevice> devices = gatewayDeviceDao.getAssignedZigbeeDevices(gateway.getZigbeeDeviceId());
            for (ZigbeeDevice device : devices) {
                PointValueHolder pvh = attributeDynamicDataSource.getPointValue(device, BuiltInAttribute.ZIGBEE_LINK_STATUS);
                Commissioned oldState = PointStateHelper.decodeRawState(Commissioned.class, pvh.getValue());
                
                if (oldState != Commissioned.DECOMMISSIONED) {
                    //Change state to match on all gateways if it was commissioned
                    //This is setting the update time as now time.
                    zigbeeServiceHelper.sendPointStatusUpdate(device, 
                                                              BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                              state);
                }
            }
        }
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
    public void setZigbeeControlEventDao(ZigbeeControlEventDao zigbeeControlEventDao) {
        this.zigbeeControlEventDao = zigbeeControlEventDao;
    }
    
    @Autowired
    public void setZigbeeServiceHelper(ZigbeeServiceHelper zigbeeServiceHelper) {
        this.zigbeeServiceHelper = zigbeeServiceHelper;
    }
    
    @Autowired
    public void setZigbeeStateUpdaterService(ZigbeeStateUpdaterService zigbeeStateUpdaterService) {
        this.zigbeeStateUpdaterService = zigbeeStateUpdaterService;
    }
    
    @Autowired
    public void setAttributeDynamicDataSource(AttributeDynamicDataSource attributeDynamicDataSource) {
        this.attributeDynamicDataSource = attributeDynamicDataSource;
    }
}
