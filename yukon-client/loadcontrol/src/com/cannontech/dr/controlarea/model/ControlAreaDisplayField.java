package com.cannontech.dr.controlarea.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.spring.YukonSpringHook;

public enum ControlAreaDisplayField {

    STATE {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            ControlAreaState state = ControlAreaState.valueOf(area.getControlAreaState());
            return new YukonMessageSourceResolvable(getBaseKey(name()) + "." + state.name());
        }
    },
    STATE_CLASSNAME {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            ControlAreaState state = ControlAreaState.valueOf(area.getControlAreaState());
            return YukonMessageSourceResolvable.createDefault(getBaseKey(name()) + "." + state.name(),
                                                              state.name());
        }

        @Override
        public boolean isCssClass() {
            return true;
        }
    },
    PRIORITY {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            int result = area.getCurrentPriority().intValue() <= 0 ? 1
                    : area.getCurrentPriority();
            return buildResolvable(name(), result);
        }
    },
    START {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            SystemDateFormattingService systemDateFormattingService = (SystemDateFormattingService) YukonSpringHook.getBean("systemDateFormattingService");
            Calendar startDate = null;
            if (area.getDailyStartTime() > -1) {
                startDate = systemDateFormattingService.getSystemCalendar();
                startDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                startDate.set(GregorianCalendar.MINUTE, 0);
                startDate.set(GregorianCalendar.SECOND, area.getDailyStartTime());
            }

            if (startDate == null || startDate.before(CtiUtilities.get1990GregCalendar())) {
                return blankFieldResolvable;
            }
            return buildResolvable(name(), startDate.getTime());
        }
    },
    STOP {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            SystemDateFormattingService systemDateFormattingService = (SystemDateFormattingService) YukonSpringHook.getBean("systemDateFormattingService");
            Calendar stopDate = null;
            if (area.getDailyStopTime() > -1) {
                stopDate = systemDateFormattingService.getSystemCalendar();
                stopDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                stopDate.set(GregorianCalendar.MINUTE, 0);
                stopDate.set(GregorianCalendar.SECOND, area.getDailyStopTime());
            }

            if (stopDate == null || stopDate.before(CtiUtilities.get1990GregCalendar())
                    || stopDate.compareTo(CtiUtilities.get2035GregCalendar()) >= 0) {
                return blankFieldResolvable;
            }
            return buildResolvable(name(), stopDate.getTime());
        }
    },
    LOAD_CAPACITY {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            return buildResolvable(name(), new Double(0.0));
        }
    };

    private final static String baseKey = "yukon.web.modules.dr.controlArea.value";
    private final static MessageSourceResolvable blankFieldResolvable = new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");

    private static String getBaseKey(String name) {
        return baseKey + "." + name;
    }

    private static MessageSourceResolvable buildResolvable(String name,
            Object... args) {
        return new YukonMessageSourceResolvable(getBaseKey(name), args);
    }

    public abstract MessageSourceResolvable getValue(LMControlArea area);

    public boolean isCssClass() {
        return false;
    }
}
