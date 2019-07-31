package com.cannontech.common.dr.gear.setup.fields;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.cannontech.common.dr.gear.setup.WhenToChange;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WhenToChangeFields {

    private WhenToChange whenToChange;
    private Integer changeDurationInMinutes;
    private Integer changePriority;
    private Integer triggerNumber;
    private Double triggerOffset;

    public WhenToChange getWhenToChange() {
        return whenToChange;
    }

    public void setWhenToChange(WhenToChange whenToChange) {
        this.whenToChange = whenToChange;
    }

    public Integer getChangeDurationInMinutes() {
        return changeDurationInMinutes;
    }

    public void setChangeDurationInMinutes(Integer changeDurationInMinutes) {
        this.changeDurationInMinutes = changeDurationInMinutes;
    }

    public Integer getChangePriority() {
        return changePriority;
    }

    public void setChangePriority(Integer changePriority) {
        this.changePriority = changePriority;
    }

    public Integer getTriggerNumber() {
        return triggerNumber;
    }

    public void setTriggerNumber(Integer triggerNumber) {
        this.triggerNumber = triggerNumber;
    }

    public Double getTriggerOffset() {
        return triggerOffset;
    }

    public void setTriggerOffset(Double triggerOffset) {
        if (triggerOffset != null) {
            this.triggerOffset = new BigDecimal(triggerOffset).setScale(4, RoundingMode.HALF_DOWN).doubleValue();
        } else {
            this.triggerOffset = triggerOffset;
        }
    }

    public void buildModel(LMProgramDirectGear directGear) {
        WhenToChange whenToChange = WhenToChange.valueOf(directGear.getChangeCondition());
        setWhenToChange(whenToChange);

        if (whenToChange == WhenToChange.Priority) {
            setChangePriority(directGear.getChangePriority());
        }
        if (whenToChange == WhenToChange.Duration) {
            setChangeDurationInMinutes(directGear.getChangeDuration() / 60);
        }
        if (whenToChange == WhenToChange.TriggerOffset) {
            setTriggerNumber(directGear.getChangeTriggerNumber());
            setTriggerOffset(directGear.getChangeTriggerOffset());
        }

    }

    public void buildDBPersistent(LMProgramDirectGear directGear) {

        directGear.setChangeCondition(getWhenToChange().name());

        if (getWhenToChange() == WhenToChange.Duration) {
            directGear.setChangeDuration(getChangeDurationInMinutes() * 60);
        }
        if (getWhenToChange() == WhenToChange.Priority) {
            directGear.setChangePriority(getChangePriority());
        }

        if (getWhenToChange() == WhenToChange.TriggerOffset) {
            directGear.setChangeTriggerNumber(getTriggerNumber());

            if (getTriggerOffset() != null) {
                directGear.setChangeTriggerOffset(getTriggerOffset());
            } else {
                directGear.setChangeTriggerOffset(0.0);
            }
        }

    }

}
