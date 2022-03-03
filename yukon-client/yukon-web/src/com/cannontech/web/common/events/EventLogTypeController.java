package com.cannontech.web.common.events;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.events.model.EventLogTypeFilter;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EventLogColumnTypePropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.validator.EventLogTypeValidator;
import com.cannontech.web.util.JsTreeNode;
import com.cannontech.web.util.WebFileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;

@Controller
@RequestMapping("/eventLog/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_EVENT_LOGS)
public class EventLogTypeController {
    
    private final int maxCsvRows = 65535; // The total number of rows possible in excel minus one row for the header row.
    private final String eventLogResolvablePrefix ="yukon.common.events.";
    
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EventLogFilterFactory eventLogFilterFactory;
    @Autowired private EventLogTypeValidator eventLogTypeValidator;
    @Autowired private EventLogDao eventLogDao;
    @Autowired private EventLogService eventLogService;
    @Autowired private EventLogUIService eventLogUIService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DateFormattingService dateFormattingService;

    @RequestMapping(value="viewByType", method=RequestMethod.GET)
    public void selectByType(ModelMap model) {
        
        SearchResults<EventLog> emptyResult = SearchResults.emptyResult();
        model.addAttribute("searchResult", emptyResult);
        model.addAttribute("paging", PagingParameters.of(50, 1));
    }
    
    @RequestMapping(value="viewByType", params="export", method=RequestMethod.GET)
    public void exportByType(@ModelAttribute("filter") EventLogTypeFilter filter,
                           HttpServletResponse response,
                           YukonUserContext userContext) throws IOException {

        UiFilter<EventLog> eventLogSqlFilters = getEventLogUIFilters(filter);
        
        // Get search results
        SearchResults<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByType(filter.getEventLogType(), 
                 filter.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                 filter.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                 0, 
                 Integer.MAX_VALUE,
                 eventLogSqlFilters,
                 userContext);
        
        // Get column names
        List<String> columnNames = Lists.newArrayList();
        final MessageSourceAccessor messageSourceAccessor = 
            messageResolver.getMessageSourceAccessor(userContext);

        List<Integer> argumentIndexes = Lists.newArrayList(); // the EventLog column name number part only; shall be index into searchResults.resultList
        List<EventLogFilter> eventLogFilters = filter.getEventLogFilters();
        columnNames.add(messageSourceAccessor.getMessage("yukon.web.modules.support.eventViewer.byType.event"));
        columnNames.add(messageSourceAccessor.getMessage("yukon.web.modules.support.eventViewer.byType.date"));
        columnNames.add(messageSourceAccessor.getMessage("yukon.web.modules.support.eventViewer.byType.time"));
        for (EventLogFilter eventLogFilter : eventLogFilters) {
            columnNames.add(messageSourceAccessor.getMessage(eventLogFilter.getKey()));
            argumentIndexes.add(eventLogFilter.getArgumentColumn().getColumnIndex());
        }
        
        // Get data grid
        List<List<String>> dataGrid = 
            eventLogUIService.getDataGridRowByType(searchResult, argumentIndexes, userContext);
        
        // Build and write csv report
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, filter.getEventLogType() + ".csv");
    }

    @RequestMapping(value="viewByType", params={"eventLogType", "!export"}, method=RequestMethod.GET)
    public void viewByType(@ModelAttribute("filter") EventLogTypeFilter filter,
                           BindingResult bindingResult,
                           FlashScope flashScope,
                           YukonUserContext userContext, 
                           HttpServletRequest request,
                           ModelMap model,
                           @DefaultItemsPerPage(50) PagingParameters paging) {
        
        model.addAttribute("paging", paging);

        // Validating the search data
        eventLogTypeValidator.doValidation(filter, bindingResult);
        if (bindingResult.hasErrors()) {

            List<MessageSourceResolvable> messages = 
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return;
        } 

        UiFilter<EventLog> eventLogSqlFilters = getEventLogUIFilters(filter);

        // Get default search results
        SearchResults<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByType(filter.getEventLogType(), 
                 filter.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                 filter.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                 paging.getStartIndex(), 
                 paging.getItemsPerPage(),
                 eventLogSqlFilters,
                 userContext);

        buildEventLogResults(userContext, filter, searchResult, model);
        
        String csvLink = ServletUtil.tweakHTMLRequestURI(request, "export", "CSV");
        model.addAttribute("csvLink", csvLink);
    }
    
    @RequestMapping("testAll")
    public void testAll(ModelMap map) {
        Date now = new Date();
        List<ArgumentColumn> argumentColumns = eventLogDao.getArgumentColumns();
        Object[] fakeArguments = new Object[argumentColumns.size()];
        int i = 0;
        for (ArgumentColumn argumentColumn : argumentColumns) {
            fakeArguments[i++] = argumentColumn.getColumnName();
        }
        ListMultimap<EventCategory, String> typeMultiMap = eventLogService.getEventLogTypeMultiMap();
        
        List<MessageSourceResolvable> allKeys = Lists.newArrayList();
        
        for (Entry<EventCategory, String> type : typeMultiMap.entries()) {
            String fullType = EventLog.getFullName(type.getKey(), type.getValue());
            MethodLogDetail detailForEventType = eventLogService.getDetailForEventType(fullType);
            EventLog eventLog = new EventLog();
            eventLog.setDateTime(now);
            eventLog.setEventType(fullType);
            eventLog.setArguments(fakeArguments);
            MessageSourceResolvable eventDescription = eventLog.getMessageSourceResolvable();
            allKeys.add(eventDescription);
            Set<EventParameter> keySet = detailForEventType.getParameterToColumnMapping().keySet();
            for (EventParameter eventParameter : keySet) {
                MessageSourceResolvable columnKey = getColumnKey(fullType, eventParameter);
                allKeys.add(columnKey);
            }
        }
        
        map.addAttribute("allKeys", allKeys);
    }
    
    @ModelAttribute("filter")
    public EventLogTypeFilter initializeTypeFilter(String eventLogType, YukonUserContext userContext) {
        
        if (StringUtils.isBlank(eventLogType)) return null;
        
        List<EventLogFilter> filters = getEventLogFilter(eventLogType, userContext);
        DateTimeZone tz = userContext.getJodaTimeZone();
        EventLogTypeFilter filter = new EventLogTypeFilter(eventLogType, tz, filters);
        
        return filter;
    }
    
    @ModelAttribute("maxCsvRows")
    public int getMaxCsvRows() {
        return maxCsvRows;
    }
    
    @ModelAttribute
    public void setupTreeModelData(String eventLogType, ModelMap map) throws JsonProcessingException {
        buildTreeModelData(eventLogType, map);
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
        
        binder.registerCustomEditor(EventLogColumnTypeEnum.class, new EventLogColumnTypePropertyEditor());
        
        binder.registerCustomEditor(EventCategory.class, new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                EventCategory eventCategory = (EventCategory) getValue();
                return eventCategory.getFullName();
            }
            
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                EventCategory eventCategory = EventCategory.createCategory(text);
                setValue(eventCategory);
            }
        });
    }
    
    /**
     * This method builds up a list of UIFilters from the given backing bean.
     */
    private UiFilter<EventLog> getEventLogUIFilters(EventLogTypeFilter filter) {

        // Build up filter list
        List<UiFilter<EventLog>> filters = Lists.newArrayList();
        for (EventLogFilter eventLogFilter : filter.getEventLogFilters()) {
            FilterValue filterValue = eventLogFilter.getFilterValue();
            
            // Create and add event log filter
            UiFilter<EventLog> uifilter = 
                eventLogFilterFactory.getFilter(filterValue, eventLogFilter.getArgumentColumn());
            if (uifilter != null) {
                filters.add(uifilter);
            }
        }
        UiFilter<EventLog> eventLogSqlFilters = UiFilterList.wrap(filters);
        return eventLogSqlFilters;
    }
    
    /**
     * This method loads up the model the search results, column headers, and also 
     * data grid that is used to display the results.
     */
    private void buildEventLogResults(YukonUserContext userContext,
            EventLogTypeFilter filter,
            SearchResults<EventLog> rawResults,
            ModelMap model) {
        // Get column names
        List<ColumnHeader> columnNames = Lists.newArrayList();
        List<Integer> columnArgumentIndexes = Lists.newArrayList();
        
        String eventLogName = filter.getEventLogType();
        MethodLogDetail eventTypeDetail = eventLogService.getDetailForEventType(eventLogName);
        Set<EventParameter> parameters = eventTypeDetail.getParameterToColumnMapping().keySet();
        // TODO see if parameters could be sorted 
        for (EventParameter eventParameter : parameters) {
            // get name for column
            MessageSourceResolvable columnKey = getColumnKey(eventLogName, eventParameter);
            ArgumentColumn argumentColumn = eventTypeDetail.getParameterToColumnMapping().get(eventParameter);
            
            ColumnHeader columnHeader = new ColumnHeader(columnKey, argumentColumn);
            columnNames.add(columnHeader);
            
            // get column indexes
            int index = eventLogDao.getArgumentColumns().indexOf(argumentColumn);
            columnArgumentIndexes.add(index);
        }
        model.addAttribute("columnNames", columnNames);
        
        SearchResults<ReportableEventLog> result = new SearchResults<ReportableEventLog>();
        result.setBounds(rawResults.getStartIndex(), rawResults.getCount(), rawResults.getHitCount());
        List<ReportableEventLog> resultList = Lists.newArrayListWithCapacity(rawResults.getResultList().size());
        for (EventLog rawLog : rawResults.getResultList()) {
            List<String> cells = Lists.newArrayListWithCapacity(columnArgumentIndexes.size());
            for (Integer index : columnArgumentIndexes) {
                Object rawCell = rawLog.getArguments()[index];
                String cell = formatCell(rawCell, userContext);
                cells.add(cell);
            }
            ReportableEventLog reportableEventLog = new ReportableEventLog(rawLog, cells);
            resultList.add(reportableEventLog);
        }
        result.setResultList(resultList);
        model.addAttribute("searchResult", result);
    }
    
    private String formatCell(Object argument, YukonUserContext userContext) {
        if (argument == null) { // return empty string if argument is null.
            return "";
        } else if (argument instanceof Date) {
            return dateFormattingService.format(argument, DateFormatEnum.BOTH, userContext);
        } else {
            return argument.toString();
        }
    }

    /**
     * Builds up the model map for the tree structure for selecting an event type.
     * @throws JsonProcessingException 
     */
    private void buildTreeModelData(String eventLogType, ModelMap model) throws JsonProcessingException {
        // Build Select Event Tree
        // ALL GROUPS HIERARCHY
        EventCategoryHierarchy everythingHierarchy = getEventLogHierarchy();
        
        HighlightSelectedEventLogNodeAttributeSettingCallback callback = 
            new HighlightSelectedEventLogNodeAttributeSettingCallback(StringUtils.defaultString(eventLogType, ""));
        JsTreeNode allGroupsRoot = 
            new EventLogTreeUtils().makeEventCategoryJsTree(everythingHierarchy, callback);

        // selected node Ext path
        String extSelectedNodePath = callback.getjsTreeSelectedNodePath();
        model.addAttribute("extSelectedNodePath", extSelectedNodePath);
        
        String allGroupsDataJson = JsonUtils.toJson(allGroupsRoot.toMap());
        model.addAttribute("allEventCategoriesDataJson", allGroupsDataJson);
    }
    
    /**
     * This method builds up the event log filters portion of the eventLogTypeBackingBean.
     * This also handles the creation of the i18n keys.
     */
    private List<EventLogFilter> getEventLogFilter(String eventLogType, YukonUserContext userContext) {
        List<EventLogFilter> eventLogFilters = Lists.newArrayList();

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

            MessageSourceResolvable eventLogColumnKey = getColumnKey(eventLogType,
                                                                     eventParameter);
            eventLogFilter.setKey(eventLogColumnKey);

        }
                
        return eventLogFilters;
    }
    
    private EventCategoryHierarchy getEventLogHierarchy() {

        ListMultimap<EventCategory, String> eventLogTypeMultiMap = 
            eventLogService.getEventLogTypeMultiMap();
        
        SetMultimap<EventCategory, EventCategory> parentChildLookup = getCategoriesByParent(eventLogTypeMultiMap.keySet());

        EventCategoryHierarchy eventLogHierarchy = new EventCategoryHierarchy();
        eventLogHierarchy.setEventCategory(EventCategory.createCategory("Categories"));
        eventLogHierarchy.setEventLogTypes(ImmutableList.<String>of());

        fillInChildren(eventLogHierarchy, null, eventLogTypeMultiMap, parentChildLookup);
        
        return eventLogHierarchy;
    }
    
    private SetMultimap<EventCategory, EventCategory> getCategoriesByParent(
            Iterable<EventCategory> categories) {
        SetMultimap<EventCategory, EventCategory> parentChildLookup = HashMultimap.create();
        
        for (EventCategory eventCategory : categories) {
            // this extra for loop is needed because the DAO does not return categories that
            // do not have child types (e.g. currently a category for "system" is not returned by itself)
            for (EventCategory category = eventCategory; category != null; category = category.getParent()) {
                parentChildLookup.put(category.getParent(), category);
            }
        }
        return parentChildLookup;
    }
    
    private void fillInChildren(EventCategoryHierarchy parentHierarchy,
            EventCategory eventCategory, ListMultimap<EventCategory, String> eventLogTypeMultiMap,
            SetMultimap<EventCategory, EventCategory> parentChildLookup) {
        
        Set<EventCategory> unsortedCategories = parentChildLookup.get(eventCategory);
        List<EventCategory> sortedCategories = Ordering.natural().sortedCopy(unsortedCategories);
        
        for (EventCategory childEventCategory : sortedCategories) {
            EventCategoryHierarchy eventLogHierarchy = new EventCategoryHierarchy();
            eventLogHierarchy.setEventCategory(childEventCategory);
            fillInChildren(eventLogHierarchy, childEventCategory, eventLogTypeMultiMap, parentChildLookup);
            
            parentHierarchy.addChildEventCategoryHierarchy(eventLogHierarchy);
        }
        List<String> childTypes = eventLogTypeMultiMap.get(eventCategory);
        parentHierarchy.setEventLogTypes(Ordering.natural().sortedCopy(childTypes));
    }
    
    private MessageSourceResolvable getColumnKey(String eventLogType, EventParameter eventParameter) {
        ArrayList<String> keys = Lists.newArrayListWithCapacity(3);
        String key1 = eventLogResolvablePrefix + eventLogType + "." + eventParameter.getArgumentNumber();
        keys.add(key1);
        if (eventParameter.isNamed()) {
            String key2 = eventLogResolvablePrefix + eventLogType + "." + eventParameter.getAnnotatedName();
            keys.add(key2);
            String key3 = eventLogResolvablePrefix + eventParameter.getAnnotatedName();
            keys.add(key3);
        }
        
        String name = "unknown";
        if (eventParameter.isNamed()) {
            name = eventParameter.getName();
        }
        String defaultText = name + " (#" + eventParameter.getArgumentNumber() + ")";
        
        MessageSourceResolvable result = 
            YukonMessageSourceResolvable.createMultipleCodesWithDefault(keys, defaultText);
        return result;
    }
    
    public static class ColumnHeader {
        private MessageSourceResolvable label;
        private ArgumentColumn argumentColumn;
        private ColumnHeader(MessageSourceResolvable label, ArgumentColumn argumentColumn) {
            this.label = label;
            this.argumentColumn = argumentColumn;
        }
        public MessageSourceResolvable getLabel() {
            return label;
        }
        public ArgumentColumn getArgumentColumn() {
            return argumentColumn;
        }
        
    }
    
    public static class ReportableEventLog {
        private EventLog eventLog;
        private List<String> parameters;
        
        private ReportableEventLog(EventLog eventLog, List<String> parameters) {
            this.eventLog = eventLog;
            this.parameters = parameters;
        }
        public EventLog getEventLog() {
            return eventLog;
        }
        public List<String> getParameters() {
            return parameters;
        }
    }
    
}