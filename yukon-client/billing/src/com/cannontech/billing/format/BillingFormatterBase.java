package com.cannontech.billing.format;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.mainprograms.BillingFileDefaults;

public abstract class BillingFormatterBase implements BillingFormatter {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public static final DecimalFormat FORMAT_NODECIMAL = new DecimalFormat("#####");
    public static final DecimalFormat DECIMAL_FORMAT_3v2 = new DecimalFormat("##0.00");
    public static final DecimalFormat DECIMAL_FORMAT4V3 = new DecimalFormat("0000.000");
    public static final DecimalFormat DECIMAL_FORMAT5V0 = new DecimalFormat("00000");
    public static final DecimalFormat DECIMAL_FORMAT6V3 = new DecimalFormat("000000.000");
    public static final DecimalFormat DECIMAL_FORMAT_7V2 = new DecimalFormat("#######.00");
    public static final DecimalFormat DECIMAL_FORMAT7V0 = new DecimalFormat("0000000");
    public static final DecimalFormat DECIMAL_FORMAT_8V3 = new DecimalFormat("########.000");
    public static final DecimalFormat DECIMAL_FORMAT9V0 = new DecimalFormat("000000000");
    public static final DecimalFormat DECIMAL_FORMAT_10V2 = new DecimalFormat("##########.00");

    public static final DecimalFormat KW_FORMAT = new DecimalFormat("##0.000");

    private int readingCount = 0;
    private BillingFileDefaults billingFileDefaults = null;
    abstract public String dataToString(BillableDevice device);

    public int writeBillingFile( List<BillableDevice> deviceList)
            throws IOException {

        StringBuffer output = getBillingFileString(deviceList);

        FileWriter outputFileWriter = new FileWriter(getBillingFileDefaults().getOutputFileDir(),
                                                     getBillingFileDefaults().isAppendToFile());
        outputFileWriter.write(output.toString());
        outputFileWriter.flush();
        outputFileWriter.close();

        return this.readingCount;

    }

    /**
     * Method to generate a String which is the entire billing file data for the
     * list of devices passed in
     * @param deviceList - List of devices to create billing file for
     * @return String representation of the entire billing file data
     */
    public String getBillingFileDetailsString(List<BillableDevice> deviceList) {

        StringBuffer billingFileString = new StringBuffer();
        Iterator<BillableDevice> deviceListIter = deviceList.iterator();
        while (deviceListIter.hasNext()) {
            BillableDevice device = deviceListIter.next();

            String deviceString = dataToString(device);
            if (deviceString != null && deviceString.length() > 0) {
                this.readingCount++;
            }
            billingFileString.append(deviceString);
        }

        return billingFileString.toString();

    }

    /**
     * Method to genereate a header string for the billing file. This method
     * should be implemented in formats which have a specific header.
     * @return String header
     */
    public String getBillingFileHeader() {
        return "";
    }

    /**
     * Method to genereate a footer string for the billing file. This method
     * should be implemented in formats which have a specific footer.
     * @return String footer
     */
    public String getBillingFileFooter() {
        return "";
    }

    /**
     * Method to generate the entire billing file string: including
     *  the header, details, and footer.
     * @param deviceList - List of devices to create billing file for  
     * @return StringBuffer billingData
     */
    public StringBuffer getBillingFileString(List<BillableDevice> deviceList) {
        StringBuffer billingData = new StringBuffer();
        
        // Add header to beginning of file string
        billingData.append(getBillingFileHeader());
    
        // Add all of the device data to the file string
        billingData.append(getBillingFileDetailsString(deviceList));
    
        // Add footer to end of file string
        billingData.append(getBillingFileFooter());
        
        return billingData;
    }
    
    /**
     * Method to add an object to a string buffer with or without a comma. Adds
     * an empty string if the object is null.
     * @param buffer - StringBuffer to add to
     * @param object - Object to add
     * @param addComma - True if a comma should be added after the object
     */
    protected void addToStringBuffer(StringBuffer buffer, Object object, boolean addComma) {

        addToStringBufferWithTrailingFiller(buffer, object, -1, null, addComma);
    }

    /**
     * Method to add an object to a string buffer. The object will be added with
     * trailing filler strings to equal the field length. A comma will be added
     * at the end if indicated
     * @param buffer - StringBuffer to add to
     * @param object - Object to add
     * @param fieldLength - Length the field should be (spaces are added after
     *            the object value if the object does not fill the field)
     * @param filler - String used to fill the spaces to reach the field length
     * @param addComma - True if a comma should be added after the object
     */
    protected void addToStringBufferWithTrailingFiller(StringBuffer buffer, Object object,
            int fieldLength, String filler, boolean addComma) {

        if (object != null) {
            buffer.append(object);
            fieldLength = fieldLength - object.toString().length();
        }

        if (fieldLength != -1) {
            for (int i = 0; i < fieldLength; i++) {
                buffer.append(filler);
            }
        }

        if (addComma) {
            buffer.append(",");
        }
    }

    /**
     * Method to add an object to a string buffer. The object will be added with
     * preceding filler strings to equal the field length. A comma will be added
     * at the end if indicated
     * @param buffer - StringBuffer to add to
     * @param object - Object to add
     * @param fieldLength - Length the field should be (spaces are added before
     *            the object value if the object does not fill the field)
     * @param filler - String used to fill the spaces to reach the field length
     * @param addComma - True if a comma should be added after the object
     */
    protected void addToStringBufferWithPrecedingFiller(StringBuffer buffer, Object object,
            int fieldLength, String filler, boolean addComma) {

        if (object != null) {
            fieldLength = fieldLength - object.toString().length();
        }

        if (fieldLength != -1) {
            for (int i = 0; i < fieldLength; i++) {
                buffer.append(filler);
            }
        }

        if (object != null) {
            buffer.append(object);
        }

        if (addComma) {
            buffer.append(",");
        }
    }

    /**
     * Method to format an object useing a given format
     * @param value - Object to be formatted
     * @param format - Format to use
     * @return String representation of the formatted object
     */
    protected String format(Object value, Format format) {
        /*
         * Helper method to format a number. Returns String representation of
         * formatted number or null if number was null.
         */

        if (value == null) {
            return null;
        }

        return format.format(value);
    }

	public BillingFileDefaults getBillingFileDefaults() {
		return billingFileDefaults;
	}

	public void setBillingFileDefaults(BillingFileDefaults billingFileDefaults) {
		this.billingFileDefaults = billingFileDefaults;
	}

    public int writeBillingFile( List<BillableDevice> deviceList, OutputStream out) throws IOException {
        StringBuffer output = getBillingFileString(deviceList);
        out.write(output.toString().getBytes());
        
        return this.readingCount;
    }
}
