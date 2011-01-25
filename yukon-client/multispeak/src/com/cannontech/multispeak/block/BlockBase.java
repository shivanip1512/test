package com.cannontech.multispeak.block;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.spring.YukonSpringHook;

public abstract class BlockBase implements Block{

    public boolean hasData = false;
    
    public BlockBase() {
        super();
    }
    
	/**
	 * Helper method to populate the block when the type of richPointData is unknown.
	 * Looks for a matching richPointData pointId for the attribute provided before loading.
	 * @param meter
	 * @param richPointData
	 * @param attribute
	 */
	protected void populateByPointValue(Meter meter, RichPointData richPointData, BuiltInAttribute attribute) {
		
		AttributeService attributeService = (AttributeService)YukonSpringHook.getBean("attributeService");
		try {
		    boolean isPointForAttribute = attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), attribute);
		    if(isPointForAttribute){
		    	populate(meter, richPointData, attribute);
		    	return;
		    }
		} catch (IllegalUseOfAttribute e) {
		    CTILogger.debug(e);
		}
	}

    protected boolean hasValidPointValue(RichPointData richPointData) {
		if (richPointData == null) {
			return false;
		}

		if (richPointData.getPointValue().getPointQuality().getQuality() == PointQuality.Uninitialized.getQuality()) {
			return false;
		}
		
		return true;
    }
    
    @Override
    public boolean hasData() {
        return hasData;
    }
}
