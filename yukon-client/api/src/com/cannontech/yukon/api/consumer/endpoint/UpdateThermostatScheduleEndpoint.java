package com.cannontech.yukon.api.consumer.endpoint;

import java.util.List;
import java.util.Map.Entry;

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
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.consumer.endpoint.endpointMappers.ThermostatScheduleElementRequestMapper;
import com.cannontech.yukon.api.stars.model.SchedulePeriod;
import com.cannontech.yukon.api.stars.model.ThermostatSchedule;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Lists;

@Endpoint
public class UpdateThermostatScheduleEndpoint {

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;

    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(UpdateThermostatScheduleEndpoint.class);
    
    @PostConstruct
    public void initialize() throws JDOMException {}
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="updateThermostatScheduleRequest")
    public Element invoke(Element updateThermostatSchedule, YukonUserContext userContext) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(updateThermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(updateThermostatSchedule);

        // init response
        Element resp = new Element("updateThermostatScheduleResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        try {
            
            rolePropertyDao.verifyRole(YukonRole.INVENTORY, userContext.getYukonUser());
            
            // Get the information for the supplied request.
            List<ThermostatSchedule> thermostatSchedules =
                    requestTemplate.evaluate("//y:updateThermostatScheduleRequest/y:thermostatSchedule", 
                                             new NodeToElementMapperWrapper<ThermostatSchedule>(new ThermostatScheduleElementRequestMapper()));
            
            // Log thermostat schedule save attempt
            for (ThermostatSchedule thermostatSchedule : thermostatSchedules) {
                accountEventLogService.thermostatScheduleSavingAttempted(userContext.getYukonUser(), thermostatSchedule.getAccountNumber(), 
                                                                         thermostatSchedule.getSchedulableThermostatType().toString(), thermostatSchedule.getScheduleName(),
                                                                         EventSource.API);
            }
            List<AccountThermostatSchedule> accountThermostatSchedules = convertThermostatSchedules(thermostatSchedules, userContext.getYukonUser());
            
            // Save the schedule
            for (AccountThermostatSchedule newATS: accountThermostatSchedules) {
                accountThermostatScheduleDao.save(newATS);
                Element thermostatScheduleIdElement = XmlUtils.createIntegerElement("accountThermostatScheduleId", ns, newATS.getAccountThermostatScheduleId());
                resp.addContent(thermostatScheduleIdElement);
            }
            
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(updateThermostatSchedule, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(updateThermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
            return resp;
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }

    /**
     * Converts the thermostatSchedules over to accountThermostatSchedule that can be used the with thermostat schedules services 
     */
    private List<AccountThermostatSchedule> convertThermostatSchedules(List<ThermostatSchedule> thermostatSchedules, LiteYukonUser user) {
        List<AccountThermostatSchedule> accountThermostatSchedules = Lists.newArrayList();

        for (ThermostatSchedule thermostatSchedule : thermostatSchedules) {
            AccountThermostatSchedule accountThermostatSchedule = new AccountThermostatSchedule();
            accountThermostatSchedule.setAccountThermostatScheduleId(thermostatSchedule.getAcctThermostatScheduleId());
            accountThermostatSchedule.setScheduleName(thermostatSchedule.getScheduleName());
            accountThermostatSchedule.setThermostatScheduleMode(thermostatSchedule.getThermostatScheduleMode());
            
            int energyCompanyId = yukonEnergyCompanyService.getEnergyCompanyIdByOperator(user);
            List<Integer> childEnergyCompanyIds = yukonEnergyCompanyService.getChildEnergyCompanies(energyCompanyId);
            childEnergyCompanyIds.add(energyCompanyId);
            CustomerAccount customerAccount =  customerAccountDao.getByAccountNumber(thermostatSchedule.getAccountNumber(), childEnergyCompanyIds);
            accountThermostatSchedule.setAccountId(customerAccount.getAccountId());
            accountThermostatSchedule.setThermostatType(thermostatSchedule.getSchedulableThermostatType());

            // Getting the thermostat type 
            List<AccountThermostatScheduleEntry> accountThermostatScheduleEntrys = convertSchedulePeriodToAccountThermostatScheduleEntries(thermostatSchedule);
            accountThermostatSchedule.setScheduleEntries(accountThermostatScheduleEntrys);
            
            accountThermostatSchedules.add(accountThermostatSchedule);
        }

        return accountThermostatSchedules;
    }

    /**
     * Converts the thermostatSchedules over to accountThermostatScheduleEntries that can be used the with thermostat schedules services 
     */
    private List<AccountThermostatScheduleEntry> convertSchedulePeriodToAccountThermostatScheduleEntries(ThermostatSchedule thermostatSchedule) {
        List<AccountThermostatScheduleEntry> accountThermostatScheduleEntrys = Lists.newArrayList();

        for (Entry<TimeOfWeek, SchedulePeriod> timeOfWeekToSchedulePeriodEntry : thermostatSchedule.getSchedulePeriodContainer().entries()) {
            TimeOfWeek timeOfWeek = timeOfWeekToSchedulePeriodEntry.getKey();
            SchedulePeriod schedulePeriod = timeOfWeekToSchedulePeriodEntry.getValue();

            AccountThermostatScheduleEntry accountThermostatScheduleEntry = new AccountThermostatScheduleEntry();
            accountThermostatScheduleEntry.setTimeOfWeek(timeOfWeek);
            accountThermostatScheduleEntry.setStartTime(schedulePeriod.getPeriodStartTime());
            accountThermostatScheduleEntry.setCoolTemp(schedulePeriod.getCoolTemperature());
            accountThermostatScheduleEntry.setHeatTemp(schedulePeriod.getHeatTemperature());
            
            accountThermostatScheduleEntrys.add(accountThermostatScheduleEntry);
        }
        
        return accountThermostatScheduleEntrys;
    }
}