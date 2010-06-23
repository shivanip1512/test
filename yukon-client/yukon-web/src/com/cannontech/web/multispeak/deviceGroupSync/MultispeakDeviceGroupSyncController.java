package com.cannontech.web.multispeak.deviceGroupSync;

import java.util.Map;

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

@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
@RequestMapping("/setup/deviceGroupSync/*")
public class MultispeakDeviceGroupSyncController {

	private MeterDao meterDao;
	private MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService;
	private MultispeakFuncs multispeakFuncs;
	
	// HOME
	@RequestMapping
    public String home(ModelMap modelMap) {
        
		// sync in progress go to progress page
		if (multispeakDeviceGroupSyncService.isProgressAvailable()) {
			return "redirect:progress";
		}
		
		// start page
        MultispeakDeviceGroupSyncType[] deviceGroupSyncTypes = MultispeakDeviceGroupSyncType.values();
        modelMap.addAttribute("deviceGroupSyncTypes", deviceGroupSyncTypes);
        
        Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstants = multispeakDeviceGroupSyncService.getLastSyncInstants();
        modelMap.addAttribute("lastSyncInstants", lastSyncInstants);
        
        return "setup/deviceGroupSync/home.jsp";
    }
	
	// START
	@RequestMapping
    public String start(ModelMap modelMap, FlashScope flashScope, String deviceGroupSyncType, YukonUserContext userContext) {
        
		int vendorId = multispeakFuncs.getPrimaryCIS();
		if (vendorId <= 0) {
			flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.deviceGroupSyncHome.error.noCisVendorId"));
			return "redirect:home";
		}
		
		try {
			MultispeakDeviceGroupSyncType multispeakDeviceGroupSyncType = MultispeakDeviceGroupSyncType.valueOf(deviceGroupSyncType);
			multispeakDeviceGroupSyncService.startSyncForType(multispeakDeviceGroupSyncType, userContext);
		} catch (IllegalArgumentException e) {
			flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.deviceGroupSyncHome.error.invalidSyncType"));
			return "redirect:home";
		}
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.deviceGroupSyncHome.startOk"));
        return "redirect:progress";
    }
	
	// PROGRESS
	@RequestMapping
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
	@RequestMapping
    public String cancel(ModelMap modelMap, FlashScope flashScope) {
        
		multispeakDeviceGroupSyncService.getProgress().cancel();
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.multispeak.deviceGroupSyncProgress.cancelOk"));
		
        return "redirect:progress";
    }
	
	// DONE
	@RequestMapping
    public String done(ModelMap modelMap, FlashScope flashScope) {
        
		multispeakDeviceGroupSyncService.reset();
		
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
