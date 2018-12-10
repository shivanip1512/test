package com.cannontech.billing.device;

import java.sql.Timestamp;

import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.common.pao.definition.model.PointIdentifier;

/**
 * Class which represents billing data for an MCT410
 */
public class MCT410 extends BillingDeviceBase {

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

            case 1: // KWh
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;
                
            case 2: // KWh (Channel 2)
                addData(Channel.TWO, readingType, BillableField.totalConsumption, data);
                break;
                
            case 3: // KWh (Channel 3)
                addData(Channel.THREE, readingType, BillableField.totalConsumption, data);
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

            case 12: // Peak kW (Channel 2)
                addData(Channel.TWO, readingType, BillableField.totalPeakDemand, data);
                break;
                
            case 13: // Peak kW (Channel 3)
                addData(Channel.THREE, readingType, BillableField.totalPeakDemand, data);
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
        default:
        }

    }
    
    @Override
    public boolean isEnergy(PointIdentifier pointIdentifier) {
        switch (pointIdentifier.getPointType()) {

        case PulseAccumulator:

            switch (pointIdentifier.getPointType().getPointTypeId()) {

            case 1: // KWh
            case 2: // KWh (Channel 2)
            case 3: // KWh (Channel 3)
            case 101: // Rate A KWh
            case 121: // Rate B KWh
            case 141: // Rate C KWh
            case 161: // Rate D KWh
                return true;
            }

            break;
        default:
        }
        return false;
    }
    
    @Override
    public boolean isDemand(PointIdentifier pointIdentifier) {
        switch (pointIdentifier.getPointType()) {

        case DemandAccumulator:

            switch (pointIdentifier.getOffset()) {

            case 11: // Peak kW 
            case 12: // Peak kW (Channel 2)
            case 13: // Peak kW (Channel 3)
            case 111: // Rate A KW
            case 131: // Rate B KW
            case 151: // Rate C KW
            case 171: // Rate D KW
                return true;
            }

            break;
        default:
        }
        return false;
    }
}
