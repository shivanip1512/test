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
 * Class which represents billing data for an MCT430
 */
public class MCT430 extends BillingDeviceBase {

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

        case Analog:

            switch (pointIdentifier.getOffset()) {

            // Electric

            case 1: // KWh
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;

            case 2: // Rate A KW
                addData(Channel.ONE, readingType, BillableField.rateADemand, data);
                break;

            case 3: // Rate A KWh
                addData(Channel.ONE, readingType, BillableField.rateAConsumption, data);
                break;

            case 4: // Rate B KW
                addData(Channel.ONE, readingType, BillableField.rateBDemand, data);
                break;

            case 5: // Rate B KWh
                addData(Channel.ONE, readingType, BillableField.rateBConsumption, data);
                break;

            case 6: // Rate C KW
                addData(Channel.ONE, readingType, BillableField.rateCDemand, data);
                break;

            case 7: // Rate C KWh
                addData(Channel.ONE, readingType, BillableField.rateCConsumption, data);
                break;

            case 8: // Rate D KW
                addData(Channel.ONE, readingType, BillableField.rateDDemand, data);
                break;

            case 9: // Rate D KWh
                addData(Channel.ONE, readingType, BillableField.rateDConsumption, data);
                break;

            // Variable - could be any type of reading

            case 11: // Total Consumption
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;

            case 12: // Rate A Demand
                addData(Channel.ONE, readingType, BillableField.rateADemand, data);
                break;

            case 13: // Rate A Consumption
                addData(Channel.ONE, readingType, BillableField.rateAConsumption, data);
                break;

            case 14: // Rate B Demand
                addData(Channel.ONE, readingType, BillableField.rateBDemand, data);
                break;

            case 15: // Rate B Consumption
                addData(Channel.ONE, readingType, BillableField.rateBConsumption, data);
                break;

            case 16: // Rate C Demand
                addData(Channel.ONE, readingType, BillableField.rateCDemand, data);
                break;

            case 17: // Rate C Consumption
                addData(Channel.ONE, readingType, BillableField.rateCConsumption, data);
                break;

            case 18: // Rate D Demand
                addData(Channel.ONE, readingType, BillableField.rateDDemand, data);
                break;

            case 19: // Rate D Consumption
                addData(Channel.ONE, readingType, BillableField.rateDConsumption, data);
                break;

            case 21: // Peak kW (IED)
                addData(Channel.ONE, readingType, BillableField.totalPeakDemand, data);
                break;

            case 22: // Peak kM (Coincidental) (IED)
                addData(Channel.ONE, readingType, BillableField.totalPeakDemand, data);
                break;

            }
            break;
        default:
        }
    }
    
    @Override
    public boolean isEnergy(PointIdentifier pointIdentifier) {
        switch (pointIdentifier.getPointType()) {

        case Analog:

            switch (pointIdentifier.getOffset()) {

            // Electric
            case 1: // KWh
            case 3: // Rate A KWh
            case 5: // Rate B KWh
            case 7: // Rate C KWh
            case 9: // Rate D KWh

            // Variable - could be any type of reading
            case 11: // Total Consumption
            case 13: // Rate A Consumption
            case 15: // Rate B Consumption
            case 17: // Rate C Consumption
            case 19: // Rate D Consumption
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

        case Analog:

            switch (pointIdentifier.getOffset()) {

            // Electric
            case 2: // Rate A KW
            case 4: // Rate B KW
            case 6: // Rate C KW
            case 8: // Rate D KW

            // Variable - could be any type of reading
            case 12: // Rate A Demand
            case 14: // Rate B Demand
            case 16: // Rate C Demand
            case 18: // Rate D Demand
            case 21: // Peak kW (IED)
            case 22: // Peak kM (Coincidental) (IED)
                return true;
            }

            break;
        default:
        }
        return false;
    }
}
