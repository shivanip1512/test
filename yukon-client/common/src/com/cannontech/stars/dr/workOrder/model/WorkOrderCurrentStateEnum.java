package com.cannontech.stars.dr.workOrder.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum WorkOrderCurrentStateEnum implements DisplayableEnum{
    PENDING(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING),
    SCHEDULED(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_SCHEDULED),
    COMPLETED(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_COMPLETED),
    CANCELLED(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_CANCELLED),
    ASSIGNED(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_ASSIGNED),
    RELEASED(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_RELEASED),
    PROCESSED(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PROCESSED),
    HOLD(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_HOLD),
    ;

    private final String keyPrefix = "yukon.dr.workOrder.currentState.displayname.";
    private int definitionId;

    private final static ImmutableMap<Integer, WorkOrderCurrentStateEnum> lookupByDefinitionId;
    static {
        Builder<Integer, WorkOrderCurrentStateEnum> byDefinitionIdBuilder =
            ImmutableMap.builder();

        for (WorkOrderCurrentStateEnum workOrderCurrentStateEnum : values()) {
            byDefinitionIdBuilder.put(workOrderCurrentStateEnum.getDefinitionId(), workOrderCurrentStateEnum);
        }
        lookupByDefinitionId = byDefinitionIdBuilder.build();
    }
    
    private WorkOrderCurrentStateEnum(int definitionId) {
        this.definitionId = definitionId;
    }

    public int getDefinitionId() {
        return definitionId;
    }
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
    public static WorkOrderCurrentStateEnum getByDefinitionId(Integer definitionId) {
        return lookupByDefinitionId.get(definitionId);
    }
    
}