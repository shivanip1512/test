package com.cannontech.web.common.dashboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The complete representation of a dashboard, including associated widgets.
 * @see LiteDashboard LiteDashboard, a lighter dashboard object.
 */
public class Dashboard extends DashboardBase {

    private List<Widget> column1Widgets = new ArrayList<>();;
    private List<Widget> column2Widgets = new ArrayList<>();;
        
    public List<Widget> getColumn1Widgets() {
        return column1Widgets;
    }
    
    public void setColumn1Widgets(List<Widget> column1Widgets) {
        if (column1Widgets != null) {
            this.column1Widgets = new ArrayList<>(column1Widgets);
        }
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
        if (column2Widgets != null) {
            this.column2Widgets = new ArrayList<>(column2Widgets);
        }
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
    
    @Override
    public String toString() {
        return super.toString() + " [column1Widgets=" + column1Widgets + ", column2Widgets=" + column2Widgets + "]";
    }
}
