package com.cannontech.maintenance;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.web.input.type.InputType;

public enum MaintenanceSettingType implements DisplayableEnum {
    NO_OF_MONTHS(InputTypeFactory.enumType(DurationType.class), DurationType.TWO_YEARS);

    private final InputType<?> type;
    private final Object defaultValue;

    private MaintenanceSettingType(InputType<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public InputType<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.setting." + name();
    }

}
