package com.cannontech.database.db.stars.customer;

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

    public static final int NONE_INT = 0;

    private Integer customerID = null;
    private Integer primaryContactID = new Integer(NONE_INT);
    private Integer customerTypeID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );
    private String timeZone = "";
    private Integer paObjectID = new Integer(NONE_INT);

    public static final String[] SETTER_COLUMNS = {
        "PrimaryContactID", "CustomerTypeID", "TimeZone", "PAObjectID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "CustomerID" };

    public static final String TABLE_NAME = "CustomerBase";

    public static final String GET_NEXT_CUSTOMER_ID_SQL =
        "SELECT MAX(CustomerID) FROM " + TABLE_NAME;

    public CustomerBase() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getCustomerID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getCustomerID() == null)
    		setCustomerID( getNextCustomerID() );
    		
        Object[] addValues = {
            getCustomerID(), getPrimaryContactID(), getCustomerTypeID(),
            getTimeZone(), getPAObjectID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getPrimaryContactID(), getCustomerTypeID(), getTimeZone(), getPAObjectID()
        };

        Object[] constraintValues = { getCustomerID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getCustomerID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setPrimaryContactID( (Integer) results[0] );
            setCustomerTypeID( (Integer) results[1] );
            setTimeZone( (String) results[2] );
            setPaoID( (Integer) results[3] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextCustomerID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextCustomerID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_CUSTOMER_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextCustomerID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextCustomerID );
    }
    
    public com.cannontech.database.db.customer.CustomerContact[] getAllCustomerContacts(java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "SELECT cont.* FROM " + com.cannontech.database.db.customer.CustomerContact.TABLE_NAME
        		   + " cont, CstBaseCstContactMap map WHERE map.CustomerID = ? AND map.CustomerContactID = cont.ContactID";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList contactList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, getCustomerID().intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                	com.cannontech.database.db.customer.CustomerContact contact = new com.cannontech.database.db.customer.CustomerContact();
                	
                	contact.setContactID( new Integer(rset.getInt("ContactID")) );
                	contact.setContFirstName( rset.getString("ContFirstName") );
                	contact.setContLastName( rset.getString("ContLastName") );
                	contact.setContPhone1( rset.getString("ContPhone1") );
                	contact.setContPhone2( rset.getString("ContPhone2") );
                	contact.setLocationID( new Integer(rset.getInt("LocationID")) );
                	contact.setLogInID( new Integer(rset.getInt("LogInID")) );
                	
                	contactList.add( contact );
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
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

		com.cannontech.database.db.customer.CustomerContact[] contacts =
				new com.cannontech.database.db.customer.CustomerContact[ contactList.size() ];
		contactList.toArray( contacts );
		return contacts;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer newCustomerID) {
        customerID = newCustomerID;
    }

    public Integer getPrimaryContactID() {
        return primaryContactID;
    }

    public void setPrimaryContactID(Integer newPrimaryContactID) {
        primaryContactID = newPrimaryContactID;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String newTimeZone) {
        timeZone = newTimeZone;
    }

    public Integer getPAObjectID() {
        return paObjectID;
    }

    public void setPaoID(Integer newPAObjectID) {
        paObjectID = newPAObjectID;
    }
	/**
	 * Returns the customerTypeID.
	 * @return Integer
	 */
	public Integer getCustomerTypeID() {
		return customerTypeID;
	}

	/**
	 * Sets the customerTypeID.
	 * @param customerTypeID The customerTypeID to set
	 */
	public void setCustomerTypeID(Integer customerTypeID) {
		this.customerTypeID = customerTypeID;
	}

}