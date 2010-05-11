package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;

public class TriggerPeakProjectionField extends TriggerBackingFieldBase {

    @Override
    public String getFieldName() {
        return "PEAK_PROJECTION";
    }
    
    @Override
    public Object getTriggerValue(LMControlAreaTrigger trigger, YukonUserContext userContext) {
        
        TriggerType triggerType = trigger.getTriggerType();

        if (triggerType == TriggerType.THRESHOLD) {
            ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));

            Double peakPointValue = trigger.getPeakPointValue();
            Double projectedPointValue = trigger.getProjectedPointValue();
            template.addData("peak", peakPointValue);
            template.addData("projection", projectedPointValue);
            return template;
        }
        return blankFieldResolvable;
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new TriggerComparator() {
            @Override
            public int triggerCompare(
                    TriggerType triggerType,
                    LMControlAreaTrigger trigger1, LMControlAreaTrigger trigger2) {

                if (triggerType == TriggerType.THRESHOLD) {
                    return trigger1.getPeakPointValue().compareTo(trigger2.getPeakPointValue());
                }
                return 0;
            }};
    }
}
