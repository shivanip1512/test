package com.cannontech.web.dev;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.ivvc.model.IvvcSimulatorSettings;
import com.cannontech.ivvc.model.IvvcSimulatorStatus;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.message.request.IvvcSimulatorSettingsChangedRequest;
import com.cannontech.simulators.message.request.IvvcSimulatorStartRequest;
import com.cannontech.simulators.message.request.IvvcSimulatorStatusRequest;
import com.cannontech.simulators.message.request.IvvcSimulatorStopRequest;
import com.cannontech.simulators.message.response.IvvcSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/ivvc/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class IvvcSimulatorController {
    
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    
    private JmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(IvvcSimulatorController.class);
    private final IvvcSimulatorSettings ivvcSimulatorSettings = new IvvcSimulatorSettings(false, 3000.0, true, 1200, 1200);
    
    @RequestMapping("ivvcSimulator")
    public String ivvcSimulator(ModelMap model) {
        model.addAttribute("ivvcSimulatorSettings", ivvcSimulatorSettings);
        IvvcSimulatorResponse response = getIvvcSimulatorStatusResponse().response;
        if (response == null) {
            return "ivvc/ivvcSimulator.jsp";
        }
        model.addAttribute("ivvcSimulatorSettings", response.getSettings());
        model.addAttribute("ivvcSimulatorStatus", response.getStatus());
        String blockedPointsString = yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_BLOCKED_POINTS);
        List<Integer> blockedPoints = Stream.of(blockedPointsString.split(","))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        model.addAttribute("blockedPoints", blockedPoints);
        String badQualityPointsString = yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_BAD_QUALITY_POINTS);
        List<Integer> badQualityPoints = Stream.of(badQualityPointsString.split(","))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        model.addAttribute("badQualityPoints", badQualityPoints);
        return "ivvc/ivvcSimulator.jsp";
    }
    
    @RequestMapping("existing-ivvcSimulator-status")
    @ResponseBody
    public Map<String, Object> existingIvvcSimulatorStatus(YukonUserContext userContext) {
        IvvcSimResponseOrError status = getIvvcSimulatorStatusResponse();
        if (status.response == null) {
            return status.errorJson;
        }
        return buildSimulatorStatusJson(status.response.getStatus());
    }
    
    @RequestMapping("startIvvcSimulatorRequest")
    public void startIvvcSimulatorRequest(IvvcSimulatorSettings ivvcSimulatorSettings, FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new IvvcSimulatorStartRequest(ivvcSimulatorSettings),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }
    
    @RequestMapping("stopIvvcSimulatorRequest")
    public void stopIvvcSimulatorRequest(YukonUserContext userContext, FlashScope flash) {
        try {
            simulatorsCommunicationService.sendRequest(new IvvcSimulatorStopRequest(),
                SimulatorResponseBase.class);
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(
                "Unable to send message to Simulator Service: " + e.getMessage()));
        }
    }
    
    @RequestMapping(value = "saveSimulatorSettings", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveSimulatorSettings(IvvcSimulatorSettings ivvcSimulatorSettings, YukonUserContext userContext) {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            SimulatorResponseBase response = simulatorsCommunicationService.sendRequest(
                new IvvcSimulatorSettingsChangedRequest(ivvcSimulatorSettings), SimulatorResponseBase.class);
            json.put("hasError", !response.isSuccessful());
            if (response.isSuccessful()) {
                json.put("message", accessor.getMessage("yukon.web.modules.dev.ivvc.ivvcSimulator.saveSettings.success"));
            } else {
                json.put("message", accessor.getMessage("yukon.web.modules.dev.ivvc.ivvcSimulator.saveSettings.error"));
            }
        } catch (Exception e) {
            log.error(e);
            json.put("hasError", true);
            json.put("message", accessor.getMessage("yukon.web.modules.dev.ivvc.ivvcSimulator.saveSettings.error"));
        }
        return json;
    }
    
    private IvvcSimResponseOrError getIvvcSimulatorStatusResponse() {
        try {
            IvvcSimulatorResponse response = simulatorsCommunicationService.sendRequest(
                new IvvcSimulatorStatusRequest(), IvvcSimulatorResponse.class);
            return new IvvcSimResponseOrError(response);
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage() + ".");
            return new IvvcSimResponseOrError(json);
        }
    }
    
    private static class IvvcSimResponseOrError {
        public final IvvcSimulatorResponse response;
        public final Map<String, Object> errorJson;

        public IvvcSimResponseOrError(IvvcSimulatorResponse response) {
            this.response = response;
            errorJson = null;
        }

        public IvvcSimResponseOrError(Map<String, Object> errorJson) {
            response = null;
            this.errorJson = errorJson;
        }
    }
    
    private Map<String, Object> buildSimulatorStatusJson(IvvcSimulatorStatus status) {
        Map<String, Object> json = new HashMap<>();
        json.put("running", status.isRunning());
        return json;
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder binder, YukonUserContext userContext) {
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
