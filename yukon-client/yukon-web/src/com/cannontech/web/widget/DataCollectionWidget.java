package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display data collection information for devices
 */
@Controller
@RequestMapping("/dataCollectionWidget")
public class DataCollectionWidget extends AdvancedWidgetControllerBase {
    
    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) throws Exception {
        String deviceGroup = WidgetParameterHelper.getStringParameter(request, "deviceGroup");
        model.addAttribute("deviceGroup", deviceGroup);
        
        Boolean includeDisabled = WidgetParameterHelper.getBooleanParameter(request, "includeDisabled", true);
        model.addAttribute("includeDisabled", includeDisabled);

        return "dataCollectionWidget/render.jsp";
    }

}
