package com.cannontech.stars.xml.serialize;

public abstract class StarsCustListEntry {
    private String content = "";
    private int entryID;
    private boolean hasEntryID;
    private int yukonDefID;
    private boolean hasYukonDefID;

    public StarsCustListEntry() {
        setContent("");
    }

    public void deleteEntryID() {
        this.hasEntryID= false;
    }

    public void deleteYukonDefID() {
        this.hasYukonDefID= false;
    } 

    public java.lang.String getContent() {
        return this.content;
    } 

    public int getEntryID() {
        return this.entryID;
    } 

    public int getYukonDefID() {
        return this.yukonDefID;
    } 

    public boolean hasEntryID() {
        return this.hasEntryID;
    } 

    public boolean hasYukonDefID() {
        return this.hasYukonDefID;
    } 

    public void setContent(java.lang.String content) {
        this.content = content;
    } 

    public void setEntryID(int entryID) {
        this.entryID = entryID;
        this.hasEntryID = true;
    }

    public void setYukonDefID(int yukonDefID) {
        this.yukonDefID = yukonDefID;
        this.hasYukonDefID = true;
    } 
    
}