package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:08:41 PM)
 * @author: 
 */
public final class FileFormatFactory
{
/**
 * Insert the method's description here.
 * Creation date: (5/18/00 3:50:38 PM)
 */
public final static FileFormatBase createFileFormat(int type)
{
	switch(type)
	{
		case FileFormatTypes.SEDC:
			return new SEDCFormat();
			
		case FileFormatTypes.ATS:
			return new ATSFormat();		
		case FileFormatTypes.CADP:
			return new CADPFormat();

		case FileFormatTypes.CADPXL2:
			return new CADPXL2Format();

		case FileFormatTypes.NCDC:
			return new NCDCFormat();

		case FileFormatTypes.CTICSV:
			return new CTICSVFormat();

		case FileFormatTypes.OPU:
			return new OPUFormat();

		case FileFormatTypes.WLT_40:
			return new WLT_40Format();
			
		case FileFormatTypes.DAFFRON:
			return new DAFFRONFormat();

		case FileFormatTypes.MV_90:
			return new MV_90Format();

		case FileFormatTypes.SEDC_5_4:
			return new SEDC54Format();

		case FileFormatTypes.NISC_TURTLE:
			return new NISCFormat();
			
		case FileFormatTypes.NISC_TURTLE_NO_LIMIT_KWH:
			return new NISC_NoLimt_kWh_Format();

		case FileFormatTypes.NISC_NCDC:
			return new NISC_NCDCFormat();

		case FileFormatTypes.NCDC_HANDHELD:
			return new NCDC_HandheldFormat();
			
		case FileFormatTypes.CTIStandard2:
			return new CTIStandard2Format();

		case FileFormatTypes.MVRS:
			return new MVRSFormat();
/*
		case FileFormatTypes.CTIStandard2:
			return new CTIStandard2Format();
*/
	    case FileFormatTypes.NISC_TOU_KVARH:
	        return new NISC_TOU_kVarHFormat();
	    
	    case FileFormatTypes.SEDC_yyyyMMdd:
	        return new SEDCFormat_yyyyMMdd();
            
        case FileFormatTypes.IVUE_BI_T65:
            return new IVUE_BI_T65Format();
        
        case FileFormatTypes.SIMPLE_TOU:
            return new SimpleTOUFormat();
        
        case FileFormatTypes.EXTENDED_TOU:
            return new ExtendedTOUFormat();
		
		default: //this is bad
			throw new Error("FileFormatFactory::createFileFormat - Unrecognized file format type");
	}
}
}
