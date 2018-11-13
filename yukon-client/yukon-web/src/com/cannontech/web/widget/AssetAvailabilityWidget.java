package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/assetAvailabilityWidget")
public class AssetAvailabilityWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    
    public AssetAvailabilityWidget() {
    }
    
    @Autowired
    public AssetAvailabilityWidget(@Qualifier("widgetInput.controlAreaOrProgramOrScenarioId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws Exception {
        boolean showAssetAvailability = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser());
        if (!showAssetAvailability) {
            throw new NotAuthorizedException("User is not autorized to view this page.");
        }
        Integer paoId = WidgetParameterHelper.getIntParameter(request, "controlAreaOrProgramOrScenarioId");
        model.addAttribute("controlAreaOrProgramOrScenarioId", paoId);
        model.addAttribute("statuses", AssetAvailabilityCombinedStatus.values());
        return "assetAvailabilityWidget/render.jsp";
    }

}
