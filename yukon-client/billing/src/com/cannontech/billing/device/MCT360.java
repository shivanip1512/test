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
 * Class which represents billing data for an MCT360
 */
public class MCT360 extends BillingDeviceBase {

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

        case PointTypes.PULSE_ACCUMULATOR_POINT:

            switch (offSet) {

            case 3: // Total Consumption - channel one
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);

                break;
            }

            break;
        }

    }
}
