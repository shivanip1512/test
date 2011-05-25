package com.cannontech.web.capcontrol;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.util.NavigableHierarchy;
import com.cannontech.web.taglib.YukonTagSupport;
import com.google.common.collect.Lists;

@Configurable("navigableHierarchyTagPrototype")
public class NavigableHierarchyTag extends YukonTagSupport {

    private NavigableHierarchy<?> hierarchy;
    private String styleClass = null;
    private String var;
    private String depth;
    
    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        out.print("<div class=\"" + styleClass + "\">");
        
        Object root = hierarchy.getNode();
        
        if (root != null) {
            List<NavigableHierarchy<?>> children = Lists.newArrayList();
            children.add(hierarchy);
            doTagZones(out, children, 0);
        }
        
        out.print("</div>");
    }

    /**
     * Recursive function to generate code for the Zones and children.
     * 
     * @throws JspException
     * @throws IOException
     */
    private void doTagZones(JspWriter out, List<? extends NavigableHierarchy<?>> children, Integer childDepth) throws JspException, IOException{

        out.print("<ul>");
        for (NavigableHierarchy<?> child : children) {            
            out.print("<li>");
            getJspContext().setAttribute(var, child.getNode());
            getJspContext().setAttribute(depth, childDepth);
            getJspBody().invoke(out);
            List<? extends NavigableHierarchy<?>> grandChildren = child.getChildren();
            if (!grandChildren.isEmpty()) {
                Integer newDepth = childDepth + 1;
                doTagZones(out, grandChildren, newDepth);
            }
            out.print("</li>");

        }
        out.print("</ul>");
    }
    
    public void setHierarchy(NavigableHierarchy<?> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    public void setVar(String var) {
        this.var = var;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }
    
}
