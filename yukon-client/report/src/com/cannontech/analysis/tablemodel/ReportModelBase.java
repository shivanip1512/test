package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.model.ModelFactory;
import com.cannontech.util.ServletUtil;


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
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public String NULL_STRING = "---";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
	protected String columnDateTimeFormat = "MM/dd/yyyy HH:mm:ss";
	protected String columnValueFormat = "#,##0.000";
	private TimeZone timeZone = TimeZone.getDefault();		
	
	/** Array of String values representing the column names. */
	protected String[] columnNames;
	
	/** Array of Class values representing the column object classes. */
	protected Class[] columnTypes;
	
	/** Array of ColumnProperties values representing the columns. */
	protected ColumnProperties[] columnProperties;
	
	/** String for the title for the table model */
	protected String title;
	
	private int[] filterModelTypes = null;	//all models for a report type (ModelFactory.xxx)
	private int filterModelType = -1;	//default to invalid?  The currently selected model.
	
	/** Yukon.DeviceMeterGroup's to query for, null means all */
	private String [] billingGroups = null;
	/** Yukon.paobjectid's to query for, null means all */
	private int[] paoIDs = null;
	
	/** Vector of data (of inner class type from implementors)*/
	protected java.util.Vector data = new java.util.Vector(100);

	private Date startDate = null;
	private Date stopDate = null;
	/** Class fields */
	private int[] ecIDs = null;
	
	protected static final String ATT_START_DATE = "startDate";
	protected static final String ATT_STOP_DATE = "stopDate";
	protected static final String ATT_EC_ID = "ecID";	//ONLY TAKES ONE ID, BUT MORE CAN BE SET USING THE STRING[] setter
	protected static final String ATT_PAOBJECT_IDS = "paoIDs";
	protected static final String ATT_SORT_ORDER = "sortOrder";	
	
	protected static final String ATT_FILTER_MODEL_TYPE = "filterModelType";
	protected static final String ATT_FILTER_MODEL_VALUES = "filterValues";

	public  static final int ASCENDING = 0;
	public static final int DESCENDING = 1;
	protected int sortOrder = ASCENDING;
	protected static final int[] ALL_SORT_ORDERS = new int[]
	{
		ASCENDING, DESCENDING
	};
	
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
	public ReportModelBase(Date startDate_, Date stopDate_)
	{
		this();
		setStartDate(startDate_);
		setStopDate(stopDate_);
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

	public void setTimeZone(TimeZone tz)
	{
		if( timeZone.getID() != tz.getID())
		{
			timeZone = tz;
			getDateFormat().setTimeZone(timeZone);
		}
	}
	
	/**
	 * Return the date information for the data.
	 * OVERRIDE FOR ALL EXTENDERS USING START/STOP DATE INTERVALS.
	 * @return String formated date string.
	 */
	public String getDateRangeString()
	{
		return (getDateFormat().format(getStartDate())) + " through " +
				   (getDateFormat().format(getStopDate()));
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

	public Date getStartDate()
	{
		if (startDate == null)
		{
			startDate = ServletUtil.getYesterday();
		}
		return startDate;
	}

	public Date getStopDate()
	{
		if( stopDate == null)
		{
			stopDate = ServletUtil.getTomorrow();
		}
		return stopDate;		
	}
	/**
	 * Set the startDate
	 * @param Date date 
	 */
	public void setStartDate(Date startDate_)
	{
		startDate = startDate_;
	}
	/**
	 * Set the stopDate
	 * @param Date date
	 */
	public void setStopDate(Date stopDate_)
	{
		stopDate = stopDate_;
	}

	/**
	 * @return
	 */
	public String[] getBillingGroups()
	{
		return billingGroups;
	}

	/**
	 * @param strings
	 */
	public void setBillingGroups(String[] strings)
	{
		billingGroups = strings;
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
	    if( ecID != null)
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

	/**
	 * Override this method to build an html table for a model's "options".
	 * @return
	 */
	public String getHTMLOptionsTable()
	{
		String html = "N/A";
//		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell' height='100%'>" + LINE_SEPARATOR;
//		html += "  <tr>" + LINE_SEPARATOR;
//		html += "    <td align='center' valign='middle'>None" + LINE_SEPARATOR;
//		html += "    </td>" + LINE_SEPARATOR;
//		html += "  </tr>" + LINE_SEPARATOR;
//		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	
	/**
	 * Generatest the HTML for base options used at the ReportModelBase level (aka options for all reports)
	 * @return
	 */
	public String getHTMLBaseOptionsTable()
	{
		String html = "";
		
		html += "<SCRIPT>" + LINE_SEPARATOR;
		html += "function disableGroup(form){" + LINE_SEPARATOR;
		html += "  if (form.paoIDs) {" + LINE_SEPARATOR;
		html += "    uncheckAll(form, form.paoIDs)" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;
		html += "  if (form.filterValues) {" + LINE_SEPARATOR;
		html += "    uncheckAll(form, form.filterValues)" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;
		html += "}" + LINE_SEPARATOR;

		html += "function uncheckAll(form, group){" + LINE_SEPARATOR;
		html += "  for (var i = 0; i < group.length; i++) {" + LINE_SEPARATOR;
		html += "    group[i].disabled = form.selectAll.checked;" + LINE_SEPARATOR;		
		html += "    group[i].selectedIndex = -1;" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;
		html += "}" + LINE_SEPARATOR;
		
		if (getFilterModelTypes() != null)
		{
			html += "function changeFilter(filterBy) {" + LINE_SEPARATOR;
			html += "  document.getElementById('selectAll').disabled = (filterBy == -1);" + LINE_SEPARATOR;
			html += "  var filterModelValues = document.reportForm." + ATT_FILTER_MODEL_VALUES+";" + LINE_SEPARATOR;
			html += "  for (var i = 0; i < filterModelValues.length; i++) {" + LINE_SEPARATOR;
			html += "    filterModelValues[i].selectedIndex = -1;" + LINE_SEPARATOR;
			html += "  }" + LINE_SEPARATOR + LINE_SEPARATOR;
			
			for(int i = 0; i < getFilterModelTypes().length; i++)
			{
				html += "  document.getElementById('Div"+ ModelFactory.getModelString(getFilterModelTypes()[i])+"').style.display = (filterBy == " + getFilterModelTypes()[i] + ")? \"\" : \"none\";" + LINE_SEPARATOR;			    
			}
			html += "}" + LINE_SEPARATOR + LINE_SEPARATOR;
		}
		html += "</SCRIPT>" + LINE_SEPARATOR + LINE_SEPARATOR;

		html += "<table width='100%' border='0' cellspacing='0' cellpadding='0' align='center'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td class='main' style='padding-left:5; padding-top:5'>" + LINE_SEPARATOR;
		
		if( getFilterModelTypes() != null )
		{
			html += "      <div id='DivFilterModelType' style='display:true'>" + LINE_SEPARATOR;

			if (getFilterModelTypes() != null)
			{
				html += "        <select id='" + ATT_FILTER_MODEL_TYPE+ "' name='" + ATT_FILTER_MODEL_TYPE + "' onChange='changeFilter(this.value)'>" + LINE_SEPARATOR;
				for (int i = 0; i < getFilterModelTypes().length; i++)
				{
					html += "          <option value='" + getFilterModelTypes()[i] +"'>" + ModelFactory.getModelString(getFilterModelTypes()[i]).toString() + "</option>"  + LINE_SEPARATOR;
				}
				html += "        </select>" + LINE_SEPARATOR;
			}
			html += "      </div>" + LINE_SEPARATOR;
		}
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "  <tr><td height='9'></td></tr>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td class='main' valign='top' height='19' style='padding-left:5; padding-top:5'>" + LINE_SEPARATOR;
		
		if( getFilterModelTypes() != null)
		{
			for(int i = 0; i < getFilterModelTypes().length; i++)
			{
				html += "      <div id='Div"+ ModelFactory.getModelString(getFilterModelTypes()[i]) +"' style='display:"+(i==0?"true":"none")+"'>" + LINE_SEPARATOR;
				html += "        <select name='" + ATT_FILTER_MODEL_VALUES+ "' size='10' multiple style='width:250px;'>" + LINE_SEPARATOR;
				List objects = getObjectsByModelType(getFilterModelTypes()[i]);
				if (objects != null)
				{
					for (int j = 0; j < objects.size(); j++)
					{
					    if( objects.get(j) instanceof String)
					        html += "          <option value='" + objects.get(j).toString()+ "'>" + objects.get(j).toString() + "</option>" + LINE_SEPARATOR;
					    else if (objects.get(j) instanceof LiteYukonPAObject)
					        html += "          <option value='" + ((LiteYukonPAObject)objects.get(j)).getYukonID() + "'>" + ((LiteYukonPAObject)objects.get(j)).getPaoName() + "</option>" + LINE_SEPARATOR;
					}
				}
				html += "        </select>" + LINE_SEPARATOR;
				html += "      </div>" + LINE_SEPARATOR;
			}
		}
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td class='main' height='10' style='padding-left:5; padding-top:5'>" + LINE_SEPARATOR;
		if( getFilterModelTypes() != null)
		{
			html += "      <div id='DivSelectAll' style='displaytrue'>" + LINE_SEPARATOR;
			html += "        <input type='checkbox' name='selectAll' value='selectAll' onclick='disableGroup(document.reportForm);'>Select All" + LINE_SEPARATOR;
			html += "      </div>" + LINE_SEPARATOR;
			html += "      <div id='DivSelectNone' style='displaytrue'>" + LINE_SEPARATOR;			
			html += "        <input type='button' value='Unselect All' onclick='disableGroup(document.reportForm);'/>";
			html += "      </div>" + LINE_SEPARATOR;
			    
		}
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;

		return html;
	}

	public void setParameters( HttpServletRequest req )
	{
		if( req != null)
		{
			String param = req.getParameter(ATT_EC_ID);
			if( param != null)
				setECIDs(Integer.valueOf(param));
			else
				setECIDs((int[])null);
				
			param = req.getParameter(ATT_START_DATE);
			if( param != null)
				setStartDate(ServletUtil.parseDateStringLiberally(param, getTimeZone()));				
			else
				setStartDate(null);
			
			param = req.getParameter(ATT_STOP_DATE);
			if( param != null)
				setStopDate(ServletUtil.parseDateStringLiberally(param, getTimeZone()));
			else
				setStopDate(null);
				
			param = req.getParameter(ATT_FILTER_MODEL_TYPE);
			if( param != null)
				setFilterModelType(Integer.valueOf(param).intValue());
			else
				setFilterModelType(-1);	//default to nothing selected?
			
			//Load billingGroup model values
			if ( getFilterModelType() == ModelFactory.COLLECTIONGROUP ||
		        getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP ||
		        getFilterModelType() == ModelFactory.BILLING_GROUP )
			{
				String[] paramArray = req.getParameterValues(ATT_FILTER_MODEL_VALUES);
				if( paramArray != null)
					setBillingGroups(paramArray);
				else
					setBillingGroups(null);
			}
			else	//Load PaobjectID int values
			{
				String[] paramArray = req.getParameterValues(ATT_FILTER_MODEL_VALUES);
				if( paramArray != null)
				{
					int [] idsArray = new int[paramArray.length];
					for (int i = 0; i < paramArray.length; i++)
					{
						idsArray[i] = Integer.valueOf(paramArray[i]).intValue();
					}
					setPaoIDs(idsArray);
				}
				else
					setPaoIDs(null);
			}
			//This parameter is not implemented in this getHTMLOptionsTable since most of the implementing classes
			//	have their own rendering of this parameter.  But setting it here takes care of all the other classes not having to do it.
			param = req.getParameter(ATT_SORT_ORDER);
			if( param != null)
				setSortOrder(Integer.valueOf(param).intValue());
			else
				setSortOrder(ASCENDING);			
		}
	}
	/**
	 * @return
	 */
	public SimpleDateFormat getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * @return
	 */
	public TimeZone getTimeZone()
	{
		return timeZone;
	}

	/**
	 * @param format
	 */
	public void setDateFormat(SimpleDateFormat format)
	{
		dateFormat = format;
	}

	/**
	 * @return
	 */
	public int getFilterModelType()
	{
	    return filterModelType;
	}
	/**
	 * @param i
	 */
	public void setFilterModelType(int modelType)
	{
	    if( filterModelType != modelType)
	        filterModelType = modelType;
	}
	
	/**
	 * Override this function if an extended model does not have a Billing Group
	 * @return
	 */
//	public boolean useFilterModel()
//	{
//		return false;
//	}
		
	/**
	 * Override this function if an extended model does not use a Start Date
	 * @return
	 */
	public boolean useStartDate()
	{
		return true;
	}
	/**
	 * Override this function if an extended model does not use a Stop Date
	 * @return
	 */
	public boolean useStopDate()
	{
		return true;
	}
	
	/**
	 * @return
	 */
	public int[] getFilterModelTypes()
	{
	    return filterModelTypes;
	}
	/**
	 * @param is
	 */
	public void setFilterModelTypes(int[] models)
	{
	    filterModelTypes = models;
	}

	/*
	 * Returns a List of Objects (Strings for DeviceMeterGroup stuff, and LiteYukonPaobjects for pao stuff)
	 * List is from cache.
	 */
	public List getObjectsByModelType(int model)
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		switch (model)
        {
	        case ModelFactory.LMCONTROLAREA:
	            return cache.getAllLMControlAreas();
	        case ModelFactory.LMGROUPS:
	            return cache.getAllLMGroups();
	        case ModelFactory.DEVICE:
	            return cache.getAllDevices();
	        case ModelFactory.MCT:
	            return cache.getAllMCTs();
	        case ModelFactory.COLLECTIONGROUP:
	            return cache.getAllDMG_CollectionGroups();
	        case ModelFactory.TESTCOLLECTIONGROUP:
	            return cache.getAllDMG_AlternateGroups();
	        case ModelFactory.BILLING_GROUP:
	            return cache.getAllDMG_BillingGroups();
	        case ModelFactory.ROUTE:
	        	return cache.getAllRoutes();
        }
		return null;
	}
	public static String getBillingGroupDatabaseString(int modelType)
	{
	    if ( modelType == ModelFactory.TESTCOLLECTIONGROUP)
	        return DeviceMeterGroup.getValidBillGroupTypeStrings()[DeviceMeterGroup.TEST_COLLECTION_GROUP];
	    else if ( modelType == ModelFactory.BILLING_GROUP)
	        return DeviceMeterGroup.getValidBillGroupTypeStrings()[DeviceMeterGroup.BILLING_GROUP];
	    else	// if ( modelType == ModelFactory.COLLECTIONGROUP)
	        return DeviceMeterGroup.getValidBillGroupTypeStrings()[DeviceMeterGroup.COLLECTION_GROUP];
	}
	public int getSortOrder()
	{
		return sortOrder;
	}

	public void setSortOrder(int i)
	{
		sortOrder = i;
	}
	public String getSortOrderString(int sortOrder)
	{
		switch (sortOrder)
		{
			case ASCENDING:
				return "Ascending";
			case DESCENDING:
				return "Descending";
		}
		return "UNKNOWN";
	}	
	public static int[] getAllSortOrders()
	{
		return ALL_SORT_ORDERS;
	}
	
	/**
	 * Returns a calendar with the time zeroed out.  Useful for grouping by date.
	 * @param cal
	 * @return
	 */
	public static GregorianCalendar getBeginingOfDay(GregorianCalendar cal)
	{
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.setTimeInMillis(cal.getTimeInMillis());
		tempCal.set(Calendar.HOUR_OF_DAY, 0);
		tempCal.set(Calendar.MINUTE, 0);
		tempCal.set(Calendar.SECOND, 0);
		tempCal.set(Calendar.MILLISECOND, 0);
		if ( cal.get(Calendar.HOUR_OF_DAY) == 0 &&
			cal.get(Calendar.MINUTE) == 0 &&
			cal.get(Calendar.SECOND) == 0 &&
			cal.get(Calendar.MILLISECOND) == 0)	//This is actually the previous day's LAST possible reading, return yesterday's date!
			tempCal.add(Calendar.DATE, -1);

		return tempCal;
	}
	
	/**
	 * Convert seconds of time into hh:mm:ss string.
	 * @param int seconds
	 * @return String in format hh:mm:ss
	 */
	protected static String convertSecondsToTimeString(int seconds)
	{
		DecimalFormat format = new DecimalFormat("00");
		int hour = seconds / 3600;
		int temp = seconds % 3600;
		int min = temp / 60;
		int sec = temp % 60; 
			
		return format.format(hour) + ":" + format.format(min) + ":" + format.format(sec);
	}	
}
