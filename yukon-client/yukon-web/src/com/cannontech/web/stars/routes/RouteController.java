package com.cannontech.web.stars.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/device/routes/")
public class RouteController {    
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String redirectListPageLink = "redirect:/stars/device/routes/list";
    private static final Logger log = YukonLogManager.getLogger(RouteController.class);
    
    @GetMapping("list")
    public String list(ModelMap model, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        ResponseEntity<List<? extends Object>> response = null;
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl);
            response = apiRequestHelper.callAPIForList(userContext, request, url, Object.class, HttpMethod.GET);

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            return redirectListPageLink;
        }

        
          List<RouteBaseModel> routeList = new ArrayList<>();
          List<RouteBaseModel> routeListWithoutCcu = new ArrayList<>();
         
                
        if (response.getStatusCode() == HttpStatus.OK) {
            routeList = (List<RouteBaseModel>) response.getBody();
            
            routeListWithoutCcu = routeList.stream()
                                           .filter(c -> c.getSignalTransmitterId() != 0 && !c.getDeviceType().isCcu())
                                           .collect(Collectors.toList());
            /*
             * routeList.forEach(route -> {
             * if(route.getSignalTransmitterId() != 0 && (!route.getDeviceType().isCcu())) {
             * routeList = (List<RouteBaseModel>) response.getBody();
             * }
             * });
             */
        }
        model.addAttribute("routeList", routeListWithoutCcu);
        return "/routes/list.jsp";
        
    }

}
