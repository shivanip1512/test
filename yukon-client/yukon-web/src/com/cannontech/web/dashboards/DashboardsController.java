package com.cannontech.web.dashboards;

import java.util.ArrayList;
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

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.model.WidgetType;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/*")
public class DashboardsController {
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private WidgetService widgetService;
    
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
        model.addAttribute("dashboard", createDashboardMockup(userContext));
        return "dashboardView.jsp";
    }
    
    @RequestMapping("{id}/edit")
    public String editDashboard(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        model.addAttribute("dashboard", createDashboardMockup(userContext));
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
    
    private Dashboard createDashboardMockup(YukonUserContext userContext) {
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Utility Company Sample Dashboard");
        dashboard.setDescription("Utility Company Default Test Dashboard");
        dashboard.setVisibility(Visibility.SHARED);
        dashboard.setOwner(userContext.getYukonUser());
        dashboard.setPageType(DashboardPageType.MAIN);
        dashboard.setId(12);
        dashboard.setPageType(DashboardPageType.MAIN);
        Widget widget = new Widget();
        widget.setDashboardId(12);
        widget.setId(11);
        widget.setType(WidgetType.MONITOR_SUBSCRIPTIONS);
        Widget widget2 = new Widget();
        widget2.setDashboardId(12);
        widget2.setId(12);
        widget2.setType(WidgetType.TREND);
        Map<String, String> params = new HashMap<String, String>();
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
        Map<String, String> params2 = new HashMap<String, String>();
        SimpleMeter meter2 = meterIterator.next();
        params2.put("deviceId",  Integer.toString(meter2.getPaoIdentifier().getPaoId()));
        widget3.setParameters(params2);
        widget3.setId(21);
        dashboard.addColumn2Widget(widget3);
        return dashboard;
    }
    
    private List<LiteDashboard> createDashboardsListMockup(YukonUserContext userContext) {
        List<LiteDashboard> dashboards = new ArrayList<LiteDashboard>();
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

}
