package com.cannontech.billing.record;

import java.sql.Timestamp;
import java.util.Vector;

/**
 * Billing record for iVUE T65
 */
public class IVUE_BI_T65Record extends CADPXL2Record {

    private String serviceLocationNumber = "";
    private String route = "";

    public IVUE_BI_T65Record() {
        super();
    }

    public IVUE_BI_T65Record(String newAccountNumber, String newMeterNumber,
            Integer newMeterPosition, Timestamp newTimeStamp, Vector newRegNumVector,
            Vector newKwhVector, Vector newKwVector, Vector newKvarVector) {

        super();

        setAccountNumber(newAccountNumber);
        setMeterNumber(newMeterNumber);
        setMeterPositionNumber(newMeterPosition);
        setReadDate(newTimeStamp);
        setRegisterNumberVector(newRegNumVector);
        setKwhReadingVector(newKwhVector);
        setKwReadingVector(newKwVector);
        setKvarReadingVector(newKvarVector);
    }

    public IVUE_BI_T65Record(String accountNumber, String serviceLocationNumber, String importType,
            String serviceGroup, String paymentSign, Double payment, Timestamp batchDate,
            String batchNumber, Timestamp readDate, String whoReadMeter, String meterNumber,
            Integer meterPositionNumber, String route, Vector registerNumberVector,
            Vector kwhReadingVector, Vector kwReadingVector, Vector kvarReadingVector) {

        super();

        setAccountNumber(accountNumber);
        setServiceLocationNumber(serviceLocationNumber);
        setImportType(importType);
        setServiceGroup(serviceGroup);
        setPaymentSign(paymentSign);
        setPayment(payment);
        setBatchDate(batchDate);
        setBatchNumber(batchNumber);
        setReadDate(readDate);
        setWhoReadMeter(whoReadMeter);
        setMeterNumber(meterNumber);
        setMeterPositionNumber(meterPositionNumber);
        setRoute(route);
        setRegisterNumberVector(registerNumberVector);
        setKwhReadingVector(kwhReadingVector);
        setKwReadingVector(kwReadingVector);
        setKvarReadingVector(kvarReadingVector);
    }

    /**
     * Method to construct the String representation of the record
     * @return String representation of the record
     */
    public String dataToString() {
        int pass = 0;

        StringBuffer writeToFile = new StringBuffer();

        String tempAcctNumber = getAccountNumber();
        if (tempAcctNumber.length() > 10)
            tempAcctNumber = tempAcctNumber.substring(0, 10); // only keep 10
        // characters
        for (int i = 0; i < (10 - tempAcctNumber.length()); i++) {
            writeToFile.append("0"); // add 0s to end of string if less than
            // 10 chars
        }
        writeToFile.append(tempAcctNumber);

        writeToFile.append(getServiceLocationNumber());
        for (int i = 0; i < (10 - getServiceLocationNumber().length()); i++) {
            writeToFile.append(" "); 
        }

        writeToFile.append(getImportType());

        writeToFile.append(getServiceGroup());
        for (int i = 0; i < (5 - getServiceGroup().length()); i++) {
            writeToFile.append(" "); // add blanks to end of string if less
            // than 5 chars
        }

        writeToFile.append(getPaymentSign());

        String tempPaymentString = decimalFormat7v2.format(getPayment().doubleValue());
        for (int i = 0; i < tempPaymentString.length(); i++) {
            if (tempPaymentString.charAt(i) != '.') {
                writeToFile.append(tempPaymentString.charAt(i));
            }
        }

        writeToFile.append(getBatchDate());

        writeToFile.append(getBatchNumber());

        writeToFile.append(getReadDate());

        writeToFile.append(getWhoReadMeter());

        writeToFile.append(getMeterNumber());
        for (int i = 0; i < (15 - getMeterNumber().length()); i++) {
            writeToFile.append(" ");
        }

        for (int i = 0; i < (2 - getMeterPositionNumber().toString().length()); i++) {
            writeToFile.append("0");
        }
        writeToFile.append(getMeterPositionNumber());

        writeToFile.append(getRoute());
        for (int i = 0; i < (5 - getRoute().length()); i++) {
            writeToFile.append(" "); 
        }

        if (getRegisterNumberVector().size() > 0) {
            for (int i = 0; i < getRegisterNumberVector().size(); i++) {
                for (int j = 0; j < (2 - ((Integer) getRegisterNumberVector().get(i)).toString()
                                                                                     .length()); j++) {
                    writeToFile.append("0");
                }
                writeToFile.append(getRegisterNumberVector().get(i).toString());

                if (getKwhReadingVector().size() > i) {
                    pass++;
                    String tempKwhReadingString = decimalFormat9v0.format(((Double) getKwhReadingVector().get(i)).doubleValue());
                    for (int j = 0; j < tempKwhReadingString.length(); j++) {
                        if (tempKwhReadingString.charAt(j) != '.') {
                            writeToFile.append(tempKwhReadingString.charAt(j));
                        }
                    }
                } else {
                    for (int j = 0; j < 9; j++) {
                        writeToFile.append("0");
                    }
                }

                if (getKwReadingVector().size() > i) {
                    pass++;
                    String tempKWReadingString = decimalFormat6v3.format(((Double) getKwReadingVector().get(i)).doubleValue());
                    for (int j = 0; j < tempKWReadingString.length(); j++) {
                        if (tempKWReadingString.charAt(j) != '.') {
                            writeToFile.append(tempKWReadingString.charAt(j));
                        }
                    }
                } else {
                    for (int j = 0; j < 9; j++) {
                        writeToFile.append("0");
                    }
                }

                if (getKvarReadingVector().size() > i) {
                    pass++;

                    String tempKvarReadingString = decimalFormat7v2.format(((Double) getKvarReadingVector().get(i)).doubleValue());
                    for (int j = 0; j < tempKvarReadingString.length(); j++) {
                        if (tempKvarReadingString.charAt(j) != '.') {
                            writeToFile.append(tempKvarReadingString.charAt(j));
                        }
                    }
                } else {
                    for (int j = 0; j < 9; j++) {
                        writeToFile.append("0");
                    }
                }
            }
        } else {}

        writeToFile.append("\r\n");
        if (pass > 0)
            return writeToFile.toString();
        else
            return null;
    }

    /**
     * Getter method for service location number
     * @return Service location number
     */
    public java.lang.String getServiceLocationNumber() {
        return serviceLocationNumber;
    }

    /**
     * Getter method for route
     * @return Route
     */
    public java.lang.String getRoute() {
        return route;
    }

    /**
     * Setter method for service location number
     * @param newServiceLocationNumber
     */
    public void setServiceLocationNumber(String newServiceLocationNumber) {
        serviceLocationNumber = newServiceLocationNumber;
    }

    /**
     * Setter method for route
     * @param newRoute
     */
    public void setRoute(String newRoute) {
        route = newRoute;
    }
}
