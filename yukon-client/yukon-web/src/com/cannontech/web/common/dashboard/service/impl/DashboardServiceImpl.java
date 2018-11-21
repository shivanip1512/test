package com.cannontech.web.common.dashboard.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.DashboardEventLogService;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.model.UserPageType;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.model.Widget;
import com.cannontech.web.common.dashboard.service.DashboardService;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class DashboardServiceImpl implements DashboardService {
    
    @Autowired DashboardDao dashboardDao;
    @Autowired YukonUserDao userDao;
    @Autowired DashboardEventLogService dashboardEventLogService;
    @Autowired private WidgetService widgetService;
    @Autowired private UserPageDao userPageDao;
 
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
    public void setDefault(LiteYukonUser yukonUser, Iterable<Integer> userIds, DashboardPageType dashboardType, int dashboardId) {
        dashboardDao.assignDashboard(userIds, dashboardType, dashboardId);
        dashboardEventLogService.dashboardAssigned(yukonUser, Iterables.size(userIds), dashboardDao.getDashboard(dashboardId).getName());
    }
    
    @Override
    public void unassignDashboardFromUsers(LiteYukonUser yukonUser, Iterable<Integer> userIds, int dashboardId) {
        dashboardDao.unassignDashboardFromUsers(userIds, dashboardId);
        dashboardEventLogService.dashboardUnassigned(yukonUser, Iterables.size(userIds), dashboardDao.getDashboard(dashboardId).getName());
    }

    @Override
    public void logDetailsEdited(LiteYukonUser yukonUser, String oldName, String oldDescription, String newName, String newDescription) {
        if (!oldName.equals(newName)) {
            dashboardEventLogService.nameChanged(yukonUser, oldName, newName);
        }
        if (!oldDescription.equals(newDescription)) {
            dashboardEventLogService.descriptionChanged(yukonUser, oldDescription, newDescription, newName);
        }
    }
    
    @Override
    public void logWidgetsAddedOrRemoved(LiteYukonUser yukonUser, Dashboard existingDashboard, Dashboard newDashboard) {
        
        //create list of existing widget ids and new widget ids
        Set<Integer> existingWidgetIds = existingDashboard.getAllWidgets().stream().map(Widget::getId).collect(Collectors.toSet());
        Set<Integer> newWidgetIds = newDashboard.getAllWidgets().stream().map(Widget::getId).collect(Collectors.toSet());

        //find removed widgets and log the event
        existingDashboard.getAllWidgets().stream()
                         .filter(widget -> !newWidgetIds.remove(widget.getId()))
                         .forEach(widget -> {
                             dashboardEventLogService.widgetRemoved(yukonUser, widget.getType().toString(), newDashboard.getName());
                         });
        
        //find added widgets and log the event
        newDashboard.getAllWidgets().stream()
                         .filter(widget -> !existingWidgetIds.remove(widget.getId()))
                         .forEach(widget -> {
                             dashboardEventLogService.widgetAdded(yukonUser, widget.getType().toString(), newDashboard.getName());
                         });
    }
    
    @Override
    @Transactional
    public int create(DashboardBase dashboardBase) throws DuplicateException, WidgetParameterValidationException,
            WidgetMissingParameterException {
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
    public int update(LiteYukonUser yukonUser, Dashboard dashboard) throws DuplicateException,
            WidgetParameterValidationException, WidgetMissingParameterException {
        ListMultimap<DashboardPageType, Integer> userPageMap = dashboardDao.getPageAssignmentToUserIdMap(dashboard.getDashboardId());
        Dashboard existingDashboard = dashboardDao.getDashboard(dashboard.getDashboardId());
        validateWidgetParameters(dashboard);
        dashboardDao.deleteDashboard(dashboard.getDashboardId());
        int dashboardId = create(dashboard);
        for (DashboardPageType type : userPageMap.keySet()) {
            dashboardDao.assignDashboard(userPageMap.get(type), type, dashboardId);
        }
        
        logDetailsEdited(yukonUser, existingDashboard.getName(), existingDashboard.getDescription(), dashboard.getName(), dashboard.getDescription());
        logWidgetsAddedOrRemoved(yukonUser, existingDashboard, dashboard);
        
        return dashboardId;
    }

    @Override
    @Transactional
    public int copy(int dashboardId, String name, String description, Visibility visibility, int userId)
            throws DuplicateException, WidgetParameterValidationException, WidgetMissingParameterException {
        Dashboard dashboard = getDashboard(dashboardId);
        dashboard.setOwner(userDao.getLiteYukonUser(userId));
        dashboard.setDashboardId(0);
        dashboard.setDescription(description);
        dashboard.setName(name);
        dashboard.setVisibility(visibility);
        validateWidgetParameters(dashboard);
        return create(dashboard);
    }

    @Override
    public void delete(LiteYukonUser yukonUser, int dashboardId) {
        String dashboardName = dashboardDao.getDashboard(dashboardId).getName();
        dashboardDao.deleteDashboard(dashboardId);
        userPageDao.deleteUserPages(dashboardId, UserPageType.DASHBOARD);
        dashboardEventLogService.dashboardDeleted(yukonUser, dashboardName);
    }

    @Override
    public void setOwner(int userId, int dashboardId) throws WidgetParameterValidationException,
            WidgetMissingParameterException {
        Dashboard dashboard = getDashboard(dashboardId);
        LiteYukonUser oldOwner = dashboard.getOwner();
        LiteYukonUser newOwner = userDao.getLiteYukonUser(userId);
        dashboard.setOwner(newOwner);
        if (oldOwner.getUserID() != newOwner.getUserID()) {
            dashboardEventLogService.dashboardOwnerChanged(oldOwner, newOwner, dashboard.getName());
        }
        update(newOwner, dashboard);
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

    private void validateWidgetParameters(Dashboard dashboard) throws WidgetMissingParameterException, WidgetParameterValidationException {
        for (Widget widget : dashboard.getAllWidgets()) {
            widgetService.createWidget(widget.getType(), widget.getParameters());
        }
    }
}
