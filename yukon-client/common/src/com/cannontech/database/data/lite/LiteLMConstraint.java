package com.cannontech.database.data.lite;


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