package com.cannontech.multispeak.block.data.load.v4;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.block.syntax.v4.SyntaxItem;
import com.cannontech.multispeak.block.v4.Block;

public class LoadBlock implements Block {

    public String meterNumber;
    public Double loadProfileDemand;
    public Date loadProfileDemandDateTime;
    public Double kVAr;
    public Date kVArDateTime;
    public Double voltage;
    public Date voltageDateTime;
    public Double voltageProfile;
    public Date voltageProfileDateTime;
    
    public boolean hasData = false;
    
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

	@Override
	public boolean hasData() {
	    return hasData;
	}
	
    @Override
    public String getObjectId() {
    	return meterNumber;
    }
    
    /**
     * Sets the loadProfile value and timestamp fields
     * Sets hasData flag
     * @param value
     */
    public void setLoadProfileDemand(PointValueHolder value) {
        this.loadProfileDemand = value.getValue();
        this.loadProfileDemandDateTime = value.getPointDataTimeStamp();
        this.hasData = true;
    }

    /**
     * Sets the kVar value and timestamp fields
     * Sets hasData flag
     * @param value
     */
    public void setkVAr(PointValueHolder value) {
        kVAr = value.getValue();
        kVArDateTime = value.getPointDataTimeStamp();
        this.hasData = true;
    }

    /**
     * Sets the voltage value and timestamp fields
     * Sets hasData flag
     * @param value
     */
    public void setVoltage(PointValueHolder value) {
        voltage = value.getValue();
        voltageDateTime = value.getPointDataTimeStamp();
        this.hasData = true;
    }
    
    /**
     * Sets the voltageProfile value and timestamp fields
     * Sets hasData flag
     * @param value
     */
    public void setVoltageProfile(PointValueHolder value) {
        voltageProfile = value.getValue();
        voltageProfileDateTime = value.getPointDataTimeStamp();
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
