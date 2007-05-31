package com.cannontech.sensus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IServerConnection;

public class PointDataGenerator implements PointValueUpdater {
    private Logger log = YukonLogManager.getLogger(PointDataGenerator.class);
    private YukonDeviceLookup yukonDeviceLookup;
    private IServerConnection dispatchConnection;
    private PointDao pointDao;
    private int type;
    private int offset;
    private boolean cachePoints = true;
    private boolean invertStatus = false;
    private Map<Integer, PointHolder> repIdToPoint = new HashMap<Integer, PointHolder>(); // use only in synchronized in method
    
    private class PointHolder {
        LitePoint point = null;
        double multiplier = 1;
    }
    
    public void writePointDataMessage(int repId, double rawValue, Date time) {
        PointHolder holder = getPointForRepId(repId);
        if (holder == null) {
            return;
        }
        PointData pointData = new PointData();
        pointData.setId(holder.point.getPointID());
        pointData.setQuality(PointQualities.NORMAL_QUALITY);
        pointData.setType(type);
        double value = rawValue;
        value *= holder.multiplier;
        pointData.setValue(value);

        pointData.setTime(time);
        log.info("Updating point " + holder.point.getPointID() + " with value=" + value);
        dispatchConnection.write(pointData);
    }

    public void writePointDataMessage(int repId, boolean value, Date time) {
        writePointDataMessage(repId, (value != invertStatus) ? 1 : 0, time);
    }
    
    public void setYukonDeviceLookup(YukonDeviceLookup yukonDeviceLookup) {
        this.yukonDeviceLookup = yukonDeviceLookup;
    }
    
    private synchronized PointHolder getPointForRepId(int repId) {
        PointHolder holder;
        if (cachePoints) {
            holder = repIdToPoint.get(new Integer(repId));
            if (holder != null) {
                return holder;
            }
        }
        holder = new PointHolder();
        // find status point
        LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
        if (device == null) {
            log.info("No device found for repId: " + repId);
            return null;
        }
        int pointId = pointDao.getPointIDByDeviceID_Offset_PointType(device.getLiteID(), 
                                                                     offset, 
                                                                     type);
        LitePoint point = pointDao.getLitePoint(pointId);
        if (point == null) {
            log.warn("Unable to find point. DeviceId=" + device.getLiteID() 
                     + ", offset=" + offset + ", type=" + type);
            return null;
        }
        log.debug("Got point " + point.getPointID() + " from dao for device=" + device + ", offset=" + offset + ", type=" + type);
        
        holder.point = point;
        try {
            holder.multiplier = pointDao.getPointMultiplier(pointId);
        } catch (DataAccessException e) {
        }

        if (cachePoints) {
            repIdToPoint.put(new Integer(repId), holder);
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



}
