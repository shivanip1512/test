package com.cannontech.dr.controlarea.model;

import java.util.Date;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.dr.loadgroup.model.LoadGroupState;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;

public enum LoadGroupDisplayField {

    STATE {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            LoadGroupState state = LoadGroupState.valueOf(group.getGroupControlState());
            return new YukonMessageSourceResolvable(getBaseKey(name()) + "." + state.name());
        }
    },
    STATE_CLASSNAME {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            LoadGroupState state = LoadGroupState.valueOf(group.getGroupControlState());
            return YukonMessageSourceResolvable.createDefault(getBaseKey(name()) + "." + state.name(),
                                                              state.name());
        }

        @Override
        public boolean isCssClass() {
            return true;
        }
    },
    LAST_ACTION {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            Date lastActionDate = group.getGroupTime();
            if (lastActionDate == null || lastActionDate.before(CtiUtilities.get1990GregCalendar().getTime())) {
                return blankFieldResolvable;
            }
            return buildResolvable(name(), lastActionDate);
        }
    },
    CONTROL_DAILY {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            String value = CtiUtilities.decodeSecondsToTime(group.getCurrentHoursDaily()) ;
            return buildResolvable(name(), value);
        }
    },
    CONTROL_MONTHLY {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            String value = CtiUtilities.decodeSecondsToTime(group.getCurrentHoursMonthly()) ;
            return buildResolvable(name(), value);
        }
    },
    CONTROL_SEASONALLY {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            String value = CtiUtilities.decodeSecondsToTime(group.getCurrentHoursSeasonal()) ;
            return buildResolvable(name(), value);
        }
    },
    CONTROL_ANNUALLY {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            String value = CtiUtilities.decodeSecondsToTime(group.getCurrentHoursAnnually()) ;
            return buildResolvable(name(), value);
        }
    },
    REDUCTION {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            return buildResolvable(name(), group.getReduction());
        }
    },
    LOAD_CAPACITY {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            return buildResolvable(name(), new Double(0.0));
        }
    }    
    ;
    private final static String baseKey = "yukon.web.modules.dr.loadGroup.value";
    private final static MessageSourceResolvable blankFieldResolvable = new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");

    private static String getBaseKey(String name) {
        return baseKey + "." + name;
    }

    private static MessageSourceResolvable buildResolvable(String name,
            Object... args) {
        return new YukonMessageSourceResolvable(getBaseKey(name), args);
    }

    public abstract MessageSourceResolvable getValue(
            LMDirectGroupBase group);

    public boolean isCssClass() {
        return false;
    }
}
