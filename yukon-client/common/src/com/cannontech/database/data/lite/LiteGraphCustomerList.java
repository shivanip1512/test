package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;

public class LiteGraphCustomerList extends LiteBase {
    private int graphDefinitionID;

    public LiteGraphCustomerList() {
        setLiteType(LiteTypes.GRAPH_CUSTOMER_LIST);
    }

    public LiteGraphCustomerList(int custId) {
        setLiteType(LiteTypes.GRAPH_CUSTOMER_LIST);
        setCustomerID(custId);
    }

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

    public void retrieve(String databaseAlias) {
        com.cannontech.database.SqlStatement s =
                new com.cannontech.database.SqlStatement(
                "SELECT CustomerID, GraphDefinitionID "  +
                " FROM GraphCustomerList where customerID = " + getCustomerID(),
                CtiUtilities.getDatabaseAlias() );

        try {
            s.execute();

            if (s.getRowCount() <= 0)
                throw new IllegalStateException("Unable to find graphCustomerList with customerID = " + getLiteID());

            setCustomerID(new Integer(s.getRow(0)[0].toString()).intValue());
            setGraphDefinitionID(new Integer(s.getRow(0)[1].toString()).intValue());
        } catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
        }
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