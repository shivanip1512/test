package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display data collection information for devices
 */
@Controller
@RequestMapping("/dataCollectionWidget")
public class DataCollectionWidget extends AdvancedWidgetControllerBase {
    @Autowired DeviceGroupService deviceGroupService;
    
    @Autowired
    public DataCollectionWidget(@Qualifier("widgetInput.deviceGroup") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceGroup.jsp");
    }
        
    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) throws Exception {
        String deviceGroup = WidgetParameterHelper.getStringParameter(request, "deviceGroup");
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        String selectedNodeId = deviceGroupService.getNodeId(group);
        
        model.addAttribute("selectedNodeId", selectedNodeId);        
        model.addAttribute("deviceGroup", deviceGroup);
        
        Boolean includeDisabled = WidgetParameterHelper.getBooleanParameter(request, "includeDisabled", true);
        model.addAttribute("includeDisabled", includeDisabled);
        
        model.addAttribute("rangeTypes", RangeType.values());

        return "dataCollectionWidget/render.jsp";
    }

}
