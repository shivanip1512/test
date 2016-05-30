package com.cannontech.yukon.api.consumer.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.yukon.api.consumer.endpoint.helper.ThermostatScheduleHelper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Endpoint
public class DeleteThermostatScheduleEndpoint {
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ThermostatScheduleHelper thermostatScheduleHelper;

    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="deleteThermostatScheduleRequest")
    public Element invoke(Element deleteThermostatSchedule, CustomerAccount customerAccount) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(deleteThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(deleteThermostatSchedule);

        // init response
        Element resp = new Element("deleteThermostatScheduleResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
       
        List<Element> thermostatScheduleResults = new ArrayList<>();
        try {     
            LiteYukonUser yukonUser = customerAccountDao.getYukonUserByAccountId(customerAccount.getAccountId());
            List<String> scheduleNames = requestTemplate.evaluateAsStringList("//y:scheduleName");
            // remove duplicate names
            scheduleNames = ImmutableSet.copyOf(scheduleNames).asList();
            for (String scheduleName : scheduleNames) {
                accountEventLogService.thermostatScheduleDeleteAttempted(yukonUser, customerAccount.getAccountNumber(), scheduleName, EventSource.API);
                Element thermostatScheduleResult = thermostatScheduleHelper.addThermostatScheduleResultNode(ns, scheduleName);
                try{
                    AccountThermostatSchedule accountThermostatSchedule = accountThermostatScheduleDao.getSchedulesForAccountByScheduleName(customerAccount.getAccountId(), scheduleName);
                    accountThermostatScheduleDao.deleteById(accountThermostatSchedule.getAccountThermostatScheduleId());
                    thermostatScheduleResult.addContent(new Element("success", ns));
                } catch (EmptyResultDataAccessException e) {
                    Element errors = new Element("errors", ns);
                    thermostatScheduleHelper.addScheduleError("The schedule name supplied does not exist.", errors, ns);
                    thermostatScheduleResult.addContent(errors);
                }
                thermostatScheduleResults.add(thermostatScheduleResult);
            }
            for (Element element : thermostatScheduleResults) {
                resp.addContent(element);
            }
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(deleteThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
        }
        return resp;
    }
}