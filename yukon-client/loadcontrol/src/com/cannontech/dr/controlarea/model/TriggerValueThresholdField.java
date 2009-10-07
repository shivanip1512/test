package com.cannontech.dr.controlarea.model;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.loadcontrol.LoadManagementUtils;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;

public class TriggerValueThresholdField extends TriggerBackingFieldBase {

    @Override
    public String getFieldName() {
        return "VALUE_THRESHOLD";
    }
    
    @Override
    public Object getTriggerValue(LMControlAreaTrigger trigger, YukonUserContext userContext) {
        
        ControlAreaTrigger.TriggerType triggerType =
            ControlAreaTrigger.TriggerType.valueOf(trigger.getTriggerType().toUpperCase());

        ResolvableTemplate template = 
            new ResolvableTemplate(getKey(triggerType + "." + getFieldName()));

        if (triggerType == ControlAreaTrigger.TriggerType.STATUS) {
            String triggerState = LoadManagementUtils.getTriggerStateValue(trigger);
            String triggerThreshold = LoadManagementUtils.getTriggerStateThreshold(trigger);
            template.addData("state", triggerState);
            template.addData("threshold", triggerThreshold);
        } else {
            Double triggerValue = trigger.getPointValue();
            Double triggerThreshold = trigger.getThreshold();
            template.addData("value", triggerValue);
            template.addData("threshold", triggerThreshold);
        }
        
        return template;
    }
}
