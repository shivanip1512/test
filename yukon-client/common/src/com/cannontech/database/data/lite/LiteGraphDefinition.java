package com.cannontech.database.data.lite;

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
    
    public void setGraphDefinitionID(int graphDefinitionID) {
        setLiteID(graphDefinitionID);
    }
    
    @Override
    public String toString() {
        return name;
    }
}