package com.cannontech.database.data.starscustomer;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CustomerBase extends DBPersistent {

    private com.cannontech.database.db.starscustomer.CustomerBase customerBase = null;
    private com.cannontech.database.db.customer.CustomerContact primaryContact = null;

    private java.util.Vector customerContactVector = null;
    //private java.util.Vector customerAccountVector = null;

    private com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase = null;

    public CustomerBase() {
        super();
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getCustomerBase().setDbConnection(conn);
        getPrimaryContact().setDbConnection(conn);

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
        // delete from EnergyCompanyCustomerBaseList
        //delete( "EnergyCompanyCustomerBaseList", "CustomerID", getCustomerBase().getCustomerID() );

        getPrimaryContact().delete();

        // delete from CustomerAccount
        com.cannontech.database.db.starscustomer.CustomerAccount[] accounts =
            com.cannontech.database.db.starscustomer.CustomerAccount.getAllCustomerAccounts( getCustomerBase().getCustomerID(), getDbConnection() );
        com.cannontech.database.data.starscustomer.CustomerAccount account = new com.cannontech.database.data.starscustomer.CustomerAccount();
        for (int i = 0; i < accounts.length; i++) {
            account.setCustomerAccount( accounts[i] );
            account.setDbConnection( getDbConnection() );
            account.delete();
        }

        /* this whole block can be replaced with a single line:
         * com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts()
         * after merging the tables
         */
        // delete from CustomerBaseCustContact
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

        // delete from CallReportBase
        com.cannontech.database.db.starsreport.CallReportBase.deleteAllCallReports(
            getCustomerBase().getCustomerID(), getDbConnection() );

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
/*
        if (getEnergyCompanyBase() != null) {
            Object[] addValues = {
                getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
                getCustomerBase().getCustomerID()
            };
            add( "EnergyCompanyCustomerBaseList", addValues );
        }*/
    }

    public void update() throws java.sql.SQLException {
        getCustomerBase().update();
        getPrimaryContact().update();

        /* this whole block can be replaced with a single line:
         * com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts()
         * after merging the tables
         */
        // delete from CustomerBaseCustContact
        com.cannontech.database.db.customer.CustomerContact[] contacts =
            com.cannontech.database.db.customer.CustomerContact.getAllCustomerContacts( getCustomerBase().getCustomerID(), getDbConnection() );
        delete( "CustomerBaseCustContact", "CustomerID", getCustomerBase().getCustomerID() );
        // use "data" class to delete from CustomerContact
        com.cannontech.database.data.customer.CustomerContact contact = new com.cannontech.database.data.customer.CustomerContact();
        for (int i = 0; i < contacts.length; i++) {
            contact.setCustomerContact( contacts[i] );
            contact.setDbConnection( getDbConnection() );
            contact.delete();
        }

        for (int i = 0; i < getCustomerContactVector().size(); i++) {
            contact.setCustomerContact( (com.cannontech.database.db.customer.CustomerContact) getCustomerContactVector().elementAt(i) );
            contact.setLogInID( new Integer(com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID) );
            contact.add();

            Object[] addValues = {
                getCustomerBase().getCustomerID(),
                contact.getCustomerContact().getContactID()
            };
            add( "CustomerBaseCustContact", addValues );
        }
    }

    public void retrieve() throws java.sql.SQLException {
        getCustomerBase().retrieve();

        getPrimaryContact().setContactID( getCustomerBase().getPrimaryContactID() );
        getPrimaryContact().retrieve();

        com.cannontech.database.db.customer.CustomerContact[] contacts =
            com.cannontech.database.db.customer.CustomerContact.getAllCustomerContacts( getCustomerBase().getCustomerID(), getDbConnection() );
        for (int i = 0; i < contacts.length; i++)
            getCustomerContactVector().addElement( contacts[i] );
/*
        com.cannontech.database.db.starscustomer.CustomerAccount[] accounts =
            com.cannontech.database.db.starscustomer.CustomerAccount.getAllCustomerAccounts( getCustomerBase().getCustomerID(), getDbConnection() );
        for (int i = 0; i < accounts.length; i++) {
             com.cannontech.database.data.starscustomer.CustomerAccount account = new com.cannontech.database.data.starscustomer.CustomerAccount();

             account.setCustomerAccount( accounts[i] );
             account.setDbConnection( getDbConnection() );
             account.retrieve();

             getCustomerAccountVector().addElement( account );
        }

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

    public com.cannontech.database.db.starscustomer.CustomerBase getCustomerBase() {
        if (customerBase == null)
            customerBase = new com.cannontech.database.db.starscustomer.CustomerBase();
        return customerBase;
    }

    public void setCustomerBase(com.cannontech.database.db.starscustomer.CustomerBase newCustomerBase) {
        customerBase = newCustomerBase;
    }

    public com.cannontech.database.db.customer.CustomerContact getPrimaryContact() {
        if (primaryContact == null)
            primaryContact = new com.cannontech.database.db.customer.CustomerContact();
        return primaryContact;
    }

    public void setPrimaryContact(com.cannontech.database.db.customer.CustomerContact newPrimaryContact) {
        primaryContact = newPrimaryContact;
    }
/*
    public java.util.Vector getCustomerAccountVector() {
        if (customerAccountVector == null)
            customerAccountVector = new java.util.Vector(1);
        return customerAccountVector;
    }

    public void setCustomerAccountVector(java.util.Vector newCustomerAccountVector) {
        customerAccountVector = newCustomerAccountVector;
    }*/

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
}