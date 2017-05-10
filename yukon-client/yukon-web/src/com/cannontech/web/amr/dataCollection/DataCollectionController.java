package com.cannontech.web.amr.dataCollection;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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

@Controller
@RequestMapping("/dataCollection/*")
public class DataCollectionController {
    
    @Autowired private DataCollectionWidgetService dataCollectionWidgetService;
    @Autowired private DeviceGroupService deviceGroupService;
    
    @RequestMapping(value="updateChart", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> updateChart(ModelMap model, String deviceGroup, Boolean includeDisabled, HttpServletResponse resp) throws Exception {
        Map<String, Object> json = new HashMap<>();
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        DataCollectionSummary summary = dataCollectionWidgetService.getDataCollectionSummary(group, true);
        json.put("summary",  summary);
        return json;
    }
    
    @RequestMapping("forceUpdate")
    public @ResponseBody Map<String, Object> forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        dataCollectionWidgetService.collectData();
        json.put("success", true);
        return json;
    }
}
