package com.cannontech.web.dr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.system.SystemJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.RfPerformanceSettings;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String KEY_BASE = "yukon.web.modules.dr.home.rfPerformance.configure.";
    
    private static final Logger log = YukonLogManager.getLogger(RfPerformanceController.class);
    
    @Autowired private ScheduledRepeatingJobDao jobDao;
    
    @RequestMapping(value="/rf/performance", method=RequestMethod.POST)
    public String saveSettings(@ModelAttribute("settings") RfPerformanceSettings settings, YukonUserContext userContext, FlashScope flash) {
        
        LocalTime time = LocalTime.MIDNIGHT.plusMinutes(settings.getTime());
        
        StringBuilder cron = new StringBuilder("0 ");
        cron.append(time.getMinuteOfHour() + " ");
        cron.append(time.getHourOfDay() + " * * ?");
        
        log.debug("Cron update: '" + cron.toString() + "'");
        
        ScheduledRepeatingJob testCommandJob = jobDao.getById(SystemJob.RF_BROADCAST_PERFORMANCE.getJobId());
        testCommandJob.setCronString(cron.toString());
        
        ScheduledRepeatingJob emailJob = jobDao.getById(SystemJob.RF_BROADCAST_PERFORMANCE_EMAIL.getJobId());
        emailJob.setDisabled(!settings.isEmail());
        emailJob.getJobProperties().put("notificationGroups", StringUtils.join(settings.getNotifGroupIds(), ","));
//        emailJob.getJobProperties().put("additionalEmails", "");
        
        try {
            jobDao.update(testCommandJob);
            jobDao.update(emailJob);
            flash.setConfirm(new YukonMessageSourceResolvable(KEY_BASE + "success"));
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(KEY_BASE + "failed"));
            log.error("Failed to update RF Broadcast Performance Email Job", e);
        }
        
        return "redirect:/dr/home";
    }
}