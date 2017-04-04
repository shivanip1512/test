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
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.multispeak.MspHandler;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
@RequestMapping("/setup/deviceGroupSync/*")
public class MultispeakDeviceGroupSyncController {
    
    @Autowired MultispeakDao multispeakDao;
    @Autowired MspHandler mspHandler;
    private MeterDao meterDao;
    private MultispeakFuncs multispeakFuncs;

	// HOME
    @RequestMapping("home")
    public String home(ModelMap modelMap) {
        
        MultispeakDeviceGroupSyncProgress progress = null;
        Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstants = Maps.newLinkedHashMap();

        MultispeakDeviceGroupSyncType[] deviceGroupSyncTypes = MultispeakDeviceGroupSyncType.values();
        modelMap.addAttribute("deviceGroupSyncTypes", deviceGroupSyncTypes);

        MultispeakVendor mspVendor = getMspVendor();
        if (mspVendor != null) {
            progress = mspHandler.getDeviceGroupSyncService(mspVendor).getProgress();
            // start page
            lastSyncInstants =
                    mspHandler.getDeviceGroupSyncService(mspVendor).getLastSyncInstants();
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

        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        MultispeakDeviceGroupSyncType multispeakDeviceGroupSyncType = MultispeakDeviceGroupSyncType.valueOf(deviceGroupSyncType);
         
        mspHandler.startDeviceGroupSync(mspVendor, multispeakDeviceGroupSyncType, userContext);

    
            flashScope.setConfirm(new YukonMessageSourceResolvable(
                "yukon.web.modules.adminSetup.deviceGroupSyncHome.startOk"));
        return "redirect:progress";
    }
	
	// PROGRESS
	@RequestMapping("progress")
    public String progress(ModelMap modelMap, FlashScope flashScope) {

        MultispeakDeviceGroupSyncProgress progress =
                mspHandler.getDeviceGroupSyncService(getMspVendor()).getProgress();

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

	    mspHandler.getDeviceGroupSyncService(getMspVendor()).getProgress().cancel();

        flashScope.setConfirm(new YukonMessageSourceResolvable(
            "yukon.web.modules.adminSetup.deviceGroupSyncProgress.cancelOk"));

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
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
		this.multispeakFuncs = multispeakFuncs;
	}
    
    private MultispeakVendor getMspVendor() {
        int vendorId = multispeakFuncs.getPrimaryCIS();
        if (vendorId <= 0) {
            return null;
        }
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        return mspVendor;
    }
}
