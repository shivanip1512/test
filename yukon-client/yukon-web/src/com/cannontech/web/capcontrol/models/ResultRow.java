package com.cannontech.web.capcontrol.models;

public class ResultRow {
    
    private int itemId = -1;
    private int parentId = -1;
    private String parentString = "";
    private boolean isController = false;
    private boolean isPaobject = false;
    private String name = "";
    private String itemType = "";
    private String itemDescription = "";
    
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
    
    public boolean isPaobject() {
        return isPaobject;
    }
    
    public void setIsPaobject(boolean isPaobject) {
        this.isPaobject = isPaobject;
    }

    public boolean isController() {
        return isController;
    }
    
    public void setIsController(boolean isController) {
        this.isController = isController;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getParentString() {
        return parentString;
    }
    
    public void setParentString(String parentString) {
        this.parentString = parentString;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    
    public String getItemDescription() {
        return itemDescription;
    }
    
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    
    public Integer getParentId() {
        return parentId;
    }
    
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
