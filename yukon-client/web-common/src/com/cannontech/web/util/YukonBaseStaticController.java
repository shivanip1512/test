package com.cannontech.web.util;

import java.io.FileInputStream;
import java.io.FileReader;

import javax.naming.directory.Attributes;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.ResourceAttributes;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.common.util.CtiUtilities;

public class YukonBaseStaticController extends AbstractController {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private String docBase;
    private final FileDirContext dirContext = new FileDirContext();

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String pathWithinServletMapping = urlPathHelper.getPathWithinServletMapping(request);

        Attributes atts = dirContext.getAttributes(pathWithinServletMapping);
        if (!(atts instanceof ResourceAttributes)) {
            atts = new ResourceAttributes(atts);
        }
        ResourceAttributes attributes = (ResourceAttributes) atts;

        String filename = attributes.getCanonicalPath();
        if (!filename.startsWith(docBase)) {
            throw new RuntimeException("illegal file access");
        }

        response.setContentLength((int) attributes.getContentLength());
        ServletContext servletContext = request.getServletContext();
        String contentType = servletContext.getMimeType(pathWithinServletMapping);
        response.setContentType(contentType);
        response.setHeader("ETag", attributes.getETag());
        response.setHeader("Last-Modified", attributes.getLastModifiedHttp());

        // Text files need to take character encoding into account.  We determine what is a text
        // file the same way Tomcat does in DefaultServlet.serveResource.
        if (contentType == null || contentType.startsWith("text") || contentType.endsWith("xml")
                || contentType.contains("/javascript")) {
            response.setCharacterEncoding("UTF-8");
            FileCopyUtils.copy(new FileReader(filename), response.getWriter());
        } else {
            FileCopyUtils.copy(new FileInputStream(filename), response.getOutputStream());
        }

        return null;
    }

    public void setPrefix(String prefix) {
        dirContext.setDocBase(CtiUtilities.getYukonBase() + prefix);
        docBase = dirContext.getDocBase();
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }
}
