package com.cannontech.web.multispeak.multispeakSync;

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
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakSyncType;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.multispeak.MspHandler;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
@RequestMapping("/setup/multispeakSync/*")
public class MultispeakSyncController {

    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MeterDao meterDao;
    @Autowired private MspHandler mspHandler;
    @Autowired private MultispeakDao multispeakDao;

    // HOME
    @RequestMapping("home")
    public String home(ModelMap modelMap) {

        MultispeakDeviceGroupSyncProgress progress = null;
        Map<MultispeakSyncTypeProcessorType, Instant> lastSyncInstants = Maps.newLinkedHashMap();

        MultispeakSyncType[] multispeakSyncTypes = MultispeakSyncType.values();
        modelMap.addAttribute("multispeakSyncTypes", multispeakSyncTypes);
        int vendorId = multispeakFuncs.getPrimaryCIS();
        if (vendorId > 0) {
            progress = mspHandler.getDeviceGroupSyncService().getProgress();
            // start page
            lastSyncInstants = mspHandler.getDeviceGroupSyncService().getLastSyncInstants();
        } else {
            lastSyncInstants.put(MultispeakSyncTypeProcessorType.SUBSTATION, null);
            lastSyncInstants.put(MultispeakSyncTypeProcessorType.BILLING_CYCLE, null);
        }
        List<LastRunTimestampValue> lastRunTimestampValues = Lists.newArrayListWithCapacity(lastSyncInstants.size());
        for (Entry<MultispeakSyncTypeProcessorType, Instant> lastSyncInstantEntry : lastSyncInstants.entrySet()) {
            LastRunTimestampValue lastRunTimestampValue = new LastRunTimestampValue(lastSyncInstantEntry.getKey(),
                    lastSyncInstantEntry.getValue(), progress);
            lastRunTimestampValues.add(lastRunTimestampValue);
        }

        modelMap.addAttribute("lastRunTimestampValues", lastRunTimestampValues);
        return "setup/multispeakSync/home.jsp";
    }

    /**
     * This method will start the synchronization for selected multispeak synchronization type.
     * It will display error if the selected type is enrollment and no vendor has support for NOT_Server(DR).
     * If the selected type is either Substation or Billing or Substation and Billing Cycle and if no primary CIS vendor is
     * configured then it will display error.
     */
    @RequestMapping("start")
    public String start(ModelMap modelMap, FlashScope flashScope, String multispeakSyncType, YukonUserContext userContext) {

        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors(true);
        boolean isEnrollmentSyncSupportExists = allVendors.stream()
                                                          .anyMatch(multispeakVendor -> multispeakVendor.getMspInterfaces()
                                                                                                        .stream()
                                                                                                        .anyMatch(mspInterface -> mspInterface.getMspInterface()
                                                                                                                                              .equals(MultispeakDefines.NOT_Server_DR_STR)));

        if (multispeakSyncType.equals(MultispeakSyncType.ENROLLMENT.name())) {
            // Start the synchronization for enrollment only if any vendor has support for NOT_Server(DR)
            if (isEnrollmentSyncSupportExists) {
                // To Do - Start the multispeak synchronization for enrollment

                flashScope.setConfirm(new YukonMessageSourceResolvable(
                        "yukon.web.modules.adminSetup.multispeakSyncHome.startOk"));
                return "redirect:enrollmentProgress";
            } else {
                // Show error for enrollment as no vendor has support for NOT_Server(DR).
                flashScope.setError(
                        new YukonMessageSourceResolvable(
                                "yukon.web.modules.adminSetup.multispeakSyncHome.error.noNOTServerVendor"));
                return "redirect:home";
            }

        } else {
            // Start the synchronization for Substation/Billing/Substation and Billing Cycle

            int vendorId = multispeakFuncs.getPrimaryCIS();
            if (vendorId <= 0) {
                flashScope.setError(
                        new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.multispeakSyncHome.error.noCisVendorId"));
                return "redirect:home";
            }

            MultispeakSyncType multispeakSynchronizationType = MultispeakSyncType
                    .valueOf(multispeakSyncType);

            mspHandler.startDeviceGroupSync(multispeakSynchronizationType, userContext);

            flashScope.setConfirm(new YukonMessageSourceResolvable(
                    "yukon.web.modules.adminSetup.multispeakSyncHome.startOk"));
            return "redirect:progress";
        }

    }

    /**
     * This will show the synchronization progress for Substation/Billing/Substation and Billing Cycle.
     */
    @RequestMapping("progress")
    public String progress(ModelMap modelMap, FlashScope flashScope) {

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                "yukon.web.modules.adminSetup.multispeakSyncHome.startOk"));
        MultispeakDeviceGroupSyncProgress progress = null;
        MultispeakDeviceGroupSyncServiceBase service = mspHandler.getDeviceGroupSyncService();
        if (service != null) {
            progress = service.getProgress();
        }

        if (progress == null) {
            return "redirect:home";
        }

        modelMap.addAttribute("progress", progress);

        modelMap.addAttribute("totalCount", meterDao.getMeterCount());
        modelMap.addAttribute("isEnrollmentSelected", false);
        return "setup/multispeakSync/progress.jsp";
    }

    /**
     * This will show the synchronization progress for enrollment.
     */
    @RequestMapping("enrollmentProgress")
    public String enrollmentProgress(ModelMap modelMap, FlashScope flashScope) {

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                "yukon.web.modules.adminSetup.multispeakSyncHome.startOk"));
        MultispeakDeviceGroupSyncProgress progress = new MultispeakDeviceGroupSyncProgress(MultispeakSyncType.ENROLLMENT);
        // To Do service call 

        // This is not a dead code. Will be used once service call is ready.
        if (progress == null) {
            return "redirect:home";
        }

        modelMap.addAttribute("progress", progress);

        // To Do - Set the total enrollment message count
        modelMap.addAttribute("totalCount", 100);
        modelMap.addAttribute("isEnrollmentSelected", true);

        return "setup/multispeakSync/progress.jsp";
    }
    
    // CANCEL
    @RequestMapping(value = "done", params = "cancel")
    public String done(FlashScope flashScope) {
        if (MultispeakSyncType.ENROLLMENT.equals(mspHandler.getDeviceGroupSyncService().getProgress().getType())) {
            // To do cancel the enrollment 
        } else {
            mspHandler.getDeviceGroupSyncService().getProgress().cancel();
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                "yukon.web.modules.adminSetup.multispeakSyncProgress.cancelOk"));

        return "redirect:progress";
    }

    // BACK TO HOME
    @RequestMapping(value = "done", params = "backToHome")
    public String done() {

        return "redirect:home";
    }
}
