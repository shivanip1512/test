package com.cannontech.web.dr.nest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;

import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.dao.NestDao.SortBy;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.model.NestSyncType;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.JobManagerException;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.dr.model.NestSyncSettings;
import com.google.common.collect.Iterables;

@Controller
public class NestController {
    
    private static DateTimeFormatter dateTimeFormatter;
    private static String baseKey = "yukon.web.modules.dr.nest.";
    
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private JobManager jobManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private NestSyncService nestSyncService;
    @Autowired private NestDao nestDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @Autowired @Qualifier("nestScheduledSync")
    private YukonJobDefinition<NestSyncTask> nestSyncJobDef;

    @PostConstruct
    public void init() {
        String defaultCron = "0 0 0 * * ?";// every day at 12am
        try {
            List<ScheduledRepeatingJob> nestSyncJobs =
                    jobManager.getNotDeletedRepeatingJobsByDefinition(nestSyncJobDef);
            if (CollectionUtils.isEmpty(nestSyncJobs)) {
                ScheduledRepeatingJob job = new ScheduledRepeatingJob();
                job.setBeanName(nestSyncJobDef.getName());
                job.setCronString(defaultCron);
                job.setDisabled(true);
                job.setUserContext(null);
                job.setJobGroupId(nextValueHelper.getNextValue("Job"));
                job.setJobDefinition(nestSyncJobDef);
                job.setJobProperties(Collections.<String, String>emptyMap());
                scheduledRepeatingJobDao.save(job);
                jobManager.instantiateTask(job);
            }
        } catch (JobManagerException e) {
            
        }

        dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, YukonUserContext.system);

    }
    
    @RequestMapping(value="/nest", method=RequestMethod.GET)
    public String details(ModelMap model, YukonUserContext userContext, @DefaultItemsPerPage(value=250) PagingParameters paging, 
                          @DefaultSort(dir=Direction.desc, sort="type") SortingParameters sorting) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        ScheduledRepeatingJob nestSyncJob = getJob(nestSyncJobDef);
        
        NestSyncSettings nestSyncSettings = new NestSyncSettings();
        nestSyncSettings.setSync(!nestSyncJob.isDisabled());
        
        try {
            Date now = new Date();
            Date nextScheduledSync = jobManager.getNextRuntime(nestSyncJob, now);
            nestSyncSettings.setScheduledSyncTime(LocalTime.fromDateFields(nextScheduledSync));
            
        } catch (IllegalArgumentException | ScheduleException e) {
            nestSyncSettings.setScheduledSyncTime(LocalTime.MIDNIGHT);
        }
        String scheduledSyncTime = nestSyncSettings.getScheduledSyncTime().toString();
        scheduledSyncTime = dateFormattingService.getDateTimeFormatter(DateFormatEnum.TIME24H, YukonUserContext.system).print(nestSyncSettings.getScheduledSyncTime());
        model.addAttribute("scheduledSyncTime", scheduledSyncTime);
        model.addAttribute("nestSyncSettings", nestSyncSettings);
        
        NestSyncTimeInfo nestSyncTimeInfo = nestSyncService.getSyncTimeInfo();
        String lastSyncTime = dateFormattingService.format(nestSyncTimeInfo.getSyncTime(), DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
        model.addAttribute("lastSyncTime", lastSyncTime);

        Instant nestSyncTime = nestSyncTimeInfo.getNextSyncTime();
        if (nestSyncTime != null) {
            String nextSyncTime = dateFormattingService.format(nestSyncTime, DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
            model.addAttribute("syncTitle", accessor.getMessage(baseKey + "nextSync") + nextSyncTime);
        } 
        if (nestSyncTime == null && !nestSyncTimeInfo.enableSyncButton()) {
            model.addAttribute("syncTitle", accessor.getMessage(baseKey + "nestSyncInProgress"));
        } else {
            model.addAttribute("syncTitle", accessor.getMessage(baseKey + "forceSync"));
        }
        model.addAttribute("syncNowEnabled", nestSyncTimeInfo.enableSyncButton());
        
        getDiscrepancies(model, userContext, paging, sorting, NestSyncType.values(), 0);

        return "dr/nest/details.jsp";
    }
    
    @RequestMapping(value="/nest/discrepancies", method=RequestMethod.GET)
    public String discrepancies(@DefaultItemsPerPage(value = 250) PagingParameters paging, @DefaultSort(dir=Direction.desc, sort="type") SortingParameters sorting,
                                ModelMap model, YukonUserContext userContext, NestSyncType[] types, int syncId) throws ServletException {
        getDiscrepancies(model, userContext, paging, sorting, types, syncId);
        return "dr/nest/discrepanciesTable.jsp";
    }
    
    private void getDiscrepancies(ModelMap model, YukonUserContext userContext, PagingParameters paging, 
                                  SortingParameters sorting, NestSyncType[] types, int syncId) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("types", NestSyncType.values());
        model.addAttribute("selectedTypes", types);
        
        SyncSortBy sortBy = SyncSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (SyncSortBy column : SyncSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        
        List<NestSync> syncTimes = nestDao.getNestSyncs();
        model.addAttribute("syncTimes", syncTimes);
        if (syncId == 0) {
            syncId = syncTimes.get(0).getId();
        }
        model.addAttribute("selectedSyncId", syncId);
        List<NestSyncType> typeList = new ArrayList<NestSyncType>();
        if (types != null) {
            typeList = Arrays.asList(types);
        }
        SearchResults<NestSyncDetail> searchResult = nestDao.getNestSyncDetail(syncId, paging, sortBy.getValue(), dir, typeList);
        model.addAttribute("discrepancies", searchResult);
    }
    
    @RequestMapping(value="/nest/download", method=RequestMethod.GET)
    public void download(NestSyncType[] types, int syncTime) throws IOException {
        //TODO: Create CSV of filtered discrepancies
        String test = "TESTING";
    }
    
    @RequestMapping(value="/nest/settings", method=RequestMethod.POST)
    public String settings(@ModelAttribute("nestSyncSettings")NestSyncSettings nestSyncSettings, BindingResult bindingResult,
                           @RequestParam(value="scheduledSyncTime", required=false) String scheduledSyncTime
                           ) {
        ScheduledRepeatingJob nestSyncJob = getJob(nestSyncJobDef);
        
        LocalTime updatedTime = LocalTime.parse(scheduledSyncTime);
        String updateTimeCron = "0 " + updatedTime.getMinuteOfHour() + " " 
                + updatedTime.getHourOfDay() + " * * ?";
        
        if (!nestSyncJob.getCronString().equals(updatedTime)) {
            jobManager.replaceScheduledJob(nestSyncJob.getId(), nestSyncJobDef, nestSyncJob.getJobDefinition().createBean(), 
                                           updateTimeCron, null, nestSyncJob.getJobProperties());
            nestSyncJob = getJob(nestSyncJobDef);
        }
        if (nestSyncJob.isDisabled() && nestSyncSettings.isSync()) {
            jobManager.enableJob(nestSyncJob);
            nestSyncJob = getJob(nestSyncJobDef);

        }
        if (!nestSyncJob.isDisabled() && !nestSyncSettings.isSync()) {
            jobManager.disableJob(nestSyncJob);
            nestSyncJob = getJob(nestSyncJobDef);

        }
        return "redirect:/dr/nest";
    }
    
    @RequestMapping(value="/nest/syncNow", method=RequestMethod.GET)
    public String syncNow() {
        nestSyncService.sync(true);
        return "redirect:/dr/nest";
    }
    
    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
    
    public enum SyncSortBy implements DisplayableEnum {

        type(SortBy.SYNCTYPE),
        reason(SortBy.SYNCREASONKEY),
        action(SortBy.SYNCACTIONKEY);
        
        private SyncSortBy(SortBy value) {
            this.value = value;
        }

        private final SortBy value;

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}
