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
    private Integer customerID = new Integer( com.cannontech.database.db.stars.customer.CustomerBase.NONE_INT );
    private Integer siteID = new Integer( com.cannontech.database.db.stars.customer.SiteInformation.NONE_INT );
    private Integer serviceCompanyID = new Integer( ServiceCompany.NONE_INT );
    private java.util.Date dateReported = new java.util.Date(0);
    private String description = "";
    private java.util.Date dateScheduled = new java.util.Date(0);
    private java.util.Date dateCompleted = new java.util.Date(0);
    private String actionTaken = "";

    public static final String[] SETTER_COLUMNS = {
        "OrderNumber", "WorkTypeID", "CurrentStateID", "CustomerID", "SiteID", "ServiceCompanyID",
        "DateReported", "Description", "DateScheduled", "DateCompleted", "ActionTaken"
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
            getCustomerID(), getSiteID(), getServiceCompanyID(), getDateReported(),
            getDescription(), getDateScheduled(), getDateCompleted(), getActionTaken()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getOrderNumber(), getWorkTypeID(), getCurrentStateID(), getCustomerID(),
            getSiteID(), getServiceCompanyID(), getDateReported(), getDescription(),
            getDateScheduled(), getDateCompleted(), getActionTaken()
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
            setCustomerID( (Integer) results[3] );
            setSiteID( (Integer) results[4] );
            setServiceCompanyID( (Integer) results[5] );
            setDateReported( new java.util.Date(((java.sql.Timestamp) results[6]).getTime()) );
            setDescription( (String) results[7] );
            setDateScheduled( new java.util.Date(((java.sql.Timestamp) results[8]).getTime()) );
            setDateCompleted( new java.util.Date(((java.sql.Timestamp) results[9]).getTime()) );
            setActionTaken( (String) results[10] );
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
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextOrderID );
    }

    public static WorkOrderBase[] getAllSiteWorkOrders(Integer siteID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " where SiteID = ? "
                   + "ORDER BY DateReported DESC";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList orderList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, siteID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    WorkOrderBase order = new WorkOrderBase();

                    order.setOrderID( new Integer(rset.getInt("OrderID")) );
                    order.setOrderNumber( rset.getString("OrderNumber") );
                    order.setWorkTypeID( new Integer(rset.getInt("WorkTypeID")) );
                    order.setCurrentStateID( new Integer(rset.getInt("CurrentStateID")) );
                    order.setCustomerID( new Integer(rset.getInt("CustomerID")) );
                    order.setSiteID( new Integer(rset.getInt("SiteID")) );
                    order.setServiceCompanyID( new Integer(rset.getInt("ServiceCompanyID")) );
                    order.setDateReported( new java.util.Date(rset.getTimestamp("DateReported").getTime()) );
                    order.setDescription( rset.getString("Description") );
                    order.setDateScheduled( new java.util.Date(rset.getTimestamp("DateScheduled").getTime()) );
                    order.setDateCompleted( new java.util.Date(rset.getTimestamp("DateCompleted").getTime()) );
                    order.setActionTaken( rset.getString("ActionTaken") );

                    orderList.add(order);
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

        WorkOrderBase[] orders = new WorkOrderBase[ orderList.size() ];
        orderList.toArray( orders );
        return orders;
    }

    public static WorkOrderBase[] getAllServiceCompanyWorkOrders(Integer serviceCompanyID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " where ServiceCompanyID = ? "
                   + "ORDER BY DateReported DESC";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList orderList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, serviceCompanyID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    WorkOrderBase order = new WorkOrderBase();

                    order.setOrderID( new Integer(rset.getInt("OrderID")) );
                    order.setOrderNumber( rset.getString("OrderNumber") );
                    order.setWorkTypeID( new Integer(rset.getInt("WorkTypeID")) );
                    order.setCurrentStateID( new Integer(rset.getInt("CurrentStateID")) );
                    order.setCustomerID( new Integer(rset.getInt("CustomerID")) );
                    order.setSiteID( new Integer(rset.getInt("SiteID")) );
                    order.setServiceCompanyID( new Integer(rset.getInt("ServiceCompanyID")) );
                    order.setDateReported( new java.util.Date(rset.getTimestamp("DateReported").getTime()) );
                    order.setDescription( rset.getString("Description") );
                    order.setDateScheduled( new java.util.Date(rset.getTimestamp("DateScheduled").getTime()) );
                    order.setDateCompleted( new java.util.Date(rset.getTimestamp("DateCompleted").getTime()) );
                    order.setActionTaken( rset.getString("ActionTaken") );

                    orderList.add(order);
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

        WorkOrderBase[] orders = new WorkOrderBase[ orderList.size() ];
        orderList.toArray( orders );
        return orders;
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
	 * Returns the customerID.
	 * @return Integer
	 */
	public Integer getCustomerID() {
		return customerID;
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
	 * Returns the siteID.
	 * @return Integer
	 */
	public Integer getSiteID() {
		return siteID;
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
	 * Sets the customerID.
	 * @param customerID The customerID to set
	 */
	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
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
	 * Sets the siteID.
	 * @param siteID The siteID to set
	 */
	public void setSiteID(Integer siteID) {
		this.siteID = siteID;
	}

	/**
	 * Sets the workTypeID.
	 * @param workTypeID The workTypeID to set
	 */
	public void setWorkTypeID(Integer workTypeID) {
		this.workTypeID = workTypeID;
	}

}