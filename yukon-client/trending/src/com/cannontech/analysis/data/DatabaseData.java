package com.cannontech.analysis.data;

import java.sql.ResultSet;

/**
 * Created on Dec 15, 2003
 * @author snebben
 * DatabaseData TableModel object
 * Innerclass object for row data is CarrierData:
 *  String paoName			- YukonPaobject.paoName (device)
 *  String paoType			- YukonPaobject.type
 *  String address			- DeviceCarrierSettings.address
 *  String routeName		- YukonPaobject.paoName (route)
 *  String collGroup		- DeviceMeterGroup.collectionGroup
 *  String testCollGroup	- DeviceMeterGroup.testCollectionGroup
 */
public class DatabaseData extends ReportDataBase
{
	/** Class fields */
	private String paoClass = null;
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int PAO_TYPE_COLUMN = 1;
	public final static int ADDRESS_COLUMN = 2;
	public final static int ROUTE_NAME_COLUMN = 3;
	public final static int COLL_GROUP_NAME_COLUMN = 4;
	public final static int TEST_COLL_GROUP_NAME_COLUMN = 5;
	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "MCT Name";
	public final static String PAO_TYPE_STRING = "Type";
	public final static String ADDRESS_STRING  = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String TEST_COLL_GROUP_NAME_STRING = "Alternate Group";

	/** Inner class container of table model data*/
	private class CarrierData
	{
		public String paoName = null;
		public String paoType = null;
		public String address = null;			
		public String routeName = null;
		public String collGroup = null;
		public String testCollGroup = null;
		public CarrierData(String paoName_, String paoType_, String address_, String routeName_, String collGroup_, String testCollGroup_)
		{
			paoName = paoName_;
			paoType = paoType_;
			address = address_;
			routeName = routeName_;
			collGroup = collGroup_;
			testCollGroup = testCollGroup_;
		}		
	}
	
	/**
	 * Default Constructor
	 */
	public DatabaseData()
	{
		this("CARRIER");
	}	
	/**
	 * Constructor.
	 * @param paoClass_ = YukonPaobject.paoClass
	 */
	public DatabaseData(String paoClass_)
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
					
			CarrierData carrierData = new CarrierData(paoName, paoType, address, routeName, collGroup, testCollGroup);
			data.add(carrierData);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve DatabaseData data.
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
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[NUMBER_COLUMNS];
			columnNames[0] = PAO_NAME_STRING;
			columnNames[1] = PAO_TYPE_STRING;
			columnNames[2] = ADDRESS_STRING;
			columnNames[3] = ROUTE_NAME_STRING;
			columnNames[4] = COLL_GROUP_NAME_STRING;
			columnNames[5] = TEST_COLL_GROUP_NAME_STRING;
		}
		return columnNames;
	}
	

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[NUMBER_COLUMNS];
			columnTypes[0] = String.class;
			columnTypes[1] = String.class;
			columnTypes[2] = String.class;
			columnTypes[3] = String.class;
			columnTypes[4] = String.class;
			columnTypes[5] = String.class;
		}
		return columnTypes;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{

		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[NUMBER_COLUMNS];
			columnProperties[0] = new ColumnProperties(0, 1, 175, 20, null);
			columnProperties[1] = new ColumnProperties(175, 1, 75, 20, "#,##0");
			columnProperties[2] = new ColumnProperties(240, 1, 50, 20, "#,##0");
			columnProperties[3] = new ColumnProperties(305, 1, 50, 20, "##0.00%");
			columnProperties[4] = new ColumnProperties(370, 1, 55, 20, "##0.00%");
			columnProperties[5] = new ColumnProperties(425, 1, 55, 20, "##0.00%");
		}
		return columnProperties;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#collectData()
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
	 * @see com.cannontech.analysis.data.ReportDataBase#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof CarrierData)
		{
			CarrierData carrier = ((CarrierData)o); 
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return carrier.paoName;
		
				case PAO_TYPE_COLUMN:
					return carrier.paoType;
	
				case ADDRESS_COLUMN:
					return carrier.address;
	
				case ROUTE_NAME_COLUMN:
					return carrier.routeName;
				
				case COLL_GROUP_NAME_COLUMN:
					return carrier.collGroup;
				
				case TEST_COLL_GROUP_NAME_COLUMN:
					return carrier.testCollGroup;
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getTitleString()
	 */
	public String getTitleString()
	{
		return "Database Report - Carrier";
	}	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getDateRangeString()
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
