package com.cannontech.web.capcontrol;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.validators.RegulatorValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonProcessingException;

@RequestMapping("regulators")
@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class RegulatorController {

    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RegulatorValidator regulatorValidator;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private ZoneDao zoneDao;

    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, YukonUserContext context) {

        Regulator regulator = regulatorService.getRegulatorById(id);

        model.addAttribute("mode",  PageEditMode.VIEW);
        return setUpModelMap(model, regulator, context);
    }

    private String setUpModelMap(ModelMap model, Regulator regulator, YukonUserContext context) {

        if (!model.containsAttribute("regulator")) {
            Map<RegulatorPointMapping, Integer> sortedMappings =
                    regulatorService.sortMappingsAllKeys(regulator.getMappings(), context);

            regulator.setMappings(sortedMappings);
            model.addAttribute("regulator", regulator);
        }

        model.addAttribute("regulatorTypes", PaoType.getRegulatorTypes());

        Set<LightDeviceConfiguration> availableConfigs = new HashSet<>();
        for (PaoType type : PaoType.getRegulatorTypes()) {
            availableConfigs.addAll(deviceConfigurationDao.getAllConfigurationsByType(type));
        }
        model.addAttribute("availableConfigs", availableConfigs);

        if (regulator.getId() != null) {
            try {
                Zone zone = zoneDao.getZoneByRegulatorId(regulator.getId());
                model.addAttribute("zone", zone);
            } catch (OrphanedRegulatorException e) {
                //The regulator is an orphan, so there is no zone to put in the model.
            }
        }

        try {
            String jsonMap = JsonUtils.toJson(RegulatorPointMapping.getMappingsByPaoType());
            model.addAttribute("paoTypeMap", jsonMap);
        } catch (JsonProcessingException e) {
            //Writing this simple object to JSON should not throw an exception.
            throw new RuntimeException(e);
        }

        return "regulator.jsp";
    }

    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext context) {

        Regulator regulator = regulatorService.getRegulatorById(id);

        model.addAttribute("mode",  PageEditMode.EDIT);
        return setUpModelMap(model, regulator, context);
    }

    @RequestMapping("create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, YukonUserContext context) {

        Regulator regulator = new Regulator();

        model.addAttribute("mode",  PageEditMode.CREATE);
        return setUpModelMap(model, regulator, context);
    }

    @RequestMapping(value={""}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("regulator") Regulator regulator,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        regulatorValidator.validate(regulator, bindingResult);

        if (bindingResult.hasErrors()) {
            return bindAndForward(regulator, bindingResult, redirectAttributes);
        }

        int id;
        try {
            id = regulatorService.save(regulator);
        } catch (InvalidDeviceTypeException e) {
            //Something happened to make the config invalid since validation.
            bindingResult.rejectValue("configId", "yukon.web.modules.capcontrol.regulator.error.invalidConfig");

            return bindAndForward(regulator, bindingResult, redirectAttributes);
        }

        return "redirect:regulators/" + id;
    }

    private String bindAndForward(Regulator regulator, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("regulator", regulator);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.regulator", bindingResult);

        if (regulator.getId() == null) {
            return "redirect:regulators/create";
        }

        return "redirect:regulators/" + regulator.getId() + "/edit";
    }

    @RequestMapping(value="{id}", method = RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(HttpServletResponse response, @PathVariable int id) {

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        try {
            zoneDao.getZoneByRegulatorId(id);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        } catch (OrphanedRegulatorException e) {
          //The regulator is an orphan, which is what we need when deleting
        }

        regulatorService.delete(id);

        return "redirect:/capcontrol/tier/areas";
    }

}
