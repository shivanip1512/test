package com.cannontech.database.data.stars.customer;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CustomerBase extends DBPersistent {

    private com.cannontech.database.db.stars.customer.CustomerBase customerBase = null;
    private com.cannontech.database.db.customer.CustomerContact primaryContact = null;
    private com.cannontech.database.db.stars.CustomerListEntry customerType = null;

    private java.util.Vector customerContactVector = null;

    private com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase = null;

    public CustomerBase() {
        super();
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getCustomerBase().setDbConnection(conn);
        getPrimaryContact().setDbConnection(conn);
        getCustomerType().setDbConnection(conn);

        for (int i = 0; i < getCustomerContactVector().size(); i++)
            ((com.cannontech.database.db.DBPersistent) getCustomerContactVector().elementAt(i)).setDbConnection(conn);
/*
        for (int i = 0; i < getCustomerAccountVector().size(); i++)
            ((com.cannontech.database.db.DBPersistent) getCustomerAccountVector().elementAt(i)).setDbConnection(conn);*/
    }

    public void setCustomerID(Integer newID) {
        getCustomerBase().setCustomerID(newID);
    }

    public void delete() throws java.sql.SQLException {
        getPrimaryContact().delete();

        // delete from CustomerAccount
        com.cannontech.database.db.stars.customer.CustomerAccount[] accounts =
            com.cannontech.database.db.stars.customer.CustomerAccount.getAllCustomerAccounts( getCustomerBase().getCustomerID(), getDbConnection() );
        com.cannontech.database.data.stars.customer.CustomerAccount account = new com.cannontech.database.data.stars.customer.CustomerAccount();
        for (int i = 0; i < accounts.length; i++) {
            account.setCustomerAccount( accounts[i] );
            account.setDbConnection( getDbConnection() );
            account.delete();
        }

        /* Delete from CstBaseCstContactMap.
         * After merging, can be replaced with:
         * com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts()
         */
        com.cannontech.database.db.customer.CustomerContact[] contacts =
            com.cannontech.database.db.customer.CustomerContact.getAllCustomerContacts( getCustomerBase().getCustomerID(), getDbConnection() );
        delete( "CstBaseCstContactMap", "CustomerID", getCustomerBase().getCustomerID() );
        
        // use "data" class to delete from CustomerContact
        com.cannontech.database.data.customer.CustomerContact contact = new com.cannontech.database.data.customer.CustomerContact();
        for (int i = 0; i < contacts.length; i++) {
            contact.setCustomerContact( contacts[i] );
            contact.setDbConnection( getDbConnection() );
            contact.delete();
        }

        getCustomerBase().delete();
    }

    public void add() throws java.sql.SQLException {
        getCustomerBase().add();
        getPrimaryContact().add();

        com.cannontech.database.data.customer.CustomerContact contact = new com.cannontech.database.data.customer.CustomerContact();
        for (int i = 0; i < getCustomerContactVector().size(); i++) {
            contact.setCustomerContact( (com.cannontech.database.db.customer.CustomerContact) getCustomerContactVector().elementAt(i) );
            contact.setLogInID( new Integer(com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID) );
            contact.add();

            Object[] addValues = {
                getCustomerBase().getCustomerID(),
                contact.getCustomerContact().getContactID()
            };
            add( "CstBaseCstContactMap", addValues );
        }
        
        // add to EnergyCompany to CustomerBase mapping table after merging
    }

    public void update() throws java.sql.SQLException {
        getCustomerBase().update();
        
		/*
		 * Update the primary contact directly
		 * We need to do this because CustomerContact.update will add the primary contact,
		 * not update it, see CustomerContact.update for the reason
		 */
        com.cannontech.database.db.customer.CustomerContact primContact = getPrimaryContact();
		Object setValues[] = {
						primContact.getContFirstName(),
						primContact.getContLastName(),
						primContact.getContPhone1(),
						primContact.getContPhone2(),
						primContact.getLocationID(),
						primContact.getLogInID() };

		Object constraintValues[] = { primContact.getContactID() };
		update( primContact.TABLE_NAME, primContact.SETTER_COLUMNS, setValues, primContact.CONSTRAINT_COLUMNS, constraintValues );

        // delete from CstBaseCstContactMap
        com.cannontech.database.db.customer.CustomerContact[] contacts =
            	getCustomerBase().getAllCustomerContacts( getDbConnection() );
        delete( "CstBaseCstContactMap", "CustomerID", getCustomerBase().getCustomerID() );
        
        // use "data" class to delete from CustomerContact
        com.cannontech.database.data.customer.CustomerContact contact = new com.cannontech.database.data.customer.CustomerContact();
        for (int i = 0; i < contacts.length; i++) {
            contact.setCustomerContact( contacts[i] );
            contact.setDbConnection( getDbConnection() );
            contact.delete();
        }

		// add back to CstBaseCstContactMap
        for (int i = 0; i < getCustomerContactVector().size(); i++) {
            contact.setCustomerContact( (com.cannontech.database.db.customer.CustomerContact) getCustomerContactVector().elementAt(i) );
            contact.setLogInID( new Integer(com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID) );
            contact.add();

            Object[] addValues = {
                getCustomerBase().getCustomerID(),
                contact.getCustomerContact().getContactID()
            };
            add( "CstBaseCstContactMap", addValues );
        }
    }

    public void retrieve() throws java.sql.SQLException {
        getCustomerBase().retrieve();

        getPrimaryContact().setContactID( getCustomerBase().getPrimaryContactID() );
        getPrimaryContact().retrieve();
        
        getCustomerType().setEntryID( getCustomerBase().getCustomerTypeID() );
        getCustomerType().retrieve();

        com.cannontech.database.db.customer.CustomerContact[] contacts =
            	getCustomerBase().getAllCustomerContacts( getDbConnection() );
        for (int i = 0; i < contacts.length; i++)
            getCustomerContactVector().addElement( contacts[i] );
/*
        if (getEnergyCompanyBase() == null) {
            // retrieve EnergyCompanyBase
            String[] setterColumns = {
                "EnergyCompanyID"
            };
            String[] constraintColumns = {
                "CustomerID"
            };
            Object[] constraintValues = {
                getCustomerBase().getCustomerID()
            };
            Object[] results = retrieve( setterColumns, "EnergyCompanyCustomerBaseList", constraintColumns, constraintValues );

            if (results.length == 1) {
                com.cannontech.database.data.company.EnergyCompanyBase energyCompany = new com.cannontech.database.data.company.EnergyCompanyBase();
                energyCompany.setEnergyCompanyID( (Integer) results[0] );
                energyCompany.setDbConnection( getDbConnection() );
                energyCompany.retrieve();

                setEnergyCompanyBase( energyCompany );
            }
        }*/
    }

	/**
	 * Returns the customerBase.
	 * @return com.cannontech.database.db.stars.customer.CustomerBase
	 */
	public com.cannontech.database.db.stars.customer.CustomerBase getCustomerBase() {
		if (customerBase == null)
			customerBase = new com.cannontech.database.db.stars.customer.CustomerBase();
		return customerBase;
	}

	/**
	 * Sets the customerBase.
	 * @param customerBase The customerBase to set
	 */
	public void setCustomerBase(
		com.cannontech.database.db.stars.customer.CustomerBase customerBase) {
		this.customerBase = customerBase;
	}

    public com.cannontech.database.db.customer.CustomerContact getPrimaryContact() {
        if (primaryContact == null)
            primaryContact = new com.cannontech.database.db.customer.CustomerContact();
        return primaryContact;
    }

    public void setPrimaryContact(com.cannontech.database.db.customer.CustomerContact newPrimaryContact) {
        primaryContact = newPrimaryContact;
    }

    public java.util.Vector getCustomerContactVector() {
        if (customerContactVector == null)
            customerContactVector = new java.util.Vector(3);
        return customerContactVector;
    }

    public void setCustomerContactVector(java.util.Vector newCustomerContactVector) {
        customerContactVector = newCustomerContactVector;
    }

    public com.cannontech.database.data.company.EnergyCompanyBase getEnergyCompanyBase() {
        return energyCompanyBase;
    }

    public void setEnergyCompanyBase(com.cannontech.database.data.company.EnergyCompanyBase newEnergyCompanyBase) {
        energyCompanyBase = newEnergyCompanyBase;
    }

	/**
	 * Returns the customerType.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getCustomerType() {
		if (customerType == null)
			customerType = new com.cannontech.database.db.stars.CustomerListEntry();
		return customerType;
	}

	/**
	 * Sets the customerType.
	 * @param customerType The customerType to set
	 */
	public void setCustomerType(
		com.cannontech.database.db.stars.CustomerListEntry customerType) {
		this.customerType = customerType;
	}

}