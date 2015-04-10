package com.cannontech.web.capcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.RegulatorEventsDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.export.RegulatorPointMappingExportService;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResult;
import com.cannontech.web.capcontrol.regulator.setup.service.RegulatorMappingService;
import com.cannontech.web.capcontrol.validators.RegulatorValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RequestMapping("regulators")
@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class RegulatorController {
    
    @Autowired private DateFormattingService dateFormatting;
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private RegulatorPointMappingExportService exportService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RegulatorValidator validator;
    @Autowired private RegulatorEventsDao eventsDao;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private RegulatorMappingService mappingService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ZoneDao zoneDao;
    
    private final Logger log = YukonLogManager.getLogger(RegulatorController.class);
    private static final Map<RegulatorEvent.EventType, String> classNameForEventType;
    
    static {
        ImmutableMap.Builder<EventType, String> builder = ImmutableMap.builder();
        
        builder.put(EventType.TAP_UP, "icon-bullet-go-up");
        builder.put(EventType.TAP_DOWN, "icon-bullet-go-down");
        builder.put(EventType.INCREASE_SETPOINT, "icon-bullet-go-up");
        builder.put(EventType.DECREASE_SETPOINT, "icon-bullet-go-down");
        builder.put(EventType.INTEGRITY_SCAN, "icon-transmit-blue");
        builder.put(EventType.ENABLE_REMOTE_CONTROL, "icon-accept");
        builder.put(EventType.DISABLE_REMOTE_CONTROL, "icon-delete");
        
        classNameForEventType = builder.build();
        
    }
    
    private static final String eventTypeBaseKey = "yukon.web.modules.capcontrol.ivvc.eventType";
    
    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        
        Regulator regulator = regulatorService.getRegulatorById(id);
        model.addAttribute("mode",  PageEditMode.VIEW);
        
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
        
        return "redirect:/capcontrol/tier/areas";
    }
    
    @RequestMapping(value="{id}/events")
    public @ResponseBody Map<String,Object> getEvents(@PathVariable int id, 
            @RequestParam(defaultValue="0") long lastUpdate, YukonUserContext userContext) {
        
        Map<String,Object> resp = new HashMap<>();
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Instant start = Instant.now();
        resp.put("timestamp", start.getMillis());
        
        List<RegulatorEvent> events = eventsDao.getForIdSinceTimestamp(id, new Instant(lastUpdate));
        
        if (events.size() > 20) {
            events = events.subList(0, 20);
        }
        
        List<Map<String, String>> eventsJson = 
                Lists.transform(events, new Function<RegulatorEvent, Map<String, String>>() {
            
            @Override
            public Map<String, String> apply(RegulatorEvent event) {
                
                ImmutableMap.Builder<String, String> eventJson = new ImmutableMap.Builder<>();
                
                String formattedTime = dateFormatting.format(event.getTimestamp(), DateFormatEnum.BOTH, userContext);
                eventJson.put("timestamp", formattedTime);
                
                String iconClass = classNameForEventType.get(event.getType());
                eventJson.put("icon", iconClass);
                
                eventJson.put("user", event.getUserName());
                
                String key = eventTypeBaseKey + "." + event.getType().name();
                
                String phaseString = accessor.getMessage(event.getPhase());
                String message = accessor.getMessage(key, phaseString);
                
                eventJson.put("message", message);
                
                return eventJson.build();
            }
        });
        resp.put("events", eventsJson);
        
        return resp;
    }
    
    @RequestMapping(value="{id}/build-mapping-file", method = RequestMethod.GET)
    public void export(HttpServletResponse resp, @PathVariable int id, YukonUserContext userContext) {
        
        String prefix = "RegulatorAttributeMapping";
        LiteYukonPAObject regulator = dbCache.getAllPaosMap().get(id);
        
        try {
            File csvFile = exportService.generateCsv(prefix, Collections.singletonList(id), userContext);
            
            //Set response properties for CSV file
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=" + csvFile.getName());
            resp.setHeader("Content-Length", Long.toString(csvFile.length()));
            
            FileCopyUtils.copy(new FileInputStream(csvFile), resp.getOutputStream());
        } catch (IOException e) {
            log.error("Error creating regulator attribute mapping file for regulator: "
                    + regulator + " " + regulator.getPaoIdentifier(), e);
        }
        
        return; 
    }
    
}