package com.cannontech.yukon.api.consumer.endpoint;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.xml.xpath.XPathException;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.yukon.api.consumer.endpoint.endpointMappers.ThermostatScheduleElementRequestMapper;
import com.cannontech.yukon.api.consumer.endpoint.helper.ThermostatScheduleHelper;
import com.cannontech.yukon.api.stars.model.ThermostatSchedule;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;


@Endpoint
public class NewThermostatScheduleEndpoint {    
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ThermostatScheduleHelper thermostatScheduleHelper;


    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="newThermostatScheduleRequest")
    public Element invoke(Element newThermostatSchedule, CustomerAccount customerAccount) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(newThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(newThermostatSchedule);

        // init response
        Element resp = new Element("newThermostatScheduleResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
                
        try {          
            LiteYukonUser yukonUser = customerAccountDao.getYukonUserByAccountId(customerAccount.getAccountId());
            List<ThermostatSchedule> thermostatSchedules =
                    requestTemplate.evaluate("/y:newThermostatScheduleRequest/y:thermostatSchedule", 
                                             new NodeToElementMapperWrapper<ThermostatSchedule>(new ThermostatScheduleElementRequestMapper()));
            Element resultList = new Element("thermostatScheduleResultList", ns);
            resp.addContent(resultList);
            
            for (ThermostatSchedule schedule : thermostatSchedules) {
                accountEventLogService.thermostatScheduleCreationAttempted(yukonUser, customerAccount
                    .getAccountNumber(), schedule.getSchedulableThermostatType().name(), schedule.getScheduleName(), EventSource.API);
                Element thermostatScheduleResultNode = thermostatScheduleHelper.addThermostatScheduleResultNode(ns, resultList, schedule);
                try {
                    if (thermostatScheduleHelper.isValidSchedule(customerAccount.getAccountId(),schedule,thermostatScheduleResultNode, ns, false)) {
                        AccountThermostatSchedule accountThermostatSchedule =thermostatScheduleHelper.convertToAccountThermostatSchedule(schedule,
                                                                                        customerAccount.getAccountId());
                        accountThermostatScheduleDao.save(accountThermostatSchedule);
                        thermostatScheduleResultNode.addContent(new Element("success", ns));
                    }
                } catch (Exception e) {
                    Element fe = XMLFailureGenerator.generateFailure(thermostatScheduleResultNode, e, "OtherException",
                                                            "An exception has been caught.");
                    thermostatScheduleResultNode.addContent(fe);
                }
            }
        
        } catch (XPathException e) {
            Element fe = XMLFailureGenerator.generateFailure(newThermostatSchedule, e, "OtherException", e.getMessage());
            resp.addContent(fe);
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(newThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
        }
        return resp;
    }
}
