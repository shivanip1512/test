package com.cannontech.web.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.SiteMapCategory;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

@Controller
public class HomeController {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserPageDao userPageDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    @Autowired private UserPageService userPageService;

    @RequestMapping(value = {"/home", "/index.jsp"})
    public String home(LiteYukonUser user) {
        String homeUrl = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user);

        return "redirect:" + homeUrl;
    }

    @RequestMapping("/dashboard")
    public String dashboard(ModelMap model, YukonUserContext userContext) {
        List<UserPage> pages = userPageDao.getPagesForUser(userContext.getYukonUser());

        List<UserPage> history = pages;//setupDisplayableHistory(pages, userContext);
        if (history.size() > UserPageDao.MAX_HISTORY ) {
            history = history.subList(0, UserPageDao.MAX_HISTORY);
        }
        model.put("history", history);

        Multimap<SiteMapCategory, UserPage> favoritesMap = setupDisplayableFavorites(pages, userContext);
        model.put("favorites", favoritesMap.asMap());

        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());

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
    public @ResponseBody Map<String, Boolean> toggleFavorite(String module, String name, String labelArgs,
            String path, YukonUserContext userContext) {
        List<String> arguments = StringUtils.restoreJsSafeList(labelArgs);
        UserPage page = new UserPage(userContext.getYukonUser().getUserID(), path, true,
            SiteModule.getByName(module), name, arguments);
        return Collections.singletonMap("isFavorite", userPageDao.toggleFavorite(page));
    }

    @RequestMapping("/isFavorite")
    public @ResponseBody Map<String, Boolean> isFavorite(String path, YukonUserContext userContext) {
        UserPage page = new UserPage(userContext.getYukonUser().getUserID(), path, true);
        return Collections.singletonMap("isFavorite", userPageDao.isFavorite(page));
    }

    @RequestMapping("/addToHistory")
    public @ResponseBody Map<String, Object> addToHistory(String module, String name, String labelArgs, String path, 
            YukonUserContext userContext) {
        List<String> arguments = StringUtils.restoreJsSafeList(labelArgs);

        UserPage page = new UserPage(userContext.getYukonUser().getUserID(), path, false,
            SiteModule.getByName(module), name, arguments);
        userPageDao.updateHistory(page);

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
