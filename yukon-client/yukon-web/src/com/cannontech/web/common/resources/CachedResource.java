package com.cannontech.web.common.resources;

public enum CachedResource {
    
    YUKON_CSS("classpath:com/cannontech/web/common/resources/yukon.less"),
    LAYOUT_CSS("classpath:com/cannontech/web/common/resources/layout.less"),
    BUTTONS_CSS("classpath:com/cannontech/web/common/resources/buttons.less"),
    OVERRIDES_CSS("classpath:com/cannontech/web/common/resources/overrides.less");
    
    private String path;
    private CachedResource(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
}