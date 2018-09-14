package com.cannontech.web.dev;

import java.util.List;

import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.util.StringUtils;
import com.cannontech.dr.nest.service.NestSimulatorService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.NestFileGenerationSetting;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/nest/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NestTestController {
    @Autowired NestSimulatorService nestService;

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home() {
        return "nest/home.jsp";
    }

    @RequestMapping(value = "/useAsNestFile", method = RequestMethod.POST)
    public String useAsNestFile(String fileName, FlashScope flash) {
        if (StringUtil.isBlank(fileName)) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.useNestFile.blankFileName"));
            return "redirect:home";
        }
        nestService.saveSettings(fileName);
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
            flash.setError(
                new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.fileGenerationError"));
            return "redirect:home";
        }

        if (settings.isUseFileForNest()) {
            nestService.saveSettings(generatedFileName);
        }

        flash.setConfirm(
            new YukonMessageSourceResolvable("yukon.web.modules.dev.nest.nestFileGenerator.fileGenerationSuccessful"));
        return "redirect:home";
    }
}
