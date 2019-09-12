package com.cannontech.web.collectionActions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;

/**
 * Handles request directly off the /collectionActions/* url.
 */
@Controller
@RequestMapping("/*")
public class CollectionActionsController {
    private static final Logger log = YukonLogManager.getLogger(CollectionActionsController.class);
    
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    
    private static final String baseKey ="yukon.web.modules.tools.collectionActions";
    
    @RequestMapping("home")
    public String home(ModelMap model, HttpServletRequest request, @RequestParam(defaultValue = "false") boolean isFileUpload, 
                       FlashScope flashScope) throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        if (!StringUtils.isBlank(errorMsg)) {
            model.addAttribute("errorMsg", errorMsg);
        }
        
        String redirectUrl = ServletRequestUtils.getStringParameter(request, "redirectUrl", null);
        if (!StringUtils.isBlank(redirectUrl)) {
            model.addAttribute("redirectUrl", redirectUrl);
            String action = ServletRequestUtils.getStringParameter(request, "action", null);
            model.addAttribute("actionString", action);
        }
        
        //check for device collection
        try {
            DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
            model.addAttribute("deviceCollection", collection);
        } catch (IllegalArgumentException exception) {
            //device collection has not been picked yet, not an error
        }

        return "collectionActionsHome.jsp";
    }
    
    @RequestMapping("deviceSelectionGetDevices")
    public String deviceSelectionGetDevices(ModelMap model, HttpServletRequest request, RedirectAttributes redirectAtts,
            @RequestParam(defaultValue = "false") boolean isFileUpload, FlashScope flashScope, YukonUserContext yukonUserContext)
            throws ServletRequestBindingException {
        String redirectUrl = ServletRequestUtils.getStringParameter(request, "redirectUrl", null);
        String action = ServletRequestUtils.getStringParameter(request, "actionString", null);
        try {
            String view = collectionActions(model, request, isFileUpload, flashScope, yukonUserContext);
            if (!StringUtils.isBlank(redirectUrl)) {
                model.addAttribute("redirectUrl", redirectUrl);
                model.addAttribute("actionString", action);
            } 
            return view;
        } catch (DeviceCollectionCreationException e) {
            log.error("There was an issue creating a device collection.", e);
            model.addAttribute("errorMsg", e.getMessage());
            return "collectionActionsHome.jsp";
        }
    }
    
    // COLLECTION ACTIONS
    @RequestMapping("collectionActions")
    public String collectionActions(ModelMap model, HttpServletRequest request,
            @RequestParam(defaultValue = "false") boolean isFileUpload, FlashScope flashScope, YukonUserContext userContext)
            throws ServletRequestBindingException {

        String view = "collectionActions.jsp";
        
        try {
        
            DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
            if (isFileUpload) {
                Map<String, String> collectionParameters = collection.getCollectionParameters();
                model.addAllAttributes(collectionParameters);
            }
            model.addAttribute("deviceCollection", collection);
            model.addAttribute("deviceErrors", collection.getErrorDevices());
            model.addAttribute("deviceErrorCount", collection.getDeviceErrorCount());
            if (collection.getErrorDevices().size() > 0) {
                String totalErrors = Integer.toString(collection.getErrorDevices().size());
                flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".deviceUploadFailed", totalErrors));
            }
        } catch (ObjectMappingException | UnsupportedOperationException e) {
            log.error("There was an issue creating a device collection.", e);
            model.addAttribute("errorMsg", e.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("There was an issue creating a device collection.", exception);
            flashScope.setError(new YukonMessageSourceResolvable(exception.getMessage()));
        }
        
        return view;
    }

}
