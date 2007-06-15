package com.cannontech.multispeak.block.data.outage;

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

public class OutageBlock implements Block{

    public String meterNumber;
    public double blinkCount;
    public Date blinkCountDateTime;
    
    public OutageBlock() {
        super();
    }
   
    public OutageBlock(String meterNumber, double blinkCount, Date blinkCountDateTime) {
        super();
        this.meterNumber = meterNumber;
        this.blinkCount = blinkCount;
        this.blinkCountDateTime = blinkCountDateTime;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getField(SyntaxItem syntaxItem) {
        
        if( syntaxItem.equals(SyntaxItem.METER_NUMBER))
            return meterNumber;
        
        else if (syntaxItem.equals(SyntaxItem.BLINK_COUNT))
            return String.valueOf(blinkCount);
            
        else if (syntaxItem.equals(SyntaxItem.BLINK_COUNT_DATETIME)){
            SimpleDateFormat sdf = new SimpleDateFormat(defaultDateFormat);
            return sdf.format(blinkCountDateTime);
        }
        else {
            CTILogger.error("SyntaxItem: " + syntaxItem + " - Not Valid for LoadBlock");
        }
        
        return "";
    }

    public void populate(Meter meter, PointValueHolder pointValue) {
        
        //TODO - Probably shouldn't set this everytime...need to find a better way.
        meterNumber = meter.getMeterNumber();
        
        AttributeService attributeService = (AttributeService)YukonSpringHook.getBean("attributeService");
        try {
            LitePoint litePoint = 
                attributeService.getPointForAttribute(meter, BuiltInAttribute.BLINK_COUNT);
            
            if( pointValue.getId() == litePoint.getPointID()){
                blinkCount = pointValue.getValue();
                blinkCountDateTime = pointValue.getPointDataTimeStamp();
            }
        } catch (IllegalArgumentException e) {}
    }
}
