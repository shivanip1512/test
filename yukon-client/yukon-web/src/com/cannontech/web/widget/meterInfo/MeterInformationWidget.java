package com.cannontech.web.widget.meterInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.device.range.IntegerRange;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.widget.meterInfo.model.MeterModel;
import com.cannontech.web.widget.meterInfo.model.PlcMeterModel;
import com.cannontech.web.widget.meterInfo.model.RfMeterModel;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.collect.Lists;

/**
 * Widget used to display basic device information
 */
public class MeterInformationWidget extends AdvancedWidgetControllerBase {
    
    private final static String baseKey = "yukon.web.widgets.meterInformationWidget.";
    
    private final Validator plcValidator = new SimpleValidator<MeterModel>(MeterModel.class) {
        
        private final static String key = baseKey + "error.";
        
        @Override
        protected void doValidation(MeterModel meter, Errors errors) {
            
            LiteYukonPAObject pao = cache.getAllPaosMap().get(meter.getDeviceId());
            PaoType type = pao.getPaoType();
            IntegerRange range = addressRangeService.getEnforcedAddressRangeForDevice(type);
            
            // Device Name
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "deviceName.required");
            if (!errors.hasFieldErrors("name")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "name", meter.getName(), 60);
            }
            if (!errors.hasFieldErrors("name")) {
                LiteYukonPAObject unique = paoDao.findUnique(meter.getName(), type);
                if (unique.getPaoIdentifier().getPaoId() != pao.getPaoIdentifier().getPaoId()) {
                    errors.rejectValue("name", key + "deviceName.unique");
                }
            }
            
            // Meter Number
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "meterNumber", key + "meterNumber.required");
            
            // Validate PLC meter settings
            if (meter instanceof PlcMeterModel) {
                
                PlcMeterModel plc = (PlcMeterModel) meter;
                
                // Physical Address
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", key + "physicalAddress.required");
                if (!errors.hasFieldErrors("address")) {
                    if (!range.isWithinRange(plc.getAddress())) {
                        Object[] args = new Object[] { range.getLower(), range.getUpper() };
                        errors.rejectValue("address", key + "physicalAddress.range", args, null);
                    }
                }
            }
            
            // Validate RF meter settings
            if (meter instanceof RfMeterModel) {
                
                RfMeterModel rf = (RfMeterModel) meter;
                
            }
        }
    };
    
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private CommandRequestDeviceExecutor cre;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private DlcAddressRangeService addressRangeService;
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
        
        return "meterInformationWidget/render.jsp";
    }
    
    @RequestMapping("ping")
    public String ping(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        CommandResultHolder result = cre.execute(meter, "ping", DeviceRequestType.METER_INFORMATION_PING_COMMAND, user);
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
        }
        
        return "meterInformationWidget/edit.jsp";
    }
    
    @RequestMapping(value="edit-plc", method=RequestMethod.PUT)
    public String editPlc(HttpServletResponse resp, ModelMap model, FlashScope flash,
            @ModelAttribute("meter") PlcMeterModel meter, BindingResult result) throws IOException {
        
        plcValidator.validate(meter, result);
        
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
        
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".update.successful"));
        
        return null;
    }
    
    @RequestMapping(value="edit-rf", method=RequestMethod.PUT)
    public String editRf(ModelMap model, LiteYukonUser user, @ModelAttribute("meter") RfMeterModel meter) {
        
        return null;
    }
    
}