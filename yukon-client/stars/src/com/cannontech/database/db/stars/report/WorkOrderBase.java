package com.cannontech.database.db.stars.report;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class WorkOrderBase extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer orderID = null;
    private String orderNumber = "";
    private Integer workTypeID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );
    private Integer currentStateID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );
    private Integer serviceCompanyID = new Integer( ServiceCompany.NONE_INT );
    private java.util.Date dateReported = new java.util.Date(0);
    private String orderedBy = "";
    private String description = "";
    private java.util.Date dateScheduled = new java.util.Date(0);
    private java.util.Date dateCompleted = new java.util.Date(0);
    private String actionTaken = "";
    private Integer accountID = new Integer( com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "OrderNumber", "WorkTypeID", "CurrentStateID", "ServiceCompanyID", "DateReported",
        "OrderedBy", "Description", "DateScheduled", "DateCompleted", "ActionTaken", "AccountID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "OrderID" };

    public static final String TABLE_NAME = "WorkOrderBase";

    public static final String GET_NEXT_ORDER_ID_SQL =
        "SELECT MAX(OrderID) FROM " + TABLE_NAME;

    public WorkOrderBase() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getOrderID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getOrderID() == null)
    		setOrderID( getNextOrderID() );
    		
        Object[] addValues = {
            getOrderID(), getOrderNumber(), getWorkTypeID(), getCurrentStateID(),
            getServiceCompanyID(), getDateReported(), getOrderedBy(), getDescription(),
            getDateScheduled(),getDateCompleted(), getActionTaken(), getAccountID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getOrderNumber(), getWorkTypeID(), getCurrentStateID(),
            getServiceCompanyID(), getDateReported(), getOrderedBy(), getDescription(),
            getDateScheduled(),getDateCompleted(), getActionTaken(), getAccountID()
        };

        Object[] constraintValues = { getOrderID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getOrderID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setOrderNumber( (String) results[0] );
            setWorkTypeID( (Integer) results[1] );
            setCurrentStateID( (Integer) results[2] );
            setServiceCompanyID( (Integer) results[3] );
            setDateReported( new java.util.Date(((java.sql.Timestamp) results[4]).getTime()) );
            setOrderedBy( (String) results[5] );
            setDescription( (String) results[6] );
            setDateScheduled( new java.util.Date(((java.sql.Timestamp) results[7]).getTime()) );
            setDateCompleted( new java.util.Date(((java.sql.Timestamp) results[8]).getTime()) );
            setActionTaken( (String) results[9] );
            setAccountID( (Integer) results[10] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextOrderID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextOrderID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_ORDER_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextOrderID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
				if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextOrderID );
    }

    public static WorkOrderBase[] getAllAccountWorkOrders(Integer accountID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = " + accountID.toString()
        		   + " ORDER BY DateReported DESC";

		try {
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement( sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			stmt.execute();

	        WorkOrderBase[] orders = new WorkOrderBase[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++) {
            	Object[] row = stmt.getRow(i);
                orders[i] = new WorkOrderBase();

                orders[i].setOrderID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                orders[i].setOrderNumber( (String) row[1] );
                orders[i].setWorkTypeID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
                orders[i].setCurrentStateID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
                orders[i].setServiceCompanyID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
                orders[i].setDateReported( (java.util.Date) row[5] );
                orders[i].setOrderedBy( (String) row[6] );
                orders[i].setDescription( (String) row[7] );
                orders[i].setDateScheduled( (java.util.Date) row[8] );
                orders[i].setDateCompleted( (java.util.Date) row[9] );
                orders[i].setActionTaken( (String) row[10] );
                orders[i].setAccountID( new Integer(((java.math.BigDecimal) row[11]).intValue()) );
            }
            
            return orders;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public static WorkOrderBase[] getAllServiceCompanyWorkOrders(Integer serviceCompanyID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " where ServiceCompanyID = " + serviceCompanyID.toString()
                   + " ORDER BY DateReported DESC";

		try {
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement( sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			stmt.execute();

	        WorkOrderBase[] orders = new WorkOrderBase[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++) {
            	Object[] row = stmt.getRow(i);
                orders[i] = new WorkOrderBase();

                orders[i].setOrderID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                orders[i].setOrderNumber( (String) row[1] );
                orders[i].setWorkTypeID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
                orders[i].setCurrentStateID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
                orders[i].setServiceCompanyID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
                orders[i].setDateReported( (java.util.Date) row[5] );
                orders[i].setOrderedBy( (String) row[6] );
                orders[i].setDescription( (String) row[7] );
                orders[i].setDateScheduled( (java.util.Date) row[8] );
                orders[i].setDateCompleted( (java.util.Date) row[9] );
                orders[i].setActionTaken( (String) row[10] );
                orders[i].setAccountID( new Integer(((java.math.BigDecimal) row[11]).intValue()) );
            }
            
            return orders;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }

	/**
	 * Returns the actionTaken.
	 * @return String
	 */
	public String getActionTaken() {
		return actionTaken;
	}

	/**
	 * Returns the currentStateID.
	 * @return Integer
	 */
	public Integer getCurrentStateID() {
		return currentStateID;
	}

	/**
	 * Returns the dateCompleted.
	 * @return java.util.Date
	 */
	public java.util.Date getDateCompleted() {
		return dateCompleted;
	}

	/**
	 * Returns the dateReported.
	 * @return java.util.Date
	 */
	public java.util.Date getDateReported() {
		return dateReported;
	}

	/**
	 * Returns the dateScheduled.
	 * @return java.util.Date
	 */
	public java.util.Date getDateScheduled() {
		return dateScheduled;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the orderID.
	 * @return Integer
	 */
	public Integer getOrderID() {
		return orderID;
	}

	/**
	 * Returns the orderNumber.
	 * @return String
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Returns the serviceCompanyID.
	 * @return Integer
	 */
	public Integer getServiceCompanyID() {
		return serviceCompanyID;
	}

	/**
	 * Returns the workTypeID.
	 * @return Integer
	 */
	public Integer getWorkTypeID() {
		return workTypeID;
	}

	/**
	 * Sets the actionTaken.
	 * @param actionTaken The actionTaken to set
	 */
	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	/**
	 * Sets the currentStateID.
	 * @param currentStateID The currentStateID to set
	 */
	public void setCurrentStateID(Integer currentStateID) {
		this.currentStateID = currentStateID;
	}

	/**
	 * Sets the dateCompleted.
	 * @param dateCompleted The dateCompleted to set
	 */
	public void setDateCompleted(java.util.Date dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	/**
	 * Sets the dateReported.
	 * @param dateReported The dateReported to set
	 */
	public void setDateReported(java.util.Date dateReported) {
		this.dateReported = dateReported;
	}

	/**
	 * Sets the dateScheduled.
	 * @param dateScheduled The dateScheduled to set
	 */
	public void setDateScheduled(java.util.Date dateScheduled) {
		this.dateScheduled = dateScheduled;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the orderID.
	 * @param orderID The orderID to set
	 */
	public void setOrderID(Integer orderID) {
		this.orderID = orderID;
	}

	/**
	 * Sets the orderNumber.
	 * @param orderNumber The orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Sets the serviceCompanyID.
	 * @param serviceCompanyID The serviceCompanyID to set
	 */
	public void setServiceCompanyID(Integer serviceCompanyID) {
		this.serviceCompanyID = serviceCompanyID;
	}

	/**
	 * Sets the workTypeID.
	 * @param workTypeID The workTypeID to set
	 */
	public void setWorkTypeID(Integer workTypeID) {
		this.workTypeID = workTypeID;
	}

	/**
	 * Returns the orderedBy.
	 * @return String
	 */
	public String getOrderedBy() {
		return orderedBy;
	}

	/**
	 * Sets the orderedBy.
	 * @param orderedBy The orderedBy to set
	 */
	public void setOrderedBy(String orderedBy) {
		this.orderedBy = orderedBy;
	}

	/**
	 * Returns the accountID.
	 * @return Integer
	 */
	public Integer getAccountID() {
		return accountID;
	}

	/**
	 * Sets the accountID.
	 * @param accountID The accountID to set
	 */
	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
	}

}