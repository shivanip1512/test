package com.cannontech.dr.service.impl;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.dr.controlarea.model.ControlAreaState;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.loadgroup.model.LoadGroupState;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.model.CombinedState;
import com.cannontech.dr.program.model.ProgramState;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Ordering;

public class DemandResponseServiceImpl implements DemandResponseService {
    private SystemDateFormattingService systemDateFormattingService;
    private ControlAreaService controlAreaService;
    private ProgramService programService;
    private LoadGroupService loadGroupService;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    private PaoDefinitionDao paoDefinitionDao;
    final Map<CombinedSortableField, Comparator<DisplayablePao>> sorters;

    private static class PaoStateInfo implements Comparable<PaoStateInfo>{
        boolean enabled = false;
        CombinedState state = null;
        PaoStateInfo(boolean enabled, CombinedState state) {
            this.enabled = enabled;
            this.state = state;
        }

        @Override
        public int compareTo(PaoStateInfo other) {
            return new CompareToBuilder().append(state, other.state)
                                         .append(other.enabled, enabled)
                                         .toComparison();
        }
    }

    public DemandResponseServiceImpl() {
        Builder<CombinedSortableField, Comparator<DisplayablePao>> builder =
            ImmutableMap.builder();

        builder.put(CombinedSortableField.NAME, new DisplayablePaoComparator());
        builder.put(CombinedSortableField.STATE, new Comparator<DisplayablePao>(){

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                PaoStateInfo pao1State = stateForPao(pao1);
                PaoStateInfo pao2State = stateForPao(pao2);

                if (pao1State == pao2State) {
                    return 0;
                }
                if (pao1State == null) {
                    return 1;
                }
                if (pao2State == null) {
                    return -1;
                }

                return pao1State.compareTo(pao2State);
            }});

        sorters = builder.build();
    }

    @Override
    public int getTimeSlotsForTargetCycle(Date stopTime, Date startTime) {
        Calendar stopCal = systemDateFormattingService.getSystemCalendar();
        stopCal.setTime(stopTime);
        stopCal.set(Calendar.MINUTE, 0);
        stopCal.set(Calendar.SECOND, 0);
        stopCal.set(Calendar.MILLISECOND, 0);
        Calendar startCal = systemDateFormattingService.getSystemCalendar();
        startCal.setTime (startTime);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        int timeSlots = (int) ((stopCal.getTimeInMillis()
                - startCal.getTimeInMillis())/(60*60*1000)) + 1;

        // see if we roll over on the last hour
        Calendar tempCal = systemDateFormattingService.getSystemCalendar();
        tempCal.setTime(stopTime);
        int newHr = tempCal.get(Calendar.HOUR_OF_DAY);
        if (newHr > stopCal.get(Calendar.HOUR_OF_DAY)) {
            timeSlots++;
        }
        return Math.min(timeSlots, 24);
    }

    private PaoStateInfo stateForPao(DisplayablePao pao) {
        if (pao == null) {
            return null;
        }

        if (pao.getPaoIdentifier().getPaoType() == PaoType.LM_SCENARIO) {
            return null;
        }

        if (pao.getPaoIdentifier().getPaoType() == PaoType.LM_CONTROL_AREA) {
            LMControlArea controlArea = controlAreaService.getControlAreaForPao(pao);
            if (controlArea == null) {
                return null;
            }
            ControlAreaState state = ControlAreaState.valueOf(controlArea.getControlAreaState());
            return new PaoStateInfo(!controlArea.getDisableFlag(),
                                    CombinedState.forControlAreaState(state));
        }

        if (paoDefinitionDao.isTagSupported(pao.getPaoIdentifier().getPaoType(), PaoTag.LM_PROGRAM)) {
            LMProgramBase program = programService.getProgramForPao(pao);
            if (program == null) {
                return null;
            }
            ProgramState state = ProgramState.valueOf(program.getProgramStatus());
            return new PaoStateInfo(!program.getDisableFlag(),
                                    CombinedState.forProgramState(state));
        }

        LMDirectGroupBase loadGroup = loadGroupService.getGroupForPao(pao);
        if (loadGroup == null) {
            return null;
        }
        LoadGroupState state = LoadGroupState.valueOf(loadGroup.getGroupControlState());
        return new PaoStateInfo(!loadGroup.getDisableFlag(),
                                CombinedState.forLoadGroupState(state));
    }

    private String getLocalizedTypeName(DisplayablePao pao,
            YukonUserContext userContext) {
        MessageSourceResolvable msr =
            new YukonMessageSourceResolvable("yukon.web.modules.dr.paoType." +
                                             pao.getPaoIdentifier().getPaoType());
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage(msr);
    }

    @Override
    public Comparator<DisplayablePao> getSorter(CombinedSortableField field,
            final YukonUserContext userContext) {
        if (field == CombinedSortableField.TYPE) {
            Ordering<DisplayablePao> typeComparator = new Ordering<DisplayablePao>(){

                @Override
                public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                    return new CompareToBuilder().append(getLocalizedTypeName(pao1,
                                                                              userContext),
                                                         getLocalizedTypeName(pao2,
                                                                              userContext))
                                                 .toComparison();
                }
            };
            return typeComparator.nullsFirst();
        }
        return sorters.get(field);
    }

    @Autowired
    public void setSystemDateFormattingService(
            SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }

    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }

    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setLoadGroupService(LoadGroupService loadGroupService) {
        this.loadGroupService = loadGroupService;
    }

    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}
