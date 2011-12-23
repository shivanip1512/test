package com.cannontech.core.dynamic.impl;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.Signal;

/**
 * Implementation of PointService
 * 
 * @author asolberg
 *
 */
public class PointServiceImpl implements PointService
{
    
    private DynamicDataSource dynamicDataSource;
    private PointDao pointDao;
    private StateDao stateDao;

    /**
     * Used to retrieve the state this point is currently in
     * @return LiteState the current state of this point
     * @author asolberg
     */
    public LiteState getCurrentState(int pointId)
    {
        
        LitePoint lp = pointDao.getLitePoint(pointId);
        Set<Signal>  signalSet = dynamicDataSource.getSignals(pointId);
        if(!signalSet.isEmpty())
        {
            Iterator<Signal> iter = signalSet.iterator();
            int highestPriorityCondition = -1;
            while(iter.hasNext())
            {
                Signal sig = iter.next();
                if(TagUtils.isConditionActive(sig.getTags()))
                {
                    int nextCondition = sig.getCondition();
                    String conditionText = AlarmUtils.getAlarmConditionText(nextCondition,lp); 
                    if((lp.getPointType() == PointTypes.ANALOG_POINT && conditionText.equalsIgnoreCase(IAlarmDefs.OTHER_ALARM_STATES[10]))
                    		|| (lp.getPointType() == PointTypes.STATUS_POINT && conditionText.equalsIgnoreCase(IAlarmDefs.STATUS_ALARM_STATES[4]))){
                    	/*
                    	 *  Skip the stale state since it's not a real state in the stategroup.
                    	 *  This will need to change if we decide that the stale alarm condition 
                    	 *  should actually be the state the point is currently in.
                    	 */
                    	continue;
                    }
                    
                    if(nextCondition > highestPriorityCondition) {
                        highestPriorityCondition = nextCondition;
                    }
                }
            }
            LiteState ls = stateDao.findLiteState(lp.getStateGroupID(), highestPriorityCondition + 1);
            return ls;
        }else 
        {
            LiteState ls = stateDao.findLiteState(lp.getStateGroupID(), 0);
            return ls;
        }
    }

    @Required
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    @Required
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    @Required
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
}