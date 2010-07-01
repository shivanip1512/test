package com.cannontech.web.common.events;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.service.EventLogService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.events.model.ByCategoryBackingBean;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/eventLog/*")
@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
public class EventLogViewerController {

    private EventLogDao eventLogDao;
    private EventLogService eventLogService;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private DateFormattingService dateFormattingService;

    @RequestMapping
    public void viewByCategory(@ModelAttribute("byCategoryBackingBean") ByCategoryBackingBean byCategoryBackingBean, 
                               YukonUserContext userContext, 
                               ModelMap model) throws ServletException, ParseException {

        if (byCategoryBackingBean.getStartDate() == null) {
            byCategoryBackingBean.setStartDate(new LocalDate(userContext.getJodaTimeZone()).minusDays(1));
        }
        
        if (byCategoryBackingBean.getStopDate() == null) {
            byCategoryBackingBean.setStopDate(new LocalDate(userContext.getJodaTimeZone()));
        }

        
// Validation         
//        
//        if (stopDate.before(startDate)) {
//            throw new RuntimeException("start must be before stop");
//        }
//        

        List<EventCategory> eventCategories = Lists.newArrayList();
        if (byCategoryBackingBean.getCategories() == null) {
            eventCategories = getAllEventCategories();
        } else {
            for (String categoryStr : byCategoryBackingBean.getCategories()) {
                EventCategory category = EventCategory.createCategory(categoryStr);
                eventCategories.add(category);
            }
        }
        
        SearchResult<EventLog> searchResult = 
            eventLogService.getFilteredPagedSearchResultByCategories(eventCategories, 
                                                                     byCategoryBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                     byCategoryBackingBean.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                     byCategoryBackingBean.getStartIndex(), 
                                                                     byCategoryBackingBean.getItemsPerPage(),
                                                                     byCategoryBackingBean.getFilterValue(),
                                                                     userContext);
        
        Map<EventCategory, Boolean> selectedMap = ServletUtil.convertListToMap(eventCategories); 
        
        model.addAttribute("selectedCategories", selectedMap);
        model.addAttribute("searchResult", searchResult);
        model.addAttribute("events", searchResult.getResultList());
    }

//    @RequestMapping
//    public void viewByTypeSelection(String categoryName, YukonUserContext userContext, ModelMap model) {
//
//        model.addAttribute("categoryName", categoryName);
//        		
//        EventCategory eventCategory = null;
//        
//        if (!StringUtils.isEmpty(categoryName)) {
//            
//            try {
//                eventCategory = eventLogDao.resolveGroupName(categoryName);
//            } catch (NotFoundException e) {
//                model.addAttribute("errorMessage", e.getMessage());
//                manageEventLogs(null, userContext, model);
//                return;
//            }
//            
//        } else {
//            eventCategory = rootGroup;
//        }
//        mav.addObject("eventCategory", eventCategory);
//        
        
        
        
        
        
        // NodeAttributeSettingCallback to highlight node for selected group
        // This one has been given an additional responsibility of recording the node path of the selected node,
        // this path will be used to expand the tree to the selected node and ensure it is visible.
//        EventLog selectedEventLog = EventLog.createCategory("Categories");
//        if (!StringUtils.isBlank(categoryName)) {
//            selectedEventCategory = EventCategory.createCategory(categoryName);
//        }
        
//        // ALL GROUPS HIERARCHY
//        EventLogHierarchy everythingHierarchy = getEventLogHierarchy();
//        
//        // ALL GROUPS TREE JSON
//        HighlightSelectedEventLogNodeAttributeSettingCallback callback = 
//            new HighlightSelectedEventLogNodeAttributeSettingCallback(null);
//        ExtTreeNode allGroupsRoot = 
//            EventLogTreeUtils.makeEventCategoryExtTree(everythingHierarchy, "Categories", callback);
//
//        // selected node Ext path
//        String extSelectedNodePath = callback.getExtSelectedNodePath();
//        model.addAttribute("extSelectedNodePath", extSelectedNodePath);
//        
//        JSONObject allGroupsJsonObj = new JSONObject(allGroupsRoot.toMap());
//        String allGroupsDataJson = allGroupsJsonObj.toString();
//        model.addAttribute("allEventCategoriesDataJson", allGroupsDataJson);
//    }

//    @RequestMapping
//    public void viewByType(String categoryName, String fromDate, String toDate, Integer itemsPerPage, Integer page, YukonUserContext userContext, ModelMap model) throws ParseException {
//
//        Instant startDate = new Instant(dateFormattingService.flexibleDateParser(fromDate, DateOnlyMode.START_OF_DAY, userContext));
//        if (startDate == null) {
//            startDate = new DateTime(userContext.getJodaTimeZone()).minusDays(1).toInstant();
//        }
//        
//        Instant stopDate = new Instant(dateFormattingService.flexibleDateParser(toDate, DateOnlyMode.END_OF_DAY, userContext));
//        if (stopDate == null) {
//            stopDate = new Instant();
//        }
//        
//        if (stopDate.isBefore(startDate)) {
//            throw new RuntimeException("start must be before stop");
//        }
//        
//        if(page == null){
//            page = 1;
//        }
//        if(itemsPerPage == null){
//            itemsPerPage = 10;
//        }
//
//        int startIndex = (page - 1) * itemsPerPage;
//        
//        // Getting Event Categories
//        EventLogHierarchy eventLogHierarchy = getEventLogHierarchy();
//        List<EventLog> eventLogs = findEventCategoryTree(eventLogHierarchy, categoryName);
//        model.addAttribute("eventCategories", eventLogs);
//
//        List<String> eventLogTypes = getEventLogTypes(eventLogs);
//        
//        // Get the Event Logs for the given event categories. 
//        SearchResult<EventLog> searchResults =
//            eventLogDao.getPagedSearchResultByLogTypes(eventLogTypes, startDate, stopDate, startIndex, itemsPerPage);
//        
//        model.addAttribute("itemsPerPage", itemsPerPage);
//        model.addAttribute("searchResults", searchResults);
//        model.addAttribute("events", searchResults.getResultList());
//    }

//    private List<String> getEventLogTypes(List<EventLog> eventLogs) {
//        List<String> eventLogTypes = Lists.newArrayList();
//        
//        for (EventLog eventLog : eventLogs) {
//            eventLogTypes.add(eventLog.getEventCategory().getFullName()+eventLog.getEventType());
//        }
//        
//        return eventLogTypes;
//    }

//    @RequestMapping
//    public void manage(YukonUserContext userContext, ModelMap model) {
////        eventLogDao.getEventLogTypes();
//        getEventLogHierarchy();
//    }
    
//    private EventLog getEventLogTypes(){
//        
//    }
//    
    
//    private EventLogHierarchy getEventLogHierarchy() {
//        
//        List<EventCategory> allEventCategories = getAllEventCategories();
//        List<EventLog> allEventLogs = eventLogDao.findAllByCategories(allEventCategories,
//                                                                      new Instant(0),
//                                                                      new Instant());
//        
//        EventLogHierarchy eventLogHierarchy = new EventLogHierarchy();
//        EventLog baseEventLog = new EventLog();
//        for (EventLog eventLog : allEventLogs) {
//            helper(eventLogHierarchy, eventLog);
//        }
//        return eventLogHierarchy;
//    }
    
//    private void helper(EventLogHierarchy eventLogHierarchy,
//                        EventLog eventLog) {
//        
//        // The event category hierarchy is the direct parent of the eventCategory.  
//        // Add the event category to the list.
//        if (eventLog.getEventCategory().getParent() == null || 
//            eventLogHierarchy.getEventLog().getEventCategory().equals(eventLog.getEventCategory().getParent())) {
//            EventLogHierarchy newEventLogHierarchy = new EventLogHierarchy();
//            newEventLogHierarchy.setEventLog(eventLog);
//            eventLogHierarchy.getChildEventLogList().add(newEventLogHierarchy);
//            return;
//        }
//        
//        // Check to see if the eventCategory is a distant child of the eventLogHierarchy
//        String echFullName = eventLogHierarchy.getEventLog().getEventCategory().getFullName()+
//                             eventLogHierarchy.getEventLog().getEventType();
//        if ((eventLogHierarchy.getEventLog().getEventCategory().getFullName().equalsIgnoreCase("Categories") ||
//             eventLog.getEventCategory().getFullName().startsWith(echFullName)) &&
//             eventLogHierarchy.isChildEventLogsPresent()) {
//            for (EventLogHierarchy elhChild : eventLogHierarchy.getChildEventLogList()) {
//                helper(elhChild, eventLog);
//            }
//        }
//    }
//    
//    private List<EventLog> findEventCategoryTree(EventLogHierarchy elh, String categoryName) {
//        List<EventLog> results = Lists.newArrayList();
//        
//        if (elh.getEventLog().getEventCategory().getFullName().equalsIgnoreCase(categoryName)){
//            return getEventLogTree(elh);
//        }
//        
//        if (elh.getEventLog().getEventCategory().getFullName().equalsIgnoreCase("Categories") ||
//            categoryName.startsWith(elh.getEventLog().getEventCategory().getFullName())) {
//            for (EventLogHierarchy eventLogHierarchy : elh.getChildEventLogList()) {
//                results.addAll(findEventCategoryTree(eventLogHierarchy, categoryName));
//            }
//        }
//        
//        return results;
//    }
    
//    private List<EventLog> getEventLogTree(EventLogHierarchy elh) {
//        List<EventLog> results = Lists.newArrayList();
//        results.add(elh.getEventLog());
//
//        for (EventLogHierarchy eventLogHierarchy : elh.getChildEventLogList()) {
//            results.addAll(getEventLogTree(eventLogHierarchy));
//        }
//        
//        return results;
//    }

    @ModelAttribute
    public List<EventCategory> getAllEventCategories() {
        Set<EventCategory> allCategories = eventLogDao.getAllCategories();
        List<EventCategory> result = Ordering.natural().sortedCopy(allCategories);
        return result;
    }
    
    /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        binder.registerCustomEditor(LocalDate.class, 
                                    "startDate", 
                                    datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext));
        
        binder.registerCustomEditor(LocalDate.class, 
                                    "stopDate", 
                                    datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext));
    }
    
    @Autowired
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }
    
    @Autowired
    public void setEventLogService(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }
    
    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}
