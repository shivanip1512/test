package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (6/7/2002 2:40:14 PM)
 * @author: 
 */
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public interface GraphDefines 
{
	public String TITLE_HEADER_BGCOLOR = "#FFFFFF";
	public String HEADER_CELL_BGCOLOR = "#999966";
	public String TABLE_CELL_BGCOLOR = "#CCCC99";

	public static final String HELP_FILE = com.cannontech.common.util.CtiUtilities.getHelpDirPath() + "Yukon Trending Help.chm";
	
	//old GraphDataFormats interface removed and fields moved into here.
	public static SimpleDateFormat timestampQueryFormat = new SimpleDateFormat("dd-MMM-yyyy");
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	public static SimpleDateFormat extendedDateFormat = new SimpleDateFormat( "MMM d, yyyy");//old datePopupFormat

	public static SimpleDateFormat multipleDaystimedateFormat = new SimpleDateFormat("MM/dd HH:mm");
	public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat extendedTimeFormat = new SimpleDateFormat("HH:mm:ss");
	
		
	public static SimpleDateFormat dateTimeformat = new SimpleDateFormat("M/dd/yyyy  HH:mm:ss");
	public static SimpleDateFormat extendedDateTimeformat = new SimpleDateFormat("EEE MMM d HH:mm:ss a z yyyy");		

	public static SimpleDateFormat dwellValuesDateTimeformat = new SimpleDateFormat("MMM d, HH:mm:ss");

	public static DecimalFormat valueFormat = new DecimalFormat();
	public static DecimalFormat percentFormat = new DecimalFormat(" ##0.0% ");
	
	public static Character [] axisChars = new Character[]{new Character('L'), new Character('R')};
	
	public static java.text.SimpleDateFormat LEGEND_FORMAT = new java.text.SimpleDateFormat("MMM dd");
	public static java.text.SimpleDateFormat CATEGORY_FORMAT = new java.text.SimpleDateFormat(" MMM dd, HH:mm ");

	public static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
	public static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
	
}
