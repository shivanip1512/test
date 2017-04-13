package com.cannontech.web.common.dashboard.dao;

import java.util.List;

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
     * deletes Dashboard.
     */
    void deleteDashboard(int dashboardId);
    
    /**
     * Obtain UserIds that are using Dashboard.
     */
    List<Integer> getAllUsersForDashboard(int dashboardId);
    


}
