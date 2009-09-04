package com.cannontech.dr.controlarea.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.LoadManagementUtils;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;

public enum TriggerDisplayField {

    VALUE_THRESHOLD {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            ControlAreaTrigger.TriggerType triggerType =
                ControlAreaTrigger.TriggerType.valueOf(trigger.getTriggerType().toUpperCase());
            Object[] result = null;
            if (triggerType == ControlAreaTrigger.TriggerType.STATUS) {
                result = new Object[] {
                        LoadManagementUtils.getTriggerStateValue(trigger),
                        LoadManagementUtils.getTriggerStateThreshold(trigger) };
            } else {
                result = new Object[] { trigger.getPointValue(),
                        trigger.getThreshold() };
            }
            return buildResolvable(triggerType + "." + name(), result);
        }
    },
    PEAK_PROJECTION {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            if (trigger.getTriggerType()
                       .equalsIgnoreCase(ControlAreaTrigger.TriggerType.THRESHOLD.getDbString())) {
                return buildResolvable(name(),
                                       trigger.getPeakPointValue(),
                                       trigger.getProjectedPointValue());
            }
            return blankFieldResolvable;
        }
    },
    ATKU {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            if (trigger.getTriggerType()
                       .equalsIgnoreCase(ControlAreaTrigger.TriggerType.THRESHOLD.getDbString())) {
                String result = trigger.getThresholdKickPercent() <= 0 ? "DISABLED KU"
                        : trigger.getThresholdKickPercent().toString();
                return buildResolvable(name(), result);
            }
            return blankFieldResolvable;
        }
    };

    private final static String baseKey = "yukon.web.modules.dr.controlAreaTrigger.value";
    private final static MessageSourceResolvable blankFieldResolvable =
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");

    private static String getBaseKey(String name) {
        return baseKey + "." + name;
    }

    private static MessageSourceResolvable buildResolvable(String name,
            Object... args) {
        return new YukonMessageSourceResolvable(getBaseKey(name), args);
    }

    public abstract MessageSourceResolvable getValue(
            LMControlAreaTrigger trigger);

    public boolean isCssClass() {
        return false;
    }
}
