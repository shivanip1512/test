package com.cannontech.yukon.api.stars.endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.activemq.YukonTextMessageDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.yukon.api.stars.endpoint.endpointMappers.TextMessageElementRequestMapper;
import com.cannontech.yukon.api.stars.model.DeviceTextMessage;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Endpoint
public class SendTextMessageEndpoint {

    @Autowired private InventoryDao inventoryDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private YukonTextMessageDao yukonTextMessageDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ThermostatService thermostatService;

    // @Autowired by setter
    private JmsTemplate jmsTemplate;

    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(SendTextMessageEndpoint.class);

    private static final String MESSAGE_PATTERN = "[\\w!#$%&'()*+,-./:;=?@[\\\\]^_`\\{\\|\\}~ ]+";
    private Pattern pattern = Pattern.compile(MESSAGE_PATTERN);

    @PostConstruct
    public void initialize() throws JDOMException {
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "sendTextMessageRequest")
    public Element invoke(Element sendTextMessage, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(sendTextMessage,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(sendTextMessage);

        // init response
        Element resp = new Element("sendTextMessageResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);

        try {

            DeviceTextMessage deviceTextMessage =
                requestTemplate.evaluateAsObject("//y:textMessage",
                                      new NodeToElementMapperWrapper<DeviceTextMessage>(new TextMessageElementRequestMapper()));

            YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
            
            // Check authorization
            rolePropertyDao.verifyRole(YukonRole.INVENTORY, user);

            //Validate text message
            Matcher matcher = pattern.matcher(deviceTextMessage.getMessage());
            if (matcher.matches()) {
                Map<String, Integer> serialNumberToInventoryIdMap =
                    inventoryDao.getSerialNumberToInventoryIdMap(deviceTextMessage.getSerialNumbers(),
                                                                 yukonEnergyCompany.getEnergyCompanyId());
                //Remove invalid serial numbers
                List<String> lookupFailedSerialNumbers = removeInvalidSerialNumbers(deviceTextMessage, serialNumberToInventoryIdMap);

                if (!deviceTextMessage.getSerialNumbers().isEmpty()) {
                    YukonTextMessage yukonTextMessage =
                        getYukonTextMessage(deviceTextMessage, serialNumberToInventoryIdMap, user);

                    // Send out message
                    thermostatService.sendTextMessage(yukonTextMessage);
                }
                //build a response
                addRequestedNode(deviceTextMessage.getSerialNumbers().size(), lookupFailedSerialNumbers.size(),
                                 resp);
                addLookupErrorsNode(lookupFailedSerialNumbers, resp);
            } else {
                //build a response
                addRequestedNode(0, 0, resp);
                addInvalidMessageNode(deviceTextMessage, resp);
            }

        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendTextMessage, e, "UserNotAuthorized",
                                     "The user is not authorized to send text messages.");
            resp.addContent(fe);
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(sendTextMessage, e, "OtherException",
                                                    "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * This method converts a text message to Yukon text message object. Yukon text message object
     * can then be passed to the Yukon Message Handler and sent out.
     *
     * @param textMessage 
     * @param serialNumberToInventoryIdMap
     * @param user
     * @return
     */
    private YukonTextMessage getYukonTextMessage(DeviceTextMessage textMessage,
                                                 final Map<String, Integer> serialNumberToInventoryIdMap,
                                                 final LiteYukonUser user) {
        YukonTextMessage yukonTextMessage = new YukonTextMessage();

        Collection<Integer> inventoryIds =
            Collections2.transform(textMessage.getSerialNumbers(), new Function<String, Integer>() {
                @Override
                public Integer apply(String serialNumber) {
                    return serialNumberToInventoryIdMap.get(serialNumber);
                }
            });
        // Adding the mapping to the database.
        Instant endTime = textMessage.getStartTime().plus(textMessage.getDisplayDuration());
        long yukonMessageId = nextValueHelper.getNextValue("ExternalToYukonMessageIdMapping");
        yukonTextMessageDao.addMessageIdMapping(textMessage.getMessageId(), yukonMessageId, user,
                                                endTime);

        yukonTextMessage.setMessageId(yukonMessageId);
        yukonTextMessage.setYukonUser(user);
        yukonTextMessage.setInventoryIds(Sets.newHashSet(inventoryIds));
        yukonTextMessage.setConfirmationRequired(textMessage.isConfirmationRequired());
        yukonTextMessage.setDisplayDuration(textMessage.getDisplayDuration());
        yukonTextMessage.setMessage(textMessage.getMessage());
        yukonTextMessage.setStartTime(textMessage.getStartTime());

        return yukonTextMessage;

    }

    /**
     * Removes the invalid serial numbers.
     *
     * @param deviceTextMessage
     * @param serialNumberToInventoryIdMap
     * @return
     */
    private List<String> removeInvalidSerialNumbers(DeviceTextMessage deviceTextMessage,
                                                    Map<String, Integer> serialNumberToInventoryIdMap) {
        List<String> lookupFailedSerialNumbers = Lists.newArrayList();
        for (String serialNumber : deviceTextMessage.getSerialNumbers()) {
            if (serialNumberToInventoryIdMap.get(serialNumber) == null) {
                lookupFailedSerialNumbers.add(serialNumber);
            }
        }
        deviceTextMessage.getSerialNumbers().removeAll(lookupFailedSerialNumbers);
        return lookupFailedSerialNumbers;
    }

    /**
     * Adds the lookup errors node.
     *
     * @param serialNumbers
     * @param 
     */
    private void addLookupErrorsNode(List<String> serialNumbers, Element parent) {
        if (!serialNumbers.isEmpty()) {
            Element lookupErrorElem = new Element("lookupError", ns);
            for (String serialNumber : serialNumbers) {
                lookupErrorElem.addContent(XmlUtils.createStringElement("serialNumber", ns, serialNumber));
            }
            parent.addContent(lookupErrorElem);
        }
    }

    /**
     * Adds the requested node.
     *
     * @param initiated 
     * @param failed 
     * @param parent
     */
    private void addRequestedNode(int initiated, int failed, Element parent) {
        Element requestedElem = new Element("requested", ns);
        requestedElem.setAttribute("initiated", Integer.toString(initiated));
        if (failed > 0) {
            requestedElem.setAttribute("failedLookup", Integer.toString(failed));
        }
        parent.addContent(requestedElem);
    }

    /**
     * Adds the invalid message node.
     *
     * @param deviceTextMessage
     * @param parent
     */
    private void addInvalidMessageNode(DeviceTextMessage deviceTextMessage, Element parent) {
        Element invalidMessageElem = new Element("invalidMessage", ns);
        invalidMessageElem.addContent(XmlUtils.createLongElement("messageId", ns,
                                                                 deviceTextMessage.getMessageId()));
        Element serialNumbers = new Element("serialNumbers", ns);
        for (String serialNumber : deviceTextMessage.getSerialNumbers()) {
            serialNumbers
                .addContent(XmlUtils.createStringElement("serialNumber", ns, serialNumber));
        }
        invalidMessageElem.addContent(serialNumbers);
        parent.addContent(invalidMessageElem);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
    }

}