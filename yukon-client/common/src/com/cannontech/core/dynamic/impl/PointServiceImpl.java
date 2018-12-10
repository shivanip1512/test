package com.cannontech.core.dynamic.impl;

import java.util.Date;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.events.loggers.PointEventLogService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class PointServiceImpl implements PointService {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private PointDao pointDao;
    @Autowired private PointEventLogService eventLog;
    @Autowired private PointFormattingService pointFormattingService;
    
    private static final Logger log = YukonLogManager.getLogger(PointServiceImpl.class);
    
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
                    if (lp.getPointTypeEnum() == PointType.Analog
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
    public void addPointData(int pointId, double value, YukonUserContext context) {
        PointValueQualityTagHolder pd = asyncDynamicDataSource.getPointValueAndTags(pointId);
        PointData data = new PointData();
        data.setId(pointId);
        data.setTags(pd.getTags());
        data.setTimeStamp(new java.util.Date());
        data.setTime(new java.util.Date());
        data.setType(pd.getType());
        data.setValue(value);
        data.setPointQuality(PointQuality.Manual);
        data.setStr("Manual change occurred from " + context.getYukonUser().getUsername());
        data.setUserName(context.getYukonUser().getUsername());

        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPaobjectID());
        String formattedValue = pointFormattingService.getValueString(data, Format.VALUE, context);
        
        logPointDataAction("Adding", pao, point, new Instant(data.getMillis()), formattedValue, null);
        
        asyncDynamicDataSource.putValue(data);
        
        eventLog.pointDataAdded(pao.getPaoName(), point.getPointName(), formattedValue, data.getTimeStamp(),
            context.getYukonUser());
    }

    @Transactional
    @Override
    public void updatePointData(int pointId, double oldValue, double newValue, Instant timestamp,
            YukonUserContext context) {
        
        LitePoint point = pointDao.getLitePoint(pointId);
        PointData data = new PointData();
        data.setId(pointId);
        data.setTime(timestamp.toDate());
        data.setPointQuality(PointQuality.Manual);
        data.setValue(newValue);
        data.setType(point.getPointType());
        data.setTagsPointMustArchive(true);
        data.setStr(context.getYukonUser().getUsername() + " updated point data.");
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPaobjectID());
        String formattedNewValue = pointFormattingService.getValueString(data, Format.VALUE, context);
        String formattedOldValue = getFormattedValue(point, oldValue, timestamp, context);
                
        logPointDataAction("Updating", pao, point, timestamp, formattedNewValue, formattedOldValue);
        
        rawPointHistoryDao.deletePointData(pointId, oldValue, timestamp);
        asyncDynamicDataSource.putValue(data);
        
        eventLog.pointDataUpdated(pao.getPaoName(), point.getPointName(), formattedOldValue, formattedNewValue,
            timestamp.toDate(), context.getYukonUser());
    }

    @Transactional
    @Override
    public void deletePointData(int pointId, double value, Instant timestamp, YukonUserContext context) {
        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPaobjectID());
        String formattedValue = getFormattedValue(point, value, timestamp, context);
        logPointDataAction("Deleting", pao, point, timestamp, formattedValue, null);
        rawPointHistoryDao.deletePointData(pointId, value, timestamp);

        eventLog.pointDataDeleted(pao.getPaoName(), point.getPointName(), formattedValue, timestamp.toDate(),
            context.getYukonUser());
    }
    
    private void logPointDataAction(String action, LiteYukonPAObject pao, LitePoint point, Instant timestamp,
            String newValue, String oldValue) {
        String pointInfo = action + " historical point data for [" + pao.getLiteID() + "] " + pao.getPaoName() + " ["
            + point.getLiteID() + "] " + point.getPointName() + " "
            + timestamp.toDateTime().toString("MM/dd/YYYY HH:mm:ss") + " new value=" + newValue;
        if (oldValue != null) {
            pointInfo += " old value=" + oldValue;
        }
        log.debug(pointInfo);
    }
    
    /**
     * Format point data value for event logging.
     */
    private String getFormattedValue(LitePoint point, double value, Instant timestamp, YukonUserContext context) {
        return pointFormattingService.getValueString(new PointValueHolder() {
            @Override
            public int getId() {
                return point.getPointID();
            }

            @Override
            public Date getPointDataTimeStamp() {
                return timestamp.toDate();
            }

            @Override
            public int getType() {
                return point.getPointType();
            }

            @Override
            public double getValue() {
                return value;
            }

        }, Format.VALUE, context);
    }
}