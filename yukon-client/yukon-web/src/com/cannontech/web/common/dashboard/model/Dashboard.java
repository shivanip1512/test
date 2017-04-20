package com.cannontech.web.common.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * The complete representation of a dashboard, including associated widgets.
 * @see LiteDashboard LiteDashboard, a lighter dashboard object.
 */
public class Dashboard implements DashboardBase {
    private int dashboardId;
    private String name;
    private String description;
    private DashboardPageType pageType;
    private LiteYukonUser owner;
    private Visibility visibility;
    private List<Widget> column1Widgets = new ArrayList<>();;
    private List<Widget> column2Widgets = new ArrayList<>();;
    
    @Override
    public int getDashboardId() {
        return dashboardId;
    }
    
    @Override
    public void setDashboardId(int id) {
        dashboardId = id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public LiteYukonUser getOwner() {
        return owner;
    }
    
    @Override
    public void setOwner(LiteYukonUser owner) {
        this.owner = owner;
    }
    
    @Override
    public Visibility getVisibility() {
        return visibility;
    }
    
    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    public List<Widget> getColumn1Widgets() {
        return column1Widgets;
    }
    
    public void setColumn1Widgets(List<Widget> column1Widgets) {
        this.column1Widgets = new ArrayList<>(column1Widgets);
    }
    
    public void addColumn1Widget(Widget widget) {
        if (column1Widgets == null) {
            column1Widgets = new ArrayList<>();
        }
        column1Widgets.add(widget);
    }
    
    public List<Widget> getColumn2Widgets() {
        return column2Widgets;
    }
    
    public void setColumn2Widgets(List<Widget> column2Widgets) {
        this.column2Widgets = new ArrayList<>(column2Widgets);
    }
    
    public void addColumn2Widget(Widget widget) {
        if (column2Widgets == null) {
            column2Widgets = new ArrayList<>();
        }
        column2Widgets.add(widget);
    }
    
    public List<Widget> getAllWidgets() {
        List<Widget> allWidgets = new ArrayList<>();
        allWidgets.addAll(column1Widgets);
        allWidgets.addAll(column2Widgets);
        return allWidgets;
    }
    
    public DashboardPageType getPageType() {
        return pageType;
    }

    public void setPageType(DashboardPageType pageType) {
        this.pageType = pageType;
    }

    @Override
    public String toString() {
        return "Dashboard [dashboardId=" + dashboardId + ", name=" + name + ", description=" + description
               + ", pageType=" + pageType + ", owner=" + owner + ", visibility=" + visibility + ", column1Widgets="
               + column1Widgets + ", column2Widgets=" + column2Widgets + "]";
    }
    
}
