package com.cannontech.analysis.tablemodel;

import java.util.Vector;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportTypes;
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
public abstract class ReportModelBase extends javax.swing.table.AbstractTableModel
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
	
	/** Class fields */
		/** Start time for query in millis */
	private long startTime = Long.MIN_VALUE;
	
	/** Stop time for query in millis */
	private long stopTime = Long.MIN_VALUE;	
	
	/**
	 * Default Constructor
	 */
	public ReportModelBase()
	{
		super();
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
	 * Implement this method to return the correct field of the o.
	 * @param columnIndex
	 * @param o object (inner class of most extenders)
	 * @return object value for the columnIndex and o class.
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		return ((Reportable)o).getAttribute(columnIndex, o);
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
	 * Return the title string for the report data.
	 * @return String 
	 */
	public String getTitleString()
	{
		if( title == null)
		{
			title = ReportTypes.getTitleString(getReportType());
		}
		return title;
	}	
	
	/**
	 * Return the date information for the data.
	 * OVERRIDE FOR ALL EXTENDERS USING START/STOP DATE INTERVALS.
	 * @return String formated date string.
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return format.format(new java.util.Date());
	}

	/**
	 * Return the columnNames array
	 * @return String[] columnNames
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = ReportTypes.getColumnNames(getReportType());
		}
		return columnNames;
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
	 * Return the columnTypes array
	 * @return Class[] columnTypes
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = ReportTypes.getColumnTypes(getReportType());
		}
		return columnTypes;
	}
	/**
	 * Return the columnProperties array
	 * @return ColumnProperties[] columnProperties
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if( columnProperties == null)
		{
			columnProperties = ReportTypes.getColumnProperties(getReportType());
		}
		return columnProperties;
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
			return startTime;
		}
		/**
		 * Returns the stopTime in millis
		 * @return long stopTime
		 */
		public long getStopTime()
		{
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

}
