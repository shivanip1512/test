package com.cannontech.billing.device;

import java.sql.Timestamp;

import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents default billing data for an MCT
 */
public class GenericMCT extends BillingDeviceBase {

    public void populate(DevicePointIdentifier devicePointIdentifier, Timestamp timestamp, double value,
            int unitOfMeasure, String pointName, DeviceData deviceData) {

        addMeterData(Channel.ONE, deviceData);

        BillingData data = new BillingData();
        data.setData(pointName);
        data.setValue(value);
        data.setUnitOfMeasure(unitOfMeasure);
        data.setTimestamp(timestamp);

        ReadingType readingType = getReadingType(unitOfMeasure);

        switch (devicePointIdentifier.getType()) {

        case PointTypes.PULSE_ACCUMULATOR_POINT:

            switch (devicePointIdentifier.getOffset()) {

            case 1: // KWh
                addData(Channel.ONE, readingType, BillableField.totalConsumption, data);
                break;

            }

            break;
        }
    }

    @Override
    public boolean isEnergy(DevicePointIdentifier devicePointIdentifier) {

        switch (devicePointIdentifier.getType()) {

        case PointTypes.PULSE_ACCUMULATOR_POINT:

            switch (devicePointIdentifier.getOffset()) {

            case 1: // KWh
                return true;
            }
            break;
        }
        return false;
    }

    @Override
    public boolean isDemand(DevicePointIdentifier devicePointIdentifier) {
        return false;
    }
}
