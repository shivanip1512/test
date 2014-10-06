package com.cannontech.esub.web.filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonImage;

/**
 * Forwards all request for any file that matches this filter to the /images
 * directory. Filter should only be used for urls matching '/esub/*'.
 * 
 * @author alauinger
 */
public class ImageFilter implements Filter {
    
    private static final Logger log = YukonLogManager.getLogger(ImageFilter.class);
    private FilterConfig config;
    
    public void init(FilterConfig fc) throws ServletException {
        config = fc;
    }
    
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest hreq = (HttpServletRequest) req;
        
        String uri = hreq.getRequestURI();
        String conPath = hreq.getContextPath();
        
        if (!(uri.endsWith(".gif") || uri.endsWith(".png") || uri.endsWith(".jpg"))) {
            chain.doFilter(req, resp);
            return;
        }
        
        String imgPath = uri.replaceFirst(conPath, "");
        
        if (imgPath.startsWith("/esub/images/")) {
            ensureImageExists(URLDecoder.decode(config.getServletContext().getRealPath(imgPath), "UTF-8"));
            chain.doFilter(req, resp);
        } else {
            imgPath = "/esub/images" + imgPath.substring(imgPath.lastIndexOf("/"));
            ensureImageExists(URLDecoder.decode(config.getServletContext().getRealPath(imgPath), "UTF-8"));
            config.getServletContext().getRequestDispatcher(imgPath).forward(req, resp);
        }
        
    }
    
    public void destroy() {
        config = null;
    }
    
    private void ensureImageExists(String imagePath) {
        
        File iFile = new File(imagePath);
        if (iFile.exists())
            return;
        
        int indx = imagePath.lastIndexOf("\\");
        if (indx == -1) {
            indx = imagePath.lastIndexOf("/");
        }
        
        if (indx == -1) {
            log.error("Unable to determine image name from path: " + imagePath);
            return;
        }
        
        String imageName = imagePath.substring(indx + 1);
        
        log.info("Attempting to locate image: " + imageName + " in the database");
        
        for (LiteYukonImage img : DefaultDatabaseCache.getInstance().getImages().values()) {
            
            if (img.getImageName().equals(imageName)) {
                
                log.info("Image found, copying to: " + imagePath);
                byte[] imgBuf = img.getImageValue();
                OutputStream out = null;
                try {
                    
                    out = new FileOutputStream(imagePath);
                    out.write(imgBuf);
                    
                    return;
                    
                } catch (IOException ioe) {
                    log.error("Error copying " + imageName + " to " + imagePath, ioe);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ioe2) {
                        }
                    }
                }
            }
        }
        
        log.info("Unable to locate " + imageName + " in the database, sorry");
    }
    
}