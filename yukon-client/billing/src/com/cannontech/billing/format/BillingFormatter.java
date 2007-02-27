package com.cannontech.billing.format;

import java.io.IOException;
import java.util.List;

import com.cannontech.billing.device.base.BillableDevice;

/**
 * Interface to be implemented for each type of billing format
 */
public interface BillingFormatter {

    /**
     * Method to generate a String which is the billing data for the given
     * device in this billing format
     * @param device - Device to generate the formatted billing data for
     * @return A String representing the formatted billing data for the device
     */
    public String dataToString(BillableDevice device);

    /**
     * Method to create a String billing file which includes all of the devices
     * and then write the string out to a file in the deviceList
     * @param deviceList - List of devices to create a billing file for
     * @return The number of valid readings
     */
    public int writeBillingFile(List<BillableDevice> deviceList)
            throws IOException;

}
