package com.cannontech.maintenance.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTaskSettings;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingEditorDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MaintenanceTaskDaoImpl implements MaintenanceTaskDao {
    private SimpleTableAccessTemplate<MaintenanceSetting> insertTemplate;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private GlobalSettingEditorDao globalSettingEditorDao;

    private static final YukonRowMapper<MaintenanceTask> maintenanceTaskRowMapper =
        new YukonRowMapper<MaintenanceTask>() {
            @Override
            public MaintenanceTask mapRow(YukonResultSet rs) throws SQLException {

                MaintenanceTask maintenanceTask = new MaintenanceTask();
                maintenanceTask.setTaskId(rs.getInt("TaskId"));
                maintenanceTask.setTaskName(rs.getEnum("TaskName", MaintenanceTaskName.class));
                maintenanceTask.setDisabled(rs.getEnum("Disabled", YNBoolean.class).getBoolean());

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
    public List<MaintenanceTaskName> getMaintenanceTaskNames(boolean excludeDisabled) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskName");
        sql.append("FROM MaintenanceTask");
        if (excludeDisabled) {
            sql.append("WHERE Disabled").eq_k(YNBoolean.NO);
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
    public List<MaintenanceTask> getMaintenanceTasks(boolean excludeDisabled) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskId, TaskName, Disabled");
        sql.append("FROM MaintenanceTask");
        if (excludeDisabled) {
            sql.append("WHERE Disabled").eq_k(YNBoolean.NO);
        }
        return jdbcTemplate.query(sql, maintenanceTaskRowMapper);
    }

    @Override
    public MaintenanceTask getMaintenanceTask(MaintenanceTaskName taskName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskId, TaskName, Disabled");
        sql.append("FROM MaintenanceTask");
        sql.append("WHERE TaskName").eq(taskName);
        MaintenanceTask maintenanceTask = jdbcTemplate.queryForObject(sql, maintenanceTaskRowMapper);
        return maintenanceTask;
    }

    @Override
    public MaintenanceTask getMaintenanceTaskById(int taskId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskId, TaskName, Disabled");
        sql.append("FROM MaintenanceTask");
        sql.append("WHERE TaskId").eq(taskId);
        MaintenanceTask maintenanceTask = jdbcTemplate.queryForObject(sql, maintenanceTaskRowMapper);
        return maintenanceTask;
    }

    @Override
    public void updateTaskStatus(MaintenanceTask task) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE MaintenanceTask").set("Disabled", task.isDisabled() ? YNBoolean.YES : YNBoolean.NO);
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
            }
        });

        return settings;
    }

    @Override
    public void updateSettings(List<MaintenanceSetting> settings) {
        for (MaintenanceSetting setting : settings) {
            insertTemplate.save(setting);
        }
    }

    private final FieldMapper<MaintenanceSetting> maintenanceSettingFieldMapper =
        new FieldMapper<MaintenanceSetting>() {
            @Override
            public void extractValues(MapSqlParameterSource p, MaintenanceSetting setting) {
                p.addValue("TaskPropertyId", setting.getTaskPropertyId());
                p.addValue("TaskId", setting.getTaskId());
                p.addValue("Attribute", setting.getAttribute());
                p.addValue("Value", setting.getAttributeValue());
            }

            @Override
            public Number getPrimaryKey(MaintenanceSetting maintenanceSetting) {
                return maintenanceSetting.getTaskPropertyId();
            }

            @Override
            public void setPrimaryKey(MaintenanceSetting maintenanceSetting, int value) {
                maintenanceSetting.setTaskPropertyId(value);
            }
        };

    @PostConstruct
    public void init() {
        insertTemplate = new SimpleTableAccessTemplate<MaintenanceSetting>(jdbcTemplate, nextValueHelper);
        insertTemplate.setTableName("MaintenanceTaskSettings");
        insertTemplate.setPrimaryKeyField("TaskPropertyId");
        insertTemplate.setFieldMapper(maintenanceSettingFieldMapper);
    }

    @Override
    public Map<GlobalSettingType, Pair<Object, String>> getValuesAndComments() {
        ImmutableSet<GlobalSettingType> all = GlobalSettingType.getMaintenanceTasksSettings();
        Map<GlobalSettingType, GlobalSetting> settings = globalSettingEditorDao.getSettings(all);
        return Maps.transformValues(settings, new Function<GlobalSetting, Pair<Object, String>>() {
            @Override
            public Pair<Object, String> apply(GlobalSetting input) {
                Pair<Object, String> valueAndComment = Pair.of(input.getValue(), input.getComments());
                return valueAndComment;
            }
        });
    }

}
