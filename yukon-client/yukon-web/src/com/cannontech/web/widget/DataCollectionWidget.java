package com.cannontech.web.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display data collection information for devices
 */
@Controller
@RequestMapping("/dataCollectionWidget")
public class DataCollectionWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private DataCollectionWidgetService dataCollectionWidgetService;
    @Autowired private DeviceGroupService deviceGroupService;
    //user id/widget id
    private Map<Integer, Integer> includeDisabled = new ConcurrentHashMap<>();
    
    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) throws Exception {

        // force button
        //dataCollectionWidgetService.collectData();

        // called by data updater
        String deviceGroup = WidgetParameterHelper.getStringParameter(request, "deviceGroup");
        model.addAttribute("deviceGroup", deviceGroup);
        
        Boolean includeDisabled = WidgetParameterHelper.getBooleanParameter(request, "includeDisabled", true);
        model.addAttribute("includeDisabled", includeDisabled);

        //DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        // check includeDisabled
        //DataCollectionSummary summary = dataCollectionWidgetService.getDataCollectionSummary(group, true);
        //model.addAttribute("summary", mockedSummary());
        //dataCollectionWidgetService.getDataCollectionSummary(group, false);

        // returns values for the detail page
        //List<DeviceCollectionDetail> values = dataCollectionWidgetService.getDeviceCollectionResult(group, true, null);

        return "dataCollectionWidget/render.jsp";
    }
    
    @RequestMapping(value="updateChart", method=RequestMethod.GET)
    public @ResponseBody Object updateChart(ModelMap model, String deviceGroup, Boolean includeDisabled) throws Exception {
        Map<String, Object> json = new HashMap<>();

        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        DataCollectionSummary summary = dataCollectionWidgetService.getDataCollectionSummary(group, true);
        json.put("summary",  summary);

        return json;
    }
    
    @RequestMapping("forceUpdate")
    public @ResponseBody Object forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        dataCollectionWidgetService.collectData();
        json.put("success", true);
        return json;
    }
}
