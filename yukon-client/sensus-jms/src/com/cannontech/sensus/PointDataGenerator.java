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
    private boolean invertStatus = false;
    private Map<Integer, LitePoint> repIdToPoint = new HashMap<Integer, LitePoint>();
    private Map<Integer, Double> repIdToMult = new HashMap<Integer, Double>();
    
    public void writePointDataMessage(int repId, double rawValue, Date time) {
        LitePoint point = getFaultStatusPoint(repId);
        if (point == null) {
            return;
        }
        PointData pointData = new PointData();
        pointData.setId(point.getPointID());
        pointData.setQuality(PointQualities.NORMAL_QUALITY);
        pointData.setType(type);
        double value = rawValue;
        Double multiplier = repIdToMult.get(repId);
        if (multiplier != null) {
            value = value * multiplier;
        }
        pointData.setValue(value);

        pointData.setTime(time);
        log.info("Updating " + point + " with value=" + value);
        dispatchConnection.write(pointData);
    }

    public void writePointDataMessage(int repId, boolean value, Date time) {
        writePointDataMessage(repId, (value != invertStatus) ? 1 : 0, time);
    }
    
    public void setYukonDeviceLookup(YukonDeviceLookup yukonDeviceLookup) {
        this.yukonDeviceLookup = yukonDeviceLookup;
    }
    
    private LitePoint getFaultStatusPoint(int repId) {
        LitePoint point = repIdToPoint.get(repId);
        if (point != null) {
            return point;
        }
        // find status point
        LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
        if (device == null) {
            log.info("No device found for repId: " + repId);
            return null;
        }
        int pointId = pointDao.getPointIDByDeviceID_Offset_PointType(device.getLiteID(), 
                                                                     offset, 
                                                                     type);
        point = pointDao.getLitePoint(pointId);
        if (point == null) {
            log.warn("Unable to find point. DeviceId=" + device.getLiteID() 
                     + ", offset=" + offset + ", type=" + type);
        } else {
            log.info("Mapping id to point: " + point);
            repIdToPoint.put(repId, point);
        }
        
        try {
            double multiplier = pointDao.getPointMultiplier(pointId);
            repIdToMult.put(repId, multiplier);
        } catch (DataAccessException e) {
        }
        return point;
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



}
