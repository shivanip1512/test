package com.cannontech.web.common.events;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    public void view(String[] categories, String fromDate, String toDate, YukonUserContext userContext, ModelMap model) throws ServletException, ParseException {
        
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
        
        List<EventLog> allEvents;
        if (categories != null) {
            Set<EventCategory> eventCategories = Sets.newHashSet();
            for (String categoryStr : categories) {
                EventCategory category = EventCategory.createCategory(categoryStr);
                eventCategories.add(category);
            }
            allEvents = eventLogDao.findAllByCategories(eventCategories, startDate, stopDate);
            model.addAttribute("selectedCategories", ServletUtil.convertSetToMap(eventCategories));
        } else {
            allEvents = Collections.emptyList();
        }

        model.addAttribute("events", allEvents);
        model.addAttribute("fromDate", startDate);
        model.addAttribute("toDate", stopDate);
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
