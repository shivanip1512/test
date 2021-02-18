package com.cannontech.web.dev;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.service.CachedPointDataCorrelationService;

@Controller
public class CacheManagementController {
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CachedPointDataCorrelationService cachedPointDataCorrelationService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
  
    private static final Logger log = YukonLogManager.getLogger(CacheManagementController.class);
    
    @RequestMapping("cacheManagement")
    public String cacheManagement(ModelMap model) {
        String email = yukonSimulatorSettingsDao
                .getStringValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_NOTIFICATION_EMAIL);
        String hours = yukonSimulatorSettingsDao
                .getStringValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_FREQUENCY_HOURS);
        String groups = yukonSimulatorSettingsDao
                .getStringValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_GROUPS);
        model.addAttribute("email", email);
        model.addAttribute("hours", hours);
        model.addAttribute("groups", List.of(groups.split(",")));
        model.addAttribute("location", CtiUtilities.getCacheCollerationDirPath());
        return "cacheManagement.jsp";
    }
    
    @RequestMapping("clearCollectionActionCache")
    public String clearCache(FlashScope flash) {
        collectionActionService.clearCache();
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Collection Actions cache has been cleared."));
        return "redirect:cacheManagement";
    }
    
    @RequestMapping("terminateCollectionActions")
    public String terminateCollectionActions(FlashScope flash) {
        int result = collectionActionService.terminate();
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode(result + " Collection Actions cache has been terminated."));
        return "redirect:cacheManagement";
    }

    @RequestMapping("correlatePointData")
    public String correlatePointData(String[] deviceSubGroups, FlashScope flash,
            YukonUserContext userContext) {
        Set<? extends DeviceGroup> deviceGroups = null;
        try {
            deviceGroups = deviceGroupService.resolveGroupNames(List.of(deviceSubGroups));
            List<Integer> deviceIds = deviceGroupService.getDevices(deviceGroups).stream()
                    .map(device -> device.getDeviceId()).collect(Collectors.toList());
            boolean hasMismatches;
            hasMismatches = cachedPointDataCorrelationService.correlateAndLog(deviceIds, userContext);
            if (!hasMismatches) {
                flash.setConfirm(YukonMessageSourceResolvable
                        .createDefaultWithoutCode("Device data correlation complete. No mismatches found."));
            } else {
                flash.setConfirm(YukonMessageSourceResolvable
                        .createDefaultWithoutCode("Device data correlation complete. Mismatches found. File generated."));
            }
        } catch (Exception e) {
            log.error(e);
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(e.getMessage()));
        }
        return "redirect:cacheManagement";
    }
    
    @RequestMapping("scheduleCorrelationOfPointData")
    public String scheduleCorrelationOfPointData(String[] deviceSubGroups, String hours,
            String email, FlashScope flash,
            YukonUserContext userContext) {
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_NOTIFICATION_EMAIL, email);
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_FREQUENCY_HOURS, hours);
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.POINT_DATA_CACHE_CORRELATION_GROUPS,
                Arrays.stream(deviceSubGroups)
                        .collect(Collectors.joining(",")));
        cachedPointDataCorrelationService.reschedule(0);
        if (StringUtils.isEmpty(email)) {
            flash.setConfirm(
                    YukonMessageSourceResolvable.createDefaultWithoutCode("Point Data Cache Correlation Schedule is deleted."));
        } else {
            flash.setConfirm(
                    YukonMessageSourceResolvable.createDefaultWithoutCode("Point Data Cache Correlation Schedule is updated."));
        }
        return "redirect:cacheManagement";
    }
}
