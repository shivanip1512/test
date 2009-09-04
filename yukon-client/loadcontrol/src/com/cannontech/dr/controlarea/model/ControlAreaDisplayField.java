package com.cannontech.dr.controlarea.model;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.dr.model.DisplayablePaoComparator;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public enum ControlAreaDisplayField {
    NAME {
        @Override
        public MessageSourceResolvable getValue(LMControlArea controlArea) {
            return buildResolvable(name(), controlArea.getYukonName());
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMControlArea> mapper,
                final YukonUserContext userContext, boolean isDescending) {
            return new DisplayablePaoComparator(userContext, isDescending);
        }
    },
    STATE {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            ControlAreaState state = ControlAreaState.valueOf(area.getControlAreaState());
            return new YukonMessageSourceResolvable(getBaseKey(name()) + "." + state.name());
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMControlArea> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMControlArea controlArea1 = mapper.map(pao1);
                    LMControlArea controlArea2 = mapper.map(pao2);
                    if (controlArea1 == controlArea2) {
                        return 0;
                    }
                    if (controlArea1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (controlArea2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer state1 = controlArea1.getControlAreaState();
                    Integer state2 = controlArea2.getControlAreaState();
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
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

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMControlArea> mapper,
                YukonUserContext userContext, boolean isDescending) {
            return null;
        }
    },
    PRIORITY {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            int result = area.getCurrentPriority() <= 0 ? 1
                    : area.getCurrentPriority();
            return buildResolvable(name(), result);
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMControlArea> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMControlArea controlArea1 = mapper.map(pao1);
                    LMControlArea controlArea2 = mapper.map(pao2);
                    if (controlArea1 == controlArea2) {
                        return 0;
                    }
                    if (controlArea1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (controlArea2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer priority1 = controlArea1.getCurrentPriority() <= 0
                        ? 1 : controlArea1.getCurrentPriority();
                    Integer priority2 = controlArea2.getCurrentPriority() <= 0
                        ? 1 : controlArea2.getCurrentPriority();
                    int retVal = priority1.compareTo(priority2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
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

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMControlArea> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMControlArea controlArea1 = mapper.map(pao1);
                    LMControlArea controlArea2 = mapper.map(pao2);
                    if (controlArea1 == controlArea2) {
                        return 0;
                    }
                    if (controlArea1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (controlArea2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer state1 = controlArea1.getDailyStartTime();
                    Integer state2 = controlArea2.getDailyStartTime();
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
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

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMControlArea> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMControlArea controlArea1 = mapper.map(pao1);
                    LMControlArea controlArea2 = mapper.map(pao2);
                    if (controlArea1 == controlArea2) {
                        return 0;
                    }
                    if (controlArea1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (controlArea2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer state1 = controlArea1.getDailyStopTime();
                    Integer state2 = controlArea2.getDailyStopTime();
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    },
    LOAD_CAPACITY {
        @Override
        public MessageSourceResolvable getValue(LMControlArea area) {
            return buildResolvable(name(), new Double(0.0));
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMControlArea> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMControlArea controlArea1 = mapper.map(pao1);
                    LMControlArea controlArea2 = mapper.map(pao2);
                    if (controlArea1 == controlArea2) {
                        return 0;
                    }
                    if (controlArea1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (controlArea2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Double state1 = 0.0;  // TO BE DETERMINED
                    Double state2 = 0.0;
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    };

    private static abstract class MappingComparator implements
            Comparator<DisplayablePao> {
        ObjectMapper<DisplayablePao, LMControlArea> mapper;

        MappingComparator(ObjectMapper<DisplayablePao, LMControlArea> mapper) {
            this.mapper = mapper;
        }
    }

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

    public abstract Comparator<DisplayablePao> getSorter(
            ObjectMapper<DisplayablePao, LMControlArea> mapper,
            YukonUserContext userContext, boolean isDescending);
}
