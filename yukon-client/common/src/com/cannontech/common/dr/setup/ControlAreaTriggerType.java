package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

public enum ControlAreaTriggerType implements DisplayableEnum, DatabaseRepresentationSource {
    THRESHOLD("Threshold"),
    THRESHOLD_POINT("Threshold Point"),
    STATUS("Status");

    private String triggerType;
    private final static ImmutableMap<String, ControlAreaTriggerType> lookupByTriggerType;
    static {
        try {
            ImmutableMap.Builder<String, ControlAreaTriggerType> triggerTypeBuilder = ImmutableMap.builder();
            for (ControlAreaTriggerType type : values()) {
                triggerTypeBuilder.put(type.triggerType, type);
            }
            lookupByTriggerType = triggerTypeBuilder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private ControlAreaTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerTypeValue() {
        return triggerType;
    }

    public static ControlAreaTriggerType getTriggerValue(String value) {
        ControlAreaTriggerType controlAreaTriggerTypeType = lookupByTriggerType.get(value);
        checkArgument(controlAreaTriggerTypeType != null, controlAreaTriggerTypeType);
        return controlAreaTriggerTypeType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.controlArea.triggerType." + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return triggerType;
    }

}
