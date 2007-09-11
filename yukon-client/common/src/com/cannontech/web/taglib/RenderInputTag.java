package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.web.input.Input;

/**
 * Tag used to render an Input. This tag gets the rendering jsp from the input
 * and then passes the input itself to the rendering jsp.
 */
public class RenderInputTag extends SimpleTagSupport {
    private Input input;

    public RenderInputTag() {
        super();
    }

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();

        ServletRequest request = pageContext.getRequest();
        Object oldValue = null;
        if (input != null) {
            oldValue = request.getAttribute("input");
            request.setAttribute("input", input);
        }

        String renderer = input.getRenderer();

        try {
            pageContext.include("/WEB-INF/pages/input/" + renderer);
        } catch (ServletException e) {
            throw new JspException("Couldn't include renderer: " + renderer);
        }

        if (oldValue != null) {
            request.setAttribute("input", oldValue);
        }
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

}
