package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display infrastructure warnings
 */
@Controller
@RequestMapping("/infrastructureWarningsWidget")
public class InfrastructureWarningsWidget extends AdvancedWidgetControllerBase {
    private static final Logger log = YukonLogManager.getLogger(InfrastructureWarningsWidget.class);

    public InfrastructureWarningsWidget() {
        setSmartNotificationsEvent(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
    }

    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request) {
        String deviceType = null;
        try {
            deviceType = WidgetParameterHelper.getStringParameter(request, "deviceType");
        } catch (ServletRequestBindingException e) {
            log.error("Error rendering Infrastructure Warnings Widget", e);
        }
        model.addAttribute("deviceType", deviceType);
        return "infrastructureWarningsWidget/render.jsp";
    }

}
