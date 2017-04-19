package com.cannontech.web.common.dashboard.dao;

import java.util.List;

import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.Widget;

/**
 * Dao for saving and retrieving information about dashboards and widgets.
 */
public interface DashboardDao {
    /**
     * Delete from db UserDaboard table db user relationship.
     * If user is owner update Dashboard table to set owner to null if there are other users who are using it. (this part should be done in the controller method)
     */
    void deleteUserDashboard(int userId, int dashboardId);
    
    /**
     * Deletes a dashboard.
     */
    void deleteDashboard(int dashboardId);
    
    /**
     * Obtain UserIds that are using Dashboard.
     */
    List<Integer> getAllUsersForDashboard(int dashboardId);
    
    /**
     * Creates a dashboard. Any widgets associated with the dashboard will need to be added separately, via 
     * <code>insertWidgets</code>.
     * @return The id of the newly created dashboard.
     */
    public int create(DashboardBase dashboard);
    
    /**
     * Inserts widgets for the specified dashboard.
     */
    public void insertWidgets(int dashboardId, List<Widget> widgets, int column);
}
