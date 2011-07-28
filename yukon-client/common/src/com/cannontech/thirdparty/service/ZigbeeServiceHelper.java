package com.cannontech.thirdparty.service;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.PointState;

/**
 * Helper service to hold some common functions across ZigBee services
 * 
 */
public class ZigbeeServiceHelper {
    private AttributeService attributeService;
    private SimplePointAccessDao simplePointAccessDao;
    
    public void sendPointStatusUpdate(YukonDevice device, BuiltInAttribute attribute, PointState pointState) {
        sendPointStatusUpdate(device,new Instant(), attribute, pointState);
    }
    
    public void sendPointStatusUpdate(YukonDevice device, Instant lastTime, BuiltInAttribute attribute, PointState pointState) {
        LitePoint point = attributeService.getPointForAttribute(device, attribute);
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
