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
		case FileFormatTypes.WLT_40:
			return new WLT_40Format();

        case FileFormatTypes.MV_90:
			return new MV_90Format();

		case FileFormatTypes.NCDC_HANDHELD:
			return new NCDC_HandheldFormat();
			
		case FileFormatTypes.MVRS:
			return new MVRSFormat();

        default: //this is bad
			throw new Error("FileFormatFactory::createFileFormat - Unrecognized file format type");
	}
}
}
