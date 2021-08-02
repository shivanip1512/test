package com.cannontech.web.widget;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.model.Rfn1200Detail;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.stars.commChannel.CommChannelSetupHelper;
import com.cannontech.web.stars.rfn1200.Rfn1200Validator;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/rfn1200InfoWidget")
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class Rfn1200InfoWidget extends AdvancedWidgetControllerBase {

    @Autowired private Rfn1200Validator rfn1200Validator;
    @Autowired private RfDaCreationService rfdaCreationService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CommChannelSetupHelper commChanelSetupHelper;

    private static final Logger log = YukonLogManager.getLogger(Rfn1200InfoWidget.class);

    @Autowired
    public Rfn1200InfoWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        int deviceId = 0;
        try {
            deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
            model.addAttribute("mode", PageEditMode.VIEW);
            retrieveRfn1200(userContext, deviceId, model);
        } catch (ServletRequestBindingException e) {
            log.error("Error rendering RFN-1200 Information widget", e);
        }
        return "rfn1200InfoWidget/render.jsp";
    }

    @GetMapping("/{id}/edit")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.EDIT);
        retrieveRfn1200(userContext, id, model);
        return "rfn1200InfoWidget/render.jsp";
    }

    private void retrieveRfn1200(YukonUserContext userContext, int id, ModelMap model) {
        try {
            Rfn1200Detail rfn1200 = rfdaCreationService.retrieve(id);
            model.addAttribute("rfn1200", rfn1200);
        } catch (Exception e) {
            log.error("Unable to retrieve RFN-1200 with id " + id, e);
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String commChannelDeviceLabel = accessor.getMessage("yukon.common.pao.RFN_1200");
            String errorMsg = accessor.getMessage("yukon.web.api.retrieve.error", commChannelDeviceLabel, e.getMessage());
            model.addAttribute("errorMsg", errorMsg);
        }
    }

    @PostMapping("/save")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public String save(@ModelAttribute("rfn1200") Rfn1200Detail rfn1200, BindingResult result, FlashScope flash, ModelMap model, HttpServletResponse resp, YukonUserContext userContext) {
        rfn1200Validator.validate(rfn1200, result);
        if (result.hasErrors()) {
            model.addAttribute("mode", rfn1200.getId() == null ? PageEditMode.CREATE : PageEditMode.EDIT);
            model.addAttribute("webSupportedCommChannelTypes", commChanelSetupHelper.getWebSupportedCommChannelTypes());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "rfn1200InfoWidget/render.jsp";
        }

        try {
            if (rfn1200.getId() == null) {
                rfn1200 = rfdaCreationService.create(rfn1200, userContext.getYukonUser());
            } else {
                rfn1200 = rfdaCreationService.update(rfn1200, userContext.getYukonUser());
            }
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", rfn1200.getName()));
            Map<String, Object> json = new HashMap<>();
            json.put("id", rfn1200.getId());
            resp.setContentType("application/json");
            JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
            return null;
        } catch (Exception e) {
            log.error("Unable to create RFN-1200 " + rfn1200.getName(), e);
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", rfn1200.getName(), e.getMessage()));
            return "rfn1200InfoWidget/render.jsp";
        }
    }

}
