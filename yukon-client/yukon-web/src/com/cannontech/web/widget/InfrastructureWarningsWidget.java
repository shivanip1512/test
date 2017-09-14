package com.cannontech.web.widget;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

/**
 * Widget used to display infrastructure warnings
 */
@Controller
@RequestMapping("/infrastructureWarningsWidget")
public class InfrastructureWarningsWidget extends AdvancedWidgetControllerBase {
    
    public InfrastructureWarningsWidget() {
        setSmartNotificationsEvent(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
    }
            
    @RequestMapping("render")
    public String render(ModelMap model) {
        return "infrastructureWarningsWidget/render.jsp";
    }

}
