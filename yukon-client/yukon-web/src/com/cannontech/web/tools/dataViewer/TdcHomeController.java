package com.cannontech.web.tools.dataViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.DisplayType;
import com.cannontech.common.tdc.model.IDisplay;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

@Controller
@CheckRole(YukonRole.TABULAR_DISPLAY_CONSOLE)
public class TdcHomeController {
    
    @Autowired private DisplayDao displayDao;
    @Autowired private TdcService tdcService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    @RequestMapping("data-viewer")
    public String home(ModelMap model) {
        
       List<Display> custom = displayDao.getDisplayByType(DisplayType.CUSTOM_DISPLAYS);
       List<Display> events = displayDao.getDisplayByType(DisplayType.ALARMS_AND_EVENTS);
       List<Display> topEvents = new ArrayList<>();
       ListIterator<Display> it =  events.listIterator();
        while (it.hasNext()) {
            Display display = it.next();
            switch (display.getDisplayId()) {
            case IDisplay.SOE_LOG_DISPLAY_NUMBER:
            case IDisplay.TAG_LOG_DISPLAY_NUMBER:
            case IDisplay.EVENT_VIEWER_DISPLAY_NUMBER:
            case IDisplay.GLOBAL_ALARM_DISPLAY:
                topEvents.add(display);
                it.remove();
            }
        }
        model.addAttribute("customEvents", custom);
        model.addAttribute("events", events);
        model.addAttribute("topEvents", topEvents);
        return "data-viewer/home.jsp";
    }
    
    
    @RequestMapping("data-viewer/refresh")
    public String refresh(ModelMap model) {
        
        // unacknowledged alarms
        List<DisplayData> unackAlarms = tdcService.getAlarms(false);
        // unacknowledged or active alarms
        List<DisplayData> alarms = tdcService.getAlarms(true);
        model.addAttribute("alarms", alarms);
        Map<DisplayData, DisplayData> mappedAlarms =
            Maps.uniqueIndex(unackAlarms, new Function<DisplayData, DisplayData>() {
                @Override
                public DisplayData apply(DisplayData data) {
                    return data;
                }
            });
        model.addAttribute("unackAlarms", mappedAlarms);
        model.addAttribute("colorStateBoxes", tdcService.getUnackAlarmColorStateBoxes(null, alarms));
        model.addAttribute("allAlarmsDislay", IDisplay.GLOBAL_ALARM_DISPLAY);
        return "data-viewer/alarming.jsp";
    }
     
    
    @RequestMapping(value="data-viewer/acknowledge-all", method=RequestMethod.POST)
    public @ResponseBody Map<String, String> acknowledgeAll(YukonUserContext context) {
        
        int alarms = tdcService.acknowledgeAllAlarms(context.getYukonUser());
        MessageSourceResolvable successMsg =
                new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.ack.success", alarms);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        return Collections.singletonMap("success", accessor.getMessage(successMsg));
    }
    
    @RequestMapping(value="data-viewer/acknowledge", method=RequestMethod.POST)
    public @ResponseBody Map<String, String> acknowledge(YukonUserContext context, int pointId, int condition) {
        
        tdcService.acknowledgeAlarm(pointId, condition, context.getYukonUser());
        return Collections.singletonMap("success", "success");

    }
}