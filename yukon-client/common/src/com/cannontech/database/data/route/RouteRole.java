package com.cannontech.database.data.route;

import java.util.Vector;

public class RouteRole {
    
    private int fixedBit = -1;
    private int varBit = -1;
    private Vector duplicates;
    
    public RouteRole() {
        setFixedBit(-1);
        setVarbit(-1);
        setDuplicates(new Vector());
    }
    
    public RouteRole(int fixedBit, int varBit, Vector dups) {
        setFixedBit(fixedBit);
        setVarbit(varBit);
        setDuplicates(dups);
    }

    public Vector getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(Vector duplicates) {
        this.duplicates = duplicates;
    }

    public int getFixedBit() {
        return fixedBit;
    }
    
    public void setFixedBit(int fixedBit) {
        this.fixedBit = fixedBit;
    }

    public int getVarbit() {
        return varBit;
    }

    public void setVarbit(int varbit) {
        this.varBit = varbit;
    }

}
