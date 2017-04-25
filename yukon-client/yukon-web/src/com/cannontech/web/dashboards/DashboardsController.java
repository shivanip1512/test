package com.cannontech.web.dashboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.cannontech.common.util.JsonUtils;
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
import com.cannontech.web.common.dashboard.model.WidgetCategory;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.cannontech.web.common.dashboard.service.DashboardService;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/*")
public class DashboardsController {
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private WidgetService widgetService;
    @Autowired private DashboardService dashboardService;
    @Autowired private DashboardDao dashboardDao;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DashboardValidator validator;

    private final static String baseKey = "yukon.web.modules.dashboard.";

    @RequestMapping("manage")
    public String manageDashboards(ModelMap model, @RequestParam(value="filter", required=false) String filter, YukonUserContext userContext, 
                                   @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, PagingParameters paging) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("pageTypes", DashboardPageType.values());
        List<LiteDashboard> dashboards = createDashboardsListMockup(userContext);
        //List<LiteDashboard> dashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
        if (filter != null) {
            if (filter.equals(DashboardFilter.MYFAVORITES.name())) {
                dashboards = dashboards.subList(0, 1);
                //dashboards = dashboardService.getFavorites(userContext.getYukonUser().getUserID());
            } else if (filter.equals(DashboardFilter.CREATEDBYME.name())) {
                dashboards = dashboards.subList(0, 2);
                //dashboards = dashboardService.getOwnedDashboards(userContext.getYukonUser().getUserID());
            }
        }
        model.addAttribute("filter", filter);

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
    public String saveSettings(@ModelAttribute UserDashboardSettings settings, YukonUserContext userContext, FlashScope flash) {
        settings.getSettings().forEach(setting -> {
            //dashboardService.setDefault(Arrays.asList(userContext.getYukonUser().getUserID()), setting.getPageType(), setting.getDashboardId());
        });
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "saveSettings.success"));
        return "redirect:/dashboards/manage";
    }
    
    @RequestMapping("admin")
    public String adminDashboards(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        return "dashboardAdmin.jsp";
    }
    
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createDialog(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.CREATE);
        setupDashboardDetailsModel(model, userContext);
        model.addAttribute("dashboard", new Dashboard());
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/copy")
    public String copyDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.CREATE);
        setupDashboardDetailsModel(model, userContext);
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardId(id);
        model.addAttribute("dashboard", dashboard);
        return "dashboardDetails.jsp";
    }
    
    private void setupDashboardDetailsModel(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("visibilityOptions", Visibility.values());
        //List<LiteDashboard> dashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
        List<LiteDashboard> dashboards = createDashboardsListMockup(userContext);
        model.addAttribute("dashboards", dashboards);
    }
    
    @RequestMapping(value="create", method=RequestMethod.POST)
    public String createDashboard(@ModelAttribute Dashboard dashboard, YukonUserContext userContext, ModelMap model,
                                  FlashScope flash, BindingResult result, HttpServletResponse resp) {
        int id = dashboard.getDashboardId();
        validator.validate(dashboard, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            setupDashboardDetailsModel(model, userContext);
            return "dashboardDetails.jsp";
        }
        if (id > 0) {
            Dashboard copyDashboard = dashboardService.copy(dashboard.getDashboardId(), userContext.getYukonUser().getUserID());
            copyDashboard.setName(dashboard.getName());
            copyDashboard.setDescription(dashboard.getDescription());
            copyDashboard.setVisibility(dashboard.getVisibility());
            id = dashboardService.update(copyDashboard);
        } else {
            dashboard.setOwner(userContext.getYukonUser());
            id = dashboardService.create(dashboard);
        }
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("dashboardId", id);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "create.success"));
        return JsonUtils.writeResponse(resp, json);
    }
    
    @RequestMapping("{id}/editDetails")
    public String editDetails(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        setupDashboardDetailsModel(model, userContext);
        Dashboard dashboard = dashboardService.getDashboard(id);
        model.addAttribute("dashboard", dashboard);
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/view")
    public String viewDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.VIEW);
        Dashboard dashboard = dashboardService.getDashboard(id);
        model.addAttribute("dashboard", dashboard);
        return "dashboardView.jsp";
    }
    
    @RequestMapping("{id}/edit")
    public String editDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        Dashboard dashboard = dashboardService.getDashboard(id);
        model.addAttribute("dashboard", dashboard);
        return "dashboardEdit.jsp";
    }
    
    @RequestMapping("{id}/addWidgets")
    public String addWidgets(@PathVariable int id, ModelMap model) {
        Map<WidgetCategory, Collection<WidgetType>> widgetMap = widgetService.getTypesByCategory().asMap();
        List<WidgetCategory> categories = Lists.newArrayList(widgetMap.keySet());
        Collections.sort(categories);
        Map<WidgetCategory, List<WidgetType>> realMap = Maps.newLinkedHashMap();
        categories.forEach(category -> realMap.put(category,  Lists.newArrayList(widgetMap.get(category))));
        model.addAttribute("widgetMap", realMap);
        model.addAttribute("totalWidgets", WidgetType.values().length);
        return "addWidgets.jsp";
    }
    
    @RequestMapping("{id}/addWidget/{type}")
    public String addWidgetType(@PathVariable int id, @PathVariable String type, ModelMap model, YukonUserContext userContext) {
        WidgetType widgetType = WidgetType.valueOf(type);
        Widget widget = new Widget();
        widget.setType(widgetType);
        model.addAttribute("widget", widget);
        Dashboard dashboard = dashboardService.getDashboard(id);
        model.addAttribute("dashboard", dashboard);
        return "widgetAddRow.jsp";
    }

    
    @RequestMapping("saveDetails")
    public String saveDetails(@ModelAttribute Dashboard dashboard, YukonUserContext userContext, 
                              BindingResult result, ModelMap model, HttpServletResponse resp, FlashScope flash) {
        Dashboard existingDashboard = dashboardService.getDashboard(dashboard.getDashboardId());
        dashboard.setColumn1Widgets(existingDashboard.getColumn1Widgets());
        dashboard.setColumn2Widgets(existingDashboard.getColumn2Widgets());
        validator.validate(dashboard, result);
        if (result.hasErrors()) {
            model.addAttribute("mode", PageEditMode.EDIT);
            setupDashboardDetailsModel(model, userContext);
            return "dashboardDetails.jsp";
        }
        dashboard.setOwner(userContext.getYukonUser());
        int id = dashboardService.update(dashboard);
        
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("dashboardId", id);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success"));
        return JsonUtils.writeResponse(resp, json);
    }
    
    @RequestMapping("save")
    public String saveDashboard(@ModelAttribute Dashboard dashboard, YukonUserContext userContext, FlashScope flash) {
        dashboard.setOwner(userContext.getYukonUser());
        int id = dashboardService.update(dashboard);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success"));
        return "redirect:/dashboards/" + id + "/view";
    }
    
    @RequestMapping("{id}/delete")
    public String deleteDashboard(FlashScope flash, @PathVariable int id, LiteYukonUser user) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        LiteYukonUser owner = dashboard.getOwner();
        if (dashboard.getVisibility() == Visibility.SYSTEM) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.exception.system", dashboard.getName()));
        } else if (dashboardDao.getAllUsersForDashboard(id).size() > 1) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.exception.currentInUse", dashboard.getName()));
        } else if (owner == null || owner.getUserID() == user.getUserID()) {
            dashboardService.delete(id);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", dashboard.getName()));
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.exception.notOwner", dashboard.getName()));
        }
        return "redirect:/dashboards/manage";
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
            return baseKey + name();
        }
    }
    
    public enum DashboardFilter {
        ALL,
        MYFAVORITES,
        CREATEDBYME;
    }

}
