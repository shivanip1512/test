package com.cannontech.stars.energyCompany;

import static com.cannontech.core.roleproperties.InputTypeFactory.booleanType;
import static com.cannontech.core.roleproperties.InputTypeFactory.integerType;
import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;
import static com.cannontech.stars.energyCompany.model.SettingCategory.ACCOUNT;
import static com.cannontech.stars.energyCompany.model.SettingCategory.HARDWARE;
import static com.cannontech.stars.energyCompany.model.SettingCategory.MISC;
import static com.cannontech.stars.energyCompany.model.SettingStatus.ALWAYS_SET;
import static com.cannontech.stars.energyCompany.model.SettingStatus.UNSET;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.enums.TemperatureUnit;
import com.cannontech.stars.energyCompany.model.SettingCategory;
import com.cannontech.stars.energyCompany.model.SettingStatus;
import com.cannontech.web.input.type.InputType;

public enum EnergyCompanySettingType implements DisplayableEnum {

    // ACCOUNT
    ACCOUNT_NUMBER_LENGTH(ACCOUNT, ALWAYS_SET, integerType(), 0),
    ALTERNATE_PROGRAM_ENROLLMENT(ACCOUNT, ALWAYS_SET, booleanType(), false),
    APPLICABLE_POINT_TYPE_KEY(ACCOUNT, UNSET, stringType(), null),
    AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS(ACCOUNT, ALWAYS_SET, booleanType(), true),
    ROTATION_DIGIT_LENGTH(ACCOUNT, ALWAYS_SET, integerType(), 0),

    // HARDWARE
    AUTOMATIC_CONFIGURATION(HARDWARE, ALWAYS_SET, booleanType(), false),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY(HARDWARE, ALWAYS_SET, booleanType(), true),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND(HARDWARE, ALWAYS_SET, booleanType(), false),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_7_DAY(HARDWARE, ALWAYS_SET, booleanType(), false),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL(HARDWARE, ALWAYS_SET, booleanType(), true),
    DEFAULT_TEMPERATURE_UNIT(HARDWARE, ALWAYS_SET,  InputTypeFactory.enumType(TemperatureUnit.class), TemperatureUnit.F),
    METER_MCT_BASE_DESIGNATION(HARDWARE, ALWAYS_SET, InputTypeFactory.enumType(MeteringType.class), MeteringType.stars),
    SERIAL_NUMBER_VALIDATION(HARDWARE, ALWAYS_SET, InputTypeFactory.enumType(SerialNumberValidation.class), SerialNumberValidation.NUMERIC),
    TRACK_HARDWARE_ADDRESSING(HARDWARE, ALWAYS_SET, booleanType(), false),

    // MISC
    ADMIN_EMAIL_ADDRESS(MISC, ALWAYS_SET, stringType(), "info@cannontech.com"),
    ADMIN_ALLOW_DESIGNATION_CODE(MISC, ALWAYS_SET, booleanType(), false),
    BROADCAST_OPT_OUT_CANCEL_SPID(MISC, UNSET, integerType(), 0),
    ENERGY_COMPANY_DEFAULT_TIME_ZONE(MISC, ALWAYS_SET, stringType(), "CST"),
    INHERIT_PARENT_APP_CATS(MISC, ALWAYS_SET, booleanType(), true),
    OPTOUT_NOTIFICATION_RECIPIENTS(MISC, ALWAYS_SET, stringType(), null),
    OPTIONAL_PRODUCT_DEV(MISC, UNSET, stringType(), "00000000"),
    SINGLE_ENERGY_COMPANY(MISC, ALWAYS_SET, booleanType(), true),
    ;

    private final SettingCategory category;
    private final SettingStatus status;
    private final Object defaultValue;
    private final InputType<?> type;

    private EnergyCompanySettingType(SettingCategory category, SettingStatus status, InputType<?> type, Object defaultValue) {
        this.category = category;
        this.status = status;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public SettingStatus getDefaultStatus() {
        return status;
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