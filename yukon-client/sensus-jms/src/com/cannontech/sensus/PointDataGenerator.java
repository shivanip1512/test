package com.cannontech.sensus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IServerConnection;

public class PointDataGenerator implements PointValueUpdater {
    private Logger log = YukonLogManager.getLogger(PointDataGenerator.class);
    private IServerConnection dispatchConnection;
    private PointDao pointDao;
    private DynamicDataSource dynamicDataSource;
    private int type;
    private int offset;
    private boolean cachePoints = true;
    private boolean invertStatus = false;
    private Map<Integer, PointHolder> paoIdToPoint = new HashMap<Integer, PointHolder>(); // use only in synchronized in method
    
    private class PointHolder {
        LitePoint point = null;
        double multiplier = 1;
    }
    
    public void writePointDataMessage(LiteYukonPAObject lpao, double rawValue, Date time) {
        PointHolder holder = getPointForPaoId(lpao);
        if (holder == null) {
            return;
        }
        if(holder.point.getPointID() != 0) {
            PointData pointData = new PointData();
            pointData.setId(holder.point.getPointID());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setType(type);
            double value = rawValue;
            value *= holder.multiplier;
            pointData.setValue(value);

            pointData.setTime(time);
            log.info("Updating " + lpao.getPaoName() + " point " + holder.point.getPointID() + ": " + holder.point.getPointName() + " with value=" + value);
            // dispatchConnection.write(pointData);
            dynamicDataSource.putValue(pointData);
            
            // PointValueHolder pvh = dynamicDataSource.getPointValue(lpao.getLiteID());
        }
    }

    public void writePointDataMessage(LiteYukonPAObject lpao, boolean value, Date time) {
        writePointDataMessage(lpao, (value != invertStatus) ? 1 : 0, time);
    }
    
    private synchronized PointHolder getPointForPaoId(LiteYukonPAObject lpao) {
    	if (lpao == null) {
    		log.info("No device found for pao ");
    		return null;
    	}
        PointHolder holder;
        if (cachePoints) {
            holder = paoIdToPoint.get(lpao.getLiteID());
            if (holder != null) {
                return holder;
            }
        }
        holder = new PointHolder();
        // find status point
        
        int pointId = pointDao.getPointIDByDeviceID_Offset_PointType(lpao.getLiteID(), offset, type);
        LitePoint point = pointDao.getLitePoint(pointId);
        if (point == null) {
            log.warn("Unable to find point. DeviceId=" + lpao.getLiteID() 
                     + ", offset=" + offset + ", type=" + type);
            return null;
        }
        log.debug("Got point " + point.getPointID() + " from dao for device=" + lpao + ", offset=" + offset + ", type=" + type);
        
        holder.point = point;
        try {
            holder.multiplier = pointDao.getPointMultiplier(point);
        } catch (DataAccessException e) {
        }

        if (cachePoints) {
            paoIdToPoint.put(lpao.getLiteID(), holder);
        }
        return holder;
    }

    public void setDispatchConnection(IServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
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

    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public DynamicDataSource getDynamicDataSource() {
        return dynamicDataSource;
    }



}
