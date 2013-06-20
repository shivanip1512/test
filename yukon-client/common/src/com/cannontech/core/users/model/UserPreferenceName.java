package com.cannontech.core.users.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This enumerates all possible preferences and links each to their options (in separate enums).
 * 
 * {@link YukonRoleProperty}    Inspiration.
 */
public enum UserPreferenceName implements DisplayableEnum {
    
    GRAPH_DISPLAY_TIME_DURATION(InputTypeFactory.enumType(PreferenceGraphTimeDurationOption.class),
        PreferenceGraphTimeDurationOption.getDefault().name()),
    GRAPH_DISPLAY_VISUAL_TYPE(InputTypeFactory.enumType(PreferenceGraphVisualTypeOption.class),
        PreferenceGraphVisualTypeOption.getDefault().name());
//    ERROR_PAGE_SHOW_DETAILS(InputTypeFactory.enumType(PreferenceShowHide.class), PreferenceShowHide.HIDE.name()),
//    HOME_URL(InputTypeFactory.stringType(), "/dashboard");

    // Setup the internal map for easy translations:
    private final static ImmutableMap<String, UserPreferenceName> stringToName;
    static {
        Builder<String, UserPreferenceName> builder = ImmutableMap.builder();
        for (UserPreferenceName prefName : values()) {
            builder.put(prefName.name(), prefName);
        }
        stringToName = builder.build();
    }
    
    public static UserPreferenceName getName(String forName) {
        UserPreferenceName name = stringToName.get(forName);
        return name;
    }

    final private InputType<?> valueType;
    final private String defaultValue;
    
    private UserPreferenceName(InputType<?> valueType, String defaultValue) {
        this.valueType = valueType;
        this.defaultValue = defaultValue;
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

}