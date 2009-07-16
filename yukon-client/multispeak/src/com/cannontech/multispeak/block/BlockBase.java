package com.cannontech.multispeak.block;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.spring.YukonSpringHook;

public abstract class BlockBase implements Block{

    public boolean hasData = false;
    
    public BlockBase() {
        super();
    }
    
	/**
	 * Helper method to populate the block when the type of pointValue is unknown.
	 * Looks for a matching pointValue pointId for the attribute provided before loading.
	 * @param meter
	 * @param pointValue
	 * @param attribute
	 */
	protected void populateByPointValue(Meter meter, PointValueHolder pointValue, BuiltInAttribute attribute) {
		
		AttributeService attributeService = (AttributeService)YukonSpringHook.getBean("attributeService");
		try {
		    LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
		    
		    if( pointValue.getId() == litePoint.getPointID()){
		    	populate(meter, pointValue, attribute);
		    	return;
		    }
		} catch (IllegalArgumentException e) {
		    CTILogger.debug(e);
		} catch (NotFoundException e){
		    CTILogger.error(e);
		}
	}

    protected boolean hasValidPointValue(PointValueHolder pointValue) {
		if (pointValue == null) {
			return false;
		}

		if (pointValue instanceof PointValueQualityHolder &&
				((PointValueQualityHolder)pointValue).getPointQuality().getQuality() == PointQuality.Uninitialized.getQuality()) {
			return false;
		}
		
		return true;
    }
    
    @Override
    public boolean hasData() {
        return hasData;
    }
}
