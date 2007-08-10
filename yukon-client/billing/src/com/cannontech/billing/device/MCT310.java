package com.cannontech.billing.device;

import java.sql.Timestamp;

import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents billing data for an MCT310
 */
public class MCT310 extends BillingDeviceBase {

    public void populate(String ptType, int offSet, Timestamp timestamp, double value,
            int unitOfMeasure, String pointName, DeviceData deviceData) {

        addMeterData(Channel.ONE, deviceData);

        BillingData data = new BillingData();
        data.setData(pointName);
        data.setValue(value);
        data.setUnitOfMeasure(unitOfMeasure);
        data.setTimestamp(timestamp);

        int type = PointTypes.getType(ptType);

        switch (type) {

        case PointTypes.PULSE_ACCUMULATOR_POINT:

            switch (offSet) {

            case 1: // KWh
                addData(BillableField.totalConsumption, data);
                break;

            }

            break;
        }

    }

}
