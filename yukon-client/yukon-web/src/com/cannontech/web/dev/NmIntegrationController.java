package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.model.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewaySimulatorService;
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
import com.cannontech.web.dev.service.YsmJmxQueryService;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/rfn/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NmIntegrationController {

    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private RfnEventTestingService rfnEventTestingService;
    @Autowired private RfnPerformanceVerificationService performanceVerificationService;
    @Autowired private RfnLcrDataSimulatorService dataSimulator;
    @Autowired private YsmJmxQueryService jmxQueryService;
    @Autowired private RfnGatewayDataCache gatewayCache;
    @Autowired private RfnGatewaySimulatorService gatewaySimService;
    
    private JmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(NmIntegrationController.class);
    private static final String meterReadServiceBean = "com.cannontech.yukon.ServiceManager:name=meterReadingArchiveRequestListener,type=MeterReadingArchiveRequestListener";
    private static final String meterReadQueueBean = "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";
    private static final String lcrReadServiceBean = "com.cannontech.yukon.ServiceManager:name=lcrReadingArchiveRequestListener,type=LcrReadingArchiveRequestListener";
    private static final String lcrReadQueueBean = "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";
    private static final String gatewayServiceBean = "com.cannontech.yukon.ServiceManager:name=gatewayArchiveRequestListener,type=GatewayArchiveRequestListener";
    private static final String gatewayArchiveReqQueueBean = "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayArchiveRequest";
    private static final String gatewayDataReqQueueBean = "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayDataRequest";
    private static final String gatewayDataQueueBean = "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayData";
    private static final String rfDaArchiveQueueBean = "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.da.rfn.RfDaArchiveRequest";
    private static final DecimalFormat df = new DecimalFormat("##,###.## ms");
    
    @RequestMapping("viewBase")
    public String viewBase(ModelMap model) {
        
        model.addAttribute("data", getIntegrationData());
        
        return "rfn/viewBase.jsp";
    }
    
    @RequestMapping("data")
    public @ResponseBody List<Map<String, Object>> data() {
        
        return getIntegrationData();
    }
    
    private List<Map<String, Object>> getIntegrationData() {
        
        List<Map<String, Object>> data = new ArrayList<>();
        
        try {
            // Meter Read Stats
            Map<String, Object> meterData = new LinkedHashMap<>();
            ObjectName meterReadService = ObjectName.getInstance(meterReadServiceBean);
            meterData.put("meter-reads-archived", ImmutableMap.of(
                    "name", "Meter Reads Archived", 
                    "value", jmxQueryService.get(meterReadService, "ArchivedReadings")));
            meterData.put("meter-reads-requests-processed", ImmutableMap.of(
                    "name", "Meter Reads Requests Processed", 
                    "value", jmxQueryService.get(meterReadService, "ProcessedArchiveRequest")));
            ObjectName meterReadQueue = ObjectName.getInstance(meterReadQueueBean);
            meterData.put("meter-reads-enqueue-count", ImmutableMap.of(
                    "name", "Meter Reads Enqueue Count", 
                    "value", jmxQueryService.get(meterReadQueue, "EnqueueCount")));
            meterData.put("meter-reads-queue-size", ImmutableMap.of(
                    "name", "Meter Reads Queue Size", 
                    "value", jmxQueryService.get(meterReadQueue, "QueueSize")));
            Double mraet = (Double) jmxQueryService.get(meterReadQueue, "AverageEnqueueTime");
            meterData.put("meter-reads-average-enqueue-time", ImmutableMap.of(
                    "name", "Meter Reads Average Enqueue Time", 
                    "value", df.format(mraet)));
            data.add(meterData);
            
            // LCR Read Stats
            Map<String, Object> lcrData = new LinkedHashMap<>();
            ObjectName lcrReadService = ObjectName.getInstance(lcrReadServiceBean);
            lcrData.put("lcr-reads-archived", ImmutableMap.of(
                    "name", "LCR Reads Archived", 
                    "value", jmxQueryService.get(lcrReadService, "ArchivedReadings")));
            lcrData.put("lcr-reads-requests-processed", ImmutableMap.of(
                    "name", "LCR Reads Requests Processed", 
                    "value", jmxQueryService.get(lcrReadService, "ProcessedArchiveRequest")));
            ObjectName lcrReadQueue = ObjectName.getInstance(lcrReadQueueBean);
            lcrData.put("lcr-reads-enqueue-count", ImmutableMap.of(
                    "name", "LCR Reads Enqueue Count", 
                    "value", jmxQueryService.get(lcrReadQueue, "EnqueueCount")));
            lcrData.put("lcr-reads-queue-size", ImmutableMap.of(
                    "name", "LCR Reads Queue Size", 
                    "value", jmxQueryService.get(lcrReadQueue, "QueueSize")));
            Double lraet = (Double) jmxQueryService.get(lcrReadQueue, "AverageEnqueueTime");
            lcrData.put("lcr-reads-average-enqueue-time", ImmutableMap.of(
                    "name", "LCR Reads Average Enqueue Time", 
                    "value", df.format(lraet)));
            data.add(lcrData);
            
            //RF DA Archive Stats
            Map<String, Object> rfDaData = new LinkedHashMap<>();
            ObjectName rfDaArchiveQueue = ObjectName.getInstance(rfDaArchiveQueueBean);
            rfDaData.put("rfda-archive-enqueue-count", ImmutableMap.of(
                    "name", "RF DA Archive Enqueue Count", 
                    "value", jmxQueryService.get(rfDaArchiveQueue, "EnqueueCount")));
            rfDaData.put("rfda-archive-queue-size", ImmutableMap.of(
                    "name", "RF DA Queue Size", 
                    "value", jmxQueryService.get(rfDaArchiveQueue, "QueueSize")));
            Double rfdaaet = (Double) jmxQueryService.get(rfDaArchiveQueue, "AverageEnqueueTime");
            rfDaData.put("rfda-archive-average-enqueue-time", ImmutableMap.of(
                    "name", "RF DA Average Enqueue Time", 
                    "value", df.format(rfdaaet)));
            data.add(rfDaData);
            
            // Gateway Archive Stats
            Map<String, Object> gatewayArchiveData = new LinkedHashMap<>();
            ObjectName gatewayService = ObjectName.getInstance(gatewayServiceBean);
            gatewayArchiveData.put("gateway-archive-requests-processed", ImmutableMap.of(
                    "name", "Gateway Archive Requests Processed", 
                    "value", jmxQueryService.get(gatewayService, "ProcessedArchiveRequest")));
            ObjectName gatewayQueue = ObjectName.getInstance(gatewayArchiveReqQueueBean);
            gatewayArchiveData.put("gateway-archive-enqueue-count", ImmutableMap.of(
                    "name", "Gateway Archive Enqueue Count", 
                    "value", jmxQueryService.get(gatewayQueue, "EnqueueCount")));
            gatewayArchiveData.put("gateway-archive-dequeue-count", ImmutableMap.of(
                    "name", "Gateway Archive Dequeue Count", 
                    "value", jmxQueryService.get(gatewayQueue, "DequeueCount")));
            gatewayArchiveData.put("gateway-archive-queue-size", ImmutableMap.of(
                    "name", "Gateway Archive Queue Size", 
                    "value", jmxQueryService.get(gatewayQueue, "QueueSize")));
            Double gaaet = (Double) jmxQueryService.get(gatewayQueue, "AverageEnqueueTime");
            gatewayArchiveData.put("gateway-archive-average-enqueue-time", ImmutableMap.of(
                    "name", "Gateway Archive Average Enqueue Time", 
                    "value", df.format(gaaet)));
            data.add(gatewayArchiveData);
            
            // Gateway Data Stats
            Map<String, Object> gatewayData = new LinkedHashMap<>();
            gatewayQueue = ObjectName.getInstance(gatewayDataReqQueueBean);
            gatewayData.put("gateway-data-req-enqueue-count", ImmutableMap.of(
                    "name", "Gateway Data Request Enqueue Count", 
                    "value", jmxQueryService.get(gatewayQueue, "EnqueueCount")));
            gatewayData.put("gateway-data-req-dequeue-count", ImmutableMap.of(
                    "name", "Gateway Data Request Dequeue Count", 
                    "value", jmxQueryService.get(gatewayQueue, "DequeueCount")));
            gatewayData.put("gateway-data-req-queue-size", ImmutableMap.of(
                    "name", "Gateway Data Request Queue Size", 
                    "value", jmxQueryService.get(gatewayQueue, "QueueSize")));
            Double gdaet = (Double) jmxQueryService.get(gatewayQueue, "AverageEnqueueTime");
            gatewayData.put("gateway-data-req-average-enqueue-time", ImmutableMap.of(
                    "name", "Gateway Data Request Average Enqueue Time", 
                    "value", df.format(gdaet)));
            
            gatewayQueue = ObjectName.getInstance(gatewayDataQueueBean);
            gatewayData.put("gateway-data-enqueue-count", ImmutableMap.of(
                    "name", "Gateway Data Enqueue Count", 
                    "value", jmxQueryService.get(gatewayQueue, "EnqueueCount")));
            gatewayData.put("gateway-data-dequeue-count", ImmutableMap.of(
                    "name", "Gateway Data Dequeue Count", 
                    "value", jmxQueryService.get(gatewayQueue, "DequeueCount")));
            gatewayData.put("gateway-data-queue-size", ImmutableMap.of(
                    "name", "Gateway Data Queue Size", 
                    "value", jmxQueryService.get(gatewayQueue, "QueueSize")));
            Double gdraet = (Double) jmxQueryService.get(gatewayQueue, "AverageEnqueueTime");
            gatewayData.put("gateway-data-average-enqueue-time", ImmutableMap.of(
                    "name", "Gateway Data Average Enqueue Time", 
                    "value", df.format(gdraet)));
            data.add(gatewayData);
            
        } catch (Exception e) {
            log.error("Couldn't look up value.", e);
        }
        
        return data;
    }
    
    @RequestMapping("gatewaySimulator")
    public String gatewaySimulator(ModelMap model) {
        model.addAttribute("ackTypes", RfnGatewayUpgradeRequestAckType.values());
        model.addAttribute("acceptedUpdateStatusTypes", SimulatedCertificateReplySettings.acceptedUpdateStatusTypes);
        model.addAttribute("autoDataReplyActive", gatewaySimService.isAutoDataReplyActive());
        model.addAttribute("autoCertificateReplyActive", gatewaySimService.isAutoUpgradeReplyActive());
        
        return "rfn/gatewayDataSimulator.jsp";
    }
    
    @RequestMapping("createNewGateway")
    public String createNewGateway(@RequestParam String name, 
                                   @RequestParam String serial, 
                                   @RequestParam(defaultValue="false") boolean isGateway2,
                                   FlashScope flash) {
        
        gatewaySimService.sendGatewayArchiveRequest(name, serial, isGateway2);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.gatewayCreated", name));
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("enableGatewayDataReply")
    public String enableGatewayDataReply(@RequestParam(defaultValue="false") boolean alwaysGateway2, FlashScope flash) {
        
        SimulatedGatewayDataSettings dataSettings = new SimulatedGatewayDataSettings();
        dataSettings.setReturnGwy800Model(alwaysGateway2);
        boolean autoDataReplyActive = gatewaySimService.startAutoDataReply(dataSettings);
        
        if (autoDataReplyActive == true) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess"));
        } else {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartFailed"));
        }
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("disableGatewayDataReply")
    public String disableGatewayDataReply() {
        gatewaySimService.stopAutoDataReply();
        while (gatewaySimService.isAutoDataReplyActive()) {
            //wait for sim to stop
        }
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("enableGatewayCertificateReply")
    public String enableGatewayCertificateReply(@RequestParam RfnGatewayUpgradeRequestAckType ackType,
                                                @RequestParam GatewayCertificateUpdateStatus updateStatus,
                                                FlashScope flash) {
        
        SimulatedCertificateReplySettings certSettings = new SimulatedCertificateReplySettings();
        certSettings.setAckType(ackType);
        certSettings.setDeviceUpdateStatus(updateStatus);
        boolean autoUpdateReplyActive = gatewaySimService.startAutoCertificateReply(certSettings);
        
        if (autoUpdateReplyActive == true) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess"));
        } else {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartFailed"));
        }
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("disableGatewayCertificateReply")
    public String disableGatewayCertificateReply() {
        gatewaySimService.stopAutoCertificateReply();
        while (gatewaySimService.isAutoUpgradeReplyActive()) {
            //wait for sim to stop
        }
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("viewMeterReadArchiveRequest")
    public String viewMeterReadArchiveRequest() {
        return "rfn/viewMeterReadArchive.jsp";
    }
    
    @RequestMapping("viewLocationArchiveRequest")
    public String viewLocationArchiveRequest() {
        return "rfn/viewLocationArchive.jsp";
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
    
    @RequestMapping("viewRfDaArchiveRequest")
    public String viewRfDaArchiveRequest() {
        return "rfn/viewRfDaArchive.jsp";
    }
    
    @RequestMapping("viewGatewayDataSimulator")
    public String viewGatewayDataSimulator() {
        return "rfn/gatewayDataSimulator.jsp";
    }
    
    @RequestMapping("sendPerformanceVerification")
    public String sendPerformanceVerification(ModelMap model) {
        model.addAttribute("drReports", DRReport.values());
        performanceVerificationService.sendPerformanceVerificationMessage();
        return "rfn/viewLcrReadArchive.jsp";
    }

    @RequestMapping("viewLcrDataSimulator")
    public String viewLcrDataSimulator(ModelMap model) {
        model.addAttribute("isRunning", dataSimulator.isRunning());
        SimulatorSettings currentSettings = dataSimulator.getCurrentSettings();
        if (currentSettings == null) {
            currentSettings = new SimulatorSettings(100000, 200000, 300000, 320000, 123456789, 1390000000, 0.0);
        }
        model.addAttribute("currentSettings", currentSettings);
        return "rfn/dataSimulator.jsp";
    }

    @RequestMapping(value="startDataSimulator", method=RequestMethod.GET)
    public String startDataSimulator(@RequestParam int lcr6200serialFrom, @RequestParam int lcr6200serialTo,
            @RequestParam int lcr6600serialFrom, @RequestParam int lcr6600serialTo,
            @RequestParam long messageId, @RequestParam long messageIdTimestamp, double daysBehind) {
        SimulatorSettings settings = new SimulatorSettings(lcr6200serialFrom, lcr6200serialTo,
            lcr6600serialFrom, lcr6600serialTo, messageId, messageIdTimestamp, daysBehind);

        dataSimulator.startSimulator(settings);

        return "redirect:viewLcrDataSimulator";
    }

    @RequestMapping(value="stopDataSimulator", method=RequestMethod.GET)
    public String stopDataSimulator() {
        dataSimulator.stopSimulator();
        return "redirect:viewLcrDataSimulator";
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
    public String send(int serialFrom, int serialTo, String manufacturer, String model, Double value, 
            RfnMeterReadingType type, boolean random, String uom, boolean quad1, boolean quad2,
            boolean quad3, boolean quad4, boolean max, boolean min, boolean avg, boolean phaseA, 
            boolean phaseB, boolean phaseC, boolean touRateA, boolean touRateB, boolean touRateC,
            boolean touRateD, boolean touRateE, boolean netFlow, boolean coincident, boolean harmonic, boolean cumulative) {
        
        rfnEventTestingService.sendMeterArchiveRequests(serialFrom, serialTo, manufacturer, model, value, type, random, 
                uom, quad1, quad2, quad3, quad4, max, min, avg, phaseA, phaseB, phaseC,
                touRateA, touRateB, touRateC, touRateD, touRateE, netFlow, coincident, harmonic, cumulative);
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
    
    @RequestMapping("sendLocationArchiveRequest")
    public String sendLocationArchiveRequest(int serialFrom, int serialTo, String manufacturer, String model, String latitude, String longitude) { 
        rfnEventTestingService.sendLocationResponse(serialFrom, serialTo, manufacturer, model, Double.parseDouble(latitude), Double.parseDouble(longitude));
        return "redirect:viewLocationArchiveRequest";
    }
    
    @RequestMapping("sendEvent")
    public String sendEvent(@ModelAttribute RfnTestEvent event, ModelMap model, FlashScope flashScope) {
        int numEventsSent = rfnEventTestingService.sendEventsAndAlarms(event);
        
        if (numEventsSent > 0) {
            MessageSourceResolvable createMessage = 
                    new YukonMessageSourceResolvable("yukon.web.modules.support.rfnTest.numEventsSent", numEventsSent);
            flashScope.setConfirm(createMessage);
        } else {
            MessageSourceResolvable createMessage = 
                    new YukonMessageSourceResolvable("yukon.web.modules.support.rfnTest.numEventsSent", numEventsSent);
            flashScope.setError(createMessage);
        }
        
        return setupEventAlarmAttributes(model, event);
    }
    
    @RequestMapping("sendRfDaArchiveRequest")
    public String sendRfDaArchiveRequest(int serial, String manufacturer, String model) {
        rfnEventTestingService.sendRfDaArchiveRequest(serial, manufacturer, model);
        return "redirect:viewRfDaArchiveRequest";
    }
    
    @RequestMapping("calc-stress-test")
    public void calcStressTest() {
        rfnEventTestingService.calculationStressTest();
    }
    
    @RequestMapping("clear-gateway-cache")
    public void clearGatewayCache() {
        gatewayCache.getCache().asMap().clear();
    }
    
    @RequestMapping("resend-startup")
    public void startup(HttpServletResponse resp) {
        RfnArchiveStartupNotification notif = new RfnArchiveStartupNotification();
        try {
            jmsTemplate.convertAndSend("yukon.notif.obj.common.rfn.ArchiveStartupNotification", notif);
            resp.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder binder, YukonUserContext userContext) {
        
        EnumPropertyEditor.register(binder, RfnConditionType.class);
        PropertyEditor instantEditor = datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, 
                userContext, BlankMode.ERROR);
        binder.registerCustomEditor(Instant.class, instantEditor);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
    
}