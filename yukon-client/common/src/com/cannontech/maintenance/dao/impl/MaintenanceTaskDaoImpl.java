package com.cannontech.maintenance.dao.impl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.maintenance.MaintenanceHelper;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingEditorDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;
import com.cannontech.web.input.type.EnumInputType;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MaintenanceTaskDaoImpl implements MaintenanceTaskDao {
    private static final Logger log = YukonLogManager.getLogger(MaintenanceTaskDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private MaintenanceHelper maintenanceHelper;
    @Autowired private GlobalSettingEditorDao globalSettingEditorDao;

    @Override
    public List<MaintenanceTask> getMaintenanceTasks() {
        List<MaintenanceTask> maintenanceTasks = new ArrayList<>();

        EnumSet.allOf(MaintenanceTaskType.class).forEach(taskType -> {
            MaintenanceSettingType settingType = MaintenanceSettingType.getEnabledSetting(taskType);
            MaintenanceTask maintenanceTask = new MaintenanceTask();
            maintenanceTask.setTaskType(taskType);
            maintenanceTask.setEnabled(getBooleanValue(settingType));
            maintenanceTasks.add(maintenanceTask);
        });
        return maintenanceTasks;
    }

    @Override
    public MaintenanceTask getMaintenanceTask(MaintenanceTaskType taskType) {
        MaintenanceSettingType settingType = MaintenanceSettingType.getEnabledSetting(taskType);

        MaintenanceTask maintenanceTask = new MaintenanceTask();
        maintenanceTask.setTaskType(taskType);
        maintenanceTask.setEnabled(getBooleanValue(settingType));
        return maintenanceTask;
    }

    @Override
    public boolean getBooleanValue(MaintenanceSettingType setting) {
        return getConvertedValue(setting, Boolean.class);
    }

    private <E extends Enum<E>> E getEnum(MaintenanceSettingType setting, Class<E> enumClass) {
        if (enumClass.isAssignableFrom(setting.getType().getTypeClass())) {
            return getConvertedValue(setting, enumClass);
        }
        return null;
    }

    @Override
    public void updateSettings(List<MaintenanceSetting> settings) {
        settings.stream().forEach( setting -> {
            if (setting.getAttribute() != null) {
                setValue(setting.getAttribute(), setting.getAttributeValue());
            }
        });
    }

    @Override
    public List<MaintenanceSetting> getSettingsForTaskType(MaintenanceTaskType taskType) {
        final List<MaintenanceSetting> allSettings = Lists.newArrayList();
        Set<MaintenanceSettingType> allSettingForTask = taskType.getSettings();
        allSettingForTask.stream().forEach(setting -> {
            Class<?> valueType = setting.getType().getTypeClass();
            Object settingValue = null;
            if (valueType == Boolean.class) {
                settingValue = getBooleanValue(setting);
            } else if (valueType.isEnum()) {
                EnumInputType<?> inputType = (EnumInputType<?>) setting.getType();
                settingValue = getEnum(setting, inputType.getTypeClass());
            }
            MaintenanceSetting maintenanceSetting = new MaintenanceSetting(setting, settingValue);
            allSettings.add(maintenanceSetting);
        });
        return allSettings;
    }

    @Override
    public Object getSettingValue(MaintenanceSettingType setting) {
        Class<?> valueType = setting.getType().getTypeClass();
        Object settingValue = null;
        if (valueType == Boolean.class) {
            settingValue = getBooleanValue(setting);
        } else if (valueType.isEnum()) {
            EnumInputType<?> inputType = (EnumInputType<?>) setting.getType();
            settingValue = getEnum(setting, inputType.getTypeClass());
        }
        return settingValue;
    }

    @Override
    public Map<GlobalSettingType, Pair<Object, String>> getValuesAndComments() {
        Map<GlobalSettingType, GlobalSetting> settings =
            globalSettingEditorDao.getSettings(maintenanceHelper.getGlobalSettingsForMaintenance());
        return Maps.transformValues(settings, new Function<GlobalSetting, Pair<Object, String>>() {
            @Override
            public Pair<Object, String> apply(GlobalSetting input) {
                Pair<Object, String> valueAndComment = Pair.of(input.getValue(), input.getComments());
                return valueAndComment;
            }
        });
    }

    /**
     * Saves the value of the setting.
     * If setting is not present in table then the setting is inserted else updated.
     */
    private void setValue(MaintenanceSettingType setting, Object value) {
        // try update
        SqlStatementBuilder sqlUpdate = new SqlStatementBuilder();
        sqlUpdate.update("MaintenanceTaskSettings");
        sqlUpdate.append("Value").eq(value.toString());
        sqlUpdate.append("WHERE SettingType").eq_k(setting);
        int rows = jdbcTemplate.update(sqlUpdate);
        if (rows == 1) {
            return;
        }
        // try insert
        SqlStatementBuilder sqlInsert = new SqlStatementBuilder();
        sqlInsert.append("INSERT INTO MaintenanceTaskSettings");
        sqlInsert.values(setting, value.toString());
        jdbcTemplate.update(sqlInsert);
    }

    /**
     * Get the value of the setting.
     * First checks if setting value is present in database if not then gets default value of the setting.
     */
    private <T> T getConvertedValue(MaintenanceSettingType setting, Class<T> returnType) {
        if (log.isDebugEnabled()) {
            log.debug("getting converted value of " + setting + " as " + returnType.getSimpleName());
        }

        Validate.isTrue(returnType.isAssignableFrom(setting.getType().getTypeClass()),
            "can't convert " + setting + " to " + returnType);
        String stringValue = getPropertyValue(setting);
        Object convertedValue = convertSettingValue(setting, stringValue);
        if (convertedValue == null) {
            log.debug("convertedValue was null, using default");
            convertedValue = setting.getDefaultValue();
        }

        if (log.isDebugEnabled()) {
            log.debug("returning: " + convertedValue);
        }

        T result = returnType.cast(convertedValue);
        return result;
    }

    /**
     *  Fetches the setting value from database.
     */
    private String getPropertyValue(MaintenanceSettingType setting) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Value");
        sql.append("FROM MaintenanceTaskSettings");
        sql.append("WHERE SettingType").eq_k(setting);

        String result;
        try {
            result = jdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return result;
    }

    private Object convertSettingValue(MaintenanceSettingType setting, String value) {
        return InputTypeFactory.convertPropertyValue(setting.getType(), value);
    }
}
