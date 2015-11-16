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
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncService;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
@RequestMapping("/setup/deviceGroupSync/*")
public class MultispeakDeviceGroupSyncController {

	private MeterDao meterDao;
	private MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService;
	private MultispeakFuncs multispeakFuncs;
	
	// HOME
	@RequestMapping("home")
    public String home(ModelMap modelMap) {
        
		MultispeakDeviceGroupSyncProgress progress = multispeakDeviceGroupSyncService.getProgress();
		
		// start page
        MultispeakDeviceGroupSyncType[] deviceGroupSyncTypes = MultispeakDeviceGroupSyncType.values();
        modelMap.addAttribute("deviceGroupSyncTypes", deviceGroupSyncTypes);
        
        Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstants = multispeakDeviceGroupSyncService.getLastSyncInstants();
        
        List<LastRunTimestampValue> lastRunTimestampValues = Lists.newArrayListWithCapacity(lastSyncInstants.size());
        for (Entry<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstantEntry : lastSyncInstants.entrySet()) {
        	
        	LastRunTimestampValue lastRunTimestampValue = new LastRunTimestampValue(lastSyncInstantEntry.getKey(), lastSyncInstantEntry.getValue(), progress);
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
		multispeakDeviceGroupSyncService.startSyncForType(multispeakDeviceGroupSyncType, userContext);
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.deviceGroupSyncHome.startOk"));
        return "redirect:progress";
    }
	
	// PROGRESS
	@RequestMapping("progress")
    public String progress(ModelMap modelMap, FlashScope flashScope) {
		
		MultispeakDeviceGroupSyncProgress progress = multispeakDeviceGroupSyncService.getProgress();
		
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
        
		multispeakDeviceGroupSyncService.getProgress().cancel();
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.deviceGroupSyncProgress.cancelOk"));
		
        return "redirect:progress";
    }
	
	// BACK TO HOME
	@RequestMapping(value="done", params="backToHome")
    public String done() {
		
        return "redirect:home";
    }
	
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
    
    @Autowired
    public void setMultispeakDeviceGroupSyncService(MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService) {
		this.multispeakDeviceGroupSyncService = multispeakDeviceGroupSyncService;
	}
    
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
		this.multispeakFuncs = multispeakFuncs;
	}
}
