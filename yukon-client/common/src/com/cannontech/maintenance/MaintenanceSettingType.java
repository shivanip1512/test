package com.cannontech.maintenance;

import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSetMultimap.Builder;

public enum MaintenanceSettingType implements DisplayableEnum {
    NO_OF_MONTHS(MaintenanceTaskName.POINT_DATA_PRUNING, InputTypeFactory.enumType(DurationType.class),
                 DurationType.TWO_YEARS);
    private static final ImmutableSetMultimap<MaintenanceTaskName, MaintenanceSettingType> maintenanceTaskSettingMapping;
    private final InputType<?> type;
    private final Object defaultValue;
    private final MaintenanceTaskName task;

    static {
        final Builder<MaintenanceTaskName, MaintenanceSettingType> b = ImmutableSetMultimap.builder();
        for (MaintenanceSettingType maintenanceSettingType : values()) {
            b.put(maintenanceSettingType.getTask(), maintenanceSettingType);
        }
        maintenanceTaskSettingMapping = b.build();

    }

    private MaintenanceSettingType(MaintenanceTaskName task, InputType<?> type, Object defaultValue) {
        this.type = type;
        this.task = task;
        this.defaultValue = defaultValue;
    }

    public static ImmutableSetMultimap<MaintenanceTaskName, MaintenanceSettingType> getMaintenancetasksettingmapping() {
        return maintenanceTaskSettingMapping;
    }

    public InputType<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public MaintenanceTaskName getTask() {
        return task;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.setting." + name();
    }

    public static Set<MaintenanceSettingType> getSettingsForTask(MaintenanceTaskName task) {
        return maintenanceTaskSettingMapping.get(task);
    }
}
