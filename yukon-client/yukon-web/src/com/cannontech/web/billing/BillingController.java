package com.cannontech.web.billing;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.mainprograms.BillingBean;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.dynamicBilling.dao.DynamicBillingFileDao;
import com.cannontech.common.dynamicBilling.model.DynamicFormat;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

/**
 * CURRENT URL = /billing/*
 */
@Controller
@CheckRole(YukonRole.APPLICATION_BILLING)
public class BillingController {
    private static final Logger log = YukonLogManager.getLogger(BillingController.class);

    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DynamicBillingFileDao dynamicBillingFileDao;
    @Autowired private ScheduledFileExportService scheduledFileExportService;

    @RequestMapping(value="home", method=RequestMethod.GET)
    public String home(ModelMap modelMap) {

        log.debug("START BillingController.home(..)");

        // 1st tab: Generation
        BillingBean billingBean = new BillingBean();
        modelMap.addAttribute("billingBean", billingBean);
        modelMap.addAttribute("formatMap", FileFormatTypes.getValidFormats());
        modelMap.addAttribute("TYPE_CURTAILMENT_EVENTS_ITRON", FileFormatTypes.CURTAILMENT_EVENTS_ITRON);

        // 2nd tab: Setup
        List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();
        modelMap.addAttribute("allRows", allRows);

        // 3rd tab: Scheduling
        setupJobs(modelMap);

        return "billing.jsp";
    }

    @RequestMapping(value="schedules", method=RequestMethod.GET)
    public String schedules(ModelMap modelMap) {
        modelMap.addAttribute("showTabGeneration", false);
        modelMap.addAttribute("showTabSchedule", true);
        return home(modelMap);
    }

    @RequestMapping(value="_jobs", method=RequestMethod.GET)
    public String showJobs(ModelMap model) {
        setupJobs(model);
        return "../amr/scheduledBilling/_jobs.jsp";
    }

    private void setupJobs(ModelMap model) {
        
        List<ScheduledRepeatingJob> billingExportJobs =
                scheduledFileExportService.getJobsByType(ScheduledExportType.BILLING);
        List<ScheduledFileExportJobData> jobs = Lists.newArrayListWithCapacity(billingExportJobs.size());

        for(ScheduledRepeatingJob job : billingExportJobs) {
            jobs.add(scheduledFileExportService.getExportJobData(job));
        }
        Collections.sort(jobs);

        model.addAttribute("jobs", jobs);
        model.addAttribute("jobType", FileExportType.BILLING);
    }

}