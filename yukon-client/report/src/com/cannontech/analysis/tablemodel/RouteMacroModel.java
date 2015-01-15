package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.CarrierRouteMacro;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public class RouteMacroModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;
	
	/** Enum values for column representation */
	public final static int MACRO_ROUTE_NAME_COLUMN = 0;
	public final static int ROUTE_NAME_COLUMN = 1;
	public final static int TRANSMITTER_NAME_COLUMN = 2;
	public final static int CCU_BUS_NUMBER_COLUMN = 3;
	public final static int AMP_USE_COLUMN = 4;
	public final static int FIXED_BITS_COLUMN = 5;
	public final static int VARIABLE_BITS_COLUMN = 6;
	public final static int DEFAULT_ROUTE_COLUMN = 7;
	
	/** String values for column representation */
	public final static String MACRO_ROUTE_NAME_STRING = "Route Macro Name";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String TRANSMITTER_NAME_STRING  = "Transmitter Name";
	public final static String CCU_BUS_NUMBER_STRING = "CCU Bus";
	public final static String AMP_USE_STRING = "AMP Use";
	public final static String FIXED_BITS_STRING = "Fixed Bits";
	public final static String VARIABLE_BITS_STRING = "Variable Bits";
	public final static String DEFAULT_ROUTE_STRING = "Default Route";

	/** A string for the title of the data */
	private static String title = "Database Report - Route Macro";

	/** Class fields */
//	private String paoClass = null;
	
	private Integer macroRouteID = null; 
	
	/**
	 * Constructor.
	 * @param paoClass_ = YukonPaobject.paoClass
	 */
	public RouteMacroModel()
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
			String macroRouteName = rset.getString(1);
			String transmitterName = rset.getString(2);
			String routeName = rset.getString(3);
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
		StringBuffer sql = new StringBuffer	("SELECT MPAO.PAONAME MACRO, TPAO.PAONAME TRANS, RPAO.PAONAME ROUTE, " + 
			" CR.BUSNUMBER, DIDLCR.CCUAMPUSETYPE, CR.CCUFIXBITS, CR.CCUVARIABLEBITS, R.DEFAULTROUTE " + 
			" FROM YUKONPAOBJECT MPAO, MACROROUTE MR, YUKONPAOBJECT RPAO, CARRIERROUTE CR , "+
			" ROUTE R, YUKONPAOBJECT TPAO, DEVICEIDLCREMOTE DIDLCR " +
			" WHERE MPAO.PAOBJECTID = MR.ROUTEID ");
		
		if( getMacroRouteID() != null)
			sql.append(" AND MR.ROUTEID = " + getMacroRouteID());
		
		sql.append(	" AND RPAO.PAOBJECTID = MR.SINGLEROUTEID " +
			" AND CR.ROUTEID = MR.SINGLEROUTEID " +
			" AND MR.SINGLEROUTEID = R.ROUTEID " + 
			" AND TPAO.PAOBJECTID = R.DEVICEID " +
			" AND TPAO.PAOBJECTID = DIDLCR.DEVICEID");
			
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

	@Override
	public String getDateRangeString()
	{
		return "Run Date: " + getDateFormat().format(new java.util.Date());
	}

	/**
	 * @return
	 */
	public Integer getMacroRouteID()
	{
		return macroRouteID;
	}

	/**
	 * @param integer
	 */
	public void setMacroRouteID(Integer integer)
	{
		macroRouteID = integer;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	@Override
    public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof CarrierRouteMacro)
		{
			CarrierRouteMacro carrierRouteMacro = ((CarrierRouteMacro)o); 
			switch( columnIndex)
			{
				case MACRO_ROUTE_NAME_COLUMN:
					return carrierRouteMacro.getRouteMacroName();
		
				case ROUTE_NAME_COLUMN:
					return carrierRouteMacro.getRouteName();
	
				case TRANSMITTER_NAME_COLUMN:
					return carrierRouteMacro.getTransmitterName();
	
				case CCU_BUS_NUMBER_COLUMN:
					return carrierRouteMacro.getCcuBusNumber();
				
				case AMP_USE_COLUMN:
					return carrierRouteMacro.getAmpUse();
				
				case FIXED_BITS_COLUMN:
					return carrierRouteMacro.getFixedBits();
					
				case VARIABLE_BITS_COLUMN:
					return carrierRouteMacro.getVariableBits();
					
				case DEFAULT_ROUTE_COLUMN:
					return carrierRouteMacro.getDefaultRoute();
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
				MACRO_ROUTE_NAME_STRING,
				ROUTE_NAME_STRING,
				TRANSMITTER_NAME_STRING,
				CCU_BUS_NUMBER_STRING,
				AMP_USE_STRING,
				FIXED_BITS_STRING,
				VARIABLE_BITS_STRING,
				DEFAULT_ROUTE_STRING,
			};
		}
		return columnNames;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	@Override
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
			columnProperties = new ColumnProperties[]
			{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 130, null),
				new ColumnProperties(0, 1, 130, null),
				new ColumnProperties(130, 1, 125, null),
				new ColumnProperties(255, 1, 55, null),
				new ColumnProperties(310, 1, 55, null),
				new ColumnProperties(365, 1, 55, null),
				new ColumnProperties(420, 1, 65, null),
				new ColumnProperties(485, 1, 120, null)
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
	public boolean useStartDate()
	{
		return false;
	}
	@Override
	public boolean useStopDate()
	{
		return false;
	}

}
