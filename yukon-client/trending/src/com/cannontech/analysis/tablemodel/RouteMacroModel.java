package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.route.CarrierRouteMacro;;

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
public class RouteMacroModel extends ReportModelBase
{
	/** Class fields */
	private String paoClass = null;
	
	/**
	 * Default Constructor
	 */
	public RouteMacroModel()
	{
		this("CARRIER", ReportTypes.ROUTE_MACRO_DATA);
	}	
	/**
	 * Constructor.
	 * @param paoClass_ = YukonPaobject.paoClass
	 */
	public RouteMacroModel(String paoClass_)
	{
		this(paoClass_, ReportTypes.ROUTE_MACRO_DATA);
	}	

	/**
	 * Constructor.
	 * @param paoClass_ = YukonPaobject.paoClass
	 */
	public RouteMacroModel(String paoClass_, int reportType_)
	{
		super();
		setPaoClass(paoClass_);
		setReportType(reportType_);
	}	
		
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String macroRouteName = rset.getString(1);
			String routeName = rset.getString(2);
			String transmitterName = rset.getString(3);			
			String ccuBus = rset.getString(4);
			String ampUse = rset.getString(5);
			String fixedBits = rset.getString(6);
			String variableBits = rset.getString(7);
			String defaultRoute = rset.getString(8);		
					
			CarrierRouteMacro carrierRouteMacro = new CarrierRouteMacro(macroRouteName, routeName, transmitterName, ccuBus, ampUse, fixedBits,variableBits,defaultRoute);
			getData().add(carrierRouteMacro);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve RouteMacroModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("select pao1.paoname as macro, pao3.paoname as transmitter,pao2.paoname as route, " + 
		" carrier.busnumber, DIDLCR.ccuampusetype, carrier.ccufixbits,carrier.ccuvariablebits,route.defaultroute " + 
		" from yukonpaobject pao1, macroroute macro,yukonpaobject pao2,carrierroute carrier,route,yukonpaobject pao3,deviceidlcremote DIDLCR " +
		" where pao1.paobjectid=macro.routeid and macro.routeid=32 and pao2.paobjectid=macro.singlerouteid " +
		" and carrier.routeid=macro.singlerouteid and macro.singlerouteid=route.routeid and pao3.paobjectid=route.deviceid and " +
		" pao3.paobjectid=didlcr.deviceid");
			
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
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + getData().size());
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
