package com.cannontech.stars.database.db.report;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;


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
    private Integer workTypeID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private Integer currentStateID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private Integer serviceCompanyID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private java.util.Date dateReported = new java.util.Date(0);
    private String orderedBy = "";
    private String description = "";
    private java.util.Date dateScheduled = new java.util.Date(0);
    private java.util.Date dateCompleted = new java.util.Date(0);
    private String actionTaken = "";
    private Integer accountID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private String additionalOrderNumber = "";

    public static final String[] SETTER_COLUMNS = {
        "OrderNumber", "WorkTypeID", "CurrentStateID", "ServiceCompanyID", "DateReported",
        "OrderedBy", "Description", "DateScheduled", "DateCompleted", "ActionTaken", "AccountID",
        "AdditionalOrderNumber"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "OrderID" };

    public static final String TABLE_NAME = "WorkOrderBase";

    public WorkOrderBase() {
        super();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getOrderID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getOrderID() == null)
    		setOrderID( getNextOrderID() );
    		
        Object[] addValues = {
            getOrderID(), getOrderNumber(), getWorkTypeID(), getCurrentStateID(),
            getServiceCompanyID(), getDateReported(), getOrderedBy(), getDescription(),
            getDateScheduled(),getDateCompleted(), getActionTaken(), getAccountID(),
            getAdditionalOrderNumber()
        };

        add( TABLE_NAME, addValues );
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getOrderNumber(), getWorkTypeID(), getCurrentStateID(),
            getServiceCompanyID(), getDateReported(), getOrderedBy(), getDescription(),
            getDateScheduled(),getDateCompleted(), getActionTaken(), getAccountID(),
            getAdditionalOrderNumber()
        };

        Object[] constraintValues = { getOrderID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
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
            setAdditionalOrderNumber( (String) results[11]);
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public static final Integer getNextOrderID() {
    	Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
    	return nextValueId;
    }
    
	public static int[] searchByOrderNumber(String orderNo, int energyCompanyID) {
		String sql = "SELECT OrderID FROM " + TABLE_NAME + " wo, ECToWorkOrderMapping map " +
				"WHERE UPPER(OrderNumber) = UPPER(?) AND wo.OrderID = map.WorkOrderID AND map.EnergyCompanyID = ?";
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			stmt = conn.prepareStatement( sql );
			stmt.setString(1, orderNo);
			stmt.setInt(2, energyCompanyID);
			
			rset = stmt.executeQuery();
	    	
			List<Integer> orderIDList = new ArrayList<Integer>();
			while (rset.next())
				orderIDList.add(rset.getInt(1));
	    	
			int[] orderIDs = new int[orderIDList.size()];
			for (int i = 0; i < orderIDList.size(); i++)
				orderIDs[i] = orderIDList.get(i).intValue();
	    	
			return orderIDs;
		}
		catch (java.sql.SQLException e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		return null;
	}
    
    public static int[] searchByAccountID(int accountID) {
    	String sql = "SELECT OrderID FROM " + TABLE_NAME + " WHERE AccountID=" + accountID + " ORDER BY DateReported DESC";
    	SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
	    	
			int[] orderIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				orderIDs[i] = ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue();
	    	
			return orderIDs;
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }
    
    public static void resetServiceCompany(int companyID) {
    	String sql = "UPDATE " + TABLE_NAME + " SET ServiceCompanyID = 0 WHERE ServiceCompanyID = " + companyID;
    	SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    	}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
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
    
    public String getAdditionalOrderNumber() {
        return additionalOrderNumber;
    }

    public void setAdditionalOrderNumber(String newNum) {
        this.additionalOrderNumber = newNum;
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