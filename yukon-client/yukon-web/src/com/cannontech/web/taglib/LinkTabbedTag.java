package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * USAGE EXAMPLE - top verion is easiest/currently used most often:
 * 
 * <cti:linkTabbedContainer mode="section">
 *     <cti:linkTab selectorKey="yukon.web.billing.tab.schedules.title">
 *         <c:url value="/user/profile" />
 *     </cti:linkTab>
 * 
 *     <c:url var="url_setup" value="/servlet/BillingServlet" />
 *     <cti:linkTab selectorKey="yukon.web.billing.tab.setup.title" tabHref="${url_setup}" />
 * 
 *     <cti:msg2 var="name_gen" key="yukon.web.billing.tab.generation.title" />
 *     <cti:linkTab selectorName="${name_gen}" initiallySelected="true" />
 * </cti:linkTabbedContainer>
 */
public class LinkTabbedTag extends BodyTagSupport {

    private enum Mode {
        box, section
    }

    private Mode mode = Mode.section;
    private String classes = "tabbed-container link-tabbed-container ui-tabs ui-widget ui-widget-content ui-corner-all";
    private List<LinkTabTag> tabs = new ArrayList<>();
    private String id = "";

    public void addTab(LinkTabTag tab) {
        tabs.add(tab);
    }

    public void setMode(String newMode) {
        this.mode = Mode.valueOf(newMode);
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    @Override
    public int doStartTag() throws JspException {
        tabs.clear();
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    @Override
    public void setBodyContent(BodyContent arg0) {
        super.setBodyContent(arg0);
    }

    @Override
    public int doEndTag() throws JspException {

        try {
            if (mode == Mode.section) classes += " section";
            id = ! StringUtils.isEmpty(id) ? id : "linkTabbedControl_"+ UniqueIdentifierTag.generateIdentifier(pageContext, "tabbedContentSelectorContainer_");

            pageContext.getOut().println("<div id=\"" + id + "\" class=\"" + classes + "\">");
            pageContext.getOut().println("<ul class=\"ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all\">");

            final String activeCss = " ui-tabs-active ui-state-active";
            boolean notYetSelected = true;

            for (LinkTabTag tab : tabs) {
                boolean isSelected = tab.isInitiallySelected() && notYetSelected;
                String css = "ui-state-default ui-corner-top";
                String tag = "a href=\"" + tab.getTabHref() + "\"";
                if (isSelected) {
                    css += " " + activeCss;
                    tag = "a href=\"javascript:void(0);\"";
                }
                String tid = "";
                if (!StringUtils.isEmpty(tab.getTabId())) {
                    tid = " id=\"" + tab.getTabId() + "\"";
                }
                pageContext.getOut().println("<li role=\"tab\" " + tid + " class=\"" + css + "\"><" + tag + " class=\"ui-tabs-anchor\">" + tab.getSelectorName() + "</a></li>");
                notYetSelected = notYetSelected || isSelected;
            }
            pageContext.getOut().println("</ul>");

            pageContext.getOut().write(bodyContent.getString());
            pageContext.getOut().println("</div>");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}