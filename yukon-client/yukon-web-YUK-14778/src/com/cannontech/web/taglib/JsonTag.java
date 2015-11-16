package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.web.util.HtmlUtils;

import com.cannontech.common.util.JsonUtils;

public class JsonTag extends YukonTagSupport {
    private String id;
    private Object object;
    private String var;

    @Override
    public void doTag() throws JspException, IOException {
        if (id != null && var != null) {
            throw new IllegalArgumentException("Cannot use JsonTag with both id and var set.");
        }

        String htmlEscapedJson = HtmlUtils.htmlEscape(JsonUtils.toJson(object));
        if (var != null) {
            getJspContext().setAttribute(var, htmlEscapedJson);
        } else if (id != null) {
            JspWriter out = getJspContext().getOut();
            out.print("<div id=\""+ id +"\" class=\"dn\">");
            out.print(htmlEscapedJson);
            out.print("</div>");
        } else {
            getJspContext().getOut().print(htmlEscapedJson);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
