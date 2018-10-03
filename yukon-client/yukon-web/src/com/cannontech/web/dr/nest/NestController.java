package com.cannontech.web.dr.nest;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.JobManagerException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.model.NestSyncSettings;

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
    
//    @Autowired @Qualifier("nestScheduledSync")
//    private YukonJobDefinition<NestSyncTask> nestSyncJobDef;

//    @PostConstruct
//    public void init() {
//        String defaultCron = "0 0 0 * * ?";// every day at 12am
//        try {
//            List<ScheduledRepeatingJob> nestSyncJobs =
//                    jobManager.getNotDeletedRepeatingJobsByDefinition(nestSyncJobDef);
//            if (nestSyncJobs == null || nestSyncJobs.isEmpty()) {
//                ScheduledRepeatingJob job = new ScheduledRepeatingJob();
//                job.setBeanName(nestSyncJobDef.getName());
//                job.setCronString(defaultCron);
//                job.setDisabled(true);
//                job.setJobGroupId(nextValueHelper.getNextValue("Job"));
//                job.setJobDefinition(nestSyncJobDef);
//                job.setJobProperties(Collections.<String, String>emptyMap());
//                scheduledRepeatingJobDao.save(job);
//                jobManager.instantiateTask(job);
//            }
//        } catch (JobManagerException e) {
//            
//        }
//
//        dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, YukonUserContext.system);
//
//    }
    
    @RequestMapping(value="/nest", method=RequestMethod.GET)
    public String details(ModelMap model, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        
        NestSyncSettings nestSyncSettings = new NestSyncSettings();
        nestSyncSettings.setSync(false);
        nestSyncSettings.setSyncTime(LocalTime.MIDNIGHT);
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
        return "dr/nest/details.jsp";
    }
    
    @RequestMapping(value="nest/settings", method=RequestMethod.POST)
    public String settings(ModelMap model, YukonUserContext userContext) {
        return "redirect:/dr/nest";
    }
    
    @RequestMapping(value="nest/syncNow", method=RequestMethod.GET)
    public String syncNow() {
        nestSyncService.sync(true);
        return "redirect:/dr/nest";
    }
}
