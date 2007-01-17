package com.cannontech.billing.device;

import java.sql.Timestamp;

import com.cannontech.billing.device.base.BillableField;
import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.Channel;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.billing.device.base.ReadingType;
import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents billing data for a Sentinel meter
 */
public class Sentinel extends BillingDeviceBase {

    public void populate(String ptType, int offSet, Timestamp timestamp, double value,
            int unitOfMeasure, String pointName, DeviceData deviceData) {

        addMeterData(Channel.ONE, deviceData);

        BillingData data = new BillingData();
        data.setData(pointName);
        data.setValue(value);
        data.setUnitOfMeasure(unitOfMeasure);
        data.setTimestamp(timestamp);

        ReadingType readingType = getReadingType(unitOfMeasure);
        int type = PointTypes.getType(ptType);

        switch (type) {

        case PointTypes.ANALOG_POINT:

            switch (offSet) {

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

            case 10: // Rate E KW
                // not used for billing
                break;

            case 11: // Rate E KWh
                // not used for billing
                break;

            case 14: // Last Interval KW
                // not used for billing
                break;

            case 15: // Load Profile KW
                // not used for billing
                break;

            case 21: // Total kVarh
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;

            case 22: // Rate A kVar
                addData(Channel.ONE, readingType, BillableField.rateADemand, data);
                break;

            case 23: // Rate A kVarh
                addData(Channel.ONE, readingType, BillableField.rateAConsumption, data);
                break;

            case 24: // Rate B kVar
                addData(Channel.ONE, readingType, BillableField.rateBDemand, data);
                break;

            case 25: // Rate B kVarh
                addData(Channel.ONE, readingType, BillableField.rateBConsumption, data);
                break;

            case 26: // Rate C kVar
                addData(Channel.ONE, readingType, BillableField.rateCDemand, data);
                break;

            case 27: // Rate C kVarh
                addData(Channel.ONE, readingType, BillableField.rateCConsumption, data);
                break;

            case 28: // Rate D kVar
                addData(Channel.ONE, readingType, BillableField.rateDDemand, data);
                break;

            case 29: // Rate D kVarh
                addData(Channel.ONE, readingType, BillableField.rateDConsumption, data);
                break;

            case 30: // Rate E kVar
                // not used for billing
                break;

            case 31: // Rate E kVarh
                // not used for billing
                break;

            case 34: // Last Int./Instantaneous kVar
                // not used for billing
                break;

            case 35: // Load Profile kVar
                // not used for billing
                break;

            case 41: // Total kVah
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;

            case 42: // Rate A kVa
                addData(Channel.ONE, readingType, BillableField.rateADemand, data);
                break;

            case 43: // Rate A kVah
                addData(Channel.ONE, readingType, BillableField.rateAConsumption, data);
                break;

            case 44: // Rate B kVa
                addData(Channel.ONE, readingType, BillableField.rateBDemand, data);
                break;

            case 45: // Rate B kVah
                addData(Channel.ONE, readingType, BillableField.rateBConsumption, data);
                break;

            case 46: // Rate C kVa
                addData(Channel.ONE, readingType, BillableField.rateCDemand, data);
                break;

            case 47: // Rate C kVah
                addData(Channel.ONE, readingType, BillableField.rateCConsumption, data);
                break;

            case 48: // Rate D kVa
                addData(Channel.ONE, readingType, BillableField.rateDDemand, data);
                break;

            case 49: // Rate D kVah
                addData(Channel.ONE, readingType, BillableField.rateDConsumption, data);
                break;

            case 50: // Rate E kVa
                // not used for billing
                break;

            case 51: // Rate E kVah
                // not used for billing
                break;

            case 54: // Last Int./Instantaneous kVa
                // not used for billing
                break;

            case 55: // Load Profile kVa
                // not used for billing
                break;

            case 61: // Instantaneous Voltage Phase A
                // not used for billing
                break;

            case 62: // Load Profile Voltage Phase A
                // not used for billing
                break;

            case 63: // Instantaneous Voltage Phase B
                // not used for billing
                break;

            case 64: // Load Profile Voltage Phase B
                // not used for billing
                break;

            case 65: // Instantaneous Voltage Phase C
                // not used for billing
                break;

            case 66: // Load Profile Voltage Phase C
                // not used for billing
                break;

            case 71: // Instantaneous Current Phase A
                // not used for billing
                break;

            case 72: // Load Profile Current Phase A
                // not used for billing
                break;

            case 73: // Instantaneous Current Phase B
                // not used for billing
                break;

            case 74: // Load Profile Current Phase B
                // not used for billing
                break;

            case 75: // Instantaneous Current Phase C
                // not used for billing
                break;

            case 76: // Load Profile Current Phase C
                // not used for billing
                break;

            case 77: // Instantaneous Neutral Current
                // not used for billing
                break;

            case 78: // Load Profile Neutral Current
                // not used for billing
                break;

            }

            break;

        case PointTypes.PULSE_ACCUMULATOR_POINT:

            switch (offSet) {

            case 1: // Total Consumption - channel 1
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);

                break;

            case 2: // Total Consumption - channel 2
                addMeterData(Channel.TWO, deviceData);
                addData(Channel.TWO, readingType, BillableField.totalConsumption, data);

                break;

            case 3: // Total Consumption - channel 3
                addMeterData(Channel.THREE, deviceData);
                addData(Channel.THREE, readingType, BillableField.totalConsumption, data);

                break;

            case 4: // Total Consumption - channel 4
                addMeterData(Channel.FOUR, deviceData);
                addData(Channel.FOUR, readingType, BillableField.totalConsumption, data);

                break;
            }

            break;

        case PointTypes.DEMAND_ACCUMULATOR_POINT:

            switch (offSet) {

            case 11: // Total Peak Demand - channel 1
                addData(Channel.ONE, readingType, BillableField.totalPeakDemand, data);

                break;

            case 12: // Total Peak Demand - channel 2
                addData(Channel.TWO, readingType, BillableField.totalPeakDemand, data);

                break;

            case 13: // Total Peak Demand - channel 3
                addData(Channel.THREE, readingType, BillableField.totalPeakDemand, data);

                break;

            case 14: // Total Peak Demand - channel 4
                addData(Channel.FOUR, readingType, BillableField.totalPeakDemand, data);

                break;

            }

            break;
        }
    }

}
