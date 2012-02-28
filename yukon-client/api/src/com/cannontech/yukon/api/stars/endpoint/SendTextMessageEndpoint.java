package com.cannontech.yukon.api.stars.endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.yukon.api.stars.endpoint.endpointMappers.TextMessageElementRequestMapper;
import com.cannontech.yukon.api.stars.model.TextMessage;
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
    @Autowired private ZigbeeWebService zigbeeWebService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(SendTextMessageEndpoint.class);
    
    @PostConstruct
    public void initialize() throws JDOMException {}
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="sendTextMessageRequest")
    public Element invoke(Element sendTextMessage, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(sendTextMessage, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(sendTextMessage);
        
        List<TextMessage> textMessages = 
                requestTemplate.evaluate("//y:textMessageList/y:textMessage", new NodeToElementMapperWrapper<TextMessage>(new TextMessageElementRequestMapper()));
        List<ZigbeeTextMessage> zigbeeTextMessages = getZigbeeTextMessages(textMessages, user);
        
        // init response
        Element resp = new Element("sendTextMessageResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        try {
            
            // Check authorization
            rolePropertyDao.verifyRole(YukonRole.INVENTORY, user);
            
            // Send out messages
            for (ZigbeeTextMessage zigbeeTextMessage : zigbeeTextMessages) {
                zigbeeWebService.sendTextMessage(zigbeeTextMessage);
            }
        } catch (DigiNotConfiguredException e) {
            Element fe = XMLFailureGenerator.generateFailure(sendTextMessage, e, "DigiNotConfigured", "Digi is not configured.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
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
        
        return resp;
    }
    
    /**
     * This method converts a list of text messages to zigbee text message objects.  These can then be passed
     * to the ZigBee web service and sent out.
     */
    private List<ZigbeeTextMessage> getZigbeeTextMessages(List<TextMessage> textMessages, LiteYukonUser user) {
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);

        // Get all the serial numbers used in this web service call.
        final Set<String> serialNumbers = Sets.newHashSet();
        for (TextMessage textMessage : textMessages) {
            serialNumbers.addAll(textMessage.getSerialNumbers());
        }

        Map<String, Integer> serialNumberToInventoryIdMap = 
                inventoryDao.getSerialNumberToInventoryIdMap(serialNumbers, yukonEnergyCompany.getEnergyCompanyId());
        
        return getZigbeeTextMessages(textMessages, serialNumberToInventoryIdMap) ;
    }
    
    /**
     * This method converts a list of text messages to zigbee text message objects.  These can then be passed
     * to the ZigBee web service and sent out.
     */
    private List<ZigbeeTextMessage>  getZigbeeTextMessages(List<TextMessage> textMessages, final Map<String, Integer> serialNumberToInventoryIdMap) {
        List<ZigbeeTextMessage> zigbeeTextMessages = 
                Lists.transform(textMessages, new Function<TextMessage, ZigbeeTextMessage>() {

                    @Override
                    public ZigbeeTextMessage apply(TextMessage textMessage) {
                        ZigbeeTextMessage zigbeeTextMessage = new ZigbeeTextMessage();

                        Collection<Integer> inventoryIds = 
                                Collections2.transform(textMessage.getSerialNumbers(), new Function<String, Integer>() {
                                    @Override
                                    public Integer apply(String serialNumber) {
                                        return serialNumberToInventoryIdMap.get(serialNumber);
                                    }
                                });
                        
                        zigbeeTextMessage.setInventoryIds(Sets.newHashSet(inventoryIds));
                        zigbeeTextMessage.setConfirmationRequired(textMessage.isConfirmationRequired());
                        zigbeeTextMessage.setDisplayDuration(textMessage.getDisplayDuration());
                        zigbeeTextMessage.setMessage(textMessage.getMessage());
                        zigbeeTextMessage.setStartTime(textMessage.getStartTime());
                        
                        return zigbeeTextMessage;
                    }
                });

        return zigbeeTextMessages;
    }
}