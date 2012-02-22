package com.cannontech.yukon.api.consumer.endpoint;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Maps;

@Endpoint
public class ThermostatAndScheduleListRequestEndpoint {

    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(HardwareSummaryListRequestEndpoint.class);
    
    @Autowired private InventoryDao inventoryDao;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "thermostatAndScheduleListRequest")
    public Element invoke(Element thermostatAndScheduleListRequest, CustomerAccount customerAccount)
            throws Exception {
       
        XmlVersionUtils.verifyYukonMessageVersion(thermostatAndScheduleListRequest,XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(thermostatAndScheduleListRequest);
        
        // get all thermostats for the account id
        List<Thermostat> thermostats = inventoryDao.getThermostatsByAccountId(customerAccount.getAccountId());
        
        // get inventory ids
        List<Integer> inventoryIds = requestTemplate.evaluateAsIntegerList("//y:inventoryId");
        // only schedules for inventory ids provided should be included in the response
        // if no inventory ids provided all schedule information will be included the the response
        removeInapplicableThermostats(thermostats, inventoryIds);
        
        // init response
        Element response = new Element("ThermostatAndScheduleListResponse", ns);
        response.setAttribute(new Attribute("version", "1.0"));
        try {
            // get all the schedules
            Map<Thermostat, List<AccountThermostatSchedule>> thermostatSchedules = getThermostatSchedules(customerAccount.getAccountId(), thermostats);
            if(!thermostatSchedules.isEmpty()){
                //build a response
                buildResponse(customerAccount.getAccountId(), thermostatSchedules,  response);
            }
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(thermostatAndScheduleListRequest, e, "UserNotAuthorized", "The user is not authorized to retrieve thermostat and schedule list.");
            response.addContent(fe);
        }  catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(thermostatAndScheduleListRequest, e, "OtherException", "An exception has been caught.");
            response.addContent(fe);
            log.error(e.getMessage(), e);
        }
        return response;
    }
    
    private void removeInapplicableThermostats(List<Thermostat> thermostats, List<Integer> inventoryIds){
        if (!inventoryIds.isEmpty()) {
            ListIterator<Thermostat> iterator = thermostats.listIterator();
            while(iterator.hasNext()){
                Thermostat thermostat = iterator.next();
                if (!inventoryIds.contains(thermostat.getId())) {
                    iterator.remove();
                }
            }
        }
    }
    
    private Map<Thermostat, List<AccountThermostatSchedule>> getThermostatSchedules(Integer accountId, List<Thermostat> thermostats) {
        Map<SchedulableThermostatType, List<AccountThermostatSchedule>> schedulableThermostatTypes = Maps.newHashMap();
        Map<Thermostat, List<AccountThermostatSchedule>> thermostatSchedules = Maps.newLinkedHashMap();
        for (Thermostat thermostat : thermostats) { 
            if (!thermostatSchedules.containsKey(thermostat)) {
                List<AccountThermostatSchedule> schedules = schedulableThermostatTypes.get(thermostat.getSchedulableThermostatType());
                if(schedules == null){
                    schedules = accountThermostatScheduleDao.getAllAllowedSchedulesForAccountByType(accountId, thermostat.getSchedulableThermostatType());
                    schedulableThermostatTypes.put(thermostat.getSchedulableThermostatType(), schedules);
                }
                thermostatSchedules.put(thermostat , schedules);
            }
        }
        return thermostatSchedules;
    }

    private void buildResponse(Integer accountId, Map<Thermostat, List<AccountThermostatSchedule>> thermostatSchedules, Element response){
       
        Element ThermostatAndScheduleListResponse = new Element("thermostatAndScheduleList", ns);
        Element accountElement = new Element("account", ns);
        accountElement.addContent(XmlUtils.createIntegerElement("accountId", ns,  accountId));
        
        for (Thermostat thermostat : thermostatSchedules.keySet()) {
            Element thermostatElement = new Element("thermostat", ns);
            thermostatElement.addContent(XmlUtils.createStringElement("inventoryId", ns, String.valueOf(thermostat.getId())));
            thermostatElement.addContent(XmlUtils.createStringElement("deviceLabel", ns, String.valueOf(thermostat.getDeviceLabel())));
            thermostatElement.addContent(XmlUtils.createStringElement("thermostatType", ns, String.valueOf(thermostat.getSchedulableThermostatType())));
            for (AccountThermostatSchedule schedule : thermostatSchedules.get(thermostat)) {
                Element scheduleElement = new Element("schedule", ns);
                scheduleElement.addContent(XmlUtils.createIntegerElement("scheduleId", ns, schedule.getAccountThermostatScheduleId()));
                scheduleElement .addContent(XmlUtils.createStringElement("scheduleName", ns, schedule.getScheduleName()));
                thermostatElement.addContent(scheduleElement);
            }
            accountElement.addContent(thermostatElement);
        }
        ThermostatAndScheduleListResponse.addContent(accountElement);
        response.addContent(ThermostatAndScheduleListResponse);
    }
    
}


