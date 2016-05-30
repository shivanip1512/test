package com.cannontech.yukon.api.consumer.endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.ImmutableSet;

@Endpoint
public class ThermostatScheduleEndpoint {

    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private RolePropertyDao rolePropertyDao;

    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(ThermostatScheduleEndpoint.class);
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="thermostatScheduleRequest")
    public Element invoke(Element thermostatSchedule, CustomerAccount customerAccount) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(thermostatSchedule, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(thermostatSchedule);

        // init response
        Element resp = new Element("thermostatScheduleResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        try {
            
            List<String> scheduleNames = requestTemplate.evaluateAsStringList("//y:scheduleName");
            if(scheduleNames.isEmpty()){
                Element fe = XMLFailureGenerator.generateFailure(thermostatSchedule, "OtherException", "At least one schedule name is required.");
                resp.addContent(fe);
            }else{
                // remove duplicate names
                scheduleNames = ImmutableSet.copyOf(scheduleNames).asList();
                for (String scheduleName : scheduleNames) {
                    try {
                        AccountThermostatSchedule accountThermostatSchedule = accountThermostatScheduleDao.getSchedulesForAccountByScheduleName(customerAccount
                                .getAccountId(), scheduleName);
                        resp.addContent(buildResponseForAccountThermostatSchedule(customerAccount, accountThermostatSchedule));
                    } catch (EmptyResultDataAccessException e) {
                        Element fe;
                        if(scheduleName.isEmpty()){
                            fe = XMLFailureGenerator.generateFailure(thermostatSchedule, e, "OtherException", "Schedule name can't be empty.");
                        }else{
                            fe = XMLFailureGenerator.generateFailure(thermostatSchedule, e, "OtherException", "No schedule named: " + scheduleName);
                        }
                        resp.addContent(fe);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(thermostatSchedule, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
        }
        return resp;
    }

    /**
     * This method generates an xml account thermostat schedule.
     */
    private Element buildResponseForAccountThermostatSchedule(CustomerAccount customerAccount, AccountThermostatSchedule accountThermostatSchedule) {
        Element thermostatScheduleElem = new Element("thermostatSchedule", ns);
        thermostatScheduleElem.setAttribute("thermostatType", XmlUtils.toXmlRepresentation(accountThermostatSchedule.getThermostatType()));
        thermostatScheduleElem.setAttribute("thermostatScheduleMode", XmlUtils.toXmlRepresentation(accountThermostatSchedule.getThermostatScheduleMode()));

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
                coolTempElem.setAttribute("unit", XmlUtils.toXmlRepresentation(TemperatureUnit.FAHRENHEIT));
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