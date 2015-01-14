package com.cannontech.database.data.lite;


public class LiteGraphCustomerList extends LiteBase {
    private int graphDefinitionID;

    public LiteGraphCustomerList(int custID, int gdefID) {
        setCustomerID(custID);
        setGraphDefinitionID(gdefID);
        setLiteType(LiteTypes.GRAPH_CUSTOMER_LIST);
    }

    public int getGraphDefinitionID() {
        return graphDefinitionID;
    }

    public int getCustomerID() {
        return getLiteID();
    }

    public void setGraphDefinitionID(int newGDefID) {
        graphDefinitionID = newGDefID;
    }

    public void setCustomerID(int newCustID) {
        setLiteID(newCustID);
    }

    @Override
    public String toString() {
        return "GraphDefID: " + getGraphDefinitionID() + "  CustomerID: " + getCustomerID();
    }
}