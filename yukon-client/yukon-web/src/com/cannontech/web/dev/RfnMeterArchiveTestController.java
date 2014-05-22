package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.development.service.impl.DRReport;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.service.RfnLcrDataSimulatorService;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/rfn/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class RfnMeterArchiveTestController {

    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private RfnEventTestingService rfnEventTestingService;
    @Autowired private RfnPerformanceVerificationService performanceVerificationService;
    @Autowired private RfnLcrDataSimulatorService dataSimulator;
    
    private JmsTemplate jmsTemplate;

    @RequestMapping("viewBase")
    public String viewBase() {
        return "rfn/viewBase.jsp";
    }
    
    @RequestMapping("viewMeterReadArchiveRequest")
    public String viewMeterReadArchiveRequest() {
        return "rfn/viewMeterReadArchive.jsp";
    }
    
    @RequestMapping("viewEventArchiveRequest")
    public String viewEventArchiveRequest(ModelMap model) {
        return setupEventAlarmAttributes(model, new RfnTestEvent());
    }
    
    @RequestMapping("viewLcrReadArchiveRequest")
    public String viewLcrReadArchiveRequest(ModelMap model) {
        model.addAttribute("drReports", DRReport.values());
        return "rfn/viewLcrReadArchive.jsp";
    }
    
    @RequestMapping("viewLcrArchiveRequest")
    public String viewLcrArchiveRequest() {
        return "rfn/viewLcrArchive.jsp";
    }
    
    @RequestMapping("sendPerformanceVerification")
    public String sendPerformanceVerification(ModelMap model) {
        model.addAttribute("drReports", DRReport.values());
        performanceVerificationService.sendPerformanceVerificationMessage();
        return "rfn/viewLcrReadArchive.jsp";
    }

    @RequestMapping("viewLcrDataSimulator")
    public String viewLcrDataSimulator() {
        return "rfn/dataSimulator.jsp";
    }

    @RequestMapping(value="startDataSimulator", method=RequestMethod.GET)
    public String startDataSimulator(
            @RequestParam(value = "lcr6200serialFrom", required = true) String lcr6200serialFrom,
            @RequestParam(value = "lcr6200serialTo", required = true) String lcr6200serialTo,
            @RequestParam(value = "lcr6600serialTo", required = true) String lcr6600serialTo,
            @RequestParam(value = "lcr6600serialFrom", required = true) String lcr6600serialFrom,
            @RequestParam(value = "messageId", required = true) String messageId,
            @RequestParam(value = "messageIdTimestamp", required = true) String messageIdTimestamp) {
        
        SimulatorSettings settings = new SimulatorSettings(
                Integer.parseInt(lcr6200serialFrom), Integer.parseInt(lcr6200serialTo), 
                Integer.parseInt(lcr6600serialFrom), Integer.parseInt(lcr6600serialTo),
                Long.parseLong(messageId), Long.parseLong(messageIdTimestamp));
        
        dataSimulator.startSimulator(settings);
        
        return "rfn/dataSimulator.jsp";
    }

    @RequestMapping("stopDataSimulator")
    public String stopDataSimulator() {
        dataSimulator.stopSimulator();
        return "rfn/dataSimulator.jsp";
    }

    private String setupEventAlarmAttributes(ModelMap model, RfnTestEvent event) {
        List<RfnConditionType> rfnConditionTypes = Lists.newArrayList(RfnConditionType.values());
        model.addAttribute("rfnConditionTypes", rfnConditionTypes);
        ArrayList<RfnConditionDataType> dataTypes = Lists.newArrayList(RfnConditionDataType.values());
        model.addAttribute("dataTypes", dataTypes);
        model.addAttribute("event", event);
        return "rfn/viewEventArchive.jsp";
    }

    @RequestMapping("sendMeterArchiveRequest")
    public String send(int serialFrom, int serialTo, String manufacturer, String model, Double value, RfnMeterReadingType type, boolean random, String uom, 
                       boolean quad1, boolean quad2, boolean quad3, boolean quad4, boolean max, boolean min, boolean avg,
                       boolean phaseA, boolean phaseB, boolean phaseC, boolean touRateA, boolean touRateB, boolean touRateC,
                       boolean touRateD, boolean touRateE, boolean netFlow, boolean coincident, boolean harmonic, boolean cumulative) {
        rfnEventTestingService.sendMeterArchiveRequests(serialFrom,
                                                        serialTo,
                                                        manufacturer,
                                                        model,
                                                        value,
                                                        type,
                                                        random,
                                                        uom,
                                                        quad1,
                                                        quad2,
                                                        quad3,
                                                        quad4,
                                                        max,
                                                        min,
                                                        avg,
                                                        phaseA,
                                                        phaseB,
                                                        phaseC,
                                                        touRateA,
                                                        touRateB,
                                                        touRateC,
                                                        touRateD,
                                                        touRateE,
                                                        netFlow,
                                                        coincident,
                                                        harmonic,
                                                        cumulative);
        return "redirect:viewMeterReadArchiveRequest";
    }
    
    @RequestMapping("sendLcrReadArchiveRequest")
    public String sendLcrReadArchive(int serialFrom, int serialTo, int days, String drReport) throws IOException {
        rfnEventTestingService.sendLcrReadArchive(serialFrom, serialTo, days, DRReport.valueOf(drReport));
        return "redirect:viewLcrReadArchiveRequest";
    }
    
    @RequestMapping("sendLcrArchiveRequest")
    public String sendLcrArchive(int serialFrom, int serialTo, String manufacturer, String model) {
        rfnEventTestingService.sendLcrArchiveRequest(serialFrom, serialTo, manufacturer, model);
        return "redirect:viewLcrArchiveRequest";
    }
    
    @RequestMapping("sendEvent")
    public String sendEvent(@ModelAttribute RfnTestEvent event, ModelMap model, FlashScope flashScope) {
        int numEventsSent = rfnEventTestingService.sendEventsAndAlarms(event);
        
        if (numEventsSent > 0) {
            MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.support.rfnTest.numEventsSent", numEventsSent);
            flashScope.setConfirm(createMessage);
        } else {
            MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.support.rfnTest.numEventsSent", numEventsSent);
            flashScope.setError(createMessage);
        }
        
        return setupEventAlarmAttributes(model, event);
    }
    
    @RequestMapping("calcStressTest")
    public void calcStressTest() {
        rfnEventTestingService.calculationStressTest();
    }
    
    @RequestMapping("resendStartup")
    public void resendStartup() {
        RfnArchiveStartupNotification notif = new RfnArchiveStartupNotification();
        jmsTemplate.convertAndSend("yukon.notif.obj.common.rfn.ArchiveStartupNotification", notif);
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
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
    
}