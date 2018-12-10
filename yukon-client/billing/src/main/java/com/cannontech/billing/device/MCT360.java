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
 * Class which represents billing data for an MCT360
 */
public class MCT360 extends BillingDeviceBase {

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

            // kVar

            case 11: // Total Kvarh
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;

            case 12: // Rate A Kvar
                addData(Channel.ONE, readingType, BillableField.rateADemand, data);
                break;

            case 13: // Rate A Kvarh
                addData(Channel.ONE, readingType, BillableField.rateAConsumption, data);
                break;

            case 14: // Rate B Kvar
                addData(Channel.ONE, readingType, BillableField.rateBDemand, data);
                break;

            case 15: // Rate B Kvarh
                addData(Channel.ONE, readingType, BillableField.rateBConsumption, data);
                break;

            case 16: // Rate C Kvar
                addData(Channel.ONE, readingType, BillableField.rateCDemand, data);
                break;

            case 17: // Rate C Kvarh
                addData(Channel.ONE, readingType, BillableField.rateCConsumption, data);
                break;

            case 18: // Rate D Kvar
                addData(Channel.ONE, readingType, BillableField.rateDDemand, data);
                break;

            case 19: // Rate D Kvarh
                addData(Channel.ONE, readingType, BillableField.rateDConsumption, data);
                break;

            // kVa

            case 21: // Total Kvah
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;

            case 22: // Rate A Kva
                addData(Channel.ONE, readingType, BillableField.rateADemand, data);
                break;

            case 23: // Rate A Kvah
                addData(Channel.ONE, readingType, BillableField.rateAConsumption, data);
                break;

            case 24: // Rate B Kva
                addData(Channel.ONE, readingType, BillableField.rateBDemand, data);
                break;

            case 25: // Rate B Kvah
                addData(Channel.ONE, readingType, BillableField.rateBConsumption, data);
                break;

            case 26: // Rate C Kva
                addData(Channel.ONE, readingType, BillableField.rateCDemand, data);
                break;

            case 27: // Rate C Kvah
                addData(Channel.ONE, readingType, BillableField.rateCConsumption, data);
                break;

            case 28: // Rate D Kva
                addData(Channel.ONE, readingType, BillableField.rateDDemand, data);
                break;

            case 29: // Rate D Kvah
                addData(Channel.ONE, readingType, BillableField.rateDConsumption, data);
                break;
            }

            break;

        case PulseAccumulator:

            switch (pointIdentifier.getOffset()) {

            case 3: // Total Consumption - channel one
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);

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

            // kVar
            case 11: // Total Kvarh
            case 13: // Rate A Kvarh
            case 15: // Rate B Kvarh
            case 17: // Rate C Kvarh
            case 19: // Rate D Kvarh

            // kVa
            case 21: // Total Kvah
            case 23: // Rate A Kvah
            case 25: // Rate B Kvah
            case 27: // Rate C Kvah
            case 29: // Rate D Kvah
                return true;
            }

            break;

        case PulseAccumulator:

            switch (pointIdentifier.getOffset()) {

            case 3: // Total Consumption - channel one
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

            // kVar
            case 12: // Rate A Kvar
            case 14: // Rate B Kvar
            case 16: // Rate C Kvar
            case 19: // Rate D Kvarh

            // kVa
            case 22: // Rate A Kva
            case 24: // Rate B Kva
            case 26: // Rate C Kva
            case 28: // Rate D Kva
                return true;
            }

            break;
        default:
        }
        return false;
    }
}
