package com.cannontech.web.common.resources;

import java.io.IOException;

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
import com.cannontech.web.common.resources.service.error.ResourceBundleException;

@Controller
public class ResourceBundleController {

    @Autowired private ResourceBundleService resourceBundleService;
    
    private static final Logger log = YukonLogManager.getLogger(ResourceBundleController.class);
    
    /**
     * Get a {@link ResourceBundle} by ResourceType and String Identifier serialized as object within a {@link HttpServletResponse}
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
    @RequestMapping("/resource-bundle/{resourceType}/{packageName}")
    public void getResourceBundle(HttpServletResponse response, @PathVariable ResourceType resourceType, @PathVariable String packageName) throws ResourceBundleException, IOException {
            log.debug("getJSResourceBundle:" + packageName);
            ResourceBundle resourceBundleResponse = resourceBundleService.getResourceBundle(packageName, resourceType);
            response.setDateHeader("Expires", resourceBundleResponse.getTimestamp().plus(resourceBundleResponse.getTTL()).getMillis());
            response.setContentType(resourceBundleResponse.getResourceType().getContentType());
            response.setCharacterEncoding(resourceBundleResponse.getResourceType().getEncoding());
            response.getWriter().write(resourceBundleResponse.getResourceResult());    
    }
}

