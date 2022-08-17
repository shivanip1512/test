package com.cannontech.web.common.events;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
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
import com.cannontech.util.ServletUtil;
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

    @RequestMapping(value="viewByCategory", params="!export", method = RequestMethod.GET)
    public void viewByCategory(@ModelAttribute("filter") EventLogCategoryFilter filter, 
                               BindingResult bindingResult,
                               FlashScope flashScope,
                               YukonUserContext userContext,
                               HttpServletRequest request,
                               ModelMap model,
                               @DefaultItemsPerPage(50) PagingParameters paging) {
        
        model.addAttribute("paging", paging);
        
        // Validating the search data
        eventLogCategoryValidator.doValidation(filter, bindingResult);
        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = 
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return;
        } 
        
        List<EventCategory> eventCategories = Lists.newArrayList();
        eventCategories.addAll(Arrays.asList(filter.getCategories()));
        
        // Getting the search results
        SearchResults<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByCategories(eventCategories, 
                   filter.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   filter.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   paging.getStartIndex(), 
                   paging.getItemsPerPage(),
                   filter.getFilterValue(),
                   userContext);
        
        model.addAttribute("searchResult", searchResult);
        
        String csvLink = ServletUtil.tweakHTMLRequestURI(request, "export", "CSV");
        model.addAttribute("csvLink", csvLink);
        
    }

    @RequestMapping(value="viewByCategory", params="export", method=RequestMethod.GET)
    public void exportByCategory(@ModelAttribute("filter") EventLogCategoryFilter filter,
                           HttpServletResponse response,
                           YukonUserContext userContext, 
                           @DefaultItemsPerPage(50) PagingParameters paging) throws IOException {

        List<EventCategory> eventCategories = Lists.newArrayList();
        eventCategories.addAll(Arrays.asList(filter.getCategories()));
        
        // Getting the search results
        SearchResults<EventLog> searchResult = 
            eventLogUIService.getFilteredPagedSearchResultByCategories(eventCategories, 
                   filter.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   filter.getStopDate().plusDays(1).toDateTimeAtStartOfDay(userContext.getJodaTimeZone()),
                   paging.getStartIndex(), 
                   Integer.MAX_VALUE,
                   filter.getFilterValue(),
                   userContext);
        
        // Get column names
        List<String> columnNames = Lists.newArrayList();
        final MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        columnNames.add(accessor.getMessage("yukon.common.events.columnHeader.event"));
        columnNames.add(accessor.getMessage("yukon.common.events.columnHeader.dateAndTime"));
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