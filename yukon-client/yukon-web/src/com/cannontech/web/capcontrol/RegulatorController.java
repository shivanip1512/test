package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.RegulatorEventsDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.export.RegulatorPointMappingExportService;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeRange;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.regulator.setup.FileExporter;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResult;
import com.cannontech.web.capcontrol.regulator.setup.service.RegulatorMappingService;
import com.cannontech.web.capcontrol.validators.RegulatorValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.yukon.IDatabaseCache;

@RequestMapping("regulators")
@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class RegulatorController {
    
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private RegulatorPointMappingExportService exportService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RegulatorValidator validator;
    @Autowired private RegulatorEventsDao eventsDao;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private RegulatorMappingService mappingService;
    @Autowired private ZoneDao zoneDao;
    @Autowired private FileExporter fileExporter;
    @Autowired private UserPreferenceService userPreferenceService;
    
    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String view(HttpServletRequest req, ModelMap model, @PathVariable int id, YukonUserContext userContext) 
    throws IOException {
        
        Regulator regulator = regulatorService.getRegulatorById(id);
        model.addAttribute("mode",  PageEditMode.VIEW);
        
        model.addAttribute("ranges", TimeRange.values());
        
        Map<String, Integer> hours = new HashMap<>();
        for (TimeRange range : TimeRange.values()) {
            hours.put(range.name(), range.getHours());
        }
        model.put("hours", hours);
        
        // Check user preference for last event range
        TimeRange range =
            TimeRange.valueOf(userPreferenceService.getPreference(userContext.getYukonUser(),
                UserPreferenceName.DISPLAY_EVENT_RANGE));
        model.addAttribute("lastRange", range);

        return setUpModel(model, regulator, userContext);
    }
    
    @RequestMapping(value="{id}/mapping-table", method=RequestMethod.GET)
    public String mappingTable(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        
        Regulator regulator = regulatorService.getRegulatorById(id);
        Map<RegulatorPointMapping, Integer> sortedMappings =
                regulatorService.sortMappingsAllKeys(regulator.getMappings(), userContext);
        regulator.setMappings(sortedMappings);
        model.addAttribute("regulator",  regulator);
        model.addAttribute("mode",  PageEditMode.VIEW);
        
        return "regulator/mapping-table.jsp";
    }
    
    private String setUpModel(ModelMap model, Regulator regulator, YukonUserContext userContext) {
        
        Object modelReg = model.get("regulator");
        if (modelReg instanceof Regulator) {
            regulator = (Regulator) modelReg;
        }
        
        Map<RegulatorPointMapping, Integer> sortedMappings =
                regulatorService.sortMappingsAllKeys(regulator.getMappings(), userContext);
        
        regulator.setMappings(sortedMappings);
        model.addAttribute("regulator", regulator);
        
        model.addAttribute("regulatorTypes", PaoType.getRegulatorTypes());
        
        Set<LightDeviceConfiguration> availableConfigs = new HashSet<>();
        for (PaoType type : PaoType.getRegulatorTypes()) {
            availableConfigs.addAll(deviceConfigDao.getAllConfigurationsByType(type));
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
        
        model.addAttribute("paoTypeMap", RegulatorPointMapping.getMappingsByPaoType());
        
        return "regulator/regulator.jsp";
    }
    
    @RequestMapping(value="{id}/edit", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        
        Regulator regulator = regulatorService.getRegulatorById(id);
        model.addAttribute("mode",  PageEditMode.EDIT);
        
        return setUpModel(model, regulator, userContext);
    }
    
    @RequestMapping(value="{id}/automap", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public @ResponseBody Map<String, Object> automap(@PathVariable int id, YukonUserContext userContext) {
        
        Regulator regulator = regulatorService.getRegulatorById(id);
        RegulatorMappingResult result = mappingService.start(regulator);
        Map<String, Object> json = mappingService.buildJsonResult(result, userContext);
        
        return json;
    }
    
    @RequestMapping("create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, YukonUserContext userContext) {
        
        Regulator regulator = new Regulator();
        model.addAttribute("mode",  PageEditMode.CREATE);
        
        return setUpModel(model, regulator, userContext);
    }
    
    @RequestMapping(value={""}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("regulator") Regulator regulator,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        validator.validate(regulator, result);
        
        if (result.hasErrors()) {
            return bindAndForward(regulator, result, redirectAttributes);
        }
        
        int id;
        try {
            id = regulatorService.save(regulator);
        } catch (InvalidDeviceTypeException e) {
            //Something happened to make the config invalid since validation.
            result.rejectValue("configId", "yukon.web.modules.capcontrol.regulator.error.invalidConfig");
            
            return bindAndForward(regulator, result, redirectAttributes);
        }
        
        return "redirect:regulators/" + id;
    }
    
    private String bindAndForward(Regulator regulator, BindingResult result, RedirectAttributes attrs) {
        
        attrs.addFlashAttribute("regulator", regulator);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.regulator", result);
        
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
        
        return "redirect:/capcontrol/search/searchResults?cbc_lastSearch=__cti_oRegulators__";
    }
    
    @RequestMapping(value="{id}/build-mapping-file", method = RequestMethod.GET)
    public void export(HttpServletResponse resp, @PathVariable int id, YukonUserContext userContext) throws IOException {
        String key = fileExporter.build(resp, Collections.singleton(id), userContext);
        fileExporter.export(key, resp);
    }
    
}