package com.cannontech.web.login;

import java.util.Collections;
import java.util.List;
import java.util.Map;


import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadRequestException;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage.Key;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.service.DashboardService;

@Controller
public class HomeController {
    
    private final Logger log = YukonLogManager.getLogger(HomeController.class);
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserPageDao userPageDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    @Autowired private DashboardService dashboardService;

    @RequestMapping(value = {"/home", "/index.jsp"})
    public String home(LiteYukonUser user) {
        String homeUrl = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user);
        if ("/operator/Operations.jsp".equals(homeUrl)) {
            homeUrl = "/dashboard";
        }

        return "redirect:" + homeUrl;
    }

    @RequestMapping("/dashboard")
    public String dashboard(ModelMap model, YukonUserContext userContext) {
        Dashboard mainDashboard = dashboardService.getAssignedDashboard(userContext.getYukonUser().getUserID(), DashboardPageType.MAIN);
        model.addAttribute("dashboardPageType", DashboardPageType.MAIN);
        return "redirect:/dashboards/" + mainDashboard.getDashboardId() + "/view";
    }

    @RequestMapping("/operator/Operations.jsp")
    public String operations() {
        return "redirect:/dashboard";
    }

    @RequestMapping("/toggleFavorite")
    public @ResponseBody Map<String, Boolean> toggleFavorite(String module, String name, String labelArgs, String path,
            YukonUserContext userContext) throws BadRequestException {
        boolean isFavorite = false;
        List<String> arguments = StringUtils.restoreJsSafeList(labelArgs);
        SiteModule moduleName = SiteModule.getByName(module);
        if (null != name && null != path && null != moduleName) {

            try {
                isFavorite = userPageDao.toggleFavorite(new Key(userContext.getYukonUser().getUserID(), path), moduleName, name,
                        arguments);
            } catch (DataIntegrityViolationException e) {
                log.error("Unable to save Favorite data .", e);
            }
        } else {
            log.warn("Unexpected error : any request parameter should not be null");
            throw new BadRequestException();
        }
        return Collections.singletonMap("isFavorite", isFavorite);
    }

    @RequestMapping(value = "/isFavorite", method = RequestMethod.GET)
    public @ResponseBody Map<String, Boolean> isFavorite(String path, YukonUserContext userContext) {
        Key userPageKey = new Key(userContext.getYukonUser().getUserID(), path);
        return Collections.singletonMap("isFavorite", userPageDao.isFavorite(userPageKey));
    }

    @RequestMapping(value = "/addToHistory", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> addToHistory(String module, String name, String labelArgs, String path,
            YukonUserContext userContext) throws BadRequestException {
        List<String> arguments = StringUtils.restoreJsSafeList(labelArgs);
        SiteModule moduleName = SiteModule.getByName(module);
        if (null != name && null != path && null != moduleName) {

            try {
                userPageDao.updateHistory(new Key(userContext.getYukonUser().getUserID(), path), moduleName, name,
                    arguments);
            } catch (DataIntegrityViolationException e) {
                log.error("Unable to save History data .", e);
            }
        } else {
            log.warn("Unexpected error : any request parameter should not be null ");
            throw new BadRequestException();
        }
        return Collections.emptyMap();
    }

    @RequestMapping("/toggleSubscribed")
    public @ResponseBody Map<String, Boolean> toggleSubscribed(SubscriptionType subscriptionType, Integer refId,
            YukonUserContext userContext) {
        UserSubscription monitor = 
                new UserSubscription (userContext.getYukonUser().getUserID(), subscriptionType, refId, null);

        boolean isSubscribed = userSubscriptionDao.contains(monitor);
        if(isSubscribed) {
            userSubscriptionDao.delete(monitor);
        } else {
            userSubscriptionDao.save(monitor);
        }

        return Collections.singletonMap("isSubscribed", !isSubscribed);
    }

    @RequestMapping("/isSubscribed")
    public @ResponseBody Map<String, Boolean> isSubscribed(SubscriptionType subscriptionType, Integer refId,
            YukonUserContext userContext) {
        UserSubscription monitor = 
                new UserSubscription(userContext.getYukonUser().getUserID(), subscriptionType, refId, null);

        return Collections.singletonMap("isSubscribed", userSubscriptionDao.contains(monitor));
    }
}
