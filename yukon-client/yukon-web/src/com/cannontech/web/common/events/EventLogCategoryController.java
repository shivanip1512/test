package com.cannontech.web.common.events;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.model.EventLogColumnTypeEnum;
import com.cannontech.common.events.service.EventLogUIService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.events.model.EventLogCategoryFilter;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EventLogColumnTypePropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.validator.EventLogCategoryValidator;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/eventLog/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_EVENT_LOGS)
public class EventLogCategoryController {

    private final int maxCsvRows = 65535; // The total number of rows possible in excel minus one row for the header row.

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EventLogCategoryValidator eventLogCategoryValidator;
    @Autowired private EventLogDao eventLogDao;
    @Autowired private EventLogUIService eventLogUIService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @GetMapping("viewByCategory")
    public String viewByCategory(@ModelAttribute("filter") EventLogCategoryFilter filter, 
                               BindingResult bindingResult, FlashScope flashScope,
                               YukonUserContext userContext, ModelMap model, HttpServletResponse response,
                               @DefaultItemsPerPage(50) PagingParameters paging) {
        
        return filterEventLog(filter,
                              bindingResult,
                              flashScope,
                              userContext,
                              response,
                              model,
                              paging,
                              "eventLog/viewByCategory.jsp");
        
    }
    
    private String filterEventLog(EventLogCategoryFilter filter, BindingResult bindingResult,
                                FlashScope flashScope, YukonUserContext userContext, HttpServletResponse response,
                                ModelMap model, PagingParameters paging, String successView) {        
        // Validating the search data
        eventLogCategoryValidator.doValidation(filter, bindingResult);
        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = 
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "eventLog/viewByCategory.jsp";
        } 
        
        List<EventCategory> eventCategories = Lists.newArrayList();
        eventCategories.addAll(Arrays.asList(filter.getCategories()));
        
        // Getting the search results
        SearchResults<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByCategories(eventCategories, 
                   filter.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   filter.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   paging,
                   filter.getFilterValue(),
                   userContext);
        
        model.addAttribute("searchResult", searchResult);
        
        return successView;
    }
    
    @GetMapping("filterByCategory")
    public String filterEventsByCategory(@ModelAttribute("filter") EventLogCategoryFilter filter, 
                               BindingResult bindingResult, FlashScope flashScope,
                               YukonUserContext userContext, ModelMap model, HttpServletResponse response,
                               @DefaultItemsPerPage(50) PagingParameters paging) {
        return filterEventLog(filter,
                              bindingResult,
                              flashScope,
                              userContext,
                              response,
                              model,
                              paging,
                              "eventLog/filteredResults.jsp");
                
    }
    
    @PostMapping("filterByCategory")
    public String filterByCategory(@ModelAttribute("filter") EventLogCategoryFilter filter, 
                               BindingResult bindingResult, FlashScope flashScope,
                               YukonUserContext userContext, ModelMap model, HttpServletResponse response,
                               @DefaultItemsPerPage(50) PagingParameters paging) {
        return filterEventLog(filter,
                              bindingResult,
                              flashScope,
                              userContext,
                              response,
                              model,
                              paging,
                              "eventLog/filteredResults.jsp");
                
    }

    @PostMapping("downloadByCategory")
    public void downloadByCategory(@ModelAttribute("filter") EventLogCategoryFilter filter,
                           HttpServletResponse response,
                           YukonUserContext userContext) throws IOException {

        List<EventCategory> eventCategories = Lists.newArrayList();
        eventCategories.addAll(Arrays.asList(filter.getCategories()));
 
        // Getting the search results
        SearchResults<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByCategories(eventCategories, 
                   filter.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   filter.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   PagingParameters.EVERYTHING,
                   filter.getFilterValue(),
                   userContext);
        
        // Get column names
        List<String> columnNames = Lists.newArrayList();
        final MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        columnNames.add(accessor.getMessage("yukon.common.events.columnHeader.event"));
        columnNames.add(accessor.getMessage("yukon.common.events.columnHeader.date"));
        columnNames.add(accessor.getMessage("yukon.common.events.columnHeader.time"));
        columnNames.add(accessor.getMessage("yukon.common.events.columnHeader.message"));
        
        // Get data grid
        List<List<String>> dataGrid = 
            eventLogUIService.getDataGridRowByCategory(searchResult, userContext);
        
        // Build and write csv report
        String categoryCsvFileName = accessor.getMessage("yukon.web.modules.support.eventViewer.byCategory.csvExport.fileName");
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, categoryCsvFileName + "_" + now + ".csv");
    }
    
    @ModelAttribute("filter")
    public EventLogCategoryFilter initializeCategoryFilter(YukonUserContext userContext) {
        
        EventLogCategoryFilter filter = new EventLogCategoryFilter();
        filter.setStartDate(new LocalDate(userContext.getJodaTimeZone()).minusDays(1));
        filter.setStopDate(new LocalDate(userContext.getJodaTimeZone()));
        
        filter.setCategories(getAllEventCategories().toArray(new EventCategory[]{}));
        
        return filter;
    }

    @ModelAttribute("maxCsvRows")
    public int getMaxCsvRows() {
        return maxCsvRows;
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
    
}