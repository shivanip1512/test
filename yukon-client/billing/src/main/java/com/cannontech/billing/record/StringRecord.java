package com.cannontech.billing.record;

public class StringRecord implements BillingRecordBase {
    private String data = null;

    /**
     * StringRecord constructor comment.
     */
    protected StringRecord() {
        super();
    }

    /**
     * Converts data in a StringFormat to a formatted StringBuffer for stream use.
     */
    @Override
    public final String dataToString() {
        return getData();
    }

    /**
     * Insert the method's description here.
     */
    public java.lang.String getData() {
        return data;
    }

    /**
     * Insert the method's description here.
     */
    public void setData(java.lang.String newData) {
        data = newData;
    }
}
