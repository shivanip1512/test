package com.cannontech.multispeak.block.data.outage.v4;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.syntax.v4.SyntaxItem;
import com.cannontech.multispeak.block.v4.Block;

public class OutageBlock implements Block {

    public String meterNumber;
    public Double blinkCount;
    public Date blinkCountDateTime;
    
    public boolean hasData = false;
    
    public OutageBlock() {
        super();
    }
   
    public OutageBlock(String meterNumber, double blinkCount, Date blinkCountDateTime) {
        super();
        this.meterNumber = meterNumber;
        this.blinkCount = blinkCount;
        this.blinkCountDateTime = blinkCountDateTime;
        hasData = true;
    }

    @Override
    public String getField(SyntaxItem syntaxItem) {
        
        if( syntaxItem.equals(SyntaxItem.METER_NUMBER)) {
            return meterNumber;
        } else if (syntaxItem.equals(SyntaxItem.BLINK_COUNT)) {
            if( blinkCount != null) {
                return String.valueOf(blinkCount);
            }
        } else if (syntaxItem.equals(SyntaxItem.BLINK_COUNT_DATETIME)){
            if( blinkCountDateTime != null) {
	    		return Iso8601DateUtil.formatIso8601Date(blinkCountDateTime);
            }
        } else {
            CTILogger.error("SyntaxItem: " + syntaxItem + " - Not Valid for OutageBlock");
        }
        
        return "";
    }

	@Override
	public boolean hasData() {
	    return hasData;
	}
	
    @Override
    public String getObjectId() {
    	return meterNumber;
    }
    
    /**
     * Sets the blinkCount value and timestamp fields
     * Sets hasData flag
     * @param value
     */
    public void setBlinkCount(PointValueHolder value) {
        this.blinkCount = value.getValue();
        this.blinkCountDateTime = value.getPointDataTimeStamp();
        this.hasData = true;
    }

    /**
     * Sets meterNumber field
     * @param meterNumber
     */
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
}
