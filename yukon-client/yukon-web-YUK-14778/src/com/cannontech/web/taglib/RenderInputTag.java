package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputSecurity;

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

        Object oldInput = null;
        if (input != null) {
            oldInput = request.getAttribute("input");
            request.setAttribute("input", input);
        }

        String renderer = input.getRenderer();

        try {

            LiteYukonUser user = ServletUtil.getYukonUser(request);
            InputSecurity security = input.getSecurity();

            // Include the input's renderer jsp if editable other wise include
            // the plain text renderer jsp
            if (security.isEditable(user)) {
                pageContext.include("/WEB-INF/pages/input/" + renderer);
            } else {
                pageContext.include("/WEB-INF/pages/input/plainTextType.jsp");
            }
        } catch (ServletException e) {
            throw new JspException("Couldn't include renderer: " + renderer, e);
        }

        if (oldInput != null) {
            request.setAttribute("input", oldInput);
        }
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

}
