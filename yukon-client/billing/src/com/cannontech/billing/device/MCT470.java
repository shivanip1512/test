package com.cannontech.billing.device;

import java.sql.Timestamp;

import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents billing data for an MCT470
 */
public class MCT470 extends BillingDeviceBase {

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

}
