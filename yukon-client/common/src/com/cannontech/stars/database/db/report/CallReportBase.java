package com.cannontech.stars.database.db.report;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.db.customer.CustomerAccount;

public class CallReportBase extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer callID = null;
    private String callNumber = "";
    private Integer callTypeID = new Integer(CtiUtilities.NONE_ZERO_ID);
    private java.util.Date dateTaken = new java.util.Date(0);
    private String takenBy = "";
    private String description = "";
    private Integer accountID = new Integer(CustomerAccount.NONE_INT);

    public static final String[] SETTER_COLUMNS = { "CallNumber", "CallTypeID", "DateTaken", "TakenBy", "Description", "AccountID" };

    public static final String[] CONSTRAINT_COLUMNS = { "CallID" };

    public static final String TABLE_NAME = "CallReportBase";

    public CallReportBase() {
        super();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getCallID() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
    }

    @Override
    public void add() throws java.sql.SQLException {
        if (getCallID() == null)
            setCallID(getNextCallID());

        Object[] addValues = { getCallID(), getCallNumber(), getCallTypeID(), getDateTaken(), getTakenBy(),
                getDescription(), getAccountID() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object[] setValues = { getCallNumber(), getCallTypeID(), getDateTaken(), getTakenBy(), getDescription(), getAccountID() };

        Object[] constraintValues = { getCallID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getCallID() };

        Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setCallNumber((String) results[0]);
            setCallTypeID((Integer) results[1]);
            setDateTaken(new java.util.Date(((java.sql.Timestamp) results[2]).getTime()));
            setTakenBy((String) results[3]);
            setDescription((String) results[4]);
            setAccountID((Integer) results[5]);
        } else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextCallID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }

    public Integer getCallID() {
        return callID;
    }

    public void setCallID(Integer newCallID) {
        callID = newCallID;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String newCallNumber) {
        callNumber = newCallNumber;
    }

    public java.util.Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(java.util.Date newDateTaken) {
        dateTaken = newDateTaken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    /**
     * Returns the accountID.
     * @return Integer
     */
    public Integer getAccountID() {
        return accountID;
    }

    /**
     * Returns the callTypeID.
     * @return Integer
     */
    public Integer getCallTypeID() {
        return callTypeID;
    }

    /**
     * Sets the accountID.
     * @param accountID The accountID to set
     */
    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    /**
     * Sets the callTypeID.
     * @param callTypeID The callTypeID to set
     */
    public void setCallTypeID(Integer callTypeID) {
        this.callTypeID = callTypeID;
    }

    /**
     * Returns the takenBy.
     * @return String
     */
    public String getTakenBy() {
        return takenBy;
    }

    /**
     * Sets the takenBy.
     * @param takenBy The takenBy to set
     */
    public void setTakenBy(String takenBy) {
        this.takenBy = takenBy;
    }

}