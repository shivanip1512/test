package com.cannontech.analysis.tablemodel;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.database.db.point.SystemLog;

/**
 * Created on Dec 15, 2003
 * LMControlLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  Integer pointID 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class LMControlLogModel extends SystemLogModel
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 4;
	
	/** A string for the title of the data */
	private static String title = "LOAD MANAGEMENT CONTROL LOG";

	// MUST OVERRIDE THE COLUMM INT REFERENCES FOR GETATTRIBUTES TO WORK
	protected final static int DATE_COLUMN = 0;
	protected final static int DATE_TIME_COLUMN = 1;
	protected final static int ACTION_COLUMN = 2;
	protected final static int DESCRIPTION_COLUMN = 3;	

	/** String values for column representation */
	// THESE ARE OVERRIDES OF THE EXTENDED CLASS SYSTEMLOGDATA
	protected final static String DATE_STRING = "Date";
	protected final static String DATE_TIME_STRING = "Date/Time";
	protected final static String ACTION_STRING = "Action";
	protected final static String DESCRIPTION_STRING = "Description";
	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 */
//	public LMControlLogModel(Date start_, Date stop_)
//	{
//		super(start_, stop_, null, null);
//	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public LMControlLogModel(Date start_, Date stop_)
	{
		this(start_, stop_, null);
	}
	/**
	 * Constructor class
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public LMControlLogModel()
	{
		super();
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param pointID_ SYSTEM.pointID
	 * @param logType_ SYSTEMLOG.type
	 */
	public LMControlLogModel(Date start_, Date stop_, Integer pointID_)
	{
		super(start_, stop_, new Integer(SystemLog.TYPE_LOADMANAGEMENT), pointID_);
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof SystemLog)
		{
			SystemLog sl = ((SystemLog)o);
			switch( columnIndex)
			{
				case DATE_COLUMN:
				{
					GregorianCalendar tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(sl.getDateTime().getTime());
					return getBeginingOfDay(tempCal).getTime();
				}
				case DATE_TIME_COLUMN:
					return sl.getDateTime();
				case ACTION_COLUMN:
					return sl.getAction();					
				case DESCRIPTION_COLUMN:
					return sl.getDescription();
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				DATE_STRING,
				DATE_TIME_STRING,
				ACTION_STRING,
				DESCRIPTION_STRING
			};
		}
		return columnNames;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				java.util.Date.class,
				java.util.Date.class,
				String.class,
				String.class
			};
		}
		return columnTypes;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 100, "dd MMMMM yyyy"),
				new ColumnProperties(0, 1, 130, "dd MMM yyyy  HH:mm:ss"),
				new ColumnProperties(130, 1, 220, null),
				new ColumnProperties(350, 1, 300, null)
			};
		}
		return columnProperties;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
	
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0'cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Sort By Timestamp</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllSortOrders().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" +ATT_SORT_ORDER + "' value='" + getAllSortOrders()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getSortOrderString(getAllSortOrders()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	
	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
	}	
}
