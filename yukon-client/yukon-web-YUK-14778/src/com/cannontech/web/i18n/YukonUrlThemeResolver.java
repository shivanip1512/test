package com.cannontech.web.i18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ThemeResolver;

/**
 * Theme resolver that resolves the theme from a url param
 */
public class YukonUrlThemeResolver implements ThemeResolver {
    private String defaultThemeName = "";

    @Override
    public String resolveThemeName(HttpServletRequest request) {
    	String themeName = ServletRequestUtils.getStringParameter(request, "theme", defaultThemeName);
        return themeName;
    }

    @Override
    public void setThemeName(HttpServletRequest request,
                             HttpServletResponse response,
                             String themeName) {
        throw new UnsupportedOperationException("Themes can only be changed via the theme url param");
    }
    
    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }
    
}
