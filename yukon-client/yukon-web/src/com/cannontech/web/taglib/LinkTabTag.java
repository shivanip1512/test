package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;


/**
 * TODO FIXME: Needs to extend a new version of YukonTagSupport which extends from BodyTagSupport
 *              so that we can use the tag content as the URL so appropriate URL tags can be used.
 *              AND??? so we can insert SelectorKey instead of having to pre-interpret i18n keys outside the tags... 
 *
 *             IE. THIS IS THE MOST INCONVENIENT CONVENIENCE TAG POSSIBLE.
 */
public class LinkTabTag extends YukonTagSupport {

    private String selectorName = "";
    private String tabHref;
    private String tabId;
    private boolean initiallySelected = false;

    @Override
    public void doTag() throws JspException, IOException {
        tabId = ! StringUtils.isEmpty(tabId) ? tabId : UniqueIdentifierTag.generateIdentifier(getJspContext(), "fauxTabbedContentSelectorContent_");

        // tell container tag about ourself
        LinkTabbedTag parent = getParent(LinkTabbedTag.class);
        parent.addTab(this);

        if (StringUtils.isBlank(tabHref)) {
            tabHref = "";
        }
    }

    public String getSelectorName() {
        return selectorName;
    }

    public void setSelectorName(String selectorName) {
        this.selectorName = selectorName;
    }

    public String getTabHref() {
        return tabHref;
    }

    public void setTabHref(String tabHref) {
        this.tabHref = tabHref;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public boolean isInitiallySelected() {
        return initiallySelected;
    }

    public void setInitiallySelected(boolean initiallySelected) {
        this.initiallySelected = initiallySelected;
    }

}