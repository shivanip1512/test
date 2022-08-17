package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;
import javax.jms.ConnectionFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.jdom2.Namespace;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.dr.dao.SepReportedAddress;
import com.cannontech.dr.dao.SepReportedAddressDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiKeyNotAuthorizedException;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.model.DevConnectwareId;
import com.cannontech.thirdparty.digi.model.DeviceCore;
import com.cannontech.thirdparty.digi.model.FileData;
import com.cannontech.thirdparty.digi.model.MacAddress;
import com.cannontech.thirdparty.digi.model.NodeAddress;
import com.cannontech.thirdparty.digi.model.NodeStatus;
import com.cannontech.thirdparty.digi.model.NodeType;
import com.cannontech.thirdparty.digi.model.XbeeCore;
import com.cannontech.thirdparty.digi.service.errors.ZigbeePingResponse;
import com.cannontech.thirdparty.exception.ZigbeeCommissionException;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.model.SEPAttributeValue;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;
import com.cannontech.thirdparty.model.ZigbeeEventAction;
import com.cannontech.thirdparty.service.ZigbeeServiceHelper;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DigiResponseHandler {
  
    private static final Logger log = YukonLogManager.getLogger(DigiResponseHandler.class);
    private static final LogHelper logHelper = YukonLogManager.getLogHelper(DigiResponseHandler.class);
    
    @Autowired private ZigbeeDeviceDao zigbeeDeviceDao;
    @Autowired private GatewayDeviceDao gatewayDeviceDao;
    @Autowired private ZigbeeControlEventDao zigbeeControlEventDao;
    @Autowired private ZigbeeServiceHelper zigbeeServiceHelper;
    @Autowired private ZigbeeStateUpdaterService zigbeeStateUpdaterService;
    @Autowired private AttributeDynamicDataSource attributeDynamicDataSource;
    @Autowired private SepReportedAddressDao sepReportedAddressDao;

    private JmsTemplate jmsTemplate;
    private static String regexForMac = "([\\da-fA-F]{2}:){7}[\\da-fA-F]{2}";
    private static Pattern macPattern = Pattern.compile(regexForMac);
    private static String regexNodeId = "NWK: ([\\da-fA-F]{4})";
    private static Pattern nodeAddrPattern = Pattern.compile(regexNodeId);
    
    private static final Namespace existNamespace = Namespace.getNamespace("exist", "http://exist.sourceforge.net/NS/exist");
    private static Properties existProperties = new Properties();
    static {
        existProperties.put(existNamespace.getPrefix(), existNamespace.getURI());
    }
    
    private static ObjectMapper<Node, FileData> digiFileListingNodeMapper = new ObjectMapper<Node,FileData>() {

        @Override
        public FileData map(Node node) throws DOMException {                            
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);

            String fdType = template.evaluateAsString("fdType");
            String fdData = template.evaluateAsString("fdData");
            String fdName = template.evaluateAsString("id/fdName");
            String fdPath = template.evaluateAsString("id/fdPath");

            FileData data = new FileData();

            data.setFdData(fdData);
            data.setFdName(fdName);
            data.setFdPath(fdPath);
            data.setFdType(fdType);
            
            return data;
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

    private static ObjectMapper<Node, XbeeCore> digiXbeeCoreNodeMapper = new ObjectMapper<Node,XbeeCore>() {

        @Override
        public XbeeCore map(Node node) throws DOMException {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);

            String temp = template.evaluateAsString("xpExtAddr");
            MacAddress macAddress = new MacAddress(temp);
            
            temp = template.evaluateAsString("devConnectwareId");
            DevConnectwareId devConnectwareId = new DevConnectwareId(temp);
            
            int xpNetAddr = template.evaluateAsInt("xpNetAddr");
            NodeAddress nodeId = new NodeAddress(xpNetAddr);
            
            int xpNodeType = template.evaluateAsInt("xpNodeType");
            NodeType nodeType = NodeType.getNodeType(xpNodeType);
            
            int xpStatus = template.evaluateAsInt("xpStatus");
            NodeStatus nodeStatus = NodeStatus.getNodeStatus(xpStatus);
            
            String updateTime = template.evaluateAsString("xpUpdateTime");
            Instant xpUpdateTime = new Instant(updateTime);
            
            return new XbeeCore(macAddress,devConnectwareId,nodeId,nodeType,nodeStatus,xpUpdateTime);
        }
    };
    
    private static ObjectMapper<Node, SEPAttributeValue> digiAttributeNodeMapper = new ObjectMapper<Node,SEPAttributeValue>() {

        @Override
        public SEPAttributeValue map(Node node) throws DOMException {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);

            /*
                <item type="ReadAttributeStatusRecord">
                    <status type="int">0x0</status>
                    <attribute_type type="int">0x20</attribute_type>
                    <attribute_id type="int">0x0</attribute_id>
                    <value type="int">0x3</value>
                </item>
             */
            
            String attributeIdStr = template.evaluateAsString("attribute_id");
            int attributeId = Integer.decode(attributeIdStr);
            DRLCClusterAttribute attribute = DRLCClusterAttribute.getForId(attributeId);
            
            String valueStr = template.evaluateAsString("value");
            int value = Integer.decode(valueStr);
            return new SEPAttributeValue(attribute,value);
        }
    };
    
    /**
     * Returns a list of paths to DeviceNotifications that are stored on the gateway. 
     * 
     * @return
     */
    public List<FileData> handleFolderListingResponse(String source) {
        List<FileData> fileNodes = Lists.newArrayList();
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        if (template.evaluateAsString("/wsError") != null) {
           int code = template.evaluateAsInt("/wsError/error/code");
           String message = template.evaluateAsString("/wsError/error/message");
           String hint = template.evaluateAsString("/wsError/error/hint");
           log.error("Error Polling Gateway: " + code + " " + hint + ". " + message);
        } else { 
            fileNodes.addAll(template.evaluate("/result/FileData",digiFileListingNodeMapper));
        }
        return fileNodes;
    }
    
    /**
     * Processes the DeviceNotification contained in the response.
     * 
     * @param source
     * @throws UnsupportedDataTypeException 
     */
    public void handleDeviceNotification(FileData fileData) throws UnsupportedDataTypeException {

        if(!"file".equals(fileData.getFdType())) {
            log.debug("Skipping listing of type: " + fileData.getFdType());
            return;
        }
        
        String fileName = fileData.getFdName(); 
        log.debug("Processing device notification file: " + fileName);
        
        byte [] newData = Base64.decodeBase64(fileData.getFdData().getBytes()); 
        String data = new String(newData);
        
        log.debug(data);
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(data);

        //Figure out what this file is and process it.
        try {
            Node tester = template.evaluateAsNode("/write_attributes_response");
            if (tester != null) {
                //We don't need to do anything, this is just an acknowledge.
                log.debug("\"write attribute response\" received.");
                return;
            }
            
            tester = template.evaluateAsNode("/received_report_event_status");
            if (tester != null) {
                log.debug("Processing XML file of type: 'received_report_event_status message'.");
                processReportEventStatus(data);
                return;
            }
    
            tester = template.evaluateAsNode("/message");
            if (tester != null) {
                log.debug("Processing XML file of type: 'message'.");
                
                Long seconds = template.evaluateAsLong("//@timestamp");
                Instant time = new Instant(seconds*1000);
                
                String description = template.evaluateAsString("//description");
                log.debug("RECV at "+ time + " from iDigi: " + description);
                
                parseMessageForAction(time,description);
                return;
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Unknown EndPoint during Device Notification processing.");
            //Normally I would "debug" log the Cause of the error here, but it was printed above: log.debug(data);
            return;
        }
        
        throw new UnsupportedDataTypeException("Unsupported XML file type returned from iDigi.");
    }

    /**
     * Parse for the connection status of the gateway contained in source.
     * 
     * returns a list of PaoIds that were updated.
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
            ZigbeeDevice digiGateway;
            try {
                digiGateway =  gatewayDeviceDao.getDigiGateway(core.getDevMac());
            } catch ( EmptyResultDataAccessException e) {
                log.warn("Unknown Gateway during DeviceCore refresh: MAC: " + core.getDevMac() + " DevId: " + core.getDevId());
                continue;
            }
            
            gatewayDeviceDao.updateDigiId(digiGateway.getPaoIdentifier(), core.getDevId());
            gatewayDeviceDao.updateFirmwareVersion(digiGateway.getPaoIdentifier(), core.getDevFirmware());

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
            ZigbeePingResponse response = new ZigbeePingResponse(true, state, resolvable);
            pingResponses.put(digiGateway.getPaoIdentifier(), response);
        }
        
        //Set Decommissioned to anything we did not get a response from.
        MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(successKey);
        ZigbeePingResponse decommissionedResponse = new ZigbeePingResponse(true,
                                                             Commissioned.DECOMMISSIONED,
                                                             resolvable);

        for (ZigbeeDevice gateway : expected) {
            if (pingResponses.keySet().contains(gateway.getPaoIdentifier())) {
                continue;
            }
            pingResponses.put(gateway.getPaoIdentifier(),decommissionedResponse);
            decommissionGateway(gateway);
        }
        
        return pingResponses;
    }
    
    public void handleLoadGroupAddressingRead(String response, ZigbeeDevice endPoint, ZigbeeDevice gateway) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(response);
              
        /* Success Case
        <sci_reply version="1.0">
            <send_message>
                <device id="00000000-00000000-00409DFF-FF3D7221">
                    <rci_reply version="1.1">
                        <do_command target="RPC_request">
                            <responses remaining="0" timestamp="1351008766.0">
                                <read_attributes_response timestamp="1351008765.0">
                                    <source_endpoint_id type="int">0x1</source_endpoint_id>
                                    <server_or_client type="int">0x1</server_or_client>
                                    <record_list type="list">
                                        <item type="ReadAttributeStatusRecord">
         */
        List<SEPAttributeValue> attibuteValues = template.evaluate("/sci_reply/send_message/device/rci_reply/do_command/responses/read_attributes_response/record_list/item", 
                                                                   digiAttributeNodeMapper);

        if (attibuteValues.isEmpty()) {
            //Checking for Error
            /* Error case:
                <sci_reply version="1.0">
                <send_message>
                    <device id="00000000-00000000-00409DFF-FF3D7221">
                        <rci_reply version="1.1">
                            <do_command target="RPC_request">
                                <responses remaining="0" timestamp="1351008382.0">
                                    <message timestamp="1351008382.0">
                                        <description type="string">Error during conversation with
                                            (00:0C:C1:00:27:19:C4:D5, 0x1, 0x109, 0x701): Conversation received
                                            unsuccessful TX status: 0xBB, Key not authorized
                                        </description>
             */
            String errorString = template.evaluateAsString("/sci_reply/send_message/device/rci_reply/do_command/responses/message/description");
            log.error(errorString);
            
            if (errorString.contains("Key not authorized")) {
                throw new DigiKeyNotAuthorizedException(errorString);
            }
            throw new DigiWebServiceException("Unknown error reading attributes from iDigi.");
        }
        
        String timestampStr = template.evaluateAsString("/sci_reply/send_message/device/rci_reply/do_command/responses/read_attributes_response/@timestamp");
        long seconds = Long.decode(timestampStr.split("\\.")[0]);
        Instant timestamp = new Instant(seconds*1000);

        SepReportedAddress address = new SepReportedAddress();
        address.setTimestamp(timestamp);
        
        address.setDeviceId(endPoint.getZigbeeDeviceId());
        for (SEPAttributeValue value : attibuteValues) {
            switch (value.getAttribute()) {
            case UTILITY_ENROLLMENT_GROUP:
                address.setUtilityEnrollmentGroup(value.getValue());
                break;
            case START_RANDOMIZE_MINUTES:
                address.setRandomStartTimeMinutes(value.getValue());
                break;
            case STOP_RANDOMIZE_MINTES:
                address.setRandomStopTimeMinutes(value.getValue());
                break;
            case DEVICE_CLASS:
                address.setDeviceClass(value.getValue());
                break;
            default:
                //Not throwing. Can't happen. The node mapper would have thrown converting the ID to an enum
            }
        }
        
        
        logHelper.debug("Received LM Address for %s - " + address, endPoint.getName());
        sepReportedAddressDao.save(address);
        
        jmsTemplate.convertAndSend(JmsApiDirectory.LM_ADDRESS_NOTIFICATION.getQueue().getName(), address);
    }
    
    public Map<PaoIdentifier,ZigbeePingResponse> handleXbeeCoreResponse(String source, List<ZigbeeDevice> expected) {        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        String successKey = "yukon.web.modules.operator.hardware.refreshSuccessful";
        List<XbeeCore> cores = template.evaluate("/result/XbeeCore", digiXbeeCoreNodeMapper);
        
        Map<PaoIdentifier,ZigbeePingResponse> xbeeCoreResponses = Maps.newHashMap();
        for (XbeeCore core : cores) {
            ZigbeeEndpoint endPoint;
            try {
                endPoint = zigbeeDeviceDao.getZigbeeEndPointByMACAddress(core.getMacAddress().getMacAddress());
                
                Commissioned state = Commissioned.DISCONNECTED;
                if (core.getNodeStatus().isConnected()) {
                    state = Commissioned.CONNECTED;
                }

                zigbeeServiceHelper.sendPointStatusUpdate(endPoint,
                                                          BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                          state);

                MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(successKey);
                ZigbeePingResponse response = new ZigbeePingResponse(true, state, resolvable);
                xbeeCoreResponses.put(endPoint.getPaoIdentifier(), response);
            } catch (EmptyResultDataAccessException e) {
                //This is either not a end point or not tracked by yukon.
                log.warn("Unknown EndPoint during XbeeCore refresh: " + core.getMacAddress().getMacAddress());
            }
        }
        
        ZigbeePingResponse decommissionedResponse = new ZigbeePingResponse(true,
                                                             Commissioned.DECOMMISSIONED,
                                                             YukonMessageSourceResolvable.createSingleCode(successKey));
        
        //Look for devices we expected to get something for but did not. Set them to decommissioned since
        // that is the only way for this to happen.
        for (ZigbeeDevice endPoint : expected) {
            if (xbeeCoreResponses.keySet().contains(endPoint.getPaoIdentifier())) {
                continue;
            }
            xbeeCoreResponses.put(endPoint.getPaoIdentifier(),decommissionedResponse);
            zigbeeServiceHelper.sendPointStatusUpdate(endPoint,
                                                      BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                      Commissioned.DECOMMISSIONED);
        }
        
        return xbeeCoreResponses;
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
            //This will throw EmptyResultSetException is there is no MAC, to be handled by caller, not here.
            ZigbeeEndpoint utilPro = zigbeeDeviceDao.getZigbeeEndPointByMACAddress(macAddress);
            
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
                //This is the device announce. We are truly commissioned now. 
                //So move to "Disconnected" and wait for the next message. 'Device marked active' 
                //During the time from this message and the next, the device is booting up, so we must not go connected until that is complete or pings and commands will unexpectedly fail
                
                log.debug("Device: " + macAddress + " has been commissioned.");
                zigbeeServiceHelper.sendPointStatusUpdate(utilPro,time,BuiltInAttribute.ZIGBEE_LINK_STATUS,
                                                          Commissioned.DISCONNECTED);
                
                //"Received device announce message from known device 00:0C:C1:00:27:19:C4:D1 (NWK: E0BB)"
                m = nodeAddrPattern.matcher(message);
                
                if (m.find()) {
                    int nodeId = Integer.parseInt(m.group(1), 16);
                    
                    utilPro.setNodeId(nodeId);
                    
                    zigbeeDeviceDao.updateNodeId(utilPro.getPaoIdentifier(), nodeId);
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
    
    private void processReportEventStatus(String xmlData) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(xmlData);
        
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
            MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createDefaultWithArguments("yukon.web.modules.operator.hardware.commandFailed", description, description);
            throw new ZigbeeCommissionException(resolvable);
        }
        
        String desc = template.evaluateAsString("//desc");

        //Error case
        if (desc != null) {
            MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createDefaultWithArguments("yukon.web.modules.operator.hardware.commandFailed", desc, desc);
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
        try {
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
        } catch (DigiNotConfiguredException e) {
            log.warn("Digi not configured", e);
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
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
    
}