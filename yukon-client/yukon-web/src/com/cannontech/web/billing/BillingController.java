package com.cannontech.web.billing;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
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
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.user.YukonUserContext;
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
    private static int DEFAULT_PAGE_INDEX_ONE_BASED = 1;

    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DynamicBillingFileDao dynamicBillingFileDao;
    @Autowired private ScheduledFileExportService scheduledFileExportService;

    private static final Logger log = YukonLogManager.getLogger(BillingController.class);

    @RequestMapping(method = RequestMethod.GET, value = "home")
    public String home(ModelMap modelMap, YukonUserContext userContext, HttpServletRequest request) throws ServletRequestBindingException {

        log.debug("START BillingController.home(..)");

        // 1st tab: Generation
        modelMap.addAttribute("billingBean", new BillingBean());
        modelMap.addAttribute("formatMap", FileFormatTypes.getValidFormats());
        modelMap.addAttribute("TYPE_CURTAILMENT_EVENTS_ITRON", FileFormatTypes.CURTAILMENT_EVENTS_ITRON);

        // 2nd tab: Setup
        List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();
        modelMap.addAttribute("allRows", allRows);

        // 3rd tab: Scheduling
        setupJobs(modelMap);

        return "billing.jsp";
    }

    @RequestMapping(method = RequestMethod.GET, value = "schedules")
    public String schedules(ModelMap modelMap, YukonUserContext userContext, HttpServletRequest request) throws ServletRequestBindingException {
        String to = home(modelMap, userContext, request);
        modelMap.addAttribute("showTabGeneration", false);
        modelMap.addAttribute("showTabSchedule", true);
        return to;
    }

    @RequestMapping(method = RequestMethod.GET, value = "_jobs")
    public String showJobs(ModelMap model, Integer itemsPerPage, Integer page) {

        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
        if (page == null){
            page = DEFAULT_PAGE_INDEX_ONE_BASED;
        }
        setupJobs(model, page, itemsPerPage);
        return "../amr/scheduledBilling/_jobs.jsp";
    }

    
    public void setupJobs(ModelMap model) {
        setupJobs(model, DEFAULT_PAGE_INDEX_ONE_BASED, CtiUtilities.DEFAULT_ITEMS_PER_PAGE);
    }

    /**
     * 
     * @param model
     * @param pageIndex     1-based integer (eg. 1 = first page)
     * @param rowsPerPage
     */
    protected void setupJobs(ModelMap model, int pageIndex, int rowsPerPage) {
        List<ScheduledRepeatingJob> billingExportJobs =
                scheduledFileExportService.getJobsByType(ScheduledExportType.BILLING);
        List<ScheduledFileExportJobData> jobDataObjects = Lists.newArrayListWithCapacity(billingExportJobs.size());

        for(ScheduledRepeatingJob job : billingExportJobs) {
            jobDataObjects.add(scheduledFileExportService.getExportJobData(job));
        }
        Collections.sort(jobDataObjects);

        SearchResults<ScheduledFileExportJobData> filterResult =
                SearchResults.pageBasedForWholeList(pageIndex, rowsPerPage, jobDataObjects);
        model.addAttribute("filterResult", filterResult);
        model.addAttribute("jobType", FileExportType.BILLING);
    }

}