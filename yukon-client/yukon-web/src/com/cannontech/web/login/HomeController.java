package com.cannontech.web.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadRequestException;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.SiteMapCategory;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Key;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.cannontech.web.support.SystemHealthMetric;
import com.cannontech.web.support.SystemHealthMetricIdentifier;
import com.cannontech.web.support.SystemHealthMetricType;
import com.cannontech.web.support.service.SystemHealthService;
import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

@Controller
public class HomeController {
    
    private final Logger log = YukonLogManager.getLogger(HomeController.class);
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SystemHealthService systemHealthService;
    @Autowired private UserPageDao userPageDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    @Autowired private UserPageService userPageService;

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
        List<UserPage> pages = userPageDao.getPagesForUser(userContext.getYukonUser());
        
        // History
        List<UserPage> history = pages;//setupDisplayableHistory(pages, userContext);
        if (history.size() > UserPageDao.MAX_HISTORY ) {
            history = history.subList(0, UserPageDao.MAX_HISTORY);
        }
        model.put("history", history);
        
        // Favorites
        Multimap<SiteMapCategory, UserPage> favoritesMap = setupDisplayableFavorites(pages, userContext);
        model.put("favorites", favoritesMap.asMap());

        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());
        
        // System health metrics
        List<SystemHealthMetricIdentifier> favoriteIds = systemHealthService.getFavorites(userContext.getYukonUser());
        Multimap<SystemHealthMetricType, SystemHealthMetric> metrics = systemHealthService.getMetricsByIdentifiers(favoriteIds);
        
        if (metrics.size() > 0) {
            model.addAttribute("showSystemHealth", true);
            Collection<SystemHealthMetric> extendedQueueData = metrics.get(SystemHealthMetricType.JMS_QUEUE_EXTENDED);
            model.addAttribute("extendedQueueData", extendedQueueData);
            Collection<SystemHealthMetric> queueData = metrics.get(SystemHealthMetricType.JMS_QUEUE);
            model.addAttribute("queueData", queueData);
        } else {
            model.addAttribute("showSystemHealth", false);
        }
        
        return "dashboard.jsp";
    }

    @RequestMapping("/operator/Operations.jsp")
    public String operations() {
        return "redirect:/dashboard";
    }

    private Multimap<SiteMapCategory, UserPage> setupDisplayableFavorites(List<UserPage> pages,
            final YukonUserContext userContext) {
        List<UserPage> favorites = new ArrayList<>();
        for (UserPage page : pages) {
            if (page.isFavorite()) {
                favorites.add(page);
            }
        }

        Collections.sort(favorites, byModuleAsc);

        // Sort on the localized name.
        Function<UserPage, String> translator = new Function<UserPage, String>() {
            @Override
            public String apply(UserPage userPage) {
                return userPageService.getLocalizedPageTitle(userPage, userContext);
            }
        };
        favorites = CtiUtilities.smartTranslatedSort(favorites, translator);

        Multimap<SiteMapCategory, UserPage> favoritesMap = LinkedListMultimap.create();
        for (UserPage page : favorites) {
            favoritesMap.put(page.getModule().getSiteMapCategory(), page);
        }

        return favoritesMap;
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

    @RequestMapping("/isFavorite")
    public @ResponseBody Map<String, Boolean> isFavorite(String path, YukonUserContext userContext) {
        Key userPageKey = new Key(userContext.getYukonUser().getUserID(), path);
        return Collections.singletonMap("isFavorite", userPageDao.isFavorite(userPageKey));
    }

    @RequestMapping("/addToHistory")
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

    private final static Comparator<UserPage> byModuleAsc = Ordering.natural().onResultOf(
        new Function<UserPage, SiteMapCategory>() {
            @Override
            public SiteMapCategory apply(UserPage userPage) {
                return userPage.getModule().getSiteMapCategory();
            }
        });
}
