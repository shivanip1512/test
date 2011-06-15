package com.cannontech.thirdparty.service.impl;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;
import javax.xml.transform.stream.StreamSource;

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
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.impl.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.exception.DigiEmptyDeviceCoreResultException;
import com.cannontech.thirdparty.exception.DigiEndPointDisconnectedException;
import com.cannontech.thirdparty.exception.DigiGatewayDisconnectedException;
import com.cannontech.thirdparty.exception.DigiWebServiceException;
import com.cannontech.thirdparty.model.DeviceCore;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEventAction;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.cannontech.thirdparty.service.ZigbeeServiceHelper;

public class DigiResponseHandler {
  
    private static final Logger logger = YukonLogManager.getLogger(DigiResponseHandler.class);
    
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private GatewayDeviceDao gatewayDeviceDao;
    private ZigbeeControlEventDao digiControlEventDao;
    private ZigbeeServiceHelper zigbeeServiceHelper;
    
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

            int devId = template.evaluateAsInt("//devId");
            String devMac = template.evaluateAsString("//devMac");

            int connectionStatus = template.evaluateAsInt("//dpConnectionStatus");
            boolean connected = connectionStatus==1?true:false;
            
            String devFirmware = template.evaluateAsString("//dpFirmwareLevelDesc");
            String lastTimeStr = template.evaluateAsString("//dpLastConnectTime");
            
            //If the gateway has not been connected yet, this field may be null
            if (devFirmware == null) {
                devFirmware = CtiUtilities.STRING_NONE;
            }
            
            //Even if lastTimeStr is null, this will just create an Instant to now.
            Instant instant = new Instant(lastTimeStr);
            
            return new DeviceCore(devId,devMac,devFirmware,connected,instant);
        }
    };
    
    /**
     * Returns a list of paths to DeviceNotifications that are stored on the gateway. 
     * 
     * @return
     */
    public List<String> handleFolderListingResponse(StreamSource source) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        template.setNamespaces(existProperties);
 
        List<String> fileNames = template.evaluate("//exist:resource", digiFileListingNodeMapper);
        
        return fileNames;
    }
    
    /**
     * Processes the DeviceNotification contained in the response.
     * 
     * @param source
     * @throws UnsupportedDataTypeException 
     */
    public void handleDeviceNotification(StreamSource source) throws UnsupportedDataTypeException {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        //Figure out what this file is
        Node tester = template.evaluateAsNode("/received_report_event_status");
        if (tester != null) {
            logger.info("Processing XML file of type: 'received_report_event_status message'.");
            processReportEventStatus(template);
            return;
        }

        tester = template.evaluateAsNode("/message");
        if (tester != null) {
            logger.info("Processing XML file of type: 'message'.");
            String description = template.evaluateAsString("//description");
            logger.info("RECV from iDigi: " + description);
            parseMessageForAction(description);
            return;
        }
        
        throw new UnsupportedDataTypeException("Unsupported XML file type returned from iDigi.");
    }

    /**
     * Parse for the connection status of the gateway contained in source.
     * 
     * @param source
     */
    public void handleDeviceCoreResponse(StreamSource source) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        List<DeviceCore> cores = template.evaluate("//DeviceCore", digiDeviceCoreNodeMapper);
        
        //Make sure we got a result
        if (cores.isEmpty()) {
            throw new DigiEmptyDeviceCoreResultException("0 results from DeviceCore query.");
        }
        
        for (DeviceCore core : cores) {
            DigiGateway digiGateway =  gatewayDeviceDao.getDigiGateway(core.getDevMac());        
            digiGateway.setDigiId(core.getDevId());
            digiGateway.setFirmwareVersion(core.getDevFirmware());
            gatewayDeviceDao.updateDigiGateway(digiGateway);
            
            if (core.isConnected()) {
                zigbeeServiceHelper.sendPointStatusUpdate(digiGateway,
                                                      core.getLastTime(),
                                                      BuiltInAttribute.CONNECTION_STATUS,
                                                      CommStatusState.CONNECTED);
                
            } else {
                zigbeeServiceHelper.sendPointStatusUpdate(digiGateway,
                                                          BuiltInAttribute.CONNECTION_STATUS,
                                                          CommStatusState.DISCONNECTED);
                
                //Disconnect all devices on the gateway
                List<ZigbeeDevice> devices = gatewayDeviceDao.getAssignedZigbeeDevices(digiGateway.getPaoId());
                for (ZigbeeDevice device : devices) {
                    zigbeeServiceHelper.sendPointStatusUpdate(device, 
                                                              BuiltInAttribute.CONNECTION_STATUS, 
                                                              CommStatusState.DISCONNECTED);
                }
                
            }
        }
    }
    
    /**
     * The messages we get from digi have no Type indicators. It is just a raw String.. making due with what we get..
     * 
     * @param message
     */
    private void parseMessageForAction(String message) {
        //Try to determine what this message is
        String regexForMac = "([\\da-fA-F]{2}:){7}[\\da-fA-F]{2}";
        String commissionStr = " registered with XBee";
        String decommissionStr = " unregistered from XBee";
        String connectStr = "Received device announce message from known device";
        String connectStr2 = "detected and marked as active";
        String disconnectStr = "marked as inactive";
        
        String in = message;
        Pattern p = Pattern.compile(regexForMac);
        Matcher m = p.matcher(in);
       
        if (m.find()) {
            String macAddress = m.group();
            ZigbeeThermostat utilPro = zigbeeDeviceDao.getZigbeeUtilProByMACAddress(macAddress);
            //We found a MAC, so lets do something..
            if (message.contains(commissionStr)) {
                //Commissioned
                logger.debug("Device: " + macAddress + "was commissioned.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                                            Commissioned.COMMISSIONED);
                return;
                
            } else if ( message.contains(decommissionStr)) {
                //DeCommissioned
                logger.debug("Device: " + macAddress + "was decommissioned.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                                            Commissioned.DECOMMISSIONED);
                return;
                
            } else if ( message.contains(connectStr) || message.contains(connectStr2)) {
                //Connected
                logger.debug("Device: " + macAddress + "has connected.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,BuiltInAttribute.CONNECTION_STATUS,
                                                                            CommStatusState.CONNECTED);
                
                if (message.contains(connectStr)) {
                    //"Received device announce message from known device 00:0C:C1:00:27:19:C4:D1 (NWK: E0BB)"
                    String regexNodeId = "NWK: ([\\da-fA-F]{4})";
                    p = Pattern.compile(regexNodeId);
                    m = p.matcher(in);
                    
                    if (m.find()) {
                        String nodeIdStr = "0x" + m.group(1);
                        int nodeId = Integer.decode(nodeIdStr);
                        
                        utilPro.setNodeId(nodeId);
                        zigbeeDeviceDao.updateZigbeeUtilPro(utilPro);
                    } else {
                        logger.warn("NodeId was not in the reponse.");
                    }
                }
                
                return;
                
            } else if ( message.contains(disconnectStr)) {
                //Disconnected
                logger.debug("Device: " + macAddress + "has disconnected.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,BuiltInAttribute.CONNECTION_STATUS,
                                                                            CommStatusState.DISCONNECTED);
                return;
            }
            
            logger.debug("No Action found in current message for device: " + macAddress);
            return;
        }
        
        logger.debug("No device detected in Message. No actions to performed");
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
        logger.info("event_status: " + action.name());
        
        switch (action) {
            case THERMOSTAT_ACK: {
                digiControlEventDao.updateDeviceAck(true, eventId, deviceId);
                break;
            }
            case EVENT_START: {
                digiControlEventDao.updateDeviceStartTime(statusTime, eventId, deviceId);
                break;
            }
            case EVENT_COMPLETE: {
                digiControlEventDao.updateDeviceStopTime(statusTime, eventId, deviceId, false);
                break;
            }
            case THERMOSTAT_EVENT_CANCELED: {
                digiControlEventDao.updateDeviceStopTime(statusTime, eventId, deviceId, true);
                break;
            }
            case THERMOSTAT_EVENT_SUPERSEDED: {
                digiControlEventDao.updateDeviceStopTime(statusTime, eventId, deviceId, true);
            }
            case INVALID_CANCEL_COMMAND_BAD_EVENT: {
                logger.info("Invalid event cancel command was sent to device: "+ deviceId + " Bad event Id: " + eventId);
            }
            default: {
                logger.info("Unhandled event_status from ZigBee: " + action.name());
            }
        }
    }

    /**
     * Determines the success of the ping.
     * 
     * @param source
     */
    public void evaluatePingResponse(StreamSource source){
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        String readResponse = template.evaluateAsString("//read_attributes_response");
        //Error Case
        if (readResponse == null) {
            String error = template.evaluateAsString("//desc");
            
            if( error == null) {
                String errorSource = "Error Communicating with ZigBee EndPoint";
                logger.error("Error Communicating with ZigBee EndPoint ");
                error = template.evaluateAsString("//description");
                throw new DigiEndPointDisconnectedException( errorSource + ": " + error);
            } else {
                String errorSource = "Error Communicating with Gateway";
                throw new DigiGatewayDisconnectedException(errorSource + ": " + error);
            }
        }
    }
    
    /**
     * Determines the success of the add device call.
     * 
     * @param source
     */
    public void evaluateAddDevice(StreamSource source) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        String description = template.evaluateAsString("//description");
        
        //Error case
        if (description != null) {
            throw new DigiWebServiceException(description);
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
    public void setDigiControlEventDao(ZigbeeControlEventDao digiControlEventDao) {
        this.digiControlEventDao = digiControlEventDao;
    }
    
    @Autowired
    public void setZigbeeServiceHelper(ZigbeeServiceHelper zigbeeServiceHelper) {
        this.zigbeeServiceHelper = zigbeeServiceHelper;
    }
    
}
