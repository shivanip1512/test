package com.cannontech.billing.device.base;

import java.sql.Timestamp;

import com.cannontech.database.data.point.PointUnits;

/**
 * Data class which represents data for a BillableField
 */
public class BillingData {

    private String data = null;
    private Double value = null;
    private String unitOfMeasure = null;
    private Timestamp timestamp = null;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Integer unitOfMeasure) {

        switch (unitOfMeasure) {

        case PointUnits.UOMID_KWH:
            this.unitOfMeasure = "KWH";
            break;

        case PointUnits.UOMID_KW:
            this.unitOfMeasure = "KW";
            break;

        case PointUnits.UOMID_MWH:
            this.unitOfMeasure = "MWH";
            break;

        case PointUnits.UOMID_MW:
            this.unitOfMeasure = "MW";
            break;

        case PointUnits.UOMID_KVA:
            this.unitOfMeasure = "KVA";
            break;

        case PointUnits.UOMID_KVAH:
            this.unitOfMeasure = "KVAH";
            break;

        case PointUnits.UOMID_KVAR:
            this.unitOfMeasure = "KVAR";
            break;

        case PointUnits.UOMID_KVARH:
            this.unitOfMeasure = "KVARH";
            break;

        }
    }

    /**
     * Method to add the value and timestamp of the data passed in to this
     * @param data - BillingData to add
     */
    public void addForTotalConsumption(BillingData data) {

        if (data != null) {

            if (data.getValue() != null) {

                if (this.value != null) {
                    this.value += data.getValue();
                } else {
                    this.value = data.getValue();
                }
            }

            if (data.getTimestamp() != null) {

                if (this.timestamp == null
                        || (this.timestamp != null && this.timestamp.getTime() < data.getTimestamp()
                                                                                     .getTime())) {
                    this.timestamp = data.getTimestamp();
                    //Set the additional fields data and unitOfMeasure to be set with the same values from the Timestamp.
                    //Don't assume that this is the total consumption point name, it is simply the field values that correspond to the timestamp used.
                    this.data = data.getData();
                    this.unitOfMeasure = data.getUnitOfMeasure();
                }
            }
        }
    }
}