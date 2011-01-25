package com.cannontech.multispeak.dao.impl;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.multispeak.dao.MspMeterReadDao;
import com.cannontech.multispeak.deploy.service.MeterRead;

public class MspMeterReadDaoImpl implements MspMeterReadDao {

    public AttributeDynamicDataSource attrDynamicDataSource;
    
    public void setAttrDynamicDataSource(
            AttributeDynamicDataSource attrDynamicDataSource) {
        this.attrDynamicDataSource = attrDynamicDataSource;
    }
    
    public MeterRead getMeterRead(Meter meter) {
        MeterRead meterRead = new MeterRead();
        meterRead.setDeviceID(meter.getMeterNumber());
        meterRead.setMeterNo(meter.getMeterNumber());
        meterRead.setObjectID(meter.getMeterNumber());

        try {
            PointValueHolder demand = attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.DEMAND);
            meterRead.setKW(new Float(demand.getValue()));
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(demand.getPointDataTimeStamp());
            meterRead.setKWDateTime(cal);
        } catch (IllegalUseOfAttribute e) {
        	//If the attribute/point doesn't exist, don't add the data
        }

        try {
            PointValueHolder usage = attrDynamicDataSource.getPointValue(meter, BuiltInAttribute.USAGE);
            meterRead.setPosKWh(new BigInteger(String.valueOf(new Double(usage.getValue()).intValue())));
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(usage.getPointDataTimeStamp());
            meterRead.setReadingDate(cal);
        } catch (IllegalUseOfAttribute e) {
        	//If the attribute/point doesn't exist, don't add the data
        }
        return meterRead;
    }

    public MeterRead[] getMeterRead( List<Meter> meters) {
        
        MeterRead[] meterRead = new MeterRead[meters.size()];
        int indexCount = 0;
        for (Meter meter : meters) {
            meterRead[indexCount++] = getMeterRead(meter);
        }
        
        return meterRead;
    }

}
