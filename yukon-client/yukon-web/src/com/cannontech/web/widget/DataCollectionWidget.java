package com.cannontech.web.widget;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

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
    public String render(ModelMap model) throws Exception {

        // force button
        dataCollectionWidgetService.collectData();

        // called by data updater
        DeviceGroup group = deviceGroupService.resolveGroupName("/Test");
        // check includeDisabled
        dataCollectionWidgetService.getDataCollectionSummary(group, true);
        dataCollectionWidgetService.getDataCollectionSummary(group, false);

        // returns values for the detail page
        List<DeviceCollectionDetail> values = dataCollectionWidgetService.getDeviceCollectionResult(group, true, null);

        return "dataCollectionWidget/render.jsp";
    }

}
