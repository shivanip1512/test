package com.cannontech.web.common.dashboard.dao;

import java.util.List;

import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.model.Widget;
import com.google.common.collect.ListMultimap;

/**
 * Dao for saving and retrieving information about dashboards and widgets.
 */
public interface DashboardDao {
    
    /**
     * Retrieves the dashboard with the specified ID, and all of its associated widgets.
     */
    Dashboard getDashboard(int dashboardId);
    
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
    int create(DashboardBase dashboard);
    
    /**
     * Inserts widgets for the specified dashboard.
     */
    void insertWidgets(int dashboardId, List<Widget> widgets, int column);

    /**
     * Assigns users to dashboard.
     */
    void assignDashboard(Iterable<Integer> userIds, DashboardPageType dashboardType, int dashboardId);

    /**
     * Unassigns users from dashboard.
     */
    void unassignDashboard(Iterable<Integer> userIds, DashboardPageType dashboardType);

    /**
     * Retrieves dashboard for userId and dashboard type.
     */
    Dashboard getDashboard(int userId, DashboardPageType dashboardType);

    /**
     * Retrieves dashboards that have a owner.
     */
    List<LiteDashboard> getOwnedDashboards(int ownerId);

    /**
     * Retrieves dashboards without an owner.
     */
    List<LiteDashboard> getAllOwnerless();

    /**
     * Retrieves dashboards by visibility and type.
     */
    Dashboard getDashboard(Visibility visibility, DashboardPageType dashboardType);

    /**
     * Retrieves dashboards by visibility.
     */
    List<LiteDashboard> getDashboardsByVisibility(Visibility... visibility);

    /**
     * Retrieves all dashboards.
     */
    List<LiteDashboard> getAllDashboards();

    /**
     * Retrieves map of users and all the page assignments for a specific dashboard.
     * @param dashboardId
     * @return
     */
    ListMultimap<DashboardPageType, Integer> getPageAssignmentToUserIdMap(int dashboardId);  
}
