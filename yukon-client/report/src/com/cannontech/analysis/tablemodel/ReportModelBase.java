package com.cannontech.analysis.tablemodel;

import java.util.Vector;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;


/**
 * Created on Dec 15, 2003
 *
 * Base Class for Report data models.  Implements the required AbstractTableModel
 * for use with the JFreeReport API.
 
 * Extending classes must initialize these ReportModelBase fields:
 *   String[] columnNames - table column names
 *   Class[] columnTypes  - table column class type.
 *   ColumnProperties[] columnProperties - properties for column display in the report.
 *     (See com.cannontech.analysis.data.ColumnProperties)
 
 *   Vector data - contains data object o for table rows
 *     (o is normally defined in the extending class as an inner class object model).    
 * 
 * @author snebben
 */
public abstract class ReportModelBase extends javax.swing.table.AbstractTableModel implements Reportable
{
	/** Array of String values representing the column names. */
	protected String[] columnNames;
	
	/** Array of Class values representing the column object classes. */
	protected Class[] columnTypes;
	
	/** Array of ColumnProperties values representing the columns. */
	protected ColumnProperties[] columnProperties;
	
	/** String for the title for the table model */
	protected String title;
	
	/** Yukon.paobjectid's to query for, null means all */
	private int[] paoIDs = null;
	
	/** Yukon.DeviceMeterGroup's to query for, null means all */
	private String [] collectionGroups = null;

	/** Vector of data (of inner class type from implementors)*/
	protected java.util.Vector data = new java.util.Vector(100);

	/** The report type, valid types are in com.cannontech.analysis.data.ReportTypes*/
	protected int reportType;
	
	/** Start time for query in millis */
	private long startTime = Long.MIN_VALUE;
	
	/** Stop time for query in millis */
	private long stopTime = Long.MIN_VALUE;	
	
	/** Class fields */
	private int[] ecIDs = null;
	
	/**
	 * Default Constructor
	 */
	public ReportModelBase()
	{
		super();
	}	

	/**
	 * Default Constructor
	 */
	public ReportModelBase(int reportType_, long startTime_, long stopTime_)
	{
		this();
		setReportType(reportType_);
		setStartTime(startTime_);
		setStopTime(stopTime_);
	}	
	/**
	 * Implement this method to add/populate row items to the data Vector.
	 */
	public abstract void collectData();
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return getColumnNames().length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if( rowIndex < getData().size() && columnIndex < getColumnCount() )
		{
			return getAttribute(columnIndex, getData().get(rowIndex) );
		}
		return null;
	}

	/**
	 * Return the Vector of data objects
	 * @return Vector data
	 */
	public java.util.Vector getData()
	{
		if (data == null)
		{
			data = new Vector();
		}
		
		return data;
	}
	
	/* (non-Javadoc)
	* @see javax.swing.table.TableModel#getRowCount()
	*/
	public int getRowCount()
	{
		return getData().size();
	}

	/**
	 * Return the date information for the data.
	 * OVERRIDE FOR ALL EXTENDERS USING START/STOP DATE INTERVALS.
	 * @return String formated date string.
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return (format.format(new java.util.Date(getStartTime())) + " through " +
				   (format.format(new java.util.Date(getStopTime()))));
	}	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(final int column)
	{
		return getColumnNames()[column];
	} 
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int column)
	{
		return getColumnTypes()[column];
	}

	/**
	 * Returns the ColumnProperties for column
	 * @return ColumnProperties for column
	 */
	public ColumnProperties getColumnProperties(int column)
	{
		return getColumnProperties()[column];
	}

	/**
	 * Set the columnProperties
	 * @param ColumnProperties[] properties
	 */
	public void setColumnProperties(ColumnProperties[] properties_)
	{
		columnProperties = properties_;
	}
	/**
	 * @return
	 */
	public int[] getPaoIDs()
	{
		return paoIDs;
	}

	/**
	 * @param is
	 */
	public void setPaoIDs(int[] is)
	{
		paoIDs = is;
	}

	/**
	 * @return
	 */
	public int getReportType()
	{
		return reportType;
	}

	/**
	 * @param i
	 */
	public void setReportType(int i)
	{
		reportType = i;
	}
	
	/**
	 * Returns the startTime in millis
	 * @return long startTime
	 */
	public long getStartTime()
	{
		if( startTime < 0 )
		{
			java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
			tempCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			tempCal.set(java.util.Calendar.MINUTE, 0);
			tempCal.set(java.util.Calendar.SECOND, 0);
			tempCal.set(java.util.Calendar.MILLISECOND, 0);
			tempCal.add(java.util.Calendar.DATE, -1);
			startTime = tempCal.getTime().getTime();				
		}
		return startTime;
	}

	/**
	 * Returns the stopTime in millis
	 * @return long stopTime
	 */
	public long getStopTime()
	{
		if( stopTime < 0 )
		{
			java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
			tempCal.setTimeInMillis(getStartTime());
			tempCal.add(java.util.Calendar.DATE, 1);
			stopTime = tempCal.getTime().getTime();				
		}
		return stopTime;
	}
	/**
	 * Set the startTime in millis
	 * @param long time
	 */
	public void setStartTime(long time)
	{
		startTime = time;
	}
	/**
	 * Set the stopTime in millis
	 * @param long time
	 */
	public void setStopTime(long time)
	{
		stopTime = time;
	}

	/**
	 * @return
	 */
	public String[] getCollectionGroups()
	{
		return collectionGroups;
	}

	/**
	 * @param strings
	 */
	public void setCollectionGroups(String[] strings)
	{
		collectionGroups = strings;
	}

	/**
	 * @param vector
	 */
	public void setData(java.util.Vector vector)
	{
		data = vector;
	}
	/**
	 * @param i
	 */
	public void setECIDs(Integer ecID)
	{
		setECIDs(new int[]{ecID.intValue()});
	}
	/**
	 * @return
	 */
	public int[] getECIDs()
	{
		return ecIDs;
	}

	/**
	 * @param is
	 */
	public void setECIDs(int[] is)
	{
		ecIDs = is;
	}
	
}
