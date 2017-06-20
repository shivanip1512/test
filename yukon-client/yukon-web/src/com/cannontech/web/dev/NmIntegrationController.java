package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.jms.ConnectionFactory;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;
import com.cannontech.common.rfn.message.network.NeighborData;
import com.cannontech.common.rfn.message.network.NeighborFlagType;
import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReplyType;
import com.cannontech.common.rfn.message.network.RfnParentReplyType;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReplyType;
import com.cannontech.common.rfn.message.network.RouteData;
import com.cannontech.common.rfn.message.network.RouteFlagType;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.simulation.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedDataStreamingSettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareVersionReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;
import com.cannontech.common.rfn.simulation.SimulatedUpdateReplySettings;
import com.cannontech.common.rfn.simulation.service.RfnGatewaySimulatorService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.model.RfnTestMeterReading;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.development.service.impl.DRReport;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.model.SimulatorSettings.ReportingInterval;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.DataStreamingSimulatorStatusRequest;
import com.cannontech.simulators.message.request.GatewaySimulatorStatusRequest;
import com.cannontech.simulators.message.request.ModifyDataStreamingSimulatorRequest;
import com.cannontech.simulators.message.request.ModifyGatewaySimulatorRequest;
import com.cannontech.simulators.message.request.NmNetworkSimulatorRequest;
import com.cannontech.simulators.message.request.NmNetworkSimulatorRequest.Action;
import com.cannontech.simulators.message.request.RfnLcrAllDeviceSimulatorStartRequest;
import com.cannontech.simulators.message.request.RfnLcrAllDeviceSimulatorStopRequest;
import com.cannontech.simulators.message.request.RfnLcrSimulatorByRangeStartRequest;
import com.cannontech.simulators.message.request.RfnLcrSimulatorByRangeStopRequest;
import com.cannontech.simulators.message.request.RfnLcrSimulatorStatusRequest;
import com.cannontech.simulators.message.request.RfnMeterDataSimulatorStartRequest;
import com.cannontech.simulators.message.request.RfnMeterDataSimulatorStatusRequest;
import com.cannontech.simulators.message.request.RfnMeterDataSimulatorStopRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.request.SimulatorStartupSettingsRequest;
import com.cannontech.simulators.message.request.SimulatorStartupSettingsStatusRequest;
import com.cannontech.simulators.message.response.DataStreamingSimulatorStatusResponse;
import com.cannontech.simulators.message.response.GatewaySimulatorStatusResponse;
import com.cannontech.simulators.message.response.NmNetworkSimulatorResponse;
import com.cannontech.simulators.message.response.RfnLcrSimulatorStatusResponse;
import com.cannontech.simulators.message.response.RfnMeterDataSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.simulators.message.response.SimulatorStartupSettingsResponse;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.service.YsmJmxQueryService;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.support.service.impl.BeanTypeForJMXConnector;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/rfn/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NmIntegrationController {

    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private RfnEventTestingService rfnEventTestingService;
    @Autowired private RfnPerformanceVerificationService performanceVerificationService;
    @Autowired private YsmJmxQueryService jmxQueryService;
    @Autowired private RfnGatewayDataCache gatewayCache;
    @Autowired private RfnGatewaySimulatorService gatewaySimService;
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;

    SimulatorSettings lcrCurrentSettings = new SimulatorSettings(100000, 200000, 300000, 320000, 10,
        ReportingInterval.REPORTING_INTERVAL_24_HOURS);

    SimulatorSettings rfnCurrentSettings = new SimulatorSettings("ALL RFN Type", 10,
        ReportingInterval.REPORTING_INTERVAL_24_HOURS);
    
    private JmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(NmIntegrationController.class);
    private static final String meterReadServiceBean = "com.cannontech.yukon.ServiceManager:name=meterReadingArchiveRequestListener,type=MeterReadingArchiveRequestListener";
    private static final String meterReadQueueBean = "org.apache.activemq:type=Broker,brokerName=YukonMessageBroker,destinationType=Queue,destinationName=yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";
    private static final String lcrReadServiceBean = "com.cannontech.yukon.ServiceManager:name=lcrReadingArchiveRequestListener,type=LcrReadingArchiveRequestListener";
    private static final String lcrReadQueueBean = "org.apache.activemq:type=Broker,brokerName=YukonMessageBroker,destinationType=Queue,destinationName=yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";
    private static final String gatewayServiceBean = "com.cannontech.yukon.ServiceManager:name=gatewayArchiveRequestListener,type=GatewayArchiveRequestListener";
    private static final String gatewayArchiveReqQueueBean = "org.apache.activemq:type=Broker,brokerName=YukonMessageBroker,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayArchiveRequest";
    private static final String gatewayDataReqQueueBean = "org.apache.activemq:type=Broker,brokerName=YukonMessageBroker,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayDataRequest";
    private static final String gatewayDataQueueBean = "org.apache.activemq:type=Broker,brokerName=YukonMessageBroker,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayData";
    private static final String rfDaArchiveQueueBean = "org.apache.activemq:type=Broker,brokerName=YukonMessageBroker,destinationType=Queue,destinationName=yukon.qr.obj.da.rfn.RfDaArchiveRequest";
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
        
        data.add(getQueueData("Meter Reads", "meter-reads", meterReadQueueBean, meterReadServiceBean));
        data.add(getQueueData("LCR Reads", "lcr-reads", lcrReadQueueBean, lcrReadServiceBean));
        data.add(getQueueData("RF DA Archive", "rfda-archive", rfDaArchiveQueueBean, null));
        data.add(getQueueData("Gateway Archive", "gateway-archive", gatewayArchiveReqQueueBean, gatewayServiceBean));
        data.add(getQueueData("Gateway Data Request", "gateway-data-rec", gatewayDataReqQueueBean, null));
        data.add(getQueueData("Gateway Data", "gateway-data", gatewayDataQueueBean, null));
        
        return data;
    }
    
    private Map<String, Object> getQueueData(String queueName, String queueIdentifier, String queueBean, String serviceBean) {
        
        Map<String, Object> data = new LinkedHashMap<>();
        
        try {
            if (serviceBean != null) {
            ObjectName service = ObjectName.getInstance(serviceBean);
                data.put(queueIdentifier + "-archived", ImmutableMap.of(
                        "name", queueName + " Archived", 
                        "value", jmxQueryService.getTypedValue(service, "ArchivedReadings", 0, Integer.class, BeanTypeForJMXConnector.SERVICE)));
                data.put(queueIdentifier + "-requests-processed", ImmutableMap.of(
                        "name", queueName + " Requests Processed", 
                        "value", jmxQueryService.getTypedValue(service, "ProcessedArchiveRequest", 0, Integer.class, BeanTypeForJMXConnector.SERVICE)));
            }
            
            ObjectName queue = ObjectName.getInstance(queueBean);
            data.put(queueIdentifier + "-enqueue-count", ImmutableMap.of(
                    "name", queueName + " Enqueue Count", 
                    "value", jmxQueryService.getTypedValue(queue, "EnqueueCount", 0L, Long.class, BeanTypeForJMXConnector.QUEUE)));
            data.put(queueIdentifier + "-queue-size", ImmutableMap.of(
                    "name", queueName + " Queue Size", 
                    "value", jmxQueryService.getTypedValue(queue, "QueueSize", 0L, Long.class, BeanTypeForJMXConnector.QUEUE)));
            Double aet = jmxQueryService.getTypedValue(queue, "AverageEnqueueTime", 0.0, Double.class, BeanTypeForJMXConnector.QUEUE);
            data.put(queueIdentifier + "-average-enqueue-time", ImmutableMap.of(
                    "name", queueName + " Average Enqueue Time", 
                    "value", df.format(aet)));
        } catch (MalformedObjectNameException | InstanceNotFoundException e) {
            log.info("Unable to retrieve metrics for queue: " + queueName + ". Queue may not have been used yet.");
        } catch (Exception e) {
            log.warn("Couldn't look up value.", e);
        }
        
        return data;
    }

    @RequestMapping("gatewaySimulator")
    public String gatewaySimulator(ModelMap model, FlashScope flash) {
        // Enums for selects
        model.addAttribute("ackTypes", RfnGatewayUpgradeRequestAckType.values());
        model.addAttribute("acceptedUpdateStatusTypes", SimulatedCertificateReplySettings.acceptedUpdateStatusTypes);
        model.addAttribute("firmwareVersionReplyTypes", RfnUpdateServerAvailableVersionResult.values());
        model.addAttribute("firmwareUpdateResultTypes", GatewayFirmwareUpdateRequestResult.values());
        model.addAttribute("gatewayUpdateResultTypes", GatewayUpdateResult.values());
        
        try {
            GatewaySimulatorStatusResponse response = simulatorsCommunicationService.sendRequest(new GatewaySimulatorStatusRequest(), GatewaySimulatorStatusResponse.class);
            // Simulator statuses
            model.addAttribute("autoDataReplyActive", response.isDataReplyActive());
            model.addAttribute("autoUpdateReplyActive", response.isUpdateReplyActive());
            model.addAttribute("autoCertificateReplyActive", response.isCertificateReplyActive());
            model.addAttribute("autoFirmwareReplyActive", response.isFirmwareReplyActive());
            model.addAttribute("autoFirmwareVersionReplyActive", response.isFirmwareVersionReplyActive());
            model.addAttribute("numberOfSimulatorsRunning", response.getNumberOfSimulatorsRunning());
            // Current settings
            model.addAttribute("certificateSettings", response.getCertificateSettings());
            model.addAttribute("firmwareSettings", response.getFirmwareSettings());
            model.addAttribute("firmwareVersionSettings", response.getFirmwareVersionSettings());
            model.addAttribute("dataSettings", response.getDataSettings());
            model.addAttribute("updateSettings", response.getUpdateSettings());
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
        }
        
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

    @RequestMapping("sendGatewayDataResponse")
    public String sendGatewayDataResponse(@RequestParam String serial,
            @RequestParam(defaultValue = "false") boolean isGateway2, FlashScope flash) {

        try {
            GatewaySimulatorStatusResponse response = simulatorsCommunicationService.sendRequest(
                new GatewaySimulatorStatusRequest(), GatewaySimulatorStatusResponse.class);
            SimulatedGatewayDataSettings settings = response.getDataSettings();
            if(settings == null){
                settings = enableGatewayDataSimulator(false, 50, 1000, 500, flash);
            }
            gatewaySimService.sendGatewayDataResponse(serial, isGateway2, settings);
            flash.setConfirm(new YukonMessageSourceResolvable(
                "yukon.web.modules.dev.rfnTest.gatewaySimulator.gatewayDataResponse", serial));
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
            return "redirect:gatewaySimulator";
        }
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableAll")
    public String enableAllSimulators(FlashScope flash) {
        // Only start the threads that aren't already running. This lets the user specify non-default parameters for
        // some threads, then just bulk-start the rest.
        
        try {
            GatewaySimulatorStatusResponse status = simulatorsCommunicationService.sendRequest(new GatewaySimulatorStatusRequest(), GatewaySimulatorStatusResponse.class);
            ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
            
            // Data Reply
            if (!status.isDataReplyActive()) {
                SimulatedGatewayDataSettings dataSettings = new SimulatedGatewayDataSettings();
                dataSettings.setReturnGwy800Model(false);
                dataSettings.setCurrentDataStreamingLoading(50);
                dataSettings.setNumberOfReadyNodes(1000);
                dataSettings.setNumberOfNotReadyNodes(500);
                request.setDataSettings(dataSettings);
            }
            
            // Update reply
            if (!status.isUpdateReplyActive()) {
                SimulatedUpdateReplySettings updateSettings = new SimulatedUpdateReplySettings();
                updateSettings.setCreateResult(GatewayUpdateResult.SUCCESSFUL);
                updateSettings.setEditResult(GatewayUpdateResult.SUCCESSFUL);
                updateSettings.setDeleteResult(GatewayUpdateResult.SUCCESSFUL);
                request.setUpdateSettings(updateSettings);
            }
            
            // Certificate upgrade reply
            if (!status.isCertificateReplyActive()) {
                SimulatedCertificateReplySettings certSettings = new SimulatedCertificateReplySettings();
                certSettings.setAckType(RfnGatewayUpgradeRequestAckType.ACCEPTED_FULLY);
                certSettings.setDeviceUpdateStatus(GatewayCertificateUpdateStatus.REQUEST_ACCEPTED);
                request.setCertificateSettings(certSettings);
            }
            
            // Firmware upgrade reply
            if (!status.isFirmwareReplyActive()) {
                SimulatedFirmwareReplySettings firmwareSettings = new SimulatedFirmwareReplySettings();
                firmwareSettings.setResultType(GatewayFirmwareUpdateRequestResult.ACCEPTED);
                request.setFirmwareSettings(firmwareSettings);
            }
            
            // Firmware version reply
            if (!status.isFirmwareVersionReplyActive()) {
                SimulatedFirmwareVersionReplySettings settings = new SimulatedFirmwareVersionReplySettings();
                settings.setVersion("1.2.3");
                settings.setResult(RfnUpdateServerAvailableVersionResult.SUCCESS);
                request.setFirmwareVersionSettings(settings);
            }
            
            sendStartStopRequest(request, flash, true);
            
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
        }
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("disableAll") 
    public String disableAllSimulators(FlashScope flash) {

        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setAllStop();
            
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableGatewayDataReply")
    public String enableGatewayDataReply(@RequestParam(defaultValue="false") boolean alwaysGateway2, 
                                         @RequestParam(defaultValue="50") String currentDataStreamingLoading, 
                                         @RequestParam(defaultValue="1000") String readyNodes,
                                         @RequestParam(defaultValue="500") String notReadyNodes,
                                         FlashScope flash) {
        
        enableGatewayDataSimulator(alwaysGateway2, 
                                   Double.valueOf(currentDataStreamingLoading), 
                                   Integer.valueOf(readyNodes), 
                                   Integer.valueOf(notReadyNodes), 
                                   flash);
        return "redirect:gatewaySimulator";
    }
    
    private SimulatedGatewayDataSettings enableGatewayDataSimulator(boolean alwaysGateway2, 
                                                                    double currentDataStreamingLoading,
                                                                    int readyNodes,
                                                                    int notReadyNodes,
                                                                    FlashScope flash){
        clearGatewayCache();
        SimulatedGatewayDataSettings dataSettings = new SimulatedGatewayDataSettings();
        dataSettings.setReturnGwy800Model(alwaysGateway2);
        dataSettings.setCurrentDataStreamingLoading(currentDataStreamingLoading);
        dataSettings.setNumberOfReadyNodes(readyNodes);
        dataSettings.setNumberOfNotReadyNodes(notReadyNodes);
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setDataSettings(dataSettings);
        sendStartStopRequest(request, flash, true);
        return dataSettings;
    }

    @RequestMapping("disableGatewayDataReply")
    public String disableGatewayDataReply(FlashScope flash) {
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setStopDataReply(true);
        
        sendStartStopRequest(request, flash, false);
        
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
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setUpdateSettings(updateSettings);
        
        sendStartStopRequest(request, flash, true);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("disableGatewayUpdateReply")
    public String disableGatewayUpdateReply(FlashScope flash) {
        
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setStopUpdateReply(true);
        
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableGatewayCertificateReply")
    public String enableGatewayCertificateReply(@RequestParam RfnGatewayUpgradeRequestAckType ackType,
                                                @RequestParam GatewayCertificateUpdateStatus updateStatus,
                                                FlashScope flash) {
        
        SimulatedCertificateReplySettings certSettings = new SimulatedCertificateReplySettings();
        certSettings.setAckType(ackType);
        certSettings.setDeviceUpdateStatus(updateStatus);
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setCertificateSettings(certSettings);
        
        sendStartStopRequest(request, flash, true);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("disableGatewayCertificateReply")
    public String disableGatewayCertificateReply(FlashScope flash) {
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setStopCertificateReply(true);
        
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableGatewayFirmwareReply")
    public String enableGatewayFirmwareReply(@RequestParam GatewayFirmwareUpdateRequestResult updateResult,
                                             FlashScope flash) {
        
        SimulatedFirmwareReplySettings settings = new SimulatedFirmwareReplySettings();
        settings.setResultType(updateResult);
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setFirmwareSettings(settings);
        
        sendStartStopRequest(request, flash, true);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("disableGatewayFirmwareReply")
    public String disableGatewayFirmwareReply(FlashScope flash) {
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setStopFirmwareReply(true);
        
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableFirmwareVersionReply")
    public String enableFirmwareVersionReply(@RequestParam RfnUpdateServerAvailableVersionResult replyType,
                                             @RequestParam String version,
                                             FlashScope flash) {
        
        SimulatedFirmwareVersionReplySettings settings = new SimulatedFirmwareVersionReplySettings();
        settings.setVersion(version);
        settings.setResult(replyType);
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setFirmwareVersionSettings(settings);
        
        sendStartStopRequest(request, flash, true);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("disableFirmwareVersionReply")
    public String disableFirmwareVersionReply(FlashScope flash) {
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setStopFirmwareVersionReply(true);
        
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }

    /**
     * Sends a request to modify a gateway simulator.
     * @param request The request to start or stop simulators.
     * @param isStartRequest Is this a request to start a simulator (true) or stop a simulator (false)? This determines
     * which i18n keys are used for flash scope success and failure messages.
     */
    private void sendStartStopRequest(ModifyGatewaySimulatorRequest request, FlashScope flash, boolean isStartRequest) {
        String successKey = isStartRequest ? "yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartSuccess" :
                                             "yukon.web.modules.dev.rfnTest.gatewaySimulator.simStopSuccess";
        String failureKey = isStartRequest ? "yukon.web.modules.dev.rfnTest.gatewaySimulator.simStartFailed" :
                                             "yukon.web.modules.dev.rfnTest.gatewaySimulator.simStopFailed";
        try {
            SimulatorResponseBase response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            if (response.isSuccessful()) {
                flash.setConfirm(new YukonMessageSourceResolvable(successKey));
            } else {
                flash.setError(new YukonMessageSourceResolvable(failureKey));
            }
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
        }
    }

    @RequestMapping("viewMeterReadArchiveRequest")
    public String viewMeterReadArchiveRequest(ModelMap model) {
        
        model.addAttribute("meterReading", new RfnTestMeterReading());
        
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

    @RequestMapping("viewRelayArchiveRequest")
    public String viewRelayArchiveRequest() {
        return "rfn/viewRelayArchive.jsp";
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

    @RequestMapping("stopMetersArchieveRequest")
    public void stopRfnMeterSimulator(FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new RfnMeterDataSimulatorStopRequest(),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }

    @RequestMapping("startMetersArchiveRequest")
    public void startMetersArchiveRequest(SimulatorSettings settings, FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new RfnMeterDataSimulatorStartRequest(settings),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }

    @RequestMapping("testMeterArchiveRequest")
    public void testMeterArchiveRequest(SimulatorSettings settings, FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new RfnMeterDataSimulatorStartRequest(settings, true),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }
    
    @RequestMapping("updateStartup")
    @ResponseBody
    public Map<String, Object> enableRfnAutoStart(SimulatorType simulatorType, boolean runOnStartup, FlashScope flash) {
        SimulatorStartupSettingsResponseOrError startupResponse = getSimulatorStartupSettingsResponse(runOnStartup, simulatorType);
        if (startupResponse.response == null) {
            return startupResponse.errorJson;
        }
        return null; 
    }
    
    @RequestMapping("existingStartupStatus")
    @ResponseBody
    public Map<String, Object> existingStartupStatus(SimulatorType simulatorType, FlashScope flash) {
        SimulatorStartupSettingsResponseOrError startupResponse = getSimulatorStartupSettingsResponse(simulatorType);
        if (startupResponse.response == null) {
            return startupResponse.errorJson;
        }
        Map<String, Object> json = new HashMap<>();
        json.put("hasError", false);
        json.put("runOnStartup", startupResponse.response.isRunOnStartup());
        return json;
    }

    @RequestMapping("existing-rfnMetersimulator-status")
    @ResponseBody
    public Map<String, Object> existingrfnMeterSimulatorStatus(YukonUserContext userContext) {
        MeterSimStatusResponseOrError status = getRfnMeterSimulatorStatusResponse();
        if (status.response == null) {
            return status.errorJson;
        }
        return buildSimulatorStatusJson(status.response.getStatus());
    }

    @RequestMapping("viewRfnMeterSimulator")
    public String viewRfnMeterSimulator(ModelMap model) {
        model.addAttribute("currentSettings", rfnCurrentSettings);

        ImmutableSet<PaoType> paoTypes = PaoType.getRfMeterTypes();
        model.addAttribute("paoTypes", paoTypes);
        model.addAttribute("rfnMeterReportingIntervals",ReportingInterval.values());

        RfnMeterDataSimulatorStatusResponse response = getRfnMeterSimulatorStatusResponse().response;
        if(response == null){
            return "rfn/rfnMeterSimulator.jsp";
        }
        model.addAttribute("currentSettings", response.getSettings());
        model.addAttribute("selectedReportingInterval", response.getSettings().getReportingInterval());

        model.addAttribute("rfnMeterSimulatorStatus", buildSimulatorStatusJson(response.getStatus()));
        
        return "rfn/rfnMeterSimulator.jsp";
    }

    @RequestMapping("viewLcrDataSimulator")
    public String viewLcrDataSimulator(ModelMap model) {

        model.addAttribute("currentSettings", lcrCurrentSettings);

        RfnLcrSimulatorStatusResponse response = getRfnLcrSimulatorStatusResponse().response;
        if(response == null){
            return "rfn/dataSimulator.jsp";
        }
        if (response.getStatusByRange().isRunning().get()) {
            model.addAttribute("currentSettings", response.getSettings());
        }

        model.addAttribute("dataSimulatorStatus", buildSimulatorStatusJson(response.getStatusByRange()));
        model.addAttribute("existingDataSimulatorStatus", buildSimulatorStatusJson(response.getAllDevicesStatus()));
        return "rfn/dataSimulator.jsp";
    }

    private LcrSimStatusResponseOrError getRfnLcrSimulatorStatusResponse() {
        try {
            RfnLcrSimulatorStatusResponse response = simulatorsCommunicationService.sendRequest(
                new RfnLcrSimulatorStatusRequest(), RfnLcrSimulatorStatusResponse.class);
            return new LcrSimStatusResponseOrError(response);
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage());
            return new LcrSimStatusResponseOrError(json);
        }
    }

    private MeterSimStatusResponseOrError getRfnMeterSimulatorStatusResponse() {
        try {
            RfnMeterDataSimulatorStatusResponse response = simulatorsCommunicationService.sendRequest(
                new RfnMeterDataSimulatorStatusRequest(), RfnMeterDataSimulatorStatusResponse.class);
            return new MeterSimStatusResponseOrError(response);
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage() + ".");
            return new MeterSimStatusResponseOrError(json);
        }
    }
    
    private SimulatorStartupSettingsResponseOrError getSimulatorStartupSettingsResponse(boolean runOnStartup, SimulatorType affectedSimulator) {
        try {
            SimulatorStartupSettingsResponse response = simulatorsCommunicationService.sendRequest(
                new SimulatorStartupSettingsRequest(runOnStartup, affectedSimulator), SimulatorStartupSettingsResponse.class);
            if (response.isSuccessful()) {
                return new SimulatorStartupSettingsResponseOrError(response);
            }
            else {
                Map<String, Object> json = new HashMap<>();
                json.put("hasError", true);
                if (runOnStartup) {
                    json.put("errorMessage", "Unable to enable automatic startup for the simulator of type: " + affectedSimulator.name() + "."); 
                } else {
                    json.put("errorMessage", "Unable to disable automatic startup for the simulator of type: " + affectedSimulator.name() + "."); 
                }
                return new SimulatorStartupSettingsResponseOrError(json);
            }
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage());
            return new SimulatorStartupSettingsResponseOrError(json);
        }
    }
    
    private SimulatorStartupSettingsResponseOrError getSimulatorStartupSettingsResponse(SimulatorType affectedSimulator) {
        try {
            SimulatorStartupSettingsResponse response = simulatorsCommunicationService.sendRequest(
                new SimulatorStartupSettingsStatusRequest(affectedSimulator), SimulatorStartupSettingsResponse.class);
            if (response.isSuccessful()) {
                return new SimulatorStartupSettingsResponseOrError(response);
            }
            else {
                Map<String, Object> json = new HashMap<>();
                json.put("hasError", true);
                json.put("errorMessage", "Unable to retrieve simulator startup settings for the simulator of type: " + affectedSimulator.name() + ".");
                return new SimulatorStartupSettingsResponseOrError(json);
            }
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage());
            return new SimulatorStartupSettingsResponseOrError(json);
        }
    }

    @RequestMapping(value = "startDataSimulator")
    @ResponseBody
    public void startDataSimulator(SimulatorSettings settings, FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new RfnLcrSimulatorByRangeStartRequest(settings),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }

    @RequestMapping(value = "stopDataSimulator")
    public void stopDataSimulator(FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new RfnLcrSimulatorByRangeStopRequest(),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }

    @RequestMapping(value = "sendLcrDeviceMessages")
    @ResponseBody
    public void sendLcrDeviceMessages(SimulatorSettings settings, FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new RfnLcrAllDeviceSimulatorStartRequest(settings),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }

    @RequestMapping(value = "stopSendingLcrDeviceMessages", method = RequestMethod.GET)
    public void stopSendLcrDeviceMessages(FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new RfnLcrAllDeviceSimulatorStopRequest(),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }

    @RequestMapping("datasimulator-status")
    @ResponseBody
    public Map<String, Object> dataSimulatorStatus() {
        LcrSimStatusResponseOrError status = getRfnLcrSimulatorStatusResponse();
        if (status.response == null) {
            return status.errorJson;
        }
        return buildSimulatorStatusJson(status.response.getStatusByRange());
    }

    @RequestMapping("existing-datasimulator-status")
    @ResponseBody
    public Map<String, Object> existingDataSimulatorStatus() {
        LcrSimStatusResponseOrError status = getRfnLcrSimulatorStatusResponse();
        if (status.response == null) {
            return status.errorJson;
        }
        return buildSimulatorStatusJson(status.response.getAllDevicesStatus());
    }

    private Map<String, Object> buildSimulatorStatusJson(RfnDataSimulatorStatus status) {
        Map<String, Object> json = new HashMap<>();
        if (status.getStartTime() == null) {
            json.put("startTime", "");
        } else {
            json.put("startTime", status.getStartTime().toDateTime().toString("MM/dd/YYYY HH:mm"));
        }
        if (status.getStopTime() == null) {
            json.put("stopTime", "");
        } else {
            json.put("stopTime", status.getStopTime().toDateTime().toString("MM/dd/YYYY HH:mm"));
        }
        json.put("success", status.getSuccess());
        json.put("failure", status.getFailure());
        json.put("running", status.isRunning());
        if (status.getLastInjectionTime() == null) {
            json.put("lastInjectionTime", "");
        } else {
            json.put("lastInjectionTime", status.getLastInjectionTime().toDateTime().toString("MM/dd/YYYY HH:mm"));
        }
        return json;
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
    public String sendMeterReadArchiveRequest(@ModelAttribute RfnTestMeterReading meterReading) {
        
        rfnEventTestingService.sendMeterArchiveRequests(meterReading);
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
    
    @RequestMapping("sendRelayArchiveRequest")
    public String sendRelayArchive(int serialFrom, int serialTo, String manufacturer, String model) {
        rfnEventTestingService.sendRelayArchiveRequest(serialFrom, serialTo, manufacturer, model);
        return "redirect:viewRelayArchiveRequest";
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
    
    @RequestMapping("viewDataStreamingSimulator")
    public String viewDataStreamingSimulator(ModelMap model, FlashScope flash) {
        
        try {
            DataStreamingSimulatorStatusResponse response = simulatorsCommunicationService.sendRequest(new DataStreamingSimulatorStatusRequest(), DataStreamingSimulatorStatusResponse.class);
            model.addAttribute("simulatorRunning", response.isRunning());
            model.addAttribute("settings", response.getSettings());
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
        }
        
        model.addAttribute("deviceErrors", DeviceDataStreamingConfigError.values());
        
        return "rfn/dataStreamingSimulator.jsp";
    }
    
    @RequestMapping("startDataStreamingSimulator")
    public String startDataStreamingSimulator(Boolean isOverloadGatewaysOnVerification, 
                                              Boolean isNetworkManagerFailOnVerification,
                                              Boolean isDeviceErrorOnVerification,
                                              @RequestParam(required=false) DeviceDataStreamingConfigError deviceErrorOnVerification, 
                                              @RequestParam(defaultValue="0") Integer numberOfDevicesToErrorOnVerification,
                                              Boolean isOverloadGatewaysOnConfig,
                                              Boolean isNetworkManagerFailOnConfig,
                                              Boolean isDeviceErrorOnConfig,
                                              Boolean isAcceptedWithError,
                                              @RequestParam(required=false) DeviceDataStreamingConfigError deviceErrorOnConfig,
                                              @RequestParam(defaultValue="0") Integer numberOfDevicesToErrorOnConfig,
                                              FlashScope flash) {
        try {
            SimulatedDataStreamingSettings settings = new SimulatedDataStreamingSettings();
            settings.setOverloadGatewaysOnVerification(Optional.ofNullable(isOverloadGatewaysOnVerification).orElse(false));
            settings.setNetworkManagerFailOnVerification(Optional.ofNullable(isNetworkManagerFailOnVerification).orElse(false));
            settings.setDeviceErrorOnVerification(isDeviceErrorOnVerification != null ? deviceErrorOnVerification : null);
            settings.setNumberOfDevicesToErrorOnVerification(numberOfDevicesToErrorOnVerification);
            
            settings.setOverloadGatewaysOnConfig(Optional.ofNullable(isOverloadGatewaysOnConfig).orElse(false));
            settings.setNetworkManagerFailOnConfig(Optional.ofNullable(isNetworkManagerFailOnConfig).orElse(false));
            settings.setDeviceErrorOnConfig(isDeviceErrorOnConfig != null ? deviceErrorOnConfig : null);
            settings.setNumberOfDevicesToErrorOnConfig(numberOfDevicesToErrorOnConfig);
            
            settings.setAcceptedWithError(Optional.ofNullable(isAcceptedWithError).orElse(false));
            
            ModifyDataStreamingSimulatorRequest request = new ModifyDataStreamingSimulatorRequest();
            request.setSettings(settings);
            SimulatorResponseBase response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            
            //start gateway simulator
            boolean startedGatewaySimualtor = false;
            GatewaySimulatorStatusResponse gatewayResponse = simulatorsCommunicationService.sendRequest(
                new GatewaySimulatorStatusRequest(), GatewaySimulatorStatusResponse.class);
            SimulatedGatewayDataSettings gatewaySettings = gatewayResponse.getDataSettings();
            if(gatewaySettings == null){
                enableGatewayDataSimulator(false, 50, 1000, 500, flash);
                startedGatewaySimualtor = true;
            }
            
            if (response.isSuccessful()) {
                if (startedGatewaySimualtor) {
                    flash.setConfirm(new YukonMessageSourceResolvable(
                        "yukon.web.modules.dev.rfnTest.dataStreamingSimulator.gatewayAndDataStreamingSimulatorsStart"));
                } else {
                    flash.setConfirm(new YukonMessageSourceResolvable(
                        "yukon.web.modules.dev.rfnTest.dataStreamingSimulator.simulatorStart"));
                }
            }
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
        }
        
        return "redirect:viewDataStreamingSimulator";
    }
    
    @RequestMapping("stopDataStreamingSimulator")
    public String stopDataStreamingSimulator(FlashScope flash) {
        try {
            ModifyDataStreamingSimulatorRequest request = new ModifyDataStreamingSimulatorRequest();
            request.setStopSimulator(true);
            SimulatorResponseBase response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            if (response.isSuccessful()) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.dataStreamingSimulator.simulatorStop"));
            }
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
        }
        return "redirect:viewDataStreamingSimulator";
    }
    
    @RequestMapping("viewMappingSimulator")
    public String viewMappingSimulator(ModelMap model, FlashScope flash, HttpServletRequest request) {
        model.addAttribute("routeFlags", RouteFlagType.values());
        model.addAttribute("neighborFlags", NeighborFlagType.values());
        model.addAttribute("parentReplys", RfnParentReplyType.values());
        model.addAttribute("neighborReplys", RfnNeighborDataReplyType.values());
        model.addAttribute("routeReplys", RfnPrimaryRouteDataReplyType.values());
        
        NmNetworkSimulatorRequest simRequest = new NmNetworkSimulatorRequest(Action.GET_SETTINGS);
        SimulatorResponseBase response = sendRequest(simRequest, null, flash);  
        
        SimulatedNmMappingSettings settings = null;
        if (response != null) {
            settings = ((NmNetworkSimulatorResponse) response).getSettings();
            model.addAttribute("simulatorRunning", true);
        }
        if (settings == null) {
            settings = new SimulatedNmMappingSettings();
            NeighborData neighborData = new NeighborData();
            neighborData.setEtxBand((short) 3);
            neighborData.setLastCommTime(new Date().getTime());
            neighborData.setLinkPower("125 mWatt");
            neighborData.setLinkRate("4x");
            neighborData.setNeighborAddress("00:14:08:03:FA:A2");
            neighborData.setNeighborDataTimestamp(new Date().getTime());
            Set<NeighborFlagType> types = new HashSet<>();
            types.add(NeighborFlagType.BN);
            types.add(NeighborFlagType.IN);
            neighborData.setNeighborFlags(types);
            neighborData.setNeighborLinkCost(new Float(3));
            neighborData.setNextCommTime(new Date().getTime());
            neighborData.setNumSamples(1);
            neighborData.setSerialNumber("123");
            settings.setNeighborData(neighborData);

            RouteData routeData = new RouteData();
            routeData.setDestinationAddress("00:14:08:03:FA:A2");
            routeData.setHopCount((short)1);
            routeData.setNextHopAddress("00:14:08:03:FA:A2");
            routeData.setRouteColor((short)1);
            routeData.setRouteDataTimestamp(new Date().getTime());
            Set<RouteFlagType> routeTypes = new HashSet<>();
            routeTypes.add(RouteFlagType.BR);
            routeTypes.add(RouteFlagType.GC);
            routeData.setRouteFlags(routeTypes);
            routeData.setRouteTimeout(new Date().getTime());
            routeData.setSerialNumber("101");
            routeData.setTotalCost((short)2);
            settings.setRouteData(routeData);

            ParentData parentData = new ParentData();
            parentData.setNodeMacAddress("17:14:08:03:FA:A2");
            parentData.setNodeSN("123");
            settings.setParentData(parentData);
            model.addAttribute("simulatorRunning", false);
        }

        model.addAttribute("currentSettings", settings);
        return "rfn/mappingSimulator.jsp";
    }
    
    private void retrieveFlagSettings(SimulatedNmMappingSettings currentSettings, HttpServletRequest request) {
        DateTime now = new DateTime();
        long dateTime = now.getMillis();
        if (currentSettings.getNeighborData() != null) {
            currentSettings.getNeighborData().setLastCommTime(dateTime);
            currentSettings.getNeighborData().setNeighborDataTimestamp(dateTime);
            currentSettings.getNeighborData().setNextCommTime(dateTime);
            currentSettings.getNeighborData().setNeighborFlags(new HashSet<>());
            for (NeighborFlagType flag : NeighborFlagType.values()) {
                boolean flagSet= ServletRequestUtils.getBooleanParameter(request, "neighborFlag_" + flag, false);
                if (flagSet) {
                    currentSettings.getNeighborData().getNeighborFlags().add(flag);
                }
            }
        }
        if (currentSettings.getRouteData() != null) {
            currentSettings.getRouteData().setRouteDataTimestamp(dateTime);
            currentSettings.getRouteData().setRouteTimeout(dateTime);
            currentSettings.getRouteData().setRouteFlags(new HashSet<>());
            for (RouteFlagType flag : RouteFlagType.values()) {
                boolean flagSet= ServletRequestUtils.getBooleanParameter(request, "routeFlag_" + flag, false);
                if (flagSet) {
                    currentSettings.getRouteData().getRouteFlags().add(flag);
                }
            }
        }
    }
    
    @RequestMapping(value="populateMappingDatabase", method = RequestMethod.POST)
    public String populateMappingDatabase(ModelMap model, FlashScope flash, @ModelAttribute("currentSettings") SimulatedNmMappingSettings currentSettings, HttpServletRequest request) {
        retrieveFlagSettings(currentSettings, request);
        NmNetworkSimulatorRequest simRequest = new NmNetworkSimulatorRequest(currentSettings, Action.SETUP);
        sendRequest(simRequest, new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.mappingSimulator.databasePopulated"), flash);
        return "redirect:viewMappingSimulator";
    }
    
    @RequestMapping(value="updateMappingSettings", method = RequestMethod.POST)
    public String updateMappingSettings(ModelMap model, FlashScope flash, @ModelAttribute("currentSettings") SimulatedNmMappingSettings currentSettings, HttpServletRequest request) {
        retrieveFlagSettings(currentSettings, request);
        NmNetworkSimulatorRequest simRequest = new NmNetworkSimulatorRequest(currentSettings, Action.UPDATE_SETTINGS);
        sendRequest(simRequest, new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.mappingSimulator.settingsUpdated"), flash);
        model.addAttribute("simulatorRunning", true);
        return "redirect:viewMappingSimulator";
    }
        
    @RequestMapping(value="startMappingSimulator", method = RequestMethod.POST)
    public String startMappingSimulator(ModelMap model, FlashScope flash, @ModelAttribute("currentSettings") SimulatedNmMappingSettings currentSettings, HttpServletRequest request) {
        retrieveFlagSettings(currentSettings, request);
        NmNetworkSimulatorRequest simRequest = new NmNetworkSimulatorRequest(currentSettings, Action.START);
        sendRequest(simRequest, new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.mappingSimulator.simulatorStart"), flash);       
        return "redirect:viewMappingSimulator";
    }
    
    @RequestMapping("stopMappingSimulator")
    public String stopMappingSimulator(ModelMap model, FlashScope flash, @ModelAttribute("currentSettings") SimulatedNmMappingSettings currentSettings, HttpServletRequest request) {
        NmNetworkSimulatorRequest simRequest = new NmNetworkSimulatorRequest(Action.STOP);
        sendRequest(simRequest, new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.mappingSimulator.simulatorStop"), flash);       
        return "redirect:viewMappingSimulator";
    }
    
    private SimulatorResponseBase sendRequest(SimulatorRequest request, YukonMessageSourceResolvable confirmation, FlashScope flash){
        SimulatorResponseBase response = null;
        try {
            response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            if (response.isSuccessful() && confirmation != null) {
                flash.setConfirm(confirmation);
            }
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
        }
        return response;
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

    private static class LcrSimStatusResponseOrError {
        public final RfnLcrSimulatorStatusResponse response;
        public final Map<String, Object> errorJson;

        public LcrSimStatusResponseOrError(RfnLcrSimulatorStatusResponse response) {
            this.response = response;
            errorJson = null;
        }

        public LcrSimStatusResponseOrError(Map<String, Object> errorJson) {
            response = null;
            this.errorJson = errorJson;
        }
    }

    private static class MeterSimStatusResponseOrError {
        public final RfnMeterDataSimulatorStatusResponse response;
        public final Map<String, Object> errorJson;

        public MeterSimStatusResponseOrError(RfnMeterDataSimulatorStatusResponse response) {
            this.response = response;
            errorJson = null;
        }

        public MeterSimStatusResponseOrError(Map<String, Object> errorJson) {
            response = null;
            this.errorJson = errorJson;
        }
    }
    
    private static class SimulatorStartupSettingsResponseOrError {
        public final SimulatorStartupSettingsResponse response;
        public final Map<String, Object> errorJson;

        public SimulatorStartupSettingsResponseOrError(SimulatorStartupSettingsResponse response) {
            this.response = response;
            errorJson = null;
        }

        public SimulatorStartupSettingsResponseOrError(Map<String, Object> errorJson) {
            response = null;
            this.errorJson = errorJson;
        }
    }
}