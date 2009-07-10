package com.cannontech.web.common.events;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/eventLog/*")
@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
public class EventLogViewerController {

    private EventLogDao eventLogDao;

    @RequestMapping
    public void view(String[] categories, LiteYukonUser user, ModelMap model) throws ServletException {
        List<EventLog> allEvents;
        if (categories != null) {
            Set<EventCategory> eventCategories = Sets.newHashSet();
            for (String categoryStr : categories) {
                EventCategory category = EventCategory.createCategory(categoryStr);
                eventCategories.add(category);
            }
            allEvents = eventLogDao.findAllByCategories(eventCategories);
            model.addAttribute("selectedCategories", ServletUtil.convertSetToMap(eventCategories));
        } else {
            allEvents = Collections.emptyList();
        }

        model.addAttribute("events", allEvents);
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
}
