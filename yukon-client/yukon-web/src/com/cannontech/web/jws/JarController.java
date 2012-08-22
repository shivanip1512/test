package com.cannontech.web.jws;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.google.common.collect.ImmutableMap;

public class JarController extends AbstractController {
    private File jarBaseFile = new File(CtiUtilities.getYukonBase(), "Client/bin");
    private Logger log = YukonLogManager.getLogger(JarController.class);
    private final static ImmutableMap<String, String> filenameMappings;
    static {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("org.springframework.aop.jar", "spring-aop-3.0.5.jar");
        builder.put("org.springframework.aspects.jar", "spring-aspects-3.0.5.jar");
        builder.put("org.springframework.beans.jar", "spring-beans-3.0.5.jar");
        builder.put("org.springframework.context.jar", "spring-context-3.0.5.jar");
        builder.put("org.springframework.jdbc.jar", "spring-jdbc-3.0.5.jar");
        builder.put("org.springframework.transaction.jar", "spring-transaction-3.0.5.jar");
        builder.put("org.springframework.web.jar", "spring-web-3.0.5.jar");
        filenameMappings = builder.build();
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // set mime type
        response.setContentType("application/java-archive");
        
        // get name of jar
        String uri = request.getRequestURI();
        File uriFile = new File(uri);
        String jarName = uriFile.getName();
        if (filenameMappings.containsKey(jarName)) {
            jarName = filenameMappings.get(jarName);
        }

        // construct file that points to jar
        File jarFile = new File(jarBaseFile, jarName);

        // set content length
        response.setContentLength((int) jarFile.length());

        // set last-modified header
        response.setDateHeader("Last-Modified", jarFile.lastModified());

        //copy bytes to output
        boolean alreadyBad = false;
        
        // the following is kind of copied from FileCopyUtils, I added it here
        // so that I could prevent a full stack trace dump on exceptions
        FileInputStream fis = null;
        ServletOutputStream sos = null;
        try {
            fis = new FileInputStream(jarFile);
            sos = response.getOutputStream();
            int byteCount = 0;
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                sos.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            sos.flush();
        } catch (IOException e) {
            // For some reason, this exception occurs often when JWS downloads the JARs.
            // Everytime this happens, the application is still able to start just fine,
            // so I'd like to ignore it for the time being.
            log.debug("Got Exception while downloading Web Start JAR (this can be ignored): " + e);
            alreadyBad = true;
        } finally {
            try {
                if(fis != null) fis.close();
            }
            catch (IOException ex) {
                if (!alreadyBad) logger.warn("Could not close InputStream: " + ex);
            }
            try {
                if(sos != null) sos.close();
            }
            catch (IOException ex) {
                if (!alreadyBad) logger.warn("Could not close OutputStream: " + ex);
            }
        }
        return null;
    }

}
