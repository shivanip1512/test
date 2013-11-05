package com.cannontech.web.common.resources;

public enum ThemeableResource {
    
    YUKON_CSS("classpath:com/cannontech/web/common/resources/yukon.less", false),
    YUKON_DEFAULT_CSS("classpath:com/cannontech/web/common/resources/yukon.less", true),
    LAYOUT_CSS("classpath:com/cannontech/web/common/resources/layout.less", false),
    BUTTONS_CSS("classpath:com/cannontech/web/common/resources/buttons.less", false),
    BUTTONS_DEFAULT_CSS("classpath:com/cannontech/web/common/resources/buttons.less", true),
    OVERRIDES_CSS("classpath:com/cannontech/web/common/resources/overrides.less", false);
    
    private String path;
    private boolean defaultFile;
    
    private ThemeableResource(String path, boolean defaultFile) {
        this.path = path;
        this.defaultFile = defaultFile;
    }
    
    public String getPath() {
        return path;
    }
    
    public boolean isDefaultFile() {
        return defaultFile;
    }
    
}