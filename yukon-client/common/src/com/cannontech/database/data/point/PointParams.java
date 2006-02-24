/**
 * 
 */
package com.cannontech.database.data.point;

public abstract class PointParams{

    private int offset;
    private String name;
    
    
    public PointParams(int offset, String name){

        setName(name);
        setOffset(offset);
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public abstract int getType();


    public String getName() {
        return name;
    }


    public int getOffset() {
        return offset;
    }
    
}