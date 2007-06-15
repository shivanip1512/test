package com.cannontech.multispeak.dao.impl;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.dao.MspMeterReadDao;
import com.cannontech.multispeak.service.MeterRead;

public class MspMeterReadDaoImpl implements MspMeterReadDao {

    public AttributeDynamicDataSource attrDynamicDataSource;
    
    public void setAttrDynamicDataSource(
            AttributeDynamicDataSource attrDynamicDataSource) {
        this.attrDynamicDataSource = attrDynamicDataSource;
    }
    
    public MeterRead getMeterRead(Meter meter, String uniqueKey) {
        String meterID = ""; 
        if( uniqueKey.toLowerCase().startsWith("device") || uniqueKey.toLowerCase().startsWith("pao"))
            meterID = meter.getName();
        else //if(key.toLowerCase().startsWith("meternum"))
            meterID = meter.getMeterNumber();
        
        MeterRead meterRead = new MeterRead();
        meterRead.setDeviceID(meterID);
        meterRead.setMeterNo(meterID);
        meterRead.setObjectID(meterID);

        try {
            PointValueHolder demand = 
                attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.DEMAND);
            meterRead.setKW(new Float(demand.getValue()));
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(demand.getPointDataTimeStamp());
            meterRead.setKWDateTime(cal);
        } catch (IllegalArgumentException e) {}

        try {
            PointValueHolder usage = attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.USAGE);
            meterRead.setPosKWh(new BigInteger(String.valueOf(new Double(usage.getValue()).intValue())));
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(usage.getPointDataTimeStamp());
            meterRead.setReadingDate(cal);
        } catch (IllegalArgumentException e) {}
        
        return meterRead;
    }

    public MeterRead[] getMeterRead( List<Meter> meters, String uniqueKey) {
        
        MeterRead[] meterRead = new MeterRead[meters.size()];
        int indexCount = 0;
        for (Meter meter : meters) {
            meterRead[indexCount++] = getMeterRead(meter, uniqueKey);
        }
        
        return meterRead;
    }

}
