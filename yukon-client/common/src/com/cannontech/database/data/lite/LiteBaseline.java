package com.cannontech.database.data.lite;

public class LiteBaseline extends LiteBase {
    private String baselineName;

    public LiteBaseline() {
        super();
        setLiteType(LiteTypes.BASELINE);
    }

    public LiteBaseline(int baselineID) {
        super();
        setLiteID(baselineID);
        setLiteType(LiteTypes.BASELINE);
    }

    public LiteBaseline(int baselineID, String baselineName) {
        this(baselineID);
        setBaselineName(baselineName);
    }

    public int getBaselineID() {
        return getLiteID();
    }

    public String getBaselineName() {
        return baselineName;
    }

    public void setBaselineID(int baselineID) {
        setLiteID(baselineID);
    }

    public void setBaselineName(String name) {
        baselineName = name;
    }

    @Override
    public String toString() {
        return getBaselineName();
    }
}