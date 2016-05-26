package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;

public class TriggerValueThresholdField extends TriggerBackingFieldBase {
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;

    @Override
    public String getFieldName() {
        return "VALUE_THRESHOLD";
    }
    
    @Override
    public Object getTriggerValue(LMControlAreaTrigger trigger, YukonUserContext userContext) {

        TriggerType triggerType = trigger.getTriggerType();

        ResolvableTemplate template = 
            new ResolvableTemplate(getKey(triggerType + "." + getFieldName()));

        if (triggerType == TriggerType.STATUS) {
            String triggerState = getTriggerStateValue(trigger);
            String triggerThreshold = getTriggerStateThreshold(trigger);
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

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new TriggerComparator() {
            @Override
            public int triggerCompare(
                    TriggerType triggerType,
                    LMControlAreaTrigger trigger1, LMControlAreaTrigger trigger2) {

                if (triggerType == TriggerType.STATUS) {
                    return getTriggerStateValue(trigger1).compareTo(getTriggerStateValue(trigger2));
                }
                return trigger1.getPointValue().compareTo(trigger2.getPointValue());
            }};
    }

    private String getTriggerStateValue(LMControlAreaTrigger trigger) {
        String result = null;
        LitePoint point = pointDao.getLitePoint( trigger.getPointId().intValue() );
        if (trigger.getTriggerType() == TriggerType.STATUS) {
            LiteState state = stateGroupDao.findLiteState( point.getStateGroupID(), trigger.getPointValue().intValue() );
            result = (state == null ? "(Unknown State)" : state.getStateText());
        }
        return result;
    }
    
    private String getTriggerStateThreshold(LMControlAreaTrigger trigger) {
        String result = null;
        LitePoint point = pointDao.getLitePoint( trigger.getPointId().intValue() );
        if (trigger.getTriggerType() == TriggerType.STATUS) {
            LiteState state = stateGroupDao.findLiteState( point.getStateGroupID(), trigger.getThreshold().intValue() );
            result = (state == null ? "(Unknown State)" : state.getStateText());
        }   
        return result;
    }
}
