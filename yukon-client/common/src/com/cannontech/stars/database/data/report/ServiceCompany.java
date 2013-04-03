package com.cannontech.stars.database.data.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;
import com.cannontech.stars.energyCompany.EcMappingCategory;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ServiceCompany extends DBPersistent {

    private com.cannontech.stars.database.db.report.ServiceCompany serviceCompany = null;
    private com.cannontech.database.db.customer.Address address = null;
    private com.cannontech.database.data.customer.Contact primaryContact = null;
    
    private Integer energyCompanyID = null;
    private List<ServiceCompanyDesignationCode> designationCodes = null;

    public ServiceCompany() {
        super();
    }

    public void setCompanyID(Integer newID) {
        getServiceCompany().setCompanyID(newID);
    }
    
    public void setAddressID(Integer newID) {
        getServiceCompany().setAddressID(newID);
        getAddress().setAddressID(newID);
    }
    
    public void setContactID(Integer newID) {
        getServiceCompany().setPrimaryContactID(newID);
        getPrimaryContact().setContactID(newID);
    }
    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getServiceCompany().setDbConnection(conn);
        getAddress().setDbConnection(conn);
        getPrimaryContact().setDbConnection(conn);
    }

    @Override
    public void delete() throws java.sql.SQLException {
    	// delete from mapping table
    	delete("ECToGenericMapping",
				new String[] {"ItemID", "MappingCategory"},
				new Object[] {getServiceCompany().getCompanyID(), EcMappingCategory.SERVICE_COMPANY.getDatabaseRepresentation()});
    	
    	ServiceCompanyDesignationCode.deleteDesignationCode(getServiceCompany().getCompanyID().intValue(), getDbConnection()); 
    	
        getServiceCompany().delete();
        
    	getPrimaryContact().setContactID( getServiceCompany().getPrimaryContactID() );
    	getPrimaryContact().delete();
	
        getAddress().setAddressID( getServiceCompany().getAddressID() );
    	getAddress().delete();        
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    		
        getAddress().add();
        getPrimaryContact().add();
        
        getServiceCompany().setAddressID( getAddress().getAddressID() );
        getServiceCompany().setPrimaryContactID( getPrimaryContact().getContact().getContactID() );
        getServiceCompany().add();
        
    	// Add to mapping table
    	Object[] addValues = {
    		getEnergyCompanyID(),
    		getServiceCompany().getCompanyID(),
    		EcMappingCategory.SERVICE_COMPANY.getDatabaseRepresentation()
    	};
    	add("ECToGenericMapping", addValues);
    }

    @Override
    public void update() throws java.sql.SQLException {
        getServiceCompany().update();
        
        getAddress().setAddressID( getServiceCompany().getAddressID() );
        getAddress().update();
    	
    	getPrimaryContact().setContactID( getServiceCompany().getPrimaryContactID() );
        getPrimaryContact().update();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        getServiceCompany().retrieve();
        
        getAddress().setAddressID( getServiceCompany().getAddressID() );
        getAddress().retrieve();
        
        getPrimaryContact().setContactID( getServiceCompany().getPrimaryContactID() );
        getPrimaryContact().retrieve();
        
        setDesignationCodes(ServiceCompanyDesignationCode.getServiceCompanyDesignationCodes(getServiceCompany().getCompanyID().intValue()));
    }
	/**
	 * Returns the serviceCompany.
	 * @return com.cannontech.stars.database.db.report.ServiceCompany
	 */
	public com.cannontech.stars.database.db.report.ServiceCompany getServiceCompany() {
		if (serviceCompany == null)
			serviceCompany = new com.cannontech.stars.database.db.report.ServiceCompany();
		return serviceCompany;
	}

	/**
	 * Sets the serviceCompany.
	 * @param serviceCompany The serviceCompany to set
	 */
	public void setServiceCompany(
		com.cannontech.stars.database.db.report.ServiceCompany serviceCompany) {
		this.serviceCompany = serviceCompany;
	}

	/**
	 * Returns the address.
	 * @return com.cannontech.database.db.customer.CustomerAddress
	 */
	public com.cannontech.database.db.customer.Address getAddress() {
		if (address == null)
			address = new com.cannontech.database.db.customer.Address();
		return address;
	}

	/**
	 * Returns the primaryContact.
	 * @return com.cannontech.database.db.customer.CustomerContact
	 */
	public com.cannontech.database.data.customer.Contact getPrimaryContact() {
		if (primaryContact == null)
			primaryContact = new com.cannontech.database.data.customer.Contact();
		return primaryContact;
	}

	/**
	 * Sets the address.
	 * @param address The address to set
	 */
	public void setAddress(
		com.cannontech.database.db.customer.Address address) {
		this.address = address;
	}

	/**
	 * Sets the primaryContact.
	 * @param primaryContact The primaryContact to set
	 */
	public void setPrimaryContact(
		com.cannontech.database.data.customer.Contact primaryContact) {
		this.primaryContact = primaryContact;
	}

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	public static List<ServiceCompany> retrieveAllServiceCompanies(final Integer energyCompanyID) {
	    
	    YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM ServiceCompany");

        SqlStatementBuilder inClause = new SqlStatementBuilder();
        inClause.append("SELECT ectgm.ItemId FROM EcToGenericMapping ectgm");
        inClause.append("WHERE ectgm.MappingCategory").eq_k(EcMappingCategory.SERVICE_COMPANY);
        inClause.append("AND ectgm.EnergyCompanyId").eq(energyCompanyID);
        
        sql.append("WHERE CompanyId").in(inClause);

        List<ServiceCompany> retList = yukonJdbcTemplate.query(sql, new YukonRowMapper<ServiceCompany>() {

            @Override
            public ServiceCompany mapRow(YukonResultSet rs) throws SQLException {
                ServiceCompany serviceCompany = new ServiceCompany();
                serviceCompany.setCompanyID(rs.getInt("CompanyId"));
                serviceCompany.getServiceCompany().setCompanyName(rs.getString("CompanyName"));
                serviceCompany.getServiceCompany().setAddressID(rs.getInt("AddressId"));
                serviceCompany.getServiceCompany().setMainPhoneNumber(rs.getString("MainPhoneNumber"));
                serviceCompany.getServiceCompany().setMainFaxNumber(rs.getString("MainFaxNumber"));
                serviceCompany.getServiceCompany().setPrimaryContactID(rs.getInt("PrimaryContactId"));
                serviceCompany.getServiceCompany().setHIType(rs.getString("HiType"));
                serviceCompany.setEnergyCompanyID(energyCompanyID);
                serviceCompany.setDesignationCodes(ServiceCompanyDesignationCode.getServiceCompanyDesignationCodes(serviceCompany.getServiceCompany().getCompanyID()));
                return serviceCompany;
            }
        });
    	return retList;
    }
	
	/** 
	 * Returns the ServiceCompany for loginID, where loginID is the ServiceCompany's contact's login.
	 * @param loginID
	 * @return
	 */
	public static com.cannontech.stars.database.db.report.ServiceCompany retrieveServiceCompanyByContactLoginID(Integer loginID) {
    	StringBuffer sql = new StringBuffer( "SELECT COMPANYID, COMPANYNAME, SC.ADDRESSID, MAINPHONENUMBER, MAINFAXNUMBER, PRIMARYCONTACTID, HITYPE " +
    										" FROM " + com.cannontech.stars.database.db.report.ServiceCompany.TABLE_NAME + " SC, CONTACT C" +
    										" WHERE SC.PRIMARYCONTACTID = C.CONTACTID " + 
    										" AND C.LOGINID = " + loginID.toString());
    	
    	SqlStatement stmt = new SqlStatement( sql.toString(), CtiUtilities.getDatabaseAlias() );
    			
    	try {
    		stmt.execute();
    		com.cannontech.stars.database.db.report.ServiceCompany company = new com.cannontech.stars.database.db.report.ServiceCompany();
    		
    		if( stmt.getRowCount() > 0) {
    			Object[] row = stmt.getRow(0);

    			Integer companyID = new Integer(((java.math.BigDecimal) row[0]).intValue()) ;
    			company.setCompanyID( companyID);
    			company.setCompanyName( (String) row[1] );
    			company.setAddressID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    			company.setMainPhoneNumber( (String) row[3] );
    			company.setMainFaxNumber( (String) row[4] );
    			company.setPrimaryContactID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
    			company.setHIType( (String) row[6] );
    		}
    		return company;
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }

	public List<ServiceCompanyDesignationCode> getDesignationCodes() {
		if( designationCodes == null)
			designationCodes = new ArrayList<ServiceCompanyDesignationCode>();
		return designationCodes;
	}

	public void setDesignationCodes(final List<ServiceCompanyDesignationCode> designationCodes) {
		this.designationCodes = designationCodes;
	}
}