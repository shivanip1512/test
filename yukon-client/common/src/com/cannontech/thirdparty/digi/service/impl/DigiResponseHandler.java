package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;

import org.apache.log4j.Logger;
import org.jdom.Namespace;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.model.DeviceCore;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEventAction;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.cannontech.thirdparty.service.ZigbeeServiceHelper;
import com.google.common.collect.Lists;

public class DigiResponseHandler {
  
    private static final Logger log = YukonLogManager.getLogger(DigiResponseHandler.class);
    
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeControlEventDao zigbeeControlEventDao;
    private ZigbeeServiceHelper zigbeeServiceHelper;
    
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
            
            return fileName;
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
            log.info("RECV at "+ time.toDate() + " from iDigi: " + description);
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
    public List<Integer> handleDeviceCoreResponse(String source, List<ZigbeeDevice> expected) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        List<DeviceCore> cores = template.evaluate("/result/DeviceCore", digiDeviceCoreNodeMapper);
        
        List<Integer> updatedDeviceIds = Lists.newArrayList();
        for (DeviceCore core : cores) {
            DigiGateway digiGateway =  gatewayDeviceDao.getDigiGateway(core.getDevMac());        
            digiGateway.setDigiId(core.getDevId());
            digiGateway.setFirmwareVersion(core.getDevFirmware());
            gatewayDeviceDao.updateDigiGateway(digiGateway);

            updatedDeviceIds.add(digiGateway.getPaoId());
            
            zigbeeServiceHelper.sendPointStatusUpdate(digiGateway,
                                                      BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                      Commissioned.COMMISSIONED);
            
            if (core.isConnected()) {
                zigbeeServiceHelper.sendPointStatusUpdate(digiGateway,
                                                      BuiltInAttribute.ZIGBEE_CONNECTION_STATUS,
                                                      CommStatusState.CONNECTED);
            } else {
                zigbeeServiceHelper.sendPointStatusUpdate(digiGateway,
                                                          BuiltInAttribute.ZIGBEE_CONNECTION_STATUS,
                                                          CommStatusState.DISCONNECTED);
                
                //Disconnect all devices on the gateway
                List<ZigbeeDevice> devices = gatewayDeviceDao.getAssignedZigbeeDevices(digiGateway.getPaoId());
                for (ZigbeeDevice device : devices) {
                    zigbeeServiceHelper.sendPointStatusUpdate(device, 
                                                              BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                              CommStatusState.DISCONNECTED);
                }
            }
        }
        
        //Set Decommissioned to anything we did not get a response from.
        for (ZigbeeDevice gateway : expected) {
            if (updatedDeviceIds.contains(gateway.getZigbeeDeviceId())) {
                continue;
            }
            
            zigbeeServiceHelper.sendPointStatusUpdate(gateway,
                                                      BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                      Commissioned.DECOMMISSIONED);
            
            //Disconnect all devices on the gateway
            List<ZigbeeDevice> devices = gatewayDeviceDao.getAssignedZigbeeDevices(gateway.getZigbeeDeviceId());
            for (ZigbeeDevice device : devices) {
                zigbeeServiceHelper.sendPointStatusUpdate(device, 
                                                          BuiltInAttribute.ZIGBEE_LINK_STATUS, 
                                                          Commissioned.DECOMMISSIONED);
            }
        }
        
        return updatedDeviceIds;
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
            ZigbeeThermostat utilPro = zigbeeDeviceDao.getZigbeeUtilProByMACAddress(macAddress);
            //We found a MAC, so lets do something..
            if (message.contains(commissionStr)) {
                //Commissioned
                log.debug("Device: " + macAddress + " was commissioned.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                                            Commissioned.COMMISSIONED);
                return;
                
            } else if ( message.contains(decommissionStr)) {
                //DeCommissioned
                log.debug("Device: " + macAddress + " was decommissioned.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                                            Commissioned.DECOMMISSIONED);
                return;
                
            } else if ( message.contains(connectStr) || message.contains(connectStr2)) {
                //Connected
                log.debug("Device: " + macAddress + " has connected.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_CONNECTION_STATUS,
                                                                            CommStatusState.CONNECTED);
                
                if (message.contains(connectStr)) {
                    //"Received device announce message from known device 00:0C:C1:00:27:19:C4:D1 (NWK: E0BB)"
                    m = nodeAddrPattern.matcher(message);
                    
                    if (m.find()) {
                        int nodeId = Integer.parseInt(m.group(1), 16);
                        
                        utilPro.setNodeId(nodeId);
                        zigbeeDeviceDao.updateZigbeeUtilPro(utilPro);
                    } else {
                        log.warn("NodeId was not in the reponse.");
                    }
                }
                
                return;
                
            } else if ( message.contains(disconnectStr)) {
                //Disconnected
                log.debug("Device: " + macAddress + " has disconnected.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_CONNECTION_STATUS,
                                                                            CommStatusState.DISCONNECTED);
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
    public void handlePingResponse(String source, ZigbeeDevice endPoint, ZigbeeDevice gateway){
        CommStatusState endPointState = CommStatusState.CONNECTED;

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        String readResponse = template.evaluateAsString("//read_attributes_response");
        
        //Error Case
        if (readResponse == null) {
            String error = template.evaluateAsString("//desc");
            
            if( error == null) {
                error = template.evaluateAsString("//description");
                log.error("Error Communicating with ZigBee EndPoint: " + error);
                endPointState = CommStatusState.DISCONNECTED;
            } else {
                log.error("Error Communicating with Gateway: " + error);
                endPointState = CommStatusState.DISCONNECTED;
            }
        }
        
        zigbeeServiceHelper.sendPointStatusUpdate(endPoint, 
                                                  BuiltInAttribute.ZIGBEE_CONNECTION_STATUS, 
                                                  endPointState);
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
            throw new DigiWebServiceException(description);
        }
        
        String desc = template.evaluateAsString("//desc");

        //Error case
        if (desc != null) {
            throw new DigiWebServiceException(desc);
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
    
}
