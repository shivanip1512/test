package com.cannontech.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.common.util.CtiUtilities;

public class YukonBaseStaticController extends AbstractController {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private String prefix = "";
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String pathWithinServletMapping = urlPathHelper.getPathWithinServletMapping(request);
        File base = new File(getYukonBase() + prefix);
        String path = base.getPath() + pathWithinServletMapping;
        
        File file = new File(path);
        boolean badFile = !isFileRootedInBase(base, file);
        if (badFile) {
            throw new RuntimeException("illegal file access");
        }
        
        InputStream inputStream = new FileInputStream(path);
        OutputStream outputStream = response.getOutputStream();
        
        FileCopyUtils.copy(inputStream, outputStream);
        
        return null;
    }

    private boolean isFileRootedInBase(File base, File file) throws IOException {
        String canonicalPath = file.getCanonicalPath();
        boolean result = canonicalPath.startsWith(base.getCanonicalPath());
        return result;
    }
    
    protected String getYukonBase() {
        return CtiUtilities.getYukonBase();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

}
