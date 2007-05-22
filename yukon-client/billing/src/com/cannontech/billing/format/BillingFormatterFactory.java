package com.cannontech.billing.format;

import com.cannontech.billing.FileFormatTypes;

/**
 * Factory class to generate billing formatters
 */
public final class BillingFormatterFactory {

    /**
     * Method to get a new billing formatter whose type is based on the file
     * format type passed in
     * @param type - file format type of formatter needed
     * @return A new instance of the billing formatter
     */
    public final static BillingFormatter createFileFormat(int type) {
        switch (type) {
        case FileFormatTypes.SEDC:
            return new SEDCRecordFormatter();

        case FileFormatTypes.ATS:
            return new ATSRecordFormatter();

        case FileFormatTypes.CADP:
            return new CADPRecordFormatter();

        case FileFormatTypes.CADPXL2:
            return new CADPXL2RecordFormatter();

        case FileFormatTypes.NCDC:
            return new NCDCRecordFormatter();

        case FileFormatTypes.CTICSV:
            return new CTICSVRecordFormatter();

        case FileFormatTypes.OPU:
            return new OPURecordFormatter();

        case FileFormatTypes.DAFFRON:
            return new DAFFRONRecordFormatter();

        case FileFormatTypes.SEDC_5_4:
            return new SEDC54RecordFormatter();

        case FileFormatTypes.NISC_TURTLE:
            return new NISCRecordFormatter();

        case FileFormatTypes.NISC_TURTLE_NO_LIMIT_KWH:
            return new NISC_NoLimt_kWh_RecordFormatter();

        case FileFormatTypes.NISC_NCDC:
            return new NISC_NCDCRecordFormatter();

        case FileFormatTypes.CTIStandard2:
            return new CTIStandard2RecordFormatter();

        case FileFormatTypes.NISC_TOU_KVARH:
            return new NISC_TOU_kVarHRecordFormatter();

        case FileFormatTypes.SEDC_yyyyMMdd:
            return new SEDC_yyyyMMddRecordFormatter();

        case FileFormatTypes.IVUE_BI_T65:
            return new IVUE_BI_T65RecordFormatter();

        case FileFormatTypes.SIMPLE_TOU:
            return new SimpleTOURecordFormatter();

        case FileFormatTypes.EXTENDED_TOU:
            return new ExtendedTOURecordFormatter();

        case FileFormatTypes.BIG_RIVERS_ELEC_COOP:
            return new BigRiversElecCoopFormatter();

        case FileFormatTypes.EXTENDED_TOU_INCODE:
        	return new ExtendedTOU_IncodeRecordFormatter();

        default:
            /*  return null if format not found - other formats will be handled
                by the FileFormatFactory:
                    case FileFormatTypes.WLT_40:
                    case FileFormatTypes.MV_90:
                    case FileFormatTypes.NCDC_HANDHELD:
                    case FileFormatTypes.MVRS:
            */
            return null;
        }
    }
}
