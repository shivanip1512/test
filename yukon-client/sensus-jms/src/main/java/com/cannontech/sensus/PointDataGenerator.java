package com.cannontech.sensus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;

public class PointDataGenerator implements PointValueUpdater {
    private Logger log = YukonLogManager.getLogger(PointDataGenerator.class);
    private PointDao pointDao;
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private int type;
    private int offset;
    private boolean cachePoints = true;
    private boolean invertStatus = false;
    private Map<Integer, LitePoint> paoIdToPoint = new HashMap<Integer, LitePoint>(); // use only in synchronized in method
    
    @Override
    public void writePointDataMessage(LiteYukonPAObject lpao, double rawValue, Date time) {
        LitePoint point = getPointForPaoId(lpao);
        if (point == null) {
            return;
        }
        if(point.getPointID() != 0) {
            PointData pointData = new PointData();
            pointData.setId(point.getPointID());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setType(type);
            double value = rawValue;
            Double multiplier = point.getMultiplier();
            if (multiplier != null) {
                value *= point.getMultiplier();
            }
            pointData.setValue(value);

            pointData.setTime(time);
            log.info("Updating " + lpao.getPaoName() + " point " + point.getPointID() + ": " + point.getPointName() + " with value=" + value);
            asyncDynamicDataSource.putValue(pointData);
        }
    }

    @Override
    public void writePointDataMessage(LiteYukonPAObject lpao, boolean value, Date time) {
        writePointDataMessage(lpao, (value != invertStatus) ? 1 : 0, time);
    }
    
    private synchronized LitePoint getPointForPaoId(LiteYukonPAObject lpao) {
    	if (lpao == null) {
    		log.info("No device found for pao ");
    		return null;
    	}
        LitePoint point;
        if (cachePoints) {
            point = paoIdToPoint.get(lpao.getLiteID());
            if (point != null) {
                return point;
            }
        }
        // find status point
        
        int pointId = pointDao.getPointIDByDeviceID_Offset_PointType(lpao.getLiteID(), offset, PointType.getForId(type));
        point = pointDao.getLitePoint(pointId);
        if (point == null) {
            log.warn("Unable to find point. DeviceId=" + lpao.getLiteID() 
                     + ", offset=" + offset + ", type=" + type);
            return null;
        }
        log.debug("Got point " + point.getPointID() + " from dao for device=" + lpao + ", offset=" + offset + ", type=" + type);
        
        if (cachePoints) {
            paoIdToPoint.put(lpao.getLiteID(), point);
        }
        return point;
    }

    public void setInvertStatus(boolean invertStatus) {
        this.invertStatus = invertStatus;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isCachePoints() {
        return cachePoints;
    }

    public void setCachePoints(boolean cachePoints) {
        this.cachePoints = cachePoints;
    }

    public void setDynamicDataSource(AsyncDynamicDataSource dynamicDataSource) {
        this.asyncDynamicDataSource = dynamicDataSource;
    }

    public AsyncDynamicDataSource getDynamicDataSource() {
        return asyncDynamicDataSource;
    }



}
