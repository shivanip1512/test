package com.cannontech.yukon.api.consumer.endpoint.endpointMappers;

import java.util.List;

import org.jdom2.Element;
import org.w3c.dom.Node;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.yukon.api.stars.model.SchedulePeriod;
import com.cannontech.yukon.api.stars.model.ThermostatSchedule;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class ThermostatScheduleElementRequestMapper implements ObjectMapper<Element, ThermostatSchedule> {

    @Override
    public ThermostatSchedule map(Element createThermostatScheduleRequestElement) throws ObjectMappingException {

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(createThermostatScheduleRequestElement);

        ThermostatSchedule thermostatSchedule = new ThermostatSchedule();        
        thermostatSchedule.setSchedulableThermostatType(yukonTemplate.evaluateAsEnum("y:*/@thermostatType", SchedulableThermostatType.class));
        thermostatSchedule.setScheduleName(yukonTemplate.evaluateAsString("y:*/y:scheduleName"));

        thermostatSchedule.setThermostatScheduleMode(yukonTemplate.evaluateAsEnum("y:*/@thermostatScheduleMode", ThermostatScheduleMode.class));
        List<Node> schedulePeriodNodes = yukonTemplate.evaluateAsNodeList("y:*/y:schedulePeriod");
        SetMultimap<TimeOfWeek, SchedulePeriod> schedulePeriodContainer = HashMultimap.create();
        for (Node schedulePeriodNode : schedulePeriodNodes) {
            YukonXPathTemplate schedulePeriodTemplate = YukonXml.getXPathTemplateForNode(schedulePeriodNode);
            schedulePeriodContainer.putAll(evaluateSchedulePeriod(schedulePeriodTemplate));
        }
        thermostatSchedule.setSchedulePeriodContainer(schedulePeriodContainer);
        
        return thermostatSchedule;
    }
    
    /**
     * This method evaluates the schedule period of the request, handling the thermostat schedule period, start time, cool temp, and heat temp
     * for the thermostat schedule.
     */
    private SetMultimap<TimeOfWeek, SchedulePeriod> evaluateSchedulePeriod(YukonXPathTemplate schedulePeriodTemplate) {
        SetMultimap<TimeOfWeek, SchedulePeriod> schedulePeriodContainer = HashMultimap.create();
        
        TimeOfWeek timeOfWeek = schedulePeriodTemplate.evaluateAsEnum("@timeOfWeek", TimeOfWeek.class);
        List<Node> thermostatSchedulePeriodNodes = schedulePeriodTemplate.evaluateAsNodeList("y:thermostatSchedulePeriod");
        for (Node thermostatSchedulePeriodNode : thermostatSchedulePeriodNodes) {
            YukonXPathTemplate thermostatSchedulePeriodTemplate = YukonXml.getXPathTemplateForNode(thermostatSchedulePeriodNode);

            SchedulePeriod schedulePeriod = new SchedulePeriod();
            schedulePeriod.setPeriodStartTime(thermostatSchedulePeriodTemplate.evaluateAsLocalTime("y:startTime"));
            schedulePeriod.setCoolTemperature(thermostatSchedulePeriodTemplate.evaluateAsTemperature("y:coolTemp"));
            schedulePeriod.setHeatTemperature(thermostatSchedulePeriodTemplate.evaluateAsTemperature("y:heatTemp"));
            
            schedulePeriodContainer.put(timeOfWeek, schedulePeriod);
        }

        return schedulePeriodContainer;
    }
}