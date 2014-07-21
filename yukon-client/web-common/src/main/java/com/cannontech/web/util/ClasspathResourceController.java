package com.cannontech.web.util;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

public class ClasspathResourceController extends AbstractController {
    private String classpathPrefix = "";
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String pathWithinServletMapping = urlPathHelper.getPathWithinServletMapping(request);
        String path = classpathPrefix + pathWithinServletMapping;
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(path);
        ServletOutputStream outputStream = response.getOutputStream();
        
        FileCopyUtils.copy(resourceAsStream, outputStream);

        return null;
    }

    public String getClasspathPrefix() {
        return classpathPrefix;
    }

    public void setClasspathPrefix(String classpathPrefix) {
        this.classpathPrefix = classpathPrefix;
    }

    public UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

}
