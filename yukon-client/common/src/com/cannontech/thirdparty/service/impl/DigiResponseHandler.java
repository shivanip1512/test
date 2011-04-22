package com.cannontech.thirdparty.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.xml.xpath.NodeMapper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.database.db.point.stategroup.PointState;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.impl.DigiControlEventDao;
import com.cannontech.thirdparty.digi.model.GatewayResponse;
import com.cannontech.thirdparty.model.ZigbeeEventAction;
import com.cannontech.thirdparty.model.ZigbeeThermostat;

public class DigiResponseHandler {
  
    private static final Logger logger = YukonLogManager.getLogger(DigiResponseHandler.class);
    
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private DigiControlEventDao digiControlEventDao;
    private SimplePointAccessDao simplePointAccessDao;
    private AttributeService attributeService;
    
    private static final Namespace existNamespace = Namespace.getNamespace("exist", "http://exist.sourceforge.net/NS/exist");
    private static Properties existProperties = new Properties();
    static {
        existProperties.put(existNamespace.getPrefix(), existNamespace.getURI());
    }
    
    private static NodeMapper digiSEPControlResponseNodeMapper = new NodeMapper() {

        @Override
        public Object mapNode(Node node, int nodeNum) throws DOMException {                
            GatewayResponse response = new GatewayResponse();
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
                String eventId = template.evaluateAsString("rci_reply/do_command/responses/create_DRLC_event_response/record/issuer_event_id");
                
                response.setDeviceList(zbDeviceIds);
                response.setStatus(Integer.parseInt(statusStr.substring(2),16));
                response.setEventId(Integer.parseInt(eventId.substring(2),16));
            }
            
            return response;
        }
    };
    
    private static NodeMapper digiFileListingNodeMapper = new NodeMapper() {

        @Override
        public Object mapNode(Node node, int nodeNum) throws DOMException {                            
            String fileName = node.getAttributes().getNamedItem("name").getNodeValue();
            
            return fileName;
        }
    };
    
    /**
     * Parse through the XML response to the SEP Control message.
     * 
     * Log events for each device we heard about from the gateway.
     * 
     * @param source
     * @return
     */
    public void handleSEPControlResponses(StreamSource source) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        
        @SuppressWarnings("unchecked")
        List<GatewayResponse> gatewayResponses = template.evaluate("//device", digiSEPControlResponseNodeMapper);

        Date now = new Date();
        for (GatewayResponse response : gatewayResponses) {
            if (response.hasError())
                continue;

            List<Integer> deviceIds = zigbeeDeviceDao.getDeviceIdsForMACAddresses(response.getDeviceList());
            //Throw if we can't find the paoIds
            for (Integer deviceId:deviceIds) {
                digiControlEventDao.insertControlEvent(response.getEventId(),
                                                                     now,
                                                                     deviceId,
                                                                     ZigbeeEventAction.GATEWAY_ACK);
            }
        }
    }
    
    /**
     * Returns a list of paths to DeviceNotifications that are stored on the gateway. 
     * 
     * @return
     */
    public List<String> handleFolderListingResponse(StreamSource source) {
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(source);
        template.setNamespaces(existProperties);
 
        @SuppressWarnings("unchecked")
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
        String disconnectStr = "marked as inactive";
        
        String in = message;
        Pattern p = Pattern.compile(regexForMac);
        Matcher m = p.matcher(in);
       
        if (m.find()) {
            String macAddress = m.group();
            //We found a MAC, so lets do something..
            if (message.contains(commissionStr)) {
                //Commissioned
                logger.debug("Device: " + macAddress + "was commissioned.");
                sendPointStatusUpdate(macAddress,BuiltInAttribute.ZIGBEE_LINK_STATUS,Commissioned.COMMISSIONED);
                return;
            } else if ( message.contains(decommissionStr)) {
                //DeCommissioned
                logger.debug("Device: " + macAddress + "was decommissioned.");
                sendPointStatusUpdate(macAddress,BuiltInAttribute.ZIGBEE_LINK_STATUS,Commissioned.DECOMMISSIONED);
                return;
            } else if ( message.contains(connectStr)) {
                //Connected
                logger.debug("Device: " + macAddress + "has connected.");
                sendPointStatusUpdate(macAddress,BuiltInAttribute.CONNECTION_STATUS,CommStatusState.CONNECTED);
                return;
            } else if ( message.contains(disconnectStr)) {
                //Disconnected
                logger.debug("Device: " + macAddress + "has disconnected.");
                sendPointStatusUpdate(macAddress,BuiltInAttribute.CONNECTION_STATUS,CommStatusState.DISCONNECTED);
                return;
            }
            
            logger.debug("No Action found in current message for device: " + macAddress);
            return;
        }
        
        logger.debug("No device detected in Message. No actions to performed");
        return;
    }
    
    private void sendPointStatusUpdate(String macAddress, BuiltInAttribute attribute, PointState pointState) {
        ZigbeeThermostat utilPro = zigbeeDeviceDao.getZigbeeUtilProByMACAddress(macAddress);
        LitePoint point = attributeService.getPointForAttribute(utilPro, attribute);
        simplePointAccessDao.setPointValue(point, pointState);
    }
    
    private void processReportEventStatus(SimpleXPathTemplate template) {        
        String temp = template.evaluateAsString("//event_status_time");
        long seconds = Long.parseLong(temp.substring(2),16);
        Date statusTime = new Date(seconds*1000);
        
        String macAddress = template.evaluateAsString("//source_address");
        int deviceId = zigbeeDeviceDao.getDeviceIdForMACAddress(macAddress);
        
        temp = template.evaluateAsString("//event_status");
        int eventStatus = Integer.parseInt(temp.substring(2), 16);
        ZigbeeEventAction action = ZigbeeEventAction.getForEventStatus(eventStatus);        
        
        temp = template.evaluateAsString("//issuer_event_id");
        int eventId = Integer.parseInt(temp.substring(2), 16);
        
        digiControlEventDao.insertControlEvent(eventId,
                                               statusTime,
                                               deviceId,
                                               action);    
    }
    
    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
    @Autowired
    public void setDigiControlEventDao(DigiControlEventDao digiControlEventDao) {
        this.digiControlEventDao = digiControlEventDao;
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
