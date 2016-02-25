package com.cannontech.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.common.util.CtiUtilities;

public class YukonBaseStaticController extends AbstractController {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private String prefix;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	 
    	 String docBase = CtiUtilities.getYukonBase() + prefix;
         String pathWithinServletMapping = urlPathHelper.getPathWithinServletMapping(request);
         
         File file = new File(docBase+pathWithinServletMapping);
         if (!file.exists()) {
             throw new RuntimeException("illegal file access");
         }

         String filename = file.getCanonicalPath();
         response.setContentLength((int) file.length());
         ServletContext servletContext = request.getServletContext();
         String contentType = servletContext.getMimeType(pathWithinServletMapping);
         response.setContentType(contentType);
         //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19
         response.setHeader("ETag", String.valueOf(file.lastModified()));
         DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
         String lastModifiedHttp = formatter.format(ZonedDateTime.ofInstant(new Date(file.lastModified()).toInstant(), ZoneId.systemDefault()));
         response.setHeader("Last-Modified", lastModifiedHttp);
         
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