package com.cannontech.loadcontrol;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;

public class LoadManagementUtils {
    
    public static String getTriggerStateValue(LMControlAreaTrigger trigger) {
        String result = null;
        LitePoint point = DaoFactory.getPointDao().getLitePoint( trigger.getPointId().intValue() );
        if (trigger.getTriggerType()
                .equalsIgnoreCase(ControlAreaTrigger.TriggerType.STATUS.getDbString())) {
            LiteState state = DaoFactory.getStateDao().getLiteState( point.getStateGroupID(), trigger.getPointValue().intValue() );
            result = (state == null ? "(Unknown State)" : state.getStateText());
        }
        return result;
    }
    
    public static String getTriggerStateThreshold(LMControlAreaTrigger trigger) {
        String result = null;
        LitePoint point = DaoFactory.getPointDao().getLitePoint( trigger.getPointId().intValue() );
        if (trigger.getTriggerType()
                .equalsIgnoreCase(ControlAreaTrigger.TriggerType.STATUS.getDbString())) {
            LiteState state = DaoFactory.getStateDao().getLiteState( point.getStateGroupID(), trigger.getThreshold().intValue() );
            result = (state == null ? "(Unknown State)" : state.getStateText());
        }   
        return result;
    }    

}
