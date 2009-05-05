package com.cannontech.multispeak.block.data.outage;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.csv.CSVReader;

public class OutageBlock implements Block{

    public String meterNumber;
    public Double blinkCount;
    public Date blinkCountDateTime;
    public boolean hasData = false;
    
    private static int FIELD_COUNT = 3;
    
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
        
        if( syntaxItem.equals(SyntaxItem.METER_NUMBER))
            return meterNumber;
        
        else if (syntaxItem.equals(SyntaxItem.BLINK_COUNT)) {
            if( blinkCount != null) {
                return String.valueOf(blinkCount);
            }
        }
            
        else if (syntaxItem.equals(SyntaxItem.BLINK_COUNT_DATETIME)){
            if( blinkCountDateTime != null) {
	    		return Iso8601DateUtil.formatIso8601Date(blinkCountDateTime);
            }
        }
        else {
            CTILogger.error("SyntaxItem: " + syntaxItem + " - Not Valid for OutageBlock");
        }
        
        return "";
    }

    @Override
    public void populate(Meter meter, PointValueHolder pointValue) {
        
    	meterNumber = meter.getMeterNumber();
        loadPointValue(meter, pointValue);
    }

	private void loadPointValue(Meter meter, PointValueHolder pointValue) {
		
		if(pointValue == null) {
			return;
		}
		
		AttributeService attributeService = (AttributeService)YukonSpringHook.getBean("attributeService");
		try {
		    LitePoint litePoint = 
		        attributeService.getPointForAttribute(meter, BuiltInAttribute.BLINK_COUNT);
		    
		    if( pointValue.getId() == litePoint.getPointID()){
		        blinkCount = pointValue.getValue();
		        blinkCountDateTime = pointValue.getPointDataTimeStamp();
		        hasData = true;
		    }
		} catch (NotFoundException e) {
		    CTILogger.warn("Point not found for Meter Number " + meter.getMeterNumber() + " for attribute " + BuiltInAttribute.BLINK_COUNT.name());
		}
	}

    @Override
    public void populate(String string, char separator){
    	
    	StringReader stringReader = new StringReader(string);
    	CSVReader reader = new CSVReader(stringReader, separator);
    	try {
	    	String [] values = reader.readNext();
	    	if (values.length == FIELD_COUNT){
	    		meterNumber = values[0];
	    		blinkCount = StringUtils.isBlank(values[1]) ? null : Double.valueOf(values[1]);

	    		blinkCountDateTime = StringUtils.isBlank(values[2]) ? null : Iso8601DateUtil.parseIso8601Date(values[2]);
	    	}else {
	    		CTILogger.error("OutageBlock could not be parsed (" + stringReader.toString() + ").  Incorrect number of expected fields.");
	    	}
    	} catch (IOException e) {
    		CTILogger.warn(e);
    	} catch (UnsupportedOperationException e) {
    		CTILogger.warn(e);
    	}
    }
    
    @Override
    public boolean hasData() {
        return hasData;
    }
    
    @Override
    public String getObjectId() {
    	return meterNumber;
    }
}
