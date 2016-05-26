package com.cannontech.clientutils.tags;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AlarmUtils
{

	/**
	 * 
	 */
	public AlarmUtils()
	{
		super();
	}

	/**
	 * Returns the condition text for a given point and is condition ID
	 */
	public static String getAlarmConditionText( int conditionID_, int ptType_, int ptID_ )
	{
        if (conditionID_ < 0) {
            return "undefined";
        }
        
		switch( ptType_ )
		{
			case PointTypes.STATUS_POINT:
			case PointTypes.CALCULATED_STATUS_POINT:
			{
				if( conditionID_ >= 0 && conditionID_ < IAlarmDefs.STATUS_ALARM_STATES.length )
					return IAlarmDefs.STATUS_ALARM_STATES[conditionID_];
				else
				{
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
        return getAlarmConditionText(contidionId, point.getPointType(), point.getPointID());
    }

}
