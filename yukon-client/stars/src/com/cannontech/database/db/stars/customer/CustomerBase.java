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
    private Integer paoID = new Integer(NONE_INT);

    public static final String[] SETTER_COLUMNS = {
        "PrimaryContactID", "CustomerTypeID", "TimeZone", "PaoID"
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
            getCustomerID(), getPrimaryContactID(), getCustomerTypeID(), getTimeZone(), getPaoID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getPrimaryContactID(), getCustomerTypeID(), getTimeZone(), getPaoID()
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
    
    public com.cannontech.database.db.customer.CustomerContact[] getAllCustomerContacts() {
        String sql = "SELECT cont.* FROM " + com.cannontech.database.db.customer.CustomerContact.TABLE_NAME
        		   + " cont, CustomerAdditionalContact map WHERE map.CustomerID = " + getCustomerID().toString()
        		   + " AND map.ContactID = cont.ContactID";

        try
        {
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			com.cannontech.database.db.customer.CustomerContact[] contacts = new com.cannontech.database.db.customer.CustomerContact[ stmt.getRowCount() ];
			for (int i = 0; i < contacts.length; i++) {
				Object[] row = stmt.getRow(i);
				contacts[i] = new com.cannontech.database.db.customer.CustomerContact();
            	
            	contacts[i].setContactID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
            	contacts[i].setContFirstName( (String) row[1] );
            	contacts[i].setContLastName( (String) row[2] );
            	contacts[i].setContPhone1( (String) row[3] );
            	contacts[i].setContPhone2( (String) row[4] );
            	contacts[i].setLocationID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
            	contacts[i].setLogInID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
            }
            
            return contacts;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
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

	/**
	 * Returns the paoID.
	 * @return Integer
	 */
	public Integer getPaoID() {
		return paoID;
	}

	/**
	 * Sets the paoID.
	 * @param paoID The paoID to set
	 */
	public void setPaoID(Integer paoID) {
		this.paoID = paoID;
	}

}