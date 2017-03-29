package com.cannontech.web.dashboards;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetCategory;
import com.cannontech.web.common.dashboard.model.WidgetType;

@Controller
@RequestMapping("/*")
public class DashboardsController {
    
    @RequestMapping("manage")
    public String manageDashboards(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        return "manageDashboards.jsp";
    }
    
    @RequestMapping("admin")
    public String adminDashboards(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        return "dashboardAdmin.jsp";
    }
    
    @RequestMapping("create")
    public String createDashboard(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("visibilityOptions", Visibility.values());
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/view")
    public String viewDashboard(@PathVariable int id, ModelMap model) {
        model.addAttribute("mode", PageEditMode.VIEW);
        model.addAttribute("dashboard", createDashboardMockup());
        return "dashboardView.jsp";
    }
    
    @RequestMapping("{id}/edit")
    public String editDashboard(@PathVariable int id, ModelMap model) {
        model.addAttribute("mode", PageEditMode.EDIT);
        model.addAttribute("dashboard", createDashboardMockup());
        return "dashboardEdit.jsp";
    }
    
    @RequestMapping("{id}/addWidgets")
    public String addWidgets(@PathVariable int id, ModelMap model) {
        model.addAttribute("widgetTypes", WidgetType.values());
        model.addAttribute("widgetCategories", WidgetCategory.values());
        return "addWidgets.jsp";
    }
    
    @RequestMapping("save")
    public String saveDashboard(@ModelAttribute Dashboard dashboard) {
        return "manageDashboards.jsp";
    }
    
    @RequestMapping("{id}/copy")
    public String copyDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("visibilityOptions", Visibility.values());
        model.addAttribute("dashboards", createDashboardsListMockup(userContext));
        return "dashboardDetails.jsp";
    }
    
    @RequestMapping("{id}/delete")
    public String deleteDashboard(@PathVariable int id) {
        return "manageDashboards.jsp";
    }
    
    @RequestMapping("{id}/favorite")
    public String favoriteDashboard(@PathVariable int id, ModelMap model) {
        return "manageDashboards.jsp";
    }
    
    @RequestMapping("{id}/unfavorite")
    public String unfavoriteDashboard(@PathVariable int id, ModelMap model) {
        return "manageDashboards.jsp";
    }
    
    private Dashboard createDashboardMockup() {
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Yukon Default Dashboard");
        dashboard.setId(12);
        dashboard.setPageType(DashboardPageType.MAIN);
        Widget widget = new Widget();
        widget.setType(WidgetType.MONITOR_SUBSCRIPTIONS);
        dashboard.addColumn1Widget(widget);
        return dashboard;
    }
    
    private List<Dashboard> createDashboardsListMockup(YukonUserContext userContext) {
        List<Dashboard> dashboards = new ArrayList<Dashboard>();
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Yukon Default Dashboard");
        dashboard.setId(12);
        dashboard.setPageType(DashboardPageType.MAIN);
        dashboard.setVisibility(Visibility.SYSTEM);
        dashboard.setOwner(userContext.getYukonUser());
        dashboards.add(dashboard);
        Dashboard dashboard2 = new Dashboard();
        dashboard2.setName("Utility Company Sample Dashboard");
        dashboard2.setId(13);
        dashboard2.setPageType(DashboardPageType.MAIN);
        dashboard2.setVisibility(Visibility.SHARED);
        dashboard2.setOwner(userContext.getYukonUser());
        dashboards.add(dashboard2);
        Dashboard dashboard3 = new Dashboard();
        dashboard3.setName("User Specific Dashboard");
        dashboard3.setId(14);
        dashboard3.setPageType(DashboardPageType.MAIN);
        dashboard3.setVisibility(Visibility.PRIVATE);
        dashboard3.setOwner(userContext.getYukonUser());
        dashboards.add(dashboard3);
        return dashboards;
    }

}
