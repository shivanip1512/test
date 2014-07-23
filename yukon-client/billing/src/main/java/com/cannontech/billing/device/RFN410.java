package com.cannontech.billing.device;

import java.sql.Timestamp;

import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.database.data.point.PointType;

public class RFN410 extends BillingDeviceBase {
    
    public void populate(PointIdentifier pointIdentifier, Timestamp timestamp, double value, int unitOfMeasure, String pointName, DeviceData deviceData) {
        addMeterData(Channel.ONE, deviceData);

        BillingData data = new BillingData();
        data.setData(pointName);
        data.setValue(value);
        data.setUnitOfMeasure(unitOfMeasure);
        data.setTimestamp(timestamp);

        ReadingType readingType = getReadingType(unitOfMeasure);
        
        switch (pointIdentifier.getPointType()) {

        case Analog:

            switch (pointIdentifier.getOffset()) {

            // Electric

            case 1: // KWh
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;
                
            case 105: // Peak kW
                addData(Channel.ONE, readingType, BillableField.totalPeakDemand, data);
                break;
            }
            
        }
    }

    @Override
    public boolean isEnergy(PointIdentifier pointIdentifier) {
        if (pointIdentifier.getPointType() == PointType.Analog && pointIdentifier.getOffset() == 1) {
                return true;
        }
        return false;
    }
     
    @Override
    public boolean isDemand(PointIdentifier pointIdentifier) {
        if (pointIdentifier.getPointType() == PointType.Analog && pointIdentifier.getOffset() == 105) {
            return true;
        }
        return false;
    }
}