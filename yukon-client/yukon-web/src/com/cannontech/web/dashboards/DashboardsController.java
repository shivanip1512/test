package com.cannontech.web.dashboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import com.cannontech.web.common.dashboard.model.DashboardSetting;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.UserDashboardSettings;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.cannontech.web.common.dashboard.service.DashboardService;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/*")
public class DashboardsController {
    
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
        List<LiteDashboard> visibleDashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
        List<LiteDashboard> dashboards = visibleDashboards;
        if (filter != null && filter.equals(DashboardFilter.CREATEDBYME.name())) {
            dashboards = dashboardService.getOwnedDashboards(userContext.getYukonUser().getUserID());
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
        model.addAttribute("dashboardsList", visibleDashboards);
        
        UserDashboardSettings settings = new UserDashboardSettings();
        Arrays.asList(DashboardPageType.values()).forEach(pageType -> {
            DashboardSetting setting = new DashboardSetting();
            setting.setPageType(pageType);
            Dashboard assignedDashboard = dashboardService.getAssignedDashboard(userContext.getYukonUser().getUserID(), pageType);
            //TODO: this should be set to the default dashboard if the user has not yet assigned one
            if (assignedDashboard != null) {
                setting.setDashboardId(assignedDashboard.getDashboardId());
            }
            settings.addSetting(setting);
        });
        
        model.addAttribute("dashboardSettings", settings);
        
        return "manageDashboards.jsp";
    }
    
    @RequestMapping("saveSettings")
    public String saveSettings(@ModelAttribute UserDashboardSettings settings, YukonUserContext userContext, FlashScope flash) {
        settings.getSettings().forEach(setting -> {
            dashboardService.setDefault(Arrays.asList(userContext.getYukonUser().getUserID()), setting.getPageType(), setting.getDashboardId());
        });
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "saveSettings.success"));
        return "redirect:/dashboards/manage";
    }
    
    @RequestMapping("admin")
    public String adminDashboards(ModelMap model, YukonUserContext userContext) {
        //TODO: Should this return all dashboards?
        List<LiteDashboard> dashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
        model.addAttribute("dashboards", dashboards);
        model.addAttribute("ownerlessDashboards", dashboardService.getAllOwnerless());
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
        List<LiteDashboard> dashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
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
    public String editDetails(@PathVariable int id, ModelMap model, YukonUserContext userContext, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (isDashboardOwner(userContext, dashboard)) {
            model.addAttribute("mode", PageEditMode.EDIT);
            List<Visibility> reducedVisibility = new ArrayList<>();
            List<Visibility> visibilityOptions = Arrays.asList(Visibility.values());
            if (dashboardDao.getAllUsersForDashboard(id).size() >= 1) {
                //only allow making visibility more visible if there are users
                List<Visibility> reversedOptions = Lists.reverse(visibilityOptions);
                for (Visibility visibility : reversedOptions) {
                    if (visibility == dashboard.getVisibility()) {
                        reducedVisibility.add(visibility);
                        break;
                    }
                    reducedVisibility.add(visibility);
                }
                visibilityOptions = reducedVisibility;
            }
            model.addAttribute("visibilityOptions", visibilityOptions);
            List<LiteDashboard> dashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
            model.addAttribute("dashboards", dashboards);
            model.addAttribute("dashboard", dashboard);
            return "dashboardDetails.jsp";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }

    }
    
    @RequestMapping("{id}/view")
    public String viewDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (dashboardService.isVisible(userContext.getYukonUser().getUserID(), id)) {
            model.addAttribute("mode", PageEditMode.VIEW);
            model.addAttribute("dashboard", dashboard);
            List<LiteDashboard> ownedDashboards = dashboardService.getOwnedDashboards(userContext.getYukonUser().getUserID());
            model.addAttribute("ownedDashboards", ownedDashboards);
            return "dashboardView.jsp";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "view.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/edit")
    public String editDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (isDashboardOwner(userContext, dashboard)) {
            model.addAttribute("mode", PageEditMode.EDIT);
            model.addAttribute("dashboard", dashboard);
            return "dashboardEdit.jsp";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/addWidgets")
    public String addWidgets(@PathVariable int id, ModelMap model, YukonUserContext userContext, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (isDashboardOwner(userContext, dashboard)) {
            model.addAttribute("widgetMap", widgetService.getTypesByCategory());
            model.addAttribute("totalWidgets", WidgetType.values().length);
            return "addWidgets.jsp";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/addWidget/{type}")
    public String addWidgetType(@PathVariable int id, @PathVariable String type, ModelMap model, YukonUserContext userContext, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (isDashboardOwner(userContext, dashboard)) {
            WidgetType widgetType = WidgetType.valueOf(type);
            Widget widget = new Widget();
            widget.setType(widgetType);
            model.addAttribute("widget", widget);
            model.addAttribute("dashboard", dashboard);
            return "widgetAddRow.jsp";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }
    }

    
    @RequestMapping("saveDetails")
    public String saveDetails(@ModelAttribute Dashboard dashboard, YukonUserContext userContext, 
                              BindingResult result, ModelMap model, HttpServletResponse resp, FlashScope flash) {
        Dashboard existingDashboard = dashboardService.getDashboard(dashboard.getDashboardId());
        if (isDashboardOwner(userContext, existingDashboard)) {
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
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("save")
    public String saveDashboard(@ModelAttribute Dashboard dashboard, YukonUserContext userContext, FlashScope flash) {
        Dashboard existingDashboard = dashboardService.getDashboard(dashboard.getDashboardId());
        if (isDashboardOwner(userContext, existingDashboard)) {
            dashboard.setOwner(userContext.getYukonUser());
            int id = dashboardService.update(dashboard);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success"));
            return "redirect:/dashboards/" + id + "/view";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }
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
    
    private boolean isDashboardOwner(YukonUserContext userContext, Dashboard dashboard) {
        return (userContext.getYukonUser().getUserID() == dashboard.getOwner().getUserID());
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
        CREATEDBYME;
    }

}
