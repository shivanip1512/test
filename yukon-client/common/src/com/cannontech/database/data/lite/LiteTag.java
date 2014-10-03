package com.cannontech.database.data.lite;

public class LiteTag extends LiteBase {
    
    private String tagName;
    private int tagLevel;
    private boolean inhibit;
    private int colorId;
    private int imageId;
    
    {
        setLiteType(LiteTypes.TAG);
    }
    
    public LiteTag(int tagId) {
        setTagId(tagId);
    }
    
    public LiteTag(int tagId, String tagName, int tagLevel, boolean inhibit, int colorId, int imageId) {
        setTagId(tagId);
        setTagName(tagName);
        setTagLevel(tagLevel);
        setInhibit(inhibit);
        setColorId(colorId);
        setImageId(imageId);
    }
    
    public int getTagId() {
        return getLiteID();
    }
    
    public void setTagId(int tagId) {
        setLiteID(tagId);
    }
    
    public String getTagName() {
        return tagName;
    }
    
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    public int getTagLevel() {
        return tagLevel;
    }
    
    public void setTagLevel(int tagLevel) {
        this.tagLevel = tagLevel;
    }
    
    public boolean isInhibit() {
        return inhibit;
    }
    
    public void setInhibit(boolean inhibit) {
        this.inhibit = inhibit;
    }
    
    public int getColorId() {
        return colorId;
    }
    
    public void setColorId(int colorId) {
        this.colorId = colorId;
    }
    
    public int getImageId() {
        return imageId;
    }
    
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    
    public String toString() {
        return getTagName();
    }
    
}