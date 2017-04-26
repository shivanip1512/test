package com.cannontech.web.common.dashboard.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.service.DashboardService;

public class DashboardServiceImpl implements DashboardService {
    
    @Autowired DashboardDao dashboardDao;
    @Autowired YukonUserDao userDao;
 
    @Override
    public Dashboard getAssignedDashboard(int userId, DashboardPageType dashboardType) {
        Dashboard dashboard = dashboardDao.getDashboard(userId, dashboardType);
        if (dashboard == null) {
            dashboard = dashboardDao.getDashboard(Visibility.SYSTEM, dashboardType);
        }
        return dashboard;
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
    public List<LiteDashboard> getFavorites(int userId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteDashboard> getFavoritesForPage(int userId, DashboardPageType type) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteDashboard> getVisible(int userId) {
        List<LiteDashboard> visibleDashboards = new ArrayList<>();

        //User can view the dashboard if
        
        //The dashboard visibility = PUBLIC or SYSTEM
        visibleDashboards.addAll(dashboardDao.getDashboardsByVisibility(Visibility.SYSTEM, Visibility.PUBLIC));
        //The user is the owner
        visibleDashboards.addAll(dashboardDao.getOwnedDashboards(userId));
        //The dashboard visibility = SHARED and the user is in the same user group as the dashboard's owner.
        visibleDashboards.addAll(dashboardDao.getVisibleSharedDashboards(userId));
        return visibleDashboards;
    }
    
    @Override
    public boolean isVisible(int userId, int dashboardId) {
        Dashboard dashboard = getDashboard(dashboardId);
        //User can view the dashboard if
          
        if (dashboard.getVisibility() == Visibility.SYSTEM || dashboard.getVisibility() == Visibility.PUBLIC) {
          //The dashboard visibility = PUBLIC or SYSTEM
            return true;
        } else if (dashboard.getOwner().getLiteID() == userId) {
            //The user is the owner
            return true;
        } else if (dashboard.getVisibility() == Visibility.SHARED && dashboard.getOwner() != null){
            LiteYukonUser owner = userDao.getLiteYukonUser(dashboard.getOwner().getLiteID());
            LiteYukonUser user = userDao.getLiteYukonUser(userId);
            if(owner.getUserGroupId() == user.getUserGroupId()){
              //The dashboard visibility = SHARED and the user is in the same user group as the dashboard's owner.
                return true;
            }
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
        return create(dashboard);
    }

    @Override
    @Transactional
    public Dashboard copy(int dashboardId, int userId) {
        Dashboard dashboard = getDashboard(dashboardId);
        dashboard.setOwner(userDao.getLiteYukonUser(userId));
        dashboard.setDashboardId(0);
        int newDashboardId = create(dashboard);
        return getDashboard(newDashboardId);
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
}
