package com.cannontech.amr.deviceDataMonitor.model;

import java.io.Serializable;
import java.util.Comparator;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class DeviceDataMonitorProcessor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer processorId;
    private Integer monitorId;
    private Attribute attribute;
    private LiteStateGroup stateGroup;
    private LiteState state;
    
    /* not stored in the db */
    private boolean deletion = false;
    
    public DeviceDataMonitorProcessor() {}
    
    public DeviceDataMonitorProcessor(Integer processorId, Integer monitorId, Attribute attribute,
                                      LiteStateGroup stateGroup, LiteState state) {
        this.processorId = processorId;
        this.monitorId = monitorId;
        this.attribute = attribute;
        this.stateGroup = stateGroup;
        this.state = state;
    }

    public Integer getProcessorId() {
        return processorId;
    }

    public void setProcessorId(Integer processorId) {
        this.processorId = processorId;
    }

    public Integer getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Integer monitorId) {
        this.monitorId = monitorId;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public LiteStateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(LiteStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public LiteState getState() {
        return state;
    }

    public void setState(LiteState state) {
        this.state = state;
    }

    public boolean isDeletion() {
        return deletion;
    }

    public void setDeletion(boolean deletion) {
        this.deletion = deletion;
    }
    
    /**
     * Comparator that orders by attribute, then state group, then state. Sorting
     * is for equality comparison reasons and not for display reasons
     */
    public final static Comparator<DeviceDataMonitorProcessor> COMPARATOR;
    static {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DeviceDataMonitorProcessor> attributeOrdering = normalStringComparer
            .onResultOf(new Function<DeviceDataMonitorProcessor, String>() {
                @Override
                public String apply(DeviceDataMonitorProcessor from) {
                    return from.getAttribute().getKey();
                }
            });
        Ordering<Integer> intComparer = Ordering.natural();
        Ordering<DeviceDataMonitorProcessor> stateGroupOrdering = intComparer
                .onResultOf(new Function<DeviceDataMonitorProcessor, Integer>() {
                    @Override
                    public Integer apply(DeviceDataMonitorProcessor from) {
                        return from.getStateGroup().getStateGroupID();
                    }
                });
        Ordering<DeviceDataMonitorProcessor> stateOrdering = intComparer
                .onResultOf(new Function<DeviceDataMonitorProcessor, Integer>() {
                    @Override
                    public Integer apply(DeviceDataMonitorProcessor from) {
                        return from.getState().getStateRawState();
                    }
                });
        COMPARATOR = attributeOrdering.compound(stateGroupOrdering).compound(stateOrdering);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + (deletion ? 1231 : 1237);
        result = prime * result + ((monitorId == null) ? 0 : monitorId.hashCode());
        result = prime * result + ((processorId == null) ? 0 : processorId.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((stateGroup == null) ? 0 : stateGroup.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceDataMonitorProcessor other = (DeviceDataMonitorProcessor) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        if (deletion != other.deletion)
            return false;
        if (monitorId == null) {
            if (other.monitorId != null)
                return false;
        } else if (!monitorId.equals(other.monitorId))
            return false;
        if (processorId == null) {
            if (other.processorId != null)
                return false;
        } else if (!processorId.equals(other.processorId))
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (stateGroup == null) {
            if (other.stateGroup != null)
                return false;
        } else if (!stateGroup.equals(other.stateGroup))
            return false;
        return true;
    }
}
