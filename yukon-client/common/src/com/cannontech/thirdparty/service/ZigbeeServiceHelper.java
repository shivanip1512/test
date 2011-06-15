package com.cannontech.thirdparty.service;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.PointState;

public class ZigbeeServiceHelper {
    private AttributeService attributeService;
    private SimplePointAccessDao simplePointAccessDao;
    
    public void sendPointStatusUpdate(YukonDevice zigbeeDevice, BuiltInAttribute attribute, PointState pointState) {
        sendPointStatusUpdate(zigbeeDevice,new Instant(), attribute, pointState);
    }
    
    public void sendPointStatusUpdate(YukonDevice zigbeeDevice, Instant lastTime, BuiltInAttribute attribute, PointState pointState) {
        LitePoint point = attributeService.getPointForAttribute(zigbeeDevice, attribute);
        simplePointAccessDao.setPointValue(point, lastTime.toDate(), pointState);
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setSimplePointAccessDao(SimplePointAccessDao simplePointAccessDao) {
        this.simplePointAccessDao = simplePointAccessDao;
    }
}
