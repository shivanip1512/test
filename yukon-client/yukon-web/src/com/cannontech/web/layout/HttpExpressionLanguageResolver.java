package com.cannontech.web.layout;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface HttpExpressionLanguageResolver {

    public abstract List<String> resolveElExpressions(
            List<String> labelArgumentExpressions, HttpServletRequest request);

    public abstract String resolveElExpression(String expression,
            HttpServletRequest request);

}