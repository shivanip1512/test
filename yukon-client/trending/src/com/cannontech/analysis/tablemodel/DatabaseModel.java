package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.data.device.Carrier;

/**
 * Created on Dec 15, 2003
 * @author snebben
 * DatabaseModel TableModel object
 * Innerclass object for row data is CarrierData:
 *  String paoName			- YukonPaobject.paoName (device)
 *  String paoType			- YukonPaobject.type
 *  String address			- DeviceCarrierSettings.address
 *  String routeName		- YukonPaobject.paoName (route)
 *  String collGroup		- DeviceMeterGroup.collectionGroup
 *  String testCollGroup	- DeviceMeterGroup.testCollectionGroup
 */
public class DatabaseModel extends ReportModelBase
{
	/** Class fields */
	private String paoClass = null;
	
	/**
	 * Default Constructor
	 */
	public DatabaseModel()
	{
		this("CARRIER");
	}	
	/**
	 * Constructor.
	 * @param paoClass_ = YukonPaobject.paoClass
	 */
	public DatabaseModel(String paoClass_)
	{
		super();
		setPaoClass(paoClass_);
	}	
	
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String paoName = rset.getString(1);
			String paoType = rset.getString(2);
			String address = rset.getString(3);			
			String routeName = rset.getString(4);
			String collGroup = rset.getString(5);
			String testCollGroup = rset.getString(6);		
					
			Carrier carrier = new Carrier(paoName, paoType, address, routeName, collGroup, testCollGroup);
			data.add(carrier);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT PAO1.PAONAME MCT, PAO1.TYPE, DCS.ADDRESS, PAO2.PAONAME ROUTE, COLLECTIONGROUP, TESTCOLLECTIONGROUP " + 
			" FROM YUKONPAOBJECT PAO1, YUKONPAOBJECT PAO2, DEVICEROUTES DR, DEVICECARRIERSETTINGS DCS, DEVICEMETERGROUP DMG "+
			" WHERE PAO1.PAOBJECTID = DMG.DEVICEID "+
			" AND PAO1.PAOBJECTID = DR.DEVICEID " + 
			" AND PAO2.PAOBJECTID = DR.ROUTEID " +
			" AND PAO1.PAOBJECTID = DCS.DEVICEID ");
			if( getPaoClass() != null ) 
				sql.append(" AND PAO1.PAOCLASS = '" + getPaoClass() + "' ");
			sql.append(" ORDER BY PAO1.PAONAME" );
		return sql;
	}
		
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		int rowCount = 0;
		
		StringBuffer sql = buildSQLStatement();
	
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
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + data.size());
		return;
	}
		
	/**
	 * Return the paoclass
	 * @return String paoClass
	 */
	private String getPaoClass()
	{
		return paoClass;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return format.format(new java.util.Date());
	}
	/**
	 * Set the paoClass (YukonPaobject.paoClass)
	 * @param string paoClass_
	 */
	public void setPaoClass(String paoClass_)
	{
		paoClass = paoClass_;
	}

}
