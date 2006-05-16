package com.cannontech.billing.record;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Billing record for simple TOU
 */
public class ExtendedTOURecord extends SimpleTOURecord {

    public static final String WATER_CODE = "W";
    public static final String GAS_CODE = "G";
    public static final String ELECTRIC_CODE = "E";

    private String readingCode = null;

    private Double rateCReadingKWh = null;
    private Double rateCReadingKW = null;
    private String rateCTimeKW = null; // HH:mm
    private String rateCDateKW = null; // MM/dd/yyyy

    private Double rateDReadingKWh = null;
    private Double rateDReadingKW = null;
    private String rateDTimeKW = null; // HH:mm
    private String rateDDateKW = null; // MM/dd/yyyy

    public ExtendedTOURecord() {
        super();
    }

    public ExtendedTOURecord(String newMeterNumber) {
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

        addToStringBuffer(writeToFile, getReadingCode(), true);

        // KWH
        addToStringBuffer(writeToFile, formatNumber(getTotalConsumption(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, getTimeTotal(), true);

        addToStringBuffer(writeToFile, getDateTotal(), true);

        // KW
        addToStringBuffer(writeToFile, formatNumber(getReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getTimeKW(), true);

        addToStringBuffer(writeToFile, getDateKW(), true);

        // Rate A
        addToStringBuffer(writeToFile, formatNumber(getOnPeakReadingKWh(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, formatNumber(getOnPeakReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getOnPeakTimeKW(), true);

        addToStringBuffer(writeToFile, getOnPeakDateKW(), true);

        // Rate B
        addToStringBuffer(writeToFile, formatNumber(getOffPeakReadingKWh(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, formatNumber(getOffPeakReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getOffPeakTimeKW(), true);

        addToStringBuffer(writeToFile, getOffPeakDateKW(), true);

        // Rate C
        addToStringBuffer(writeToFile, formatNumber(getRateCReadingKWh(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, formatNumber(getRateCReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getRateCTimeKW(), true);

        addToStringBuffer(writeToFile, getRateCDateKW(), true);

        // Rate D
        addToStringBuffer(writeToFile, formatNumber(getRateDReadingKWh(), FORMAT_NODECIMAL), true);

        addToStringBuffer(writeToFile, formatNumber(getRateDReadingKW(), KW_FORMAT), true);

        addToStringBuffer(writeToFile, getRateDTimeKW(), true);

        addToStringBuffer(writeToFile, getRateDDateKW(), false);

        writeToFile.append("\r\n");

        return writeToFile.toString();
    }

    public boolean equals(Object o) {
        return ((o != null) && (o instanceof ExtendedTOURecord) && ((ExtendedTOURecord) o).getMeterNumber()
                                                                                          .equalsIgnoreCase(getMeterNumber()));
    }

    public String getRateCDateKW() {
        return rateCDateKW;
    }

    public void setRateCDateKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.rateCDateKW = DATE_FORMAT.format(d);
    }

    public Double getRateCReadingKWh() {
        return rateCReadingKWh;
    }

    public void setRateCReadingKWh(double rateCKWh) {
        this.rateCReadingKWh = new Double(rateCKWh);
    }

    public Double getRateCReadingKW() {
        return rateCReadingKW;
    }

    public void setRateCReadingKW(double rateCReadingKW) {
        this.rateCReadingKW = new Double(rateCReadingKW);
    }

    public String getRateCTimeKW() {
        return rateCTimeKW;
    }

    public void setRateCTimeKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.rateCTimeKW = TIME_FORMAT.format(d);
    }

    public String getRateDDateKW() {
        return rateDDateKW;
    }

    public void setRateDDateKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.rateDDateKW = DATE_FORMAT.format(d);
    }

    public Double getRateDReadingKWh() {
        return rateDReadingKWh;
    }

    public void setRateDReadingKWh(double rateDKWh) {
        this.rateDReadingKWh = new Double(rateDKWh);
    }

    public Double getRateDReadingKW() {
        return rateDReadingKW;
    }

    public void setRateDReadingKW(double rateDReadingKW) {
        this.rateDReadingKW = new Double(rateDReadingKW);
    }

    public String getRateDTimeKW() {
        return rateDTimeKW;
    }

    public void setRateDTimeKW(Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        this.rateDTimeKW = TIME_FORMAT.format(d);
    }

    public String getReadingCode() {
        return readingCode;
    }

    public void setReadingCode(String readingCode) {
        this.readingCode = readingCode;
    }

}
