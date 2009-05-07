package com.cannontech.multispeak.block.data.load;

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

public class LoadBlock implements Block{

    public String meterNumber;
    public Double loadProfileDemand;
    public Date loadProfileDemandDateTime;
    public Double kVAr;
    public Date kVArDateTime;
    public Double voltage;
    public Date voltageDateTime;
    public boolean hasData = false;
    
    private static int FIELD_COUNT = 7;
    
    public LoadBlock() {
        super();
    }
    
    public LoadBlock(String meterNumber, double loadProfileDemand, Date loadProfileDemandDateTime,
            double kVAr, Date kVArDateTime, double voltage, Date voltageDateTime) {
        super();
        this.meterNumber = meterNumber;
        this.loadProfileDemand = loadProfileDemand;
        this.loadProfileDemandDateTime = loadProfileDemandDateTime;
        this.kVAr = kVAr;
        this.kVArDateTime = kVArDateTime;
        this.voltage = voltage;
        this.voltageDateTime = voltageDateTime;
        hasData = true;
    }

    @Override
    public String getField(SyntaxItem syntaxItem) {
    	
        if( syntaxItem.equals(SyntaxItem.METER_NUMBER))
            return meterNumber;
        
        else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND)){
            if( loadProfileDemand != null)
                return String.valueOf(loadProfileDemand);
        }
        
        else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND_DATETIME)){
            if ( loadProfileDemandDateTime != null){
                return Iso8601DateUtil.formatIso8601Date(loadProfileDemandDateTime);
            }
        }
        
        else if (syntaxItem.equals(SyntaxItem.KVAR)){
            if( kVAr != null)
                return String.valueOf(kVAr);
        }
        
        else if (syntaxItem.equals(SyntaxItem.KVAR_DATETIME)){
            if (kVArDateTime != null){
            	return Iso8601DateUtil.formatIso8601Date(kVArDateTime);
            }
        }

        else if (syntaxItem.equals(SyntaxItem.VOLTAGE)){
            if (voltage != null)
            return String.valueOf(voltage);
        }
        
        else if (syntaxItem.equals(SyntaxItem.VOLTAGE_DATETIME)){
            if( voltageDateTime != null) {
            	return Iso8601DateUtil.formatIso8601Date(voltageDateTime);
            }
        }
        
        else {
            CTILogger.error("SyntaxItem: " + syntaxItem + " - Not Valid for LoadBlock");
        }
        
        return "";
    }
    
    //TODO - need to clean this up so that only the attribute that the pointValue is coming for is checked.
    @Override
    public void populate(Meter meter, PointValueHolder pointValue) {
    	meterNumber = meter.getMeterNumber();
        loadPointValue(meter, pointValue);
    }

    /**
     * Load the pointValue data into LoadBlock
     * @param meter
     * @param pointValue
     */
	private void loadPointValue(Meter meter, PointValueHolder pointValue) {
		
		if (pointValue == null) {
			return;
		}
		
		AttributeService attributeService = (AttributeService)YukonSpringHook.getBean("attributeService");
		try {
		    LitePoint litePoint = 
		        attributeService.getPointForAttribute(meter, BuiltInAttribute.LOAD_PROFILE);
		    
		    if( pointValue.getId() == litePoint.getPointID()){
		        loadProfileDemand = pointValue.getValue();
		        loadProfileDemandDateTime = pointValue.getPointDataTimeStamp();
		        hasData = true;
		    }
		} catch (IllegalArgumentException e) {
		    CTILogger.debug(e);
		} catch (NotFoundException e){
		    CTILogger.error(e);
		}
		
		if (!hasData) { 
		    try {
		        LitePoint litePoint = 
		            attributeService.getPointForAttribute(meter, BuiltInAttribute.KVAR);
		        
		        if( pointValue.getId() == litePoint.getPointID()){
		            kVAr = pointValue.getValue();
		            kVArDateTime = pointValue.getPointDataTimeStamp();
		            hasData = true;
		        }
		    } catch (IllegalArgumentException e) {
		        CTILogger.debug(e);
		    } catch (NotFoundException e){
		        CTILogger.error(e);
		    }
		}
		if (!hasData) {
		    try {
		        LitePoint litePoint = 
		            attributeService.getPointForAttribute(meter, BuiltInAttribute.VOLTAGE);
		        
		        if( pointValue.getId() == litePoint.getPointID()){
		            voltage = pointValue.getValue();
		            voltageDateTime = pointValue.getPointDataTimeStamp();
		            hasData = true;
		        }
		    } catch (IllegalArgumentException e) {
		        CTILogger.debug(e);
		    } catch (NotFoundException e){
		        CTILogger.error(e);
		    }
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
	    		loadProfileDemand = StringUtils.isBlank(values[1]) ? null : Double.valueOf(values[1]);
	    		loadProfileDemandDateTime = StringUtils.isBlank(values[2]) ? null : Iso8601DateUtil.parseIso8601Date(values[2]);
	    		kVAr = StringUtils.isBlank(values[3]) ? null : Double.valueOf(values[3]);
	    		kVArDateTime = StringUtils.isBlank(values[4]) ? null : Iso8601DateUtil.parseIso8601Date(values[4]);
	    		voltage = StringUtils.isBlank(values[5]) ? null : Double.valueOf(values[5]);
	    		voltageDateTime = StringUtils.isBlank(values[6]) ? null : Iso8601DateUtil.parseIso8601Date(values[6]);
	    	}else {
	    		CTILogger.error("LoadBlock could not be parsed (" + stringReader.toString() + ").  Incorrect number of expected fields.");
	    	}
    	} catch (IOException e) {
    		CTILogger.warn(e);
    	} catch (IllegalArgumentException e) {
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
