package com.cannontech.database.db.starsreport;

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
    private Integer orderNumber = null;
    private String workType = null;
    private String currentState = null;
    private java.util.Date dateAssigned = null;
    private String description = null;
    private java.util.Date dateCompleted = null;
    private String actionTaken = null;
    private String serviceProviderName = null;
    private String _SPContactFirstName = null;
    private String _SPContactLastName = null;
    private String _SPMainPhone = null;
    private String _SPSecondPhone = null;
    private String accountNumber = null;
    private String acctFirstName = null;
    private String acctLastName = null;
    private String acctAddress1 = null;
    private String acctAddress2 = null;
    private String acctCity = null;
    private String acctState = null;
    private String acctZip = null;
    private String acctMainPhone = null;
    private String acctSecondPhone = null;
    private String acctGridNumber = null;

    public static final String[] SETTER_COLUMNS = {
        "OrderNumber", "WorkType", "CurrentState", "DateAssigned",
        "Description", "DateCompleted", "ActionTaken", "ServiceProviderName",
        "SPContactFirstName", "SPContactLastName", "SPMainPhone",
        "SPSecondPhone", "AccountNumber", "AcctFirstName", "AcctLastName",
        "AcctAddress1", "AcctAdress2", "AcctCity", "AcctState", "AcctZip",
        "AcctMainPhone", "AcctSecondPhone", "AcctGridNumber"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "OrderID" };

    public static final String TABLE_NAME = "WorkOrderBase";

    public static final String GET_NEXT_ORDER_ID_SQL =
        "SELECT MAX(OrderID) FROM " + TABLE_NAME;

    public WorkOrderBase() {
        super();
    }

    public static WorkOrderBase[] getAllWorkOrders(Integer energyCompanyID, java.sql.Connection conn) {
        String sql = "SELECT service.* FROM " + TABLE_NAME + " service, ECToWorkOrderMapping map "
				   + "WHERE service.OrderID = map.WorkOrderID AND map.EnergyCompanyID = ? "
                   + "ORDER BY service.DateAssigned DESC";

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
                pstmt.setInt( 1, energyCompanyID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    WorkOrderBase order = new WorkOrderBase();

                    order.setOrderID( new Integer(rset.getInt("OrderID")) );
                    order.setOrderNumber( new Integer(rset.getInt("OrderNumber")) );
                    order.setWorkType( rset.getString("WorkType") );
                    order.setCurrentState( rset.getString("CurrentState") );
                    order.setDateAssigned( new java.util.Date(rset.getTimestamp("DateAssigned").getTime()) );
                    order.setDescription( rset.getString("Description") );
                    if (rset.getTimestamp("DateCompleted") != null)
	                    order.setDateCompleted( new java.util.Date(rset.getTimestamp("DateCompleted").getTime()) );
                    order.setActionTaken( rset.getString("ActionTaken") );
                    order.setServiceProviderName( rset.getString("ServiceProviderName") );
                    order.setSPContactFirstName( rset.getString("SPContactFirstName") );
                    order.setSPContactLastName( rset.getString("SPContactLastName") );
                    order.setSPMainPhone( rset.getString("SPMainPhone") );
                    order.setSPSecondPhone( rset.getString("SPSecondPhone") );
                    order.setAccountNumber( rset.getString("AccountNumber") );
                    order.setAcctFirstName( rset.getString("AcctFirstName") );
                    order.setAcctLastName( rset.getString("AcctLastName") );
                    order.setAcctAddress1( rset.getString("AcctAddress1") );
                    order.setAcctAddress2( rset.getString("AcctAddress2") );
                    order.setAcctCity( rset.getString("AcctCity") );
                    order.setAcctState( rset.getString("AcctState") );
                    order.setAcctZip( rset.getString("AcctZip") );
                    order.setAcctMainPhone( rset.getString("AcctMainPhone") );
                    order.setAcctSecondPhone( rset.getString("AcctSecondPhone") );
                    order.setAcctGridNumber( rset.getString("AcctGridNumber") );

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

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getOrderID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getOrderID() == null)
    		setOrderID( getNextOrderID() );
    		
        Object[] addValues = {
            getOrderID(), getOrderNumber(), getWorkType(), getCurrentState(),
            getDateAssigned(), getDescription(), getDateCompleted(),
            getActionTaken(), getServiceProviderName(), getSPContactFirstName(),
            getSPContactLastName(), getSPMainPhone(), getSPSecondPhone(),
            getAccountNumber(), getAcctFirstName(), getAcctLastName(),
            getAcctAddress1(), getAcctAddress2(), getAcctCity(), getAcctState(),
            getAcctZip(), getAcctMainPhone(), getAcctSecondPhone(),
            getAcctGridNumber()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getOrderNumber(), getWorkType(), getCurrentState(),
            getDateAssigned(), getDescription(), getDateCompleted(),
            getActionTaken(), getServiceProviderName(), getSPContactFirstName(),
            getSPContactLastName(), getSPMainPhone(), getSPSecondPhone(),
            getAccountNumber(), getAcctFirstName(), getAcctLastName(),
            getAcctAddress1(), getAcctAddress2(), getAcctCity(), getAcctState(),
            getAcctZip(), getAcctMainPhone(), getAcctSecondPhone(),
            getAcctGridNumber()
        };

        Object[] constraintValues = { getOrderID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getOrderID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setOrderNumber( (Integer) results[0] );
            setWorkType( (String) results[1] );
            setCurrentState( (String) results[2] );
            setDateAssigned( new java.util.Date(((java.sql.Timestamp) results[3]).getTime()) );
            setDescription( (String) results[4] );
            setDateCompleted( new java.util.Date(((java.sql.Timestamp) results[5]).getTime()) );
            setActionTaken( (String) results[6] );
            setServiceProviderName( (String) results[7] );
            setSPContactFirstName( (String) results[8] );
            setSPContactLastName( (String) results[9] );
            setSPMainPhone( (String) results[10] );
            setSPSecondPhone( (String) results[11] );
            setAccountNumber( (String) results[12] );
            setAcctFirstName( (String) results[13] );
            setAcctLastName( (String) results[14] );
            setAcctAddress1( (String) results[15] );
            setAcctAddress2( (String) results[16] );
            setAcctCity( (String) results[17] );
            setAcctState( (String) results[18] );
            setAcctZip( (String) results[19] );
            setAcctMainPhone( (String) results[20] );
            setAcctSecondPhone( (String) results[21] );
            setAcctGridNumber( (String) results[22] );
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

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer newOrderID) {
        orderID = newOrderID;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer newOrderNumber) {
        orderNumber = newOrderNumber;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String newWorkType) {
        workType = newWorkType;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String newCurrentState) {
        currentState = newCurrentState;
    }

    public java.util.Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(java.util.Date newDateAssigned) {
        dateAssigned = newDateAssigned;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public java.util.Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(java.util.Date newDateCompleted) {
        dateCompleted = newDateCompleted;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String newActionTaken) {
        actionTaken = newActionTaken;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String newServiceProviderName) {
        serviceProviderName = newServiceProviderName;
    }

    public String getSPContactFirstName() {
        return _SPContactFirstName;
    }

    public void setSPContactFirstName(String newSPContactFirstName) {
        _SPContactFirstName = newSPContactFirstName;
    }

    public String getSPContactLastName() {
        return _SPContactLastName;
    }

    public void setSPContactLastName(String newSPContactLastName) {
        _SPContactLastName = newSPContactLastName;
    }

    public String getSPMainPhone() {
        return _SPMainPhone;
    }

    public void setSPMainPhone(String newSPMainPhone) {
        _SPMainPhone = newSPMainPhone;
    }

    public String getSPSecondPhone() {
        return _SPSecondPhone;
    }

    public void setSPSecondPhone(String newSPSecondPhone) {
        _SPSecondPhone = newSPSecondPhone;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String newAccountNumber) {
        accountNumber = newAccountNumber;
    }

    public String getAcctFirstName() {
        return acctFirstName;
    }

    public void setAcctFirstName(String newAcctFirstName) {
        acctFirstName = newAcctFirstName;
    }

    public String getAcctLastName() {
        return acctLastName;
    }

    public void setAcctLastName(String newAcctLastName) {
        acctLastName = newAcctLastName;
    }

    public String getAcctAddress1() {
        return acctAddress1;
    }

    public void setAcctAddress1(String newAcctAddress1) {
        acctAddress1 = newAcctAddress1;
    }

    public String getAcctAddress2() {
        return acctAddress2;
    }

    public void setAcctAddress2(String newAcctAddress2) {
        acctAddress2 = newAcctAddress2;
    }

    public String getAcctCity() {
        return acctCity;
    }

    public void setAcctCity(String newAcctCity) {
        acctCity = newAcctCity;
    }

    public String getAcctState() {
        return acctState;
    }

    public void setAcctState(String newAcctState) {
        acctState = newAcctState;
    }

    public String getAcctZip() {
        return acctZip;
    }

    public void setAcctZip(String newAcctZip) {
        acctZip = newAcctZip;
    }

    public String getAcctMainPhone() {
        return acctMainPhone;
    }

    public void setAcctMainPhone(String newAcctMainPhone) {
        acctMainPhone = newAcctMainPhone;
    }

    public String getAcctSecondPhone() {
        return acctSecondPhone;
    }

    public void setAcctSecondPhone(String newAcctSecondPhone) {
        acctSecondPhone = newAcctSecondPhone;
    }

    public String getAcctGridNumber() {
        return acctGridNumber;
    }

    public void setAcctGridNumber(String newAcctGridNumber) {
        acctGridNumber = newAcctGridNumber;
    }
}