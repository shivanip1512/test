package com.cannontech.web.stars.dr.operator.inventory;

public class SaveToBatchInfo {
    private int routeId;
    private int groupId;
    private int ecDefaultRoute;
    private String useRoutes;
    private String useGroups;
    
    public int getRouteId() {
        return routeId;
    }
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    public int getEcDefaultRoute() {
        return ecDefaultRoute;
    }
    public void setEcDefaultRoute(int ecDefaultRoute) {
        this.ecDefaultRoute = ecDefaultRoute;
    }
    public String getUseRoutes() {
        return useRoutes;
    }
    public void setUseRoutes(String useRoutes) {
        this.useRoutes = useRoutes;
    }
    public String getUseGroups() {
        return useGroups;
    }
    public void setUseGroups(String useGroups) {
        this.useGroups = useGroups;
    }
}
