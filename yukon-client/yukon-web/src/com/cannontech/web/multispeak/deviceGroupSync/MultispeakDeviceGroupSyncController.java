package com.cannontech.web.multispeak.deviceGroupSync;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.multispeak.MspHandler;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
@RequestMapping("/setup/deviceGroupSync/*")
public class MultispeakDeviceGroupSyncController {
    
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MeterDao meterDao;
    @Autowired private MspHandler mspHandler;

	// HOME
    @RequestMapping("home")
    public String home(ModelMap modelMap) {
        
        MultispeakDeviceGroupSyncProgress progress = null;
        Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstants = Maps.newLinkedHashMap();

        MultispeakDeviceGroupSyncType[] deviceGroupSyncTypes = MultispeakDeviceGroupSyncType.values();
        modelMap.addAttribute("deviceGroupSyncTypes", deviceGroupSyncTypes);
        int vendorId = multispeakFuncs.getPrimaryCIS();
        if (vendorId > 0) {
            progress = mspHandler.getDeviceGroupSyncService().getProgress();
            // start page
            lastSyncInstants = mspHandler.getDeviceGroupSyncService().getLastSyncInstants();
        } else {
            lastSyncInstants.put(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION, null);
            lastSyncInstants.put(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE, null);
        }
        List<LastRunTimestampValue> lastRunTimestampValues = Lists.newArrayListWithCapacity(lastSyncInstants.size());
        for (Entry<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstantEntry : lastSyncInstants.entrySet()) {
            LastRunTimestampValue lastRunTimestampValue =
                new LastRunTimestampValue(lastSyncInstantEntry.getKey(), lastSyncInstantEntry.getValue(), progress);
            lastRunTimestampValues.add(lastRunTimestampValue);
        }

        modelMap.addAttribute("lastRunTimestampValues", lastRunTimestampValues);
        return "setup/deviceGroupSync/home.jsp";
    }
	
	// START
	@RequestMapping("start")
    public String start(ModelMap modelMap, FlashScope flashScope, String deviceGroupSyncType, YukonUserContext userContext) {
        
	    int vendorId = multispeakFuncs.getPrimaryCIS();
        if (vendorId <= 0) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.deviceGroupSyncHome.error.noCisVendorId"));
            return "redirect:home";
        }

        MultispeakDeviceGroupSyncType multispeakDeviceGroupSyncType = MultispeakDeviceGroupSyncType.valueOf(deviceGroupSyncType);
         
        mspHandler.startDeviceGroupSync(multispeakDeviceGroupSyncType, userContext);

    
            flashScope.setConfirm(new YukonMessageSourceResolvable(
                "yukon.web.modules.adminSetup.deviceGroupSyncHome.startOk"));
        return "redirect:progress";
    }
	
	// PROGRESS
	@RequestMapping("progress")
    public String progress(ModelMap modelMap, FlashScope flashScope) {

        MultispeakDeviceGroupSyncProgress progress = null;
        MultispeakDeviceGroupSyncServiceBase service = mspHandler.getDeviceGroupSyncService();
        if (service != null) {
            progress = service.getProgress();
        }

        if (progress == null) {
            return "redirect:home";
        }

        modelMap.addAttribute("progress", progress);

        int meterCount = meterDao.getMeterCount();
        modelMap.addAttribute("meterCount", meterCount);

        return "setup/deviceGroupSync/progress.jsp";
    }
	
	// CANCEL
	@RequestMapping(value="done", params="cancel")
    public String done(FlashScope flashScope) {

	    mspHandler.getDeviceGroupSyncService().getProgress().cancel();

        flashScope.setConfirm(new YukonMessageSourceResolvable(
            "yukon.web.modules.adminSetup.deviceGroupSyncProgress.cancelOk"));

        return "redirect:progress";
    }
	
	// BACK TO HOME
	@RequestMapping(value="done", params="backToHome")
    public String done() {
		
        return "redirect:home";
    }
}
