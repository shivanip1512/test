package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.web.security.annotation.CheckRoleProperty;
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
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper; 
    @Autowired private ServerDatabaseCache dbcache;

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
        mockupData(model);
        return "summary.jsp";
    }
    
    private void mockupData(ModelMap model) {
        List<DeviceConfigSummaryDetail> results = new ArrayList<>();
        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();
        LightDeviceConfiguration testConfig = configurations.get(0);
        DeviceGroup group = deviceGroupService.resolveGroupName("/System/Device Configs/" + testConfig.getName());
        DeviceCollection collection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        collection.getDeviceList().forEach(device -> {
            DeviceConfigSummaryDetail detail = new DeviceConfigSummaryDetail();
            detail.setDeviceConfig(testConfig);
            LiteYukonPAObject liteYukonPao = dbcache.getAllPaosMap().get(device.getDeviceId());
            detail.setDevice(new DisplayableDevice(device.getPaoIdentifier(), liteYukonPao.getPaoName()));
            detail.setAction(LastAction.VERIFY);
            detail.setActionEnd(new Instant());
            detail.setActionStart(new Instant());
            detail.setInSync(InSync.IN_SYNC);
            detail.setStatus(LastActionStatus.FAILURE);
            results.add(detail);
        });
        model.addAttribute("results", results);
    }
    
}
