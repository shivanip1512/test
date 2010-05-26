package com.cannontech.multispeak.data;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.spring.YukonSpringHook;

/**
 * The base class for all BillableDevices
 */
public abstract class MeterReadBase implements ReadableDevice{
	
	private AttributeDynamicDataSource attrDynamicDataSource = YukonSpringHook.getBean("attrDynamicDataSource", AttributeDynamicDataSource.class);

    private MeterRead meterRead;
    private boolean populated = false;

    @Override
    public MeterRead getMeterRead(){
        if( meterRead == null)
            meterRead = new MeterRead();
        return meterRead;
    }
    
    @Override
    public void setMeterNumber(String meterNumber) {
        getMeterRead().setMeterNo(meterNumber);
        getMeterRead().setObjectID(meterNumber);
        getMeterRead().setDeviceID(meterNumber);
        getMeterRead().setUtility(MultispeakDefines.AMR_VENDOR);
    }
    
    @Override
	public void populate(Meter meter, RichPointData richPointData) {
		
		for (Attribute attribute : getMeterReadCompatibleAttributes()) {
			populateByPointValue(meter, richPointData, attribute);
		}
	}
    
    /**
     * Check point data is valid in general.
     * Check that point data for given attribute is valid match before setting values on MeterRead.
     */
    private void populateByPointValue(Meter meter, RichPointData richPointData, Attribute attribute) {
		
    	// general checks
    	if (richPointData == null) {
			return;
		}

		if (richPointData.getPointValue().getPointQuality().getQuality() == PointQuality.Uninitialized.getQuality()) {
			return;
		}
    	
		// check point is for attribute
		AttributeService attributeService = (AttributeService)YukonSpringHook.getBean("attributeService");
		try {
		    boolean isPointForAttribute = attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), attribute);
		    if(isPointForAttribute){
		    	populate(meter, richPointData, attribute);
		    	return;
		    }
		} catch (IllegalArgumentException e) {
		    CTILogger.debug(e);
		} catch (NotFoundException e){
		    CTILogger.error(e);
		}
	}
    
    /**
     * Direct point data to correct setters on the MeterRead based on attribute type is has been verified as
     */
    private void populate(Meter meter, RichPointData richPointData, Attribute attribute) {
		
		if (attribute.equals(BuiltInAttribute.USAGE)) {
			setUsage(meter, richPointData);
		} else if (attribute.equals(BuiltInAttribute.PEAK_DEMAND)) {
			setPeakDemand(meter, richPointData);
		} else {
			throw new IllegalArgumentException("Attribute " + attribute.toString() + " is not supported.");
		}
    }
    
    // USAGE
    protected void setUsage(Meter meter, RichPointData richPointData) {
        
    	getMeterRead().setReadingDate(calendarForPointData(richPointData));
        getMeterRead().setPosKWh(new BigDecimal(richPointData.getPointValue().getValue()).toBigInteger());
        setPopulated(true);
    }
    
    // PEAK_DEMAND
    protected void setPeakDemand(Meter meter, RichPointData richPointData) {
    	
    	getMeterRead().setKW(new Float(richPointData.getPointValue().getValue()));
        getMeterRead().setKWDateTime(calendarForPointData(richPointData));
        setPopulated(true);
    }
    
    // calendar helper
    private GregorianCalendar calendarForPointData(RichPointData richPointData) {
    	
    	GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(richPointData.getPointValue().getPointDataTimeStamp().getTime());
        return calendar;
    }
    
    @Override
    public boolean isPopulated()
    {
        return populated;
    }

    @Override
    public void setPopulated(boolean value)
    {
        populated = value;        
    }
    
    @Override
    public void populateWithCachedPointData(Meter meter) {
    	
    	for (Attribute attribute : getMeterReadCompatibleAttributes()) {
    		
    		RichPointData richPointData = attrDynamicDataSource.getRichPointData(meter, attribute);
    		populate(meter, richPointData);
    	}
    }
}
