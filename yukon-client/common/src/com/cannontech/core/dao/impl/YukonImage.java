package com.cannontech.core.dao.impl;

public enum YukonImage {
    
    DEFAULT_LOGO (1, "logos", "eaton_logo.png", "classpath:com/cannontech/web/common/resources/eaton_logo.png"),
    DEFAULT_BACKGROUND (2, "backgrounds", "yukon_background.jpg", "classpath:com/cannontech/web/common/resources/yukon_background.jpg");
    
    private int id;
    private String category;
    private String name;
    private String path;
    
    private YukonImage(int id, String category, String name, String path) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.path = path;
    }
    
    public int getId() {
        return id;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
}