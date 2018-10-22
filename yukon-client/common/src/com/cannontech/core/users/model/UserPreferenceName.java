package com.cannontech.core.users.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.device.commands.CommandPriority;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.TimeRange;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.YNBoolean;
import com.cannontech.system.OnOff;
import com.cannontech.web.input.type.InputType;

/**
 * This enumerates all possible preferences and links each to their options (in separate enums).
 * 
 * {@link YukonRoleProperty}    Inspiration.
 */
public enum UserPreferenceName implements DisplayableEnum {
    
    // Editable preferences
    ALERT_FLASH(InputTypeFactory.enumType(OnOff.class), OnOff.ON.name(), PreferenceType.EDITABLE),
    ALERT_SOUND(InputTypeFactory.enumType(OnOff.class), OnOff.OFF.name(), PreferenceType.EDITABLE),
    COMMANDER_PRIORITY(InputTypeFactory.integerType(), String.valueOf(CommandPriority.maxPriority), 
                                                        PreferenceType.EDITABLE), 
    COMMANDER_QUEUE_COMMAND(InputTypeFactory.booleanType(), Boolean.FALSE.toString(), PreferenceType.EDITABLE),
    SMART_NOTIFICATIONS_DAILY_TIME(InputTypeFactory.stringType(), null, PreferenceType.EDITABLE_SPECIAL),
    
    // Non-editable/auto-set preferences
    GRAPH_DISPLAY_TIME_DURATION(InputTypeFactory.enumType(PreferenceGraphTimeDurationOption.class),
               PreferenceGraphTimeDurationOption.getDefault().name(), PreferenceType.NONEDITABLE),
    GRAPH_DISPLAY_VISUAL_TYPE(InputTypeFactory.enumType(PreferenceGraphVisualTypeOption.class),
               PreferenceGraphVisualTypeOption.getDefault().name(), PreferenceType.NONEDITABLE),
    TREND_ZOOM(InputTypeFactory.enumType(PreferenceTrendZoomOption.class),
               PreferenceTrendZoomOption.getDefault().name(), PreferenceType.NONEDITABLE),
    DISPLAY_EVENT_RANGE(InputTypeFactory.enumType(TimeRange.class), TimeRange.DAY_1.name(), PreferenceType.NONEDITABLE),
    COMMANDER_RECENT_TARGETS(InputTypeFactory.stringType(), null, PreferenceType.NONEDITABLE),
    PORTER_QUEUE_COUNTS_ZOOM(InputTypeFactory.enumType(PreferencePorterQueueCountsZoomOption.class),
                             PreferencePorterQueueCountsZoomOption.getDefault().name(), PreferenceType.NONEDITABLE),

    // Temporary preferences, cache lived
    COMMANDER_LAST_ROUTE_ID(InputTypeFactory.integerType(), null, PreferenceType.TEMPORARY),
    COMMANDER_LAST_SERIAL_NUMBER(InputTypeFactory.stringType(), null, PreferenceType.TEMPORARY),
    COMMANDER_LAST_TARGET(InputTypeFactory.stringType(), null, PreferenceType.TEMPORARY),
    COMMANDER_LAST_PAO_ID(InputTypeFactory.integerType(), null, PreferenceType.TEMPORARY),
    TREND_TEMPERATURE(InputTypeFactory.enumType(YNBoolean.class), YNBoolean.NO.getDatabaseRepresentation().toString(), PreferenceType.NONEDITABLE);

    final private InputType<?> valueType;
    final private String defaultValue;
    final private PreferenceType preferenceType;

    private UserPreferenceName(InputType<?> valueType, String defaultValue, PreferenceType preferenceType) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.preferenceType = preferenceType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences." + name();
    }

    public InputType<?> getValueType() {
        return valueType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public PreferenceType getPreferenceType() {
        return preferenceType;
    }

    public static List<UserPreferenceName> getUserPreferencesByType(PreferenceType preferenceType) {
        List<UserPreferenceName> prefList =
            Arrays.asList(UserPreferenceName.values())
                .stream()
                .filter(preference -> (preference.preferenceType == preferenceType))
                .collect(Collectors.toList());

        return prefList;
    }
}