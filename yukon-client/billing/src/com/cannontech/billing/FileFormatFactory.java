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
	FileFormatBase retFFB = null;
	switch(type)
	{
		case FileFormatTypes.MVRS:
			retFFB =  new MVRSFormat();
			break;
		
		case FileFormatTypes.SEDC:
			retFFB =  new SEDCFormat();
			break;

		case FileFormatTypes.CADP:
			retFFB =  new CADPFormat();
			break;

		case FileFormatTypes.CADPXL2:
			retFFB = new CADPXL2Format();
			break;

		case FileFormatTypes.NCDC:
			retFFB = new NCDCFormat();
			break;

		case FileFormatTypes.CTIStandard2:
			retFFB = new CTIStandard2Format();
			break;

		case FileFormatTypes.CTICSV:
			retFFB = new CTICSVFormat();
			break;

		case FileFormatTypes.OPU:
			retFFB = new OPUFormat();
			break;

		case FileFormatTypes.WLT_40:
			retFFB = new WLT_40Format();
			break;
			
		case FileFormatTypes.DAFFRON:
			retFFB =  new DAFFRONFormat();
			break;
			
		default: //this is bad
			throw new Error("FileFormatFactory::createFileFormat - Unrecognized file format type");
	}
	return retFFB;
}
}
