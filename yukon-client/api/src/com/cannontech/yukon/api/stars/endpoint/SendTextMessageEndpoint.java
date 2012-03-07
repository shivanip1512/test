package com.cannontech.yukon.api.stars.endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    
    //@Autowired by setter
    private JmsTemplate jmsTemplate;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(SendTextMessageEndpoint.class);
    
    private static final String MESSAGE_PATTERN = "[\\w!#$%&'()*+,-./:;=?@[\\\\]^_`\\{\\|\\}~]+";
    private Pattern pattern = Pattern.compile(MESSAGE_PATTERN);
    
    @PostConstruct
    public void initialize() throws JDOMException {}
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="sendTextMessageRequest")
    public Element invoke(Element sendTextMessage, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(sendTextMessage, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(sendTextMessage);
        
        List<DeviceTextMessage> textMessages = 
                requestTemplate.evaluate("//y:textMessageList/y:textMessage", new NodeToElementMapperWrapper<DeviceTextMessage>(new TextMessageElementRequestMapper()));
        
        // init response
        Element resp = new Element("sendTextMessageResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
               
        boolean allMessagesValid = true;
        
        for(DeviceTextMessage textMessage: textMessages){  
            Matcher matcher = pattern.matcher(textMessage.getMessage());
            if(!matcher.matches()){
                allMessagesValid = false;
                Element fe = XMLFailureGenerator.generateFailure(sendTextMessage, "OtherException", "Invalid message: "+textMessage.getMessage());
                resp.addContent(fe);
            }
        }
        if (allMessagesValid) {

            List<YukonTextMessage> yukonTextMessages = getYukonTextMessages(textMessages, user);

            // run service
            try {

                // Check authorization
                rolePropertyDao.verifyRole(YukonRole.INVENTORY, user);

                // Send out messages
                for (YukonTextMessage yukonTextMessage : yukonTextMessages) {
                    thermostatService.sendTextMessage(yukonTextMessage);
                }
            } catch (NotAuthorizedException e) {
                Element fe = XMLFailureGenerator.generateFailure(sendTextMessage, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
                resp.addContent(fe);
                return resp;
            } catch (Exception e) {
                Element fe = XMLFailureGenerator.generateFailure(sendTextMessage, e, "OtherException", "An exception has been caught.");
                resp.addContent(fe);
                log.error(e.getMessage(), e);
            }

            // build response
            resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        }
        return resp;
    }
    
    /**
     * This method converts a list of text messages to Yukon text message objects.  These can then be passed
     * to the Yukon Message Handler and sent out.
     */
    private List<YukonTextMessage> getYukonTextMessages(List<DeviceTextMessage> textMessages, LiteYukonUser user) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);

        // Get all the serial numbers used in this web service call.
        final Set<String> serialNumbers = Sets.newHashSet();
        for (DeviceTextMessage textMessage : textMessages) {
            serialNumbers.addAll(textMessage.getSerialNumbers());
        }

        Map<String, Integer> serialNumberToInventoryIdMap = 
                inventoryDao.getSerialNumberToInventoryIdMap(serialNumbers, yukonEnergyCompany.getEnergyCompanyId());

        for (DeviceTextMessage textMessage : textMessages) {
            textMessage.getSerialNumbers().retainAll(serialNumberToInventoryIdMap.keySet());
        }
        
        return getYukonTextMessages(textMessages, serialNumberToInventoryIdMap, user) ;
    }
    
    /**
     * This method converts a list of text messages to Yukon text message objects.  These can then be passed
     * to the Yukon Message Handler and sent out.
     */
    private List<YukonTextMessage>  getYukonTextMessages(List<DeviceTextMessage> textMessages, final Map<String, Integer> serialNumberToInventoryIdMap, final LiteYukonUser user) {
        List<YukonTextMessage> yukonTextMessage = 
                Lists.transform(textMessages, new Function<DeviceTextMessage, YukonTextMessage>() {

                    @Override
                    public YukonTextMessage apply(DeviceTextMessage textMessage) {
                        YukonTextMessage yukonTextMessage = new YukonTextMessage();

                        Collection<Integer> inventoryIds = 
                                Collections2.transform(textMessage.getSerialNumbers(), new Function<String, Integer>() {
                                    @Override
                                    public Integer apply(String serialNumber) {
                                        return serialNumberToInventoryIdMap.get(serialNumber);
                                    }
                                });
                        //Adding the mapping to the database.
                        Instant endTime = textMessage.getStartTime().plus(textMessage.getDisplayDuration());
                        long yukonMessageId = nextValueHelper.getNextValue("ExternalToYukonMessageIdMapping");
                        yukonTextMessageDao.addMessageIdMapping(textMessage.getMessageId(), yukonMessageId, user, endTime);

                        yukonTextMessage.setMessageId(yukonMessageId);
                        yukonTextMessage.setYukonUser(user);
                        yukonTextMessage.setInventoryIds(Sets.newHashSet(inventoryIds));
                        yukonTextMessage.setConfirmationRequired(textMessage.isConfirmationRequired());
                        yukonTextMessage.setDisplayDuration(textMessage.getDisplayDuration());
                        yukonTextMessage.setMessage(textMessage.getMessage());
                        yukonTextMessage.setStartTime(textMessage.getStartTime());
                        
                        return yukonTextMessage;
                    }
                });

        return yukonTextMessage;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
    }
}