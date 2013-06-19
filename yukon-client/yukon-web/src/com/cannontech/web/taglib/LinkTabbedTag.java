package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;



/**
 * USAGE EXAMPLE:

<cti:linkTabbedContainer mode="section">
    <cti:msg var="name_gen" key="yukon.web.billing.tab.generation.title" />
    <cti:linkTab selectorName="${name_gen}" initiallySelected="true" />

    <cti:msg var="name_setup" key="yukon.web.billing.tab.setup.title" />
    <c:url var="url_setup" value="/servlet/BillingServlet" />
    <cti:linkTab selectorName="${name_setup}" tabHref="${url_setup}" />

    <cti:msg var="name_sched" key="yukon.web.billing.tab.schedules.title"/>
    <c:url var="url_sched" value="/user/profile" />
    <cti:linkTab selectorName="${name_sched}" tabHref="${url_sched}" />
</cti:linkTabbedContainer>

 */
public class LinkTabbedTag extends BodyTagSupport {
    private enum Mode{ box, section };

    private Mode mode = Mode.box;
    private String classes = "";
    private List<LinkTabTag> tabs = new ArrayList<LinkTabTag>();
    private String id="";

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

    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    @Override
    public void setBodyContent(BodyContent arg0) {
        super.setBodyContent(arg0);
    }

    public int doEndTag() throws JspException {
        try {
            final boolean doHeaderOutline = this.mode.equals(Mode.section);
            String divCss = this.classes +" tabbedContainer ui-tabs-box ui-tabs ui-widget ui-widget-content ui-corner-all" + (doHeaderOutline ? " ui-tabs-header" : "");
            id = ! StringUtils.isEmpty(id) ? id : "linkTabbedControl_"+ UniqueIdentifierTag.generateIdentifier(pageContext, "tabbedContentSelectorContainer_");
            pageContext.getOut().println("<div id=\"" + id + "\" class=\""+ divCss +"\">");
            pageContext.getOut().println("<ul class=\"ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all\">");
            final String activeCss = " ui-tabs-active ui-state-active";
            boolean notYetSelected = true;
            for (LinkTabTag tab : tabs) {
                boolean isSelected = tab.isInitiallySelected() && notYetSelected;
                String css = "ui-state-default ui-corner-top";
                String tag = "a href=\"" + tab.getTabHref() + "\"";
                if (isSelected) {
                    css += " "+ activeCss;
                    tag = "a href=\"javascript:void(0);\"";
                }
                String tid = "";
                if (! StringUtils.isEmpty(tab.getTabId())) {
                    tid = " id=\""+ tab.getTabId() +"\"";
                }
                pageContext.getOut().println("<li role=\"tab\" "+ tid +" class=\""+ css +"\"><"+ tag +" class=\"ui-tabs-anchor\">" + tab.getSelectorName() + "</a></li>");
                notYetSelected = notYetSelected || isSelected;
            }
            pageContext.getOut().println("</ul>");

            //output the actual tabbed content: CURRENTLY DOES NOTHING
            pageContext.getOut().write(bodyContent.getString());
            pageContext.getOut().println("</div>");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
