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
public class UpdateThermostatScheduleEndpoint {
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private ThermostatService thermostatService;

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
            
            YukonEnergyCompany energyCompany =  yukonEnergyCompanyService.getEnergyCompanyByAccountId(customerAccount.getAccountId());

            List<Integer> childEnergyCompanyIds = yukonEnergyCompanyService.getChildEnergyCompanies(energyCompany.getEnergyCompanyId());
            childEnergyCompanyIds.add(energyCompany.getEnergyCompanyId());
            
            Set<ThermostatScheduleMode> allowedThermostatScheduleModes = thermostatService.getAllowedThermostatScheduleModes(energyCompany);
           
            // Get the information for the supplied request.
            List<ThermostatSchedule> thermostatSchedules =
                    requestTemplate.evaluate("/y:updateThermostatScheduleRequest/y:thermostatSchedule", 
                                             new NodeToElementMapperWrapper<ThermostatSchedule>(new ThermostatScheduleElementRequestMapper()));
                       
            Element resultList = new Element("thermostatScheduleResultList", ns);
            resp.addContent(resultList);
            
            for (ThermostatSchedule schedule : thermostatSchedules) {
                boolean newScheduleCreated = false;

                Element thermostatScheduleResultNode = ThermostatScheduleHelper.addThermostatScheduleResultNode(ns, resultList, schedule);
                try {
                    if (!allowedThermostatScheduleModes.contains(schedule.getThermostatScheduleMode())) {
                        Element fe = XMLFailureGenerator.makeSimple("InvalidThermostatScheduleMode", "Thermostat schedule mode is not allowed");
                        thermostatScheduleResultNode.addContent(fe);
                    } else {
                        AccountThermostatSchedule scheduleToUpdate =
                                accountThermostatScheduleDao.findSchedulesForAccountByScheduleName(customerAccount.getAccountId(), schedule.getScheduleName());
                        AccountThermostatSchedule accountThermostatSchedule = ThermostatScheduleHelper.convertToAccountThermostatSchedule(schedule, customerAccount.getAccountId());
                        if(scheduleToUpdate != null){
                            accountThermostatSchedule.setAccountThermostatScheduleId(scheduleToUpdate.getAccountThermostatScheduleId());
                            accountThermostatScheduleDao.save(accountThermostatSchedule);
                            thermostatScheduleResultNode.addContent(new Element("success", ns));  
                        }else if(addOnFail){
                            newScheduleCreated = true;
                            accountThermostatScheduleDao.save(accountThermostatSchedule);
                            thermostatScheduleResultNode.addContent(new Element("success", ns));  
                        }else{
                            Element fe = XMLFailureGenerator.makeSimple("ScheduleDoesNotExist", "The schedule name supplied does not exist.");
                            thermostatScheduleResultNode.addContent(fe);
                        }
                    }
                } 
                catch (Exception e) {
                    Element fe = XMLFailureGenerator.generateFailure(thermostatScheduleResultNode, e, "OtherException","An exception has been caught.");
                    thermostatScheduleResultNode.addContent(fe);
                }
                
                if(newScheduleCreated){
                    accountEventLogService.thermostatScheduleCreationAttempted(yukonUser, customerAccount.getAccountNumber(),
                                                                             schedule.getSchedulableThermostatType().name(), schedule.getScheduleName(),
                                                                             EventSource.API);
                }else{
                    accountEventLogService.thermostatScheduleUpdateAttempted(yukonUser, customerAccount.getAccountNumber(),
                                                                             schedule.getSchedulableThermostatType().name(), schedule.getScheduleName(),
                                                                             EventSource.API);
                }
            }
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(updateThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
        }

        return resp;
    }
}
