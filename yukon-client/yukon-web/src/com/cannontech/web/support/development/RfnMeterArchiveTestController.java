package com.cannontech.web.support.development;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.google.common.collect.Lists;

@Controller("/development/rfn/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class RfnMeterArchiveTestController {

    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private RfnEventTestingService rfnEventTestingService;

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
        return setupEventAlarmAttributes(model, new RfnTestEvent());
    }
    
    @RequestMapping
    public String viewLcrReadArchiveRequest() {
        return "development/rfn/viewLcrReadArchive.jsp";
    }
    
    @RequestMapping
    public String viewLcrArchiveRequest() {
        return "development/rfn/viewLcrArchive.jsp";
    }

    private String setupEventAlarmAttributes(ModelMap model, RfnTestEvent event) {
        List<RfnConditionType> rfnConditionTypes = Lists.newArrayList(RfnConditionType.values());
        model.addAttribute("rfnConditionTypes", rfnConditionTypes);
        ArrayList<RfnConditionDataType> dataTypes = Lists.newArrayList(RfnConditionDataType.values());
        model.addAttribute("dataTypes", dataTypes);
        model.addAttribute("event", event);
        return "development/rfn/viewEventArchive.jsp";
    }

    @RequestMapping("sendMeterArchiveRequest")
    public String send(int serialFrom, int serialTo, String manufacturer, String model, Double value, boolean random, String uom, 
                       boolean quad1, boolean quad2, boolean quad3, boolean quad4, boolean max, boolean min, boolean avg,
                       boolean phaseA, boolean phaseB, boolean phaseC, boolean touRateA, boolean touRateB, boolean touRateC,
                       boolean touRateD, boolean touRateE, boolean netFlow, boolean coincident, boolean harmonic, boolean cumulative) {
        rfnEventTestingService.sendMeterArchiveRequests(serialFrom,
                                                        serialTo,
                                                        manufacturer,
                                                        model,
                                                        value,
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
    public String sendLcrReadArchive(int serialFrom, int serialTo, String manufacturer, String model) throws IOException {
        rfnEventTestingService.sendLcrReadArchive(serialFrom, serialTo, manufacturer, model);
        return "redirect:viewLcrReadArchiveRequest";
    }
    
    @RequestMapping("sendLcrArchiveRequest")
    public String sendLcrArchive(int serialFrom, int serialTo, String manufacturer, String model) {
        rfnEventTestingService.sendLcrArchiveRequest(serialFrom, serialTo, manufacturer, model);
        return "redirect:viewLcrArchiveRequest";
    }
    
    @RequestMapping
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