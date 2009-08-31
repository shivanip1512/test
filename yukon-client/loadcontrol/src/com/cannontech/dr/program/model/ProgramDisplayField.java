package com.cannontech.dr.program.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;


public enum ProgramDisplayField {
    STATE {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            ProgramState state = ProgramState.valueOf(program.getProgramStatus());
            return new YukonMessageSourceResolvable(getBaseKey(name()) + "."
                                             + state.name());
        }
    },
    STATE_CLASSNAME {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            ProgramState state = ProgramState.valueOf(program.getProgramStatus());
            return YukonMessageSourceResolvable.createDefault(getBaseKey(name()) + "."
                                             + state.name(), state.name());
        }
    },
    START {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            if (program.getDisableFlag()) {
                return blankFieldResolvable;
            } else {
                if (program.getStartTime() == null
                        || program.getStartTime().before(CtiUtilities.get1990GregCalendar())) {
                    return blankFieldResolvable;
                }
                return buildResolvable(name(), program.getStartTime().getTime());
            }
        }
    },
    STOP {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            if (program.getDisableFlag().booleanValue()) {
                return blankFieldResolvable;
            } else {
                // return dashes if stop time is null, <1990 or
                // >= 2035
                if (program.getStopTime() == null
                        || program.getStopTime().before(CtiUtilities.get1990GregCalendar())
                        || program.getStopTime().compareTo(CtiUtilities.get2035GregCalendar()) >= 0) {
                    return blankFieldResolvable;
                }
                return buildResolvable(name(), program.getStartTime().getTime());
            }
        }
    },
    CURRENT_GEAR {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            if (program instanceof IGearProgram) {
                LMProgramDirectGear gear = ((IGearProgram) program).getCurrentGear();
                if (gear != null) {
                    return buildResolvable(name(), gear.getGearName());
                }
            }
            return blankFieldResolvable;
        }
    },
    PRIORITY {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            return buildResolvable(name(),
                                   program.getStartPriority().intValue() <= 0
                                       ? 1 : program.getStartPriority());
        }
    },
    REDUCTION {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            return buildResolvable(name(), program.getReductionTotal());
        }
    },
    LOAD_CAPACITY {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            return buildResolvable(name(), new Double(0.0));
        }
    },
    ;

    private final static String baseKey = "yukon.web.modules.dr.program.value";
    private final static MessageSourceResolvable blankFieldResolvable =
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
    private static String getBaseKey(String name) {
        return baseKey + "." + name;
    }
    private static MessageSourceResolvable buildResolvable(String name, Object...args) {
        return new YukonMessageSourceResolvable(getBaseKey(name), args);
    }

    public abstract MessageSourceResolvable getValue(LMProgramBase program);
}
