package com.cannontech.analysis.tablemodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.model.DBTreeModel;
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
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
	private TimeZone timeZone = TimeZone.getDefault();		
	
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
	private int paoModelType = -1;	//default to invalid?	, the currently selected model
	private int[] paoModelTypes = null;	//all models valid for a report type (ONLY PAOBJECTS TODAY!)

	/** Yukon.DeviceMeterGroup's to query for, null means all */
	private String [] billingGroups = null;
	private int billingGroupType = DeviceMeterGroup.COLLECTION_GROUP;
	
	/** Vector of data (of inner class type from implementors)*/
	protected java.util.Vector data = new java.util.Vector(100);

	private Date startDate = null;
	private Date stopDate = null;
	/** Class fields */
	private int[] ecIDs = null;
	
	protected static final String ATT_START_DATE = "startDate";
	protected static final String ATT_STOP_DATE = "stopDate";
	protected static final String ATT_EC_ID = "ecID";	//ONLY TAKES ONE ID, BUT MORE CAN BE SET USING THE STRING[] setter
	protected static final String ATT_BILLING_GROUP_VALUES = "billGroupValues";
	protected static final String ATT_PAOBJECT_IDS = "paoIDs";
	
	protected static final String ATT_BILLING_GROUP_TYPE = "billGroupType";
	protected static final String ATT_PAOBJECT_MODEL_TYPE = "paoModelType";
	 
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
		html += "    var typeGroup = form.paoIDs;" + LINE_SEPARATOR;
		html += "    for (var i = 0; i < typeGroup.length; i++) {" + LINE_SEPARATOR;
		html += "      typeGroup[i].disabled = form.selectAll.checked;" + LINE_SEPARATOR;
		html += "      typeGroup[i].selectedIndex = -1;" + LINE_SEPARATOR;
		html += "    }" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;
		html += "  if (form.billGroupValues) {" + LINE_SEPARATOR;
		html += "    var billGroup = form.billGroupValues;" + LINE_SEPARATOR;
		html += "    for (var i = 0; i < billGroup.length; i++) {" + LINE_SEPARATOR;
		html += "      billGroup[i].disabled = form.selectAll.checked;" + LINE_SEPARATOR;
		html += "      billGroup[i].selectedIndex = -1;" + LINE_SEPARATOR;
		html += "    }" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;
		html += "}" + LINE_SEPARATOR;

		if (useBillingGroup())
		{
			html += "function changeFilter(filterBy) {" + LINE_SEPARATOR;
			html += "  document.getElementById('selectAll').disabled = (filterBy == -1);" + LINE_SEPARATOR;
			html += "  var typeGroup = document.reportForm.billGroupValues;" + LINE_SEPARATOR;
			html += "  for (var i = 0; i < typeGroup.length; i++) {" + LINE_SEPARATOR;
			html += "    typeGroup[i].selectedIndex = -1;" + LINE_SEPARATOR;
			html += "  }" + LINE_SEPARATOR + LINE_SEPARATOR;
			html += "  document.getElementById('DivCollGroup').style.display = (filterBy == " + DeviceMeterGroup.COLLECTION_GROUP + ")? \"\" : \"none\";" + LINE_SEPARATOR;
			html += "  document.getElementById('DivAltGroup').style.display = (filterBy == " + DeviceMeterGroup.TEST_COLLECTION_GROUP + ")? \"\" : \"none\";" + LINE_SEPARATOR;
			html += "  document.getElementById('DivBillGroup').style.display = (filterBy == " + DeviceMeterGroup.BILLING_GROUP + ")? \"\" : \"none\";" + LINE_SEPARATOR;
			html += "}" + LINE_SEPARATOR + LINE_SEPARATOR;
		}
		if (usePaobjects())
		{
			html += "function changePaoFilter(filterBy) {" + LINE_SEPARATOR;
			html += "  document.getElementById('selectAll').disabled = (filterBy == -1);" + LINE_SEPARATOR;
			html += "  var typeGroup = document.reportForm.paoIDs;" + LINE_SEPARATOR;
			html += "  for (var i = 0; i < typeGroup.length; i++) {" + LINE_SEPARATOR;
			html += "    typeGroup[i].selectedIndex = -1;" + LINE_SEPARATOR;
			html += "  }" + LINE_SEPARATOR + LINE_SEPARATOR;
			html += "  document.getElementById('DivControlArea').style.display = (filterBy == " + ModelFactory.LMCONTROLAREA + ")? \"\" : \"none\";" + LINE_SEPARATOR;
			html += "  document.getElementById('DivMCT').style.display = (filterBy == " + ModelFactory.MCT + ")? \"\" : \"none\";" + LINE_SEPARATOR;
			html += "}" + LINE_SEPARATOR;
		}
		html += "</SCRIPT>" + LINE_SEPARATOR + LINE_SEPARATOR;

		html += "<table width='100%' border='0' cellspacing='0' cellpadding='0' align='center'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td class='main' style='padding-left:5; padding-top:5'>" + LINE_SEPARATOR;
		
		if( useBillingGroup() )
		{
			html += "      <div id='DivBillGroupType' style='display:true'>" + LINE_SEPARATOR;
			html += "        <select id='billGroupType' name='billGroupType' onChange='changeFilter(this.value)'>" + LINE_SEPARATOR;
			String [] billGroupTypes = DeviceMeterGroup.getValidBillGroupTypeDisplayStrings();
			for (int i = 0; i < billGroupTypes.length; i++)
			{
				html += "          <option value='" + DeviceMeterGroup.getValidBillGroupTypeIDs()[i] +"'>" + billGroupTypes[i] + "</option>"  + LINE_SEPARATOR;
			}
			html += "        </select>" + LINE_SEPARATOR;
			html += "      </div>" + LINE_SEPARATOR;
		}
		if( usePaobjects() )
		{
			html += "      <div id='DivPaoModelType' style='display:true'>" + LINE_SEPARATOR;
			if( getPaoModelTypes() != null)
			{
				html += "        <select id='paoModelType' name='paoModelType'  onChange='changePaoFilter(this.value)'>" + LINE_SEPARATOR;
				for(int i = 0; i < getPaoModelTypes().length; i++)
				{					
					html += "          <option value='" + getPaoModelTypes()[i] + "'>" + ((DBTreeModel) ModelFactory.create(getPaoModelTypes()[i])).toString() + "</option>" + LINE_SEPARATOR;
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
		
		if( useBillingGroup())
		{
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			List collGroups = cache.getAllDMG_CollectionGroups();
			List altGroups = cache.getAllDMG_AlternateGroups();
			List billGroups = cache.getAllDMG_BillingGroups();
			
			html += "      <div id='DivCollGroup' style='display:true'>" + LINE_SEPARATOR;
			html += "        <select name='billGroupValues' size='10' multiple style='width:250px;'>" + LINE_SEPARATOR;
			for (int i = 0; i < collGroups.size(); i++)
			{
				String val = (String)collGroups.get(i);
				html += "          <option value='" + val + "'>" + val + "</option>" + LINE_SEPARATOR;
			}
			html += "        </select>" + LINE_SEPARATOR;
			html += "      </div>" + LINE_SEPARATOR;
	
			html += "      <div id='DivAltGroup' style='display:none'>" + LINE_SEPARATOR;
			html += "        <select name='billGroupValues' size='10' multiple style='width:250px;'>" + LINE_SEPARATOR;

			for (int i = 0; i < altGroups.size(); i++)
			{
				String val = (String)altGroups.get(i);
				html += "          <option value='" + val + "'>" + val + "</option>" + LINE_SEPARATOR;
			}
			html += "        </select>" + LINE_SEPARATOR;
			html += "      </div>" + LINE_SEPARATOR;
			
			html += "      <div id='DivBillGroup' style='display:none'>" + LINE_SEPARATOR;
			html += "        <select name='billGroupValues' size='10' multiple style='width:250px;'>" + LINE_SEPARATOR;
			for (int i = 0; i < billGroups.size(); i++)
			{
				String val = (String)billGroups.get(i);
				html += "          <option value='" + val + "'>" + val + "</option>" + LINE_SEPARATOR;
			}
			html += "        </select>" + LINE_SEPARATOR;
			html += "      </div>" + LINE_SEPARATOR;
		}
		
		if( usePaobjects() )
		{
			html += "      <div id='DivControlArea' style='display:" + (usePaobjects() ? "true" : "none") + "'>" + LINE_SEPARATOR;
			html += "        <select name='paoIDs' size='10' multiple style='width:250px;'>" + LINE_SEPARATOR;
			List litePaos = getPaobjectsByModel(ModelFactory.LMCONTROLAREA);
			if( litePaos != null) {
				for (int i = 0; i < litePaos.size(); i++) {
					LiteYukonPAObject lpao = (LiteYukonPAObject)litePaos.get(i);
					html += "          <option value='" + lpao.getYukonID() + "'>" + lpao.getPaoName() + "</option>" + LINE_SEPARATOR;
				}
			}
			html += "        </select>" + LINE_SEPARATOR;
			html += "      </div>" + LINE_SEPARATOR;
			html += "      <div id='DivMCT' style='display:none'>" + LINE_SEPARATOR;
			html += "        <select name='paoIDs' size='10' multiple style='width:250px;'>" + LINE_SEPARATOR;
	
			litePaos = getPaobjectsByModel(ModelFactory.MCT);
			if( litePaos != null) {
				for (int i = 0; i < litePaos.size(); i++) {
					LiteYukonPAObject lpao = (LiteYukonPAObject)litePaos.get(i);
					html += "          <option value='" + lpao.getYukonID() + "'>" + lpao.getPaoName() + "</option>" + LINE_SEPARATOR;
				}
			}
			html += "        </select>" + LINE_SEPARATOR;
			html += "      </div>" + LINE_SEPARATOR;
		}
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td class='main' height='10' style='padding-left:5; padding-top:5'>" + LINE_SEPARATOR;
		if( useBillingGroup() || usePaobjects())
		{
			html += "      <div id='DivSelectAll' style='displaytrue'>" + LINE_SEPARATOR;
			html += "        <input type='checkbox' name='selectAll' value='selectAll' onclick='disableGroup(document.reportForm);'>Select All" + LINE_SEPARATOR;
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
				
			param = req.getParameter(ATT_BILLING_GROUP_TYPE);
			if( param != null)
				setBillingGroupType(Integer.valueOf(param).intValue());
			else
				setBillingGroupType(DeviceMeterGroup.COLLECTION_GROUP);
			
			String[] paramArray = req.getParameterValues(ATT_BILLING_GROUP_VALUES);
			if( paramArray != null)
				setBillingGroups(paramArray);
			else
				setBillingGroups(null);
				
			param = req.getParameter(ATT_PAOBJECT_MODEL_TYPE);
			if( param != null)
				setPaoModelType(Integer.valueOf(param).intValue());
			else
				setPaoModelType(-1);
								
			paramArray = req.getParameterValues(ATT_PAOBJECT_IDS);
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
	public int getBillingGroupType()
	{
		return billingGroupType;
	}

	/**
	 * @param i
	 */
	public void setBillingGroupType(int billGroupType)
	{
		if( billingGroupType != billGroupType)
			billingGroupType = billGroupType;
	}
	/**
	 * @return
	 */
	public int getPaoModelType()
	{
		return paoModelType;
	}

	/**
	 * @param i
	 */
	public void setPaoModelType(int paoModelType_)
	{
		if( paoModelType != paoModelType_)
			paoModelType = paoModelType_;
	}
	/**
	 * Override this function if an extended model does not have a Billing Group
	 * @return
	 */
	public boolean useBillingGroup()
	{
		return false;
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
	public boolean usePaobjects()
	{
		return (getPaoModelTypes() != null);
	}
	
	/**
	 * @return
	 */
	public int[] getPaoModelTypes()
	{
		return paoModelTypes;
	}

	/**
	 * @param is
	 */
	public void setPaoModelTypes(int[] is)
	{
		paoModelTypes = is;
	}

	public List getPaobjectsByModel(int model)
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		List litePaos = null;
		if( model == ModelFactory.LMCONTROLAREA)
		{
			litePaos = cache.getAllLMControlAreas();
		}
		else if( model == ModelFactory.MCT)
		{
			litePaos = cache.getAllMCTs();
		}
		return litePaos;
	}

}
