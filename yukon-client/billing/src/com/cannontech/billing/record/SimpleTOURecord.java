package com.cannontech.billing.record;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Billing record for simple TOU
 */
public class SimpleTOURecord implements BillingRecordBase {
    private String meterNumber = null;

    private Double totalConsumption = null;
    private String timeTotal = null; // HH:mm
    private String dateTotal = null; // MM/dd/yyyy

    private Double readingKW = null;
    private String timeKW = null; // HH:mm
    private String dateKW = null; // MM/dd/yyyy

    private Double onPeakReadingKWh = null;
    private Double onPeakReadingKW = null; // ##0.000
    private String onPeakTimeKW = null; // HH:mm
    private String onPeakDateKW = null; // MM/dd/yyyy

    private Double offPeakReadingKWh = null;
    private Double offPeakReadingKW = null; // ##0.000
    private String offPeakTimeKW = null; // HH:mm
    private String offPeakDateKW = null; // MM/dd/yyyy

    protected static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    protected static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    protected static DecimalFormat FORMAT_NODECIMAL = new DecimalFormat("#####");
    protected static DecimalFormat KW_FORMAT = new DecimalFormat("##0.000");

    public SimpleTOURecord() {
        super();
    }

    public SimpleTOURecord(String newMeterNumber) {
        super();
        setMeterNumber(newMeterNumber);
    }

    /**
     * Converts data in a SimpleTOU format to a formatted StringBuffer for
     * stream use.
     * @return String
     */
    public String dataToString() {
        StringBuffer writeToFile = new StringBuffer();

        addToStringBuffer(writeToFile, getMeterNumber(), true);

        // KWH
        addToStringBuffer(writeToFile, formatNumber(getTotalConsumption(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, getTimeTotal(), true);

        addToStringBuffer(writeToFile, getDateTotal(), true);

        // KW
        addToStringBuffer(writeToFile, formatNumber(getReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getTimeKW(), true);

        addToStringBuffer(writeToFile, getDateKW(), true);

        // On Peak
        addToStringBuffer(writeToFile, formatNumber(getOnPeakReadingKWh(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, formatNumber(getOnPeakReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getOnPeakTimeKW(), true);

        addToStringBuffer(writeToFile, getOnPeakDateKW(), true);

        // Off Peak
        addToStringBuffer(writeToFile, formatNumber(getOffPeakReadingKWh(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, formatNumber(getOffPeakReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getOffPeakTimeKW(), true);

        addToStringBuffer(writeToFile, getOffPeakDateKW(), false);

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }

    /*
     * Helper method to add an Object followed by a comma or just a comma if the
     * object is null to a string buffer
     */
    protected void addToStringBuffer(StringBuffer buffer, Object object, boolean addComma) {

        if (object != null) {
            buffer.append(object);
        }
        if (addComma) {
            buffer.append(",");
        }
    }

    /*
     * Helper method to format a number. Returns String representation of
     * formatted number or null if number was null.
     */
    protected String formatNumber(Double value, DecimalFormat format) {

        if (value == null) {
            return null;
        }

        return format.format(value);
    }

    public boolean equals(Object o) {
        return ((o != null) && (o instanceof SimpleTOURecord) && ((SimpleTOURecord) o).getMeterNumber()
                                                                                      .equalsIgnoreCase(getMeterNumber()));
    }

    public String getDateKW() {
        return dateKW;
    }

    public void setDateKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.dateKW = DATE_FORMAT.format(d);
    }

    public String getDateTotal() {
        return dateTotal;
    }

    public void setDateTotal(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.dateTotal = DATE_FORMAT.format(d);
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getOffPeakDateKW() {
        return offPeakDateKW;
    }

    public void setOffPeakDateKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.offPeakDateKW = DATE_FORMAT.format(d);
    }

    public Double getOffPeakReadingKW() {
        return offPeakReadingKW;
    }

    public void setOffPeakReadingKW(double offPeakReadingKW) {
        this.offPeakReadingKW = new Double(offPeakReadingKW);
    }

    public Double getOffPeakReadingKWh() {
        return offPeakReadingKWh;
    }

    public void setOffPeakReadingKWh(double offPeakReadingKWh) {
        this.offPeakReadingKWh = new Double(offPeakReadingKWh);
    }

    public String getOffPeakTimeKW() {
        return offPeakTimeKW;
    }

    public void setOffPeakTimeKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.offPeakTimeKW = TIME_FORMAT.format(d);
    }

    public String getOnPeakDateKW() {
        return onPeakDateKW;
    }

    public void setOnPeakDateKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.onPeakDateKW = DATE_FORMAT.format(d);
    }

    public Double getOnPeakReadingKW() {
        return onPeakReadingKW;
    }

    public void setOnPeakReadingKW(double onPeakReadingKW) {
        this.onPeakReadingKW = new Double(onPeakReadingKW);
    }

    public Double getOnPeakReadingKWh() {
        return onPeakReadingKWh;
    }

    public void setOnPeakReadingKWh(double onPeakReadingKWh) {
        this.onPeakReadingKWh = new Double(onPeakReadingKWh);
    }

    public String getOnPeakTimeKW() {
        return onPeakTimeKW;
    }

    public void setOnPeakTimeKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.onPeakTimeKW = TIME_FORMAT.format(d);
    }

    public Double getReadingKW() {
        return readingKW;
    }

    public void setReadingKW(double readingKW) {
        this.readingKW = new Double(readingKW);
    }

    public Double getTotalConsumption() {
        return totalConsumption;
    }

    public void setTotalConsumption(double readingKWh) {
        this.totalConsumption = new Double(readingKWh);
    }

    public String getTimeKW() {
        return timeKW;
    }

    public void setTimeKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.timeKW = TIME_FORMAT.format(d);
    }

    public String getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.timeTotal = TIME_FORMAT.format(d);
    }

}
