package com.cannontech.web.common.pao.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class YukonPointHelperImpl implements YukonPointHelper {
    
    @Autowired private PointDao pointDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    public List<YukonPoint> getYukonPoints(int deviceId, String orderBy, Boolean descending) {
        orderBy = (orderBy == null) ? PointSortField.POINTNAME.name() : orderBy;
        descending = (descending == null) ? false : descending;
        
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        Function<LitePoint, YukonPoint> pointFunction = new Function<LitePoint, YukonPoint>() {
            @Override
            public YukonPoint apply(LitePoint lp) {
                PointIdentifier pointId = new PointIdentifier(lp.getPointTypeEnum(), lp.getPointOffset());
                PaoType paoType = device.getDeviceType();
                BuiltInAttribute attribute = paoDefinitionDao.findAttributeForPaoTypeAndPoint(new PaoTypePointIdentifier(paoType, pointId));
                return YukonPoint.of(new PaoPointIdentifier(device.getPaoIdentifier(), pointId), attribute, lp.getPointName(), lp.getPointID());
            }
        };
        
        List<LitePoint> points = pointDao.getLitePointsByPaObjectId(deviceId);
        
        List<YukonPoint> yukonPoints = new ArrayList<>(Lists.transform(points, pointFunction));
        Collections.sort(yukonPoints, YukonPoint.getComparatorForSortField(PointSortField.valueOf(orderBy)));
        
        if (descending) {
            yukonPoints = Lists.reverse(yukonPoints);
        }
        
        return yukonPoints;
    }
    
    public List<YukonPoint> getYukonPoints(int deviceId) {
        return getYukonPoints(deviceId, null, null);
    }
}