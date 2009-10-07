package com.cannontech.dr.controlarea.model;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;

public class TriggerATKUField extends TriggerBackingFieldBase {

    @Override
    public String getFieldName() {
        return "ATKU";
    }
    
    @Override
    public Object getTriggerValue(LMControlAreaTrigger trigger, YukonUserContext userContext) {

        String triggerType = trigger.getTriggerType();
        if (triggerType.equalsIgnoreCase(ControlAreaTrigger.TriggerType.THRESHOLD.getDbString())) {

            if(trigger.getThresholdKickPercent() <= 0) {
                return buildResolvable(getFieldName() + ".disabledKU");
            }
            ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
            Integer thresholdKickPercent = trigger.getThresholdKickPercent();
            template.addData("thresholdKickPercent", thresholdKickPercent);
            return template;
        }
        return blankFieldResolvable;
    }
}
