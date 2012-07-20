package com.cannontech.billing.device.base;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * The base class for all BillableDevices
 */
public abstract class BillingDeviceBase implements BillableDevice {

    private Map<Channel, Map<ReadingType, Map<BillableField, BillingData>>> channelMap = new HashMap<Channel, Map<ReadingType, Map<BillableField, BillingData>>>();

    /**
     * Helper method to return an adjusted/aggregate Value on channel 1, 2, or 3 (in that order) 
     * for ReadingType type.
     */
    public Double getValue(ReadingType type, BillableField field) {
    	
    	Double value = this.getValue(Channel.ONE, type, field);
    	if( value == null) 
    		value = this.getValue(Channel.TWO, type, field);
    	if( value == null)
    		value = this.getValue(Channel.THREE, type, field);

        return value;
    }

    /**
     * Returns the value for Channel and ReadingType for field.  If field is totalConsumption 
     * or totalPeakDemand, then the value is adjusted/aggregated.  Else the exact value 
     * for field is returned.
     */
    public Double getValue(Channel channel, ReadingType type, BillableField field) {

        if (!field.hasValue()) {
            throw new IllegalArgumentException("Field: " + field.toString()
                    + " does not have a value");
        }

        BillingData data = getAdjustedBillingData(channel, type, field);
        return (data != null) ? data.getValue() : null;
    }

    /**
     * Helper method to return a timestamp on channel 1, 2, or 3 (in that order) 
     * for readingType
     */
    public Timestamp getTimestamp(ReadingType type, BillableField field) {
        Timestamp returnTS = this.getTimestamp(Channel.ONE, type, field);
        if( returnTS == null) 
            returnTS = this.getTimestamp(Channel.TWO, type, field);
        if( returnTS == null)
            returnTS = this.getTimestamp(Channel.THREE, type, field);
        return returnTS; 
    }

    /**
     * Returns the timestamp for Channel and ReadingType for field.  If field is totalConsumption 
     * or totalPeakDemand, then the timestamp is adjusted/aggregated.  Else the exact timestamp 
     * for field is returned.
     */
    public Timestamp getTimestamp(Channel channel, ReadingType type, BillableField field) {

        if (!field.hasTimestamp()) {
            throw new IllegalArgumentException("Field " + field.toString()
                    + " does not have a timestamp");
        }

        BillingData data = getAdjustedBillingData(channel, type, field);
        return (data != null) ? data.getTimestamp() : null;
    };

    public String getData(ReadingType type, BillableField field) {
        return this.getData(Channel.ONE, type, field);
    }

    public String getData(Channel channel, ReadingType type, BillableField field) {

        if (!field.hasData()) {
            throw new IllegalArgumentException("Field " + field.toString() + " does not have data");
        }

        BillingData data = getAdjustedBillingData(channel, type, field);
        return (data != null) ? data.getData() : null;
    }

    public String getUnitOfMeasure(ReadingType type, BillableField field) {
        return this.getUnitOfMeasure(Channel.ONE, type, field);
    }

    public String getUnitOfMeasure(Channel channel, ReadingType type, BillableField field) {
        if (!field.hasUnitOfMeasure()) {
            throw new IllegalArgumentException("Field " + field.toString()
                    + " does not have unit of measure");
        }

        BillingData data = getAdjustedBillingData(channel, type, field);
        return (data != null) ? data.getUnitOfMeasure() : null;

    }

    public String getRate(ReadingType type, BillableField field) {
        return this.getRate(Channel.ONE, type, field);
    }
    
    public String getRate(Channel channel, ReadingType type, BillableField field) {
    
        String rate = field.getRate();
        if (rate == null) {
            throw new IllegalArgumentException("Field " + field.toString()
                    + " does not have a rate");
        }

        // Need to check that we have some data first.
        BillingData data = this.getBillingData(channel, type, field);
        return (data != null) ? rate : null;
    }
    
    /**
     * Method to add data to the dataMap (key: field, value: data). Data will be
     * added to the defaultChannel and readingType type
     * @param field - Field to be used as the key in the map entry
     * @param type - Type that the data is for 
     * @param data - Data to be used as the value in the map entry
     */
    public void addData(BillableField field, ReadingType type, BillingData data) {
        this.addData(Channel.ONE, type, field, data);
    }

    /**
     * Method to add data to the dataMap (key: field, value: data)
     * @param channel - Channel that the data is for
     * @param type - Type that the data is for
     * @param field - Field to be used as the key in the map entry
     * @param data - Data to be used as the value in the map entry
     */
    public void addData(Channel channel, ReadingType type, BillableField field, BillingData data) {

        if (!this.channelMap.containsKey(channel)) {
            this.channelMap.put(channel,
                                new HashMap<ReadingType, Map<BillableField, BillingData>>());
            // new HashMap<BillableField, BillingData>());
        }

        Map<ReadingType, Map<BillableField, BillingData>> typeMap = this.channelMap.get(channel);
        if (!typeMap.containsKey(type)) {
            typeMap.put(type, new HashMap<BillableField, BillingData>());
        }

        Map<BillableField, BillingData> dataMap = typeMap.get(type);

        // Do not try to insert more than one value for the same key in the map
        if (!dataMap.containsKey(field)) {
            dataMap.put(field, data);
        }
    }

    public void addData(BillableField field, ReadingType type, String dataValue) {
        this.addData(Channel.ONE, type, field, dataValue);
    }

    public void addData(Channel channel, ReadingType type, BillableField field, String dataValue) {

        BillingData data = new BillingData();
        data.setData(dataValue);
        addData(channel, type, field, data);
    }

    /**
     * Method to add meter data to a channel
     * @param channel - Channel to add data to
     * @param deviceData - Data to add
     */
    protected void addMeterData(Channel channel, DeviceData deviceData) {

        addData(channel, ReadingType.DEVICE_DATA, BillableField.meterNumber, deviceData.getMeterNumber());
        addData(channel, ReadingType.DEVICE_DATA, BillableField.accountNumber, deviceData.getAccountNumber());
        addData(channel, ReadingType.DEVICE_DATA, BillableField.paoName, deviceData.getPaoName());
        addData(channel, ReadingType.DEVICE_DATA, BillableField.address, deviceData.getAddress());
        addData(channel, ReadingType.DEVICE_DATA, BillableField.meterPositionNumber, deviceData.getMeterPositionNumber());
    }

    /**
     * Method to get the ReadingType based on the unit of measure
     * @param unitOfMeasure - Unit of measure to get the reading type for
     * @return Reading type or null if unknown unit of measure
     */
    protected ReadingType getReadingType(int unitOfMeasure) {

        ReadingType type = null;

        UnitOfMeasure uom = UnitOfMeasure.getForId(unitOfMeasure);
        switch (uom) {

        case KVARH:
        case KVAR:
            type = ReadingType.KVAR;

            break;

        case KVAH:
        case KVA:
            type = ReadingType.KVA;

            break;

        case GALLONS:
        case CUBIC_FEET:
        case CUBIC_METERS:
        case GAL_PM:
            type = ReadingType.WATER;

            break;

        case GAS_CFT:
            type = ReadingType.GAS;

            break;
            
        case KWH:
        case KW:
        default:
            type = ReadingType.ELECTRIC;

            break;
        }

        return type;

    }

    /**
     * Helper method to get a billing data from the channel map
     * @param channel - The channel that the data is in
     * @param type - The type that the data is in
     * @param field - The field that the data is in
     * @return The billing data or null if not found
     */
    private BillingData getBillingData(Channel channel, ReadingType type, BillableField field) {

        BillingData data = null;

        Map<BillableField, BillingData> dataMap = null;
        Map<ReadingType, Map<BillableField, BillingData>> typeMap = this.channelMap.get(channel);

        if (typeMap != null) {

            dataMap = typeMap.get(type);

            if (dataMap != null) {
                data = dataMap.get(field);
            }
        }

        return data;
    }

    /**
     * Helper method to get the billing data for the total consumption
     * Returns the totalConsumption billingData UNLESS it does not exist.
     * If TotalConsumption is null, then an aggregated (fake) billingData object will be created.
     * The aggregated BillingData object will contain the value equal to the sum of all rates consumption reading
     *  and the timestamp equal to the maximum timestamp of all rates consumption timestamp. 
     * @param channel - Channel to get the total consumption for
     * @param type - Reading type to get the total consumption for
     * @return BillingData for the total consumption
     */
    private BillingData getTotalConsumption(Channel channel, ReadingType type) {

        BillingData returnData = this.getBillingData(channel, type, BillableField.totalConsumption);

        if (returnData == null) {

            BillingData aggregatedBillingData = new BillingData();
            aggregatedBillingData.addForTotalConsumption(this.getBillingData(channel,
                                                                            type,
                                                                            BillableField.rateAConsumption));
            aggregatedBillingData.addForTotalConsumption(this.getBillingData(channel,
                                                                            type,
                                                                            BillableField.rateBConsumption));
            aggregatedBillingData.addForTotalConsumption(this.getBillingData(channel,
                                                                            type,
                                                                            BillableField.rateCConsumption));
            aggregatedBillingData.addForTotalConsumption(this.getBillingData(channel,
                                                                            type,
                                                                            BillableField.rateDConsumption));
            
            //Verify that we got an actual Value during our aggregated calculations
            if( aggregatedBillingData.getValue() != null)
                returnData = aggregatedBillingData;
        }

        return returnData;
    }

    /**
     * Helper method to get the billing data for the total peak demand
     * @param channel - Channel to get the total demand for
     * @param type - Reading type to get the total demand for
     * @return BillingData for the total peak demand
     */
    private BillingData getTotalDemand(Channel channel, ReadingType type) {

        BillingData data = this.getBillingData(channel, type, BillableField.totalPeakDemand);

        if (data == null) {
            data = this.getMaxData(data, this.getBillingData(channel,
                                                             type,
                                                             BillableField.rateADemand));
            data = this.getMaxData(data, this.getBillingData(channel,
                                                             type,
                                                             BillableField.rateBDemand));
            data = this.getMaxData(data, this.getBillingData(channel,
                                                             type,
                                                             BillableField.rateCDemand));
            data = this.getMaxData(data, this.getBillingData(channel,
                                                             type,
                                                             BillableField.rateDDemand));
        }

        return data;

    }

    /**
     * Helper method to determine the BillingData with the max value
     * @param data1 - BillingData to compare
     * @param data2 - BillingData to compare
     * @return - BillingData with max value
     */
    private BillingData getMaxData(BillingData data1, BillingData data2) {

        BillingData returnData = null;

        if (data1 == null) {
            returnData = data2;
        } else if (data2 == null) {
            returnData = data1;
        } else if (data2.getValue() > data1.getValue()) {
            returnData = data2;
        } else {
            returnData = data1;
        }

        return returnData;

    }
    
    /**
     * Helper method to return an adjusted billingData object.
     * For totalConsumption, returns an aggregated field or other rate if total not populated.
     * For totalDemand, returns peak of peaks if total not populated.
     * Otherwise, simply return normal billingData. 
     * @param channel
     * @param type
     * @param field
     * @return
     */
	private BillingData getAdjustedBillingData(Channel channel, ReadingType type, BillableField field) {
		BillingData data;
		if (field.equals(BillableField.totalConsumption)) {
            data = this.getTotalConsumption(channel, type);
        } else if (field.equals(BillableField.totalPeakDemand)) {
            data = this.getTotalDemand(channel, type);
        } else {
            data = this.getBillingData(channel, type, field);
        }
		return data;
	}
}
