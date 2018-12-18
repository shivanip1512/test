package com.cannontech.web.dashboards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.cannontech.common.exception.DisplayableException;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
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
import com.cannontech.web.common.dashboard.model.WidgetCategory;
import com.cannontech.web.common.dashboard.model.WidgetHelper;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.cannontech.web.common.dashboard.service.DashboardService;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/*")
public class DashboardsController {
    
    @Autowired private WidgetService widgetService;
    @Autowired private DashboardService dashboardService;
    @Autowired private DashboardDao dashboardDao;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DashboardValidator validator;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private WidgetHelper widgetHelper;

    private final static String baseKey = "yukon.web.modules.dashboard.";

    @RequestMapping("manage")
    public String manageDashboards(ModelMap model, @RequestParam(value="filter", required=false) String filter, YukonUserContext userContext, 
                                   @DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, PagingParameters paging) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("pageTypes", DashboardPageType.values());
        List<LiteDashboard> visibleDashboards = dashboardService.getVisible(userContext.getYukonUser().getUserID());
        List<LiteDashboard> dashboards = visibleDashboards;
        if (filter != null && filter.equals(DashboardFilter.OWNEDBYME.name())) {
            dashboards = dashboardService.getOwnedDashboards(userContext.getYukonUser().getUserID());
        }

        model.addAttribute("filter", filter);
        
        setupDashboardsTable(dashboards, sorting, paging, model, accessor);
        Collections.sort(visibleDashboards);
        model.addAttribute("dashboardsList", visibleDashboards);
        
        UserDashboardSettings settings = new UserDashboardSettings();
        Arrays.asList(DashboardPageType.values()).forEach(pageType -> {
            DashboardSetting setting = new DashboardSetting();
            setting.setPageType(pageType);
            Dashboard assignedDashboard = dashboardService.getAssignedDashboard(userContext.getYukonUser().getUserID(), pageType);
            if (assignedDashboard != null) {
                setting.setDashboardId(assignedDashboard.getDashboardId());
            }
            settings.addSetting(setting);
        });
        
        model.addAttribute("dashboardSettings", settings);
        
        return "manageDashboards.jsp";
    }
    
    private void setupDashboardsTable(List<LiteDashboard> dashboards, SortingParameters sorting, PagingParameters paging, 
                                      ModelMap model, MessageSourceAccessor accessor) {

        SearchResults<LiteDashboard> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, dashboards.size());
        
        DashboardSortBy sortBy = DashboardSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        List<LiteDashboard>itemList = Lists.newArrayList(dashboards);
        
        Comparator<LiteDashboard> comparator = (o1, o2) -> o1.getName().compareTo(o2.getName());
        if (sortBy == DashboardSortBy.owner) {
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
    }

    @RequestMapping("saveSettings")
    public String saveSettings(@ModelAttribute UserDashboardSettings settings, LiteYukonUser yukonUser, FlashScope flash) {
        settings.getSettings().forEach(setting -> {
            dashboardService.setDefault(yukonUser, Arrays.asList(yukonUser.getUserID()), setting.getPageType(), setting.getDashboardId());
        });
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "saveSettings.success"));
        return "redirect:/dashboards/manage";
    }
    
    @RequestMapping("admin")
    @CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS)
    public String adminDashboards(@DefaultSort(dir=Direction.asc, sort="name") SortingParameters sorting, PagingParameters paging, 
                                  ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<LiteDashboard> dashboards = dashboardService.getDashboards();
        setupDashboardsTable(dashboards, sorting, paging, model, accessor);
        return "dashboardAdmin.jsp";
    }
    
    @RequestMapping("{id}/changeOwner/{userId}")
    @CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS)
    public String changeOwner(@PathVariable int id, @PathVariable int userId, ModelMap model, FlashScope flash, HttpServletResponse resp) {
        try {
            dashboardService.setOwner(userId, id);
        } catch (DisplayableException e) {
            flash.setError(e.getMessageSourceResolvable());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "dashboardAdmin.jsp";
        }
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "changeOwner.success"));
        return "redirect:/dashboards/admin";
    }
    
    @RequestMapping("{id}/assignUsers")
    @CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS)
    public String assignUsersDialog(@PathVariable int id, ModelMap model) {
        List<LiteDashboard> dashboards = dashboardService.getDashboards();
        Collections.sort(dashboards);
        model.addAttribute("dashboards", dashboards);
        model.addAttribute("dashboardId", id);
        model.addAttribute("pageTypes", DashboardPageType.values());
        return "assignUsers.jsp";
    }
    
    @RequestMapping(value="{id}/assignUsers", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS)
    public void assignUsers(@PathVariable int id, @RequestParam(value="users[]", required=false) Integer[] users, 
                              @RequestParam("pageType") String pageType, 
                              FlashScope flash, HttpServletResponse resp, LiteYukonUser yukonUser) {
        DashboardPageType dashboardType = DashboardPageType.valueOf(pageType);
        if (users != null) {
            dashboardService.setDefault(yukonUser, Arrays.asList(users), dashboardType, id);
        }
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "assignUsers.success"));
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping(value="{id}/unassignUsers", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS)
    public void unassignUsers(@PathVariable int id, @RequestParam(value="users[]", required=false) Integer[] users, 
                              FlashScope flash, HttpServletResponse resp, LiteYukonUser yukonUser) {
        if (users != null) {
            dashboardService.unassignDashboardFromUsers(yukonUser, Arrays.asList(users), id);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "unassignUsers.success"));
        }
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping(value="create", method=RequestMethod.GET)
    public String createDialog(ModelMap model, LiteYukonUser yukonUser) {
        model.addAttribute("mode", PageEditMode.CREATE);
        setupDashboardDetailsModel(model, yukonUser);
        model.addAttribute("dashboard", new Dashboard());
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/copy")
    public String copyDashboard(@PathVariable int id, ModelMap model, LiteYukonUser yukonUser) {
        model.addAttribute("mode", PageEditMode.CREATE);
        setupDashboardDetailsModel(model, yukonUser);
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardId(id);
        model.addAttribute("dashboard", dashboard);
        return "dashboardDetails.jsp";
    }
    
    private void setupDashboardDetailsModel(ModelMap model, LiteYukonUser yukonUser) {
        model.addAttribute("visibilityOptions", Visibility.values());
        List<LiteDashboard> dashboards = dashboardService.getVisible(yukonUser.getUserID());
        model.addAttribute("dashboards", dashboards);
    }
    
    @RequestMapping(value="create", method=RequestMethod.POST)
    public String createDashboard(@ModelAttribute Dashboard dashboard, LiteYukonUser yukonUser, ModelMap model,
                                  FlashScope flash, BindingResult result, HttpServletResponse resp) {
        int id = dashboard.getDashboardId();
        validator.validate(dashboard, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            setupDashboardDetailsModel(model, yukonUser);
            return "dashboardDetails.jsp";
        }
        try {
            if (id != 0) {
                id = dashboardService.copy(dashboard.getDashboardId(), dashboard.getName(), dashboard.getDescription(),
                    dashboard.getVisibility(), yukonUser.getUserID());
            } else {
                dashboard.setOwner(yukonUser);
                id = dashboardService.create(dashboard);
            }
        } catch (DuplicateException e) {
            result.rejectValue("name", "yukon.web.error.nameConflict");
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            setupDashboardDetailsModel(model, yukonUser);
            return "dashboardDetails.jsp";
        } catch (DisplayableException e) {
            flash.setError(e.getMessageSourceResolvable());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            setupDashboardDetailsModel(model, yukonUser);
            return "dashboardDetails.jsp";
        }
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("dashboardId", id);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "create.success"));
        return JsonUtils.writeResponse(resp, json);
    }
    
    @RequestMapping("{id}/editDetails")
    public String editDetails(@PathVariable int id, ModelMap model, LiteYukonUser yukonUser, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (canUserEditDashboard(yukonUser, dashboard, flash)) {
            model.addAttribute("mode", PageEditMode.EDIT);
            List<Visibility> reducedVisibility = new ArrayList<>();
            List<Visibility> visibilityOptions = Arrays.asList(Visibility.values());
            if (hasOtherUsers(yukonUser, id)) {
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
            List<LiteDashboard> dashboards = dashboardService.getVisible(yukonUser.getUserID());
            model.addAttribute("dashboards", dashboards);
            model.addAttribute("dashboard", dashboard);
            return "dashboardDetails.jsp";
        } else {
            return "redirect:/dashboards/manage";
        }

    }
    
    @RequestMapping(value = "{id}/view", method = RequestMethod.GET)
    public String viewDashboard(@PathVariable int id,
            @RequestParam(value = "dashboardPageType", required = false) DashboardPageType dashboardPageType,
            ModelMap model, LiteYukonUser yukonUser, FlashScope flash, YukonUserContext yukonUserContext) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        boolean adminDashboards = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS, yukonUser);
        if (adminDashboards || dashboardService.isVisible(yukonUser.getUserID(), id)) {
            model.addAttribute("mode", PageEditMode.VIEW);
            model.addAttribute("dashboard", dashboard);
            model.addAttribute("dashboardPageType", dashboardPageType);
            Set<String> widgetJavascript = dashboard.getAllWidgets()
                                                    .stream()
                                                    .map(widget -> widget.getType().getRequiredJavascript())
                                                    .flatMap(jsSet -> jsSet.stream())
                                                    .collect(Collectors.toSet());
            Set<String> widgetCss = dashboard.getAllWidgets()
                                              .stream()
                                              .map(widget -> widget.getType().getRequiredCss())
                                              .flatMap(cssSet -> cssSet.stream())
                                              .collect(Collectors.toSet());
            model.addAttribute("widgetJavascript", widgetJavascript);
            model.addAttribute("widgetCss", widgetCss);
            List<LiteDashboard> ownedDashboards = dashboardService.getOwnedDashboards(yukonUser.getUserID());
            Collections.sort(ownedDashboards);
            widgetHelper.setWidgetHelpTextArguments(dashboard.getAllWidgets(), yukonUserContext);
            model.addAttribute("ownedDashboards", ownedDashboards);
            return "dashboardView.jsp";
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "view.exception.notOwner", dashboard.getName()));
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/edit")
    public String editDashboard(@PathVariable int id, ModelMap model, LiteYukonUser yukonUser, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (canUserEditDashboard(yukonUser, dashboard, flash)) {
            model.addAttribute("mode", PageEditMode.EDIT);
            model.addAttribute("dashboard", dashboard);
            return "dashboardEdit.jsp";
        } else {
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/addWidgets")
    public String addWidgets(@PathVariable int id, ModelMap model, LiteYukonUser yukonUser, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (canUserEditDashboard(yukonUser, dashboard, flash)) {
            Map<WidgetCategory, List<WidgetType>> widgetMap = widgetService.getTypesByCategory(yukonUser);
            long widgetCount = widgetMap.entrySet()
                                         .stream()
                                         .flatMap(e -> e.getValue().stream())
                                         .count();
            model.addAttribute("widgetMap", widgetMap);
            model.addAttribute("totalWidgets", widgetCount);
            return "addWidgets.jsp";
        } else {
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/addWidget/{type}")
    public String addWidgetType(@PathVariable int id, @PathVariable String type, ModelMap model, LiteYukonUser yukonUser, FlashScope flash) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        if (canUserEditDashboard(yukonUser, dashboard, flash)) {
            WidgetType widgetType = WidgetType.valueOf(type);
            Widget widget = new Widget();
            widget.setType(widgetType);
            widget.setParameters(widgetService.setDefaultParameters(widgetType, new HashMap<String, String>()));
            model.addAttribute("widget", widget);
            model.addAttribute("dashboard", dashboard);
            return "widgetAddRow.jsp";
        } else {
            return "redirect:/dashboards/manage";
        }
    }

    
    @RequestMapping("saveDetails")
    public String saveDetails(@ModelAttribute Dashboard dashboard, LiteYukonUser yukonUser, 
                              BindingResult result, ModelMap model, HttpServletResponse resp, FlashScope flash) {
        Dashboard existingDashboard = dashboardService.getDashboard(dashboard.getDashboardId());
        if (canUserEditDashboard(yukonUser, existingDashboard, flash)) {
            dashboard.setColumn1Widgets(existingDashboard.getColumn1Widgets());
            dashboard.setColumn2Widgets(existingDashboard.getColumn2Widgets());
            validator.validate(dashboard, result);
            if (result.hasErrors()) {
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                model.addAttribute("mode", PageEditMode.EDIT);
                setupDashboardDetailsModel(model, yukonUser);
                return "dashboardDetails.jsp";
            }
            
            try {dashboard.setOwner(yukonUser);
                int id = dashboardService.update(yukonUser, dashboard);
                
                // Success
                model.clear();
                Map<String, Object> json = new HashMap<>();
                json.put("dashboardId", id);
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success"));
                return JsonUtils.writeResponse(resp, json);
            } catch (DuplicateException e) {
                result.rejectValue("name", "yukon.web.error.nameConflict");
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                model.addAttribute("mode", PageEditMode.EDIT);
                setupDashboardDetailsModel(model, yukonUser);
                return "dashboardDetails.jsp";
            }catch (DisplayableException e) {
                flash.setError(e.getMessageSourceResolvable());
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                model.addAttribute("mode", PageEditMode.EDIT);
                setupDashboardDetailsModel(model, yukonUser);
                return "dashboardDetails.jsp";
            }
        } else {
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("save")
    public String saveDashboard(@ModelAttribute Dashboard dashboard, LiteYukonUser yukonUser, ModelMap model,
            FlashScope flash, HttpServletResponse resp) {
        Dashboard existingDashboard = dashboardService.getDashboard(dashboard.getDashboardId());
        if (canUserEditDashboard(yukonUser, existingDashboard, flash)) {
            try {
                dashboard.setOwner(yukonUser);
                int id = dashboardService.update(yukonUser, dashboard);
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success"));
                return "redirect:/dashboards/" + id + "/view";
            } catch (DisplayableException e) {
                flash.setError(e.getMessageSourceResolvable());
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                model.addAttribute("mode", PageEditMode.EDIT);
                setupDashboardDetailsModel(model, yukonUser);
                return "dashboardEdit.jsp";
            }
        } else {
            return "redirect:/dashboards/manage";
        }
    }
    
    @RequestMapping("{id}/delete")
    public void deleteDashboard(FlashScope flash, @PathVariable int id, LiteYukonUser yukonUser, HttpServletResponse resp) {
        Dashboard dashboard = dashboardService.getDashboard(id);
        LiteYukonUser owner = dashboard.getOwner();
        if (isSystemDashboard(dashboard)) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.exception.system", dashboard.getName()));
        } else if (hasOtherUsers(yukonUser, id)) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.exception.currentInUse", dashboard.getName()));
        } else if (owner == null || isDashboardOwner(yukonUser, dashboard)) {
            dashboardService.delete(yukonUser, id);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", dashboard.getName()));
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.exception.notOwner", dashboard.getName()));
        }
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    private boolean isSystemDashboard(Dashboard dashboard) {
        return dashboard.getVisibility() == Visibility.SYSTEM;
    }

    private boolean isDashboardOwner(LiteYukonUser yukonUser, Dashboard dashboard) {
        return (yukonUser.getUserID() == dashboard.getOwner().getUserID());
    }
    
    private boolean hasOtherUsers(LiteYukonUser yukonUser, int dashboardId) {
        List<Integer> dashboardUsers = dashboardDao.getAllUsersForDashboard(dashboardId);
        return !(dashboardUsers.size() == 0 || (dashboardUsers.size() == 1 && dashboardUsers.get(0) == yukonUser.getUserID()));
    }
    
    private boolean canUserEditDashboard(LiteYukonUser yukonUser, Dashboard dashboard, FlashScope flash) {
        if (isSystemDashboard(dashboard)){
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.system", dashboard.getName()));
            return false;
        }
        if (!isDashboardOwner(yukonUser, dashboard)){
            flash.setError(new YukonMessageSourceResolvable(baseKey + "edit.exception.notOwner", dashboard.getName()));
            return false;
        }
        return true;
    }
    
    public enum DashboardSortBy implements DisplayableEnum {

        name,
        owner,
        visibility,
        numberOfUsers;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
    
    public enum DashboardFilter {
        ALL,
        OWNEDBYME;
    }

}
