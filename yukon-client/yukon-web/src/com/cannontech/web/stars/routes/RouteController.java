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
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/device/routes/")
public class RouteController {
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private IDatabaseCache serverDatabaseCache;

    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String redirectListPageLink = "redirect:/stars/device/routes/list";
    private static final Logger log = YukonLogManager.getLogger(RouteController.class);

    @GetMapping("list")
    public String list(ModelMap model, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        ResponseEntity<List<? extends Object>> response = null;
        List<DeviceBaseModel> nonCCUAndMacroRoutes = new ArrayList<DeviceBaseModel>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl);
            response = apiRequestHelper.callAPIForList(userContext, request, url, Object.class, HttpMethod.GET);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<DeviceBaseModel> allRoutes = (List<DeviceBaseModel>) response.getBody();
                for (DeviceBaseModel deviceModel : allRoutes) {
                    ObjectMapper mapper = new ObjectMapper();
                    DeviceBaseModel baseModel = mapper.readValue(mapper.writeValueAsString(deviceModel),
                            DeviceBaseModel.class);
                    // Devices with type CCU710A,CCU711,CCU721 and Routes with type ROUTE_CCU are removed 
                    // in separate conditions below
                    if (baseModel != null && baseModel.getDeviceType() != null && (!baseModel.getDeviceType().isCcu())
                            && baseModel.getDeviceType() != PaoType.ROUTE_CCU) {
                        nonCCUAndMacroRoutes.add(baseModel);
                    }
                }
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            return redirectListPageLink;
        } catch (JsonProcessingException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            return redirectListPageLink;
        }
        model.addAttribute("nonCCUAndMacroRoutes", nonCCUAndMacroRoutes);
        return "/routes/list.jsp";
    }

}
