package com.cannontech.analysis.tablemodel;

import java.util.Date;
import java.util.HashMap;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.stars.ProgramDetail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteContact;

public class ProgramDetailModel extends ReportModelBase<ProgramDetail>
{
	/** A string for the title of the data */
	private static String title = "PROGRAM STATUS SUMMARY";
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int ENERGY_COMPANY_COLUMN = 0;
	public final static int PROGRAM_NAME_COLUMN = 1;
	public final static int CONTACT_COLUMN = 2;	
	public final static int ACCOUNT_NUMBER_COLUMN = 3;
	public final static int STATUS_COLUMN = 4;
	
	/** String values for column representation */
	public final static String ENERGY_COMPANY_STRING = "Energy Company";
	public final static String PROGRAM_NAME_STRING = "Program";
	public final static String CONTACT_STRING = "Contact";
	public final static String ACCOUNT_NUMBER_STRING = "Account Number";
	public final static String STATUS_STRING = "Status";
	
	/** Class fields */
	private HashMap<String, ProgramDetail> acctProgPairs = null;
	
	/**
	 * Constructor class
	 */
	public ProgramDetailModel(Date stop_)
	{
		//use the stop (max) time for both date entries.
		super(null, stop_);//default type
		setFilterModelTypes(new ReportFilter[]{ReportFilter.PROGRAM_SINGLE_SELECT});
	}

	/**
	 * Constructor class
	 */
	public ProgramDetailModel()
	{
		super();
		setFilterModelTypes(new ReportFilter[]{ReportFilter.PROGRAM_SINGLE_SELECT});
	}

	/**
	 * Constructor class
	 * Only ONE energycompanyID is used 
	 */
	public ProgramDetailModel(Integer ecID_)
	{
		this();//default type
		setEnergyCompanyID(ecID_);
		setFilterModelTypes(new ReportFilter[]{ReportFilter.PROGRAM_SINGLE_SELECT});
	}
	
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);

		if( !isValid()) {
		    return;
		}
		
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

				boolean dataExists = false;
				while( rset.next())
				{
					addDataRow(rset);
					dataExists = true;
				}
				if (dataExists)
					buildAcctProgHashMap();
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
		 
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT AM.ACCOUNTID, LPWP.PROGRAMID VirtualProgID, LPWP.DEVICEID LMProgID, " +
		" AC.DESCRIPTION, YWC.ALTERNATEDISPLAYNAME, CA.CUSTOMERID, CA.ACCOUNTNUMBER, EC.NAME, PAO.PAONAME " +
		" FROM APPLIANCECATEGORY AC, YUKONWEBCONFIGURATION YWC, LMPROGRAMWEBPUBLISHING LPWP, "+
		" ECTOACCOUNTMAPPING AM, ECTOGENERICMAPPING GM, CUSTOMERACCOUNT CA, ENERGYCOMPANY EC, YUKONPAOBJECT PAO " +
		" WHERE LPWP.WEBSETTINGSID = YWC.CONFIGURATIONID "+  
        " AND PAO.PAOBJECTID = LPWP.DEVICEID ");
        
		sql.append(" AND PAO.PAOBJECTID = " + getPaoIDs()[0] );
        
        sql.append(" AND CA.ACCOUNTID = AM.ACCOUNTID ");
        sql.append(" AND LPWP.APPLIANCECATEGORYID = AC.APPLIANCECATEGORYID ");

		if( getEnergyCompanyID() != null)
			sql.append(" AND EC.ENERGYCOMPANYID = " + getEnergyCompanyID().intValue() + " ");

		sql.append(" AND GM.ENERGYCOMPANYID = EC.ENERGYCOMPANYID "+
		" AND GM.ENERGYCOMPANYID = AM.ENERGYCOMPANYID "+
		" AND AC.APPLIANCECATEGORYID = GM.ITEMID "+
		" AND GM.MAPPINGCATEGORY = 'ApplianceCategory' " +
		" ORDER BY EC.NAME, YWC.ALTERNATEDISPLAYNAME, CA.ACCOUNTNUMBER ");
		return sql;
		
	}
	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(java.sql.ResultSet rset)
	{
		try
		{
			Integer acctID = new Integer(rset.getInt(1));
			Integer virtualProgID = new Integer(rset.getInt(2));
			String altDisplayName = rset.getString(5);
			Integer custID = new Integer(rset.getInt(6));
			String acctNum = rset.getString(7);
			String ecName = rset.getString(8);
            if ( altDisplayName.equalsIgnoreCase(",")){
                altDisplayName = rset.getString(9);
            }
			ProgramDetail pd = new ProgramDetail(ecName, altDisplayName, custID, acctNum, acctID, new Integer(-1) );
			getData().add(pd);

			//KEY = "ACCTID_PROGID"
			String acctProgStr = acctID.toString() + "_" + virtualProgID.toString();
			getAcctProgPairs().put(acctProgStr, pd);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof ProgramDetail)
		{
			ProgramDetail pd = ((ProgramDetail)o);			
			switch( columnIndex)
			{
				case ENERGY_COMPANY_COLUMN:
					return pd.getEcName();
				case PROGRAM_NAME_COLUMN:
					return pd.getProgramName();
				case CONTACT_COLUMN:
				{
					LiteContact contact = DaoFactory.getCustomerDao().getPrimaryContact(pd.getCustID().intValue());
					if (contact == null)
						return "(n/a)";

					return (contact.getContLastName() + ", " + contact.getContFirstName());
				}
				case ACCOUNT_NUMBER_COLUMN:
				{
					if (pd.getAcctNumber() == null)
					{
						if( pd.getAcctID().intValue() == -1)
							return "(n/a)";
						else
							return "(deleted)";
					}
					return pd.getAcctNumber();
				}
				case STATUS_COLUMN:
					return pd.getStatus();
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
				ENERGY_COMPANY_STRING,
				PROGRAM_NAME_STRING,
				CONTACT_STRING,
				ACCOUNT_NUMBER_STRING,
				STATUS_STRING
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
				new ColumnProperties(0, 1, 150, null),
				new ColumnProperties(0, 1, 250, null),
				new ColumnProperties(250, 1, 150, null),
				new ColumnProperties(400, 1, 90, null),
				new ColumnProperties(490, 1, 110, null)
			};				
		}
		return columnProperties;
	}
	
	public void buildAcctProgHashMap()
	{
		StringBuffer sql = new StringBuffer("SELECT LPE.ACCOUNTID, LPE.PROGRAMID, YLE.YUKONDEFINITIONID, YLE.ENTRYTEXT "+
		" FROM LMPROGRAMEVENT LPE, LMCUSTOMEREVENTBASE CEB, YUKONLISTENTRY YLE "+
		" WHERE LPE.EVENTID = CEB.EVENTID "+
		" AND YLE.ENTRYID = CEB.ACTIONID " +
		" AND LPE.EVENTID IN "+
		" (SELECT MAX(LPE2.EVENTID) FROM LMPROGRAMEVENT LPE2, LMCUSTOMEREVENTBASE CEB2 "+
		" WHERE CEB2.EVENTID = LPE2.EVENTID AND CEB2.EVENTDATETIME <= ? ");
		
		sql.append(" AND LPE.PROGRAMID = " + getPaoIDs()[0] );
		
        sql.append(" GROUP BY ACCOUNTID, PROGRAMID )" +
		" ORDER BY LPE.ACCOUNTID, LPE.PROGRAMID");
		
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStopDate().getTime() ));
				CTILogger.info("MAX STOP DATE <= " + getStopDate());
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					Integer acctID = new Integer(rset.getInt(1));
					Integer virtualProgID = new Integer(rset.getInt(2));
					Integer actionID = new Integer(rset.getInt(3));
					String entryText = rset.getString(4);
					
					//Key = "AcctID_ProgID"
					String acctProgStr = acctID.toString()+ "_" + virtualProgID.toString(); 
					ProgramDetail pd = (ProgramDetail)getAcctProgPairs().get(acctProgStr);
					if( pd != null)
					{
						pd.setAction(actionID);
						pd.setStatus(entryText);
					}
						
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
	
	public String getHTMLOptionsTable() {
        String html = "<span style='font-weight: bold;'> * You must select only one program.</span>";
        return html;
    }
	
	/**
	 * @return
	 */
	public HashMap<String, ProgramDetail> getAcctProgPairs()
	{
		if( acctProgPairs == null)
			acctProgPairs = new HashMap<String, ProgramDetail>(10);
		return acctProgPairs;
	}

	/**
	 * Return true if all parameters used for collecting data are available, else return false.
	 * @return
	 */
	private boolean isValid() {
        if (getPaoIDs() == null || getPaoIDs().length < 1) {
            return false;
        }
        return true;
	}
	
	@Override
	public String getDateRangeString()
	{
		return (getDateFormat().format(getStopDate()));
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
