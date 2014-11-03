package com.cannontech.web.widget.meterInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.widget.meterInfo.model.PlcMeterModel;
import com.cannontech.web.widget.meterInfo.model.RfMeterModel;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Widget used to display basic device information
 */
public class MeterInformationWidget extends AdvancedWidgetControllerBase {
    
    private final static String baseKey = "yukon.web.widgets.meterInformationWidget.";
    
    private final Validator validator = new SimpleValidator<PlcMeter>(PlcMeter.class) {
        
        @Override
        protected void doValidation(PlcMeter settings, Errors errors) {
            
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", baseKey + "name.required");
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", settings.getName(), 60);
            
//            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "ipAddress", baseKey + "ipAddress.required");
//            if (StringUtils.isNoneBlank(settings.getIpAddress())) {
//                boolean ipValid = InetAddressValidator.getInstance().isValid(settings.getIpAddress());
//                if (!ipValid) {
//                    errors.rejectValue("ipAddress", baseKey + "ipAddress.invalid");
//                }
//            }
//            
//            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "admin.username", baseKey + "username.required");
//            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "superAdmin.username", baseKey + "username.required");
        }
        
    };
    
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private CommandRequestDeviceExecutor cre;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

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
    
    @RequestMapping("edit-plc")
    public String editPlc(HttpServletResponse resp, ModelMap model, FlashScope flash,
            @ModelAttribute("meter") PlcMeter meter, BindingResult result) throws IOException {
        
        validator.validate(meter, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("showCarrierSettings", true);
            if (meter.getPaoType().isRoutable()) {
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
    
    @RequestMapping(value="edit-rf", method=RequestMethod.POST)
    public String editRf(ModelMap model, LiteYukonUser user, @ModelAttribute("meter") RfnMeter meter) {
        
        return null;
    }
    
}