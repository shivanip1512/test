package com.cannontech.web.multispeak.multispeakSync;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakEnrollmentSyncProgress;
import com.cannontech.multispeak.service.MultispeakSyncType;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.multispeak.service.impl.v5.MultispeakEnrollmentSyncService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
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
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;

    // HOME
    @RequestMapping("home")
    public String home(ModelMap modelMap) {
        MultispeakDeviceGroupSyncProgress progress = null;
        MultispeakEnrollmentSyncProgress enrollmentProgress = null;
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

        if (mspHandler.isEnrollmentSyncSupported()) {
            enrollmentProgress = mspHandler.getMultispeakEnrollmentSyncService().getProgress();
            // start page
            Instant lastEnrollmentSyncInstant = mspHandler.getMultispeakEnrollmentSyncService().getLastSyncInstants();
            lastSyncInstants.put(MultispeakSyncTypeProcessorType.ENROLLMENT, lastEnrollmentSyncInstant);
        } else {
            lastSyncInstants.put(MultispeakSyncTypeProcessorType.ENROLLMENT, null);
        }

        List<LastRunTimestampValue> lastRunTimestampValues = Lists.newArrayListWithCapacity(lastSyncInstants.size());
        for (Entry<MultispeakSyncTypeProcessorType, Instant> lastSyncInstantEntry : lastSyncInstants.entrySet()) {
            LastRunTimestampValue lastRunTimestampValue;
            if (MultispeakSyncTypeProcessorType.ENROLLMENT == lastSyncInstantEntry.getKey()) {
                lastRunTimestampValue = new LastRunTimestampValue(MultispeakSyncTypeProcessorType.ENROLLMENT,
                        lastSyncInstantEntry.getValue(), enrollmentProgress);
            } else {
                lastRunTimestampValue = new LastRunTimestampValue(lastSyncInstantEntry.getKey(),
                        lastSyncInstantEntry.getValue(), progress);
            }
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
        if (multispeakSyncType.equals(MultispeakSyncType.ENROLLMENT.name())) {
            // Start the synchronization for enrollment only if any vendor has support for NOT_Server(DR)
            if (mspHandler.isEnrollmentSyncSupported()) {
                mspHandler.startEnrollmentSync();
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
        MultispeakEnrollmentSyncProgress progress = null;
        MultispeakEnrollmentSyncService service = mspHandler.getMultispeakEnrollmentSyncService();

        if (service != null) {
            progress = service.getProgress();
        }

        if (progress == null) {
            return "redirect:home";
        }

        modelMap.addAttribute("progress", progress);

        modelMap.addAttribute("totalCount", lmHardwareControlGroupDao.getEnrollmentSyncMessagesToSend().size());
        modelMap.addAttribute("isEnrollmentSelected", true);

        return "setup/multispeakSync/progress.jsp";
    }
    
    // CANCEL
    @PostMapping("cancel")
    public String cancel(FlashScope flashScope, MultispeakSyncType multispeakSyncType) {
        if (multispeakSyncType == MultispeakSyncType.ENROLLMENT) {
            mspHandler.getMultispeakEnrollmentSyncService().getProgress().cancel();
            flashScope.setConfirm(new YukonMessageSourceResolvable(
                    "yukon.web.modules.adminSetup.multispeakSyncProgress.cancelOk"));
            return "redirect:enrollmentProgress";
        } else {
            mspHandler.getDeviceGroupSyncService().getProgress().cancel();
            flashScope.setConfirm(new YukonMessageSourceResolvable(
                    "yukon.web.modules.adminSetup.multispeakSyncProgress.cancelOk"));
            return "redirect:progress";
        }
    }

    // BACK TO HOME
    @RequestMapping(value = "done", params = "backToHome")
    public String done() {
        return "redirect:home";
    }
}
