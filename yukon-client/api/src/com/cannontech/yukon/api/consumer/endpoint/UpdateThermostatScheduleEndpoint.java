package com.cannontech.yukon.api.consumer.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
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
public class UpdateThermostatScheduleEndpoint {
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ThermostatScheduleHelper thermostatScheduleHelper;

    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="updateThermostatScheduleRequest")
    public Element invoke(Element updateThermostatSchedule, CustomerAccount customerAccount) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(updateThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(updateThermostatSchedule);

        // init response
        Element resp = new Element("updateThermostatScheduleResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        try {          
            
            LiteYukonUser yukonUser = customerAccountDao.getYukonUserByAccountId(customerAccount.getAccountId());
            Boolean addOnFail = requestTemplate.evaluateAsBoolean("/y:updateThermostatScheduleRequest/@addOnFail", true);
            
            // Get the information for the supplied request.
            List<ThermostatSchedule> thermostatSchedules =
                    requestTemplate.evaluate("/y:updateThermostatScheduleRequest/y:thermostatSchedule", 
                                             new NodeToElementMapperWrapper<ThermostatSchedule>(new ThermostatScheduleElementRequestMapper()));
                       
            List<Element> thermostatScheduleResults = new ArrayList<>();
            for (ThermostatSchedule schedule : thermostatSchedules) {
                boolean newScheduleCreated = false;
                Element thermostatScheduleResult = thermostatScheduleHelper.addThermostatScheduleResultNode(ns, schedule);
                Element errors = new Element("errors", ns);
                AccountThermostatSchedule scheduleToUpdate =
                    accountThermostatScheduleDao.findSchedulesForAccountByScheduleName(customerAccount.getAccountId(),
                                                                                       schedule.getScheduleName());
                if (scheduleToUpdate != null || addOnFail) {
                    if (thermostatScheduleHelper.validateSchedule(customerAccount.getAccountId(), schedule, errors,ns,
                                                                 true)) {
                        AccountThermostatSchedule accountThermostatSchedule =
                            thermostatScheduleHelper.convertToAccountThermostatSchedule(schedule,customerAccount.getAccountId());
                        if (scheduleToUpdate != null) {
                            accountThermostatSchedule.setAccountThermostatScheduleId(scheduleToUpdate.getAccountThermostatScheduleId());
                        } else {
                            newScheduleCreated = true;
                        }
                        accountThermostatScheduleDao.save(accountThermostatSchedule);
                        thermostatScheduleResult.addContent(new Element("success", ns));
                    }
                } else {
                    thermostatScheduleHelper.addScheduleError("The schedule name supplied does not exist.", errors, ns);
                }
                if(!errors.getChildren().isEmpty()){
                    thermostatScheduleResult.addContent(errors);
                }
                if (newScheduleCreated) {
                    accountEventLogService.thermostatScheduleCreationAttempted(yukonUser, customerAccount.getAccountNumber(),
                                                                               schedule.getSchedulableThermostatType()
                                                                                   .name(), schedule.getScheduleName(),
                                                                               EventSource.API);
                } else {
                    accountEventLogService.thermostatScheduleUpdateAttempted(yukonUser, customerAccount.getAccountNumber(),
                                                                             schedule.getSchedulableThermostatType()
                                                                                 .name(), schedule.getScheduleName(),
                                                                             EventSource.API);
                }
                thermostatScheduleResults.add(thermostatScheduleResult);
            }
            for (Element element : thermostatScheduleResults) {
                resp.addContent(element);
            }
        } catch (XPathException e) {
            Element fe = XMLFailureGenerator.generateFailure(updateThermostatSchedule, e, "OtherException", e.getMessage());
            resp.addContent(fe);
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(updateThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
        }
        return resp;
    }
}
