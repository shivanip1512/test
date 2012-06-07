package com.cannontech.stars.database.data.report;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;
import com.cannontech.stars.database.db.ECToGenericMapping;


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
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getServiceCompany().setDbConnection(conn);
        getAddress().setDbConnection(conn);
        getPrimaryContact().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
    	// delete from mapping table
    	delete("ECToGenericMapping",
				new String[] {"ItemID", "MappingCategory"},
				new Object[] {getServiceCompany().getCompanyID(), "ServiceCompany"});
    	
    	ServiceCompanyDesignationCode.deleteDesignationCode(getServiceCompany().getCompanyID().intValue(), getDbConnection()); 
    	
        getServiceCompany().delete();
        
    	getPrimaryContact().setContactID( getServiceCompany().getPrimaryContactID() );
    	getPrimaryContact().delete();
	
        getAddress().setAddressID( getServiceCompany().getAddressID() );
    	getAddress().delete();        
    }

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
    		com.cannontech.stars.database.db.report.ServiceCompany.TABLE_NAME
    	};
    	add("ECToGenericMapping", addValues);
    }

    public void update() throws java.sql.SQLException {
        getServiceCompany().update();
        
        getAddress().setAddressID( getServiceCompany().getAddressID() );
        getAddress().update();
    	
    	getPrimaryContact().setContactID( getServiceCompany().getPrimaryContactID() );
        getPrimaryContact().update();
    }

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

	public static ServiceCompany[] retrieveAllServiceCompanies(Integer energyCompanyID) {
    	ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems( energyCompanyID, com.cannontech.stars.database.db.report.ServiceCompany.TABLE_NAME );
    	if (items == null || items.length == 0)
    		return new ServiceCompany[0];
    			
    	StringBuffer sql = new StringBuffer( "SELECT * FROM " + com.cannontech.stars.database.db.report.ServiceCompany.TABLE_NAME + " WHERE CompanyID = " + items[0].getItemID().toString() );
    	for (int i = 1; i < items.length; i++)
    		sql.append( " OR CompanyID = " ).append( items[i].getItemID() );
    			   
    	SqlStatement stmt = new SqlStatement( sql.toString(), CtiUtilities.getDatabaseAlias() );
    			
    	try {
    		stmt.execute();
    		ServiceCompany[] companies = new ServiceCompany[ stmt.getRowCount() ];
    		
    		for (int i = 0; i < stmt.getRowCount(); i++) {
    			Object[] row = stmt.getRow(i);
    			companies[i] = new ServiceCompany();
    			
    			Integer companyID = new Integer(((java.math.BigDecimal) row[0]).intValue()) ;
    			companies[i].setCompanyID( companyID);
    			companies[i].getServiceCompany().setCompanyName( (String) row[1] );
    			companies[i].getServiceCompany().setAddressID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    			companies[i].getServiceCompany().setMainPhoneNumber( (String) row[3] );
    			companies[i].getServiceCompany().setMainFaxNumber( (String) row[4] );
    			companies[i].getServiceCompany().setPrimaryContactID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
    			companies[i].getServiceCompany().setHIType( (String) row[6] );
    			companies[i].setEnergyCompanyID(energyCompanyID);
    			companies[i].setDesignationCodes(ServiceCompanyDesignationCode.getServiceCompanyDesignationCodes(companyID.intValue()));
    		}
    		return companies;
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
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