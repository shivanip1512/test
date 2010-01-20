package com.cannontech.multispeak.block.data.outage;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.BlockBase;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.tools.csv.CSVReader;

public class OutageBlock extends BlockBase{

    public String meterNumber;
    public Double blinkCount;
    public Date blinkCountDateTime;
    
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
    public void populate(Meter meter, PointValueHolder pointValue) {
        
    	meterNumber = meter.getMeterNumber();
        loadPointValue(meter, pointValue);
    }

    /**
     * Load the pointVlaue data into OutageBlock
     * @param meter
     * @param pointValue
     */
	private void loadPointValue(Meter meter, PointValueHolder pointValue) {

		if (!hasValidPointValue(pointValue)) {
			//get out before doing any more work.
			return;
		}

		populateByPointValue(meter, pointValue, BuiltInAttribute.BLINK_COUNT);

	}

	/**
     * Helper method to load the fields based on the attribute.
     * This method assumes the pointValue matches the attribute provided.
	 * @param meter
	 * @param pointValue
	 * @param attribute
	 */
	public void populate(Meter meter, PointValueHolder pointValue, BuiltInAttribute attribute) {
		
		if (!hasValidPointValue(pointValue)) {
			return;
		}
		if (attribute.equals(BuiltInAttribute.BLINK_COUNT)) {
			setBlinkCount(meter, pointValue);
		} else {
			throw new IllegalArgumentException("Attribute " + attribute.toString() + " is not supported by OutageBlock.");
		}
		hasData = true;
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
    	} catch (IllegalArgumentException e) {
    		CTILogger.warn(e);
    	}
    }

	/**
     * Helper method to set the blinkCount fields
     * @param meter
     * @param pointValue
     */
	private void setBlinkCount(Meter meter, PointValueHolder pointValue) {
        blinkCount = pointValue.getValue();
        blinkCountDateTime = pointValue.getPointDataTimeStamp();
	}

    @Override
    public String getObjectId() {
    	return meterNumber;
    }
}
