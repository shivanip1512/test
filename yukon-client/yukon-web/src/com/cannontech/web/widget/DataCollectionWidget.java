package com.cannontech.web.widget;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

/**
 * Widget used to display data collection information for devices
 */
@Controller
@RequestMapping("/dataCollectionWidget")
public class DataCollectionWidget extends AdvancedWidgetControllerBase {
    
    @RequestMapping("render")
    public String render(ModelMap model) throws Exception {
        return "dataCollectionWidget/render.jsp";
    }

}
