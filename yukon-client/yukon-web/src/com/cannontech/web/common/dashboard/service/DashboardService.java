package com.cannontech.web.common.dashboard.service;

import java.util.List;
import java.util.Optional;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;

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
     * Get a list of dashboards favorited by the user.
     */
    List<LiteDashboard> getFavorites(int userId);
    
    /**
     * Get a list of favorite dashboards for a particular page type.
     */
    List<LiteDashboard> getFavoritesForPage(int userId, DashboardPageType type);
    
    /**
     * Get a list of dashboards that are visible to the specified user.
     */
    List<LiteDashboard> getVisible(int userId);
    
    /**
     * Get a list of all dashboards with no owner assigned.
     */
    List<LiteDashboard> getAllOwnerless();
    
    /**
     * Sets the default dashboard for the specified users and dashboard page type.
     */
    void setDefault(Iterable<Integer> userIds, DashboardPageType dashboardType, int dashboardId);
    
    /**
     * Adds the dashboard to the user's favorites.
     */
    void favorite(int userId, int dashboardId);
    
    /**
     * Removes the dashboard from the user's favorites.
     */
    void unfavorite(int userId, int dashboardId);
    
    /**
     * @return The ID of the newly created dashboard.
     */
    int create(DashboardBase dashboard);
    
    /**
     * @return The ID of the modified dashboard.
     */
    int update(Dashboard dashboard);
    
    /**
     * Copy a dashboard, saving the new copy to the database with the specified owner.
     * @return the copy.
     */
    Dashboard copy(int dashboardId, int userId);
    
    /**
     * Delete a dashboard.
     * @param userId TODO
     */
    void delete(int dashboardId);
    
    /**
     * Get the user that owns the specified dashboard.
     * @return the owner of the dashboard, or empty optional if the dashboard has no owner.
     */
    Optional<LiteYukonUser> getOwner(int dashboardId);

    boolean isVisible(int userId, int dashboardId);

    /**
     * Set a new owner for a dashboard.
     */
    void setOwner(int userId, int dashboardId);    
}
