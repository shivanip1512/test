package com.cannontech.web.util;

import java.io.FileInputStream;
import java.io.FileReader;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.ResourceAttributes;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.ResourceNotFoundException;
import com.cannontech.common.util.CtiUtilities;

public class YukonBaseStaticController extends AbstractController {
    private static final Logger log = YukonLogManager.getLogger(YukonBaseStaticController.class);
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private String prefix;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FileDirContext dirContext = new FileDirContext();
        dirContext.setDocBase(CtiUtilities.getYukonBase() + prefix);
        String docBase = dirContext.getDocBase();

        String pathWithinServletMapping = urlPathHelper.getPathWithinServletMapping(request);
        Attributes atts = null;
        try {
            atts = dirContext.getAttributes(pathWithinServletMapping);
        } catch (NamingException e) {
            log.warn("file not found for download: " + pathWithinServletMapping.substring(pathWithinServletMapping.lastIndexOf("/") + 1));
            throw new ResourceNotFoundException();
        }

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
        this.prefix = prefix;
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }
}
