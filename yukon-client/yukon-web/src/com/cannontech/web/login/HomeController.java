package com.cannontech.web.login;

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

import com.cannontech.common.userpage.dao.UserMonitorDao;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.model.UserMonitor;
import com.cannontech.common.userpage.model.UserMonitor.MonitorType;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Category;
import com.cannontech.common.userpage.model.UserPage.Module;
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

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserPageDao userPageDao;
    @Autowired private UserMonitorDao userMonitorDao;
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

        Multimap<Category, UserPage> pages = userPageDao.getPagesForUser(context.getYukonUser());

        List<UserPageWrapper> history = setupDisplayableHistory(pages, context);
        Multimap<Module, UserPageWrapper> favoritesMap = setupDisplayableFavorites(pages, context);

        model.put("history", history);
        model.put("favorites", favoritesMap.asMap());
        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());

        return "dashboard.jsp";
    }

    private List<UserPageWrapper> setupDisplayableHistory( Multimap<Category, UserPage> pages, YukonUserContext context) {

        List<UserPage> rawHistory = Lists.newArrayList(pages.get(Category.HISTORY));

        List<UserPageWrapper> history = Lists.newArrayList();
        for (UserPage page : rawHistory) {
            history.add( new UserPageWrapper(page, context));
        }

        Collections.sort(history, byDateDesc);
        return history;
    }

    private Multimap<Module, UserPageWrapper> setupDisplayableFavorites( Multimap<Category, UserPage> pages, YukonUserContext context) {

        List<UserPage> rawFavorites = Lists.newArrayList(pages.get(Category.FAVORITE));

        List<UserPageWrapper> favorites = Lists.newArrayList();
        for (UserPage page : rawFavorites) {
            favorites.add( new UserPageWrapper(page, context));
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

        UserPage page = new UserPage (context.getYukonUser().getUserID(), path, Category.FAVORITE, module, name, arguments);

        boolean isFavorite = userPageDao.contains(page);
        if( isFavorite ) {
            userPageDao.delete(page);
        } else {
            userPageDao.save(page);
        }

        JSONObject result = new JSONObject();
        result.put( "isFavorite", ! isFavorite );
        return result;
    }

    @RequestMapping("/isFavorite")
    public @ResponseBody JSONObject isFavorite(String path, YukonUserContext context) {

        UserPage page = new UserPage(context.getYukonUser().getUserID(), path, Category.FAVORITE);

        boolean isFavorite = userPageDao.contains(page);

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

        UserPage page = new UserPage(context.getYukonUser().getUserID(), path, Category.HISTORY, module, name, arguments);
        userPageDao.save(page);

        return new JSONObject();
    }

    @RequestMapping("/toggleSubscribed")
    public @ResponseBody JSONObject toggleSubscribed(String name, String monitorType, Integer monitorId, YukonUserContext context) {

        UserMonitor monitor = new UserMonitor (context.getYukonUser().getUserID(), name, MonitorType.valueOf(monitorType), monitorId, null);

        boolean isSubscribed = userMonitorDao.contains(monitor);
        if( isSubscribed ) {
            userMonitorDao.delete(monitor);
        } else {
            userMonitorDao.save(monitor);
        }

        JSONObject result = new JSONObject();
        result.put( "isSubscribed", ! isSubscribed );
        return result;
    }

    @RequestMapping("/isSubscribed")
    public @ResponseBody JSONObject isSubscribed(String monitorType, Integer monitorId, YukonUserContext context) {

        UserMonitor monitor = new UserMonitor (context.getYukonUser().getUserID(), null, MonitorType.valueOf(monitorType), monitorId, null);

        boolean isSubscribed = userMonitorDao.contains(monitor);

        JSONObject result = new JSONObject();
        result.put( "isSubscribed", isSubscribed );
        return result;
    }

    public String getHeaderForPage(UserPage page, YukonUserContext context) {
        ModuleBase a = moduleBuilder.getModuleBase(page.getModule());

        PageInfo thisPage = null;
        for (PageInfo pageInfo : a.getAllPageInfos()) {
            if (pageInfo.getName().equals(page.getName())){
                thisPage = pageInfo;
            }
        }

        PageContext pc = new PageContext();
        pc.pageInfo = thisPage;
        pc.labelArguments = page.getArguments();

        pageDetailProducer.fillInPageLabels(pc, messageSourceResolver.getMessageSourceAccessor(context));

        String result = pageDetailProducer.getPagePart("pageHeading", pc, messageSourceResolver.getMessageSourceAccessor(context));

        return result;
    }

    public class UserPageWrapper {
        private final UserPage page;
        private final String header;

        UserPageWrapper(UserPage page, YukonUserContext context) {
            this.page = page;
            this.header = getHeaderForPage(page, context);
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
        public int compare(UserPageWrapper left, UserPageWrapper right) {
            return - (left.getPage().getCreatedDate().compareTo(right.getPage().getCreatedDate()));
        }
    };

    private static Comparator<UserPageWrapper> byNameAsc = new Comparator<UserPageWrapper>() {
        public int compare(UserPageWrapper left, UserPageWrapper right) {
            return left.getHeader().compareTo(right.getHeader());
        }
    };

    private static Comparator<UserPageWrapper> byModuleAsc = new Comparator<UserPageWrapper>() {
        public int compare(UserPageWrapper left, UserPageWrapper right) {
            return left.getPage().getModuleEnum().compareTo(right.getPage().getModuleEnum());
        }
    };
}