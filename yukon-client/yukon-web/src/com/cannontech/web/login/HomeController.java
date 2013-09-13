package com.cannontech.web.login;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Module;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.layout.PageDetailProducer;
import com.cannontech.web.layout.PageDetailProducer.PageContext;
import com.cannontech.web.menu.CommonModuleBuilder;
import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.menu.PageInfo;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Controller
public class HomeController {
    private final static Logger log = YukonLogManager.getLogger(HomeController.class);

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserPageDao userPageDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PageDetailProducer pageDetailProducer;
    @Autowired private CommonModuleBuilder moduleBuilder;

    @RequestMapping({"/home", "/index.jsp"})
    public String home(HttpServletRequest req) {

        final LiteYukonUser user = ServletUtil.getYukonUser(req);
        String homeUrl = ServletUtil.createSafeUrl(req, rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user));

        return "redirect:" + homeUrl;
    }

    @RequestMapping("/dashboard")
    public String dashboard(ModelMap model, YukonUserContext context) {

        List<UserPage> pages = userPageDao.getPagesForUser(context.getYukonUser());

        List<UserPageWrapper> history = setupDisplayableHistory(pages, context);
        Multimap<Module, UserPageWrapper> favoritesMap = setupDisplayableFavorites(pages, context);

        model.put("history", history);
        model.put("favorites", favoritesMap.asMap());
        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());

        return "dashboard.jsp";
    }

    private List<UserPageWrapper> setupDisplayableHistory( List<UserPage> pages, YukonUserContext context) {

        List<UserPage> rawHistory = Lists.newArrayList(pages);

        List<UserPageWrapper> history = Lists.newArrayList();
        for (UserPage page : rawHistory) {
            UserPageWrapper wrapper = buildWrapperForPage(page, context);
            if (wrapper != null) {
                history.add(wrapper);
            } else {
                log.error("bad page found in history list: " + page.getName());
            }
        }

        Collections.sort(history, byDateDesc);

        if (history.size() > UserPageDao.MAX_HISTORY ) {
            history = history.subList(0, UserPageDao.MAX_HISTORY);
        }
        
        return history;
    }

    private Multimap<Module, UserPageWrapper> setupDisplayableFavorites( List<UserPage> pages, YukonUserContext context) {

        List<UserPage> rawFavorites = Lists.newArrayList();
        for (UserPage page : pages) {
            if (page.isFavorite()) {
                rawFavorites.add(page);
            }
        }

        List<UserPageWrapper> favorites = Lists.newArrayList();
        for (UserPage page : rawFavorites) {
            UserPageWrapper wrapper = buildWrapperForPage(page, context);
            if (wrapper != null) {
                favorites.add(wrapper);
            } else {
                log.error("bad page found in favorites list: " + page.getName());
            }
        }
        Collections.sort(favorites, byNameAsc);
        Collections.sort(favorites, byModuleAsc);

        Multimap<Module, UserPageWrapper> favoritesMap = LinkedListMultimap.create();
        for (UserPageWrapper page : favorites) {
            favoritesMap.put(page.getPage().getModuleEnum(), page);
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

        UserSubscription monitor = new UserSubscription (context.getYukonUser().getUserID(), SubscriptionType.valueOf(subscriptionType), refId, null);

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

        UserSubscription monitor = new UserSubscription (context.getYukonUser().getUserID(), SubscriptionType.valueOf(subscriptionType), refId, null);

        boolean isSubscribed = userSubscriptionDao.contains(monitor);

        JSONObject result = new JSONObject();
        result.put( "isSubscribed", isSubscribed );
        return result;
    }

    private UserPageWrapper buildWrapperForPage(UserPage page, YukonUserContext context) {
        ModuleBase moduleBase = moduleBuilder.getModuleBase(page.getModule());

        PageInfo thisPage = moduleBase.getPageInfo(page.getName());
        if (thisPage == null) {
            // This shouldn't _normally_ happen but can in a development environment when switching workspaces
            // where one workspace has a new page the other doesn't.  (The new page will be in the user history
            // and trigger this.)
            return null;
        }

        PageContext pc = new PageContext();
        pc.pageInfo = thisPage;
        pc.labelArguments = page.getArguments();

        pageDetailProducer.fillInPageLabels(pc, messageSourceResolver.getMessageSourceAccessor(context));

        String result = pageDetailProducer.getPagePart("pageHeading", pc, messageSourceResolver.getMessageSourceAccessor(context));

        return new UserPageWrapper(page, result);
    }

    public static class UserPageWrapper {
        private final UserPage page;
        private final String header;

        private UserPageWrapper(UserPage page, String header) {
            this.page = page;
            this.header = header;
        }

        public UserPage getPage() {
            return page;
        }
        public String getHeader() {
            return header;
        }
        public String getPath() {
            return page.getPath();
        }
        public String getName() {
            return page.getName();
        }
        public String getModule() {
            return page.getModule();
        }
        public String getLabelArgs() {
            String result = StringUtils.listAsJsSafeString(page.getArguments());
            return result;
        }
    }

    private static Comparator<UserPageWrapper> byDateDesc = new Comparator<UserPageWrapper>() {
        @Override
        public int compare(UserPageWrapper left, UserPageWrapper right) {
            return - (left.getPage().getLastAccess().compareTo(right.getPage().getLastAccess()));
        }
    };

    private static Comparator<UserPageWrapper> byNameAsc = new Comparator<UserPageWrapper>() {
        @Override
        public int compare(UserPageWrapper left, UserPageWrapper right) {
            return left.getHeader().compareTo(right.getHeader());
        }
    };

    private static Comparator<UserPageWrapper> byModuleAsc = new Comparator<UserPageWrapper>() {
        @Override
        public int compare(UserPageWrapper left, UserPageWrapper right) {
            return left.getPage().getModuleEnum().compareTo(right.getPage().getModuleEnum());
        }
    };
}