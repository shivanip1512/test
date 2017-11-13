package com.cannontech.maintenance.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTaskSettings;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MaintenanceTaskDaoImpl implements MaintenanceTaskDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final YukonRowMapper<MaintenanceTask> maintenanceTaskRowMapper =
        new YukonRowMapper<MaintenanceTask>() {
            @Override
            public MaintenanceTask mapRow(YukonResultSet rs) throws SQLException {

                MaintenanceTask maintenanceTask = new MaintenanceTask();
                maintenanceTask.setTaskId(rs.getInt("TaskId"));
                maintenanceTask.setTaskName(rs.getEnum("TaskName", MaintenanceTaskName.class));
                maintenanceTask.setDisabled(!rs.getString("Status").equals("1"));

                return maintenanceTask;
            }
        };
    @Override
    public Map<MaintenanceTaskSettings, String> getTaskSettings(MaintenanceTaskName taskName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Attribute, Value");
        sql.append("FROM MaintenanceTask ms");
        sql.append("  JOIN MaintenanceTaskSettings msp");
        sql.append("  ON ms.TaskId = msp.TaskId");
        sql.append("AND TaskName").eq(taskName);

        final Map<MaintenanceTaskSettings, String> taskSettings = new HashMap<>();

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                taskSettings.put(rs.getEnum("Attribute", MaintenanceTaskSettings.class), rs.getString("Value"));
            }
        });
        return taskSettings;
    }

    @Override
    public List<MaintenanceTaskName> getMaintenanceTaskNames(boolean includeDisabledTask) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskName");
        sql.append("FROM MaintenanceTask");
        if (!includeDisabledTask) {
            sql.append("WHERE Status").eq_k(1);
        }
        List<MaintenanceTaskName> maintenanceTaskNames = new ArrayList<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                maintenanceTaskNames.add(rs.getEnum("TaskName", MaintenanceTaskName.class));
            }
        });
        return maintenanceTaskNames;
    }

    @Override
    public List<MaintenanceTask> getMaintenanceTasks(boolean includeDisabledTask) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskId, TaskName, Status");
        sql.append("FROM MaintenanceTask");
        if (!includeDisabledTask) {
            sql.append("WHERE Status").eq_k(1);
        }
        return jdbcTemplate.query(sql, maintenanceTaskRowMapper);
    }

    @Override
    public MaintenanceTask getMaintenanceTask(MaintenanceTaskName taskName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskId, TaskName, Status");
        sql.append("FROM MaintenanceTask");
        sql.append("WHERE TaskName").eq_k(taskName);
        MaintenanceTask maintenanceTask = jdbcTemplate.queryForObject(sql, maintenanceTaskRowMapper);
        return maintenanceTask;
    }

    @Override
    public MaintenanceTask getMaintenanceTaskById(int taskId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskId, TaskName, Status");
        sql.append("FROM MaintenanceTask");
        sql.append("WHERE TaskId").eq_k(taskId);
        MaintenanceTask maintenanceTask = jdbcTemplate.queryForObject(sql, maintenanceTaskRowMapper);
        return maintenanceTask;
    }

    @Override
    public void updateTaskStatus(MaintenanceTask task) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE MaintenanceTask").set("Status", task.isDisabled() ? "0" : "1");
        sql.append("WHERE TaskId").eq(task.getTaskId());
        jdbcTemplate.update(sql);
    }

    @Override
    public List<MaintenanceSetting> getSettingsForMaintenanceTaskName(MaintenanceTaskName task) {
        Set<MaintenanceSettingType> all = MaintenanceSettingType.getSettingsForTask(task);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskPropertyId, TaskId, Attribute, Value");
        sql.append("FROM MaintenanceTaskSettings");
        sql.append("WHERE Attribute").in(all);

        final Set<MaintenanceSettingType> found = Sets.newHashSet();
        final List<MaintenanceSetting> settings = Lists.newArrayList();

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {

                MaintenanceSettingType type = rs.getEnum(("Attribute"), MaintenanceSettingType.class);
                Object value = InputTypeFactory.convertPropertyValue(type.getType(), rs.getString("Value"));
                if (value == null) {
                    value = type.getDefaultValue();
                }
                MaintenanceSetting setting = new MaintenanceSetting(type, value);
                setting.setTaskPropertyId((rs.getInt("TaskPropertyId")));
                setting.setTaskId((rs.getInt("TaskId")));
                settings.add(setting);
                found.add(type);
            }
        });

        Set<MaintenanceSettingType> missing = Sets.difference(all, found);
        for (MaintenanceSettingType type : missing) {
            MaintenanceSetting setting = new MaintenanceSetting(type, type.getDefaultValue());
            settings.add(setting);
        }

        return settings;
    }

    @Override
    public void updateSettings(List<MaintenanceSetting> settings) {
        for (MaintenanceSetting setting : settings) {
            updateSetting(setting);
        }
    }

    private void updateSetting(MaintenanceSetting setting) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE MaintenanceTaskSettings").set("Value", setting.getAttributeValue());
        sql.append("WHERE Attribute").eq(setting.getAttribute());
        jdbcTemplate.update(sql);
    }

}
