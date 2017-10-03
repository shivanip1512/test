package com.cannontech.web.smartNotifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao.SortBy;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventData;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.web.util.WebFileUtils;
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
    @Autowired private UserPreferenceService userPreferenceService;
    @Autowired private SmartNotificationEventDao eventDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;

    private final static String baseKey = "yukon.web.modules.smartNotifications.";
    
    @RequestMapping(value="events/{type}", method=RequestMethod.GET)
    public String eventDetailByType(@PathVariable String type, @DefaultSort(dir=Direction.desc, sort="timestamp") SortingParameters sorting, 
                               @DefaultItemsPerPage(value=250) PagingParameters paging, ModelMap model, 
                               YukonUserContext userContext, @ModelAttribute("filter") SmartNotificationEventFilter filter, BindingResult result) {
        return retrieveEventDetail(type, null, sorting, paging, userContext, model, filter);
    }
    
    @RequestMapping(value="events/{type}/{parameter}", method=RequestMethod.GET)
    public String eventDetailByTypeId(@PathVariable String type, @PathVariable String parameter, @DefaultSort(dir=Direction.asc, sort="timestamp") SortingParameters sorting, 
                               @DefaultItemsPerPage(value=250) PagingParameters paging, ModelMap model, 
                               YukonUserContext userContext, @ModelAttribute("filter") SmartNotificationEventFilter filter, BindingResult result) {
        return retrieveEventDetail(type, parameter, sorting, paging, userContext, model, filter);
    }
    
    private String retrieveEventDetail(String type, String parameter, SortingParameters sorting, PagingParameters paging, 
                                       YukonUserContext userContext, ModelMap model, SmartNotificationEventFilter filter) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        SmartNotificationEventType eventType = SmartNotificationEventType.retrieveByUrlPath(type);
        model.addAttribute("eventType", eventType);
        model.addAttribute("parameter", parameter);
        if (filter.getStartDate() == null) {
            DateTime start = new DateTime().minusDays(1).withTimeAtStartOfDay().withZone(userContext.getJodaTimeZone());
            filter.setStartDate(start.toDate());
        }
        if (filter.getEndDate() == null) {
            filter.setEndDate(new Date());
        }
        EventSortBy sortBy = EventSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (EventSortBy column : EventSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        SearchResults<SmartNotificationEventData> eventData = retrieveEventData(userContext, paging, eventType, parameter, sorting, filter, model);
        model.addAttribute("events", eventData);
        return "eventDetail.jsp";
    }
    
    private SearchResults<SmartNotificationEventData> retrieveEventData(YukonUserContext userContext, PagingParameters paging, SmartNotificationEventType eventType, String parameter,
                                                                        SortingParameters sorting, SmartNotificationEventFilter filter, ModelMap model) {
        SearchResults<SmartNotificationEventData> eventData = new SearchResults<>();
        EventSortBy sortBy = EventSortBy.valueOf(sorting.getSort());
        Range<DateTime> range = new Range<DateTime>(new DateTime(filter.getStartDate()), true, new DateTime(filter.getEndDate()), true);
        SearchResults<SmartNotificationEventData> allDetail = new SearchResults<>();
        if (eventType.equals(SmartNotificationEventType.DEVICE_DATA_MONITOR)) {
            int id = Integer.parseInt(parameter);
            eventData = eventDao.getDeviceDataMonitorEventData(userContext.getJodaTimeZone(), paging, sortBy.value, sorting.getDirection(), range, id);
            allDetail = eventDao.getDeviceDataMonitorEventData(userContext.getJodaTimeZone(), PagingParameters.EVERYTHING, sortBy.value, sorting.getDirection(), range, id);
            ddmHelper.retrieveMonitorById(model, id);
        } else if (eventType.equals(SmartNotificationEventType.INFRASTRUCTURE_WARNING)) {
            InfrastructureWarningDeviceCategory[] categories = InfrastructureWarningDeviceCategory.values();   
            if (filter.getCategories().isEmpty()) {
                filter.setCategories(Arrays.asList(categories));
            }
            model.addAttribute("types", categories);
            List<PaoType> allTypes = new ArrayList<PaoType>();
            List<InfrastructureWarningDeviceCategory> cats = filter.getCategories();
            if (cats.contains(InfrastructureWarningDeviceCategory.GATEWAY)) {
                allTypes.addAll(PaoType.getRfGatewayTypes());
            }
            if (cats.contains(InfrastructureWarningDeviceCategory.RELAY)) {
                allTypes.addAll(PaoType.getRfRelayTypes());
            }
            if (cats.contains(InfrastructureWarningDeviceCategory.CCU)) {
                allTypes.addAll(PaoType.getCcuTypes());
            }
            if (cats.contains(InfrastructureWarningDeviceCategory.REPEATER)) {
                allTypes.addAll(PaoType.getRepeaterTypes());
            }
            eventData = eventDao.getInfrastructureWarningEventData(userContext.getJodaTimeZone(), paging, sortBy.value, sorting.getDirection(), range, allTypes);
            allDetail = eventDao.getInfrastructureWarningEventData(userContext.getJodaTimeZone(), PagingParameters.EVERYTHING, sortBy.value, sorting.getDirection(), range, allTypes);
        }
        List<SimpleDevice> devices = new ArrayList<>();
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        allDetail.getResultList().forEach(item -> devices.add(deviceDao.getYukonDevice(item.getDeviceId())));
        deviceGroupMemberEditorDao.addDevices(tempGroup,  devices);
        
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
        model.addAttribute("deviceCollection", deviceCollection);
        return eventData;
    }
    
    @RequestMapping(value="subscriptions", method=RequestMethod.GET)
    public String subscriptions(@ModelAttribute("filter") SmartNotificationFilter filter, BindingResult bindingResult,
                                @DefaultSort(dir=Direction.asc, sort="type") SortingParameters sorting, 
                                @DefaultItemsPerPage(value=250) PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        List<SmartNotificationSubscription> subscriptions = new ArrayList<>();
        if (filter.getEventType() != null && !filter.getEventType().isEmpty()) {
            subscriptions = subscriptionDao.getSubscriptions(userContext.getYukonUser().getUserID(), SmartNotificationEventType.valueOf(filter.getEventType()));
        } else {
            subscriptions = subscriptionDao.getSubscriptions(userContext.getYukonUser().getUserID());
        }
        
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
        model.addAttribute("eventTypes", SmartNotificationEventType.values());
        model.addAttribute("filter", filter);
        model.addAttribute("sendTime", userPreferenceService.getPreference(userContext.getYukonUser(), UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME));
        Map<String, String> deviceDataMonitors = new HashMap<>();
        monitorCacheService.getDeviceDataMonitors().forEach(monitor -> deviceDataMonitors.put(monitor.getId().toString(), monitor.getName()));
        model.addAttribute("deviceDataMonitors", deviceDataMonitors);
        return "subscriptions.jsp";
    }
    
    @RequestMapping(value="singleNotification", method=RequestMethod.POST)
    public String singleNotificationSettings(@RequestParam(value="singleNotification", required=false) Boolean singleNotification, 
                                             @RequestParam(value="sendTime", required=false) String sendTime,  
                                             YukonUserContext userContext, FlashScope flash) throws Exception {
        if (singleNotification) {
            userPreferenceService.savePreference(userContext.getYukonUser(), UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME, sendTime);
        } else {
            userPreferenceService.savePreference(userContext.getYukonUser(), UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME, "");
        }
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "singleNotificationSuccess"));
        return "redirect:/user/profile";
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
        setupPopupModel(model, userContext);
        model.addAttribute("subscription", subscription);
        return "subscriptionPopup.jsp";
    }
    
    @RequestMapping(value="subscription/{id}/edit", method=RequestMethod.GET)
    public String editSubscription(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash) {
        model.addAttribute("mode", PageEditMode.EDIT);
        SmartNotificationSubscription subscription = subscriptionDao.getSubscription(id);
        if (subscription.getUserId() != userContext.getYukonUser().getUserID()) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "editNotOwner"));
            return "redirect:/user/profile";
        }
        model.addAttribute("subscription", subscription);
        if (subscription.getType().equals(SmartNotificationEventType.DEVICE_DATA_MONITOR)) {
            ddmHelper.retrieveMonitor(model, subscription);
        }
        setupPopupModel(model, userContext);
        return "subscriptionPopup.jsp";
    }
    
    @RequestMapping(value="subscription/{id}/unsubscribe", method=RequestMethod.POST)
    public void removeSubscription(YukonUserContext userContext, @PathVariable int id, HttpServletResponse resp, FlashScope flash) {
        SmartNotificationSubscription subscription = subscriptionDao.getSubscription(id);
        if (subscription.getUserId() != userContext.getYukonUser().getUserID()) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "deleteNotOwner"));
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        subscriptionService.deleteSubscription(id, userContext);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "unsubscribeSuccess", subscription.getType().name()));
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping(value="subscription/create", method=RequestMethod.GET)
    public String createSubscription(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.CREATE);
        SmartNotificationSubscription subscription = new SmartNotificationSubscription();
        setupPopupModel(model, userContext);
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
    
    private void setupPopupModel(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("eventTypes", SmartNotificationEventType.values());
        model.addAttribute("frequencies", SmartNotificationFrequency.values());
        model.addAttribute("mediaTypes", SmartNotificationMedia.values());
        model.addAttribute("detailTypes", SmartNotificationVerbosity.values());
        model.addAttribute("deviceDataMonitors", monitorCacheService.getDeviceDataMonitors());
        model.addAttribute("sendTime", userPreferenceService.getPreference(userContext.getYukonUser(), UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME));
    }
    
    @RequestMapping(value="subscription/saveDetails", method=RequestMethod.POST)
    public String saveDetails(ModelMap model, YukonUserContext userContext, HttpServletResponse resp,
                              @ModelAttribute("subscription") SmartNotificationSubscription subscription, BindingResult result) throws Exception {
        subscription.setUserId(userContext.getYukonUser().getUserID());
        subscriptionValidator.doValidation(subscription, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            setupPopupModel(model, userContext);
            return "subscriptionPopup.jsp";
        }
        subscriptionService.saveSubscription(subscription, userContext);
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        json.put("successMsg", messageSourceAccessor.getMessage(baseKey + "saveSuccessful"));
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        return null;
    }
    
    @RequestMapping("download")
    public String download(@ModelAttribute("filter") SmartNotificationEventFilter filter, YukonUserContext userContext, 
                          @DefaultSort(dir=Direction.asc, sort="timestamp") SortingParameters sorting, ModelMap model,
                          String eventType, String parameter, HttpServletResponse response) throws IOException {
        SmartNotificationEventType type = SmartNotificationEventType.valueOf(eventType);
        SearchResults<SmartNotificationEventData> eventData = retrieveEventData(userContext, PagingParameters.EVERYTHING, type, parameter, sorting, filter, model);

        boolean includeTypeRow = type.equals(SmartNotificationEventType.INFRASTRUCTURE_WARNING);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String deviceNameHeader = accessor.getMessage(EventSortBy.deviceName);
        String typeHeader = accessor.getMessage(EventSortBy.type);
        String statusHeader = accessor.getMessage(EventSortBy.status);
        String timestampHeader = accessor.getMessage(EventSortBy.timestamp);
        String[] headerRow = includeTypeRow ? new String[]{deviceNameHeader, typeHeader, statusHeader, timestampHeader} : new String[]{deviceNameHeader, statusHeader, timestampHeader};

        List<String[]> dataRows = Lists.newArrayList();
        for (SmartNotificationEventData event : eventData.getResultList()) {
            String name = event.getDeviceName();
            String deviceType = event.getType();
            String status = event.getStatus();
            String timestamp = dateFormattingService.format(event.getTimestamp(), DateFormatEnum.BOTH, userContext);
            String[] dataRow = includeTypeRow ? new String[]{name, deviceType, status, timestamp} : new String[]{name, status, timestamp};
            dataRows.add(dataRow);
        }
        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "notificationEvents_" + eventType + "_" + now + ".csv");
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
            return baseKey + name();
        }
    }
    
    public enum EventSortBy implements DisplayableEnum {

        deviceName(SortBy.DEVICE_NAME),
        type(SortBy.TYPE),
        status(SortBy.STATUS),
        timestamp(SortBy.TIMESTAMP);
        
        private EventSortBy(SortBy value) {
            this.value = value;
        }

        private final SortBy value;

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return baseKey + "detail." + name();
        }
    }

}
