package com.cannontech.maintenance.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
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
import com.cannontech.maintenance.MaintenanceHelper;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingEditorDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MaintenanceTaskDaoImpl implements MaintenanceTaskDao {
    private SimpleTableAccessTemplate<MaintenanceTask> maintenanceTaskTemplate;
    private SimpleTableAccessTemplate<MaintenanceSetting> maintenanceSettingTemplate;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private MaintenanceHelper maintenanceHelper;

    @Autowired private GlobalSettingEditorDao globalSettingEditorDao;

    private static final YukonRowMapper<MaintenanceTask> maintenanceTaskRowMapper =
        new YukonRowMapper<MaintenanceTask>() {
            @Override
            public MaintenanceTask mapRow(YukonResultSet rs) throws SQLException {

                MaintenanceTask maintenanceTask = new MaintenanceTask();
                maintenanceTask.setTaskId(rs.getInt("TaskId"));
                maintenanceTask.setTaskName(rs.getEnum("TaskName", MaintenanceTaskType.class));
                maintenanceTask.setDisabled(rs.getEnum("Disabled", YNBoolean.class).getBoolean());

                return maintenanceTask;
            }
        };

    @Override
    public List<MaintenanceTaskType> getMaintenanceTaskTypes(boolean excludeDisabled) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskName");
        sql.append("FROM MaintenanceTask");
        if (excludeDisabled) {
            sql.append("WHERE Disabled").eq_k(YNBoolean.NO);
        }
        List<MaintenanceTaskType> maintenanceTaskTypes = new ArrayList<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                maintenanceTaskTypes.add(rs.getEnum("TaskName", MaintenanceTaskType.class));
            }
        });
        return maintenanceTaskTypes;
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
    public MaintenanceTask getMaintenanceTask(MaintenanceTaskType taskType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskId, TaskName, Disabled");
        sql.append("FROM MaintenanceTask");
        sql.append("WHERE TaskName").eq(taskType);
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
    public List<MaintenanceSetting> getSettingsForMaintenanceTaskType(MaintenanceTaskType taskType) {
        List<MaintenanceSetting> settings = getSettingsForTaskType(taskType);
            if (CollectionUtils.isEmpty(settings)) {
                settings = Lists.newArrayList();
                Set<MaintenanceSettingType> maintenanceSettingTypes = MaintenanceTaskType.getMaintenanceTaskSettingMapping().get(taskType);
                MaintenanceTask maintenanceTask = getMaintenanceTask(taskType);
                for (MaintenanceSettingType type : maintenanceSettingTypes) {
                    MaintenanceSetting setting = new MaintenanceSetting();
                    setting.setTaskId(maintenanceTask.getTaskId());
                    Object value = type.getDefaultValue();
                    setting.setAttribute(type);
                    setting.setAttributeValue(value);
                    settings.add(setting);
                }
            }
        return settings;
    }

    private List<MaintenanceSetting> getSettingsForTaskType(MaintenanceTaskType taskType) {
        Set<MaintenanceSettingType> all = MaintenanceTaskType.getSettingsForTask(taskType);

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
            maintenanceSettingTemplate.save(setting);
        }
    }

    private final FieldMapper<MaintenanceSetting> maintenanceSettingFieldMapper =
        new FieldMapper<MaintenanceSetting>() {
            @Override
            public void extractValues(MapSqlParameterSource p, MaintenanceSetting setting) {
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

    private final FieldMapper<MaintenanceTask> maintenanceTaskFieldMapper = new FieldMapper<MaintenanceTask>() {
        @Override
        public void extractValues(MapSqlParameterSource p, MaintenanceTask maintenanceTask) {
            p.addValue("TaskName", maintenanceTask.getTaskName());
            p.addValue("Disabled", YNBoolean.valueOf(maintenanceTask.isDisabled()));
        }

        @Override
        public Number getPrimaryKey(MaintenanceTask maintenanceTask) {
            return maintenanceTask.getTaskId();
        }

        @Override
        public void setPrimaryKey(MaintenanceTask maintenanceTask, int value) {
            maintenanceTask.setTaskId(value);
        }
    };
    
    @PostConstruct
    public void init() {
        maintenanceTaskTemplate = new SimpleTableAccessTemplate<MaintenanceTask>(jdbcTemplate, nextValueHelper);
        maintenanceTaskTemplate.setTableName("MaintenanceTask");
        maintenanceTaskTemplate.setPrimaryKeyField("TaskId");
        maintenanceTaskTemplate.setFieldMapper(maintenanceTaskFieldMapper);
        maintenanceTaskTemplate.setPrimaryKeyValidOver(0);

        maintenanceSettingTemplate = new SimpleTableAccessTemplate<MaintenanceSetting>(jdbcTemplate, nextValueHelper);
        maintenanceSettingTemplate.setTableName("MaintenanceTaskSettings");
        maintenanceSettingTemplate.setPrimaryKeyField("TaskPropertyId");
        maintenanceSettingTemplate.setFieldMapper(maintenanceSettingFieldMapper);
        insertMaintenanceTask();
    }

    private void insertMaintenanceTask() {
        List<MaintenanceTaskType> allMaintenanceTaskTypes = getMaintenanceTaskTypes(false);
        List<MaintenanceTaskType> taskTypes = Arrays.stream(MaintenanceTaskType.values())
                                                    .filter(task -> !allMaintenanceTaskTypes.contains(task))
                                                    .collect(Collectors.toList());
        taskTypes.forEach(taskType -> {
            MaintenanceTask task = new MaintenanceTask();
            task.setTaskName(taskType);
            task.setDisabled(taskType.isDisabled().getBoolean());
            maintenanceTaskTemplate.save(task);
        });
    }

    @Override
    public Map<GlobalSettingType, Pair<Object, String>> getValuesAndComments() {
        Map<GlobalSettingType, GlobalSetting> settings = globalSettingEditorDao.getSettings(maintenanceHelper.getGlobalSettingsForMaintenance());
        return Maps.transformValues(settings, new Function<GlobalSetting, Pair<Object, String>>() {
            @Override
            public Pair<Object, String> apply(GlobalSetting input) {
                Pair<Object, String> valueAndComment = Pair.of(input.getValue(), input.getComments());
                return valueAndComment;
            }
        });
    }

}
