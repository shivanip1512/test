package com.cannontech.web.dr.nest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.cannontech.database.data.lite.LiteYukonUser;
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
import com.cannontech.system.GlobalSettingType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.dr.model.NestSyncSettings;
import com.cannontech.web.security.annotation.CheckGlobalSettingStringExist;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@CheckGlobalSettingStringExist(GlobalSettingType.NEST_USERNAME)
@Controller
public class NestController {
    
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
        Instant lastSyncTime = nestSyncTimeInfo.getSyncTime();
        if (lastSyncTime != null) {
            String lastSyncTimeString = dateFormattingService.format(nestSyncTimeInfo.getSyncTime(), DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
            model.addAttribute("lastSyncTime", lastSyncTimeString);
        }
        if (nestSyncTimeInfo.enableSyncButton()) {
            //button enabled 
            model.addAttribute("syncTitle", accessor.getMessage(baseKey + "forceSync"));
        } else if (nestSyncTimeInfo.isSyncInProgress()) {
            //button disabled because sync is in progress
            model.addAttribute("syncTitle", accessor.getMessage(baseKey + "nestSyncInProgress"));
        } else {
          //button disabled because to early for the next sync
            String nextSyncTimeString = dateFormattingService.format(nestSyncTimeInfo.getNextSyncTime(),
                DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
            model.addAttribute("syncTitle", accessor.getMessage(baseKey + "nextSync") + nextSyncTimeString);
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
        if (syncId == 0 && !syncTimes.isEmpty()) {
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
    public void download(NestSyncType[] types, int syncId, YukonUserContext userContext,
                         @DefaultSort(dir=Direction.asc, sort="type") SortingParameters sorting, 
                         @DefaultItemsPerPage(value=250) PagingParameters paging,
                         HttpServletResponse response) throws IOException {
        paging = PagingParameters.EVERYTHING;
        
        SyncSortBy sortBy = SyncSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<NestSyncType> typeList = new ArrayList<NestSyncType>();
        if (types != null) {
            typeList = Arrays.asList(types);
        }
        SearchResults<NestSyncDetail> searchResult = nestDao.getNestSyncDetail(syncId, paging, sortBy.getValue(), dir, typeList);
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[9];
        
        headerRow[0] = accessor.getMessage(SyncSortBy.type);
        headerRow[1] = accessor.getMessage(baseKey + "reason");
        headerRow[2] = accessor.getMessage(baseKey + "action");
        
        
        List<String[]> dataRows = Lists.newArrayList();
        for (NestSyncDetail detail: searchResult.getResultList()) {
            String[] dataRow = new String[3];
            dataRow[0] = accessor.getMessage(baseKey + detail.getType().name());
            dataRow[1] = accessor.getMessage(baseKey + detail.getReasonKey(), detail.getI18nValuesForKey(detail.getReasonKey()));
            dataRow[2] = accessor.getMessage(baseKey + detail.getActionKey(), detail.getI18nValuesForKey(detail.getActionKey()));
            dataRows.add(dataRow);
        }
        NestSync syncInfo = nestDao.getNestSyncById(syncId);
        String syncTime = dateFormattingService.format(syncInfo.getStartTime(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "nestSync_" + syncTime + ".csv");
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
    
    @RequestMapping(value="/nest/statistics", method=RequestMethod.GET)
    public String statistics(ModelMap model, LiteYukonUser user) {
        List<NestSync> syncTimes = nestDao.getNestSyncs();
        int syncId = 0;
        if (!syncTimes.isEmpty()) {
            syncId = syncTimes.get(0).getId();
        }
        SearchResults<NestSyncDetail> searchResult = nestDao.getNestSyncDetail(syncId, PagingParameters.EVERYTHING, SortBy.SYNCTYPE, Direction.desc, Arrays.asList(NestSyncType.values()));
        model.addAttribute("discrepancies", searchResult.getHitCount());
        return "dr/nest/statistics.jsp";
    }
    
    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
    
    public enum SyncSortBy implements DisplayableEnum {

        type(SortBy.SYNCTYPE);
        
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
    
    @GetMapping(value="/nest/isSyncAvailable")
    public @ResponseBody Map<String, Object> updateButton(YukonUserContext userContext) {
        Map<String, Object> json = new HashMap<>();
        NestSyncTimeInfo nestSyncTimeInfo = nestSyncService.getSyncTimeInfo();
        json.put("syncButtonEnabled", nestSyncTimeInfo.enableSyncButton());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        if (nestSyncTimeInfo.enableSyncButton()) {
            //button enabled 
            json.put("syncTitle", accessor.getMessage(baseKey + "forceSync"));
        } else if (nestSyncTimeInfo.isSyncInProgress()) {
            //button disabled because sync is in progress
            json.put("syncTitle", accessor.getMessage(baseKey + "nestSyncInProgress"));
        } else {
          //button disabled because to early for the next sync
            String nextSyncTimeString = dateFormattingService.format(nestSyncTimeInfo.getNextSyncTime(),
                DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
            json.put("syncTitle", accessor.getMessage(baseKey + "nextSync") + nextSyncTimeString);
        }
        return json;
    }
}
