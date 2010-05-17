package com.cannontech.dr.service;

import java.util.Comparator;
import java.util.Date;

import com.cannontech.dr.model.ControllablePao;
import com.cannontech.user.YukonUserContext;

public interface DemandResponseService {
    public static enum CombinedSortableField {
        NAME,
        TYPE,
        STATE
    }

    public int getTimeSlotsForTargetCycle(Date stopTime, Date startTime);

    public Comparator<ControllablePao> getSorter(CombinedSortableField field,
            YukonUserContext userContext);
}
