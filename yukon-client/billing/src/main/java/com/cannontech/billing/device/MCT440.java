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



/**
 * Class representing billing data for an MCT440-based device
 */
public class MCT440 extends BillingDeviceBase {

    @Override
    public void populate(PointIdentifier pointIdentifier, Timestamp timestamp, double value,
                         int unitOfMeasure, String pointName, DeviceData deviceData) {
        addMeterData(Channel.ONE, deviceData);

        BillingData data = new BillingData();
        data.setData(pointName);
        data.setValue(value);
        data.setUnitOfMeasure(unitOfMeasure);
        data.setTimestamp(timestamp);

        ReadingType readingType = getReadingType(unitOfMeasure);

        switch (pointIdentifier.getPointType()) {

        case PulseAccumulator:

            switch (pointIdentifier.getOffset()) {

            // FORWARD ENERGY in Channel.ONE
            case 1: // KWh
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;
                
            case 101: // Rate A KWh
                addData(Channel.ONE, readingType, BillableField.rateAConsumption, data);
                break;

            case 121: // Rate B KWh
                addData(Channel.ONE, readingType, BillableField.rateBConsumption, data);
                break;

            case 141: // Rate C KWh
                addData(Channel.ONE, readingType, BillableField.rateCConsumption, data);
                break;

            case 161: // Rate D KWh
                addData(Channel.ONE, readingType, BillableField.rateDConsumption, data);
                break;
            }

            break;

        case DemandAccumulator:

            switch (pointIdentifier.getOffset()) {

            case 11: // Peak kW 
                addData(Channel.ONE, readingType, BillableField.totalPeakDemand, data);
                break;

            case 111: // Rate A KW
                addData(Channel.ONE, readingType, BillableField.rateADemand, data);
                break;

            case 131: // Rate B KW
                addData(Channel.ONE, readingType, BillableField.rateBDemand, data);
                break;

            case 151: // Rate C KW
                addData(Channel.ONE, readingType, BillableField.rateCDemand, data);
                break;

            case 171: // Rate D KW
                addData(Channel.ONE, readingType, BillableField.rateDDemand, data);
                break;
            }

            break;

        }        
    }

    @Override
    public boolean isEnergy(PointIdentifier pointIdentifier) {
        
        if (pointIdentifier.getPointType() == PointType.PulseAccumulator) {

            switch (pointIdentifier.getOffset()) {

            case 1:     // KWh
            case 101:   // Rate A KWh
            case 121:   // Rate B KWh
            case 141:   // Rate C KWh
            case 161:   // Rate D KWh
            case 21:    // Reverse kWh
            case 201:   // Reverse Rate A KWh
            case 221:   // Reverse Rate B KWh
            case 241:   // Reverse Rate C KWh
            case 261:   // Reverse Rate D KWh
                return true;
            default:
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean isDemand(PointIdentifier pointIdentifier) {
        
        if (pointIdentifier.getPointType() == PointType.DemandAccumulator) {

            switch (pointIdentifier.getOffset()) {

            case 11:    // Peak kW 
            case 111:   // Rate A KW
            case 131:   // Rate B KW
            case 151:   // Rate C KW
            case 171:   // Rate D KW
                return true;
            default:
                return false;
            }
        }

        return false;
    }
}
