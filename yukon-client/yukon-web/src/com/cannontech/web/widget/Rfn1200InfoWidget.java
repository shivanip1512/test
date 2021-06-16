package com.cannontech.web.widget;

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

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.model.Rfn1200Detail;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.stars.rfn1200.Rfn1200Validator;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/rfn1200InfoWidget")
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class Rfn1200InfoWidget extends AdvancedWidgetControllerBase {

    @Autowired private Rfn1200Validator rfn1200Validator;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private ServerDatabaseCache dbCache;
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
            retrieveRfn1200(userContext, request, deviceId, model);
        } catch (ServletRequestBindingException e) {
            log.error("Error rendering RFN-1200 Information widget", e);
        }
        return "rfn1200InfoWidget/render.jsp";
    }

    @GetMapping("/{id}/edit")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        retrieveRfn1200(userContext, request, id, model);
        return "rfn1200InfoWidget/render.jsp";
    }

    private void retrieveRfn1200(YukonUserContext userContext, HttpServletRequest request, int id, ModelMap model) {
        //TODO: Retrieve RFN 1200
        RfnDevice device = rfnDeviceDao.getDeviceForId(id);
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        Rfn1200Detail rfn1200 = new Rfn1200Detail();
        rfn1200.setId(device.getPaoIdentifier().getPaoId());
        rfn1200.setName(device.getName());
        rfn1200.setPaoType(pao.getPaoType());
        rfn1200.getRfnAddress().setRfnIdentifier(device.getRfnIdentifier());
        rfn1200.setEnabled(pao.getDisableFlag().equals(YNBoolean.NO.getDatabaseRepresentation()));
        model.addAttribute("rfn1200", rfn1200);
    }

    @PostMapping("/save")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public String save(@ModelAttribute("rfn1200") Rfn1200Detail rfn1200, BindingResult result, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request, ModelMap model, HttpServletResponse resp) {

        rfn1200Validator.validate(rfn1200, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "rfn1200InfoWidget/render.jsp";
        }

        //TODO: SAVE
        
        //if success
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", rfn1200.getName()));
        return null;
        //if error
        //flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", rfn1200.getName(), e.getMessage()));
        //return "rfn1200InfoWidget/render.jsp";
    }

}
