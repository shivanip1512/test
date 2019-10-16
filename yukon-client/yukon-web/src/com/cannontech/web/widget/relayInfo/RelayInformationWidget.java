package com.cannontech.web.widget.relayInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Widget used to display basic device information
 */
@Controller
@RequestMapping("/relayInformationWidget/*")
public class RelayInformationWidget extends AdvancedWidgetControllerBase {
    
    private final static String baseKey = "yukon.web.widgets.relayInformationWidget.";
    
    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @Autowired
    public RelayInformationWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }
    
    private final Validator validator = new SimpleValidator<RfnRelay>(RfnRelay.class) {
        
        private final static String key = baseKey + "error.";
        
        @Override
        protected void doValidation(RfnRelay relay, Errors errors) {
            
            LiteYukonPAObject pao = cache.getAllPaosMap().get(relay.getDeviceId());
            PaoType type = pao.getPaoType();
            
            // Device Name
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "deviceName.required");
            if (!errors.hasFieldErrors("name")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "name", relay.getName(), 60);
            }
            if (!errors.hasFieldErrors("name")) {
                LiteYukonPAObject unique = paoDao.findUnique(relay.getName(), type);
                if (unique != null) {
                    if (unique.getPaoIdentifier().getPaoId() != pao.getPaoIdentifier().getPaoId()) {
                        errors.rejectValue("name", key + "deviceName.unique");
                    }
                }
            }
            if (!errors.hasFieldErrors("name")) {
                if (!PaoUtils.isValidPaoName(relay.getName())) {
                    errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
                }
            }
            
            // Validate RF meter settings
                RfnIdentifier rfnId = new RfnIdentifier(relay.getSerialNumber(), relay.getManufacturer(), relay.getModel());
                if (!rfnId.isBlank()) {
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "serialNumber", key + "serialNumber.required");
                    YukonValidationUtils.checkExceedsMaxLength(errors, "serialNumber", relay.getSerialNumber(), 30);
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "manufacturer", key + "manufacturer.required");
                    YukonValidationUtils.checkExceedsMaxLength(errors, "manufacturer", relay.getManufacturer(), 60);
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "model", key + "model.required");
                    YukonValidationUtils.checkExceedsMaxLength(errors, "model", relay.getModel(), 60);
                }
        }
    };

    @RequestMapping("render")
    public String render(ModelMap model, int deviceId) {
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("relay", device);
        
        return "relayInformationWidget/render.jsp";
    }
    
    @RequestMapping(value="edit", method=RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
    public String edit(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("relay", RfnRelay.of(device));
              
        return "relayInformationWidget/edit.jsp";
    }
    
    @RequestMapping(value="edit", method=RequestMethod.PUT)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String edit(HttpServletResponse resp, ModelMap model, FlashScope flash, YukonUserContext userContext,
            @ModelAttribute("relay") RfnRelay relay, BindingResult result, LiteYukonUser user) throws IOException {

        validator.validate(relay, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "relayInformationWidget/edit.jsp";
        }
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(relay.getDeviceId());
        RfnIdentifier rfnId = new RfnIdentifier(relay.getSerialNumber(), relay.getManufacturer(), relay.getModel());
        RfnDevice updatedDevice = new RfnDevice(relay.getName(), device.getPaoIdentifier(), rfnId);
        
        try {
            deviceDao.changeName(updatedDevice, relay.getName());
            rfnDeviceDao.updateDevice(updatedDevice);
        } catch (DataIntegrityViolationException e) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            String errorMsg = accessor.getMessage(baseKey + "error.rfn.address.duplicate");
            model.addAttribute("errorMsg", errorMsg);
            return "relayInformationWidget/edit.jsp";
        }
        
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "update.successful", relay.getName()));
        
        return null;
    }
}