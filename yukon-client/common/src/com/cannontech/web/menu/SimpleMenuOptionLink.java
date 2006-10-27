package com.cannontech.web.menu;

/**
 * Represents a menu option that is a standard <a href="[linkUrl]"> style
 * link.
 */
public class SimpleMenuOptionLink extends SimpleMenuOption {
    private String linkUrl = "";
    public SimpleMenuOptionLink(String subOptionName) {
        super(subOptionName);
    }
    public SimpleMenuOptionLink(OptionNameFactory factory) {
        super(factory);
    }
    public String getUrl() {
        return linkUrl;
    }
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

}
