package com.cannontech.billing.device.base;

import java.sql.Timestamp;

import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.common.pao.definition.model.PointIdentifier;

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
    public void populate(PointIdentifier pointIdentifier, Timestamp timestamp, double value,
            int unitOfMeasure, String pointName, DeviceData deviceData);

    /**
     * Method to add a piece of data to this device (meternumber, accountnumber,
     * etc...). Adds data to channel one and electric type
     * @param field - The name of the field to store the data as
     * @param type - The type to add the data to. 
     * @param data - The data to be stored
     */
    public void addData(BillableField field, ReadingType type, String data);

    /**
     * Method to add a piece of data to this device (meternumber, accountnumber,
     * etc...).
     * @param channel - The channel to add the data to.
     * @param type - The type to add the data to.
     * @param field - The name of the field to store the data as
     * @param data - The data to be stored
     */
    public void addData(Channel channel, ReadingType type, BillableField field, String data);

    /**
     * Method to get the unit of measure for a specific field from this device.
     * Gets the value from channel one and electric type
     * @param type - The type to add the data to. 
     * @param field - Field to get the value for
     * @return The value
     */
    public String getUnitOfMeasure(ReadingType type, BillableField field);

    /**
     * Method to get the unit of measure for a specific field from this device.
     * @param channel - The channel to get the field for
     * @param type - The type to get the field for
     * @param field - Field to get the unit of measure for
     * @return The value
     */
    public String getUnitOfMeasure(Channel channel, ReadingType type, BillableField field);

    /**
     * Helper method to return the rate based on the billableField.
     * Gets the value from channel one
     * @param type - The type to get the field for
     * @param field - Field to get the rate for
     * @return The value
     */
    public String getRate(ReadingType type, BillableField billableField);

    /**
     * Helper method to return the rate based on the billableField.
     * @param channel - The channel to get the field for
     * @param type - The type to get the field for
     * @param field - Field to get the rate for
     * @return The value
     */
    public String getRate(Channel channel, ReadingType type, BillableField billableField);

    /**
     * Method to get a specific value from this device. Gets the value from
     * channel one and electric type. If the value is total consumption or total
     * demand, the value will be adjusted/aggregated if not found using the given 'total'
     * BillableField
     * @param type - The type to add the data to.
     * @param field - Field to get the value for
     * @return The value
     */
    public Double getValue(ReadingType type, BillableField field);

    /**
     * Method to get a specific value from this device. If the value is total
     * consumption or total demand, the value will be adjusted/aggregated if not found
     * using the given 'total' BillableField
     * @param channel - The channel to get the value for
     * @param type - The type to get the data for
     * @param field - Field to get the value for
     * @return The value
     */
    public Double getValue(Channel channel, ReadingType type, BillableField field);

    /**
     * Method to get a specific timestamp from this device. Gets the timestamp
     * from channel one and electric type
     * @param type - The type to get the data for
     * @param field - Field to get the timestamp for
     * @return The timestamp
     */
    public Timestamp getTimestamp(ReadingType type, BillableField field);

    /**
     * Method to get a specific timestamp from this device.
     * @param channel - The channel to get the timestamp for
     * @param type - The type to get the data for
     * @param field - Field to get the timestamp for
     * @return The timestamp
     */
    public Timestamp getTimestamp(Channel channel, ReadingType type, BillableField field);

    /**
     * Method to get a specific piece of data from this device. 
     * Gets the data from Channel.ONE
     * @param type - The type to add the data to. 
     * @param field - Field to get the piece of data for
     * @return The piece of data
     */
    public String getData(ReadingType type, BillableField field);

    /**
     * Method to get a specific piece of data from this device.
     * @param channel - The channel to get the data for
     * @param type - The type to get the data for
     * @param field - Field to get the piece of data for
     * @return The piece of data
     */
    public String getData(Channel channel, ReadingType type, BillableField field);

    /**
     * Method to return true if the devicePointIdentifier is "Energy". 
     * @param pointType
     * @param pointOffset
     * @return true if energy type reading
     */
    public boolean isEnergy(PointIdentifier pointIdentifier);
    
    /**
     * Method to return true if the devicePointIdentifier is "Demand".
     * @param pointType
     * @param pointOffset
     * @return true if demand type reading
     */
    public boolean isDemand(PointIdentifier pointIdentifier);

}
