package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;
import java.util.Date;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.dr.model.DisplayablePaoComparator;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public enum LoadGroupDisplayField {
    NAME {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            return buildResolvable(name(), group.getYukonName());
        }

        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
                final YukonUserContext userContext, boolean isDescending) {
            return new DisplayablePaoComparator(userContext, isDescending);
        }
    },
    STATE {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            LoadGroupState state = LoadGroupState.valueOf(group.getGroupControlState());
            return new YukonMessageSourceResolvable(getBaseKey(name()) + "." + state.name());
        }
        
        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMDirectGroupBase group1 = mapper.map(pao1);
                    LMDirectGroupBase group2 = mapper.map(pao2);
                    if (group1 == group2) {
                        return 0;
                    }
                    if (group1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (group2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer state1 = group1.getGroupControlState();
                    Integer state2 = group2.getGroupControlState();
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
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
        
        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
                YukonUserContext userContext, boolean isDescending) {
            return null;
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
        
        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMDirectGroupBase group1 = mapper.map(pao1);
                    LMDirectGroupBase group2 = mapper.map(pao2);
                    if (group1 == group2) {
                        return 0;
                    }
                    if (group1 == null || group1.getGroupTime() == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (group2 == null || group2.getGroupTime() == null) {
                        return isDescending ? 1 : -1;
                    }
                    int retVal = group1.getGroupTime().compareTo(group2.getGroupTime());
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }        
    },
    CONTROL_STATISTICS {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            Object[] result = {
                    CtiUtilities.decodeSecondsToTime(group.getCurrentHoursDaily()),
                    CtiUtilities.decodeSecondsToTime(group.getCurrentHoursMonthly()),
                    CtiUtilities.decodeSecondsToTime(group.getCurrentHoursSeasonal()),
                    CtiUtilities.decodeSecondsToTime(group.getCurrentHoursAnnually()) };
            return buildResolvable(name(), result);
        }
        
        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMDirectGroupBase group1 = mapper.map(pao1);
                    LMDirectGroupBase group2 = mapper.map(pao2);
                    if (group1 == group2) {
                        return 0;
                    }
                    if (group1 == null || group1.getCurrentHoursDaily() == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (group2 == null || group2.getCurrentHoursDaily() == null) {
                        return isDescending ? 1 : -1;
                    }
                    Integer state1 = group1.getCurrentHoursDaily();
                    Integer state2 = group2.getCurrentHoursDaily();
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }
    },
    REDUCTION {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            return buildResolvable(name(), group.getReduction());
        }
        
        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMDirectGroupBase group1 = mapper.map(pao1);
                    LMDirectGroupBase group2 = mapper.map(pao2);
                    if (group1 == group2) {
                        return 0;
                    }
                    if (group1 == null || group1.getReduction() == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (group2 == null || group2.getReduction() == null) {
                        return isDescending ? 1 : -1;
                    }
                    Double reduction1 = group1.getReduction();
                    Double reduction2 = group2.getReduction();
                    int retVal = reduction1.compareTo(reduction2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }        
    },
    LOAD_CAPACITY {
        @Override
        public MessageSourceResolvable getValue(LMDirectGroupBase group) {
            return buildResolvable(name(), new Double(0.0));
        }
        
        @Override
        public Comparator<DisplayablePao> getSorter(
                ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
                YukonUserContext userContext, final boolean isDescending) {
            return new MappingComparator(mapper) {

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    LMDirectGroupBase group1 = mapper.map(pao1);
                    LMDirectGroupBase group2 = mapper.map(pao2);
                    if (group1 == group2) {
                        return 0;
                    }
                    if (group1 == null) {
                        return isDescending ? -1 : 1;
                    }
                    if (group2 == null) {
                        return isDescending ? 1 : -1;
                    }
                    Double state1 = 0.0;  // TO BE DETERMINED
                    Double state2 = 0.0;
                    int retVal = state1.compareTo(state2);
                    return isDescending ? (0 - retVal) : retVal;
                }};
        }        
    }    
    ;
    
    private static abstract class MappingComparator implements
            Comparator<DisplayablePao> {
        ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper;
        MappingComparator(ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper) {
            this.mapper = mapper;
        }
    }
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
    
    public abstract Comparator<DisplayablePao> getSorter(
            ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
            YukonUserContext userContext, boolean isDescending);
}
