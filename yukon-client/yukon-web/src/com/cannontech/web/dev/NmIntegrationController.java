package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.ConnectionFactory;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.simulation.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareVersionReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.SimulatedUpdateReplySettings;
import com.cannontech.common.rfn.simulation.service.RfnGatewaySimulatorService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.development.model.DataSimulatorParameters;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.development.service.impl.DRReport;
import com.cannontech.dr.rfn.model.RfnLcrDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.service.RfnLcrDataSimulatorService;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.model.DataSimulatorStatus;
import com.cannontech.web.dev.service.YsmJmxQueryService;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    private final DataSimulatorStatus dataSimulatorStatus = new DataSimulatorStatus();
    private final DataSimulatorStatus existingDataSimulatorStatus = new DataSimulatorStatus();
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
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
        // Enums for selects
        model.addAttribute("ackTypes", RfnGatewayUpgradeRequestAckType.values());
        model.addAttribute("acceptedUpdateStatusTypes", SimulatedCertificateReplySettings.acceptedUpdateStatusTypes);
        model.addAttribute("firmwareVersionReplyTypes", RfnUpdateServerAvailableVersionResult.values());
        model.addAttribute("firmwareUpdateResultTypes", GatewayFirmwareUpdateRequestResult.values());
        model.addAttribute("gatewayUpdateResultTypes", GatewayUpdateResult.values());
        
        // Thread statuses
        model.addAttribute("autoDataReplyActive", gatewaySimService.isAutoDataReplyActive());
        model.addAttribute("autoUpdateReplyActive", gatewaySimService.isAutoUpdateReplyActive());
        model.addAttribute("autoCertificateReplyActive", gatewaySimService.isAutoCertificateUpgradeReplyActive());
        model.addAttribute("autoFirmwareReplyActive", gatewaySimService.isAutoFirmwareReplyActive());
        model.addAttribute("autoFirmwareVersionReplyActive", gatewaySimService.isAutoFirmwareVersionReplyActive());
        model.addAttribute("numberOfSimulatorsRunning", gatewaySimService.getNumberOfSimulatorsRunning());
        
        // Current settings
        model.addAttribute("certificateSettings", gatewaySimService.getCertificateSettings());
        model.addAttribute("firmwareSettings", gatewaySimService.getFirmwareSettings());
        model.addAttribute("firmwareVersionSettings", gatewaySimService.getFirmwareVersionSettings());
        model.addAttribute("dataSettings", gatewaySimService.getGatewayDataSettings());
        model.addAttribute("updateSettings", gatewaySimService.getGatewayUpdateSettings());
        
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
    
    @RequestMapping("enableAll")
    public String enableAllSimulators(FlashScope flash) {
        // Only start the threads that aren't already running. This lets the user specify non-default parameters for
        // some threads, then just bulk-start the rest.
        
        // Data reply
        boolean autoDataReplyActive = false;
        if (!gatewaySimService.isAutoDataReplyActive()) {
            SimulatedGatewayDataSettings dataSettings = new SimulatedGatewayDataSettings();
            dataSettings.setReturnGwy800Model(false);
            autoDataReplyActive = gatewaySimService.startAutoDataReply(dataSettings);
        }
        
        // Update reply
        boolean autoUpdateReplyActive = false;
        if (!gatewaySimService.isAutoUpdateReplyActive()) {
            SimulatedUpdateReplySettings updateSettings = new SimulatedUpdateReplySettings();
            updateSettings.setCreateResult(GatewayUpdateResult.SUCCESSFUL);
            updateSettings.setEditResult(GatewayUpdateResult.SUCCESSFUL);
            updateSettings.setDeleteResult(GatewayUpdateResult.SUCCESSFUL);
            autoUpdateReplyActive = gatewaySimService.startAutoUpdateReply(updateSettings);
        }
        
        // Certificate upgrade reply
        boolean autoCertUpdateReplyActive = false;
        if (!gatewaySimService.isAutoCertificateUpgradeReplyActive()) {
            SimulatedCertificateReplySettings certSettings = new SimulatedCertificateReplySettings();
            certSettings.setAckType(RfnGatewayUpgradeRequestAckType.ACCEPTED_FULLY);
            certSettings.setDeviceUpdateStatus(GatewayCertificateUpdateStatus.REQUEST_ACCEPTED);
            autoCertUpdateReplyActive = gatewaySimService.startAutoCertificateReply(certSettings);
        }
        
        // Firmware version reply
        boolean firmwareReplyActive = false;
        if (!gatewaySimService.isAutoFirmwareReplyActive()) {
            SimulatedFirmwareReplySettings settings = new SimulatedFirmwareReplySettings();
            settings.setResultType(GatewayFirmwareUpdateRequestResult.ACCEPTED);
            firmwareReplyActive = gatewaySimService.startAutoFirmwareReply(settings);
        }
        
        // Firmware upgrade reply
        boolean firmwareVersionReplyActive = false;
        if (!gatewaySimService.isAutoFirmwareVersionReplyActive()) {
            SimulatedFirmwareVersionReplySettings settings = new SimulatedFirmwareVersionReplySettings();
            settings.setVersion("1.2.3");
            settings.setResult(RfnUpdateServerAvailableVersionResult.SUCCESS);
            firmwareVersionReplyActive = gatewaySimService.startAutoFirmwareVersionReply(settings);
        }
        
        if (autoDataReplyActive && autoCertUpdateReplyActive && autoUpdateReplyActive && firmwareReplyActive && firmwareVersionReplyActive) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess"));
        } else {
            
        }
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("disableAll") 
    public String disableAllSimulators(FlashScope flash) {
        if (gatewaySimService.isAutoDataReplyActive()) {
            gatewaySimService.stopAutoDataReply();
        }
        if (gatewaySimService.isAutoUpdateReplyActive()) {
            gatewaySimService.stopAutoUpdateReply();
        }
        if (gatewaySimService.isAutoCertificateUpgradeReplyActive()) {
            gatewaySimService.stopAutoCertificateReply();
        }
        if (gatewaySimService.isAutoFirmwareVersionReplyActive()) {
            gatewaySimService.stopAutoFirmwareVersionReply();
        }
        if (gatewaySimService.isAutoFirmwareReplyActive()) {
            gatewaySimService.stopAutoFirmwareReply();
        }
        
        while(gatewaySimService.isAutoDataReplyActive() ||
                gatewaySimService.isAutoUpdateReplyActive() ||
                gatewaySimService.isAutoCertificateUpgradeReplyActive() ||
                gatewaySimService.isAutoFirmwareReplyActive() ||
                gatewaySimService.isAutoFirmwareVersionReplyActive()) {
            // wait for all sims to stop
        }
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("enableGatewayDataReply")
    public String enableGatewayDataReply(@RequestParam(defaultValue="false") boolean alwaysGateway2, FlashScope flash) {
        
        SimulatedGatewayDataSettings dataSettings = new SimulatedGatewayDataSettings();
        dataSettings.setReturnGwy800Model(alwaysGateway2);
        boolean autoDataReplyActive = gatewaySimService.startAutoDataReply(dataSettings);
        
        if (autoDataReplyActive) {
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
    
    @RequestMapping("enableGatewayUpdateReply")
    public String enableGatewayUpdateReply(@RequestParam GatewayUpdateResult createResult,
                                           @RequestParam GatewayUpdateResult editResult,
                                           @RequestParam GatewayUpdateResult deleteResult,
                                           FlashScope flash) {
        
        SimulatedUpdateReplySettings updateSettings = new SimulatedUpdateReplySettings();
        updateSettings.setCreateResult(createResult);
        updateSettings.setEditResult(editResult);
        updateSettings.setDeleteResult(deleteResult);
        boolean autoUpdateReplyActive = gatewaySimService.startAutoUpdateReply(updateSettings);
        
        if (autoUpdateReplyActive) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess"));
        } else {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartFailed"));
        }
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("disableGatewayUpdateReply")
    public String disableGatewayUpdateReply() {
        gatewaySimService.stopAutoUpdateReply();
        while (gatewaySimService.isAutoUpdateReplyActive()) {
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
        
        if (autoUpdateReplyActive) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess"));
        } else {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartFailed"));
        }
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("disableGatewayCertificateReply")
    public String disableGatewayCertificateReply() {
        gatewaySimService.stopAutoCertificateReply();
        while (gatewaySimService.isAutoCertificateUpgradeReplyActive()) {
            //wait for sim to stop
        }
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("enableGatewayFirmwareReply")
    public String enableGatewayFirmwareReply(@RequestParam GatewayFirmwareUpdateRequestResult updateResult,
                                             FlashScope flash) {
        
        SimulatedFirmwareReplySettings settings = new SimulatedFirmwareReplySettings();
        settings.setResultType(updateResult);
        boolean firmwareReplyActive = gatewaySimService.startAutoFirmwareReply(settings);
        
        if (firmwareReplyActive) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess"));
        } else {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartFailed"));
        }
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("disableGatewayFirmwareReply")
    public String disableGatewayFirmwareReply() {
        gatewaySimService.stopAutoFirmwareReply();
        while (gatewaySimService.isAutoFirmwareReplyActive()) {
            //wait for sim to stop
        }
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("enableFirmwareVersionReply")
    public String enableFirmwareVersionReply(@RequestParam RfnUpdateServerAvailableVersionResult replyType,
                                             @RequestParam String version,
                                             FlashScope flash) {
        
        SimulatedFirmwareVersionReplySettings settings = new SimulatedFirmwareVersionReplySettings();
        settings.setVersion(version);
        settings.setResult(replyType);
        boolean firmwareVersionReplyActive = gatewaySimService.startAutoFirmwareVersionReply(settings);
        
        if (firmwareVersionReplyActive) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess"));
        } else {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartFailed"));
        }
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("disableFirmwareVersionReply")
    public String disableFirmwareVersionReply() {
        gatewaySimService.stopAutoFirmwareVersionReply();
        while (gatewaySimService.isAutoFirmwareVersionReplyActive()) {
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
    public String viewLcrDataSimulator(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("isRunning", dataSimulator.isRunning());
        SimulatorSettings currentSettings = dataSimulator.getCurrentSettings();
        if (currentSettings == null) {
            currentSettings = new SimulatorSettings(100000, 200000, 300000, 320000, 123456789, 1390000000, 0.0);
        }
        model.addAttribute("currentSettings", currentSettings);
        model.addAttribute("dataSimulatorStatus", dataSimulatorStatus(userContext));
        model.addAttribute("existingDataSimulatorStatus", existingDataSimulatorStatus(userContext));
        return "rfn/dataSimulator.jsp";
    }


    @RequestMapping(value = "startDataSimulator")
    @ResponseBody
    public void startDataSimulator(final DataSimulatorParameters dataSimulatorParameters) {
        final AtomicBoolean isRunning6200 = dataSimulatorStatus.getIsRunning6200();
        if (!isRunning6200.compareAndSet(false, true)) {
            return;
        }

        final AtomicBoolean isCancelled6200 = dataSimulatorStatus.getIsCancelled6200();
        isCancelled6200.set(false);
        dataSimulatorStatus.setErrorMessage(null);
        SimulatorSettings settings =
            new SimulatorSettings(dataSimulatorParameters.getLcr6200serialFrom(),
                dataSimulatorParameters.getLcr6200serialTo(), dataSimulatorParameters.getLcr6600serialFrom(),
                dataSimulatorParameters.getLcr6600serialTo(), dataSimulatorParameters.getMessageId(),
                dataSimulatorParameters.getMessageIdTimestamp(), dataSimulatorParameters.getDaysBehind());
        dataSimulatorStatus.getNumComplete6200().set(0);
        dataSimulatorStatus.setNumTotal6200(dataSimulatorParameters.getLcr6200serialTo()
            - dataSimulatorParameters.getLcr6200serialFrom());
        // 6600
        final AtomicBoolean isRunning6600 = dataSimulatorStatus.getIsRunning6600();
        if (!isRunning6600.compareAndSet(false, true)) {
            return;
        }

        final AtomicBoolean isCancelled6600 = dataSimulatorStatus.getIsCancelled6600();
        isCancelled6600.set(false);

        dataSimulatorStatus.getNumComplete6600().set(0);
        dataSimulatorStatus.setNumTotal6600(dataSimulatorParameters.getLcr6600serialTo()
            - dataSimulatorParameters.getLcr6600serialFrom());
        // End
        dataSimulator.startSimulator(settings);

    }

    @RequestMapping(value = "stopDataSimulator")
    public void stopDataSimulator() {
        dataSimulatorStatus.getIsCancelled6200().set(true);
        dataSimulatorStatus.getIsCancelled6600().set(true);
        dataSimulator.stopSimulator();
    }

    @RequestMapping(value = "sendLcrDeviceMessages", method = RequestMethod.GET)
    @ResponseBody
    public void sendLcrDeviceMessages() {
        List<RfnDevice> rfnLcrDeviceList = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfLcrTypes());
        getDeviceCountByType(rfnLcrDeviceList);
        final AtomicBoolean isRunning6200 = existingDataSimulatorStatus.getIsRunning6200();

        // 6200
        if (!isRunning6200.compareAndSet(false, true)) {
            return;
        }

        final AtomicBoolean isCancelled6200 = existingDataSimulatorStatus.getIsCancelled6200();
        isCancelled6200.set(false);
        existingDataSimulatorStatus.setErrorMessage(null);
        existingDataSimulatorStatus.getNumComplete6200().set(0);
        // 6600
        final AtomicBoolean isRunning6600 = existingDataSimulatorStatus.getIsRunning6600();
        if (!isRunning6600.compareAndSet(false, true)) {
            return;
        }

        final AtomicBoolean isCancelled6600 = existingDataSimulatorStatus.getIsCancelled6600();
        isCancelled6600.set(false);

        existingDataSimulatorStatus.getNumComplete6600().set(0);
        // End

        dataSimulator.sendLcrDeviceMessages(rfnLcrDeviceList);
    }

    private void getDeviceCountByType(List<RfnDevice> rfnLcrDeviceList) {
        long total6200Devices = 0;
        long total6600Devices = 0;
        for (RfnDevice device : rfnLcrDeviceList) {
            if (device.getPaoIdentifier().getPaoType().equals(PaoType.LCR6200_RFN)) {
                total6200Devices++;
            } else if (device.getPaoIdentifier().getPaoType().equals(PaoType.LCR6600_RFN)) {
                total6600Devices++;
            }
        }
        existingDataSimulatorStatus.setNumTotal6200(total6200Devices);
        existingDataSimulatorStatus.setNumTotal6600(total6600Devices);
    }

    @RequestMapping(value = "stopSendingLcrDeviceMessages", method = RequestMethod.GET)
    public String stopSendLcrDeviceMessages() {
        existingDataSimulatorStatus.getIsCancelled6200().set(true);
        existingDataSimulatorStatus.getIsCancelled6600().set(true);
        dataSimulator.stopMessageSimulator();
        return "redirect:viewLcrDataSimulator";
    }

    @RequestMapping("datasimulator-status")
    @ResponseBody
    public Map<String, Object> dataSimulatorStatus(YukonUserContext userContext) {

        RfnLcrDataSimulatorStatus rfnLcrDataSimulatorStatus = dataSimulator.getRfnLcrDataSimulatorStatus();

        dataSimulatorStatus.setErrorMessage(rfnLcrDataSimulatorStatus.getErrorMessage());
        dataSimulatorStatus.setLastFinishedInjection6200(rfnLcrDataSimulatorStatus.getLastFinishedInjection6200());
        dataSimulatorStatus.getIsRunning6200().set(rfnLcrDataSimulatorStatus.getIsRunning6200().get());
        dataSimulatorStatus.getNumComplete6200().set(rfnLcrDataSimulatorStatus.getNumComplete6200().get());

        dataSimulatorStatus.setLastFinishedInjection6600(rfnLcrDataSimulatorStatus.getLastFinishedInjection6600());
        dataSimulatorStatus.getIsRunning6600().set(rfnLcrDataSimulatorStatus.getIsRunning6600().get());
        dataSimulatorStatus.getNumComplete6600().set(rfnLcrDataSimulatorStatus.getNumComplete6600().get());

        Map<String, Object> dataSimulatorStatusJson = Maps.newHashMapWithExpectedSize(10);
        dataSimulatorStatusJson.put("isRunning6200", dataSimulatorStatus.getIsRunning6200().get());
        dataSimulatorStatusJson.put("isCancelled6200", dataSimulatorStatus.getIsCancelled6200().get());
        dataSimulatorStatusJson.put("numTotal6200", dataSimulatorStatus.getNumTotal6200());
        dataSimulatorStatusJson.put("numComplete6200", dataSimulatorStatus.getNumComplete6200().get());
        Instant lastRun6200 = dataSimulatorStatus.getLastFinishedInjection6200();

        dataSimulatorStatusJson.put("isRunning6600", dataSimulatorStatus.getIsRunning6600().get());
        dataSimulatorStatusJson.put("isCancelled6600", dataSimulatorStatus.getIsCancelled6600().get());
        dataSimulatorStatusJson.put("numTotal6600", dataSimulatorStatus.getNumTotal6600());
        dataSimulatorStatusJson.put("numComplete6600", dataSimulatorStatus.getNumComplete6600().get());
        Instant lastRun6600 = dataSimulatorStatus.getLastFinishedInjection6600();

        if (dataSimulatorStatus.getNumTotal6200() != 0 && !dataSimulatorStatus.getIsCancelled6200().get()) {
            dataSimulatorStatus.setIsRunning6200(true);
            dataSimulatorStatusJson.put("status6200", "running");
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
            if (lastRun6200 != null) {
                String lastSimulationStr =
                    dateFormattingService.format(lastRun6200, DateFormatEnum.DATEHM, userContext);
                dataSimulatorStatusJson.put("lastSimulation6200", lastSimulationStr);
            }
        } else if (lastRun6200 != null) {
            String lastSimulationStr = dateFormattingService.format(lastRun6200, DateFormatEnum.DATEHM, userContext);
            dataSimulatorStatusJson.put("status6200", "notRunning");
            dataSimulatorStatusJson.put("lastSimulation6200", lastSimulationStr);
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
        } else {
            dataSimulatorStatusJson.put("status6200", "neverRan");
        }

        String errorMessage = dataSimulatorStatus.getErrorMessage();
        if (!StringUtils.isBlank(errorMessage)) {
            dataSimulatorStatusJson.put("hasError", true);
            dataSimulatorStatusJson.put("errorMessage", "Error Occured: " + errorMessage);
        }
        // 6600
        if (dataSimulatorStatus.getNumTotal6600() != 0 && !dataSimulatorStatus.getIsCancelled6600().get()) {
            dataSimulatorStatus.setIsRunning6600(true);
            dataSimulatorStatusJson.put("status6600", "running");
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
            if (lastRun6600 != null) {
                String lastSimulationStr =
                    dateFormattingService.format(lastRun6600, DateFormatEnum.DATEHM, userContext);
                dataSimulatorStatusJson.put("lastSimulation6600", lastSimulationStr);
            }
        } else if (lastRun6600 != null) {
            String lastSimulationStr = dateFormattingService.format(lastRun6600, DateFormatEnum.DATEHM, userContext);
            dataSimulatorStatusJson.put("status6600", "notRunning");
            dataSimulatorStatusJson.put("lastSimulation6600", lastSimulationStr);
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
        } else {
            dataSimulatorStatusJson.put("status6600", "neverRan");
        }

        // End
        return dataSimulatorStatusJson;
    }

    @RequestMapping("existing-datasimulator-status")
    @ResponseBody
    public Map<String, Object> existingDataSimulatorStatus(YukonUserContext userContext) {
        RfnLcrDataSimulatorStatus rfnLcrDataSimulatorStatus = dataSimulator.getRfnLcrExistingDataSimulatorStatus();

        existingDataSimulatorStatus.setErrorMessage(rfnLcrDataSimulatorStatus.getErrorMessage());
        existingDataSimulatorStatus.setLastFinishedInjection6200(rfnLcrDataSimulatorStatus.getLastFinishedInjection6200());
        existingDataSimulatorStatus.getIsRunning6200().set(rfnLcrDataSimulatorStatus.getIsRunning6200().get());
        existingDataSimulatorStatus.getNumComplete6200().set(rfnLcrDataSimulatorStatus.getNumComplete6200().get());

        existingDataSimulatorStatus.setLastFinishedInjection6600(rfnLcrDataSimulatorStatus.getLastFinishedInjection6600());
        existingDataSimulatorStatus.getIsRunning6600().set(rfnLcrDataSimulatorStatus.getIsRunning6600().get());
        existingDataSimulatorStatus.getNumComplete6600().set(rfnLcrDataSimulatorStatus.getNumComplete6600().get());

        Map<String, Object> dataSimulatorStatusJson = Maps.newHashMapWithExpectedSize(10);
        dataSimulatorStatusJson.put("isRunning6200", existingDataSimulatorStatus.getIsRunning6200().get());
        dataSimulatorStatusJson.put("isCancelled6200", existingDataSimulatorStatus.getIsCancelled6200().get());
        dataSimulatorStatusJson.put("numTotal6200", existingDataSimulatorStatus.getNumTotal6200());
        dataSimulatorStatusJson.put("numComplete6200", existingDataSimulatorStatus.getNumComplete6200().get());
        Instant lastRun6200 = existingDataSimulatorStatus.getLastFinishedInjection6200();

        dataSimulatorStatusJson.put("isRunning6600", existingDataSimulatorStatus.getIsRunning6600().get());
        dataSimulatorStatusJson.put("isCancelled6600", existingDataSimulatorStatus.getIsCancelled6600().get());
        dataSimulatorStatusJson.put("numTotal6600", existingDataSimulatorStatus.getNumTotal6600());
        dataSimulatorStatusJson.put("numComplete6600", existingDataSimulatorStatus.getNumComplete6600().get());
        Instant lastRun6600 = existingDataSimulatorStatus.getLastFinishedInjection6600();

        if (existingDataSimulatorStatus.getNumTotal6200() != 0
            && !existingDataSimulatorStatus.getIsCancelled6200().get()) {
            existingDataSimulatorStatus.setIsRunning6200(true);
            dataSimulatorStatusJson.put("status6200", "running");
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
            if (lastRun6200 != null) {
                String lastSimulationStr =
                    dateFormattingService.format(lastRun6200, DateFormatEnum.DATEHM, userContext);
                dataSimulatorStatusJson.put("lastSimulation6200", lastSimulationStr);
            }
        } else if (lastRun6200 != null) {
            String lastSimulationStr = dateFormattingService.format(lastRun6200, DateFormatEnum.DATEHM, userContext);
            dataSimulatorStatusJson.put("status6200", "notRunning");
            dataSimulatorStatusJson.put("lastSimulation6200", lastSimulationStr);
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
        } else {
            dataSimulatorStatusJson.put("status6200", "neverRan");
        }

        String errorMessage = existingDataSimulatorStatus.getErrorMessage();
        if (!StringUtils.isBlank(errorMessage)) {
            dataSimulatorStatusJson.put("hasError", true);
            dataSimulatorStatusJson.put("errorMessage", "Error Occured: " + errorMessage);
        }
        // 6600
        if (existingDataSimulatorStatus.getNumTotal6600() != 0
            && !existingDataSimulatorStatus.getIsCancelled6600().get()) {
            existingDataSimulatorStatus.setIsRunning6600(true);
            dataSimulatorStatusJson.put("status6600", "running");
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
            if (lastRun6600 != null) {
                String lastSimulationStr =
                    dateFormattingService.format(lastRun6600, DateFormatEnum.DATEHM, userContext);
                dataSimulatorStatusJson.put("lastSimulation6600", lastSimulationStr);
            }
        } else if (lastRun6600 != null) {
            String lastSimulationStr = dateFormattingService.format(lastRun6600, DateFormatEnum.DATEHM, userContext);
            dataSimulatorStatusJson.put("status6600", "notRunning");
            dataSimulatorStatusJson.put("lastSimulation6600", lastSimulationStr);
            dataSimulatorStatusJson.put("perMinuteMsgCount", dataSimulator.getPerMinuteMsgCount());
        } else {
            dataSimulatorStatusJson.put("status6600", "neverRan");
        }
        // End
        return dataSimulatorStatusJson;
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
                    new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.numEventsSent", numEventsSent);
            flashScope.setConfirm(createMessage);
        } else {
            MessageSourceResolvable createMessage = 
                    new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.numEventsSent", numEventsSent);
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