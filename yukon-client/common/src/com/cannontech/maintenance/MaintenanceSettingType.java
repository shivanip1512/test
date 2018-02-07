package com.cannontech.maintenance;

import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public enum MaintenanceSettingType implements DisplayableEnum {

    // Setting for Point Data Pruning
    POINT_DATA_PRUNING_ENABLED(false, false, InputTypeFactory.booleanType(), MaintenanceTaskType.POINT_DATA_PRUNING),
    POINT_DATA_PRUNING_NO_OF_MONTHS(DurationType.TWO_YEARS, true, InputTypeFactory.enumType(DurationType.class),
                                    MaintenanceTaskType.POINT_DATA_PRUNING),

    // Setting for Duplicate Point Data Pruning
    DUPLICATE_POINT_DATA_PRUNING_ENABLED(false, false, InputTypeFactory.booleanType(),
                                         MaintenanceTaskType.DUPLICATE_POINT_DATA_PRUNING),

    // Setting for DR Reconciliation
    DR_RECONCILIATION_ENABLED(true, false, InputTypeFactory.booleanType(), MaintenanceTaskType.DR_RECONCILIATION);

    private final Object defaultValue;
    private final boolean displayable;
    private final InputType<?> type;
    private final MaintenanceTaskType maintenanceTaskType;
    private static ImmutableMap<MaintenanceTaskType, Set<MaintenanceSettingType>> allTaskSettings;

    static {
        buildAllSettings();
    }

    private MaintenanceSettingType(Object defaultValue, boolean displayable, InputType<?> type,
            MaintenanceTaskType maintenanceTaskType) {
        this.defaultValue = defaultValue;
        this.displayable = displayable;
        this.type = type;
        this.maintenanceTaskType = maintenanceTaskType;
    }

    /**
     * Builds a Map of Maintenance Type and its all settings (MaintenanceSettingType)
     */
    private static void buildAllSettings() {
        ImmutableMap.Builder<MaintenanceTaskType, Set<MaintenanceSettingType>> allSettingsBuilder =
            ImmutableMap.builder();
        
        Set<MaintenanceTaskType> tasks = Sets.newHashSet(MaintenanceTaskType.values());
        Set<MaintenanceSettingType> settings = Sets.newHashSet(MaintenanceSettingType.values());
        tasks.stream().forEach(task -> {
            ImmutableSet.Builder<MaintenanceSettingType> settingsBuilder = ImmutableSet.builder();
            settings.stream().forEach(setting -> {
                if (setting.getMaintenanceTaskType() == task) {
                    settingsBuilder.add(setting);
                }
            });
            allSettingsBuilder.put(task, settingsBuilder.build());
        });
        allTaskSettings = allSettingsBuilder.build();
    }

    // There is an assumption here that the Maintenance Task Setting is name exactly after the MaintenanceTaskType + "_ENABLED"
    public static MaintenanceSettingType getEnabledSetting(MaintenanceTaskType taskType) {
        return MaintenanceSettingType.valueOf(taskType.name() + "_ENABLED");
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isDisplayable() {
        return displayable;
    }

    public InputType<?> getType() {
        return type;
    }

    public MaintenanceTaskType getMaintenanceTaskType() {
        return maintenanceTaskType;
    }

    public static Set<MaintenanceSettingType> getSettingsForTask(MaintenanceTaskType type) {
        return allTaskSettings.get(type);
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.setting." + name();
    }
}