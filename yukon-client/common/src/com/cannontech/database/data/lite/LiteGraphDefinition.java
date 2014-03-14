package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;


public class LiteGraphDefinition extends LiteBase {
    
    private String name;

    public LiteGraphDefinition() {
        setLiteType(LiteTypes.GRAPHDEFINITION);
    }
    
    public LiteGraphDefinition(int id) {
        setLiteType(LiteTypes.GRAPHDEFINITION);
        setGraphDefinitionID(id);
    }
    
    public LiteGraphDefinition(int id, String name) {
        setGraphDefinitionID(id);
        setName(name);
        setLiteType(LiteTypes.GRAPHDEFINITION);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getGraphDefinitionID() {
        return getLiteID();
    }
    
    public void retrieve(String databaseAlias) {
       SqlStatement s = new SqlStatement("SELECT GraphDefinitionID,Name "  + 
             "FROM GraphDefinition where GraphDefinitionID = " + getGraphDefinitionID() +
             " ORDER BY Name",
             CtiUtilities.getDatabaseAlias());
    
       try {
          s.execute();
    
          if (s.getRowCount() <= 0) {
             throw new IllegalStateException("Unable to find graphDefinition with graphDefinitionID = " + getLiteID());
          }
    
          setGraphDefinitionID(new Integer(s.getRow(0)[0].toString()).intValue());
          setName(s.getRow(0)[1].toString());
        } catch(Exception e) {
            CTILogger.error(e.getMessage(), e);
        }
    }
    
    public void setGraphDefinitionID(int graphDefinitionID) {
        setLiteID(graphDefinitionID);
    }
    
    public String toString() {
        return name;
    }
}