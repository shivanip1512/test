package com.cannontech.web.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

@Controller
public class HomeController {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserPageDao userPageDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    @Autowired private UserPageService userPageService;

    @RequestMapping({"/home", "/index.jsp"})
    public String home(HttpServletRequest req) {

        final LiteYukonUser user = ServletUtil.getYukonUser(req);
        String homeUrl = ServletUtil.createSafeUrl(req,
            rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user));

        return "redirect:" + homeUrl;
    }

    @RequestMapping("/dashboard")
    public String dashboard(ModelMap model, YukonUserContext context) {
        List<UserPage> pages = userPageDao.getPagesForUser(context.getYukonUser());

        List<UserPage> history = pages;//setupDisplayableHistory(pages, context);
        if (history.size() > UserPageDao.MAX_HISTORY ) {
            history = history.subList(0, UserPageDao.MAX_HISTORY);
        }
        model.put("history", history);

        Multimap<UserPage.Module, UserPage> favoritesMap = setupDisplayableFavorites(pages, context);
        model.put("favorites", favoritesMap.asMap());

        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());

        return "dashboard.jsp";
    }

    private Multimap<UserPage.Module, UserPage> setupDisplayableFavorites(List<UserPage> pages,
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
                return userPageService.getLocalizePageName(userPage, userContext);
            }
        };
        favorites = CtiUtilities.smartTranslatedSort(favorites, translator);

        Multimap<UserPage.Module, UserPage> favoritesMap = LinkedListMultimap.create();
        for (UserPage page : favorites) {
            favoritesMap.put(page.getModuleEnum(), page);
        }

        return favoritesMap;
    }

    @RequestMapping("/operator/Operations.jsp")
    public String operations(HttpServletRequest req) {
        return "redirect:/dashboard";
    }

    @RequestMapping("/toggleFavorite")
    public @ResponseBody JSONObject toggleFavorite(
            String module, 
            String name, 
            String labelArgs, 
            String path, 
            YukonUserContext context) {

        List<String> arguments = StringUtils.restoreJsSafeList(labelArgs);

        UserPage page = new UserPage (context.getYukonUser().getUserID(), path, true, module, name, arguments);

        boolean isFavorite = userPageDao.toggleFavorite(page);

        JSONObject result = new JSONObject();
        result.put( "isFavorite", isFavorite );
        return result;
    }

    @RequestMapping("/isFavorite")
    public @ResponseBody JSONObject isFavorite(String path, YukonUserContext context) {

        UserPage page = new UserPage(context.getYukonUser().getUserID(), path, true);

        boolean isFavorite = userPageDao.isFavorite(page);

        JSONObject result = new JSONObject();
        result.put( "isFavorite", isFavorite );
        return result;
    }

    @RequestMapping("/addToHistory")
    public @ResponseBody JSONObject addToHistory(
            String module, 
            String name, 
            String labelArgs, 
            String path, 
            YukonUserContext context) {

        List<String> arguments = StringUtils.restoreJsSafeList(labelArgs);

        UserPage page = new UserPage(context.getYukonUser().getUserID(), path, false, module, name, arguments);
        userPageDao.updateHistory(page);

        return new JSONObject();
    }

    @RequestMapping("/toggleSubscribed")
    public @ResponseBody JSONObject toggleSubscribed(String subscriptionType, Integer refId, YukonUserContext context) {
        UserSubscription monitor = new UserSubscription (context.getYukonUser().getUserID(),
            SubscriptionType.valueOf(subscriptionType), refId, null);

        boolean isSubscribed = userSubscriptionDao.contains(monitor);
        if( isSubscribed ) {
            userSubscriptionDao.delete(monitor);
        } else {
            userSubscriptionDao.save(monitor);
        }

        JSONObject result = new JSONObject();
        result.put( "isSubscribed", ! isSubscribed );
        return result;
    }

    @RequestMapping("/isSubscribed")
    public @ResponseBody JSONObject isSubscribed(String subscriptionType, Integer refId, YukonUserContext context) {
        UserSubscription monitor = new UserSubscription (context.getYukonUser().getUserID(),
            SubscriptionType.valueOf(subscriptionType), refId, null);

        boolean isSubscribed = userSubscriptionDao.contains(monitor);

        JSONObject result = new JSONObject();
        result.put( "isSubscribed", isSubscribed );
        return result;
    }

    private static Comparator<UserPage> byModuleAsc = new Comparator<UserPage>() {
        @Override
        public int compare(UserPage left, UserPage right) {
            return left.getModuleEnum().compareTo(right.getModuleEnum());
        }
    };
}