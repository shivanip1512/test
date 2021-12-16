package com.cannontech.web.support.systemPerformanceMetrics.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.device.Device;
import com.cannontech.web.support.systemPerformanceMetrics.service.SystemPerformanceMetricsService;

public class SystemPerformanceMetricsServiceImpl implements SystemPerformanceMetricsService {
    private static Logger log = YukonLogManager.getLogger(SystemPerformanceMetricsServiceImpl.class);
    @Autowired PointDao pointDao;
    
    @Override
    public List<LitePoint> getAllSystemPoints() {
        List<LitePoint> systemPoints = new ArrayList<LitePoint>();
        try {
            // For System Points, Device Id is 0 and Point Type is System.
            systemPoints = pointDao.getLitePointIdByDeviceId_PointType(Device.SYSTEM_DEVICE_ID, PointType.System);
            systemPoints.stream().forEach(litePoint -> log.debug("Point Name {}, Point type {}, PAObject ID {} ",
                    litePoint.getPointName(), PointType.getForId(litePoint.getLiteType()), litePoint.getPaobjectID()));
        } catch (NotFoundException e) {
            log.error("No System points found in the database.");
        }
        return systemPoints;
    }

    @Override
    public List<ChartValue<Double>> getPointData(int pointId, Date startDate, Date stopDate) {
        // TODO Auto-generated method stub
        return null;
    }
}
