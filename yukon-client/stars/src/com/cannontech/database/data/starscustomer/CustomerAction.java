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

public class CustomerAction extends DBPersistent {

    private com.cannontech.database.db.starscustomer.CustomerAction customerAction = null;

    public CustomerAction() {
        super();
    }

    public void setActionID(Integer newID) {
        getCustomerAction().setActionID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getCustomerAction().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getCustomerAction().delete();
    }

    public void add() throws java.sql.SQLException {
        getCustomerAction().add();
    }

    public void update() throws java.sql.SQLException {
        getCustomerAction().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getCustomerAction().retrieve();
    }

    public com.cannontech.database.db.starscustomer.CustomerAction getCustomerAction() {
        if (customerAction == null)
            customerAction = new com.cannontech.database.db.starscustomer.CustomerAction();
        return customerAction;
    }

    public void setCustomerAction(com.cannontech.database.db.starscustomer.CustomerAction newCustomerAction) {
        customerAction = newCustomerAction;
    }
}