package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.InSync;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastAction;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastActionStatus;

@Controller
@RequestMapping("/summary/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationSummaryController {
    
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceConfigSummaryDao deviceConfigSummaryDao;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
    private final static String baseKey = "yukon.web.modules.tools.configs.summary.";

    @RequestMapping("view")
    public String view(ModelMap model, @ModelAttribute DeviceConfigSummaryFilter filter, String[] deviceSubGroups) {
        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();
        model.addAttribute("configurations", configurations);
        List<DeviceGroup> subGroups = new ArrayList<>();
        if (deviceSubGroups != null) {
            for (String subGroup : deviceSubGroups) {
                subGroups.add(deviceGroupService.resolveGroupName(subGroup));
            }
        }
        if (filter.getConfigurationIds() != null) {
            //Include Unassigned
            if (filter.getConfigurationIds().contains(-999)) {
                filter.setDisplayUnassigned(true);
            }
            //Include any assigned
            if (filter.getConfigurationIds().contains(-998)) {
                List<Integer> allIds = new ArrayList<>();
                configurations.forEach(config -> allIds.add(config.getConfigurationId()));
                filter.setConfigurationIds(allIds);
            }
        }
        filter.setGroups(subGroups);
        model.addAttribute("filter", filter);
        model.addAttribute("lastActionOptions", LastAction.values());
        model.addAttribute("statusOptions", LastActionStatus.values());
        model.addAttribute("syncOptions", InSync.values());
        model.addAttribute("deviceSubGroups", deviceSubGroups);
        getData(model, filter);
        return "summary/summary.jsp";
    }
    
    @RequestMapping("{id}/viewHistory")
    public String viewHistory(ModelMap model, @PathVariable int id) {
        model.addAttribute("details", deviceConfigSummaryDao.getDeviceConfigActionHistory(id));
        return "summary/history.jsp";
    }
    
    @RequestMapping("{id}/outOfSync")
    public String outOfSync(ModelMap model, @PathVariable int id) {
        return "summary/outOfSync.jsp";
    }
       
    @RequestMapping("{id}/sendConfig")
    public void sendConfig(ModelMap model, @PathVariable int id, FlashScope flash, HttpServletResponse resp) {
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "sendConfig.success"));
        resp.setStatus(HttpStatus.NO_CONTENT.value());    
    }
    
    @RequestMapping("{id}/readConfig")
    public void readConfig(ModelMap model, @PathVariable int id, FlashScope flash, HttpServletResponse resp) {
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "readConfig.success"));
        resp.setStatus(HttpStatus.NO_CONTENT.value()); 
    }
    
    @RequestMapping("{id}/verifyConfig")
    public void verifyConfig(ModelMap model, @PathVariable int id, FlashScope flash, HttpServletResponse resp) {
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "verifyConfig.success"));
        resp.setStatus(HttpStatus.NO_CONTENT.value()); 
    }
    
    private void getData(ModelMap model, DeviceConfigSummaryFilter filter) {
        
        SearchResults<DeviceConfigSummaryDetail> results = deviceConfigSummaryDao.getSummary(filter,
            PagingParameters.EVERYTHING, DeviceConfigSummaryDao.SortBy.DEVICE_NAME, Direction.asc);
        
        List<SimpleDevice> devices = results.getResultList().stream().map(d -> new SimpleDevice(d.getDevice())).collect(Collectors.toList());
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup,  devices);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
        model.addAttribute("deviceCollection", deviceCollection);
        
        model.addAttribute("results",  results.getResultList());
    }
    
}
