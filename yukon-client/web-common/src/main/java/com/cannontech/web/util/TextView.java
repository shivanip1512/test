package com.cannontech.web.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

public class TextView extends AbstractView {

    private String text;

    public TextView(String text) {
        this.text = text;
    }

    public TextView() {
        this.text = "";
    }
    
    @Override
    protected void renderMergedOutputModel(Map model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        response.getWriter().print(text);
    }

}
