package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Represents a menu option that is a standard <a href="[linkUrl]"> style link.
 */
public class SimpleMenuOptionLink extends SimpleMenuOption {

    private String linkUrl = "";

    public SimpleMenuOptionLink(YukonMessageSourceResolvable menuText) {
        super(menuText);
    }

    public String getUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

}
