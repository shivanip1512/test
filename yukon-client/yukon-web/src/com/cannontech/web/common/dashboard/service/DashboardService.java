package com.cannontech.web.common.dashboard.service;

import java.util.List;
import java.util.Optional;

import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;

/**
 * This service handles dashboard create, update and delete actions, as well as miscellaneous actions relating to
 * dashboards.
 * @see com.cannontech.web.common.dashboard.widget.service.WidgetService WidgetService, used to create the widgets 
 * associated with dashboards.
 */
public interface DashboardService {
    
    /**
     * Get the user's default dashboard for the dashboard page type, or the system default if it hasn't been set.
     */
    Dashboard getAssignedDashboard(int userId, DashboardPageType dashboardType);
    
    /**
     * Get a dashboard by its ID.
     */
    Dashboard getDashboard(int dashboardId);
    
    /**
     * Get a list of dashboards owned by the user.
     */
    List<LiteDashboard> getOwnedDashboards(int userId);
    
    /**
     * Get a list of dashboards that are visible to the specified user.
     * The dashboard visibility = PUBLIC or SYSTEM or the user is the owner.
     */
    List<LiteDashboard> getVisible(int userId);
    
    /**
     * Get a list of all dashboards with no owner assigned.
     */
    List<LiteDashboard> getAllOwnerless();
    
    /**
     * Sets the default dashboard for the specified users and dashboard page type.
     */
    void setDefault(LiteYukonUser yukonUser, Iterable<Integer> userIds, DashboardPageType dashboardType, int dashboardId);
    
    /**
     * Removes the users from the specified dashboard.
     */
    void unassignDashboardFromUsers(LiteYukonUser yukonUser, Iterable<Integer> userIds, int dashboardId);
    
    /**
     * Tells the DashboardEventLogService which widgets have been added to the given dashboard.
     */
    void logWidgetsAddedOrRemoved(LiteYukonUser yukonUser, Dashboard existingDashboard, Dashboard newDashboard);
    
    /**
     * Tells the DashboardEventLogService that the given dashboard's details have been edited
     */
    void logDetailsEdited(LiteYukonUser yukonUser,
                       String oldName,
                       String oldDescription,
                       String newName,
                       String newDescription);
    
    /**
     * @return The ID of the newly created dashboard.
     */
    int create(DashboardBase dashboard) throws DuplicateException;
    
    /**
     * @return The ID of the modified dashboard.
     */
    int update(LiteYukonUser yukonUser, Dashboard dashboard);
    
    /**
     * Copy a dashboard, saving the new copy to the database with the specified owner.
     * @return the copy.
     * @throws DuplicateException if attempt to create a dashboard with the same name by the same user is
     *         made.
     */
    int copy(int dashboardId, String name, String description, Visibility visibility, int userId) throws DuplicateException;
    
    /**
     * Delete a dashboard.
     * @param userId TODO
     */
    void delete(LiteYukonUser yukonUser, int dashboardId);
    
    /**
     * Get the user that owns the specified dashboard.
     * @return the owner of the dashboard, or empty optional if the dashboard has no owner.
     */
    Optional<LiteYukonUser> getOwner(int dashboardId);

    /**
     * Returns true if dashboard visibility = PUBLIC or SYSTEM or the user is the owner.
     */
    boolean isVisible(int userId, int dashboardId);

    /**
     * Set a new owner for a dashboard.
     */
    void setOwner(int userId, int dashboardId);

    /**
     * Get all dashboards.
     */
    List<LiteDashboard> getDashboards();

    /**
     * Get users for a dashboard.
     */
    List<Integer> getAllUsersForDashboard(int dashboardId);    
}
