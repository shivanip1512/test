package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.model.CBCOrderByTreeModel;

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
public class CapBankListModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int CB_NAME_COLUMN = 0;
	public final static int CB_ADDRESS_COLUMN = 1;
	public final static int CB_SIZE_COLUMN = 2;
	public final static int CBC_NAME_COLUMN = 3;
	public final static int CB_SERIAL_COLUMN = 4;
	public final static int FEEDER_NAME_COLUMN = 5;
	public final static int SUB_NAME_COLUMN = 6;
	
	/** String values for column representation */
	public final static String CB_NAME_STRING = "Bank Name";
	public final static String ADDRESS_STRING  = "Address";
	public final static String CB_SIZE_STRING = "Size";
	public final static String CBC_NAME_STRING = "CBC Name";
	public final static String CB_SERIAL_NAME_STRING = "Serial #";
	public final static String FEEDER_NAME_STRING = "Feeder";
	public final static String SUB_NAME_STRING = "Sub";

	private String orderBy = null;

	/** A string for the title of the data */
	private static String title = "Capacitor Bank Report";
		
	/**
	 * Constructor.
	 * @param paoClass_ = YukonPaobject.paoClass
	 */
	public CapBankListModel()
	{
		super();
	}	
		
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			Object[] values = new Object[]
			{
				(rset.getString(1) == null ? "---" : rset.getString(1)),
				(rset.getString(2) == null ? "---" : rset.getString(2)),
				(rset.getString(3) == null ? "---" : rset.getString(3)),
				(rset.getString(4) == null ? "---" : rset.getString(4)),
				(rset.getString(5) == null ? "---" : rset.getString(5)),
				(rset.getString(6) == null ? "---" : rset.getString(6)),
				(rset.getString(7) == null ? "---" : rset.getString(7))
			};
					
			getData().add(values);
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
		StringBuffer sql = new StringBuffer	(
			"select y.paoname, y.description, c.banksize, cbcy.paoname, cbcd.serialnumber, yfdr.paoname, ysub.paoname " +
			"from " +
			"(((capbank c join yukonpaobject y on c.deviceid = y.paobjectid) left outer join " +
			"(yukonpaobject yfdr join ccfeederbanklist fdrl on yfdr.paobjectid = fdrl.feederid) on c.deviceid = fdrl.deviceid) " +
			"left outer join " +
			"(devicecbc cbcd join yukonpaobject cbcy on cbcd.deviceid = cbcy.paobjectid) on c.controldeviceid =cbcd.deviceid) " +
			"left outer join " +
			"(yukonpaobject ysub join ccfeedersubassignment subl on ysub.paobjectid = subl.substationbusid) " +
			"on subl.feederid = fdrl.feederid" );
			
		if( CBCOrderByTreeModel.ORDER_TYPE_STRINGS[1].equals(getOrderBy()) )
			sql.append(" order by yfdr.paoname" );	
		else if( CBCOrderByTreeModel.ORDER_TYPE_STRINGS[2].equals(getOrderBy()) )
			sql.append(" order by ysub.paoname" );	
		else if( CBCOrderByTreeModel.ORDER_TYPE_STRINGS[3].equals(getOrderBy()) )
			sql.append(" order by c.banksize" );	
		else
			sql.append(" order by y.paoname" );

		return sql;
	}
		
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
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
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return format.format(new java.util.Date());
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof Object[] )
		{
			Object[] vals = (Object[])o;
			if( columnIndex >= 0 && columnIndex < NUMBER_COLUMNS )
				return vals[columnIndex];
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
				CB_NAME_STRING,
				ADDRESS_STRING,
				CB_SIZE_STRING,
				CBC_NAME_STRING,
				CB_SERIAL_NAME_STRING,
				FEEDER_NAME_STRING,
				SUB_NAME_STRING
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
			int offset = 0;
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(offset, 1, offset+=90, null),
				new ColumnProperties(offset, 1, offset+=120, null),
				new ColumnProperties(offset, 1, offset+=40, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=40, null),
				new ColumnProperties(offset, 1, offset+=90, null),
				new ColumnProperties(offset, 1, offset+=90, null)
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
	 * @return Returns the orderBy.
	 */
	public String getOrderBy() {
		return orderBy;
	}
	/**
	 * @param orderBy The orderBy to set.
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
}
