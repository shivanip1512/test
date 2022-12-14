package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Lists;

/**
 * Created on Dec 15, 2003
 * SystemLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  String pointName 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class SystemLogModel extends ReportModelBase<SystemLog>
{
    private LiteYukonUser liteUser;
    
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int DATE_COLUMN = 0;
	public final static int TIME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int PRIORITY_COLUMN = 3;
	public final static int USERNAME_COLUMN = 4;
	public final static int ACTION_COLUMN = 5;
	public final static int DESCRIPTION_COLUMN = 6;	
//	public final static int POINT_ID_COLUMN = 7;
	
	/** String values for column representation */
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
//	public final static String POINT_ID_STRING = "PointID";
	public final static String POINT_NAME_STRING = "Point";
	public final static String PRIORITY_STRING = "Priority";
	public final static String ACTION_STRING = "Action";
	public final static String DESCRIPTION_STRING = "Description";
	public final static String USERNAME_STRING = "UserName";
	
	/** A string for the title of the data */
	private static String title = "SYSTEM LOG";
	
	/** Class fields */
	/**
	 * Type int from com.cannontech.database.db.point.SystemLog.type
	 * Allows for reporting by type, null value results in all types.
	 */
	private int[] logTypes = null;

	private static final int ALL_LOG_TYPES = -1;	//use some invalid number

	//	servlet attributes/parameter strings
	protected static final String ATT_LOG_TYPE = "logType";
	protected static final String ATT_All_LOG_TYPE = "logTypeAll";
	
	/**
	 * PointId int from com.cannontech.database.db.point.SystemLog.pointID
	 * Allows for reporting by a pointid, null value results in all points.
	 */	
	private Integer pointID = null;
	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 */
	public SystemLogModel(Date start_, Date stop_)
	{
		this(start_, stop_, null, null);
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public SystemLogModel(Date start_, Date stop_, Integer logType_)
	{
		this(start_, stop_, logType_, null);
	}
	/**
	 * Constructor class
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public SystemLogModel()
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
	public SystemLogModel(Date start_, Date stop_, Integer logType_, Integer pointID_)
	{
		super(start_, stop_);
		if( logType_!= null)
			setLogType(logType_.intValue());
		setPointID(pointID_);
	}
	/**
	 * Add SystemLog objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			java.sql.Timestamp dateTime = rset.getTimestamp(1);
			Integer pointID = new Integer(rset.getInt(2));
			Integer priority = new Integer(rset.getInt(3));
			String action = rset.getString(4);
			String description = rset.getString(5);
			String userName = rset.getString(6);

			SystemLog systemLog = new SystemLog();
			systemLog.setDateTime(new java.util.Date(dateTime.getTime()));
			systemLog.setPointID(pointID);
			systemLog.setPriority(priority);
			systemLog.setAction(action);
			systemLog.setDescription(description);
			systemLog.setUserName(userName);
 
			getData().add(systemLog);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve SystemLog data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
	    
		StringBuffer sql = new StringBuffer("SELECT DATETIME, SL.POINTID, PRIORITY, ACTION, SL.DESCRIPTION, USERNAME "+ 
			" FROM SYSTEMLOG SL, POINT P, YUKONPAOBJECT PAO ");
			sql.append(" WHERE (DATETIME > ?) AND (DATETIME <= ?)" + 
						" AND P.POINTID = SL.POINTID " + 
						" AND P.PAOBJECTID = PAO.PAOBJECTID ");

		//if LMControlLog model, let's trust the YUKONPAOBJECT.class value = 'GROUP'
		// rather than the SYSTEMLOG.type value == 3 (LOADMANAGMENT)	SN / COREY
		if(this instanceof LMControlLogModel) {
		    List<LiteYukonPAObject> restrictedGroups = ReportFuncs.getRestrictedLMGroups(liteUser);
		    /*
             *  Restrict results based on PaoPermissions of LM Groups.
             */
		    if(getPaoIDs() != null){ /* They picked some groups */
    		        List<Integer> paoIds = Lists.newArrayList();
    		        for(int i : getPaoIDs()){
    		            paoIds.add(i);
    		        }
                    List<String> finalPaoIdsList = Lists.newArrayList();
                    for(YukonPao group : restrictedGroups){
                        if(paoIds.contains(group.getPaoIdentifier().getPaoId())){
                            finalPaoIdsList.add(String.valueOf(group.getPaoIdentifier().getPaoId()));
                        }
                    }
                    String sqlList = SqlStatementBuilder.convertToSqlLikeList(finalPaoIdsList);
                    sql.append("AND P.PAOBJECTID in (" + sqlList + ")");
                    
		    } else { /* They didn't pick any groups */
		            
		            List<String> finalPaoIdsList = Lists.newArrayList();
		            for(YukonPao group : restrictedGroups){
		                finalPaoIdsList.add(String.valueOf(group.getPaoIdentifier().getPaoId()));
		            }
		            String sqlList = SqlStatementBuilder.convertToSqlLikeList(finalPaoIdsList);
                    sql.append("AND P.PAOBJECTID in (" + sqlList + ")");
                    
		    }
		    
			sql.append(" AND PAOCLASS = '" + PaoClass.GROUP.getDbString() + "' ");
		} else {
		    if( getPaoIDs()!= null)  //null load groups means ALL groups!
            {
                sql.append(" AND P.PAOBJECTID in (" + getPaoIDs()[0] ); 
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(", " + getPaoIDs()[i]+" ");
                sql.append(") ");
            }
			if( getLogTypes() != null)
			{
				sql.append(" AND SL.TYPE IN (" + getLogTypes()[0]);
				for (int i = 1; i < getLogTypes().length; i++)
					sql.append(" , " + getLogTypes()[i]);
				sql.append(" )");
			}
			if( getPointID() != null)
				sql.append(" AND SL.POINTID = " + getPointID().intValue());
		}
		
		sql.append(" ORDER BY DATETIME ");
		
		if( getSortOrder() == DESCENDING ) {
			sql.append(" DESC " );
		}
		
		return sql;
	}
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
				
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
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()));
				CTILogger.info("START DATE > " + getStartDate() + "  -  STOP DATE <= " + getStopDate());
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
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

	/**
	 * Returns the logType
	 * @return Intger logType
	 */
	public int[] getLogTypes()
	{
		return logTypes;
	}

	/**
	 * Sets the logType
	 * Valid types are found in com.cannontech.database.db.point.SYSTEMLOG
	 * @param type Integer
	 */
	public void setLogType(int type_)
	{
		setLogType(new int[]{type_});
	}
	/**
	 * Sets the logType
	 * Valid types are found in com.cannontech.database.db.point.SYSTEMLOG
	 * @param type Integer
	 */
	public void setLogType(int[] types_)
	{
		logTypes = types_;
	}
	/**
	 * Returns the pointID
	 * @return pointID
	 */
	public Integer getPointID()
	{
		return pointID;
	}

	/**
	 * Sets the pointID.
	 * @param pointID Integer
	 */
	public void setPointID(Integer pointID_)
	{
		pointID = pointID_;
	}
	
	@Override
	public String getDateRangeString()
	{
		return getDateFormat().format( getStartDate()) +  "  -  " +
				getDateFormat().format( getStopDate());
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	@Override
    public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof SystemLog)
		{
			SystemLog sl = ((SystemLog)o);
			switch( columnIndex)
			{
				case DATE_COLUMN:
				{
					//Set the date to the begining of the day so we can "group" by date
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(sl.getDateTime());
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					return cal.getTime();
				}
				case TIME_COLUMN:
					return sl.getDateTime();
//				case POINT_ID_COLUMN:
//					return sl.getPointID();
				case POINT_NAME_COLUMN:
					return YukonSpringHook.getBean(PointDao.class).getPointName(sl.getPointID().intValue());
				case PRIORITY_COLUMN:
					return sl.getPriority();
				case USERNAME_COLUMN:
					return sl.getUserName();
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
	@Override
    public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				DATE_STRING,
				TIME_STRING,
				POINT_NAME_STRING,
				PRIORITY_STRING,
				USERNAME_STRING,
				ACTION_STRING,
				DESCRIPTION_STRING
			};
		}
		return columnNames;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	@Override
    public Class<?>[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				java.util.Date.class,
				java.util.Date.class,
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
	@Override
    public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 100, "MMMMM dd, yyyy"),
				new ColumnProperties(0, 1, 50, "HH:mm:ss"),
				new ColumnProperties(50, 1, 100, null),
				new ColumnProperties(150, 1, 40, "#"),
				new ColumnProperties(190, 1, 90, null),
				new ColumnProperties(280, 1, 200, null),
				new ColumnProperties(480, 1, 230, null)
			};				
		}
		return columnProperties;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	@Override
    public String getTitleString()
	{
		return title;
	}
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<script>" + LINE_SEPARATOR;
		html += "function selectAllGroup(form, value) {" + LINE_SEPARATOR;
		html += "  var typeGroup = form.logType;" + LINE_SEPARATOR;
		html += "  for (var i = 0; i < typeGroup.length; i++){" + LINE_SEPARATOR;
		html += "    typeGroup[i].checked = value;" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;
		html += "}" + LINE_SEPARATOR;
		html += "</script>" + LINE_SEPARATOR;
		
		html += "<table align='center' width='90%' border='0'cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='title-header'>Point Type</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < SystemLog.LOG_TYPES.length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='checkbox' name='" + ATT_LOG_TYPE +"' value='" + SystemLog.LOG_TYPES[i] + "' onclick='document.reportForm."+ATT_All_LOG_TYPE+".checked = false;'" +  
			 				">" + SystemLog.getLogTypeStringFromID(SystemLog.LOG_TYPES[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" + ATT_All_LOG_TYPE +"' value='" + ALL_LOG_TYPES + "' onclick='selectAllGroup(document.reportForm, this.checked)'>Select All" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		
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

			String param = req.getParameter(ATT_All_LOG_TYPE);
			if( param != null)
				setLogType(null);	//ALL Of them!
			else 
			{			
				String[] paramArray = req.getParameterValues(ATT_LOG_TYPE);
				if( paramArray != null)
				{
					int [] typesArray = new int[paramArray.length];
					for (int i = 0; i < paramArray.length; i++)
					{
						typesArray[i] = Integer.valueOf(paramArray[i]).intValue();
					}
					setLogType(typesArray);
				}
				else
					setLogType(null);
			}
			setLiteUser(ServletUtil.getYukonUser(req));
		}
	}
	
	public LiteYukonUser getLiteUser() {
        return liteUser;
    }
    public void setLiteUser(LiteYukonUser liteUser) {
        this.liteUser = liteUser;
    }
}
