package com.cannontech.web.common.dashboard.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.service.DashboardService;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class DashboardServiceImpl implements DashboardService {
    
    @Autowired DashboardDao dashboardDao;
    @Autowired YukonUserDao userDao;
 
    @Override
    public Dashboard getAssignedDashboard(int userId, DashboardPageType dashboardType) {
        Dashboard dashboard = dashboardDao.getDashboard(userId, dashboardType);
        if (dashboard == null) {
            dashboard = dashboardDao.getDashboard(dashboardType.getDefaultDashboardId());
        }
        return dashboard;
    }
    
    @Override
    public List<LiteDashboard> getDashboards() {
        return dashboardDao.getAllDashboards();
    }

    @Override
    public Dashboard getDashboard(int dashboardId) {
        return dashboardDao.getDashboard(dashboardId);
    }

    @Override
    public List<LiteDashboard> getOwnedDashboards(int ownerId) {
        return dashboardDao.getOwnedDashboards(ownerId);
    }

    @Override
    public List<LiteDashboard> getVisible(int userId) {
        Set<LiteDashboard> visibleDashboards = new HashSet<>();

        // User can view the dashboard if

        // The dashboard visibility = PUBLIC or SYSTEM
        visibleDashboards.addAll(dashboardDao.getDashboardsByVisibility(Visibility.SYSTEM, Visibility.PUBLIC));
        // The user is the owner
        visibleDashboards.addAll(dashboardDao.getOwnedDashboards(userId));
        return Lists.newArrayList(visibleDashboards);
    }
    
    @Override
    public boolean isVisible(int userId, int dashboardId) {
        Dashboard dashboard = getDashboard(dashboardId);
        // User can view the dashboard if

        if (dashboard.getVisibility() == Visibility.SYSTEM || dashboard.getVisibility() == Visibility.PUBLIC) {
            // The dashboard visibility = PUBLIC or SYSTEM
            return true;
        } else if (dashboard.getOwner() != null && dashboard.getOwner().getLiteID() == userId) {
            // The user is the owner
            return true;
        } 
        return false;
    }

    @Override
    public List<LiteDashboard> getAllOwnerless() {
        return dashboardDao.getAllOwnerless();
    }

    @Override
    public void setDefault(Iterable<Integer> userIds, DashboardPageType dashboardType, int dashboardId) {
        dashboardDao.assignDashboard(userIds, dashboardType, dashboardId);
    }

    @Override
    @Transactional
    public int create(DashboardBase dashboardBase) throws DuplicateException {
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
        ListMultimap<DashboardPageType, Integer> userPageMap = dashboardDao.getPageAssignmentToUserIdMap(dashboard.getDashboardId());
        dashboardDao.deleteDashboard(dashboard.getDashboardId());
        int dashboardId = create(dashboard);
        for (DashboardPageType type : userPageMap.keySet()) {
            dashboardDao.assignDashboard(userPageMap.get(type), type, dashboardId);
        }
        return dashboardId;
    }

    @Override
    @Transactional
    public int copy(int dashboardId, String name, String description, Visibility visibility,int userId) throws DuplicateException{
        Dashboard dashboard = getDashboard(dashboardId);
        dashboard.setOwner(userDao.getLiteYukonUser(userId));
        dashboard.setDashboardId(0);
        dashboard.setDescription(description);
        dashboard.setName(name);
        dashboard.setVisibility(visibility);
        return create(dashboard);
    }

    @Override
    public void delete(int dashboardId) {
        dashboardDao.deleteDashboard(dashboardId);
    }

    @Override
    public void setOwner(int userId, int dashboardId) {
        Dashboard dashboard = getDashboard(dashboardId);
        dashboard.setOwner(userDao.getLiteYukonUser(userId));
        update(dashboard);
    }

    @Override
    public Optional<LiteYukonUser> getOwner(int dashboardId) {
        Dashboard dashboard = getDashboard(dashboardId);
        return Optional.of(dashboard.getOwner());
    }
    
    @Override
    public  List<Integer> getAllUsersForDashboard(int dashboardId) {
        return dashboardDao.getAllUsersForDashboard(dashboardId);
    }

}
