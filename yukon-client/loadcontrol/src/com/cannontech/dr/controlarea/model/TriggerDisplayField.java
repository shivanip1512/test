package com.cannontech.dr.controlarea.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.LoadManagementUtils;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;

public enum TriggerDisplayField {

    VALUE {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            String result = null;
            if (trigger.getTriggerType()
                       .equalsIgnoreCase(ControlAreaTrigger.TriggerType.STATUS.getDbString())) {
                result = LoadManagementUtils.getTriggerStateValue(trigger);
            } else {
                result = NMBR_FORMATTER.format(trigger.getPointValue());
            }
            return buildResolvable(name(), result);
        }
    },
    THRESHOLD {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            String result = null;
            if (trigger.getTriggerType()
                       .equalsIgnoreCase(ControlAreaTrigger.TriggerType.STATUS.getDbString())) {
                result = LoadManagementUtils.getTriggerStateThreshold(trigger);
            } else {
                result = NMBR_FORMATTER.format(trigger.getThreshold());
            }
            return buildResolvable(name(), result);
        }
    },
    PEAK {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            if (trigger.getTriggerType()
                       .equalsIgnoreCase(ControlAreaTrigger.TriggerType.THRESHOLD.getDbString())) {
                return buildResolvable(name(), NMBR_FORMATTER.format(trigger.getPeakPointValue()));                
            } else {
                return blankFieldResolvable;
            }
        }
    },
    PROJECTION {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            if (trigger.getTriggerType()
                       .equalsIgnoreCase(ControlAreaTrigger.TriggerType.THRESHOLD.getDbString())) {
                return buildResolvable(name(), NMBR_FORMATTER.format(trigger.getProjectedPointValue()));
            } else {
                return blankFieldResolvable;
            }
        }
    },
    ATKU {
        @Override
        public MessageSourceResolvable getValue(LMControlAreaTrigger trigger) {
            if (trigger.getTriggerType()
                       .equalsIgnoreCase(ControlAreaTrigger.TriggerType.THRESHOLD.getDbString())) {
                String result = trigger.getThresholdKickPercent().intValue() <= 0 ? "DISABLED KU"
                        : trigger.getThresholdKickPercent().toString();
                return buildResolvable(name(), result);
            } else {
                return blankFieldResolvable;
            }
        }
    };
    private static final java.text.DecimalFormat NMBR_FORMATTER;
    static
    {
        NMBR_FORMATTER = new java.text.DecimalFormat();
        NMBR_FORMATTER.setMaximumFractionDigits( 3 );
        NMBR_FORMATTER.setMinimumFractionDigits( 1 );
    }
    private final static String baseKey = "yukon.web.modules.dr.controlAreaTrigger.value";
    private final static MessageSourceResolvable blankFieldResolvable = new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");

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
