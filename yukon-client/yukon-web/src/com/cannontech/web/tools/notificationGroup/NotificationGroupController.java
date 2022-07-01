package com.cannontech.web.tools.notificationGroup;

import java.util.ArrayList;
import java.util.List;

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
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.notificationGroup.NotificationGroup;

@Controller
@RequestMapping("/notificationGroup/*")
public class NotificationGroupController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;

    private static final Logger log = YukonLogManager.getLogger(NotificationGroupController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String redirectListPageLink = "redirect:/tools/notificationGroup/list";

    @GetMapping("create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);

        return "/notificationGroup/view.jsp";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("list")
    public String list(ModelMap model, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {

        ResponseEntity<? extends Object> response = null;
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.notificationGroupUrl);
            response = apiRequestHelper.callAPIForList(userContext, request, url, NotificationGroup.class, HttpMethod.GET,
                    NotificationGroup.class);
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            flash.setError(
                    new YukonMessageSourceResolvable("yukon.web.modules.tools.notificationGroup.filter.error", ex.getMessage()));
            return redirectListPageLink;
        }

        List<NotificationGroup> notificationGroups = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            notificationGroups = (List<NotificationGroup>) response.getBody();
        }
        model.addAttribute("notificationGroups", notificationGroups);
        return "/notificationGroup/list.jsp";
    }
}
