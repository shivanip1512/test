package com.cannontech.dr.controlarea.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;

public class TriggerValueThresholdField extends TriggerBackingFieldBase {
    private PointDao pointDao;
    private StateDao stateDao;

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

    private String getTriggerStateValue(LMControlAreaTrigger trigger) {
        String result = null;
        LitePoint point = pointDao.getLitePoint( trigger.getPointId().intValue() );
        if (trigger.getTriggerType()
                .equalsIgnoreCase(ControlAreaTrigger.TriggerType.STATUS.getDbString())) {
            LiteState state = stateDao.getLiteState( point.getStateGroupID(), trigger.getPointValue().intValue() );
            result = (state == null ? "(Unknown State)" : state.getStateText());
        }
        return result;
    }
    
    private String getTriggerStateThreshold(LMControlAreaTrigger trigger) {
        String result = null;
        LitePoint point = pointDao.getLitePoint( trigger.getPointId().intValue() );
        if (trigger.getTriggerType()
                .equalsIgnoreCase(ControlAreaTrigger.TriggerType.STATUS.getDbString())) {
            LiteState state = stateDao.getLiteState( point.getStateGroupID(), trigger.getThreshold().intValue() );
            result = (state == null ? "(Unknown State)" : state.getStateText());
        }   
        return result;
    }

    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
}
