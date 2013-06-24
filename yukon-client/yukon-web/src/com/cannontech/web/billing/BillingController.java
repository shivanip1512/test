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
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

/**
 * CURRENT URL = /operator/metering/billing
 */
@Controller
@CheckRole(YukonRole.APPLICATION_BILLING)
public class BillingController {
    private static int DEFAULT_COUNT_ITEMS_PER_PAGE = 25;
    private static int DEFAULT_PAGE_INDEX_ONE_BASED = 1;

    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DynamicBillingFileDao dynamicBillingFileDao;
    @Autowired private ScheduledFileExportService scheduledFileExportService;

    private static final Logger log = YukonLogManager.getLogger(BillingController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/metering/billing")
    public String billing(ModelMap modelMap, YukonUserContext userContext, HttpServletRequest request) throws ServletRequestBindingException {

        log.debug("START BillingController.billing(..)");

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

    @RequestMapping(method = RequestMethod.GET, value = "/metering/billing/_jobs.html")
    public String showJobs(ModelMap model, HttpServletRequest request) {
        
        int itemsPerPage = DEFAULT_COUNT_ITEMS_PER_PAGE;
        int pageNumber = DEFAULT_PAGE_INDEX_ONE_BASED;
        String perPage = request.getParameter("itemsPerPage");
        if (! StringUtils.isEmpty(perPage)) {
            itemsPerPage = Integer.parseInt(perPage);
        }
        String page = request.getParameter("page");
        if (! StringUtils.isEmpty(page)) {
            pageNumber = Integer.parseInt(page);
        }
        setupJobs(model, pageNumber, itemsPerPage);
        return "../amr/scheduledBilling/_jobs.jsp";
    }

    
    public void setupJobs(ModelMap model) {
        setupJobs(model, DEFAULT_PAGE_INDEX_ONE_BASED, DEFAULT_COUNT_ITEMS_PER_PAGE);
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

        int startIndex = (pageIndex -1) * rowsPerPage;

        for(ScheduledRepeatingJob job : billingExportJobs) {
            jobDataObjects.add(scheduledFileExportService.getExportJobData(job));
        }
        Collections.sort(jobDataObjects);
        int endIndex = startIndex + rowsPerPage > billingExportJobs.size() ? billingExportJobs.size() : startIndex + rowsPerPage;
        jobDataObjects = jobDataObjects.subList(startIndex, endIndex);

        SearchResult<ScheduledFileExportJobData> filterResult = new SearchResult<ScheduledFileExportJobData>();
        filterResult.setBounds(startIndex, rowsPerPage, billingExportJobs.size());
        filterResult.setResultList(jobDataObjects);
        model.addAttribute("filterResult", filterResult);
        model.addAttribute("jobType", FileExportType.BILLING);
    }

}