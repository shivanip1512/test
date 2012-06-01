package com.cannontech.web.support.development;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.ConnectionFactory;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.message.alarm.RfnAlarm;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.message.event.RfnEventArchiveRequest;
import com.cannontech.amr.rfn.message.event.RfnUomModifierSet;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.common.rfn.message.RfnArchiveRequest;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.cannontech.web.support.development.model.TestEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller("/development/rfn/*")
@CheckDevelopmentMode
public class RfnMeterArchiveTestController {

    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    
    @RequestMapping
    public String viewBase() {
        return "development/rfn/viewBase.jsp";
    }
    
    @RequestMapping
    public String viewMeterReadArchiveRequest() {
        return "development/rfn/viewMeterReadArchive.jsp";
    }
    
    @RequestMapping
    public String viewEventArchiveRequest(ModelMap model) {
        return setupEventAlarmAttributes(model, new TestEvent());
    }

    private String setupEventAlarmAttributes(ModelMap model, TestEvent event) {
        List<RfnConditionType> rfnConditionTypes = Lists.newArrayList(RfnConditionType.values());
        model.addAttribute("rfnConditionTypes", rfnConditionTypes);
        ArrayList<RfnConditionDataType> dataTypes = Lists.newArrayList(RfnConditionDataType.values());
        model.addAttribute("dataTypes", dataTypes);
        model.addAttribute("event", event);
        return "development/rfn/viewEventArchive.jsp";
    }

    @RequestMapping("sendMeterArchiveRequest")
    public String send(int serialFrom, int serialTo, String manufacturer, String model, Double value, boolean random, String uom, 
                       boolean quad1,
                       boolean quad2,
                       boolean quad3,
                       boolean quad4) {
        JmsTemplate jmsTemplate;
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        
        for (int i = serialFrom; i <= serialTo; i++) {
            RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
            
            RfnMeterReadingData data = new RfnMeterReadingData();
            data.setTimeStamp(new Instant().getMillis());
            RfnIdentifier meterIdentifier = new RfnIdentifier(Integer.toString(i), manufacturer, model);
            data.setRfnIdentifier(meterIdentifier);
            
            List<ChannelData> dataList = Lists.newArrayList();
            ChannelData channelData = new ChannelData();
            channelData.setChannelNumber(0);
            channelData.setStatus(ChannelDataStatus.OK);
            
            channelData.setUnitOfMeasure(uom);
            Set<String> modifiers = Sets.newHashSet();
            if (quad1) modifiers.add("Quadrant 1");
            if (quad2) modifiers.add("Quadrant 2");
            if (quad3) modifiers.add("Quadrant 3");
            if (quad4) modifiers.add("Quadrant 4");
            
            if (random) {
                value = Math.random() * 1000;
            }
            channelData.setValue(value);
            dataList.add(channelData);
            
            data.setChannelDataList(dataList);
            
            message.setData(data);
            message.setDataPointId(1);
            
            if (model.contains("water")) {
                message.setReadingType(RfnMeterReadingType.INTERVAL);
                modifiers.add("Kilo");
            } else {
                message.setReadingType(RfnMeterReadingType.CURRENT);
            }
            channelData.setUnitOfMeasureModifiers(modifiers);
            
            jmsTemplate.convertAndSend("yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest", message);
        }
        
        return "redirect:viewMeterReadArchiveRequest";
    }
    
    @RequestMapping
    public String sendEvent(@ModelAttribute TestEvent event, ModelMap model, FlashScope flashScope) {
        int numEventsSent = 0;
        for (int i = event.getSerialFrom(); i <= event.getSerialTo(); i++) {
            for (int j=0; j < event.getNumEventPerMeter(); j++) {
                buildAndSendEvent(event, i);
                numEventsSent++;
            }
            for (int j=0; j < event.getNumAlarmPerMeter(); j++) {
                buildAndSendAlarm(event, i);
                numEventsSent++;
            }
        }
        
        if (numEventsSent > 0) {
            MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.support.rfnTest.numEventsSent", numEventsSent);
            flashScope.setConfirm(createMessage);
        } else {
            MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.support.rfnTest.numEventsSent", numEventsSent);
            flashScope.setError(createMessage);
        }
        
        return setupEventAlarmAttributes(model, event);
    }
    
    private void buildAndSendEvent(TestEvent event, int serialNum) {
        RfnEvent rfnEvent = buildEvent(event, new RfnEvent(), serialNum);
        
        RfnEventArchiveRequest archiveRequest = new RfnEventArchiveRequest();
        archiveRequest.setEvent(rfnEvent);
        archiveRequest.setDataPointId(1);
        sendArchiveRequest("yukon.qr.obj.amr.rfn.EventArchiveRequest", archiveRequest);
    }
    
    private void buildAndSendAlarm(TestEvent event, int serialNum) {
        RfnAlarm rfnAlarm = buildEvent(event, new RfnAlarm(), serialNum);
        
        RfnAlarmArchiveRequest archiveRequest = new RfnAlarmArchiveRequest();
        archiveRequest.setAlarm(rfnAlarm);
        archiveRequest.setDataPointId(1);
        sendArchiveRequest("yukon.qr.obj.amr.rfn.AlarmArchiveRequest", archiveRequest);
    }

    private <T extends RfnEvent> T buildEvent(TestEvent testEvent, T rfnEvent, int serialNum) {
        rfnEvent.setType(testEvent.getRfnConditionType());
        RfnIdentifier meterIdentifier =
            new RfnIdentifier(Integer.toString(serialNum), testEvent.getManufacturer(), testEvent.getModel());
        rfnEvent.setRfnIdentifier(meterIdentifier);
        
        long timestamp;
        if (testEvent.getTimestamp() == null) {
            timestamp = new Instant().getMillis();
        } else {
            timestamp = testEvent.getTimestamp().getMillis();
        }
        rfnEvent.setTimeStamp(timestamp);
        
        if (testEvent.getOutageStartTime() == null) {
            testEvent.setOutageStartTime(timestamp - 60000); // 60 seconds ago
        } else if (testEvent.getOutageStartTime() == RfnInvalidValues.OUTAGE_DURATION.getValue()) {
            testEvent.setOutageStartTime(null);
        }

        Map<RfnConditionDataType, Object> testEventMap = Maps.newHashMap();
        copyDataTypesToMap(testEvent, testEventMap);
        
        // reset OutageStartTime value
        if (testEvent.getOutageStartTime() == null) {
            testEvent.setOutageStartTime(RfnInvalidValues.OUTAGE_DURATION.getValue());
        } else {
            testEvent.setOutageStartTime(null);
        }
        
        rfnEvent.setEventData(testEventMap);
        return rfnEvent;
    }
    
    public void copyDataTypesToMap(TestEvent testEvent, Map<RfnConditionDataType, Object> rfnEventMap) {
        rfnEventMap.put(RfnConditionDataType.CLEARED, testEvent.getCleared());
        rfnEventMap.put(RfnConditionDataType.COUNT, testEvent.getCount());
        rfnEventMap.put(RfnConditionDataType.DIRECTION, testEvent.getDirection());
        rfnEventMap.put(RfnConditionDataType.MEASURED_VALUE, testEvent.getMeasuredValue());
        rfnEventMap.put(RfnConditionDataType.OUTAGE_START_TIME, testEvent.getOutageStartTime());
        rfnEventMap.put(RfnConditionDataType.THRESHOLD_VALUE, testEvent.getThresholdValue());
        rfnEventMap.put(RfnConditionDataType.UOM, testEvent.getUom());
        
        Set<String> stringSet = Sets.newHashSet(StringUtils.split(testEvent.getUomModifiers(), ","));
        RfnUomModifierSet rfnUomModifierSet = new RfnUomModifierSet();
        rfnUomModifierSet.setUomModifiers(stringSet);
        rfnEventMap.put(RfnConditionDataType.UOM_MODIFIERS, rfnUomModifierSet);
    }
    
    private <R extends RfnArchiveRequest> void sendArchiveRequest(String queueName, R archiveRequest) {
        JmsTemplate jmsTemplate;
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.convertAndSend(queueName, archiveRequest);
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder binder, YukonUserContext userContext) {
        EnumPropertyEditor.register(binder, RfnConditionType.class);
        
        PropertyEditor instantEditor =
                datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM,
                                                                   userContext,
                                                                   BlankMode.ERROR);
            binder.registerCustomEditor(Instant.class, instantEditor);
    }
    
}