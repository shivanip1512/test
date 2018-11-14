package com.cannontech.web.dev;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.development.model.NestControlEventSimulatorParameters;
import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestURL;
import com.cannontech.dr.nest.model.v3.ControlEvent;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;
import com.cannontech.dr.nest.model.v3.RushHourEventType;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.dr.nest.service.NestSimulatorService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.dr.nest.service.impl.NestSimulatorServiceImpl;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.NestFileGenerationSetting;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/nest/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NestTestController {
    private static final Logger log = YukonLogManager.getLogger(NestTestController.class); 
    
    @Autowired NestSimulatorService nestService;
    @Autowired NestCommunicationService nestComm;
    @Autowired NestSyncService nestSync; 
    @Autowired NestService nestS; 
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    
    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupDateTimePropertyEditor(binder, userContext, BlankMode.CURRENT);
    }

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("currentNestVersion", getNestVersion());
        return "nest/home.jsp";
    }
    
    @RequestMapping(value = "viewNestVersion", method = RequestMethod.GET)
    public String nestVersion(ModelMap model, YukonUserContext userContext) {
        Integer savedNestVersion = nestService.getNestVersion(YukonSimulatorSettingsKey.NEST_VERSION);
        model.addAttribute("savedNestVersion", savedNestVersion);
        
        Set<Integer> allAvailableVersions = NestURL.getAllVersions();
        model.addAttribute("allAvailableVersions", allAvailableVersions);
        return "nest/nestVersion.jsp";
    }
    
    @RequestMapping(value = "saveNestVersion", method = RequestMethod.POST)
    public String saveNestVersion(ModelMap model, YukonUserContext userContext,@RequestParam("version") Integer version, FlashScope flash) {
        nestService.saveNestVersion(version);
        flash.setConfirm(
            new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestVersion.savedSuccessfully"));
        return "redirect:viewNestVersion";
    }
    
    @RequestMapping(value = "viewNestFileSetting", method = RequestMethod.GET)
    public String fileSettings(ModelMap model, YukonUserContext userContext) {
        String defaultFileName = nestService.getFileName(YukonSimulatorSettingsKey.NEST_FILE_NAME);
        model.addAttribute("defaultFileName", defaultFileName);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("helpTextForExistingFile", accessor.getMessage(
            "yukon.web.modules.dev.nest.useNestFile.helpText", NestSimulatorServiceImpl.SIMULATED_FILE_PATH));
        return "nest/nestFileSettings.jsp";
    }
    
    
    @RequestMapping(value = "viewNestSync", method = RequestMethod.GET)
    public String nestSync() {
        return "nest/nestSync.jsp";
    }

    @RequestMapping(value = "viewControlEvents", method = RequestMethod.GET)
    public String viewControlEvents(ModelMap model, YukonUserContext userContext) {
        setupModelMap(model);
        NestControlEventSimulatorParameters nestParameters = new NestControlEventSimulatorParameters();
        model.addAttribute("nestParameters", nestParameters);
        return "nest/nestControlEvents.jsp";
    }
    
    @RequestMapping(value = "/useAsNestFile", method = RequestMethod.POST)
    public String useAsNestFile(String fileName, FlashScope flash) {
        if (StringUtil.isBlank(fileName)) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.useNestFile.blankFileName"));
            return "redirect:viewNestFileSetting";
        } else {
            File file = new File(NestSimulatorServiceImpl.SIMULATED_FILE_PATH, fileName);
            if (!file.exists()) {
                flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.useNestFile.fileNotExists",
                    NestSimulatorServiceImpl.SIMULATED_FILE_PATH));
                return "redirect:viewNestFileSetting";
            }
        }
        nestService.saveFileName(fileName);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.useNestFile.savedSuccessfully"));
        return "redirect:viewNestFileSetting";
    }

    @RequestMapping(value = "/generateFile", method = RequestMethod.POST)
    public String generateFile(@ModelAttribute NestFileGenerationSetting settings, YukonUserContext userContext,
            FlashScope flash, BindingResult bindingResult) {

        List<String> groupNames = StringUtils.parseStringsForList(settings.getGroupName(), ",");
        if (groupNames.size() > 3 || groupNames.size() < 1) {
            flash.setError(
                new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.groupSizeError"));
            return "redirect:viewNestFileSetting";
        }
        
        if (settings.getNoOfRows() == null || settings.getNoOfThermostats() == null) {
            flash.setError(
                new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.rowsThermostatError"));
            return "redirect:viewNestFileSetting";
        }
        
        String generatedFileName = nestService.generateExistingFile(groupNames, settings.getNoOfRows(),
                settings.getNoOfThermostats(), settings.isWinterProgram(), userContext.getYukonUser());
    
        if (settings.isDefaultFile()) {
            nestService.saveFileName(generatedFileName);
        }

        flash.setConfirm(
            new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.fileGenerationSuccessful"));
        return "redirect:viewNestFileSetting";
    }
    
    @RequestMapping(value = "/syncYukonAndNest", method = RequestMethod.GET)
    public String sync(FlashScope flash) {
        nestSync.sync(true);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.sync.success"));
        return "redirect:viewNestSync";
    }

    @RequestMapping(value = "/downloadExisting", method = RequestMethod.GET)
    public String downloadExisting(FlashScope flash) {
        log.info("Downloding existing file");
        List<NestExisting> nestExisting = nestService.downloadExisting();
        log.info("Nest Existing " + nestExisting);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.downloadExisting.success"));
        return "redirect:viewNestSync";
    }
    
    @RequestMapping(value = "/sendEvent", method = RequestMethod.POST)
    public String sendEvent(@ModelAttribute("nestParameters") NestControlEventSimulatorParameters nestParameters,
            ModelMap model, BindingResult result, HttpServletResponse resp, FlashScope flash) {
        if (nestParameters.getGroupName().isEmpty()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.controlEvent.emptyGroup"));
            return "redirect:viewControlEvents";
        }
        if ((nestParameters.getStartTime()).isAfter(nestParameters.getStopTime())) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.controlEvent.incorrectDate"));
            return "redirect:viewControlEvents";
        }
        
        setupModelMap(model);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "redirect:viewControlEvents";
        }
        String message = sendEvent(nestParameters);
        model.addAttribute("message", message);
        return "nest/nestControlEvents.jsp";
    }
    
    private void setupModelMap(ModelMap model) {
        model.addAttribute("startTime", Instant.now());
        model.addAttribute("stopTime", Instant.now().plus(Duration.standardHours(4)));
        model.addAttribute("loadShapingPreparations", LoadShapingPreparation.values());
        model.addAttribute("loadShapingPeaks", LoadShapingPeak.values());
        model.addAttribute("loadShapingPosts", LoadShapingPost.values());
        
        List<GearControlMethod> controlMethods = new ArrayList<>();
        controlMethods.add(GearControlMethod.NestCriticalCycle);
        controlMethods.add(GearControlMethod.NestStandardCycle);
        model.addAttribute("controlMethods", controlMethods);
    }

    // Sends control event to nest
    private String sendEvent(NestControlEventSimulatorParameters nestParameters) {
        List<String> groupNames = StringUtils.parseStringsForList(nestParameters.getGroupName(), ",");
        String period = new Period(nestParameters.getStartTime(), nestParameters.getStopTime()).toString();

        if (nestParameters.getControlMethod() == GearControlMethod.NestCriticalCycle) {
            ControlEvent event = new ControlEvent(nestParameters.getStartTime().toString(), period, groupNames);
            String requestUrl = nestComm.constructNestUrl(getNestVersion(), NestURL.CREATE_EVENT);
            return nestComm.getNestResponse(requestUrl, event, RushHourEventType.CRITICAL);

        } else if (nestParameters.getControlMethod() == GearControlMethod.NestStandardCycle) {
            LoadShapingOptions loadShaping =
                    new LoadShapingOptions(PrepLoadShape.PREP_STANDARD, PeakLoadShape.PEAK_STANDARD, PostLoadShape.POST_STANDARD);
            ControlEvent event = new ControlEvent(nestParameters.getStartTime().toString(), period, groupNames, loadShaping);
            String requestUrl = nestComm.constructNestUrl(getNestVersion(), NestURL.CREATE_EVENT);
            return nestComm.getNestResponse(requestUrl, event, RushHourEventType.STANDARD);
        }
        return "";
    }
    
    // Get current nest version to use. If its not set gets latest version.
    private Integer getNestVersion() {
        Integer savedNestVersion = nestService.getNestVersion(YukonSimulatorSettingsKey.NEST_VERSION);
        if (savedNestVersion == null) {
            return NestURL.CURRENT_VERSION;
        } else {
            return savedNestVersion;
        }
    }
}
