package com.cannontech.billing;

import java.sql.Timestamp;
import java.util.Vector;

import com.cannontech.billing.record.CADPXL2Record;
import com.cannontech.billing.record.IVUE_BI_T65Record;

/**
 * Format for iVue T65
 */
public class IVUE_BI_T65Format extends CADPXL2Format {

    public IVUE_BI_T65Format() {
        super();
    }

    /**
     * Helper method to create a new CADPXL2Record
     * @param accountNumber
     * @param meterNumber
     * @param meterPositionNumber
     * @param ts
     * @param registerNumberVector
     * @param kwhValueVector
     * @param kwValueVector
     * @param kvarValueVector
     * @return new CADPXL2Record
     */
    public CADPXL2Record createRecord(String accountNumber, String meterNumber,
            Integer meterPositionNumber, Timestamp ts,
            Vector<Integer> registerNumberVector, Vector<Double> kwhValueVector,
            Vector<Double> kwValueVector, Vector<Double> kvarValueVector) {

        return new IVUE_BI_T65Record(accountNumber,
                                     meterNumber,
                                     meterPositionNumber,
                                     ts,
                                     registerNumberVector,
                                     kwhValueVector,
                                     kwValueVector,
                                     kvarValueVector);
    }

    /**
     * Helper method to get a CADPXL2Record
     * @param recCount
     * @return CADPXL2Record
     */
    public CADPXL2Record getRecord(int recCount) {
        return (IVUE_BI_T65Record) getRecordVector().get(recCount - 1);
    }

}
