package com.cannontech.multispeak.block.data.load;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.BlockBase;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.tools.csv.CSVReader;

public class LoadBlock extends BlockBase{

    public String meterNumber;
    public Double loadProfileDemand;
    public Date loadProfileDemandDateTime;
    public Double kVAr;
    public Date kVArDateTime;
    public Double voltage;
    public Date voltageDateTime;
    public Double voltageProfile;
    public Date voltageProfileDateTime;
    
    private static int FIELD_COUNT = 9;
    
    public LoadBlock() {
        super();
    }
    
    public LoadBlock(String meterNumber, double loadProfileDemand, Date loadProfileDemandDateTime,
            double kVAr, Date kVArDateTime, double voltage, Date voltageDateTime, double voltageProfile,
            Date voltageProfileDateTime) {
        super();
        this.meterNumber = meterNumber;
        this.loadProfileDemand = loadProfileDemand;
        this.loadProfileDemandDateTime = loadProfileDemandDateTime;
        this.kVAr = kVAr;
        this.kVArDateTime = kVArDateTime;
        this.voltage = voltage;
        this.voltageDateTime = voltageDateTime;
        this.voltageProfile = voltageProfile;
        this.voltageProfileDateTime = voltageProfileDateTime;
        hasData = true;
    }

    @Override
    public String getField(SyntaxItem syntaxItem) {
    	
        if( syntaxItem.equals(SyntaxItem.METER_NUMBER)) {
            return meterNumber;
        } else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND)){
            if( loadProfileDemand != null)
                return String.valueOf(loadProfileDemand);
        } else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND_DATETIME)){
            if ( loadProfileDemandDateTime != null){
                return Iso8601DateUtil.formatIso8601Date(loadProfileDemandDateTime);
            }
        } else if (syntaxItem.equals(SyntaxItem.KVAR)){
            if( kVAr != null)
                return String.valueOf(kVAr);
        } else if (syntaxItem.equals(SyntaxItem.KVAR_DATETIME)){
            if (kVArDateTime != null){
            	return Iso8601DateUtil.formatIso8601Date(kVArDateTime);
            }
        } else if (syntaxItem.equals(SyntaxItem.VOLTAGE)){
            if (voltage != null)
            return String.valueOf(voltage);
        } else if (syntaxItem.equals(SyntaxItem.VOLTAGE_DATETIME)){
            if( voltageDateTime != null) {
            	return Iso8601DateUtil.formatIso8601Date(voltageDateTime);
            }
        } else if (syntaxItem.equals(SyntaxItem.VOLTAGE_PROFILE)){
            if( voltageProfile != null)
                return String.valueOf(voltageProfile);
        } else if (syntaxItem.equals(SyntaxItem.VOLTAGE_PROFILE_DATETIME)){
            if ( voltageProfileDateTime != null){
                return Iso8601DateUtil.formatIso8601Date(voltageProfileDateTime);
            }
        } else {
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
		
		if(!hasValidPointValue(pointValue)) {
			//get out before doing any more work.
			return;
		}

		populateByPointValue(meter, pointValue, BuiltInAttribute.LOAD_PROFILE);
		populateByPointValue(meter, pointValue, BuiltInAttribute.KVAR);
		populateByPointValue(meter, pointValue, BuiltInAttribute.VOLTAGE);
		populateByPointValue(meter, pointValue, BuiltInAttribute.VOLTAGE_PROFILE);
	}

	/**
     * Helper method to load the fields based on the attribute.
     * This method assumes the pointValue matches the attribute provided.
	 * @param meter
	 * @param pointValue
	 * @param attribute
	 */
	@Override
	public void populate(Meter meter, PointValueHolder pointValue, BuiltInAttribute attribute) {
		
		if (!hasValidPointValue(pointValue)) {
			return;
		}
		
		if (attribute.equals(BuiltInAttribute.LOAD_PROFILE)) {
			setLoadProfileDemand(meter, pointValue);
		} else if (attribute.equals(BuiltInAttribute.KVAR)) {
			setKVar(meter, pointValue);
		} else if (attribute.equals(BuiltInAttribute.VOLTAGE)) {
			setVoltage(meter, pointValue);
		} else if (attribute.equals(BuiltInAttribute.VOLTAGE_PROFILE)) {
            setVoltageProfile(meter, pointValue);
        } else {
			throw new IllegalArgumentException("Attribute " + attribute.toString() + " is not supported by LoadBlock.");
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
	    		loadProfileDemand = StringUtils.isBlank(values[1]) ? null : Double.valueOf(values[1]);
	    		loadProfileDemandDateTime = StringUtils.isBlank(values[2]) ? null : Iso8601DateUtil.parseIso8601Date(values[2]);
	    		kVAr = StringUtils.isBlank(values[3]) ? null : Double.valueOf(values[3]);
	    		kVArDateTime = StringUtils.isBlank(values[4]) ? null : Iso8601DateUtil.parseIso8601Date(values[4]);
	    		voltage = StringUtils.isBlank(values[5]) ? null : Double.valueOf(values[5]);
	    		voltageDateTime = StringUtils.isBlank(values[6]) ? null : Iso8601DateUtil.parseIso8601Date(values[6]);
                voltageProfile = StringUtils.isBlank(values[7]) ? null : Double.valueOf(values[1]);
                voltageProfileDateTime = StringUtils.isBlank(values[8]) ? null : Iso8601DateUtil.parseIso8601Date(values[2]);
	    	}else {
	    		CTILogger.error("LoadBlock could not be parsed (" + stringReader.toString() + ").  Incorrect number of expected fields.");
	    	}
    	} catch (IOException e) {
    		CTILogger.warn(e);
    	} catch (IllegalArgumentException e) {
    		CTILogger.warn(e);
    	}
    }

	/**
     * Helper method to set the loadProfileDemand fields
     * @param meter
     * @param pointValue
     */
	private void setLoadProfileDemand(Meter meter, PointValueHolder pointValue) {
        loadProfileDemand = pointValue.getValue();
        loadProfileDemandDateTime = pointValue.getPointDataTimeStamp();
	}
	
	/**
     * Helper method to set the kVar fields
     * @param meter
     * @param pointValue
     */
	private void setKVar(Meter meter, PointValueHolder pointValue) {
        kVAr = pointValue.getValue();
        kVArDateTime = pointValue.getPointDataTimeStamp();
	}
	
	/**
     * Helper method to set the voltage fields
     * @param meter
     * @param pointValue
     */
	private void setVoltage(Meter meter, PointValueHolder pointValue) {
        voltage = pointValue.getValue();
        voltageDateTime = pointValue.getPointDataTimeStamp();
	}

	   /**
     * Helper method to set the voltageProfile fields
     * @param meter
     * @param pointValue
     */
    private void setVoltageProfile(Meter meter, PointValueHolder pointValue) {
        voltageProfile = pointValue.getValue();
        voltageProfileDateTime = pointValue.getPointDataTimeStamp();
    }

    @Override
    public String getObjectId() {
    	return meterNumber;
    }
}
