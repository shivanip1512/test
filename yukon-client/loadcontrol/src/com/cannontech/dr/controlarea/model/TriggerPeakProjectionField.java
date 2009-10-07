package com.cannontech.dr.controlarea.model;

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
        
        String triggerType = trigger.getTriggerType();

        if (triggerType.equalsIgnoreCase(ControlAreaTrigger.TriggerType.THRESHOLD.getDbString())) {
            ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));

            Double peakPointValue = trigger.getPeakPointValue();
            Double projectedPointValue = trigger.getProjectedPointValue();
            template.addData("peak", peakPointValue);
            template.addData("projection", projectedPointValue);
            return template;
        }
        return blankFieldResolvable;
    }
}
