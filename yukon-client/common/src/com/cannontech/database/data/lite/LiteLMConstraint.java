package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.device.lm.LMProgramConstraint;

public class LiteLMConstraint extends LiteBase {
    private String constraintName;
    private Integer constraintType;

    public LiteLMConstraint() {
        super();
        setLiteType(LiteTypes.LMCONSTRAINT);
    }

    public LiteLMConstraint(int constraintID) {
        super();
        setLiteID(constraintID);
        setLiteType(LiteTypes.LMCONSTRAINT);
    }

    public LiteLMConstraint(int constraintID, String conName_) {
        this(constraintID);
        setConstraintName(conName_);
    }

    public LiteLMConstraint(int constraintID, String conName_, Integer type) {
        this(constraintID);
        setConstraintName(conName_);
        setLMConstraintType(type);
    }

    public int getConstraintID() {
        return getLiteID();
    }

    public String getConstraintName() {
        return constraintName;
    }

    public Integer getLMConstraintType() {
        return constraintType;
    }

    /**
     * retrieve method comment.
     */
    public void retrieve(String databaseAlias) {

        SqlStatement s = new SqlStatement("SELECT constraintID, constraintName" + 
                        " FROM " + LMProgramConstraint.TABLE_NAME + 
                        " where constraintID = " + getConstraintID(),
                        CtiUtilities.getDatabaseAlias());

        try {
            s.execute();

            if (s.getRowCount() <= 0)
                throw new IllegalStateException("Unable to find constraint with ID = " + getLiteID());

            setConstraintID(new Integer(s.getRow(0)[0].toString()).intValue());
            setConstraintName(s.getRow(0)[1].toString());
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public void setConstraintID(int conID) {
        setLiteID(conID);
    }

    public void setConstraintName(String name) {
        constraintName = name;
    }

    public void setLMConstraintType(Integer type) {
        constraintType = type;
    }

    @Override
    public String toString() {
        return getConstraintName();
    }
}