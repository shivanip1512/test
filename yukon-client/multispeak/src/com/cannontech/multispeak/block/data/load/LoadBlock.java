package com.cannontech.multispeak.block.data.load;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.spring.YukonSpringHook;

public class LoadBlock implements Block{

    public String meterNumber;
    public double loadProfileDemand;
    public Date loadProfileDemandDateTime;
    
    public LoadBlock() {
        super();
    }
    
    public LoadBlock(String meterNumber, double loadProfileDemand, Date loadProfileDemandDateTime) {
        super();
        this.meterNumber = meterNumber;
        this.loadProfileDemand = loadProfileDemand;
        this.loadProfileDemandDateTime = loadProfileDemandDateTime;
    }
    
    public String getField(SyntaxItem syntaxItem) {
        
        if( syntaxItem.equals(SyntaxItem.METER_NUMBER))
            return meterNumber;
        
        else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND)){
            return String.valueOf(loadProfileDemand);
        }
        
        else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND_DATETIME)){
            SimpleDateFormat sdf = new SimpleDateFormat(defaultDateFormat);
            return sdf.format(loadProfileDemandDateTime);
        }
        
        else {
            CTILogger.error("SyntaxItem: " + syntaxItem + " - Not Valid for LoadBlock");
        }
        
        return "";
    }
    
    /**
     * TODO - Clean this method uP!!!  Get ride of liteYukonPaobject.
     */
    public void populate(Meter meter, PointValueHolder pointValue) {
        
        //TODO - Probably shouldn't set this everytime...need to find a better way.
        meterNumber = meter.getMeterNumber();
        
        AttributeService attributeService = (AttributeService)YukonSpringHook.getBean("attributeService");
        try {
            LitePoint litePoint = 
                attributeService.getPointForAttribute(meter, BuiltInAttribute.LOAD_PROFILE);
            
            if( pointValue.getId() == litePoint.getPointID()){
                loadProfileDemand = pointValue.getValue();
                loadProfileDemandDateTime = pointValue.getPointDataTimeStamp();
            }
        } catch (IllegalArgumentException e) {}
    }
}
