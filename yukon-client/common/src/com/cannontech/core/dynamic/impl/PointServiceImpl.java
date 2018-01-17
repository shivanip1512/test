package com.cannontech.core.dynamic.impl;

import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.events.loggers.PointEventLogService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.yukon.IDatabaseCache;

public class PointServiceImpl implements PointService {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private PointDao pointDao;
    @Autowired private PointEventLogService eventLog;
    
    @Override
    public LiteState getCurrentStateForNonStatusPoint(LitePoint lp) {
        
        Set<Signal>  signals = asyncDynamicDataSource.getSignals(lp.getPointID());
        return getCurrentStateForNonStatusPoint(lp, signals);
    }
    
    @Override
    public LiteState getCurrentStateForNonStatusPoint(LitePoint lp, Set<Signal> signals) {
        
        if (!signals.isEmpty()) {
            
            int highestPriorityCondition = -1;
            
            for (Signal signal : signals) {
                
                if (TagUtils.isConditionActive(signal.getTags())) {
                    
                    int nextCondition = signal.getCondition();
                    String conditionText = AlarmUtils.getAlarmConditionText(nextCondition, lp); 
                    if (lp.getPointType() == PointTypes.ANALOG_POINT 
                                && conditionText.equalsIgnoreCase(IAlarmDefs.OTHER_ALARM_STATES[10])) {
                        // Skip the stale state since it's not a real state in the stategroup.
                        // This will need to change if we decide that the stale alarm condition 
                        // should actually be the state the point is currently in.
                        continue;
                    }
                    
                    if (nextCondition > highestPriorityCondition) {
                        highestPriorityCondition = nextCondition;
                    }
                }
                
            }
            
            LiteState ls = stateGroupDao.findLiteState(lp.getStateGroupID(), highestPriorityCondition + 1);
            return ls;
            
        } else {
            LiteState ls = stateGroupDao.findLiteState(lp.getStateGroupID(), 0);
            return ls;
        }
    }

    @Transactional
    @Override
    public void sendPointData(int pointId, double value, LiteYukonUser user) {
        PointValueQualityTagHolder pd = asyncDynamicDataSource.getPointValueAndTags(pointId);
        PointData data = new PointData();
        data.setId(pointId);
        data.setTags(pd.getTags());
        data.setTimeStamp(new java.util.Date());
        data.setTime(new java.util.Date());
        data.setType(pd.getType());
        data.setValue(value);
        data.setPointQuality(PointQuality.Manual);
        data.setStr("Manual change occurred from " + CtiUtilities.getUserName());
        data.setUserName(user.getUsername());
        asyncDynamicDataSource.putValue(data);

        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPaobjectID());
       // eventLog.pointDataAdded(pao.getPaoName(), point.getPointName(), value, data.getTimeStamp(), user);
    }

    @Transactional
    @Override
    public void updatePointData(int pointId, double oldValue, double newValue, Instant timestamp, LiteYukonUser user) {
        rawPointHistoryDao.deletePointData(pointId, oldValue, timestamp);

        PointValueQualityHolder pd = asyncDynamicDataSource.getPointValue(pointId);
        PointData data = new PointData();
        data.setId(pointId);
        data.setTime(timestamp.toDate());
        data.setPointQuality(PointQuality.Manual);
        data.setValue(newValue);
        data.setType(pd.getPointType().getPointTypeId());
        data.setTagsPointMustArchive(true);
        data.setStr(CtiUtilities.getUserName() + " updated point data.");
        asyncDynamicDataSource.putValue(data);

        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPaobjectID());
      //  eventLog.pointDataUpdated(pao.getPaoName(), point.getPointName(), oldValue, newValue, timestamp.toDate(), user);
    }

    @Transactional
    @Override
    public void deletePointData(int pointId, double value, Instant timestamp, LiteYukonUser user) {
        rawPointHistoryDao.deletePointData(pointId, value, timestamp);

        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPaobjectID());
       // eventLog.pointDataDeleted(pao.getPaoName(), point.getPointName(), value, timestamp.toDate(), user);
    }
}