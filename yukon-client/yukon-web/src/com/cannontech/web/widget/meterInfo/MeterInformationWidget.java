package com.cannontech.web.widget.meterInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.IedMeter;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.MeteringEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.notes.search.result.model.PaoNotesSearchResult;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meter.MeterValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.widget.meterInfo.model.IEDMeterModel;
import com.cannontech.web.widget.meterInfo.model.MeterModel;
import com.cannontech.web.widget.meterInfo.model.PlcMeterModel;
import com.cannontech.web.widget.meterInfo.model.RfMeterModel;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display basic device information
 */
@Controller
@RequestMapping("/meterInformationWidget/*")
public class MeterInformationWidget extends AdvancedWidgetControllerBase {
    @Autowired private ServerDatabaseCache serverDatabaseCache;
    private final static String baseKey = "yukon.web.widgets.meterInformationWidget.";
    
    @Autowired
    public MeterInformationWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @Autowired private MeterDao meterDao;
    @Autowired private MeteringEventLogService meteringEventLogService;
    @Autowired private MeterValidator meterValidator;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping("render")
    public String render(ModelMap model, int deviceId) {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        model.addAttribute("meter", meter);
        
        if (meter instanceof RfnMeter) {
            /* Show RFMESH settings such as serial number, model, and manufacturer*/
            model.addAttribute("showRFMeshSettings", true);
        } else if (meter instanceof PlcMeter) {
            /* Show PLC settings such as route and physcal address */
            model.addAttribute("showCarrierSettings", true);
        }
        
        if (paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            model.addAttribute("supportsPing", true);
        }
        
        /* PAO Note field */
        boolean hasNotes = paoNotesService.hasNotes(deviceId);
        model.addAttribute("hasNotes", hasNotes);
        if (hasNotes) {
            List<PaoNotesSearchResult> recentNotes = paoNotesService.findMostRecentNotes(deviceId, 1);
            String noteText = recentNotes.get(0).getPaoNote().getNoteText();
            model.addAttribute("note", StringUtils.abbreviate(noteText, 28));
        }
        
        return "meterInformationWidget/render.jsp";
    }
    
    @RequestMapping(value = "ping", method = RequestMethod.POST)
    public String ping(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        CommandRequestDevice request = new CommandRequestDevice("ping", new SimpleDevice(meter.getPaoIdentifier()));
        CommandResultHolder result =
            commandExecutionService.execute(request, DeviceRequestType.METER_INFORMATION_PING_COMMAND, user);
        model.addAttribute("isRead", true);
        model.addAttribute("result", result);
        
        return "common/meterReadingsResult.jsp";
    }
    
    @RequestMapping(value="check-meter-number", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> check(ModelMap model, YukonUserContext userContext, int deviceId, String meterNumber) {
        
        Map<String, Object> json = new HashMap<>();
        List<YukonMeter> others = meterDao.getForMeterNumber(meterNumber, deviceId);
        if (others.isEmpty()) {
            json.put("inuse", false);
        } else {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            json.put("inuse", true);
            json.put("message", accessor.getMessage(baseKey + "meterNumber.inuse", others.size()));
        }
        
        return json;
    }
    
    @RequestMapping(value="edit", method=RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
    public String edit(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        
        if (meter instanceof RfnMeter) {
            model.addAttribute("meter", RfMeterModel.of((RfnMeter)meter));
            model.addAttribute("showRFMeshSettings", true);
        } else if (meter instanceof PlcMeter) {
            model.addAttribute("meter", PlcMeterModel.of((PlcMeter)meter));
            model.addAttribute("showCarrierSettings", true);
            if (meter.getPaoType().isRoutable()) {
                model.addAttribute("routable", true);
                LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
                model.addAttribute("routes", routes);
            }
        } else if (meter instanceof IedMeter) {
            model.addAttribute("meter", IEDMeterModel.of((IedMeter) meter));
            List<LiteYukonPAObject> ports = serverDatabaseCache.getAllPorts();
            model.addAttribute("ports", ports);
        } else {
            model.addAttribute("meter", MeterModel.of(meter));
        }
              
        return "meterInformationWidget/edit.jsp";
    }
    
    @RequestMapping(value="edit-plc", method=RequestMethod.PUT)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
    public String editPlc(HttpServletResponse resp, ModelMap model, FlashScope flash,
            @ModelAttribute("meter") PlcMeterModel meter, BindingResult result,  LiteYukonUser user) throws IOException {
        
        if (!rolePropertyDao.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.UPDATE, user)) {
            PlcMeter originalMeter = meterDao.getPlcMeterForId(meter.getDeviceId());
            meter.setMeterNumber(originalMeter.getMeterNumber());
            meter.setAddress(Integer.parseInt(originalMeter.getAddress()));
            meter.setName(originalMeter.getName());
        }
        meterValidator.validate(meter, result);
        
        if (result.hasErrors()) {
            LiteYukonPAObject pao = cache.getAllPaosMap().get(meter.getDeviceId());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("showCarrierSettings", true);
            if (pao.getPaoType().isRoutable()) {
                model.addAttribute("routable", true);
                LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
                model.addAttribute("routes", routes);
            }
            return "meterInformationWidget/edit.jsp";
        }
               
        LiteYukonPAObject pao = cache.getAllPaosMap().get(meter.getDeviceId());
        PlcMeter plc = new PlcMeter(pao.getPaoIdentifier(), meter.getMeterNumber(), meter.getName(), meter.isDisabled(), 
                null, meter.getRouteId(), Integer.toString(meter.getAddress()));
        
        meterDao.update(plc);
        
        meteringEventLogService.meterEdited(meter.getName(), meter.getMeterNumber(), user.getUsername());
        
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "update.successful", meter.getName()));
        
        return null;
    }
    
    @RequestMapping(value="edit-rf", method=RequestMethod.PUT)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
    public String editRf(HttpServletResponse resp, ModelMap model, FlashScope flash, YukonUserContext userContext,
            @ModelAttribute("meter") RfMeterModel meter, BindingResult result, LiteYukonUser user) throws IOException {
        
        if (!rolePropertyDao.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.UPDATE, user)) {
            RfnMeter originalMeter = meterDao.getRfnMeterForId(meter.getDeviceId());
            meter.setMeterNumber(originalMeter.getMeterNumber());
            meter.setManufacturer(originalMeter.getRfnIdentifier().getSensorManufacturer());
            meter.setModel(originalMeter.getRfnIdentifier().getSensorModel());
            meter.setSerialNumber(originalMeter.getRfnIdentifier().getSensorSerialNumber());
            meter.setName(originalMeter.getName());
        }
        meterValidator.validate(meter, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("showRFMeshSettings", true);
            return "meterInformationWidget/edit.jsp";
        }
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(meter.getDeviceId());
        RfnIdentifier rfnId = new RfnIdentifier(meter.getSerialNumber(), meter.getManufacturer(), meter.getModel());
        RfnMeter rfn = new RfnMeter(pao.getPaoIdentifier(), rfnId, 
                meter.getMeterNumber(), meter.getName(), meter.isDisabled());
        
        try {
            meterDao.update(rfn);
        } catch (DuplicateException e) {
            
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("showRFMeshSettings", true);
            String errorMsg = accessor.getMessage(baseKey + "error.rfn.address.duplicate");
            model.addAttribute("errorMsg", errorMsg);
            return "meterInformationWidget/edit.jsp";
        }
        
        meteringEventLogService.meterEdited(meter.getName(), meter.getMeterNumber(), user.getUsername());
        
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "update.successful", meter.getName()));
        
        return null;
    }
    
    @RequestMapping(value="edit-ied", method=RequestMethod.PUT)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
    public String editIed(HttpServletResponse resp, ModelMap model, FlashScope flash,
            @ModelAttribute("meter") IEDMeterModel meter, BindingResult result, LiteYukonUser user) throws IOException {
 
        if (!rolePropertyDao.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.UPDATE, user)) {
            YukonMeter originalMeter = meterDao.getForId(meter.getDeviceId());
            meter.setMeterNumber(originalMeter.getMeterNumber());
            meter.setName(originalMeter.getName());
        }
        meterValidator.validate(meter, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "meterInformationWidget/edit.jsp";
        }
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(meter.getDeviceId());
        IedMeter ied = new IedMeter(pao.getPaoIdentifier(), meter.getMeterNumber(), meter.getName(), meter.isDisabled());
        ied.setPortId(meter.getPortId());
        meterDao.update(ied);
        
        meteringEventLogService.meterEdited(meter.getName(), meter.getMeterNumber(), user.getUsername());
        
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "update.successful", meter.getName()));
        
        return null;
    }
}