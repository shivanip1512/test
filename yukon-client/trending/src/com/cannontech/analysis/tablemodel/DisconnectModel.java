package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;


import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.device.Disconnect;

/**
 * Created on Feb 18, 2004
 * DisconnectReportDatabase TableModel object
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * Contains the start and stop times for query information.
 * Contains the devicemetergroup - YukonPaobject.paoname
 * 				deviceName - YukonPaobject.paoname
 * 				pointName - Point.pointname
 * @author bjonasson
 */
public class DisconnectModel extends ReportModelBase
{
	
	/** Rawpointhistory.value critera, null results in current disconnect state? */
	/** valid types are: Current | Open | Closed | History | Current  */
	private String disconnectType = null;

	private String title = null;
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	
	public DisconnectModel()
		{
			this("Current", ReportTypes.DISCONNECT_DATA);//default type		
		}
	
	
	public DisconnectModel(String disconnectType_)
	{
		this(disconnectType_, ReportTypes.DISCONNECT_DATA);//default type		
	}
	/**
	 * Constructor class
	 * @param disconnectType_ Rawpointhistory.value 
	 	 */
	public DisconnectModel(String disconnectType_, int reportType_)
	{
		super();
		setDisconnectType(disconnectType_);
		setReportType(reportType_);		
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		int rowCount = 0;
		
		StringBuffer sql = buildSQLStatement();
		com.cannontech.clientutils.CTILogger.info(sql.toString());
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				com.cannontech.clientutils.CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime() ));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()));
				com.cannontech.clientutils.CTILogger.info("STOP DATE < " + new java.sql.Timestamp(getStopTime()));
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
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}

		 
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT DMG.COLLECTIONGROUP, PAO.PAOName, P.POINTNAME, RPH.TIMESTAMP, RPH.VALUE " +
				" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, RAWPOINTHISTORY RPH" +
				" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
				" AND PAO.PAOBJECTID = P.PAOBJECTID " +
				" AND P.POINTID = RPH.POINTID " +
				" AND P.POINTOFFSET = 1 " +
				" AND P.POINTTYPE = 'STATUS' ");
				
		if(getCollectionGroups() != null)
		{
			sql.append(" AND DMG.COLLECTIONGROUP IN ('" + getCollectionGroups()[0]);
	
			for (int i = 1; i < getCollectionGroups().length; i++)
			{
				sql.append("," + getCollectionGroups()[i]);
			}
			sql.append("')");
		}

		if( getDisconnectType().toString() == "History" )
			sql.append(" AND RPH.TIMESTAMP > ? AND RPH.TIMESTAMP <= ? ");
					
		
		sql.append(" ORDER BY PAO.PAOName");
		return sql;
		
	}

	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String collGrp = rset.getString(1);
			String paoName = rset.getString(2);
			String pointName = rset.getString(3);					
			Timestamp timestamp = rset.getTimestamp(4);
			Float value = new Float(rset.getFloat(5));
			Disconnect disconnect = new Disconnect(collGrp, paoName, pointName, new java.util.Date(timestamp.getTime()), value);

			getData().add(disconnect);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Return the paoClass (YukonPaobject.PaoClass)
	 * @return String paoClass
	 */

	/**
	 * Return the statType (DynamicPaoStatistics.statisticType)
	 * @return String statType
	 */
	public String getDisconnectType()
	{
		return disconnectType;
	}

	/**
	 * Set the statType
	 * Also sets the startTime and stopTime based on the statisticType.
	 * @param String statType_
	 */
	public void setDisconnectType(String disconnectType_)
	{
				
		disconnectType = disconnectType_;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	 public String getDateRangeString()
	 {
		 java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		 return (format.format(new java.util.Date(getStartTime())) + " through " +
		 			(format.format(new java.util.Date(getStopTime()))));
		 
	 }
}
