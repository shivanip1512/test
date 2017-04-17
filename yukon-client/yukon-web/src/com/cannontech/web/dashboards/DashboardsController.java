package com.cannontech.web.dashboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.UserDashboardSettings;
import com.cannontech.web.common.dashboard.model.DashboardSetting;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.cannontech.web.common.dashboard.service.DashboardService;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/*")
public class DashboardsController {
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private WidgetService widgetService;
    @Autowired private DashboardService dashboardService;
    @Autowired private DashboardDao dashboardDao;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping("manage")
    public String manageDashboards(ModelMap model, @RequestParam(value="filter", required=false) String filter, YukonUserContext userContext, 
                                   @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, PagingParameters paging) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("pageTypes", DashboardPageType.values());
        List<LiteDashboard> dashboards = createDashboardsListMockup(userContext);
        //List<LiteDashboard> dashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
        if (filter != null) {
            if (filter.equals("myFavorites")) {
                //dashboards = dashboardService.getFavorites(userContext.getYukonUser().getUserID());
            } else if (filter.equals("createdByMe")) {
                //dashboards = dashboardService.getOwnedDashboards(userContext.getYukonUser().getUserID());
            }
        }

        SearchResults<LiteDashboard> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, dashboards.size());
        
        DashboardSortBy sortBy = DashboardSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        List<LiteDashboard>itemList = Lists.newArrayList(dashboards);
        
        Comparator<LiteDashboard> comparator = (o1, o2) -> o1.getName().compareTo(o2.getName());
        if (sortBy == DashboardSortBy.createdBy) {
            comparator = (o1, o2) -> o1.getOwner().getUsername().compareTo(o2.getOwner().getUsername());
        } else if (sortBy == DashboardSortBy.visibility) {
            comparator = (o1, o2) -> o1.getVisibility().compareTo(o2.getVisibility());
        } else if (sortBy == DashboardSortBy.numberOfUsers) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getUsers()).compareTo(Integer.valueOf(o2.getUsers()));
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        for (DashboardSortBy column : DashboardSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, dashboards.size());
        searchResult.setResultList(itemList);        
        
        model.addAttribute("dashboards", searchResult);
        model.addAttribute("dashboardsList", dashboards);
        
        UserDashboardSettings settings = new UserDashboardSettings();
        Arrays.asList(DashboardPageType.values()).forEach(pageType -> {
            DashboardSetting setting = new DashboardSetting();
            setting.setPageType(pageType);
            //Dashboard assignedDashboard = dashboardService.getAssignedDashboard(userContext.getYukonUser().getUserID(), pageType);
            Dashboard assignedDashboard = createDashboardMockup(userContext);
            setting.setDashboardId(assignedDashboard.getDashboardId());
            settings.addSetting(setting);
        });
        
        model.addAttribute("dashboardSettings", settings);
        
        return "manageDashboards.jsp";
    }
    
    @RequestMapping("saveSettings")
    public String saveSettings(@ModelAttribute UserDashboardSettings settings, YukonUserContext userContext) {
        settings.getSettings().forEach(setting -> {
            //dashboardService.setDefault(Arrays.asList(userContext.getYukonUser().getUserID()), setting.getPageType(), setting.getDashboardId());
        });

        return "redirect:/dashboards/manage";
    }
    
    @RequestMapping("admin")
    public String adminDashboards(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        return "dashboardAdmin.jsp";
    }
    
    @RequestMapping("create")
    public String createDashboard(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.CREATE);
        model.addAttribute("visibilityOptions", Visibility.values());
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        model.addAttribute("dashboard", new Dashboard());
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/editDetails")
    public String editDetails(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        model.addAttribute("visibilityOptions", Visibility.values());
        model.addAttribute("dashboard", createDashboardMockup(userContext));
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/view")
    public String viewDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.VIEW);
        //Dashboard dashboard = dashboardService.getDashboard(id);
        Dashboard dashboard = createDashboardMockup(userContext);
        model.addAttribute("dashboard", dashboard);
        return "dashboardView.jsp";
    }
    
    @RequestMapping("{id}/edit")
    public String editDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        //Dashboard dashboard = dashboardService.getDashboard(id);
        Dashboard dashboard = createDashboardMockup(userContext);
        model.addAttribute("dashboard", dashboard);
        return "dashboardEdit.jsp";
    }
    
    @RequestMapping("{id}/addWidgets")
    public String addWidgets(@PathVariable int id, ModelMap model) {
        model.addAttribute("widgetMap", widgetService.getTypesByCategory().asMap());
        model.addAttribute("totalWidgets", WidgetType.values().length);
        return "addWidgets.jsp";
    }
    
    @RequestMapping("{id}/addWidget/{type}")
    public String addWidgetType(@PathVariable int id, @PathVariable String type, ModelMap model, YukonUserContext userContext) {
        WidgetType widgetType = WidgetType.valueOf(type);
        Widget widget = new Widget();
        widget.setType(widgetType);
        model.addAttribute("widget", widget);
        model.addAttribute("dashboard", createDashboardMockup(userContext));
        return "widgetAddRow.jsp";
    }
    
    @RequestMapping("saveDetails")
    public String saveDetails(@ModelAttribute Dashboard dashboard) {
        //only update name, description, visibility, template
        //Check for dashboardTemplate
        return "dashboardView.jsp";
        //return "redirect:/dashboards/" + dashboard.getId() + "/view";
    }
    
    @RequestMapping("save")
    public String saveDashboard(@ModelAttribute Dashboard dashboard) {
        //int id = dashboardService.update(dashboard);
        return "dashboardView.jsp";
        //return "redirect:/dashboards/" + dashboard.getId() + "/view";
    }
    
    @RequestMapping("{id}/copy")
    public String copyDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("visibilityOptions", Visibility.values());
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/delete")
    public String deleteDashboard(FlashScope flash, @PathVariable int id, YukonUserContext userContext) {
        try {
            dashboardService.getOwner(id);
            LiteYukonUser user = userContext.getYukonUser();
            List<Integer> userIdList = dashboardDao.getAllUsersForDashboard(id);
            if (user == dashboardService.getOwner(id).get()) {
                if (userIdList.size() > 1) {
                    dashboardService.delete(id);
                    flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dashboard.delete.success"));
                    return "redirect:/dashboards/manage";
                }
                flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dashboard.delete.exception.currentInUse"));
            }
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dashboard.delete.exception.notOwner"));
            return "redirect:/dashboards/manage";
        } 
        catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dashboard.delete.exception"));
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/favorite")
    public @ResponseBody Map<String, Boolean> favoriteDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        //dashboardService.favorite(userContext.getYukonUser().getUserID(), id);
        return Collections.singletonMap("isFavorite", true);
    }
    
    @RequestMapping("{id}/unfavorite")
    public @ResponseBody Map<String, Boolean> unfavoriteDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        //dashboardService.unfavorite(userContext.getYukonUser().getUserID(), id);
        return Collections.singletonMap("isFavorite", false);
    }
    
    private Dashboard createDashboardMockup(YukonUserContext userContext) {
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Utility Company Sample Dashboard");
        dashboard.setDescription("Utility Company Default Test Dashboard");
        dashboard.setVisibility(Visibility.SHARED);
        dashboard.setOwner(userContext.getYukonUser());
        dashboard.setPageType(DashboardPageType.MAIN);
        dashboard.setDashboardId(12);
        dashboard.setPageType(DashboardPageType.MAIN);
        Widget widget = new Widget();
        widget.setDashboardId(12);
        widget.setId(11);
        widget.setType(WidgetType.MONITOR_SUBSCRIPTIONS);
        Widget widget2 = new Widget();
        widget2.setDashboardId(12);
        widget2.setId(12);
        widget2.setType(WidgetType.TREND);
        Map<String, String> params = new HashMap<>();
        Map<Integer, SimpleMeter> meters = databaseCache.getAllMeters();
        Iterator<SimpleMeter> meterIterator = meters.values().iterator();
        SimpleMeter meter = meterIterator.next();
        params.put("deviceId",  Integer.toString(meter.getPaoIdentifier().getPaoId()));
        widget2.setParameters(params);
        dashboard.addColumn1Widget(widget);
        dashboard.addColumn1Widget(widget2);
        Widget widget3 = new Widget();
        widget3.setDashboardId(12);
        widget3.setId(22);
        widget3.setType(WidgetType.TREND);
        Map<String, String> params2 = new HashMap<>();
        SimpleMeter meter2 = meterIterator.next();
        params2.put("deviceId",  Integer.toString(meter2.getPaoIdentifier().getPaoId()));
        widget3.setParameters(params2);
        widget3.setId(21);
        dashboard.addColumn2Widget(widget3);
        Widget widget4 = new Widget();
        widget4.setDashboardId(12);
        widget4.setId(22);
        widget4.setType(WidgetType.METER_SEARCH);
        dashboard.addColumn2Widget(widget4);
        Widget widget5 = new Widget();
        widget5.setDashboardId(12);
        widget5.setId(13);
        widget5.setType(WidgetType.SCHEDULED_REQUESTS);
        dashboard.addColumn1Widget(widget5);
        return dashboard;
    }
    
    private List<LiteDashboard> createDashboardsListMockup(YukonUserContext userContext) {
        List<LiteDashboard> dashboards = new ArrayList<>();
        LiteDashboard dashboard = new LiteDashboard();
        dashboard.setName("Yukon Default Dashboard");
        dashboard.setDashboardId(12);
        dashboard.setPageType(DashboardPageType.MAIN);
        dashboard.setVisibility(Visibility.SYSTEM);
        dashboard.setOwner(userContext.getYukonUser());
        dashboard.setUsers(2456);
        dashboards.add(dashboard);
        LiteDashboard dashboard2 = new LiteDashboard();
        dashboard2.setName("Utility Company Sample Dashboard");
        dashboard2.setDashboardId(13);
        dashboard2.setPageType(DashboardPageType.MAIN);
        dashboard2.setVisibility(Visibility.SHARED);
        dashboard2.setOwner(userContext.getYukonUser());
        dashboard2.setUsers(5);
        dashboards.add(dashboard2);
        LiteDashboard dashboard3 = new LiteDashboard();
        dashboard3.setName("User Specific Dashboard");
        dashboard3.setDashboardId(14);
        dashboard3.setPageType(DashboardPageType.MAIN);
        dashboard3.setVisibility(Visibility.PRIVATE);
        dashboard3.setOwner(userContext.getYukonUser());
        dashboard3.setUsers(0);
        dashboards.add(dashboard3);
        return dashboards;
    }
    
    public enum DashboardSortBy implements DisplayableEnum {

        name,
        createdBy,
        visibility,
        numberOfUsers;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dashboard." + name();
        }
    }

}
