package com.cannontech.dr.program.model;

import java.util.Comparator;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.dr.model.DisplayablePaoComparator;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.user.YukonUserContext;


public enum ProgramDisplayField {
    NAME {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            return buildResolvable(name(), program.getYukonName());
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                final YukonUserContext userContext, boolean isDescending) {
            return new DisplayablePaoComparator(userContext, isDescending);
        }
    },
    ENABLED {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            String enabled = String.valueOf(!program.getDisableFlag());
            return YukonMessageSourceResolvable.createDefaultWithoutCode(enabled);
        }
        
        @Override
        public Comparator<DisplayablePao> getSorter(ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                                                    final YukonUserContext userContext, boolean isDescending) {
            return null;
        }
    },
    STATE {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            ProgramState state = ProgramState.valueOf(program.getProgramStatus());
            return new YukonMessageSourceResolvable(getBaseKey(name()) + "."
                                             + state.name());
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMProgramBase program1 = mapper.map(pao1);
                    LMProgramBase program2 = mapper.map(pao2);
                    if (program1 == program2) {
                        return 0;
                    }
                    if (program1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (program2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer state1 = program1.getProgramStatus();
                    Integer state2 = program2.getProgramStatus();
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    },
    MANUAL_ACTIVE {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            boolean manualActive = program.getProgramStatus() == LMProgramBase.STATUS_MANUAL_ACTIVE;
            return YukonMessageSourceResolvable.createDefaultWithoutCode(String.valueOf(manualActive));
        }
        
        @Override
        public Comparator<DisplayablePao> getSorter(ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                                                    final YukonUserContext userContext, boolean isDescending) {
            return null;
        }
    },
    STATE_CLASSNAME {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            ProgramState state = ProgramState.valueOf(program.getProgramStatus());
            return YukonMessageSourceResolvable.createDefault(getBaseKey(name()) + "."
                                             + state.name(), state.name());
        }

        @Override
        public boolean isCssClass() {
            return true;
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, boolean isDescending) {
            return null;
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

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMProgramBase program1 = mapper.map(pao1);
                    LMProgramBase program2 = mapper.map(pao2);
                    if (program1 == program2) {
                        return 0;
                    }
                    if (program1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (program2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    int retVal = program1.getStartTime().compareTo(program2.getStartTime());
                    return isDescending ? (0 - retVal) : retVal;
                }};
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
                return buildResolvable(name(), program.getStopTime().getTime());
            }
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMProgramBase program1 = mapper.map(pao1);
                    LMProgramBase program2 = mapper.map(pao2);
                    if (program1 == program2) {
                        return 0;
                    }
                    if (program1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (program2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    int retVal = program1.getStopTime().compareTo(program2.getStopTime());
                    return isDescending ? (0 - retVal) : retVal;
                }};
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

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMProgramBase program1 = mapper.map(pao1);
                    LMProgramBase program2 = mapper.map(pao2);

                    LMProgramDirectGear gear1 = null;
                    LMProgramDirectGear gear2 = null;
                    if (program1 != null && program1 instanceof IGearProgram) {
                        gear1 = ((IGearProgram) program1).getCurrentGear();
                    }
                    if (program2 != null && program2 instanceof IGearProgram) {
                        gear2 = ((IGearProgram) program2).getCurrentGear();
                    }

                    if (gear1 == gear2) {
                        return 0;
                    }
                    if (gear1 == null) {
                        return isDescending ? - 1 : 1;
                    }
                    if (gear2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    int retVal = gear1.getGearName().compareTo(gear2.getGearName());
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    },
    PRIORITY {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            return buildResolvable(name(),
                                   program.getStartPriority() <= 0
                                       ? 1 : program.getStartPriority());
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMProgramBase program1 = mapper.map(pao1);
                    LMProgramBase program2 = mapper.map(pao2);
                    if (program1 == program2) {
                        return 0;
                    }
                    if (program1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (program2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer priority1 = program1.getStartPriority() <= 0
                        ? 1 : program1.getStartPriority();
                    Integer priority2 = program2.getStartPriority() <= 0
                        ? 1 : program2.getStartPriority();
                    int retVal = priority1.compareTo(priority2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    },
    REDUCTION {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            return buildResolvable(name(), program.getReductionTotal());
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMProgramBase program1 = mapper.map(pao1);
                    LMProgramBase program2 = mapper.map(pao2);
                    if (program1 == program2) {
                        return 0;
                    }
                    if (program1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (program2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Double reduction1 = program1.getReductionTotal();
                    Double reduction2 = program2.getReductionTotal();
                    int retVal = reduction1.compareTo(reduction2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    },
    LOAD_CAPACITY {
        @Override
        public MessageSourceResolvable getValue(LMProgramBase program) {
            // TO BE DETERMINED
            return buildResolvable(name(), new Double(0.0));
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMProgramBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMProgramBase program1 = mapper.map(pao1);
                    LMProgramBase program2 = mapper.map(pao2);
                    if (program1 == program2) {
                        return 0;
                    }
                    if (program1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (program2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Double state1 = 0.0;  // TO BE DETERMINED
                    Double state2 = 0.0;
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    },
    ;

    private static abstract class MappingComparator
        implements Comparator<DisplayablePao> {
        ObjectMapper<DisplayablePao, LMProgramBase> mapper;
        MappingComparator(ObjectMapper<DisplayablePao, LMProgramBase> mapper) {
            this.mapper = mapper;
        }
    }

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
    public boolean isCssClass() {
        return false;
    }

    public abstract Comparator<DisplayablePao> getSorter(
            ObjectMapper<DisplayablePao, LMProgramBase> mapper,
            YukonUserContext userContext, boolean isDescending);
}
