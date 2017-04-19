package com.cannontech.web.common.dashboard.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.service.DashboardService;

public class DashboardServiceImpl implements DashboardService {
    
    @Autowired DashboardDao dashboardDao;
    
    @Override
    public Dashboard getAssignedDashboard(int userId, DashboardPageType dashboardType) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Dashboard getDashboard(int dashboardId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteDashboard> getOwnedDashboards(int userId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteDashboard> getFavorites(int userId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteDashboard> getFavoritesForPage(int userId, DashboardPageType type) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteDashboard> getVisible(int userId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteDashboard> getAllOwnerless() {
        throw new MethodNotImplementedException();
    }

    @Override
    public void setDefault(Iterable<Integer> userIds, DashboardPageType dashboardType, int dashboardId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void favorite(int userId, int dashboardId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void unfavorite(int userId, int dashboardId) {
        throw new MethodNotImplementedException();
    }

    @Override
    @Transactional
    public int create(DashboardBase dashboardBase) {
        return doCreate(dashboardBase);
    }
    
    /**
     * Creates the specified dashboard, including any widgets and widget settings.
     * Any public method that calls this should be marked @Transactional.
     */
    private int doCreate(DashboardBase dashboardBase) {
        int dashboardId = dashboardDao.create(dashboardBase);
        if (dashboardBase instanceof Dashboard) {
            Dashboard dashboard = (Dashboard) dashboardBase;
            dashboardDao.insertWidgets(dashboardId, dashboard.getColumn1Widgets(), 1);
            dashboardDao.insertWidgets(dashboardId, dashboard.getColumn2Widgets(), 2);
        }
        return dashboardId;
    }

    @Override
    @Transactional
    public int update(Dashboard dashboard) {
        dashboardDao.deleteDashboard(dashboard.getDashboardId());
        return doCreate(dashboard);
    }

    @Override
    public Dashboard copy(int dashboardId, int userId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void delete(int dashboardId) {
        dashboardDao.deleteDashboard(dashboardId);
    }

    @Override
    public void assign(int userId, int dashboardId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Optional<LiteYukonUser> getOwner(int dashboardId) {
        throw new MethodNotImplementedException();
    }

}
