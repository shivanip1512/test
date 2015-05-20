package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import com.cannontech.common.util.StringUtils;

public class ListAsSafeString extends YukonTagSupport {

    private List<String> list;
    private String var;

    @Override
    public void doTag() throws JspException, IOException {

        String safeString = StringUtils.listAsJsSafeString(list);
        if (var != null) {
            getJspContext().setAttribute(var, safeString);
        } else {
            getJspContext().getOut().print(safeString);
        }
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
