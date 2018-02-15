package com.cannontech.maintenance;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;

public enum MaintenanceSettingType implements DisplayableEnum {

    // Setting for Point Data Pruning
    POINT_DATA_PRUNING_ENABLED(false, false, InputTypeFactory.booleanType()),
    POINT_DATA_PRUNING_NO_OF_MONTHS(DurationType.TWO_YEARS, true, InputTypeFactory.enumType(DurationType.class)),

    // Setting for Duplicate Point Data Pruning
    DUPLICATE_POINT_DATA_PRUNING_ENABLED(true, false, InputTypeFactory.booleanType()),

    // Setting for DR Reconciliation
    DR_RECONCILIATION_ENABLED(true, false, InputTypeFactory.booleanType());

    private final Object defaultValue;
    private final boolean displayable;
    private final InputType<?> type;

    private MaintenanceSettingType(Object defaultValue, boolean displayable, InputType<?> type) {
        this.defaultValue = defaultValue;
        this.displayable = displayable;
        this.type = type;
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

    @Override
    public String getFormatKey() {
        return "yukon.common.setting." + name();
    }
}