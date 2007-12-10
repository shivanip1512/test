package com.cannontech.web.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller used to serve up static resources
 */
public class StaticResourceController extends AbstractController {

    private Resource resource = null;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        InputStream inputStream = resource.getInputStream();
        OutputStream outputStream = response.getOutputStream();

        FileCopyUtils.copy(inputStream, outputStream);

        return null;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

}
