package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/assetAvailabilityWidget")
@CheckRoleProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY)
public class AssetAvailabilityWidget extends AdvancedWidgetControllerBase {
    
    public AssetAvailabilityWidget() {
    }
    
    @Autowired
    public AssetAvailabilityWidget(@Qualifier("widgetInput.controlAreaOrLMProgramOrScenarioId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
    }

    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request) throws Exception {
        Integer paoId = WidgetParameterHelper.getIntParameter(request, "controlAreaOrLMProgramOrScenarioId");
        model.addAttribute("controlAreaOrLMProgramOrScenarioId", paoId);
        model.addAttribute("statuses", AssetAvailabilityCombinedStatus.values());
        return "assetAvailabilityWidget/render.jsp";
    }

}
