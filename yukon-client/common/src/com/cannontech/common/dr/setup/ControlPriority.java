package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;


public enum ControlPriority implements DisplayableEnum {
    DEFAULT(1),
    MEDIUM(2),
    HIGH(3),
    HIGHEST(4);
    
    private Integer controlPriorityValue;
    
    private final static Logger log = YukonLogManager.getLogger(ControlPriority.class);
    private final static ImmutableMap<Integer, ControlPriority> lookupByPriority;
    
    static {
        try {
            ImmutableMap.Builder<Integer, ControlPriority> priorityBuilder = ImmutableMap.builder();
            for (ControlPriority controlPriority : values()) {
                priorityBuilder.put(controlPriority.controlPriorityValue, controlPriority);
            }
            lookupByPriority = priorityBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate priority.", e);
            throw e;
        }
    }

    private ControlPriority(Integer controlPriorityValue) {
        this.controlPriorityValue = controlPriorityValue;
    }

    public Integer getControlPriorityValue() {
        return controlPriorityValue;
    }
    
    public static ControlPriority getForPriority(Integer value) {
        ControlPriority controlPriority = lookupByPriority.get(value);
        checkArgument(controlPriority != null, controlPriority);
        return controlPriority;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }
}
