package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public class CapBankListModel extends FilterObjectsReportModelBase<Object>
{
	public static final String[] ORDER_TYPE_STRINGS = {
		"Order by Cap Bank",
		"Order by Feeder",
		"Order by Substation Bus",
		"Order by Size"
	};	

	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;
	
	/** Enum values for column representation */
	public final static int CB_NAME_COLUMN = 0;
	public final static int CB_ADDRESS_COLUMN = 1;
	public final static int CB_SIZE_COLUMN = 2;
	public final static int CBC_NAME_COLUMN = 3;
	public final static int CBC_TYPE_COLUMN = 4;
	public final static int CB_SERIAL_COLUMN = 5;
	public final static int FEEDER_NAME_COLUMN = 6;
	public final static int SUB_NAME_COLUMN = 7;
	
	/** String values for column representation */
	public final static String CB_NAME_STRING = "Cap Bank";
	public final static String ADDRESS_STRING  = "Address";
	public final static String CB_SIZE_STRING = "Cap Bank Size";
	public final static String CBC_NAME_STRING = "CBC";
	public final static String CBC_TYPE_STRING = "Type";
	public final static String CB_SERIAL_NAME_STRING = "Serial #";
	public final static String FEEDER_NAME_STRING = "Feeder";
	public final static String SUB_NAME_STRING = "Substation Bus";

	private String orderBy = null;

	protected static final String ATT_ORDER_BY = "orderBy";

	/** A string for the title of the data */
	private static String title = "Cap Bank Details Report";
		
	/**
	 * Constructor.
	 * @param paoClass_ = YukonPaobject.paoClass
	 */
	public CapBankListModel()
	{
		super();
        setFilterModelTypes(new ReportFilter[]{
                ReportFilter.AREA,
                ReportFilter.CAPCONTROLSUBSTATION,
                ReportFilter.CAPCONTROLSUBBUS,
                ReportFilter.CAPCONTROLFEEDER,
         	 	ReportFilter.CAPBANK}
            );
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
				(rset.getString(7) == null ? "---" : rset.getString(7)),
				(rset.getString(8) == null ? "---" : rset.getString(8))
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
			
			"select y.paoname, y.description, c.banksize, cbcy.paoname, cbcy.type, cbcd.serialnumber, yfdr.paoname, ysubbus.paoname " + 
			"from " +
			"(((capbank c join yukonpaobject y on c.deviceid = y.paobjectid) left outer join  " +
			"(yukonpaobject yfdr join ccfeederbanklist fdrl on yfdr.paobjectid = fdrl.feederid) on c.deviceid = fdrl.deviceid) " +
			"left outer join " +
			"(devicecbc cbcd join yukonpaobject cbcy on cbcd.deviceid = cbcy.paobjectid) on c.controldeviceid =cbcd.deviceid) " +
			"left outer join " +
			"(yukonpaobject ysubbus join ccfeedersubassignment subbusl on ysubbus.paobjectid = subbusl.substationbusid) " +
			"on subbusl.feederid = fdrl.feederid " +
			"left outer join " +
			"(yukonpaobject ysubstation join ccsubstationsubbuslist ssb on ysubstation.paobjectid = ssb.substationid) " +
			"on ssb.substationbusid = subbusl.substationbusid " +
			"left outer join " +
			"(yukonpaobject ca join ccsubareaassignment saa on ca.paobjectid = saa.areaid) on saa.substationbusid = ssb.substationid ");
			
        if (getPaoIDs() != null && getPaoIDs().length > 0)
        {
			if (getFilterModelType().equals(ReportFilter.AREA)) {
				sql.append(" WHERE ca.paobjectid IN ( " + getPaoIDs()[0] + " ");
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(" , " + getPaoIDs()[i]);

				sql.append(")");
			} else if (getFilterModelType().equals(ReportFilter.CAPBANK)) {
				sql.append(" WHERE y.paobjectid IN ( " + getPaoIDs()[0] + " ");
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(" , " + getPaoIDs()[i]);

				sql.append(")");
			} else if (getFilterModelType().equals(
					ReportFilter.CAPCONTROLFEEDER)) {
				sql.append(" WHERE yfdr.paobjectid IN ( " + getPaoIDs()[0]
						+ " ");
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(" , " + getPaoIDs()[i]);

				sql.append(")");
			} else if (getFilterModelType().equals(
					ReportFilter.CAPCONTROLSUBBUS)) {
				sql.append(" WHERE subbusl.substationbusid IN ( " + getPaoIDs()[0]
						+ " ");
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(" , " + getPaoIDs()[i]);

				sql.append(")");
			} else if (getFilterModelType().equals(
                    ReportFilter.CAPCONTROLSUBSTATION)) {
                sql.append(" WHERE ssb.substationid IN ( " + getPaoIDs()[0]
                        + " ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);

                sql.append(")");
            }
		}
        
		if( ORDER_TYPE_STRINGS[1].equals(getOrderBy()) )
			sql.append(" order by yfdr.paoname" );	
		else if( ORDER_TYPE_STRINGS[2].equals(getOrderBy()) )
			sql.append(" order by ysubbus.paoname" );	
		else if( ORDER_TYPE_STRINGS[3].equals(getOrderBy()) )
			sql.append(" order by c.banksize" );	
		else
			sql.append(" order by y.paoname" );

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
		return getDateFormat().format(new java.util.Date());
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
				CBC_TYPE_STRING,
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
				new ColumnProperties(0, 1, 75, null),
				new ColumnProperties(75, 1, 120, null),
				new ColumnProperties(195, 1, 75, null),
				new ColumnProperties(270, 1, 110, null),
				new ColumnProperties(380, 1, 80, null),
				new ColumnProperties(460, 1, 60, null),
				new ColumnProperties(520, 1, 100, null),
				new ColumnProperties(620, 1, 100, null)
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
	
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Order By</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for( int i = 0; i < ORDER_TYPE_STRINGS.length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" + ATT_ORDER_BY +"' value='" + ORDER_TYPE_STRINGS[i] + "'>" + ORDER_TYPE_STRINGS[i] + LINE_SEPARATOR;
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
			String param = req.getParameter(ATT_ORDER_BY);
			setOrderBy(param);
		}
	}
	
	@Override
	public boolean useStartDate()
	{
		return false;	}

	@Override
	public boolean useStopDate()
	{
		return false;
	}
}
