package com.cannontech.web.smartNotifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
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
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.google.common.collect.Lists;

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
    @Autowired private DeviceDataMonitorSubscriptionHelper ddmHelper;

    private final static String baseKey = "yukon.web.modules.smartNotifications.";
    
    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public String eventDetail(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        //TODO: Get Subscription details
        return "eventDetail.jsp";
    }
    
    @RequestMapping(value="subscriptions", method=RequestMethod.GET)
    public String subscriptions(@DefaultSort(dir=Direction.asc, sort="type") SortingParameters sorting, PagingParameters paging, 
                                ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        List<SmartNotificationSubscription> subscriptions = subscriptionDao.getSubscriptions(userContext.getYukonUser().getUserID());
        
        SearchResults<SmartNotificationSubscription> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, subscriptions.size());

        SubscriptionSortBy sortBy = SubscriptionSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<SmartNotificationSubscription> itemList = Lists.newArrayList(subscriptions);
        
        Comparator<SmartNotificationSubscription> comparator = (o1, o2) -> o1.getType().compareTo(o2.getType());
        if (sortBy == SubscriptionSortBy.frequency) {
            comparator = (o1, o2) -> o1.getFrequency().compareTo(o2.getFrequency());
        } else if (sortBy == SubscriptionSortBy.media) {
            comparator = (o1, o2) -> o1.getMedia().compareTo(o2.getMedia());
        } else if (sortBy == SubscriptionSortBy.recipient) {
            comparator = (o1, o2) -> o1.getRecipient().compareTo(o2.getRecipient());
        } else if (sortBy == SubscriptionSortBy.detail) {
            comparator = (o1, o2) -> o1.getVerbosity().compareTo(o2.getVerbosity());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        List<SortableColumn> columns = new ArrayList<>();
        for (SubscriptionSortBy column : SubscriptionSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, subscriptions.size());
        searchResult.setResultList(itemList);
        
        model.addAttribute("subscriptions", searchResult);
        return "subscriptions.jsp";
    }
    
    @RequestMapping(value="subscription/popup/{type}", method=RequestMethod.GET)
    public String subscriptionPopup(ModelMap model, YukonUserContext userContext, @PathVariable SmartNotificationEventType type,
                                    HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        SmartNotificationSubscription subscription = new SmartNotificationSubscription();
        subscription.setType(type);
        setDefaultEmail(userContext, subscription);
        //check if subscription already exists for user and type
        List<SmartNotificationSubscription> subscriptions = subscriptionDao.getSubscriptions(userContext.getYukonUser().getUserID(), type);
        if (type.equals(SmartNotificationEventType.DEVICE_DATA_MONITOR)) {
            subscription = ddmHelper.retrieveSubscription(model, request, subscriptions, subscription);
        }
        else if (!subscriptions.isEmpty()) {
            subscription = subscriptions.get(0);
        }
        setupPopupModel(model);
        model.addAttribute("subscription", subscription);
        return "subscriptionPopup.jsp";
    }
    
    @RequestMapping(value="subscription/{id}/edit", method=RequestMethod.GET)
    public String editSubscription(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.EDIT);
        //TODO: check that user created the subscription
        SmartNotificationSubscription subscription = subscriptionDao.getSubscription(id);
        model.addAttribute("subscription", subscription);
        if (subscription.getType().equals(SmartNotificationEventType.DEVICE_DATA_MONITOR)) {
            ddmHelper.retrieveMonitor(model, subscription);
        }
        setupPopupModel(model);
        return "subscriptionPopup.jsp";
    }
    
    @RequestMapping(value="subscription/{id}/unsubscribe", method=RequestMethod.POST)
    public void removeSubscription(YukonUserContext userContext, @PathVariable int id, HttpServletResponse resp, FlashScope flash) {
        //TODO: check that user created the subscription
        SmartNotificationSubscription subscription = subscriptionDao.getSubscription(id);
        subscriptionService.deleteSubscription(id);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "unsubscribeSuccess", subscription.getType().toString()));
        resp.setStatus(HttpStatus.NO_CONTENT.value());
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
    
    public enum SubscriptionSortBy implements DisplayableEnum {

        type,
        frequency,
        media,
        recipient,
        detail;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.smartNotifications." + name();
        }
    }

}
