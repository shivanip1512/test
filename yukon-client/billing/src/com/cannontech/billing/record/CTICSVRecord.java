package com.cannontech.billing.record;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Billing record for CTICSV format
 */
public class CTICSVRecord implements BillingRecordBase {
    private String name; // 20
    private Double reading; // 12
    private String measureLabel; // 6
    private String status; // 1

    private String date;
    private String time;

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yy");
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static DecimalFormat DECIMAL_FORMAT_10V2 = new DecimalFormat("##########.00");

    /**
     * CTICSV constructor comment.
     */
    public CTICSVRecord() {
        super();
    }

    /**
     * CTICSV constructor comment.
     */
    public CTICSVRecord(String nameString, double readingValue, String label, Timestamp timeStamp) {
        super();
        setName(nameString);
        setReading(readingValue);
        setMeasureLabel(label);
        setDate(timeStamp);
        setTime(timeStamp);
        // status defaults to N
    }

    /**
     * CTICSV constructor comment.
     */
    public CTICSVRecord(String nameString, double readingValue, String label, Timestamp timeStamp,
            String readingStatus) {
        super();
        setName(nameString);
        setReading(readingValue);
        setMeasureLabel(measureLabel);
        setDate(timeStamp);
        setTime(timeStamp);
        setStatus(readingStatus);
    }

    /**
     * CTICSV constructor comment.
     */
    public CTICSVRecord(String nameString, double readingValue, Timestamp timeStamp) {
        super();
        setName(nameString);
        setReading(readingValue);
        setDate(timeStamp);
        setTime(timeStamp);

        // measureLabel defaults to KWH
        // status defaults to N
    }

    /**
     * Method to turn the record into a string to be used in the billing output
     * file
     * @return String representation of the record
     *         //nnnnnnnnnnnnnnnnnnnn,rrrrrrrrrr.rr,llllll,mm-dd-yy,hh:mm,s //n -
     *         20 name filed //r - 10.2 reading //l - 6 measure label //s - 1
     *         status
     */
    public String dataToString() {
        StringBuffer writeToFile = new StringBuffer();

        writeToFile.append(getName());
        for (int i = name.length(); i < 20; i++) {
            writeToFile.append(" ");
        }
        writeToFile.append(",");

        for (int i = 0; i < (13 - DECIMAL_FORMAT_10V2.format(getReading()).length()); i++) {
            // technically this field is 13 places (if you include the decimal)
            writeToFile.append(" ");
        }
        writeToFile.append(DECIMAL_FORMAT_10V2.format(getReading()) + ",");
        writeToFile.append(getMeasureLabel());
        for (int i = getMeasureLabel().length(); i < 6; i++) {
            writeToFile.append(" ");
        }
        writeToFile.append(",");

        writeToFile.append(getDate() + ",");
        writeToFile.append(getTime() + ",");
        writeToFile.append(getStatus() + "\r\n");

        return writeToFile.toString();
    }

    /**
     * Getter for date member
     * @return String date (mm-dd-yy)
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter for label member
     * @return String label
     */
    public String getMeasureLabel() {
        if (measureLabel == null)
            return "kWh";
        return measureLabel;
    }

    /**
     * Getter for name member
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for reading member
     * @return double reading
     */
    public Double getReading() {
        return reading;
    }

    /**
     * Getter for status member
     * @return String status
     */
    public String getStatus() {
        if (status == null)
            return "N";

        return status;
    }

    /**
     * Getter for time
     * @return String time (HH:mm)
     */
    public String getTime() {
        return time;
    }

    /**
     * Setter for date
     * @param timeStamp
     */
    public void setDate(Timestamp timeStamp) {
        Date d = new Date(timeStamp.getTime());
        date = DATE_FORMAT.format(d);
    }

    /**
     * Setter for label
     * @param newLabel String
     */
    public void setMeasureLabel(String newLabel) {
        if (newLabel.length() > 6)
            measureLabel = newLabel.substring(0, 5);
        else
            measureLabel = newLabel;
    }

    /**
     * Setter for naem
     * @param newName String
     */
    public void setName(String newName) {
        if (newName.length() > 20)
            name = newName.substring(0, 19);
        else
            name = newName;
    }

    /**
     * Setter for reading
     * @param newReading
     */
    public void setReading(double newReading) {
        reading = new Double(newReading);

    }

    /**
     * Setter for status
     * @param newStatus
     */
    public void setStatus(String newStatus) {
        status = newStatus;
    }

    /**
     * Setter for time
     * @param newTime
     */
    public void setTime(java.sql.Timestamp timestamp) {
        Date d = new Date(timestamp.getTime());
        time = TIME_FORMAT.format(d);
    }
}
