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
 * Class which represents billing data for an MCT410
 */
public class MCT410 extends BillingDeviceBase {

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

        case PointTypes.PULSE_ACCUMULATOR_POINT:

            switch (offSet) {

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

        case PointTypes.DEMAND_ACCUMULATOR_POINT:

            switch (offSet) {

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
        }

    }

}
