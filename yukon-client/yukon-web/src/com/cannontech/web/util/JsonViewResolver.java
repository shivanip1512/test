package com.cannontech.web.util;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class JsonViewResolver implements ViewResolver {

    public JsonViewResolver() {
        super();
    }

    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (viewName.equals("json")) {
            return new JsonView();
        }
        return null;
    }

}
