package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.device.MissedMeter;

/**
 * Created on Dec 15, 2003
 * MissedMeterData TableModel object
 * Innerclass object for row data is MissedMeter:
 *  String collGroup	- DeviceMeterGroup.collectionGroup 
 *  String deviceName	- YukonPaobject.paoName
 *  String pointName	- Point.pointName
 *  Integer pointID		- Point.pointID
 *  String routeName  
 * @author snebben
 */
public class MissedMeterModel extends ReportModelBase
{
	/** Class fields */

	/**
	 * 
	 */
	public MissedMeterModel()
	{
		this(ReportTypes.MISSED_METER_DATA);
	}
	
	/**
	 * 
	 */
	public MissedMeterModel(int reportType_)
	{
		super();
		setReportType(ReportTypes.MISSED_METER_DATA);
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String collGrp = rset.getString(1);
			String paoName = rset.getString(2);
			String pointName = rset.getString(3);					
			Integer pointID = new Integer(rset.getInt(4));
			String routeName = rset.getString(5);
			MissedMeter missedMeter = new MissedMeter(collGrp, paoName, pointName, pointID, routeName);

			getData().add(missedMeter);
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
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME, P.POINTID, " + 
			" PAO2.PAONAME ROUTENAME " +
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, YUKONPAOBJECT PAO2, DEVICEROUTES DR, POINTUNIT PU, RAWPOINTHISTORY RPH, "+
			" UNITMEASURE UM " +
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
			" AND PAO.PAOBJECTID = DR.DEVICEID " +
			" AND P.POINTID = PU.POINTID " +
			" AND  PU.UOMID = UM.UOMID " +
			" AND UM.FORMULA ='USAGE' " +
			" AND PAO2.PAOBJECTID = DR.ROUTEID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID ");
			
		if(getCollectionGroups() != null)
				{
					sql.append(" AND DMG.COLLECTIONGROUP IN ('" + getCollectionGroups()[0]);
	
					for (int i = 1; i < getCollectionGroups().length; i++)
					{
						sql.append("','" + getCollectionGroups()[i]);
					}
					sql.append("')");
				}
			 
			sql.append(" AND P.POINTID !=  RPH.POINTID  AND TIMESTAMP > ? " +
			" AND PAO.PAOCLASS = 'CARRIER' " +
			" ORDER BY DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME");
		return sql;
	
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
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()));
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
}
