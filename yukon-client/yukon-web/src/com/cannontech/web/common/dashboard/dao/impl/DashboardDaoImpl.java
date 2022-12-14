package com.cannontech.web.common.dashboard.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.model.LiteDashboard;
import com.cannontech.web.common.dashboard.model.Visibility;
import com.cannontech.web.common.dashboard.model.Widget;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class DashboardDaoImpl implements DashboardDao {
    private static final int col1WidgetBase = 100; // This implies no more than 100 widgets per column.
    private static final int col2WidgetBase = 200;
    private static final DashboardRowMapper dashboardRowMapper = new DashboardRowMapper();
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserDao userDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DbChangeManager dbChangeManager;
    
    private Cache<Integer, Integer> defaultDashboardUserCount = CacheBuilder.newBuilder().maximumSize(2).build();
    private Cache<YukonRole, Integer> nonResidentialActiveUsersCount = CacheBuilder.newBuilder().maximumSize(1).build();

    
    SqlStatementBuilder baseDashboardSql = new SqlStatementBuilder();
    {
        baseDashboardSql.append(
            "SELECT d.DashboardId, Name, Description, OwnerId, Visibility, yu.UserID, UserName, Status, ForceReset, UserGroupId");
        baseDashboardSql.append("FROM Dashboard d");
        baseDashboardSql.append("LEFT JOIN YukonUser yu ON d.OwnerId = yu.UserID");
    }
    
    @PostConstruct
    public void setup() {
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB) {
                    loadYukonNonResidentialUser();
                    reloadDefaultDashboardUserCount();
                }
            }
        });

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.DASHBOARD_ASSIGNMENT,
            new DatabaseChangeEventListener() {
                @Override
                public void eventReceived(DatabaseChangeEvent event) {
                   reloadDefaultDashboardUserCount();
                }
            });
        
        loadYukonNonResidentialUser();
        reloadDefaultDashboardUserCount();
    }
    
    private void loadYukonNonResidentialUser() {
        int yukonUserCount = userDao.getNonResidentialUserCount();
        nonResidentialActiveUsersCount.put(YukonRole.RESIDENTIAL_CUSTOMER, yukonUserCount);
    }
    
    private void reloadDefaultDashboardUserCount() {

        int yukonUserCount = nonResidentialActiveUsersCount.getIfPresent(YukonRole.RESIDENTIAL_CUSTOMER);
        defaultDashboardUserCount.put(DashboardPageType.AMI.getDefaultDashboardId(), yukonUserCount);
        defaultDashboardUserCount.put(DashboardPageType.MAIN.getDefaultDashboardId(), yukonUserCount);
       
        SqlStatementBuilder pageAssignmentSql = new SqlStatementBuilder();
        pageAssignmentSql.append("SELECT PageAssignment, COUNT(UserId) AS UserCount, DashboardId");
        pageAssignmentSql.append("FROM UserDashboard");
        pageAssignmentSql.append(" GROUP BY PageAssignment, DashboardId");
               
        jdbcTemplate.query(pageAssignmentSql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                DashboardPageType type = rs.getEnum("PageAssignment", DashboardPageType.class);
                Integer dashboardId = rs.getInt("DashboardId");
                if (type == DashboardPageType.AMI && dashboardId != DashboardPageType.AMI.getDefaultDashboardId()) {

                    Integer amiDashboardCount =
                        defaultDashboardUserCount.getIfPresent(DashboardPageType.AMI.getDefaultDashboardId());

                    if (amiDashboardCount == null) {
                        defaultDashboardUserCount.put(DashboardPageType.AMI.getDefaultDashboardId(),
                            yukonUserCount - rs.getInt("UserCount"));
                    } else {
                        defaultDashboardUserCount.put(DashboardPageType.AMI.getDefaultDashboardId(),
                            amiDashboardCount - rs.getInt("UserCount"));
                    }

                } else if (type == DashboardPageType.MAIN
                    && dashboardId != DashboardPageType.MAIN.getDefaultDashboardId()) {
                    Integer mainDashboardCount =
                        defaultDashboardUserCount.getIfPresent(DashboardPageType.MAIN.getDefaultDashboardId());

                    if (mainDashboardCount == null) {
                        defaultDashboardUserCount.put(DashboardPageType.MAIN.getDefaultDashboardId(),
                            yukonUserCount - rs.getInt("UserCount"));
                    } else {
                        defaultDashboardUserCount.put(DashboardPageType.MAIN.getDefaultDashboardId(),
                            mainDashboardCount - rs.getInt("UserCount"));
                    }

                }
            }
        });
    }
    
    @Override
    public Dashboard getDashboard(int userId, DashboardPageType dashboardType){
        
        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        dashboardSql.append(baseDashboardSql.getSql());
        dashboardSql.append("JOIN UserDashboard ud ON d.DashboardId = ud.DashboardId");
        dashboardSql.append("WHERE ud.UserID").eq(userId);
        dashboardSql.append("AND ud.PageAssignment").eq_k(dashboardType);
        try {
            Dashboard dashboard = jdbcTemplate.queryForObject(dashboardSql, dashboardRowMapper);
            addWidgets(dashboard);
            return dashboard;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Dashboard getDashboard(int dashboardId) {

        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        dashboardSql.append(baseDashboardSql.getSql());
        dashboardSql.append("WHERE d.DashboardId").eq(dashboardId);
        Dashboard dashboard = jdbcTemplate.queryForObject(dashboardSql, dashboardRowMapper);
        
        addWidgets(dashboard);
        return dashboard;
    }
        
    /**
     * Adds widgets to dashboard
     */
    private void addWidgets(Dashboard dashboard){
        // Get associated widgets
        SqlStatementBuilder widgetSql = new SqlStatementBuilder();
        widgetSql.append("SELECT WidgetId, DashboardId, WidgetType, Ordering");
        widgetSql.append("FROM Widget");
        widgetSql.append("WHERE DashboardId").eq(dashboard.getDashboardId());
        widgetSql.append("ORDER BY Ordering");
        jdbcTemplate.query(widgetSql, new WidgetRowCallbackHandler(dashboard));
        
        // Get widget parameters
        SqlStatementBuilder widgetParamSql = new SqlStatementBuilder();
        widgetParamSql.append("SELECT ws.WidgetId, Name, Value");
        widgetParamSql.append("FROM WidgetSettings ws");
        widgetParamSql.append("JOIN Widget w ON w.WidgetId = ws.WidgetId");
        widgetParamSql.append("WHERE w.DashboardId").eq(dashboard.getDashboardId());
        WidgetSettingCallbackHandler widgetSettingCallbackHandler = new WidgetSettingCallbackHandler();
        jdbcTemplate.query(widgetParamSql, widgetSettingCallbackHandler);
        
        Map<Integer, Map<String, String>> widgetParameters = widgetSettingCallbackHandler.getWidgetParameters();
        dashboard.getAllWidgets().forEach(widget -> {
            Map<String, String> parameters = widgetParameters.get(widget.getId());
            widget.setParameters(Optional.ofNullable(parameters).orElse(new HashMap<>()));
        });
        
    }
        
    @Override
    public List<LiteDashboard> getDashboardsByVisibility(Visibility... visibility) {
        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        dashboardSql.append(baseDashboardSql.getSql());
        dashboardSql.append("WHERE Visibility").in_k(Arrays.asList(visibility));
        List<Dashboard> dashboards = jdbcTemplate.query(dashboardSql, dashboardRowMapper);
        return getLiteDashboards(dashboards);
    }
    
    @Override
    public List<LiteDashboard> getAllDashboards() {
        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        dashboardSql.append(baseDashboardSql.getSql());
        List<Dashboard> dashboards = jdbcTemplate.query(dashboardSql, dashboardRowMapper);
        return getLiteDashboards(dashboards);
    }

    @Override
    public List<LiteDashboard> getOwnedDashboards(int ownerId) {
        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        dashboardSql.append(baseDashboardSql.getSql());
        dashboardSql.append("WHERE OwnerId").eq(ownerId);
        List<Dashboard> dashboards = jdbcTemplate.query(dashboardSql, dashboardRowMapper);
        return getLiteDashboards(dashboards);
    }
    
    @Override
    public List<LiteDashboard> getAllOwnerless() {
        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        dashboardSql.append(baseDashboardSql.getSql());
        dashboardSql.append("WHERE OwnerId IS NULL");
        List<Dashboard> dashboards = jdbcTemplate.query(dashboardSql, dashboardRowMapper);
        return getLiteDashboards(dashboards);
    }
    
    /**
     * Converts Dashboard to LiteDashboard and retrieves the user count.
     */
    private List<LiteDashboard> getLiteDashboards(List<Dashboard> dashboards) {
        if (dashboards.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Integer, Integer> dashboardIdToUserCount = new HashMap<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DashboardId, count(distinct UserId) as UserCount");
        sql.append("FROM UserDashboard");
        sql.append("GROUP BY DashboardId");
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                dashboardIdToUserCount.put(rs.getInt("DashboardId"), rs.getInt("UserCount"));
            }
        });
        
        dashboardIdToUserCount.put(DashboardPageType.AMI.getDefaultDashboardId(),
            defaultDashboardUserCount.getIfPresent(DashboardPageType.AMI.getDefaultDashboardId()));
        dashboardIdToUserCount.put(DashboardPageType.MAIN.getDefaultDashboardId(), 
            defaultDashboardUserCount.getIfPresent(DashboardPageType.MAIN.getDefaultDashboardId()));

        return dashboards.stream().map(
            d -> new LiteDashboard(d, dashboardIdToUserCount.get(d.getDashboardId()))).collect(Collectors.toList());

    }
    
    @Override
    public void deleteDashboard(int dashboardId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Dashboard");
        sql.append("WHERE DashboardId").eq(dashboardId);
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteUserDashboard(int userId, int dashboardId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserDashboard");
        sql.append("WHERE UserId").eq(userId);
        sql.append("AND DashboardId").eq(dashboardId);
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<Integer> getAllUsersForDashboard(int dashboardId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT distinct UserId");
        sql.append("FROM UserDashboard");
        sql.append("WHERE DashboardId").eq(dashboardId);
        
        List<Integer> userIdList = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        return userIdList;
    }
    
    @Override
    public int create(DashboardBase dashboard) throws DuplicateException {
        SqlStatementBuilder dupSql = new SqlStatementBuilder();
        dupSql.append("SELECT count(DashboardId)");
        dupSql.append("FROM Dashboard");
        dupSql.append("WHERE OwnerId").eq(dashboard.getOwner().getLiteID());
        dupSql.append("AND Name").eq(dashboard.getName());
        if (jdbcTemplate.queryForInt(dupSql) > 0) {
            throw new DuplicateException("Dashboard with the name " + dashboard.getName() + " is already created by "
                + dashboard.getOwner().getUsername());
        }
        int dashboardId = dashboard.getDashboardId();
        if (dashboardId == 0) {
            dashboardId = nextValueHelper.getNextValue("Dashboard");
        }
        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        SqlParameterSink dashboardSink = dashboardSql.insertInto("Dashboard");
        dashboardSink.addValue("DashboardId", dashboardId);
        dashboardSink.addValue("Name", dashboard.getName());
        dashboardSink.addValueSafe("Description", dashboard.getDescription());
        dashboardSink.addValue("OwnerId", dashboard.getOwner().getUserID());
        dashboardSink.addValue("Visibility", dashboard.getVisibility());
        jdbcTemplate.update(dashboardSql);

        return dashboardId;
    }
    
    @Override
    @Transactional
    public void insertWidgets(int dashboardId, List<Widget> widgets, int column) {
        if (widgets == null) {
            return;
        }
        
        int columnBase = column == 1 ? col1WidgetBase : col2WidgetBase;
        for (int i = 0; i < widgets.size(); i++) {
            // First, insert the widget...
            Widget widget = widgets.get(i);
            int widgetId = nextValueHelper.getNextValue("Widget");
            SqlStatementBuilder widgetSql = new SqlStatementBuilder();
            SqlParameterSink widgetSink = widgetSql.insertInto("Widget");
            widgetSink.addValue("WidgetId", widgetId);
            widgetSink.addValue("WidgetType", widget.getType());
            widgetSink.addValue("DashboardId", dashboardId);
            widgetSink.addValue("Ordering", columnBase + i);
            jdbcTemplate.update(widgetSql);
            
            // ...then insert the widget settings.
            widget.getParameters().entrySet().stream().forEach(parameter -> {
                SqlStatementBuilder widgetParamSql = new SqlStatementBuilder();
                SqlParameterSink widgetParamSink = widgetParamSql.insertInto("WidgetSettings");
                int settingId = nextValueHelper.getNextValue("WidgetSettings");
                widgetParamSink.addValue("SettingId", settingId);
                widgetParamSink.addValue("WidgetId", widgetId);
                widgetParamSink.addValue("Name", parameter.getKey());
                widgetParamSink.addValueSafe("Value", parameter.getValue());
                jdbcTemplate.update(widgetParamSql);
            });
        }
    }

    @Override
    @Transactional
    public void assignDashboard(Iterable<Integer> userIds, DashboardPageType dashboardType, int dashboardId) {
        List<List<Integer>> ids = Lists.partition(Lists.newArrayList(userIds), ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
            unassignDashboardForType(idBatch, dashboardType);
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO UserDashboard");
            insertSql.append("(UserId, DashboardId, PageAssignment)");
            insertSql.append("values");
            insertSql.append("(?, ?, ?)");

            jdbcTemplate.batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, idBatch.get(i));
                    ps.setInt(2, dashboardId);
                    ps.setString(3, dashboardType.name());
                }

                @Override
                public int getBatchSize() {
                    return idBatch.size();
                }
            });
        });
        
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.DASHBOARD_ASSIGNMENT, dashboardId);
    }
    
    @Override
    @Transactional
    public void unassignDashboard(Iterable<Integer> userIds, DashboardPageType dashboardType) {
        List<List<Integer>> ids = Lists.partition(Lists.newArrayList(userIds), ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
            unassignDashboardForType(userIds, dashboardType);
        });
    }
    
    @Override
    @Transactional
    public void unassignDashboardFromUsers(Iterable<Integer> userIds, int dashboardId) {
        SqlStatementBuilder unassignSql = new SqlStatementBuilder();
        unassignSql.append("DELETE FROM UserDashboard");
        unassignSql.append("WHERE UserId").in(userIds);
        unassignSql.append("AND DashboardId").eq_k(dashboardId);
        jdbcTemplate.update(unassignSql);
        
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.DASHBOARD_ASSIGNMENT, dashboardId);
    }
    
    private void unassignDashboardForType(Iterable<Integer> userIds, DashboardPageType type) {
        SqlStatementBuilder unassignSql = new SqlStatementBuilder();
        unassignSql.append("DELETE FROM UserDashboard");
        unassignSql.append("WHERE UserId").in(userIds);
        unassignSql.append("AND PageAssignment").eq_k(type);
        jdbcTemplate.update(unassignSql);
    }
    
    @Override
    public ListMultimap<DashboardPageType, Integer> getPageAssignmentToUserIdMap(int dashboardId) {
        ListMultimap<DashboardPageType, Integer> resultMap = ArrayListMultimap.create();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserId, PageAssignment FROM UserDashboard");
        sql.append("WHERE dashboardId").eq(dashboardId);
        
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                Integer userId = rs.getInt("UserId");
                DashboardPageType type = rs.getEnum("PageAssignment", DashboardPageType.class);
                resultMap.put(type, userId);
            }
        });
        
        return resultMap;
    }
}
