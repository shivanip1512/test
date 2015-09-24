package com.cannontech.web.common.resources;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.data.ResourceType;
import com.cannontech.web.common.resources.service.ResourceBundleService;

@Controller
public class ResourceBundleController {

    @Autowired private ResourceBundleService resourceBundleService;
    
    private static final Logger log = YukonLogManager.getLogger(ResourceBundleController.class);
    
    /**
     * getJSResourceBundle Get a {@link ResourceBundle} type Javascript as a serialized object within a {@link HttpServletResponse}
     * <p>
     * <ul>
     * <li>step 1 Request the {@link ResourceBundle} from the {@link ResourceBundleService}
     * processing the series
     * <li>step 2 Set the Cach - Control header for bundle.
     * <li>step 3 Set the contentType described in {@link ResourceBundle} {@link ResourceType}
     * <li>step 4 Write serialized ResourceBundle data to response object.
     * 
     * @see {@link ResourceBundleService}
     * @see {@link ResourceBundle}
     * @see {@link ResourceType}
     *
     * @param packageName - the name of the package request minus the exention ~i.e. .js ext.
     * @param response - HttpServletResponse object to be used as transport for {@ResourceBundle}.
     * @return void
     */
    @RequestMapping("/resource-bundle/js/{packageName}")
    public void getJSResourceBundle(HttpServletResponse response, @PathVariable String packageName) throws Exception
    {
        log.debug("getJSResourceBundle:" + packageName);
        
            ResourceBundle resourceBundleJs = resourceBundleService.getResourceBundle(packageName, ResourceType.JAVASCRIPT);
            response.setDateHeader("Expires", System.currentTimeMillis() + 604800000L);
            response.setContentType(resourceBundleJs.getResourceType().getContentType());
            response.setCharacterEncoding(resourceBundleJs.getResourceType().getEncoding());
            response.getWriter().write(resourceBundleJs.getResourceResult());    
        
    }
    
    /**
     * getCSSResourceBundle Get a {@link ResourceBundle} type CSS as a serialized object within a {@link HttpServletResponse}
     * <p>
     * <ul>
     * <li>step 1 Request the {@link ResourceBundle} from the {@link ResourceBundleService}
     * processing the series
     * <li>step 2 Set the Cach - Control header for bundle.
     * <li>step 3 Set the contentType described in {@link ResourceBundle} {@link ResourceType}
     * <li>step 4 Write serialized ResourceBundle data to response object.
     * 
     * @see {@link ResourceBundleService}
     * @see {@link ResourceBundle}
     * @see {@link ResourceType}
     *
     * @param packageName - the name of the package request minus the exention ~i.e. .CSS ext.
     * @param response - HttpServletResponse object to be used as transport for {@ResourceBundle}.
     * @return void
     */
    @RequestMapping("/resource-bundle/css/{packageName}")
    public void geCssResourceBundle(HttpServletResponse response, @PathVariable String packageName) throws Exception {
            ResourceBundle resourceBundleJs = resourceBundleService.getResourceBundle(packageName, ResourceType.CSS);
            response.setDateHeader("Expires", System.currentTimeMillis() + 604800000L);
            response.setContentType(resourceBundleJs.getResourceType().getContentType());
            response.setCharacterEncoding(resourceBundleJs.getResourceType().getEncoding());
            response.getWriter().write(resourceBundleJs.getResourceResult());    
        
    }
}

