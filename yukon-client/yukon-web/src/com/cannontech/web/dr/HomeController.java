package com.cannontech.web.dr;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.model.PerformanceVerificationAverageReports;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.dr.service.DemandResponseService.CombinedSortableField;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.OnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.model.RfPerformanceSettings;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class HomeController {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private DemandResponseService demandResponseService;
    @Autowired private UserPageDao userPageDao;
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private RfnPerformanceVerificationService performanceVerificationService;
    @Autowired private JobManager jobManager;
    @Autowired @Qualifier("rfnPerformanceVerification")
        private YukonJobDefinition<RfnPerformanceVerificationTask> rfnVerificationJobDef;
    @Autowired @Qualifier("rfnPerformanceVerificationEmail")
        private YukonJobDefinition<RfnPerformanceVerificationEmailTask> rfnEmailJobDef;
    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired private EnergyCompanyDao ecDao;

    @RequestMapping("/home")
    public String home(ModelMap model, YukonUserContext userContext) {

        LiteYukonUser user = userContext.getYukonUser();
        List<DisplayablePao> favorites = userPageDao.getDrFavorites(user);
        favorites = paoAuthorizationService.filterAuthorized(user, favorites, Permission.LM_VISIBLE);
        Collections.sort(favorites, demandResponseService.getSorter(CombinedSortableField.NAME, userContext));
        model.addAttribute("favorites", favorites);

        List<DisplayablePao> recentlyViewed = userPageDao.getDrRecentViewed(user);
        recentlyViewed = paoAuthorizationService.filterAuthorized(user, recentlyViewed, Permission.LM_VISIBLE);
        Collections.sort(recentlyViewed, demandResponseService.getSorter(CombinedSortableField.NAME, userContext));
        model.addAttribute("recents", recentlyViewed);

        /** RF BROADCAST PERMORMANCE */
        OnOff rfPerformance = globalSettingDao.getEnum(GlobalSettingType.RF_BROADCAST_PERFORMANCE, OnOff.class);
        boolean showRfPerformance = (rfPerformance == OnOff.ON);
        model.addAttribute("showRfPerformance", showRfPerformance);

        if (showRfPerformance) {
            PerformanceVerificationAverageReports averageReports = performanceVerificationService.getAverageReports();

            model.addAttribute("last24Hr", averageReports.getLastDay());
            model.addAttribute("last7Days", averageReports.getLastSevenDays());
            model.addAttribute("last30Days", averageReports.getLastThirtyDays());

            ScheduledRepeatingJob testCommandJob = getJob(rfnVerificationJobDef);
            ScheduledRepeatingJob emailJob = getJob(rfnEmailJobDef);

            RfPerformanceSettings settings = new RfPerformanceSettings();

            String[] time = StringUtils.split(testCommandJob.getCronString(), " ");
            int minutes = Integer.parseInt(time[1]);
            int hours = Integer.parseInt(time[2]);
            settings.setTime((hours * 60) + minutes); // minutes from midnight

            String[] emailTime = StringUtils.split(emailJob.getCronString(), " ");
            minutes = Integer.parseInt(emailTime[1]);
            hours = Integer.parseInt(emailTime[2]);
            settings.setEmailTime((hours * 60) + minutes); // minutes from midnight

            settings.setEmail(!emailJob.isDisabled());

            String notifs = emailJob.getJobProperties().get("notificationGroups");
            if (StringUtils.isNotBlank(notifs)) {
                List<String> groupIdStrings = Lists.newArrayList(notifs.split(","));
                List<Integer> groupIds = Lists.transform(groupIdStrings, new Function<String, Integer>(){
                    @Override public Integer apply(String input) {return Integer.parseInt(input);}
                });
                settings.setNotifGroupIds(groupIds);
            }

            model.addAttribute("settings", settings);
        }

        return "dr/home.jsp";
    }

    @RequestMapping("/details")
    public String details(LiteYukonUser user) {
        
        boolean showControlAreas =
                rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CONTROL_AREAS, user);
        boolean showScenarios =
                rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_SCENARIOS, user);

        // The Details link defaults to control area list, if control areas are hidden,
        // goes to scenarios, if they are hidden goes to programs

        String link = "/dr/controlArea/list";
        if (!showControlAreas) {
            if (showScenarios) {
                link = "/dr/scenario/list";
            } else {
                link = "/dr/program/list";
            }
        }

        return "redirect:" + link;
    }

    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
}