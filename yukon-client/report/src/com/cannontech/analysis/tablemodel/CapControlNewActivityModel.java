package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.capcontrol.CapControlActivityData;
import com.cannontech.analysis.report.SimpleYukonReportBase;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Created on Dec 15, 2003
 * @author snebben
 */
public class CapControlNewActivityModel extends FilterObjectsReportModelBase<Object> {
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;
	
	/** Enum values for column representation */
	//attempt to use sub and feeder as group item, for space reasons.
	public final static int SUB_NAME_COLUMN = 0;
	public final static int FEEDER_NAME_COLUMN = 1;
	public final static int DATE_TIME_COLUMN = 2;
	public final static int CAP_BANK_NAME_COLUMN = 3;
	public final static int BANK_SIZE_COLUMN = 4;
	public final static int CBC_NAME_COLUMN = 5;
	public final static int ACTION_COLUMN = 6;
	public final static int DESCRIPTION_COLUMN = 7;


	/** String values for column representation */
	public final static String SUB_NAME_STRING = "Substation Bus";
	public final static String FEEDER_NAME_STRING = "Feeder";
	public final static String DATE_TIME_STRING = "Date/Time";
	public final static String CAP_BANK_NAME_STRING = "Cap Bank";
	public final static String BANK_SIZE_STRING = "Cap Bank Size";
	public final static String CBC_NAME_STRING = "CBC";
	public final static String ACTION_STRING = "Action";
	public final static String DESCRIPTION_STRING = "Description";
	/** Class fields */
	
	public static final int ORDER_BY_DATE_TIME = 0;
	public static final int ORDER_BY_CAP_BANK_NAME = 1;
	
	private int orderBy = ORDER_BY_CAP_BANK_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DATE_TIME, ORDER_BY_CAP_BANK_NAME
	};

	//servlet attributes/parameter strings
	private static final String ATT_ORDER_BY = "orderBy";
	/**
	 * 
	 */
	public CapControlNewActivityModel()
	{
		this(null, null);
	}
	
	public CapControlNewActivityModel(Date start_, Date stop_)
	{
		super(start_, stop_);
        setFilterModelTypes(new ReportFilter[]{
                ReportFilter.AREA,
                ReportFilter.CAPCONTROLSUBSTATION,
                ReportFilter.CAPCONTROLSUBBUS,
                ReportFilter.CAPCONTROLFEEDER,
                ReportFilter.CAPBANK
                }
            );
	}	
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			Integer subId =  new Integer( rset.getInt(1));
			Integer feederID = new Integer(rset.getInt(2));
			Integer capBankID = new Integer(rset.getInt(3));
			Integer cbcID = new Integer(rset.getInt(4));
			Integer bankSize = new Integer( rset.getInt(5));
			String action = rset.getString(6);					
			String desc = rset.getString(7);
			Timestamp dtTimestamp = rset.getTimestamp(8);
			Date dateTime = new Date(dtTimestamp.getTime());
			
			CapControlActivityData ccActivity = new CapControlActivityData(dateTime, subId, feederID, capBankID, bankSize, cbcID, action, desc);
			getData().add(ccActivity);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve MissedMeter data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("select  " +
            "el.subid as SUBSTATIONBUSID " +
            ", el.FEEDERID " +
            ", CB.DEVICEID " +
            ", CB.CONTROLDEVICEID " +
            ", cb.BANKSIZE " +
            ", el.capbankstateinfo as ACTION " +
            ", eL.text as DESCRIPTION " +
            ", el.DATETIME  " +
            ", sbl.substationid as substationId" +
            ", saa.areaid as areaId "+
            "from cceventlog el, capbank cb, point p, CCSUBSTATIONSUBBUSLIST sbl,CCSUBAREAASSIGNMENT saa " +
            "where  " +
            "el.DATETIME > ? " +
            "AND el.DATETIME <= ? " +
            "and p.pointid = el.pointid " +
            "and p.paobjectid = cb.deviceid " +
		    "and sbl.substationbusid = el.subid " +
		    "and saa.substationbusid = sbl.substationid ");
        if (getPaoIDs() != null && getPaoIDs().length > 0){
            if(getFilterModelType().equals(ReportFilter.AREA)) {
                sql.append(" AND saa.areaId IN ( " + getPaoIDs()[0] +" ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);
                        
                sql.append(")");
            }else if(getFilterModelType().equals(ReportFilter.CAPBANK)) {
                sql.append(" AND CB.DEVICEID IN ( " + getPaoIDs()[0] +" ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);
                        
                sql.append(")");
            }else if(getFilterModelType().equals(ReportFilter.CAPCONTROLFEEDER)) {
                sql.append(" AND el.FEEDERID IN ( " + getPaoIDs()[0] +" ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);
                        
                sql.append(")");
            }else if(getFilterModelType().equals(ReportFilter.CAPCONTROLSUBBUS)) {
                sql.append(" AND el.subid IN ( " + getPaoIDs()[0] +" ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);
                        
                sql.append(")");
            }else if(getFilterModelType().equals(ReportFilter.CAPCONTROLSUBSTATION)) {
                sql.append(" AND sbl.substationid IN ( " + getPaoIDs()[0] +" ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);
                        
                sql.append(")");
            }
        }
            sql.append(" ORDER BY SUBSTATIONBUSID, el.FEEDERID ");
		
		if (getOrderBy() == ORDER_BY_CAP_BANK_NAME)
			sql.append(", CB.DEVICEID, CB.CONTROLDEVICEID, DATETIME " );
		else if (getOrderBy() == ORDER_BY_DATE_TIME)
			sql.append(", DATETIME, CB.DEVICEID, CB.CONTROLDEVICEID ");		
		
		return sql;
	}
	
	
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
				
		int rowCount = 0;
		StringBuffer sql = buildSQLStatement();
		CTILogger.info(sql.toString());
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));				
				CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());
				rset = pstmt.executeQuery();
				
				while( rset.next())
				{
					addDataRow(rset);
				}
				if(!getData().isEmpty())
				{
					if( getSortOrder() == DESCENDING)
					    Collections.reverse(getData());
				}
			}
		}
			
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			SqlUtils.close(rset, pstmt, conn );
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
	 
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof CapControlActivityData)
		{
			CapControlActivityData ccActivity= ((CapControlActivityData)o);
			switch( columnIndex)
			{
				case DATE_TIME_COLUMN:
				    return ccActivity.getDateTime();
				case SUB_NAME_COLUMN:
					return YukonSpringHook.getBean(IDatabaseCache.class).getAllPaosMap().get(ccActivity.getSubID()).getPaoName();
				case FEEDER_NAME_COLUMN:
				    return YukonSpringHook.getBean(IDatabaseCache.class).getAllPaosMap().get(ccActivity.getFeederID()).getPaoName();
				case CAP_BANK_NAME_COLUMN:
				    return YukonSpringHook.getBean(IDatabaseCache.class).getAllPaosMap().get(ccActivity.getCapBankID()).getPaoName();
				case BANK_SIZE_COLUMN:
					return ccActivity.getBankSize();
				case CBC_NAME_COLUMN:
					return YukonSpringHook.getBean(IDatabaseCache.class).getAllPaosMap().get(ccActivity.getCbcID()).getPaoName();
				case ACTION_COLUMN:
				    return ccActivity.getAction();
				case DESCRIPTION_COLUMN:
				    return ccActivity.getDesc();
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
				SUB_NAME_STRING,
				FEEDER_NAME_STRING,
				DATE_TIME_STRING,
				CAP_BANK_NAME_STRING,
				BANK_SIZE_STRING,
				CBC_NAME_STRING,
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
				String.class,
				String.class,
				Date.class,
				String.class,
				Integer.class,
				String.class,
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
		    int offset = 0;
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 200, null),
				new ColumnProperties(0, 1, 180, null),
				new ColumnProperties(0, 1, 100, SimpleYukonReportBase.columnDateFormat),
				new ColumnProperties(100, 1, 80, null),
				new ColumnProperties(180, 1, 80, null),
				new ColumnProperties(260, 1, 100, null),
				new ColumnProperties(360, 1, 100, null),
				new ColumnProperties(460, 1, 240, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return "Cap Control New Activity Report";
	}
	
	/**
	 * @return
	 */
	public int getOrderBy()
	{
		return orderBy;
	}

	/**
	 * @param i
	 */
	public void setOrderBy(int i)
	{
		orderBy = i;
	}
	public String getOrderByString(int orderBy)
	{
		switch (orderBy)
		{
			case ORDER_BY_CAP_BANK_NAME:
				return "Cap Bank";
			case ORDER_BY_DATE_TIME:
				return "Date/Time";
		}
		return "UNKNOWN";
	}
	public static int[] getAllOrderBys()
	{
		return ALL_ORDER_BYS;
	}	
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Order By</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllOrderBys().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + getAllOrderBys()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getOrderByString(getAllOrderBys()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Order Direction</td>" +LINE_SEPARATOR;
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
		html += "    </td>" + LINE_SEPARATOR;
		
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	@Override
	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_ORDER_BY);
			if( param != null) {
                try {
                    setOrderBy(Integer.valueOf(param).intValue());
                } catch (NumberFormatException e) {
                    // sometimes when report radio is first selected, the order by for another
                    // report is sent in here as a string
                    setOrderBy(ORDER_BY_CAP_BANK_NAME);
                }
            } else {
                setOrderBy(ORDER_BY_CAP_BANK_NAME);
            }
							
		}
	}
}
