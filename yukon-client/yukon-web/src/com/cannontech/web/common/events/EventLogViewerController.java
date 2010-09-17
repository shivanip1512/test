package com.cannontech.web.common.events;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.DateFilterValue;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventCategoryHierarchy;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.model.EventLogColumnTypeEnum;
import com.cannontech.common.events.model.EventLogFilter;
import com.cannontech.common.events.model.EventParameter;
import com.cannontech.common.events.model.FilterValue;
import com.cannontech.common.events.model.NumberFilterValue;
import com.cannontech.common.events.model.StringFilterValue;
import com.cannontech.common.events.service.EventLogFilterFactory;
import com.cannontech.common.events.service.EventLogService;
import com.cannontech.common.events.service.EventLogUIService;
import com.cannontech.common.events.service.impl.MethodLogDetail;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.events.model.EventLogCategoryBackingBean;
import com.cannontech.web.common.events.model.EventLogTypeBackingBean;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EventLogColumnTypePropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.validator.EventLogCategoryValidator;
import com.cannontech.web.stars.dr.operator.validator.EventLogTypeValidator;
import com.cannontech.web.util.ExtTreeNode;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/eventLog/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_EVENT_LOGS)
@SessionAttributes(value="eventLogTypeBackingBean")
public class EventLogViewerController {

    private final String eventLogResolvablePrefix ="yukon.common.events.";

    private DatePropertyEditorFactory datePropertyEditorFactory;
    private EventLogCategoryValidator eventLogCategoryValidator;
    private EventLogFilterFactory eventLogFilterFactory;
    private EventLogTypeValidator eventLogTypeValidator;
    private EventLogDao eventLogDao;
    private EventLogService eventLogService;
    private EventLogUIService eventLogUIService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping
    public void viewByCategory(@ModelAttribute("eventLogCategoryBackingBean") EventLogCategoryBackingBean eventLogCategoryBackingBean, 
                               BindingResult bindingResult,
                               FlashScope flashScope,
                               YukonUserContext userContext, 
                               ModelMap model) throws ServletException, ParseException {
        model.addAttribute("eventLogTypeBackingBean", new EventLogTypeBackingBean());
        
        if (eventLogCategoryBackingBean.getStartDate() == null) {
            eventLogCategoryBackingBean.setStartDate(new LocalDate(userContext.getJodaTimeZone()).minusDays(1));
        }
        
        if (eventLogCategoryBackingBean.getStopDate() == null) {
            eventLogCategoryBackingBean.setStopDate(new LocalDate(userContext.getJodaTimeZone()));
        }

        // Get event category map
        List<EventCategory> eventCategories = Lists.newArrayList();
        if (eventLogCategoryBackingBean.getCategories() == null) {
            eventCategories = getAllEventCategories();
        } else {
            for (String categoryStr : eventLogCategoryBackingBean.getCategories()) {
                EventCategory category = EventCategory.createCategory(categoryStr);
                eventCategories.add(category);
            }
        }

        Map<EventCategory, Boolean> selectedMap = ServletUtil.convertListToMap(eventCategories); 
        model.addAttribute("selectedCategories", selectedMap);

        // Validating the search data
        eventLogCategoryValidator.doValidation(eventLogCategoryBackingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = 
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return;
        } 
        
        // Getting the search results
        SearchResult<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByCategories(eventCategories, 
                                                                       eventLogCategoryBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                       eventLogCategoryBackingBean.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                       eventLogCategoryBackingBean.getStartIndex(), 
                                                                       eventLogCategoryBackingBean.getItemsPerPage(),
                                                                       eventLogCategoryBackingBean.getFilterValue(),
                                                                       userContext);
        
        model.addAttribute("searchResult", searchResult);
        model.addAttribute("events", searchResult.getResultList());
    }

    @RequestMapping
    public void viewByType(ModelMap model,
                           SessionStatus sessionStatus) {
        // Clear Session Attribute
        sessionStatus.setComplete();

        buildTreeModelData(model);
        
        SearchResult<EventLog> emptyResult = SearchResult.emptyResult();
        
        model.addAttribute("searchResult", emptyResult);
        model.addAttribute("events", emptyResult.getResultList());
        
    }
    
    @RequestMapping(value="viewByType", params="eventLogType", method=RequestMethod.GET)
    public void viewByType(HttpServletRequest request,
                           HttpSession session,
                           YukonUserContext userContext, 
                           ModelMap model) throws ServletRequestBindingException{
        buildTreeModelData(model);

        // Get event log type
        String eventLogType = ServletRequestUtils.getRequiredStringParameter(request, "eventLogType");
        eventLogType = StringUtils.removeStart(eventLogType, "Categories.");

        // Create event log type backing bean
        EventLogTypeBackingBean eventLogTypeBackingBean = (EventLogTypeBackingBean)session.getAttribute("eventLogTypeBackingBean");
        if (eventLogTypeBackingBean == null ||
            !eventLogType.equalsIgnoreCase(eventLogTypeBackingBean.getEventLogType())) {
            List<EventLogFilter> eventLogFilters = getEveltLogFilter(eventLogType, userContext);
            eventLogTypeBackingBean = 
                new EventLogTypeBackingBean(eventLogType,
                                            userContext,
                                            eventLogFilters);
        }
        eventLogTypeBackingBean.setItemsPerPage(ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10));
        eventLogTypeBackingBean.setPage(ServletRequestUtils.getIntParameter(request, "page", 1));
        model.addAttribute("eventLogTypeBackingBean", eventLogTypeBackingBean);

        UiFilter<EventLog> eventLogSqlFilters = getEventLogUIFilters(eventLogTypeBackingBean);
        
        // Get default search results
        SearchResult<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByType(eventLogTypeBackingBean.getEventLogType(), 
                                                                 eventLogTypeBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                 eventLogTypeBackingBean.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                 eventLogTypeBackingBean.getStartIndex(), 
                                                                 eventLogTypeBackingBean.getItemsPerPage(),
                                                                 eventLogSqlFilters,
                                                                 userContext);

        buildEventLogResults(userContext, model, eventLogTypeBackingBean, searchResult);
        
        model.addAttribute("searchResult", searchResult);
        model.addAttribute("events", searchResult.getResultList());
        
    }

    @RequestMapping(value="viewByType", params="eventLogType",method=RequestMethod.POST)
    public void viewByType(@ModelAttribute("eventLogTypeBackingBean") EventLogTypeBackingBean eventLogTypeBackingBean,
                           BindingResult bindingResult,
                           FlashScope flashScope,
                           HttpServletRequest request,
                           YukonUserContext userContext, 
                           ModelMap model) throws ServletException, ParseException {

        buildTreeModelData(model);

        // Validating the search data
        eventLogTypeValidator.doValidation(eventLogTypeBackingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = 
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return;
        } 

        UiFilter<EventLog> eventLogSqlFilters = getEventLogUIFilters(eventLogTypeBackingBean);
        
        // Get search results
        SearchResult<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByType(eventLogTypeBackingBean.getEventLogType(), 
                                                                 eventLogTypeBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                 eventLogTypeBackingBean.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                 eventLogTypeBackingBean.getStartIndex(), 
                                                                 eventLogTypeBackingBean.getItemsPerPage(),
                                                                 eventLogSqlFilters,
                                                                 userContext);
        
        
        buildEventLogResults(userContext, model, eventLogTypeBackingBean, searchResult);
        
        model.addAttribute("searchResult", searchResult);
        model.addAttribute("events", searchResult.getResultList());
        
    }

    @RequestMapping(params="export")
    public void viewByType(@ModelAttribute("eventLogTypeBackingBean") EventLogTypeBackingBean eventLogTypeBackingBean,
                           HttpServletResponse response,
                           YukonUserContext userContext, 
                           ModelMap model) throws ServletException, ParseException, IOException {

        UiFilter<EventLog> eventLogSqlFilters = getEventLogUIFilters(eventLogTypeBackingBean);
        
        // Get search results
        SearchResult<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByType(eventLogTypeBackingBean.getEventLogType(), 
                                                                 eventLogTypeBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                 eventLogTypeBackingBean.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                                                                 0, 
                                                                 Integer.MAX_VALUE,
                                                                 eventLogSqlFilters,
                                                                 userContext);
        
        // Get column names
        List<String> columnNames = Lists.newArrayList();
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);

        List<EventLogFilter> eventLogFilters = eventLogTypeBackingBean.getEventLogFilters();
        columnNames.add(messageSourceAccessor.getMessage("yukon.web.modules.support.byType.event"));
        columnNames.add(messageSourceAccessor.getMessage("yukon.web.modules.support.byType.dateAndTime"));
        for (EventLogFilter eventLogFilter : eventLogFilters) {
            columnNames.add(messageSourceAccessor.getMessage(eventLogFilter.getKey()));
        }
        
        // Get data grid
        List<List<String>> dataGrid = 
            eventLogUIService.getDataGridRow(searchResult, userContext);
        
        // Build and write csv report
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                           "filename=\"" + ServletUtil.makeWindowsSafeFileName(eventLogTypeBackingBean.getEventLogType()) + ".csv\"");
        OutputStream outputStream = response.getOutputStream();
        generateCsvReport(columnNames, dataGrid, outputStream, userContext);
        
    }

    /**
     * This method builds up a list of UIFilters from the given backing bean.
     *
     */
    private UiFilter<EventLog> getEventLogUIFilters(EventLogTypeBackingBean eventLogTypeBackingBean) {

        // Build up filter list
        List<UiFilter<EventLog>> filters = Lists.newArrayList();
        for (EventLogFilter eventLogFilter : eventLogTypeBackingBean.getEventLogFilters()) {
            FilterValue filterValue = eventLogFilter.getFilterValue();
            
            // Create and add event log filter
            UiFilter<EventLog> filter = 
                eventLogFilterFactory.getFilter(filterValue, eventLogFilter.getArgumentColumn());
            if (filter != null) {
                filters.add(filter);
            }
        }
        UiFilter<EventLog> eventLogSqlFilters = UiFilterList.wrap(filters);
        return eventLogSqlFilters;
    }

    /**
     * This method loads up the model the search results, column headers, and also 
     * data grid that is used to display the results.
     *
     */
    private void buildEventLogResults(YukonUserContext userContext,
                                      ModelMap model,
                                      EventLogTypeBackingBean eventLogTypeBackingBean,
                                      SearchResult<EventLog> searchResult) {
        // Get column names
        List<String> columnNames = Lists.newArrayList();
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        
        List<EventLogFilter> eventLogFilters = eventLogTypeBackingBean.getEventLogFilters();
        columnNames.add(messageSourceAccessor.getMessage("yukon.web.modules.support.byType.event"));
        columnNames.add(messageSourceAccessor.getMessage("yukon.web.modules.support.byType.dateAndTime"));
        for (EventLogFilter eventLogFilter : eventLogFilters) {
            columnNames.add(messageSourceAccessor.getMessage(eventLogFilter.getKey()));
        }
        model.addAttribute("columnNames", columnNames);
        
        // Get data grid
        List<List<String>> dataGrid = 
            eventLogUIService.getDataGridRow(searchResult, userContext);
        model.addAttribute("dataGrid", dataGrid);
    }
    
    
    private void generateCsvReport(List<String> columnNames,
                                    List<List<String>> dataGrid,
                                    OutputStream outputStream,
                                    YukonUserContext userContext) throws IOException {

        // csv writer setup
        //-----------------------------------------------------------------------------------------
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
         
        
        // write to csv
        //-----------------------------------------------------------------------------------------
        csvWriter.writeNext((String[])columnNames.toArray(new String[columnNames.size()]));
        for (List<String> dataRow : dataGrid) {
            csvWriter.writeNext((String[])dataRow.toArray(new String[dataRow.size()]));
        }
        
        csvWriter.close();
            
    }
    
    /**
     * Builds up the model map for the tree structure for selecting an event type.
     */
    private void buildTreeModelData(ModelMap model) {
        // Build Select Event Tree
        // ALL GROUPS HIERARCHY
        EventCategoryHierarchy everythingHierarchy = getEventLogHierarchy();
        
        // ALL GROUPS TREE JSON
        HighlightSelectedEventLogNodeAttributeSettingCallback callback = 
            new HighlightSelectedEventLogNodeAttributeSettingCallback(null);
        ExtTreeNode allGroupsRoot = 
            EventLogTreeUtils.makeEventCategoryExtTree(everythingHierarchy, "Categories", callback);

        // selected node Ext path
        String extSelectedNodePath = callback.getExtSelectedNodePath();
        model.addAttribute("extSelectedNodePath", extSelectedNodePath);
        
        JSONObject allGroupsJsonObj = new JSONObject(allGroupsRoot.toMap());
        String allGroupsDataJson = allGroupsJsonObj.toString();
        model.addAttribute("allEventCategoriesDataJson", allGroupsDataJson);
    }
    
    /**
     * This method builds up the event log filters portion of the eventLogTypeBackingBean.
     * This also handles the creation of the i18n keys.
     */
    private List<EventLogFilter> getEveltLogFilter(String eventLogType, YukonUserContext userContext) {
        List<EventLogFilter> eventLogFilters = Lists.newArrayList();
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        String eventLogResolvablePath = eventLogResolvablePrefix+eventLogType;

        MethodLogDetail detailForEventType = eventLogService.getDetailForEventType(eventLogType);
        Map<ArgumentColumn, EventParameter> columnToParameterMapping = 
            detailForEventType.getColumnToParameterMapping();

        for (Entry<ArgumentColumn, EventParameter> entry : columnToParameterMapping.entrySet()) {
            ArgumentColumn argumentColumn = entry.getKey();
            EventParameter eventParameter = entry.getValue();
            
            EventLogFilter eventLogFilter = new EventLogFilter();
            eventLogFilters.add(eventLogFilter);
            eventLogFilter.setArgumentColumn(argumentColumn);
            
            // Creates the specific filter value for the FilterValueType
            if (argumentColumn.getSqlType() == EventLogColumnTypeEnum.STRING.getSqlType()) {
                eventLogFilter.setEventLogColumnType(EventLogColumnTypeEnum.STRING);
                eventLogFilter.setFilterValue(new StringFilterValue());
            } else if (argumentColumn.getSqlType() == EventLogColumnTypeEnum.NUMBER.getSqlType()) {
                eventLogFilter.setEventLogColumnType(EventLogColumnTypeEnum.NUMBER);
                eventLogFilter.setFilterValue(new NumberFilterValue());
            } else if (argumentColumn.getSqlType() == EventLogColumnTypeEnum.DATE.getSqlType()) {
                eventLogFilter.setEventLogColumnType(EventLogColumnTypeEnum.DATE);
                eventLogFilter.setFilterValue(new DateFilterValue());
            }

            // Check to see if a numeric key exists to override a named key
            String eventLogColumnKey = eventLogResolvablePath+"."+eventParameter.getName();
            try {
                messageSourceAccessor.getMessage(eventLogColumnKey);
                eventLogFilter.setKey(eventLogColumnKey);
                continue;
                // This is fine.  It just means the message we tried didn't exist.
            } catch (NoSuchMessageException e) {
                if (!eventParameter.isNamed()) {
                    throw new IllegalArgumentException("The key "+eventLogColumnKey+" does not exist.");
                }
            }

            // A numeric key was not found.  Try using the class key
            eventLogColumnKey = eventLogResolvablePrefix+eventParameter.getName();
            try {
                messageSourceAccessor.getMessage(eventLogColumnKey);
                eventLogFilter.setKey(eventLogColumnKey);
            } catch (NoSuchMessageException e) {
                throw new IllegalArgumentException("The key "+eventLogColumnKey+" does not exist.");
            }

        }
                
        return eventLogFilters;
    }

    private EventCategoryHierarchy getEventLogHierarchy() {

        ListMultimap<EventCategory, String> eventLogTypeMultiMap = 
            eventLogService.getEventLogTypeMultiMap();

        EventCategoryHierarchy eventLogHierarchy = new EventCategoryHierarchy();
        eventLogHierarchy.setEventCategory(EventCategory.createCategory("Categories"));
        List<String> emptyList = Lists.newArrayList();
        eventLogHierarchy.setEventLogTypes(emptyList);

        for (EventCategory eventCategory : eventLogTypeMultiMap.keySet()) {
            List<String> eventLogTypes = eventLogTypeMultiMap.get(eventCategory);
            eventLogHierarchyHelper(eventLogHierarchy, eventCategory, eventLogTypes);
        }
        
        return eventLogHierarchy;
    }
    
    private void eventLogHierarchyHelper(EventCategoryHierarchy eventCategoryHierarchy,
                                            EventCategory eventCategory,
                                            List<String> eventLogTypes) {

        String echFullPath = eventCategoryHierarchy.getEventCategory().getFullName();
        echFullPath = StringUtils.removeStart(echFullPath, "Categories");
        echFullPath = StringUtils.removeStart(echFullPath, ".");
            
        if (eventCategory.getFullName().equalsIgnoreCase(echFullPath )) {
            eventCategoryHierarchy.setEventLogTypes(eventLogTypes);
            return;
        }
            
        for (EventCategoryHierarchy childEventCategoryHierarchy : eventCategoryHierarchy.getChildEventCategoryHierarchyList()) {
            String echChildFullPath = childEventCategoryHierarchy.getEventCategory().getFullName();
            echChildFullPath = StringUtils.removeStart(echChildFullPath, "Categories");
            echChildFullPath = StringUtils.removeStart(echChildFullPath, ".");
            
            if (eventCategory.getFullName().startsWith(echChildFullPath)) {
                eventLogHierarchyHelper(childEventCategoryHierarchy, eventCategory, eventLogTypes);
                return;
            }
        }
        
        // Build out eventCategoryHierarchy structure
        buildEventCategoryHierarchy(eventCategoryHierarchy, eventCategory, eventLogTypes);
        return;
        
    }

    private void buildEventCategoryHierarchy(EventCategoryHierarchy eventCategoryHierarchy,
                                             EventCategory eventCategory, List<String> eventLogTypes) {
        String echFullPath = eventCategoryHierarchy.getEventCategory().getFullName();
        echFullPath = StringUtils.removeStart(echFullPath, "Categories");
        echFullPath = StringUtils.removeStart(echFullPath, ".");
        
        String remainingEventCategoryPath = eventCategory.getFullName().replace(echFullPath, "");
        String[] remainingEventCategories = StringUtils.split(remainingEventCategoryPath, ".");

        if (remainingEventCategories.length > 0) {
            EventCategoryHierarchy echChild = new EventCategoryHierarchy();
            
            EventCategory childEventCategory = 
                EventCategory.createCategory(eventCategoryHierarchy.getEventCategory().getFullName()+"."+remainingEventCategories[0]);
            echChild.setEventCategory(childEventCategory);
            eventCategoryHierarchy.addChildEventCategoryHierarchy(echChild);
            
            buildEventCategoryHierarchy(echChild, eventCategory, eventLogTypes);
        } else {
            eventCategoryHierarchy.setEventLogTypes(eventLogTypes);
        }
        
    }

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
                                    datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext));
        
        binder.registerCustomEditor(EventLogColumnTypeEnum.class,
                                    new EventLogColumnTypePropertyEditor());
    }
    
    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setEventLogCategoryValidator(EventLogCategoryValidator eventLogCategoryValidator) {
        this.eventLogCategoryValidator = eventLogCategoryValidator;
    }
    
    @Autowired
    public void setEventLogFilterFactory(EventLogFilterFactory eventLogFilterFactory) {
        this.eventLogFilterFactory = eventLogFilterFactory;
    }
    
    @Autowired
    public void setEventLogTypeValidator(EventLogTypeValidator eventLogTypeValidator) {
        this.eventLogTypeValidator = eventLogTypeValidator;
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
    public void setEventLogUIService(EventLogUIService eventLogUIService) {
        this.eventLogUIService = eventLogUIService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}
