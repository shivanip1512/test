package com.cannontech.multispeak.block.data.load;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.spring.YukonSpringHook;

public class LoadBlock implements Block{

    public String meterNumber;
    public Double loadProfileDemand;
    public Date loadProfileDemandDateTime;
    public Double kVAr;
    public Date kVArDateTime;
    public Double voltage;
    public Date voltageDateTime;
    public boolean hasData = false;
    
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
    
    public String getField(SyntaxItem syntaxItem) {
        
        if( syntaxItem.equals(SyntaxItem.METER_NUMBER))
            return meterNumber;
        
        else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND)){
            if( loadProfileDemand != null)
                return String.valueOf(loadProfileDemand);
        }
        
        else if (syntaxItem.equals(SyntaxItem.LOAD_PROFILE_DEMAND_DATETIME)){
            if ( loadProfileDemandDateTime != null){
                SimpleDateFormat sdf = new SimpleDateFormat(defaultDateFormat);
                return sdf.format(loadProfileDemandDateTime);
            }
        }
        
        else if (syntaxItem.equals(SyntaxItem.KVAR)){
            if( kVAr != null)
                return String.valueOf(kVAr);
        }
        
        else if (syntaxItem.equals(SyntaxItem.KVAR_DATETIME)){
            if (kVArDateTime != null){
                SimpleDateFormat sdf = new SimpleDateFormat(defaultDateFormat);
                return sdf.format(kVArDateTime);
            }
        }

        else if (syntaxItem.equals(SyntaxItem.VOLTAGE)){
            if (voltage != null)
            return String.valueOf(voltage);
        }
        
        else if (syntaxItem.equals(SyntaxItem.VOLTAGE_DATETIME)){
            if( voltageDateTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(defaultDateFormat);
                return sdf.format(voltageDateTime);
            }
        }
        
        else {
            CTILogger.error("SyntaxItem: " + syntaxItem + " - Not Valid for LoadBlock");
        }
        
        return "";
    }
    
    public void populate(Meter meter) {
        //TODO - Probably shouldn't set this everytime...need to find a better way.
        meterNumber = meter.getMeterNumber();
    }
    
    //TODO - need to clean this up so that only the attribute that the pointValue is coming for is checked.
    public void populate(Meter meter, PointValueHolder pointValue) {
        populate(meter);
        
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
    
    public boolean hasData() {
        return hasData;
    }
}
