package com.cannontech.yukon.api.consumer.endpoint;

import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
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
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private ThermostatService thermostatService;

    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="newThermostatScheduleRequest")
    public Element invoke(Element newThermostatSchedule, CustomerAccount customerAccount) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(newThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(newThermostatSchedule);

        // init response
        Element resp = new Element("newThermostatScheduleResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        Element resultList = new Element("thermostatScheduleResultList", ns);
        resp.addContent(resultList);
        
        try {          
            LiteYukonUser yukonUser = customerAccountDao.getYukonUserByAccountId(customerAccount.getAccountId());
            YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByAccountId(customerAccount.getAccountId());

            List<Integer> childEnergyCompanyIds = yukonEnergyCompanyService.getChildEnergyCompanies(energyCompany.getEnergyCompanyId());
            childEnergyCompanyIds.add(energyCompany.getEnergyCompanyId());
            
            Set<ThermostatScheduleMode> allowedThermostatScheduleModes = thermostatService.getAllowedThermostatScheduleModes(energyCompany);
           
            List<ThermostatSchedule> thermostatSchedules =
                    requestTemplate.evaluate("//y:newThermostatScheduleRequest/y:thermostatSchedule", 
                                             new NodeToElementMapperWrapper<ThermostatSchedule>(new ThermostatScheduleElementRequestMapper()));
               
            for (ThermostatSchedule schedule : thermostatSchedules) {
                accountEventLogService.thermostatScheduleCreationAttempted(yukonUser, customerAccount.getAccountNumber(), schedule.getSchedulableThermostatType().toString(), schedule.getScheduleName(),
                                                                           EventSource.API);
                Element thermostatScheduleResultNode = ThermostatScheduleHelper.addThermostatScheduleResultNode(ns, resultList, schedule);
                try {
                    AccountThermostatSchedule duplicateSchedule =
                        accountThermostatScheduleDao.findSchedulesForAccountByScheduleName(customerAccount.getAccountId(), schedule.getScheduleName());
                    if (duplicateSchedule != null) {
                        Element fe = XMLFailureGenerator.makeSimple("DuplicateScheduleName", "Duplicate schedule name.");
                        thermostatScheduleResultNode.addContent(fe);
                    } else if (!allowedThermostatScheduleModes.contains(schedule.getThermostatScheduleMode())) {
                        Element fe = XMLFailureGenerator.makeSimple("InvalidThermostatScheduleMode", "Thermostat schedule mode is not allowed.");
                        thermostatScheduleResultNode.addContent(fe);
                    } else {
                        AccountThermostatSchedule accountThermostatSchedule = ThermostatScheduleHelper.convertToAccountThermostatSchedule(schedule, customerAccount.getAccountId());
                        accountThermostatScheduleDao.save(accountThermostatSchedule);
                        thermostatScheduleResultNode.addContent(new Element("success", ns));  
                    }
                } 
                catch (Exception e) {
                    Element fe = XMLFailureGenerator.generateFailure(newThermostatSchedule, e, "OtherException","An exception has been caught.");
                    resp.addContent(fe);
                }
            }
        
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(newThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
        }
        return resp;
    }
}
