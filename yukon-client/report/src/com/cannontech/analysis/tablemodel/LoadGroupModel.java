package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.util.ServletUtil;

/**
 * Created on Dec 15, 2003
 * LoadGroupReportData TableModel object
 * Innerclass object for row data is LGAccounting:
 * 	String paoName				- TempLMControlHistory.deviceName
 * 	java.util.Date startDate	- LMControlHistory.startDateTime
 *  java.util.Date stopDate		- LMControlHistory.stopDateTime
 *  String duration				- LMControlHistory.controlDuration (in seconds)
 *  String activeRestore		- LMControlHistory.activeRestore
 *  String controlType			- LMControlHistory.controlType
 *  String dailyControl			- LMControlHistory.currentDailyControl (in seconds)
 *  String monthlyControl		- LMControlHistory.currentMonthlyControl (in seconds)
 *  String seasonalControl		- LMControlHistory.currentSeasonalControl (in seconds)
 *  String annualControl		- LMControlHistory.currentAnnualControl (in seconds)
 * @author snebben
 */
public class LoadGroupModel extends ReportModelBase<LoadGroupModel.TempLMControlHistory>
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 11;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int CONTROL_DATE_COLUMN = 1;
	public final static int CONTROL_START_TIME_COLUMN = 2;
	public final static int CONTROL_STOP_TIME_COLUMN = 3;
	public final static int CONTROL_DURATION_COLUMN = 4;
	public final static int ACTIVE_RESTORE_COLUMN = 5;	
	public final static int CONTROL_TYPE_COLUMN = 6;
	public final static int DAILY_CONTROL_COLUMN = 7;
	public final static int MONTHLY_CONTROL_COLUMN = 8;
	public final static int SEASONAL_CONTROL_COLUMN = 9;
	public final static int ANNUAL_CONTROL_COLUMN = 10;

	/** String values for column representation */
	public final static String PAO_NAME_STRING = "Load Group";
	public final static String CONTROL_DATE_STRING = "Date";
	public final static String CONTROL_START_STRING = "Control Start";
	public final static String CONTROL_STOP_STRING = "Control Stop";
	public final static String CONTROL_DURATION_STRING = "Control Duration";
	public final static String ACTIVE_RESTORE_STRING = "Active Restore";
	public final static String CONTROL_TYPE_STRING= "Control Type";
	public final static String DAILY_CONTROL_STRING= "Daily";
	public final static String MONTHLY_CONTROL_STRING= "Monthly";
	public final static String SEASONAL_CONTROL_STRING= "Seasonal";
	public final static String ANNUAL_CONTROL_STRING= "Annual";

	/** A string for the title of the data */
	private static String title = "LOAD GROUP ACCOUNTING";
	
	/**
	 * A flag to show all ActiveRestore types
	 * When true - Show everything
	 * When false - Show only the most recent entry for each SOE_TAG
	 */
	private boolean showAllActiveRestore = false;
	private static final String ATT_ALL_RESTORE_TYPES = "allRestoreTypes";
	private boolean ignoreZeroDuration = false;
	private static final String ATT_IGNORE_ZERO_DURATION = "ignoreZeroDuration";
	
	private static final String ATT_SHOW_DAILY_TOTAL = "showDailyTotal";
	private boolean showDailyTotal = true;
	private static final String ATT_SHOW_MONTHLY_TOTAL = "showMonthlyTotal";
	private boolean showMonthlyTotal = true;
	private static final String ATT_SHOW_SEASONAL_TOTAL = "showSeasonalTotal";
	private boolean showSeasonalTotal = true;
	private static final String ATT_SHOW_ANNUAL_TOTAL = "showAnnualTotal";
	private boolean showAnnualTotal = true;
	private LiteYukonUser liteUser;
	
    class TempLMControlHistory
    {
        // holds just the info needed for this report for each control area
        private String deviceName = null;
        private LMControlHistory lmch = null;

        public LMControlHistory getLMControlHistory() {
            return lmch;
        }

        public void setLMControlHistory(LMControlHistory history) {
            lmch = history;
        }

        public String getDeviceName() {
            return deviceName;
        }
        
        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }
    }
    
	public static Comparator<TempLMControlHistory> lmControlHistoryPAONameComparator = new Comparator<TempLMControlHistory>()
	{
		public int compare(TempLMControlHistory o1, TempLMControlHistory o2)
		{
			String thisVal = o1.getDeviceName();
			String anotherVal = o2.getDeviceName();
		    return ( thisVal.compareToIgnoreCase(anotherVal));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public LoadGroupModel()
	{
		this(null, null, null);
	}	

	/**
	 * Constructor class
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 * 
	 * A null loadGroup is specified, which means ALL Load Groups!
	 */
	public LoadGroupModel(Date start_, Date stop_)
	{
		this(null, start_, stop_);
	}	
	
	/**
	 * Constructor class
	 * @param paoIDs_ (Array of)YukonPaobject.paobjectID (of single load group)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 */
	public LoadGroupModel( int[] paoIDs_, Date start_, Date stop_)
	{
		super(start_, stop_);
		setPaoIDs(paoIDs_);
		setFilterModelTypes(new ReportFilter[]{ 
				ReportFilter.LMGROUP}
				);
	}	
		
	/**
	 * Add LGAccounting objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
		    //Only keep the most recent entry (order by startTime asc, soe_tag desc.
//			Integer soeTag = new Integer(rset.getInt(12));
//			if(isShowAllActiveRestore() || soeTag.intValue() != lastSoeTag )
			{
			    Integer ctrlHistID = new Integer(rset.getInt(1));
				Integer paoID = new Integer(rset.getInt(2));
				java.sql.Timestamp startTS = rset.getTimestamp(3);
				java.sql.Timestamp stopTS = rset.getTimestamp(4);
				Integer controlDuration = new Integer(rset.getInt(5));
				String controlType = rset.getString(6);
				Integer dailyTime = new Integer(rset.getInt(7));
				Integer monthyTime = new Integer(rset.getInt(8));
				Integer seasonalTime = new Integer(rset.getInt(9));
				Integer annualTime = new Integer(rset.getInt(10));
				String activeRestore = rset.getString(11);
                String paoName = rset.getString(13);
				
				LMControlHistory lmControlHist = new LMControlHistory(ctrlHistID);
				lmControlHist.setPaObjectID(paoID);
				lmControlHist.setStartDateTime(new java.util.Date(startTS.getTime()));
				lmControlHist.setStopDateTime(new java.util.Date(stopTS.getTime()));
				lmControlHist.setControlDuration(controlDuration);
				lmControlHist.setControlType(controlType);
				lmControlHist.setCurrentDailyTime(dailyTime);
				lmControlHist.setCurrentMonthlyTime(monthyTime);
				lmControlHist.setCurrentSeasonalTime(seasonalTime);
				lmControlHist.setCurrentAnnualTime(annualTime);
				lmControlHist.setActiveRestore(activeRestore); 
                TempLMControlHistory tempControlHistory = new TempLMControlHistory();
                tempControlHistory.setLMControlHistory(lmControlHist);
                tempControlHistory.setDeviceName(paoName);
				getData().add(tempControlHistory);
			}
//			lastSoeTag = soeTag.intValue();
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve LoadGroup Accounting data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement() {
		List<LiteYukonPAObject> restrictedGroups = ReportFuncs.getRestrictedLMGroups(liteUser);
		StringBuffer sql = new StringBuffer("SELECT LMCH.LMCTRLHISTID, LMCH.PAOBJECTID, LMCH.STARTDATETIME, LMCH.STOPDATETIME, "+
				" LMCH.CONTROLDURATION, LMCH.CONTROLTYPE, "+
				" LMCH.CURRENTDAILYTIME, LMCH.CURRENTMONTHLYTIME, "+
				" LMCH.CURRENTSEASONALTIME, LMCH.CURRENTANNUALTIME, LMCH.ACTIVERESTORE, SOE_TAG, PAO.PAONAME "+
				" FROM LMCONTROLHISTORY LMCH, YUKONPAOBJECT PAO " + 
				" WHERE LMCH.PAOBJECTID = PAO.PAOBJECTID " + 
                " AND LMCH.StartDateTime > ? AND LMCH.StopDateTime <= ? ");
				if(!isShowAllActiveRestore()){
					sql.append(" AND LMCH.ACTIVERESTORE IN ('R', 'T', 'O', 'M') ");
				}
				if(isIgnoreZeroDuration()){
					sql.append(" AND LMCH.ControlDuration > 0 ");
				}
				/*
				 *  Restrict results based on PaoPermissions of LM Groups.
				 */
				List<String> groupIds = new ArrayList<String>();
				for(YukonPao group : restrictedGroups){
					groupIds.add(String.valueOf(group.getPaoIdentifier().getPaoId()));
				}
				String sqlList = SqlStatementBuilder.convertToSqlLikeList(groupIds);
				sql.append(" AND LMCH.PAOBJECTID in (" + sqlList + ")"); 
				
				if( getPaoIDs()!= null){	// null load groups means ALL groups!
					sql.append(" AND (LMCH.PAOBJECTID in (" + getPaoIDs()[0] ); 
					for (Integer paoId :  getPaoIDs()) {
						sql.append(", " + paoId + " ");
					}
					sql.append(") ");
					sql.append("OR LMCH.PAOBJECTID IN "
                    + "(SELECT DISTINCT GM.CHILDID " 
                    + " FROM " + GenericMacro.TABLE_NAME + " GM " 
                    + " WHERE GM.MACROTYPE = '" + MacroTypes.GROUP + "' ");
					sql.append(" AND OWNERID in (" + getPaoIDs()[0] ); 
					for (Integer paoId :  getPaoIDs()) {
						sql.append(", " + paoId + " ");
					}
					sql.append(") "); 
					sql.append(") ) ");
				}
				sql.append(" ORDER BY LMCH.PAOBJECTID, LMCH.StartDateTime, LMCTRLHISTID ");
				if (!isShowAllActiveRestore()){
					sql.append(" DESC ");  // order desc so we can parse for the most recent soe_tag per control
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime()));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()));
				CTILogger.info("START DATE > " + getStartDate() + "  -  STOP DATE <= " + getStopDate());
				
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
				}
				if(!getData().isEmpty())
				{
					Collections.sort(getData(), lmControlHistoryPAONameComparator);
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
		if( o instanceof TempLMControlHistory)
		{
			TempLMControlHistory tempLmch= ((TempLMControlHistory)o);
            LMControlHistory lmch = tempLmch.getLMControlHistory();
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return tempLmch.getDeviceName();
				case CONTROL_DATE_COLUMN:
					return lmch.getStartDateTime();
				case CONTROL_START_TIME_COLUMN:
					return lmch.getStartDateTime();
				case CONTROL_STOP_TIME_COLUMN:
					return lmch.getStopDateTime();
				case CONTROL_DURATION_COLUMN:
					return TimeUtil.convertSecondsToTimeString(lmch.getControlDuration().doubleValue());
				case ACTIVE_RESTORE_COLUMN:
					return getActiveRestoreString(lmch.getActiveRestore());
				case CONTROL_TYPE_COLUMN:
					return lmch.getControlType();
				case DAILY_CONTROL_COLUMN:
					return TimeUtil.convertSecondsToTimeString(lmch.getCurrentDailyTime().doubleValue());
				case MONTHLY_CONTROL_COLUMN:
					return TimeUtil.convertSecondsToTimeString(lmch.getCurrentMonthlyTime().doubleValue());
				case SEASONAL_CONTROL_COLUMN:
					return TimeUtil.convertSecondsToTimeString(lmch.getCurrentSeasonalTime().doubleValue());
				case ANNUAL_CONTROL_COLUMN:
					return TimeUtil.convertSecondsToTimeString(lmch.getCurrentAnnualTime().doubleValue());
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
				PAO_NAME_STRING,
				CONTROL_DATE_STRING,
				CONTROL_START_STRING,
				CONTROL_STOP_STRING,
				CONTROL_DURATION_STRING,
				ACTIVE_RESTORE_STRING,
				CONTROL_TYPE_STRING,
				DAILY_CONTROL_STRING,
				MONTHLY_CONTROL_STRING,
				SEASONAL_CONTROL_STRING,
				ANNUAL_CONTROL_STRING
			};
		}
		return columnNames;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class<?>[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				String.class,
				java.util.Date.class,
				java.util.Date.class,
				java.util.Date.class,
				String.class,
				String.class,
				String.class,
				String.class,
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
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 100, null),
				new ColumnProperties(0, 1, 60, "MM/dd/yyyy"),
				new ColumnProperties(60, 1, 50, "HH:mm:ss"),
				new ColumnProperties(110, 1, 50, "HH:mm:ss"),
				new ColumnProperties(160, 1, 60, null),
				new ColumnProperties(220, 1, 50, null),
				new ColumnProperties(270, 1, 190, null),
				new ColumnProperties(460, 1, 60, null),
				new ColumnProperties(520, 1, 60, null),
				new ColumnProperties(580, 1, 60, null),
				new ColumnProperties(640, 1, 60, null)
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
	/**
	 * @return
	 */
	public boolean isShowAllActiveRestore()
	{
		return showAllActiveRestore;
	}

	/**
	 * When true - all types
	 * When false - only R, T, M, O types
	 */
	public void setShowAllActiveRestore(boolean showAll)
	{
		showAllActiveRestore = showAll;
	}
	
	/**
	 * @return
	 */
	public boolean isIgnoreZeroDuration()
	{
		return ignoreZeroDuration;
	}

	/**
	 * When true - only duration greater than 0
	 * When false - all control durations
	 */
	public void setIgnoreZeroDuration(boolean ignoreZero)
	{
		ignoreZeroDuration = ignoreZero;
	}

	public boolean isShowAnnualTotal() {
		return showAnnualTotal;
	}

	public void setShowAnnualTotal(boolean showAnnualTotal) {
		this.showAnnualTotal = showAnnualTotal;
	}

	public boolean isShowDailyTotal() {
		return showDailyTotal;
	}

	public void setShowDailyTotal(boolean showDailyTotal) {
		this.showDailyTotal = showDailyTotal;
	}

	public boolean isShowMonthlyTotal() {
		return showMonthlyTotal;
	}

	public void setShowMonthlyTotal(boolean showMonthlyTotal) {
		this.showMonthlyTotal = showMonthlyTotal;
	}

	public boolean isShowSeasonalTotal() {
		return showSeasonalTotal;
	}

	public void setShowSeasonalTotal(boolean showSeasonalTotal) {
		this.showSeasonalTotal = showSeasonalTotal;
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
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Display</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" +ATT_ALL_RESTORE_TYPES + "' value='true'>Show Additional Detail (Multiple entries for one control)" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' checked name='" +ATT_IGNORE_ZERO_DURATION + "' value='true'>Ignore zero(00:00:00.000) Control Duration" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;

		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Show Control Totals</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' checked name='" +ATT_SHOW_DAILY_TOTAL + "' value='true'>Daily" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' checked name='" +ATT_SHOW_MONTHLY_TOTAL + "' value='true'>Monthly" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' checked name='" +ATT_SHOW_SEASONAL_TOTAL + "' value='true'>Seasonal" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' checked name='" +ATT_SHOW_ANNUAL_TOTAL + "' value='true'>Annual" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Sort By Load Group</td>" +LINE_SEPARATOR;
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

			String param = req.getParameter(ATT_ALL_RESTORE_TYPES);
			if( param != null)
				setShowAllActiveRestore(Boolean.valueOf(param).booleanValue());
			else 
				setShowAllActiveRestore(false);
			
			param = req.getParameter(ATT_IGNORE_ZERO_DURATION);
			if( param != null)
				setIgnoreZeroDuration(Boolean.valueOf(param).booleanValue());
			else 
				setIgnoreZeroDuration(false);
			
			param = req.getParameter(ATT_IGNORE_ZERO_DURATION);
			if( param != null)
				setIgnoreZeroDuration(Boolean.valueOf(param).booleanValue());
			else 
				setIgnoreZeroDuration(false);

			param = req.getParameter(ATT_SHOW_DAILY_TOTAL);
			if( param != null)
				setShowDailyTotal(Boolean.valueOf(param).booleanValue());
			else 
				setShowDailyTotal(false);
			param = req.getParameter(ATT_SHOW_MONTHLY_TOTAL);
			if( param != null)
				setShowMonthlyTotal(Boolean.valueOf(param).booleanValue());
			else 
				setShowMonthlyTotal(false);
			param = req.getParameter(ATT_SHOW_SEASONAL_TOTAL);
			if( param != null)
				setShowSeasonalTotal(Boolean.valueOf(param).booleanValue());
			else 
				setShowSeasonalTotal(false);
			param = req.getParameter(ATT_SHOW_ANNUAL_TOTAL);
			if( param != null)
				setShowAnnualTotal(Boolean.valueOf(param).booleanValue());
			else 
				setShowAnnualTotal(false);
			
			setLiteUser(ServletUtil.getYukonUser(req));
		}
	}
    
    /**
     * From yukon-server\COMMON\include\yukon.h
     * define LMAR_NEWCONTROL         "N"             // This is the first entry for any new control.
     * #define LMAR_LOGTIMER           "L"             // This is a timed log entry.  Nothing exciting happened in this interval.
     * #define LMAR_CONT_CONTROL       "C"             // Previous command was repeated extending the current control interval.
     * #define LMAR_TIMED_RESTORE      "T"             // Control terminated based on time set in load group.
     * #define LMAR_MANUAL_RESTORE     "M"             // Control terminated because of an active restore or terminate command being sent.
     * #define LMAR_OVERRIDE_CONTROL   "O"             // Control terminated because a new command of a different nature was sent to this group.
     * #define LMAR_CONTROLACCT_ADJUST "A"             // Control accounting was adjusted by user.
     * #define LMAR_PERIOD_TRANSITION  "P"             // Control was active as we crossed a control history boundary.  This log denotes the last log in the previos interval.
     * #define LMAR_DISPATCH_SHUTDOWN  "S"             // Control was active as dispatch shutdown.  This entry will be used to resume control.
     * @param restore
     * @return
     */
	public String getActiveRestoreString(String restore)
	{
	    if( restore.equalsIgnoreCase("M"))
	        return "Control End - Restore/Terminate Command";
	    else if (restore.equalsIgnoreCase("0"))
	        return "Control End - New Command Issued";
	    else if (restore.equalsIgnoreCase("N"))
	        return "New Control";
	    else if (restore.equalsIgnoreCase("T"))
	        return "Control End - LoadGroup Time";
	    else if (restore.equalsIgnoreCase("A"))
	        return "Adjusted By User";
        else if (restore.equalsIgnoreCase("C"))
            return "Extending Conmand";
        else if (restore.equalsIgnoreCase("L"))
            return "Timed Log Entry";
        else if (restore.equalsIgnoreCase("P"))
            return "Last Log in Prev Interval";
        else if (restore.equalsIgnoreCase("S"))
            return "Dispatch Shutdown";
	        
	    return restore;
	    
	}
	
	public LiteYukonUser getLiteUser() {
        return liteUser;
    }
    public void setLiteUser(LiteYukonUser liteUser) {
        this.liteUser = liteUser;
    }
}
	
