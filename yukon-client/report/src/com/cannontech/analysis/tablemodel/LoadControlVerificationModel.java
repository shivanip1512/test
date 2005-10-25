package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DynamicVerification;
import com.cannontech.database.model.ModelFactory;

/**
 * Created on Oct 20, 2005
 * @author snebben
 */
public class LoadControlVerificationModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;
	
	/** Enum values for column representation */
	public final static int DATE_COLUMN = 0;
	public final static int TIME_COLUMN = 1;
	public final static int RECEIVER_NAME_COLUMN = 2;
	public final static int TRANSMITTER_NAME_COLUMN = 3;
	public final static int CODE_SEQUENCE_COLUMN = 4;	
	public final static int RECEIVED_STATUS_COLUMN = 5;
	public final static int CODE_STATUS_COLUMN = 6;
	public final static int CODE_COLUMN = 7;
	public final static int COMMAND_COLUMN = 8;
	
	/** String values for column representation */
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String RECEIVER_NAME_STRING = "Receiver";
	public final static String TRANSMITTER_NAME_STRING = "Transmitter";
	public final static String COMMAND_STRING = "Command";
	public final static String CODE_STRING = "Code";
	public final static String CODE_SEQUENCE_STRING = "Sequence";
	public final static String RECEIVED_STATUS_STRING = "Received";
	public final static String CODE_STATUS_STRING = "Status";
	
	/** A string for the title of the data */
	private static String title = "Load Control Verification";
	
	private Integer receiverID = null;
	private Integer transmitterID = null;

	private String command = null;
	private String code = null;
	
	//	servlet attributes/parameter strings
	protected static final String ATT_RECEIVER_ID = "receiverID";
	protected static final String ATT_TRANSMITTER_ID = "transmitterID";
	protected static final String ATT_LC_COMMAND = "lcCommand";
	protected static final String ATT_LC_CODE = "lcCode";
	/**
	 * Constructor class
	 * @param startTime_ DYNAMICVERIFICATION.dateTime
	 * @param stopTime_ DYNAMICVERIFICATION.dateTime
	 */
	public LoadControlVerificationModel(Date start_, Date stop_)
	{
		this(start_, stop_, null, null, null, null);
	}	
	/**
	 * Constructor class
	 * @param startTime_ DYNAMICVERIFICATION.dateTime
	 * @param stopTime_ DYNAMICVERIFICATION.dateTime
	 */
	public LoadControlVerificationModel(Date start_, Date stop_, Integer receiverID_, Integer transID_, String command_, String code_)
	{
		super(start_, stop_);
		setReceiverID(receiverID_);
		setTransmitterID(transID_);
		setCommand(command_);
		setCode(code_);
	}
	/**
	 * Constructor class
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public LoadControlVerificationModel()
	{
		super();
	}	

	/**
	 * Add SystemLog objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			Integer logID = new Integer(rset.getInt(1));
			java.sql.Timestamp dateTime = rset.getTimestamp(2);
			Integer receiverID = new Integer(rset.getInt(3));
			Integer transID = new Integer(rset.getInt(4));
			String command = rset.getString(5);
			String code = rset.getString(6);
			Integer codesequence = new Integer(rset.getInt(7));
			Character received = new Character(rset.getString(8).charAt(0));
			String codeStatus = rset.getString(9);

			Date dt = new Date(dateTime.getTime());
			DynamicVerification dv = new DynamicVerification(logID, dt, receiverID, transID, command, code, codesequence, received, codeStatus);
 
			getData().add(dv);
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
		StringBuffer sql = new StringBuffer("SELECT LOGID, TIMEARRIVAL, RECEIVERID, TRANSMITTERID, COMMAND, CODE, CODESEQUENCE, RECEIVED, CODESTATUS "+ 
			" FROM DYNAMICVERIFICATION ");
			sql.append(" WHERE (TIMEARRIVAL > ?) AND (TIMEARRIVAL <= ?)");
			
			if( getTransmitterID() != null)
				sql.append(" AND TRANSMITTERID = " + getTransmitterID().intValue());
			else if ( getReceiverID() != null)
				sql.append(" AND RECEIVERID = " + getReceiverID().intValue());
			
			if( getCommand() != null)
				sql.append(" AND COMMAND LIKE '%" + getCommand() + "%'" );
			if( getCode() != null)
				sql.append(" AND CODE LIKE '%" + getCode() + "%'" );

			sql.append(" ORDER BY CODESEQUENCE, TIMEARRIVAL ");
			if( getSortOrder() == DESCENDING )
				sql.append(" DESC " );

		return sql;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
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
			try
			{
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		return getDateFormat().format( getStartDate()) +  "  -  " +
				getDateFormat().format( getStopDate());
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof DynamicVerification)
		{
			DynamicVerification dv = ((DynamicVerification)o);
			switch( columnIndex)
			{
				case DATE_COLUMN:
				{
					//Set the date to the begining of the day so we can "group" by date
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(dv.getTimeArrival());
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					return cal.getTime();
				}
				case TIME_COLUMN:
					return dv.getTimeArrival();
				case RECEIVER_NAME_COLUMN:
					return dv.getReceiverID().intValue() == 0 ? NULL_STRING : PAOFuncs.getYukonPAOName(dv.getReceiverID().intValue());
				case TRANSMITTER_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(dv.getTransmitterID().intValue());
				case COMMAND_COLUMN:
					return dv.getCommand();
				case CODE_COLUMN:
					return dv.getCode();
				case CODE_SEQUENCE_COLUMN:
					return dv.getCodeSequence();
				case RECEIVED_STATUS_COLUMN:
					return dv.getReceived();
				case CODE_STATUS_COLUMN:
					return dv.getCodeStatus();
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
				TIME_STRING,
				RECEIVER_NAME_STRING,
				TRANSMITTER_NAME_STRING,
				CODE_SEQUENCE_STRING,
				RECEIVED_STATUS_STRING,
				CODE_STATUS_STRING,
				CODE_STRING,
				COMMAND_STRING
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
				String.class,
				Integer.class,
				Character.class,
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
				//posX, posY, width, height, numberFormatString  552w 732h viewable
				new ColumnProperties(0, 1, 100, "MMMMM dd, yyyy"),
				new ColumnProperties(offset, 1, offset+=50, "HH:mm:ss"),
				new ColumnProperties(offset, 1, offset+=200, null),
				new ColumnProperties(offset, 1, offset+=200, null),
				new ColumnProperties(offset, 1, offset+=50, "#"),
				new ColumnProperties(offset, 1, offset+=50, null),
				new ColumnProperties(offset, 1, offset+=50, null),
				new ColumnProperties(offset, 1, offset+=130, null),
				new ColumnProperties(50, 12, 450, null)		
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
		html += "    <td colspan=3 height='100'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td height='30' align='right' class='TitleHeader'>Command (value contains):&nbsp;</td>" +LINE_SEPARATOR;
		html += "            <td><input type='text' size=50 name='" +ATT_LC_COMMAND + "' class='TitleHeader'>" +LINE_SEPARATOR;
		html += "          <td>" + LINE_SEPARATOR;
		html += "        </tr><tr>" + LINE_SEPARATOR;
		html += "          <td width='160' height='30' align='right' class='TitleHeader'>Code (value contains):&nbsp;</td>" +LINE_SEPARATOR;
		html += "            <td><input type='text' size=50 name='" +ATT_LC_CODE + "' class='TitleHeader'>" +LINE_SEPARATOR;
		html += "          <td>" + LINE_SEPARATOR; 
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Transmitter</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>" +LINE_SEPARATOR;

		html += "            <div id='Div"+ ModelFactory.getModelString(ModelFactory.TRANSMITTER) +"' style='display:true'>" + LINE_SEPARATOR;
		html += "              <select name='" + ATT_TRANSMITTER_ID+ "' size='10' style='width:300px;'>" + LINE_SEPARATOR;

		List objects = getObjectsByModelType(ModelFactory.TRANSMITTER);
		if (objects != null)
		{
			for (int j = 0; j < objects.size(); j++)
			{
				if (objects.get(j) instanceof LiteYukonPAObject)
					html += "          <option value='" + ((LiteYukonPAObject)objects.get(j)).getYukonID() + "'>" + ((LiteYukonPAObject)objects.get(j)).getPaoName() + "</option>" + LINE_SEPARATOR;
			}
		}
		html += "              </select>" + LINE_SEPARATOR;
		html += "            </div>" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Receiver</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>" +LINE_SEPARATOR;

		html += "            <div id='Div"+ ModelFactory.getModelString(ModelFactory.RTU) +"' style='display:true'>" + LINE_SEPARATOR;
		html += "            <select name='" + ATT_RECEIVER_ID+ "' size='10' style='width:300px;'>" + LINE_SEPARATOR;

		objects = getObjectsByModelType(ModelFactory.RTU);
		if (objects != null)
		{
			for (int j = 0; j < objects.size(); j++)
			{
				if (objects.get(j) instanceof LiteYukonPAObject)
					html += "          <option value='" + ((LiteYukonPAObject)objects.get(j)).getYukonID() + "'>" + ((LiteYukonPAObject)objects.get(j)).getPaoName() + "</option>" + LINE_SEPARATOR;
			}
		}
		html += "            </select>" + LINE_SEPARATOR;
		html += "            </div>" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;


		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Order Direction</td>" +LINE_SEPARATOR;
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
	
	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_TRANSMITTER_ID);
			if (param != null)
				setTransmitterID(Integer.valueOf(param));
			else
				setTransmitterID(null);

			param = req.getParameter(ATT_RECEIVER_ID);
			if (param != null)
				setReceiverID(Integer.valueOf(param));
			else
				setReceiverID(null);

			param = req.getParameter(ATT_LC_COMMAND);
			if( param != null && param.length() > 0)
				setCommand(param);	//ALL Of them!
			else
				setCommand(null);
			
			param = req.getParameter(ATT_LC_CODE);
			if( param != null && param.length() > 0)
				setCode(param);
			else 
				setCode(null);
		}
	}
	/**
	 * @return
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @return
	 */
	public String getCommand()
	{
		return command;
	}

	/**
	 * @return
	 */
	public Integer getReceiverID()
	{
		return receiverID;
	}

	/**
	 * @return
	 */
	public Integer getTransmitterID()
	{
		return transmitterID;
	}

	/**
	 * @param string
	 */
	public void setCode(String string)
	{
		code = string;
	}

	/**
	 * @param string
	 */
	public void setCommand(String string)
	{
		command = string;
	}

	/**
	 * @param integer
	 */
	public void setReceiverID(Integer integer)
	{
		receiverID = integer;
	}

	/**
	 * @param integer
	 */
	public void setTransmitterID(Integer integer)
	{
		transmitterID = integer;
	}
	
}
