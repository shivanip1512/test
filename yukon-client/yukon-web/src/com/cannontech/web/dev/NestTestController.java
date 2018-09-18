package com.cannontech.web.dev;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.StringUtils;
import com.cannontech.dr.nest.service.NestSimulatorService;
import com.cannontech.dr.nest.service.impl.NestSimulatorServiceImpl;
import com.cannontech.dr.nest.service.impl.NestSyncServiceImpl;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.NestFileGenerationSetting;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/nest/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NestTestController {
    private static final Logger log = YukonLogManager.getLogger(NestTestController.class); 
    
    @Autowired NestSimulatorService nestService;
    @Autowired NestSyncServiceImpl nestSync; 
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, YukonUserContext userContext) {
        String defaultFileName = nestService.getFileName(YukonSimulatorSettingsKey.NEST_FILE_NAME);
        model.addAttribute("defaultFileName", defaultFileName);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("helpTextForExistingFile", accessor.getMessage("yukon.web.modules.dev.nest.useNestFile.helpText",
            NestSimulatorServiceImpl.SIMULATED_FILE_PATH));
        
        return "nest/home.jsp";
    }

    @RequestMapping(value = "/useAsNestFile", method = RequestMethod.POST)
    public String useAsNestFile(String fileName, FlashScope flash) {
        if (StringUtil.isBlank(fileName)) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.useNestFile.blankFileName"));
            return "redirect:home";
        } else {
            File file = new File(NestSimulatorServiceImpl.SIMULATED_FILE_PATH, fileName);
            if (!file.exists()) {
                flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.useNestFile.fileNotExists",
                    NestSimulatorServiceImpl.SIMULATED_FILE_PATH));
                return "redirect:home";
            }
        }
        nestService.saveFileName(fileName);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.useNestFile.savedSuccessfully"));
        return "redirect:home";
    }

    @RequestMapping(value = "/generateFile", method = RequestMethod.POST)
    public String generateFile(@ModelAttribute NestFileGenerationSetting settings, YukonUserContext userContext,
            FlashScope flash, BindingResult bindingResult) {

        List<String> groupNames = StringUtils.parseStringsForList(settings.getGroupName(), ",");
        if (groupNames.size() > 3 || groupNames.size() < 1) {
            flash.setError(
                new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.groupSizeError"));
            return "redirect:home";
        }
        
        if (settings.getNoOfRows() == null || settings.getNoOfThermostats() == null) {
            flash.setError(
                new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.rowsThermostatError"));
            return "redirect:home";
        }
        
        String generatedFileName;
        try {
            generatedFileName = nestService.generateExistingFile(groupNames, settings.getNoOfRows(),
                settings.getNoOfThermostats(), settings.isWinterProgram(), userContext.getYukonUser());
        } catch (Exception e) {
            log.error("Error generating nest file " + e);
            flash.setError(
                new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.fileGenerationError"));
            return "redirect:home";
        }

        if (settings.isDefaultFile()) {
            nestService.saveFileName(generatedFileName);
        }

        flash.setConfirm(
            new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.fileGenerationSuccessful"));
        return "redirect:home";
    }
    
    @RequestMapping(value = "/syncYukonAndNest", method = RequestMethod.GET)
    public String sync() {
        nestSync.sync();
        return "redirect:home";
    }
}
