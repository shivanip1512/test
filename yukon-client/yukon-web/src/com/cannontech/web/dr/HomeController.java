package com.cannontech.web.dr;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.dr.service.DemandResponseService.CombinedSortableField;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.model.BroadcastPerformanceStats;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class HomeController {
    
    @Autowired private GlobalSettingDao gsDao;
    @Autowired private RolePropertyDao rpDao;
    @Autowired private PaoAuthorizationService paoAuthService;
    @Autowired private DemandResponseService drService;
    @Autowired private UserPageDao userPageDao;

    @RequestMapping("/home")
    public String home(ModelMap model, 
                    YukonUserContext userContext,
                    String favSort, 
                    Boolean favDescending, 
                    String rvSort,
                    Boolean rvDescending) {
        
        LiteYukonUser user = userContext.getYukonUser();
        List<DisplayablePao> favorites = userPageDao.getDrFavorites(user);
        favorites = paoAuthService.filterAuthorized(user, favorites, Permission.LM_VISIBLE);

        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator();
        if (favSort != null) {
            CombinedSortableField sortField = CombinedSortableField.valueOf(favSort);
            sorter = drService.getSorter(sortField, userContext);
            if (favDescending != null && favDescending && sorter != null) {
                sorter = Ordering.from(sorter).reverse();
            }
        }

        Collections.sort(favorites, sorter);
        model.addAttribute("favorites", favorites);

        List<DisplayablePao> recentlyViewed = userPageDao.getDrRecentViewed(user);

        recentlyViewed = paoAuthService.filterAuthorized(user, recentlyViewed, Permission.LM_VISIBLE);
        sorter = null;
        if (rvSort != null) {
            CombinedSortableField sortField = CombinedSortableField.valueOf(rvSort);
            sorter = drService.getSorter(sortField, userContext);
            if (rvDescending != null && rvDescending && sorter != null) {
                sorter = Ordering.from(sorter).reverse();
            }
        }
        if (sorter == null) {
            sorter = new DisplayablePaoComparator();
        }
        Collections.sort(recentlyViewed, sorter);
        model.addAttribute("recents", recentlyViewed);
        
        /* RF BROADCAST PERMORMANCE */
        boolean rfPerformance = rpDao.checkRole(YukonRole.LM_DIRECT_LOADCONTROL, user);
//      boolean rfPerformance = rpDao.checkRole(YukonRole.LM_DIRECT_LOADCONTROL, user)
//              && gsDao.getEnum(GlobalSettingType.RF_BROADCAST_PERFORMANCE, OnOff.class);
        LocalTime testSchedule = new LocalTime(5, 0);
        BroadcastPerformanceStats lastTest = new BroadcastPerformanceStats(47, 5, 48);
        BroadcastPerformanceStats last7Days = new BroadcastPerformanceStats(80, 15, 5);
        BroadcastPerformanceStats last30Days = new BroadcastPerformanceStats(94, 1, 5);
        
        model.addAttribute("showRfPerformance", rfPerformance);
        model.addAttribute("testSchedule", testSchedule);
        model.addAttribute("lastTest", lastTest);
        model.addAttribute("last7Days", last7Days);
        model.addAttribute("last30Days", last30Days);

        return "dr/home.jsp";
    }
    
    @RequestMapping("/details")
    public String details(ModelMap model, LiteYukonUser user) {
        
        boolean showControlAreas = 
            rpDao.checkProperty(YukonRoleProperty.SHOW_CONTROL_AREAS, user);
        boolean showScenarios = 
            rpDao.checkProperty(YukonRoleProperty.SHOW_SCENARIOS, user);

        // The Details link defaults to control area list, if control areas are hidden,
        // goes to scenarios, if they are hidden goes to programs

        String link = "/dr/controlArea/list";
        if(!showControlAreas) {
            if(showScenarios) {
                link = "/dr/scenario/list"; 
            } else {
                link = "/dr/program/list";
            }
        }

        return "redirect:" + link;
    }

}