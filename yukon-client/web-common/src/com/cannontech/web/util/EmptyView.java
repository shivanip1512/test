package com.cannontech.web.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * A view which returns an empty document. This is useful for AJAX calls for
 * which care only about success or failure.
 */
public class EmptyView extends AbstractView {

    @SuppressWarnings("unchecked")
    @Override
    protected void renderMergedOutputModel(Map model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        response.getWriter().print("<span></span>");
    }
}
