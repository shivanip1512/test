package com.cannontech.billing;

import com.cannontech.billing.format.ATSRecordFormatter;
import com.cannontech.billing.format.BigRiversElecCoopFormatter;
import com.cannontech.billing.format.CADPRecordFormatter;
import com.cannontech.billing.format.CADPXL2RecordFormatter;
import com.cannontech.billing.format.CTICSVRecordFormatter;
import com.cannontech.billing.format.CTIStandard2RecordFormatter;
import com.cannontech.billing.format.DAFFRONRecordFormatter;
import com.cannontech.billing.format.ExtendedTOURecordFormatter;
import com.cannontech.billing.format.ExtendedTOU_IncodeRecordFormatter;
import com.cannontech.billing.format.IVUE_BI_T65RecordFormatter;
import com.cannontech.billing.format.NCDCRecordFormatter;
import com.cannontech.billing.format.NISCIntervalReadings;
import com.cannontech.billing.format.NISCRecordFormatter;
import com.cannontech.billing.format.NISC_NCDCRecordFormatter;
import com.cannontech.billing.format.NISC_NoLimt_kWh_RecordFormatter;
import com.cannontech.billing.format.NISC_TOU_kVarHRecordFormatter;
import com.cannontech.billing.format.NISC_TOU_kVarH_RatesOnlyRecordFormatter;
import com.cannontech.billing.format.OPURecordFormatter;
import com.cannontech.billing.format.SEDC54RecordFormatter;
import com.cannontech.billing.format.SEDCRecordFormatter;
import com.cannontech.billing.format.SEDC_yyyyMMddRecordFormatter;
import com.cannontech.billing.format.SimpleTOURecordFormatter;
import com.cannontech.billing.format.SimpleTOU_DeviceNameRecordFormatter;
import com.cannontech.billing.format.StandardRecordFormatter;
import com.cannontech.billing.format.dynamic.DynamicBillingFormatter;
import com.cannontech.spring.YukonSpringHook;

/**
 * Factory class to generate billing formatters
 */
public final class SimpleBillingFormatFactory {

    /**
     * Method to get a new billing formatter whose type is based on the file
     * format type passed in
     * @param type - file format type of formatter needed
     * @return A new instance of the billing formatter
     */
    public final static SimpleBillingFormat createFileFormat(int type) {
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

        case FileFormatTypes.ITRON_REGISTER_READINGS_EXPORT:
            return YukonSpringHook.getBean("itronClientHandler", SimpleBillingFormat.class);
            
        case FileFormatTypes.SIMPLE_TOU_DEVICE_NAME:
            return new SimpleTOU_DeviceNameRecordFormatter();

        case FileFormatTypes.STANDARD:
            return new StandardRecordFormatter();
            
        case FileFormatTypes.NISC_TOU_KVARH_RATES_ONLY:
            return new NISC_TOU_kVarH_RatesOnlyRecordFormatter();
        
        case FileFormatTypes.NISC_INTERVAL_READINGS:
            return new NISCIntervalReadings();
            
        case FileFormatTypes.WLT_40:
			return new WLT_40Format();

        case FileFormatTypes.MV_90:
			return new MV_90Format();

		case FileFormatTypes.NCDC_HANDHELD:
			return new NCDC_HandheldFormat();
			
		case FileFormatTypes.MVRS:
		case FileFormatTypes.MVRS_KETCHIKAN:
			return new MVRSFormat();
			
		case FileFormatTypes.CURTAILMENT_EVENTS_ITRON:
            return YukonSpringHook.getBean("curtailmentEventsItronFormat", SimpleBillingFormat.class);
        	
		case FileFormatTypes.CMEP:
		    return new CMEP_MEPMD01Format();
            
        default:
        	// Assume dynamic format
        	
            DynamicBillingFormatter formatter = 
            		(DynamicBillingFormatter) YukonSpringHook.getBean("dynamicBillingFormatter");
        	formatter.loadFormat(type);
        	
        	return formatter;
        }
    }
}
