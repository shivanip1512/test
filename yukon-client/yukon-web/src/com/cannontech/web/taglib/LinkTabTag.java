package com.cannontech.web.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.i18n.MessageSourceAccessor;

/**
 * @param selectorName Human-readable text
 * @param selectorKey  i18n key: used to make human-readable text and overwrite selectorName.
 *                     ONE OF THE TWO OF THEM are required inputs.
 * @param tabHref Set directly OR pass in as body of the tag
 */
public class LinkTabTag extends YukonTagSupport {

    private String selectorName = "";
    private String tabHref;
    private String tabId;
    private boolean initiallySelected = false;

    @Override
    public void doTag() throws JspException, IOException {

        if (StringUtils.isBlank(selectorName)) {
            throw new IllegalArgumentException("You must define either 'selectorName' or 'selectorKey' in every LinkTabTag.");
        }

        tabId = ! StringUtils.isEmpty(tabId) ? tabId : UniqueIdentifierTag.generateIdentifier(getJspContext(), "linkTabContentSelectorContent_");

        JspTag parent = findAncestorWithClass(this, LinkTabbedTag.class);
        if (parent == null) {
            throw new JspTagException("LinkTabTag must be used within a LinkTabbedTag");
        }
        // tell container tag about ourself
        LinkTabbedTag parentListTag = (LinkTabbedTag) parent;
        parentListTag.addTab(this);

        String theUrl = tabHref;
        if (theUrl == null) {
            StringWriter bodyWriter = new StringWriter();
            if (getJspBody() != null) {
                getJspBody().invoke(bodyWriter);
                theUrl = bodyWriter.toString().trim();
            }
        }

        tabHref = theUrl;
        if (StringUtils.isBlank(theUrl)) {
            tabHref = "";
        }
    }

    public void setSelectorKey(String newKey) {
        if (!StringUtils.isBlank(newKey)) {
            MessageSourceAccessor accessor = getMessageSource();
            selectorName = accessor.getMessage(newKey);
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