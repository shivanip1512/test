package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
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

        TriggerType triggerType = trigger.getTriggerType();
        if (triggerType == TriggerType.THRESHOLD) {

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

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new TriggerComparator() {
            @Override
            public int triggerCompare(
                    TriggerType triggerType,
                    LMControlAreaTrigger trigger1, LMControlAreaTrigger trigger2) {

                if (triggerType == TriggerType.THRESHOLD) {
                    return trigger1.getThresholdKickPercent().compareTo(trigger2.getThresholdKickPercent());
                }
                return 0;
            }};
    }
}
