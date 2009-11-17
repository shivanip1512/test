package com.cannontech.web.common.events;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/eventLog/*")
@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
public class EventLogViewerController {

    private EventLogDao eventLogDao;
    private DateFormattingService dateFormattingService;

    @RequestMapping
    public void view(String[] categories, String fromDate, String toDate, Integer itemsPerPage, Integer page, YukonUserContext userContext, ModelMap model) throws ServletException, ParseException {
        
        Date startDate = dateFormattingService.flexibleDateParser(fromDate, DateOnlyMode.START_OF_DAY, userContext);
        if (startDate == null) {
            startDate = new DateTime().minusDays(1).toDate();
        }
        Date stopDate = dateFormattingService.flexibleDateParser(toDate, DateOnlyMode.END_OF_DAY, userContext);
        if (stopDate == null) {
            stopDate = new Date();
        }
        
        if (stopDate.before(startDate)) {
            throw new RuntimeException("start must be before stop");
        }
        
        if(page == null){
            page = 1;
        }
        if(itemsPerPage == null){
            itemsPerPage = 10;
        }
        
        List<EventLog> allEvents;
        if (categories != null) {
            Set<EventCategory> eventCategories = Sets.newHashSet();
            for (String categoryStr : categories) {
                EventCategory category = EventCategory.createCategory(categoryStr);
                eventCategories.add(category);
            }
            allEvents = eventLogDao.findAllByCategories(eventCategories, startDate, stopDate);
            Map<EventCategory, Boolean> selectedMap = ServletUtil.convertSetToMap(eventCategories); 
            model.addAttribute("selectedCategories", selectedMap);
        } else {
            allEvents = eventLogDao.findAllByCategories(getAllEventCategories(), startDate, stopDate);
            model.addAttribute("selectedCategories", ServletUtil.convertListToMap(getAllEventCategories()));
        }
        
        model.addAttribute("fromDate", startDate);
        model.addAttribute("toDate", stopDate);
        
        SearchResult<EventLog> searchResult = new SearchResult<EventLog>();
        
        List<EventLog> resultList = new ArrayList<EventLog>();
        
        int startIndex = (page - 1) * itemsPerPage;
        for (int index = startIndex; index < allEvents.size() && index - startIndex < itemsPerPage; index++) {
            resultList.add(allEvents.get(index));
        }

        searchResult.setResultList(resultList);
        searchResult.setBounds(startIndex, itemsPerPage, allEvents.size());
        
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("searchResult", searchResult);
        model.addAttribute("events", searchResult.getResultList());
    }

    @ModelAttribute
    public List<EventCategory> getAllEventCategories() {
        Set<EventCategory> allCategories = eventLogDao.getAllCategories();
        List<EventCategory> result = Ordering.natural().sortedCopy(allCategories);
        return result;
    }

    @Autowired
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}
