package com.cannontech.stars.energyCompany;

import static com.cannontech.core.roleproperties.InputTypeFactory.booleanType;
import static com.cannontech.core.roleproperties.InputTypeFactory.integerType;
import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;
import static com.cannontech.stars.energyCompany.model.SettingCategory.ACCOUNT;
import static com.cannontech.stars.energyCompany.model.SettingCategory.HARDWARE;
import static com.cannontech.stars.energyCompany.model.SettingCategory.MISC;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.stars.energyCompany.model.SettingCategory;
import com.cannontech.web.input.type.InputType;

public enum EnergyCompanySettingType implements DisplayableEnum {

    // ACCOUNT
    ACCOUNT_NUMBER_LENGTH(ACCOUNT, integerType(), 0),
    ALTERNATE_PROGRAM_ENROLLMENT(ACCOUNT, booleanType(), false),
    APPLICABLE_POINT_TYPE_KEY(ACCOUNT, true, stringType(), null),
    AUTO_CREATE_LOGIN_FOR_ACCOUNT(ACCOUNT, booleanType(), false),
    AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS(ACCOUNT, booleanType(), true),
    ROTATION_DIGIT_LENGTH(ACCOUNT, integerType(), 0),
    ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY(ACCOUNT, booleanType(), false),
    // HARDWARE
    AUTOMATIC_CONFIGURATION(HARDWARE, booleanType(), false),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY(HARDWARE, booleanType(), true),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND(HARDWARE, booleanType(), false),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_7_DAY(HARDWARE, booleanType(), false),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL(HARDWARE, booleanType(), true),
    CALL_TRACKING_NUMBER_AUTO_GEN(HARDWARE, stringType(), null),    // valid values are boolean OR actual number to start with
    DEFAULT_TEMPERATURE_UNIT(HARDWARE,  InputTypeFactory.enumType(TemperatureUnit.class), TemperatureUnit.FAHRENHEIT),
    INVENTORY_CHECKING(HARDWARE, booleanType(), true),
    INVENTORY_CHECKING_CREATE(HARDWARE, booleanType(), true),
    SERIAL_NUMBER_VALIDATION(HARDWARE, InputTypeFactory.enumType(SerialNumberValidation.class), SerialNumberValidation.NUMERIC),
    SUPPRESS_IN_OUT_SERVICE_MESSAGES(HARDWARE, booleanType(), false),
    TRACK_HARDWARE_ADDRESSING(HARDWARE, booleanType(), false),
    WORK_ORDER_NUMBER_AUTO_GEN(HARDWARE, booleanType(), false),

    // MISC
    ADMIN_EMAIL_ADDRESS(MISC, stringType(), "yukon@eaton.com"),
    ADMIN_ALLOW_DESIGNATION_CODE(MISC, booleanType(), false),
    BROADCAST_OPT_OUT_CANCEL_SPID(MISC, true, integerType(), 1),
    ENERGY_COMPANY_DEFAULT_TIME_ZONE(MISC, stringType(), "America/Chicago"),
    INHERIT_PARENT_APP_CATS(MISC, booleanType(), true),
    OPTOUT_NOTIFICATION_RECIPIENTS(MISC, stringType(), null),
    OPTIONAL_PRODUCT_DEV(MISC, true, stringType(), "00000000"),
    SINGLE_ENERGY_COMPANY(MISC, booleanType(), true),
    ;

    private final SettingCategory category;
    private final boolean usesEnabledField;
    private final Object defaultValue;
    private final InputType<?> type;

    private EnergyCompanySettingType(SettingCategory category, InputType<?> type, Object defaultValue) {
        this(category, false, type, defaultValue);
    }

    private EnergyCompanySettingType(SettingCategory category, boolean usesEnabledField, InputType<?> type,
            Object defaultValue) {
        this.category = category;
        this.usesEnabledField = usesEnabledField;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public boolean isUsesEnabledField() {
        return usesEnabledField;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.energyCompanySetting." + name();
    }

    public String getDescriptionKey() {
        return getFormatKey() + ".description";
    }

    public InputType<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public SettingCategory getCategory() {
        return category;
    }
}