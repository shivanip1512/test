package com.cannontech.clientutils.tags;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;

public class AlarmUtils {

    public AlarmUtils() {
        super();
    }

    /**
     * Returns the condition text for a given point and is condition ID
     */
    public static String getAlarmConditionText(int conditionID_, PointType ptType_, int ptID_) {
        if (conditionID_ < 0) {
            return "undefined";
        }
        
        switch (ptType_) {
        case Status:
        case CalcStatus: {
            if (conditionID_ >= 0 && conditionID_ < IAlarmDefs.STATUS_ALARM_STATES.length)
                return IAlarmDefs.STATUS_ALARM_STATES[conditionID_];
            else {
                //must be a state in the status point, (very fragile!!)
                LiteState ls = YukonSpringHook.getBean(StateGroupDao.class).findLiteState(
                    YukonSpringHook.getBean(PointDao.class).getLitePoint(ptID_).getStateGroupID(),
                    conditionID_ - IAlarmDefs.STATUS_ALARM_STATES.length );

                return ls.getStateText();
            }
        }

        default:
            return IAlarmDefs.OTHER_ALARM_STATES[conditionID_];
        }
    }
    
    public static String getAlarmConditionText( int contidionId, LitePoint point) {
        return getAlarmConditionText(contidionId, point.getPointTypeEnum(), point.getPointID());
    }
}