package com.cannontech.analysis.tablemodel;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jfree.report.modules.output.csv.CSVQuoter;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
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
public abstract class ReportModelBase<E> extends javax.swing.table.AbstractTableModel implements Reportable
{
	public enum ReportFilter{ NONE(""),
			METER("Meter Number"),
			DEVICE("Device"),
			GROUPS("Groups"),
			ROUTE("Route"),
			RECEIVER("Receiver"),
			LMGROUP("LM Group"),
			LMCONTROLAREA("LM Control Area"),
			TRANSMITTER("Transmitter"),
			RTU("RTU"),
			CAPCONTROLSUBBUS("Substation Bus"),
            CAPCONTROLSUBSTATION("Substation"),
			CAPCONTROLFEEDER("Feeder"),
			CAPBANK("Cap Bank"),
            SCHEDULE("Schedule (Script)"),
            AREA("Area"),
            PORT("Port"),
            PROGRAM("Program");

		private String filterTitle;
		
		private ReportFilter(String filterTitle) {
		this.filterTitle = filterTitle;
		}
		
		public String getFilterTitle() {
		return filterTitle;
		}
	}
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private String fieldSeparator = ",";
	
	public String NULL_STRING = "---";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm z");
	protected String columnDateTimeFormat = "MM/dd/yyyy HH:mm:ss z";
	protected String columnValueFormat = "#,##0.000###";
	private TimeZone timeZone = TimeZone.getDefault();		
	
	/** Array of String values representing the column names. */
	protected String[] columnNames;
	
	/** Array of Class values representing the column object classes. */
	protected Class[] columnTypes;
	
	/** Array of ColumnProperties values representing the columns. */
	protected ColumnProperties[] columnProperties;
	
	/** String for the title for the table model */
	protected String title;
	
	private ReportFilter[] filterModelTypes = null;
	private ReportFilter filterModelType = null;	//default to invalid?  The currently selected model.
	
	/** Yukon.DeviceMeterGroup's to query for, null means all */
	private String [] billingGroups = null;
	/** Yukon.paobjectid's to query for, null means all */
	private int[] paoIDs = null;
	
	/** Vector of data (of inner class type from implementors)*/
	protected java.util.Vector<E> data = new java.util.Vector<E>(100);

	private Date startDate = null;
	private Date stopDate = null;
	/** Class fields */
//	private int[] ecIDs = null;
	private Integer energyCompanyID = null;
	private Integer userID = null;
	
	public static final String ATT_START_DATE = "startDate";
	public static final String ATT_STOP_DATE = "stopDate";
	protected static final String ATT_EC_ID = "ecID";	//ONLY TAKES ONE ID, BUT MORE CAN BE SET USING THE STRING[] setter
	protected static final String ATT_PAOBJECT_IDS = "paoIDs";
	protected static final String ATT_SORT_ORDER = "sortOrder";	
	
	public static final String ATT_FILTER_MODEL_TYPE = "filterModelType";
    public static final String ATT_FILTER_MODEL_VALUES = "filterValues";
    public static final String ATT_FILTER_METER_VALUES = "filterMeterValues";
    public static final String ATT_FILTER_DEVICE_VALUES = "filterDeviceValues";

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
	public java.util.Vector<E> getData()
	{
		if (data == null)
		{
			data = new Vector<E>();
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
	public Class<?> getColumnClass(int column)
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
			startDate = ServletUtil.getYesterday(getTimeZone());
		}
		return startDate;
	}

	public Date getStopDate()
	{
		if( stopDate == null)
		{
			stopDate = ServletUtil.getTomorrow(getTimeZone());
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
	public void setData(Vector<E> vector)
	{
		data = vector;
	}
	/**
	 * @param i
	 */
//	public void setECIDs(Integer ecID)
//	{
//	    if( ecID != null)
//	        setECIDs(new int[]{ecID.intValue()});
//	}

	public void setEnergyCompanyID(Integer ecID)
	{
		energyCompanyID = ecID;
	}
	/**
	 * @return
	 */
//	public int[] getECIDs()
//	{
//		return ecIDs;
//	}

	public Integer getEnergyCompanyID()
	{
		return energyCompanyID;
	}
//	/**
//	 * @param is
//	 */
//	public void setECIDs(int[] is)
//	{
//		ecIDs = is;
//	}

	/**
	 * Override this method to ` an html table for a model's "options".
	 * @return
	 */
	public String getHTMLOptionsTable()
	{
		String html = "";
//		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell' height='100%'>" + LINE_SEPARATOR;
//		html += "  <tr>" + LINE_SEPARATOR;
//		html += "    <td align='center' valign='middle'>None" + LINE_SEPARATOR;
//		html += "    </td>" + LINE_SEPARATOR;
//		html += "  </tr>" + LINE_SEPARATOR;
//		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	
	public void setParameters( HttpServletRequest req )
	{
		if( req != null)
		{
			String param = req.getParameter(ATT_EC_ID);
			if( param != null)
				setEnergyCompanyID(Integer.valueOf(param));
			else
				setEnergyCompanyID(null);
				
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
			if( param != null) {
				int filterOrdinal = Integer.valueOf(param).intValue();
				for (ReportFilter filter : ReportFilter.values()) {
					if( filter.ordinal() == filterOrdinal) {
						setFilterModelType(filter);
						break;
					}
				}
			}
			else
				setFilterModelType(ReportFilter.NONE);	//default to nothing selected?
			
			//Load billingGroup model values
			if ( getFilterModelType().equals(ReportFilter.GROUPS) )
			{
				String[] paramArray = req.getParameterValues(ATT_FILTER_MODEL_VALUES);
				if( paramArray != null)
					setBillingGroups(paramArray);
				else
					setBillingGroups(null);
					
				//Unload paoIDs
				setPaoIDs(null);
			} else if( getFilterModelType().equals(ReportFilter.METER)) {
             
                String filterValueList = req.getParameter(ATT_FILTER_METER_VALUES).trim();
                StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
                int[] idsArray = new int[st.countTokens()];
                int i = 0;
                while (st.hasMoreTokens()) {
                    String meterNumber = st.nextToken().trim();
                    LiteYukonPAObject lPao = DaoFactory.getDeviceDao().getLiteYukonPaobjectByMeterNumber(meterNumber);
                    if( lPao != null) {
                        idsArray[i++] = lPao.getYukonID();
                    }
                }
                if( idsArray.length > 0 )
                    setPaoIDs(idsArray);
                else
                    setPaoIDs(null);
                //Unload billingGroups
                setBillingGroups(null);
            } else if( getFilterModelType().equals(ReportFilter.DEVICE)) {
             
                String filterValueList = req.getParameter(ATT_FILTER_DEVICE_VALUES).trim();
                StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
                int[] idsArray = new int[st.countTokens()];
                int i = 0;
        		while (st.hasMoreTokens()) {
                    String deviceName = st.nextToken().trim();
                    LiteYukonPAObject lPao = DaoFactory.getDeviceDao().getLiteYukonPaobjectByDeviceName(deviceName);
                    if( lPao != null) {
                        idsArray[i++] = lPao.getYukonID();
                    }
                }
                if( idsArray.length > 0 )
                    setPaoIDs(idsArray);
                else
                    setPaoIDs(null);

                //Unload billingGroups
                setBillingGroups(null);   
            } else { //Load PaobjectID int values

            	String[] paramArray = req.getParameterValues(ATT_FILTER_MODEL_VALUES);
				if( paramArray != null)
				{
					int [] idsArray = new int[paramArray.length];
					for (int i = 0; i < paramArray.length; i++)
					{
						if(StringUtils.isNotBlank(paramArray[i]))
							idsArray[i] = Integer.valueOf(paramArray[i]).intValue();
					}
					setPaoIDs(idsArray);
				}
				else
					setPaoIDs(null);

				//Unload billinggroups
				setBillingGroups(null);
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
	public ReportFilter getFilterModelType()
	{
	    return filterModelType;
	}
	/**
	 * @param i
	 */
	public void setFilterModelType(ReportFilter filterModelType)
	{
	    if( this.filterModelType != filterModelType)
	        this.filterModelType = filterModelType;
	}
	
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
	public ReportFilter[] getFilterModelTypes()
	{
	    return filterModelTypes;
	}
	/**
	 * @param is
	 */
	public void setFilterModelTypes(ReportFilter[] filters)
	{
	    filterModelTypes = filters;
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
	
	public void buildByteStream(OutputStream out) throws IOException
	{
        CSVQuoter quoter = new CSVQuoter(","); 

        //Write column headers
        for (int r = 0; r < getColumnCount(); r++) 
        {
            if( r != 0 )
                out.write(new String(",").getBytes());
                
            out.write(getColumnName(r).getBytes());
        }
        out.write(new String("\r\n").getBytes());
        
        //Write data
        for (int r = 0; r < getRowCount(); r++) 
        {
            for (int c = 0; c < getColumnCount(); c++) 
            { 
                if (c != 0) 
                { 
                    out.write(new String(",").getBytes()); 
                } 
                String rawValue = String.valueOf (getValueAt(r,c)); 
                out.write(quoter.doQuoting(rawValue).getBytes()); 
            } 
            out.write(new String("\r\n").getBytes());
        } 
  	}
	/**
	 * @return
	 */
	public String getFieldSeparator()
	{
		return fieldSeparator;
	}

	/**
	 * @param string
	 */
	public void setFieldSeparator(String string)
	{
		fieldSeparator = string;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

    public static int getAdjustedStartOffset(int offset, int width){
        
        int x = (offset)/ 732;
        if(offset + width > (732 *(x+1)))
            offset = ((x+1) * 732);
        return offset;
    }
    
    protected String getGroupSqlWhereClause(String identifier) {
        DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
        Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(Arrays.asList(getBillingGroups()));
        return deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, identifier);
    }
    
    protected String getPaoIdWhereClause(String fieldIdentifer) {
        
        String sql = "";
        int[] paoIds = getPaoIDs();
        
        if (paoIds != null && paoIds.length > 0)
        {
            sql = " " + fieldIdentifer + " IN (" + paoIds[0];
            for (int i = 1; i < paoIds.length; i++)
                sql += ", " + paoIds[i];
            sql += ") ";
        }   
        
        return sql;
    }
}
