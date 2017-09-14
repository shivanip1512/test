package com.cannontech.web.smartNotifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;

@Controller
@RequestMapping("/*")
public class SmartNotificationsController {
    
    @Autowired private SmartNotificationSubscriptionService subscriptionService;
    @Autowired private ContactDao contactDao;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private SmartNotificationSubscriptionValidator subscriptionValidator;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private final static String baseKey = "yukon.web.modules.smartNotifications.";

    @RequestMapping(value="settings", method=RequestMethod.GET)
    public String settings(ModelMap model, YukonUserContext userContext) {
        //TODO: Get Users Subscriptions - this may move to user profile page
        return "settings.jsp";
    }
    
    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String eventDetail(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        //TODO: Get Subscription details
        return "eventDetail.jsp";
    }
    
    @RequestMapping(value="subscription/popup/{type}", method=RequestMethod.GET)
    public String subscriptionPopup(ModelMap model, YukonUserContext userContext, @PathVariable SmartNotificationEventType type,
                                    HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        SmartNotificationSubscription subscription = new SmartNotificationSubscription();
        subscription.setType(type);
        //TODO: Move a lot of this code specific to Device Data Monitors
        int monitorId = ServletRequestUtils.getIntParameter(request, "monitorId", 0);
        if (type.equals(SmartNotificationEventType.DEVICE_DATA_MONITOR)) {
            if (monitorId != 0) {
                subscription.addParameters("monitorId",  monitorId);
                model.addAttribute("monitorName", monitorCacheService.getDeviceMonitor(monitorId).getName());
            }
        }
        //check if subscription already exists for user and type
        List<SmartNotificationSubscription> subscriptions = subscriptionDao.getSubscriptions(userContext.getYukonUser().getUserID(), type);
        boolean existingFound = false;
        if (!subscriptions.isEmpty()) {
            if (type.equals(SmartNotificationEventType.DEVICE_DATA_MONITOR)) {
                if (monitorId != 0) {
                    Optional<SmartNotificationSubscription> ddmSub = subscriptions.stream()
                        .filter(sub -> monitorId == Integer
                            .parseInt((String) sub.getParameters().get("monitorId")))
                        .findFirst();
                    if (ddmSub.isPresent()) {
                        subscription = ddmSub.get();
                        existingFound = true;
                    }
                }
            } else {
                subscription = subscriptions.get(0);
                existingFound = true;
            }
        } 
        if (existingFound) {
            model.addAttribute("mode", PageEditMode.EDIT);
        } else {
            setDefaultEmail(userContext, subscription);
        }
        setupPopupModel(model);
        model.addAttribute("subscription", subscription);
        return "subscriptionPopup.jsp";
    }
    
    @RequestMapping(value="subscription/create", method=RequestMethod.GET)
    public String createSubscription(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.CREATE);
        SmartNotificationSubscription subscription = new SmartNotificationSubscription();
        setupPopupModel(model);
        setDefaultEmail(userContext, subscription);
        model.addAttribute("subscription", subscription);
        return "subscriptionPopup.jsp";
    }
    
    private void setDefaultEmail(YukonUserContext userContext, SmartNotificationSubscription subscription) {
        List<LiteContact> contacts = contactDao.getContactsByLoginId(userContext.getYukonUser().getUserID());
        ContactDto contact = null;
        if(!contacts.isEmpty()) {
            contact = operatorAccountService.getContactDto(contacts.get(0).getContactID(), false, false, true, userContext);
            subscription.setRecipient(contact.getEmail());
        }
    }
    
    private void setupPopupModel(ModelMap model) {
        model.addAttribute("eventTypes", SmartNotificationEventType.values());
        model.addAttribute("frequencies", SmartNotificationFrequency.values());
        model.addAttribute("mediaTypes", SmartNotificationMedia.values());
        model.addAttribute("detailTypes", SmartNotificationVerbosity.values());
        model.addAttribute("deviceDataMonitors", monitorCacheService.getDeviceDataMonitors());
    }
    
    @RequestMapping(value="subscription/saveDetails", method=RequestMethod.POST)
    public String saveDetails(ModelMap model, YukonUserContext userContext, HttpServletResponse resp,
                              @ModelAttribute("subscription") SmartNotificationSubscription subscription, BindingResult result) throws Exception {
        subscription.setUserId(userContext.getYukonUser().getUserID());
        subscriptionValidator.doValidation(subscription, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            setupPopupModel(model);
            return "subscriptionPopup.jsp";
        }
        subscriptionService.saveSubscription(subscription);
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        json.put("successMsg", messageSourceAccessor.getMessage(baseKey + "saveSuccessful"));
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        return null;
    }

}
