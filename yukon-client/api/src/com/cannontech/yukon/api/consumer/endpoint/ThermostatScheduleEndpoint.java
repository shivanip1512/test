package com.cannontech.yukon.api.consumer.endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ThermostatScheduleEndpoint {

    @Autowired private AccountCheckerService accountCheckerService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RolePropertyDao rolePropertyDao;

    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="thermostatScheduleRequest")
    public Element invoke(Element thermostatSchedule, YukonUserContext userContext) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(thermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(thermostatSchedule);

        // init response
        Element resp = new Element("thermostatScheduleResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        try {
            rolePropertyDao.verifyRole(YukonRole.INVENTORY, userContext.getYukonUser());
            
            // Get all of the thermostat schedule ids and check if the user is allowed to see them.
            List<Integer> thermostatScheduleIds = requestTemplate.evaluateAsIntegerList("//y:thermostatScheduleId");
            accountCheckerService.checkThermostatSchedule(userContext.getYukonUser(), thermostatScheduleIds.toArray(new Integer[thermostatScheduleIds.size()]));

            for (int thermostatScheduleId : thermostatScheduleIds) {
                AccountThermostatSchedule accountThermostatSchedule = accountThermostatScheduleDao.getById(thermostatScheduleId);
                CustomerAccount customerAccount = customerAccountDao.getById(accountThermostatSchedule.getAccountId());
                resp.addContent(buildResponseForAccountThermostatSchedule(customerAccount, accountThermostatSchedule));
            }
            
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(thermostatSchedule, e, "UserNotAuthorized", "The user is not authorized to send text messages.");
            resp.addContent(fe);
            return resp;
        } catch (EmptyResultDataAccessException e) {
            Element fe = XMLFailureGenerator.generateFailure(thermostatSchedule, e, "ScheduleDoesNotExist", "The schedule name supplied does not exist.");
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(thermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        resp.addContent(new Element("success", ns));

        return resp;
    }

    /**
     * This method generates an xml account thermostat schedule.
     */
    private Element buildResponseForAccountThermostatSchedule(CustomerAccount customerAccount, AccountThermostatSchedule accountThermostatSchedule) {
        Element thermostatScheduleElem = new Element("thermostatSchedule", ns);
        thermostatScheduleElem.setAttribute("accountNumber", customerAccount.getAccountNumber(), ns);
        thermostatScheduleElem.setAttribute("thermostatType", XmlUtils.toXmlRepresentation(accountThermostatSchedule.getThermostatType()), ns);
        thermostatScheduleElem.setAttribute("thermostatScheduleMode", XmlUtils.toXmlRepresentation(accountThermostatSchedule.getThermostatScheduleMode()), ns);

        Element scheduleNameElem = XmlUtils.createStringElement("scheduleName", ns, accountThermostatSchedule.getScheduleName());
        thermostatScheduleElem.addContent(scheduleNameElem);

        for (Entry<TimeOfWeek, Collection<AccountThermostatScheduleEntry>> timeOfWeekEntriesEntry : accountThermostatSchedule.getEntriesByTimeOfWeekMultimapAsMap().entrySet()) {
            Element schedulePeriodElem = new Element("schedulePeriod", ns);
            schedulePeriodElem.setAttribute("timeOfWeek", XmlUtils.toXmlRepresentation(timeOfWeekEntriesEntry.getKey()));
            
            for (AccountThermostatScheduleEntry accountThermostatScheduleEntry : timeOfWeekEntriesEntry.getValue()) {
                Element thermostatSchedulePeriodElem = new Element("thermostatSchedulePeriod", ns);
                
                Element startTimeElem = XmlUtils.createLocalTimeElement("startTime", ns, accountThermostatScheduleEntry.getStartTimeLocalTime());
                thermostatSchedulePeriodElem.addContent(startTimeElem);
                
                Element coolTempElem = XmlUtils.createDoubleElement("coolTemp", ns, accountThermostatScheduleEntry.getCoolTemp().toFahrenheit().getValue());
                coolTempElem.setAttribute("unit", TemperatureUnit.FAHRENHEIT.getLetter());
                thermostatSchedulePeriodElem.addContent(coolTempElem);

                Element heatTempElem = XmlUtils.createDoubleElement("heatTemp", ns, accountThermostatScheduleEntry.getHeatTemp().toFahrenheit().getValue());
                heatTempElem.setAttribute("unit", XmlUtils.toXmlRepresentation(TemperatureUnit.FAHRENHEIT));
                thermostatSchedulePeriodElem.addContent(heatTempElem);
                
                schedulePeriodElem.addContent(thermostatSchedulePeriodElem);
            }
            
            thermostatScheduleElem.addContent(schedulePeriodElem);
        }

        return thermostatScheduleElem;
    }

}