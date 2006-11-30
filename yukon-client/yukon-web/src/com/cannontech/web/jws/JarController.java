package com.cannontech.web.jws;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.common.util.CtiUtilities;

public class JarController extends AbstractController {
    private File jarBaseFile = new File(CtiUtilities.getYukonBase(), "Client/bin");

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // set mime type
        response.setContentType("application/java-archive");
        
        // get name of jar
        String uri = request.getRequestURI();
        File uriFile = new File(uri);
        String jarName = uriFile.getName();
        
        // construct file that points to jar
        File jarFile = new File(jarBaseFile, jarName);
        
        // set content length
        response.setContentLength((int) jarFile.length());
        
        // set last-modified header
        response.setDateHeader("Last-Modified", jarFile.lastModified());
        
        //copy bytes to output
        FileInputStream fis = new FileInputStream(jarFile);
        ServletOutputStream sos = response.getOutputStream();
        FileCopyUtils.copy(fis, sos);
        return null;
    }

}
