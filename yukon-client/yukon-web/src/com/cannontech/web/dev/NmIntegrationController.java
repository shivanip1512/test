package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import javax.jms.ConnectionFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.Logger;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.message.status.type.DemandResetStatusCode;
import com.cannontech.amr.rfn.service.NmSyncService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.GatewayConfigResult;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResultType;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.message.network.NeighborFlagType;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReplyType;
import com.cannontech.common.rfn.message.network.RfnParentReplyType;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReplyType;
import com.cannontech.common.rfn.message.network.RouteFlagType;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
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
import com.cannontech.development.model.DeviceArchiveRequestParameters;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.model.RfnTestMeterReading;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.development.service.impl.DRReport;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.model.SimulatorSettings.ReportingInterval;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.message.request.DataStreamingSimulatorStatusRequest;
import com.cannontech.simulators.message.request.GatewaySimulatorStatusRequest;
import com.cannontech.simulators.message.request.ModifyDataStreamingSimulatorRequest;
import com.cannontech.simulators.message.request.ModifyGatewaySimulatorRequest;
import com.cannontech.simulators.message.request.ModifyRfnMeterReadAndControlSimulatorRequest;
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
import com.cannontech.simulators.message.request.RfnMeterReadAndControlSimulatorStatusRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.DataStreamingSimulatorStatusResponse;
import com.cannontech.simulators.message.response.GatewaySimulatorStatusResponse;
import com.cannontech.simulators.message.response.NmNetworkSimulatorResponse;
import com.cannontech.simulators.message.response.RfnLcrSimulatorStatusResponse;
import com.cannontech.simulators.message.response.RfnMeterDataSimulatorStatusResponse;
import com.cannontech.simulators.message.response.RfnMeterReadAndControlSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/rfn/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NmIntegrationController {

    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private RfnEventTestingService rfnEventTestingService;
    @Autowired private RfnPerformanceVerificationService performanceVerificationService;
    @Autowired private RfnGatewayDataCache gatewayCache;
    @Autowired private RfnGatewaySimulatorService gatewaySimService;
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired private NmSyncService nmSyncService;
    
    private JmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(NmIntegrationController.class);

    @RequestMapping("viewBase")
    public String viewBase(ModelMap model) {
        return "rfn/viewBase.jsp";
    }

    @RequestMapping("gatewaySimulator")
    public String gatewaySimulator(ModelMap model, FlashScope flash) {
        // Enums for selects
        model.addAttribute("ackTypes", RfnGatewayUpgradeRequestAckType.values());
        model.addAttribute("acceptedUpdateStatusTypes", SimulatedCertificateReplySettings.acceptedUpdateStatusTypes);
        model.addAttribute("firmwareVersionReplyTypes", RfnUpdateServerAvailableVersionResult.values());
        model.addAttribute("firmwareUpdateResultTypes", GatewayFirmwareUpdateRequestResult.values());
        model.addAttribute("gatewayUpdateResultTypes", GatewayUpdateResult.values());
        model.addAttribute("gatewayConfigResultTypes", GatewayConfigResult.values());
        model.addAttribute("connectionTypes", ConnectionStatus.values());
        
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
            return "redirect:viewBase";
        }
        
        return "rfn/gatewayDataSimulator.jsp";
    }

    @RequestMapping("createNewGateway")
    public String createNewGateway(@RequestParam String serial, 
                                   @RequestParam(defaultValue="false") boolean returnGwy800Model,
                                   FlashScope flash) {
        
        gatewaySimService.sendGatewayArchiveRequest(serial, returnGwy800Model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.gatewayCreated", serial));
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("deleteGateway")
    public String deleteGateway(@RequestParam String serial, 
                                @RequestParam(defaultValue="false") boolean returnGwy800Model,
                                FlashScope flash) {
        
        gatewaySimService.sendGatewayDeleteRequest(serial, returnGwy800Model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.rfnTest.gatewaySimulator.gatewayDelete", serial));
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("sendGatewayDataResponse")
    public String sendGatewayDataResponse(@RequestParam String serial,
            @RequestParam(defaultValue = "false") boolean returnGwy800Model, FlashScope flash) {

        try {
            GatewaySimulatorStatusResponse response = simulatorsCommunicationService.sendRequest(
                new GatewaySimulatorStatusRequest(), GatewaySimulatorStatusResponse.class);
            SimulatedGatewayDataSettings settings = response.getDataSettings();
            gatewaySimService.sendGatewayDataResponse(serial, returnGwy800Model, settings);
            flash.setConfirm(new YukonMessageSourceResolvable(
                "yukon.web.modules.dev.rfnTest.gatewaySimulator.gatewayDataResponse", serial));
        } catch (ExecutionException e) {
            log.error("Error communicating with Yukon Simulators Service.", e);
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
            return "redirect:gatewaySimulator";
        }
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableAllGatewaySimulators")
    public String enableAllGatewaySimulators(SimulatedGatewayDataSettings dataSettings,
                                      SimulatedUpdateReplySettings updateSettings,
                                      SimulatedCertificateReplySettings certSettings,
                                      SimulatedFirmwareReplySettings firmwareSettings,
                                      SimulatedFirmwareVersionReplySettings settings,
                                      FlashScope flash) {
        
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();

        request.setDataSettings(dataSettings);
        request.setUpdateSettings(updateSettings);
        request.setCertificateSettings(certSettings);
        request.setFirmwareSettings(firmwareSettings);
        request.setFirmwareVersionSettings(settings);
        
        sendStartStopRequest(request, flash, true);

        return "redirect:gatewaySimulator";
    }

    @RequestMapping("disableAllGatewaySimulators") 
    public String disableAllGatewaySimulators(FlashScope flash) {

        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setAllStop();
            
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableGatewayDataReply")
    public String enableGatewayDataReply(SimulatedGatewayDataSettings dataSettings, FlashScope flash) {
        clearGatewayCache();
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setDataSettings(dataSettings);
        sendStartStopRequest(request, flash, true);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("disableGatewayDataReply")
    public String disableGatewayDataReply(FlashScope flash) {
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setStopDataReply(true);
        
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("enableGatewayUpdateReply")
    public String enableGatewayUpdateReply(SimulatedUpdateReplySettings updateSettings, FlashScope flash) {

        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setUpdateSettings(updateSettings);
        
        sendStartStopRequest(request, flash, true);
        
        return "redirect:gatewaySimulator";
    }
    
    @RequestMapping("stopMetersDisconnectRequest")
    public String disableMeterDisconnectReply(FlashScope flash) {
        ModifyRfnMeterReadAndControlSimulatorRequest request = new ModifyRfnMeterReadAndControlSimulatorRequest();
        request.setStopDisconnectReply(true);
        
        sendRfnMeterReadAndControlStartStopRequest(request, flash, false);
        return "redirect:viewRfnMeterSimulator";
    }

    @RequestMapping("startMetersDisconnectRequest")
    public String enableMeterDisconnectReply(RfnMeterReadAndControlDisconnectSimulatorSettings settings, FlashScope flash) {
        
        ModifyRfnMeterReadAndControlSimulatorRequest request = new ModifyRfnMeterReadAndControlSimulatorRequest();
        request.setDisconnectSettings(settings);
        
        sendRfnMeterReadAndControlStartStopRequest(request, flash, true);
        return "redirect:viewRfnMeterSimulator";
    }

    @RequestMapping("stopMetersReadRequest")
    public String disableMeterReadReply(FlashScope flash) {
        
        ModifyRfnMeterReadAndControlSimulatorRequest request = new ModifyRfnMeterReadAndControlSimulatorRequest();
        request.setStopReadReply(true);
        
        sendRfnMeterReadAndControlStartStopRequest(request, flash, false);
        return "redirect:viewRfnMeterSimulator";
    }

    @RequestMapping("startMetersReadRequest")
    public String enableMeterReadReply(RfnMeterReadAndControlReadSimulatorSettings settings, FlashScope flash) {
        
        ModifyRfnMeterReadAndControlSimulatorRequest request = new ModifyRfnMeterReadAndControlSimulatorRequest();
        request.setReadSettings(settings);
        
        sendRfnMeterReadAndControlStartStopRequest(request, flash, true);
        return "redirect:viewRfnMeterSimulator";
    }
    
    @RequestMapping("disableGatewayUpdateReply")
    public String disableGatewayUpdateReply(FlashScope flash) {
        
        ModifyGatewaySimulatorRequest request = new ModifyGatewaySimulatorRequest();
        request.setStopUpdateReply(true);
        
        sendStartStopRequest(request, flash, false);
        
        return "redirect:gatewaySimulator";
    }

    @RequestMapping("enableGatewayCertificateReply")
    public String enableGatewayCertificateReply(SimulatedCertificateReplySettings certSettings, FlashScope flash) {

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
    public String enableGatewayFirmwareReply(SimulatedFirmwareReplySettings settings, FlashScope flash) {

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
    public String enableFirmwareVersionReply(SimulatedFirmwareVersionReplySettings settings, FlashScope flash) {
        
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

    /**
     * Sends a request to modify a RfnMeterReadAndControl simulator.
     * @param request The request to start or stop simulators.
     * @param isStartRequest Is this a request to start a simulator (true) or stop a simulator(false).
     */
    private void sendRfnMeterReadAndControlStartStopRequest(ModifyRfnMeterReadAndControlSimulatorRequest request, FlashScope flash, boolean isStartRequest) {
        String successKey = isStartRequest ? "yukon.web.modules.dev.rfnTest.rfnMeterReadAndControlMeterSimulator.simStartSuccess" :
                                             "yukon.web.modules.dev.rfnTest.rfnMeterReadAndControlMeterSimulator.simStopSuccess";
        String failureKey = isStartRequest ? "yukon.web.modules.dev.rfnTest.rfnMeterReadAndControlMeterSimulator.simStartFailed" :
                                             "yukon.web.modules.dev.rfnTest.rfnMeterReadAndControlMeterSimulator.simStopFailed";
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
    
    @ModelAttribute("meterReading")
    RfnTestMeterReading rfnTestMeterReadingFactory() {
        return new RfnTestMeterReading();
    }
    
    @RequestMapping("viewMeterReadArchiveRequest")
    public String viewMeterReadArchiveRequest(@ModelAttribute RfnTestMeterReading meterReading, ModelMap model, FlashScope flashScope) {

        model.addAttribute("rfnTypeGroups", rfnEventTestingService.getGroupedRfnTypes());

        Integer numberSent = (Integer) model.get("numberSent");
        if (numberSent != null) {
            flashScope.setMessage(YukonMessageSourceResolvable.createDefaultWithoutCode("Meter read requests sent: " + numberSent), 
                                  numberSent > 0 ? FlashScopeMessageType.SUCCESS : FlashScopeMessageType.ERROR);
        }

        return "rfn/viewMeterReadArchive.jsp";
    }
    
    @RequestMapping("viewConfigNotification")
    public String viewConfigNotification(@ModelAttribute RfnTestMeterReading meterReading, ModelMap model, FlashScope flashScope) {
        model.addAttribute("rfnTypeGroups", rfnEventTestingService.getGroupedRfnTypes());

        Integer numberSent = (Integer) model.get("numberSent");
        Integer totalMessages = (Integer) model.get("totalMessages");
        if (numberSent != null) {
            if (totalMessages < 0 ) {
                flashScope.setMessage(YukonMessageSourceResolvable.createDefaultWithoutCode("Invalid Serial Number range."), FlashScopeMessageType.ERROR);
            } else {
            flashScope.setMessage(YukonMessageSourceResolvable.createDefaultWithoutCode(numberSent + " of " + totalMessages + " RFN Config Notification messages sent."), 
                                  numberSent.equals(totalMessages) ? FlashScopeMessageType.SUCCESS : FlashScopeMessageType.ERROR);
            }
        }

        return "rfn/viewConfigNotification.jsp";
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
    
    @RequestMapping("viewDeviceArchiveRequest")
    public String viewDeviceArchiveRequest(ModelMap model) {
        model.addAttribute("deviceArchiveParameters", new DeviceArchiveRequestParameters());
        model.addAttribute("meterModels", rfnEventTestingService.getGroupedRfnTypes());
        model.addAttribute("rfnLcrModels", RfnManufacturerModel.getRfnLcrModels());
        model.addAttribute("rfDaModels", new ArrayList<String>(Arrays.asList("CBC-8000", "RECL-F4D", "VR-CL7")));
        return "rfn/deviceArchive.jsp";
    }
    
    @RequestMapping("sendDeviceArchiveRequest")
    public String sendDeviceArchiveRequest(@ModelAttribute DeviceArchiveRequestParameters deviceArchiveParameters) {
        rfnEventTestingService.sendArchiveRequest(deviceArchiveParameters.getSerialFrom(),
            deviceArchiveParameters.getSerialTo(), deviceArchiveParameters.getManufacturer(), deviceArchiveParameters.getModel());
        return "redirect:viewDeviceArchiveRequest";
    }

    @RequestMapping("viewRelayArchiveRequest")
    public String viewRelayArchiveRequest() {
        return "rfn/viewRelayArchive.jsp";
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

    @RequestMapping("stopMetersArchiveRequest")
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
    public String viewRfnMeterSimulator(ModelMap model, FlashScope flash) {
        ImmutableSet<PaoType> paoTypes = PaoType.getRfMeterTypes();
        model.addAttribute("paoTypes", paoTypes);
        model.addAttribute("rfnMeterReportingIntervals",ReportingInterval.values());
        model.addAttribute("rfnMeterReadReplies", RfnMeterReadingReplyType.values());
        model.addAttribute("rfnMeterReadDataReplies", RfnMeterReadingDataReplyType.values());
        model.addAttribute("rfnMeterDisconnectInitialReplies", RfnMeterDisconnectInitialReplyType.values());
        model.addAttribute("rfnMeterDisconnectConfirmationReplies", RfnMeterDisconnectConfirmationReplyType.values());
        model.addAttribute("rfnMeterDisconnectQueryResponses", RfnMeterDisconnectState.values());
        
        RfnMeterDataSimulatorStatusResponse rfnMeterResponse = getRfnMeterSimulatorStatusResponse().response;
        RfnMeterReadAndControlSimulatorStatusResponse rfnMeterReadAndControlResponse = getRfnMeterReadAndControlStatusResponse().response;
        
        if (rfnMeterResponse == null) {
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
            return "redirect:viewBase";
        }
        if (rfnMeterReadAndControlResponse == null) {
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
            return "redirect:viewBase";
        }
        
        model.addAttribute("currentSettings", rfnMeterResponse.getSettings());
        model.addAttribute("selectedReportingInterval", rfnMeterResponse.getSettings().getReportingInterval());
        model.addAttribute("rfnMeterSimulatorStatus", buildSimulatorStatusJson(rfnMeterResponse.getStatus()));
        
        model.addAttribute("currentRfnMeterReadAndControlReadSimulatorSettings", rfnMeterReadAndControlResponse.getReadSettings());
        model.addAttribute("currentRfnMeterReadAndControlDisconnectSimulatorSettings", rfnMeterReadAndControlResponse.getDisconnectSettings());
        model.addAttribute("meterDisconnectReplyActive", rfnMeterReadAndControlResponse.isMeterDisconnectReplyActive());
        model.addAttribute("meterReadReplyActive", rfnMeterReadAndControlResponse.isMeterReadReplyActive());
        model.addAttribute("numberOfSimulatorsRunning", rfnMeterReadAndControlResponse.getNumberOfSimulatorsRunning());
        
        return "rfn/rfnMeterSimulator.jsp";
    }

    private RfnMeterReadAndControlSimStatusResponseOrError getRfnMeterReadAndControlStatusResponse() {
        try {
            RfnMeterReadAndControlSimulatorStatusResponse response = simulatorsCommunicationService.sendRequest(
                new RfnMeterReadAndControlSimulatorStatusRequest(), RfnMeterReadAndControlSimulatorStatusResponse.class);
            return new RfnMeterReadAndControlSimStatusResponseOrError(response);
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage() + ".");
            return new RfnMeterReadAndControlSimStatusResponseOrError(json);
        }
    }
    
    @RequestMapping("enableAllRfnReadAndControl")
    public String enableAllRfnReadAndControlSimulators(RfnMeterReadAndControlReadSimulatorSettings readSettings,
                                                       RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings,
                                                       FlashScope flash) {
        
        ModifyRfnMeterReadAndControlSimulatorRequest request = new ModifyRfnMeterReadAndControlSimulatorRequest();

        request.setReadSettings(readSettings);
        request.setDisconnectSettings(disconnectSettings);
        
        sendRfnMeterReadAndControlStartStopRequest(request, flash, true);

        return "redirect:viewRfnMeterSimulator";
    }

    @RequestMapping("disableAllRfnReadAndControl") 
    public String disableAllRfnReadAndControlSimulators(FlashScope flash) {

        ModifyRfnMeterReadAndControlSimulatorRequest request = new ModifyRfnMeterReadAndControlSimulatorRequest();
        request.setAllStop();
            
        sendRfnMeterReadAndControlStartStopRequest(request, flash, false);
        
        return "redirect:viewRfnMeterSimulator";
    }
    
    @RequestMapping("viewLcrDataSimulator")
    public String viewLcrDataSimulator(ModelMap model, FlashScope flash) {
        RfnLcrSimulatorStatusResponse response = getRfnLcrSimulatorStatusResponse().response;
        if (response == null) {
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
            return "redirect:viewBase";
        }
        model.addAttribute("currentSettings", response.getSettings());

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

    @RequestMapping("sendMeterReadArchiveRequest")
    public String sendMeterReadArchiveRequest(@ModelAttribute RfnTestMeterReading meterReading, RedirectAttributes redirectAttributes) {
        
        int numberSent = rfnEventTestingService.sendMeterReadArchiveRequests(meterReading);

        redirectAttributes.addFlashAttribute("numberSent", numberSent);
        redirectAttributes.addFlashAttribute("meterReading", meterReading);

        return "redirect:viewMeterReadArchiveRequest";
    }
    
    @RequestMapping("sendConfigNotification")
    public String sendConfigNotification(@ModelAttribute RfnTestMeterReading meterReading, RedirectAttributes redirectAttributes) {
        int numberSent = rfnEventTestingService.sendConfigNotification(meterReading);
        int totalMessages = meterReading.getSerialTo() == null ? 1 : meterReading.getSerialTo() - meterReading.getSerialFrom() + 1;
        
        redirectAttributes.addFlashAttribute("numberSent", numberSent);
        redirectAttributes.addFlashAttribute("totalMessages", totalMessages);
        redirectAttributes.addFlashAttribute("meterReading", meterReading);

        return "redirect:viewConfigNotification";
    }

    @RequestMapping("sendLcrReadArchiveRequest")
    public String sendLcrReadArchive(int serialFrom, int serialTo, int days, String drReport) throws IOException, DecoderException {
        rfnEventTestingService.sendLcrReadArchive(serialFrom, serialTo, days, DRReport.valueOf(drReport));
        return "redirect:viewLcrReadArchiveRequest";
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
        try {
            nmSyncService.sendSyncRequest();
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
            return "redirect:viewBase";
        }
        
        model.addAttribute("deviceErrors", DeviceDataStreamingConfigError.values());
        
        return "rfn/dataStreamingSimulator.jsp";
    }
    
    @RequestMapping("startDataStreamingSimulator")
    public String startDataStreamingSimulator(SimulatedDataStreamingSettings settings, FlashScope flash) {
        try {            
            ModifyDataStreamingSimulatorRequest request = new ModifyDataStreamingSimulatorRequest();
            request.setSettings(settings);
            SimulatorResponseBase response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            
            //start gateway simulator
            boolean startedGatewaySimualtor = false;
            GatewaySimulatorStatusResponse gatewayResponse = simulatorsCommunicationService.sendRequest(
                new GatewaySimulatorStatusRequest(), GatewaySimulatorStatusResponse.class);
            SimulatedGatewayDataSettings gatewaySettings = gatewayResponse.getDataSettings();
            if(!gatewayResponse.isDataReplyActive()){
                enableGatewayDataReply(gatewaySettings, flash);
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
        model.addAttribute("metadataResponseTypes", RfnMetadataMultiResponseType.values());
        model.addAttribute("metadataQueryResponseTypes", RfnMetadataMultiQueryResultType.values());
        
        NmNetworkSimulatorRequest simRequest = new NmNetworkSimulatorRequest(Action.GET_SETTINGS);
        SimulatorResponseBase response = sendRequest(simRequest, null, flash); 
        if (response == null) {
            flash.setError(new YukonMessageSourceResolvable(SimulatorsCommunicationService.COMMUNICATION_ERROR_KEY));
            return "redirect:viewBase";
        }
        
        SimulatedNmMappingSettings settings = ((NmNetworkSimulatorResponse) response).getSettings();
        model.addAttribute("simulatorRunning", ((NmNetworkSimulatorResponse) response).isRunning());

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
    
    private static class RfnMeterReadAndControlSimStatusResponseOrError {
        public final RfnMeterReadAndControlSimulatorStatusResponse response;
        public final Map<String, Object> errorJson;

        public RfnMeterReadAndControlSimStatusResponseOrError(RfnMeterReadAndControlSimulatorStatusResponse response) {
            this.response = response;
            errorJson = null;
        }

        public RfnMeterReadAndControlSimStatusResponseOrError(Map<String, Object> errorJson) {
            response = null;
            this.errorJson = errorJson;
        }
    }
    
    @RequestMapping("viewStatusArchiveRequest")
    public String viewStatusArchiveRequest(ModelMap model) {
        model.addAttribute("dRStatusCodes", DemandResetStatusCode.values());
        return "rfn/viewStatusArchiveRequest.jsp";
    }
    
    @RequestMapping("sendStatusArchiveRequest")
    public String sendStatusArchiveRequest(String statusCode, int messageCount) {
        DemandResetStatusCode demandResetStatusCode = DemandResetStatusCode.valueOf(Integer.parseInt(statusCode));
        log.info("Demand Reset Status Code " + statusCode + " (" + demandResetStatusCode + ")" + " was sent to " + messageCount + " devices.");
        rfnEventTestingService.sendStatusArchiveRequest(demandResetStatusCode, messageCount);
        return "redirect:viewStatusArchiveRequest";
    }
}