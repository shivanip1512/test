package com.cannontech.core.image.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum YukonImage {
    
    DEFAULT_LOGO (-1, "logos", "eaton_logo.png", "classpath:com/cannontech/web/common/resources/eaton_logo.png"),
    DEFAULT_BACKGROUND (-2, "backgrounds", "yukon_background.jpg", "classpath:com/cannontech/web/common/resources/yukon_background.jpg");
    
    private final int id;
    private final String category;
    private final String name;
    private final String path;
    
    private final static Set<Integer> standardImageIds = ImmutableSet.of(DEFAULT_LOGO.id, DEFAULT_BACKGROUND.id);
    
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
    
    public static Set<Integer> getStandardImageIds() {
        return standardImageIds;
    }

    public static boolean isStandardImageId(int imageId) {
        return standardImageIds.contains(imageId);
    }
}