package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Represents a menu option that is a standard <a href="[linkUrl]"> style link.
 */
public class SimpleMenuOptionLink extends SimpleMenuOption {

    private String linkUrl = "";
    private boolean newWindow = false;

    public SimpleMenuOptionLink(String id, YukonMessageSourceResolvable menuText) {
        super(id, menuText);
    }
    
    public SimpleMenuOptionLink(String id, String menuTextKey) {
        super(id, menuTextKey);
    }

    public String getUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
    
    public boolean isNewWindow() {
        return newWindow;
    }
    
    public void setNewWindow(boolean newWindow) {
        this.newWindow = newWindow;
    }

}
