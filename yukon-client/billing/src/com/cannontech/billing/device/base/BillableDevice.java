package com.cannontech.billing.device.base;

import java.sql.Timestamp;

import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Interface to be implemented for each type of billable device
 */
public interface BillableDevice {

    /**
     * Method to add populate the billable fields of this device
     * @param ptType - the type of point for the data being populated
     * @param offSet - OffSet of the point from which the timestamp and value
     *            came from
     * @param timestamp - Time the reading was taken
     * @param value - Point reading
     * @param unitOfMeasure - Unit of measure id for the current reading
     * @param pointName - point name for the current point
     * @param deviceData - Value object which contains data about the current
     *            device
     */
    public void populate(String ptType, int offSet, Timestamp timestamp, double value,
            int unitOfMeasure, String pointName, DeviceData deviceData);

    /**
     * Method to add a piece of data to this device (meternumer, accountnumber,
     * etc...). Adds data to channel one and electric type
     * @param field - The name of the field to store the data as
     * @param data - The data to be stored
     */
    public void addData(BillableField field, String data);

    /**
     * Method to add a piece of data to this device (meternumer, accountnumber,
     * etc...).
     * @param channel - The channel to add the data to.
     * @param type - The type to add the data to.
     * @param field - The name of the field to store the data as
     * @param data - The data to be stored
     */
    public void addData(Channel channel, ReadingType type, BillableField field, String data);

    /**
     * Method to get a specific value from this device. Gets the value from
     * channel one and electric type
     * @param field - Field to get the value for
     * @return The value
     */
    public Double getValue(BillableField field);

    /**
     * Method to get a specific value from this device.
     * @param channel - The channel to get the value for
     * @param type - The type to get the data for
     * @param field - Field to get the value for
     * @return The value
     */
    public Double getValue(Channel channel, ReadingType type, BillableField field);

    /**
     * Method to get the unit of measure for a specific field from this device.
     * Gets the value from channel one and electric type
     * @param field - Field to get the value for
     * @return The value
     */
    public String getUnitOfMeasure(BillableField field);

    /**
     * Method to get the unit of measure for a specific field from this device.
     * @param channel - The channel to get the field for
     * @param type - The type to get the field for
     * @param field - Field to get the unit of measure for
     * @return The value
     */
    public String getUnitOfMeasure(Channel channel, ReadingType type, BillableField field);

    /**
     * Method to get a specific value from this device. Gets the value from
     * channel one and electric type. If the value is total consumption or total
     * demand, the value will be calculated if not found using the given 'total'
     * BillableField
     * @param field - Field to get the value for
     * @return The value
     */
    public Double getCalculatedValue(BillableField field);

    /**
     * Method to get a specific value from this device. If the value is total
     * consumption or total demand, the value will be calculated if not found
     * using the given 'total' BillableField
     * @param channel - The channel to get the value for
     * @param type - The type to get the data for
     * @param field - Field to get the value for
     * @return The value
     */
    public Double getCalculatedValue(Channel channel, ReadingType type, BillableField field);

    /**
     * Method to get a specific timestamp from this device. Gets the timestamp
     * from channel one and electric type
     * @param field - Field to get the timestamp for
     * @return The timestamp
     */
    public Timestamp getTimestamp(BillableField field);

    /**
     * Method to get a specific timestamp from this device.
     * @param channel - The channel to get the timestamp for
     * @param type - The type to get the data for
     * @param field - Field to get the timestamp for
     * @return The timestamp
     */
    public Timestamp getTimestamp(Channel channel, ReadingType type, BillableField field);

    /**
     * Method to get a specific piece of data from this device. Gets the data
     * from channel one and electric type
     * @param field - Field to get the piece of data for
     * @return The piece of data
     */
    public String getData(BillableField field);

    /**
     * Method to get a specific piece of data from this device.
     * @param channel - The channel to get the data for
     * @param type - The type to get the data for
     * @param field - Field to get the piece of data for
     * @return The piece of data
     */
    public String getData(Channel channel, ReadingType type, BillableField field);

}
