package com.cannontech.analysis.tablemodel;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.stars.ProgramDetail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.energyCompany.EcMappingCategory;

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
		
		SqlStatementBuilder sql = buildSQLStatement();
		CTILogger.info(sql.toString());

		YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
		yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
			
			@Override
			public void processRow(YukonResultSet rs) throws SQLException {
				Integer accountId = rs.getInt("AccountId");
				Integer assignedProgramId = rs.getInt("ProgramId");
				String altDisplayName = rs.getString("AlternateDisplayName");
				Integer customerId = rs.getInt("CustomerId");
				String accountNumber = rs.getString("AccountNumber");
				String ecName = rs.getString("Name");
	            if (altDisplayName.equalsIgnoreCase(",")) {
	            	// If alt name not provided, default to the LMProgram (PAO) name. 
	                altDisplayName = rs.getString("PaoName");
	            }
	            
				ProgramDetail pd = new ProgramDetail(ecName, altDisplayName, customerId, accountNumber, accountId, -1 );
				getData().add(pd);

				//KEY = "ACCTID_PROGID"
				String key = buildLookupKey(accountId, assignedProgramId);
				getAcctProgPairs().put(key, pd);
        	}
		});

		if (!getData().isEmpty()) {
			buildAcctProgHashMap();
		}
		
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
		 
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public SqlStatementBuilder buildSQLStatement()
	{
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT AM.AccountId, LPWP.ProgramId, LPWP.DeviceId,");
		sql.append(    "AC.Description, YWC.AlternateDisplayName, CA.CustomerId, CA.AccountNumber, EC.Name, PAO.PaoName");
		sql.append("FROM LmProgramWebPublishing LPWP");
		sql.append(    "JOIN YukonWebConfiguration YWC ON LPWP.WebSettingsId = YWC.ConfigurationId");
		sql.append(    "JOIN YukonPaobject PAO ON PAO.PaobjectId = LPWP.DeviceId");
		sql.append(    "JOIN ApplianceCategory AC ON AC.ApplianceCategoryId = LPWP.ApplianceCategoryId");
		sql.append(    "JOIN EcToGenericMapping GM ON GM.ItemId = AC.ApplianceCategoryId AND GM.MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
		sql.append(    "JOIN EcToAccountMapping AM ON AM.EnergyCompanyId = GM.EnergyCompanyId");
		sql.append(    "JOIN CustomerAccount CA ON CA.AccountId = AM.AccountId");
		sql.append(    "JOIN EnergyCompany EC ON EC.EnergyCompanyId = GM.EnergyCompanyId");
		sql.append("WHERE PAO.PaobjectId").eq(getPaoIDs()[0]);

		if( getEnergyCompanyID() != null) {
			sql.append("AND EC.EnergyCompanyId").eq(getEnergyCompanyID());
		}
		
		sql.append("ORDER BY EC.Name, YWC.AlternateDisplayName, CA.AccountNumber");
		return sql;
	}
	
	
	public void buildAcctProgHashMap()
	{
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT LPE.AccountId, LPE.ProgramId, YLE.YukonDefinitionId, YLE.EntryText");
		sql.append("FROM LmProgramEvent LPE");
		sql.append(    "JOIN LmCustomerEventBase CEB ON LPE.EventId = CEB.EventId");
		sql.append(    "JOIN YukonListEntry YLE ON YLE.EntryId = CEB.ActionId");
		sql.append(    "JOIN LMProgramWebPublishing LMPWP ON LMPWP.ProgramId = LPE.ProgramId");
		sql.append("WHERE LMPWP.DeviceId").eq(getPaoIDs()[0]);
		sql.append(    "AND LPE.EventId IN (");
		sql.append(        "SELECT MAX(LPE2.EventId)");
		sql.append(        "FROM LmProgramEvent LPE2");
		sql.append(            "JOIN LmCustomerEventBase CEB2 ON CEB2.EVENTID = LPE2.EVENTID");
		sql.append(        "WHERE CEB2.EventDateTime").lte(getStopDate());
		sql.append(        "GROUP BY AccountId, ProgramId)");
		sql.append("ORDER BY LPE.ACCOUNTID, LPE.PROGRAMID");

		CTILogger.info(sql.toString());
		
		YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
			
			@Override
			public void processRow(YukonResultSet rs) throws SQLException {
				Integer accountId = rs.getInt("AccountId");
				Integer assignedProgramId = rs.getInt("ProgramId");
				Integer actionID = rs.getInt("YukonDefinitionId");
				String entryText = rs.getString("EntryText");

				//Key = "AcctID_ProgID"
				String key = buildLookupKey(accountId, assignedProgramId); 
				ProgramDetail pd = getAcctProgPairs().get(key);
				if (pd != null) {
					pd.setAction(actionID);
					pd.setStatus(entryText);
				}
        	}
		});

        CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}	

	private String buildLookupKey(Integer accountId, Integer assignedProgramId) {
		return accountId.toString() + "_" + assignedProgramId.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	@Override
    public String getTitleString()
	{
		return title;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	@Override
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
					LiteContact contact = YukonSpringHook.getBean(CustomerDao.class).getPrimaryContact(pd.getCustID().intValue());
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
	@Override
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
	@Override
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
	@Override
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


	@Override
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
