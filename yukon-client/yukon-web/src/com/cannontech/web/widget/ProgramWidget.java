package com.cannontech.web.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.program.widget.model.ProgramData;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.dr.program.service.ProgramWidgetService;
import com.cannontech.dr.program.service.impl.ProgramWidgetServiceImpl;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/programWidget/*")
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class ProgramWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private ProgramWidgetService programWidgetService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private final static String widgetKey = "yukon.web.widgets.";
    private static final Duration SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardSeconds(30);
    @GetMapping("render")
    public String render(ModelMap model, YukonUserContext userContext) {

        Map<String, List<ProgramData>> programsData = programWidgetService.buildProgramWidgetData(userContext);
        model.addAttribute("programsData", programsData);

        Map<String, Object> json = Maps.newHashMap();
        Instant lastUpdateTime = Instant.now();
        json.put("lastUpdateTime", lastUpdateTime);
        Instant nextRun = lastUpdateTime.plus(SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH);   
        json.put("nextRun", nextRun);
        json.put("forceRefreshInterval", SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH.getMillis());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        json.put("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
        model.addAttribute("widgetUpdateDate", json);

        int futureAndTodaysProgramDataCount = 0;
        // Get the first entry from programData map, which is expected as future scheduled programData entry.
        Entry<String, List<ProgramData>> firstProgramDataEntry = programsData.entrySet().iterator().next();
        String key = firstProgramDataEntry.getKey();
        Date date = null;
        try {
            date = new SimpleDateFormat("MM/dd/yyyy").parse(key);
        } catch (ParseException e) {
            // Do nothing here.
        }
        if (date != null && TimeUtil.isFutureDate(new DateTime(date.getTime()))) {
            // firstProgramDataEntry is future schedule programData. Get count of it. 
            futureAndTodaysProgramDataCount += firstProgramDataEntry.getValue().size();
        }
        futureAndTodaysProgramDataCount += programsData.get(accessor.getMessage(widgetKey + "programWidget.today")).size();
        boolean showMessage = futureAndTodaysProgramDataCount >= ProgramWidgetServiceImpl.MAX_PROGRAM_TO_DISPLAY_ON_WIDGET;
        model.addAttribute("showMessage", showMessage);
        return "programWidget/render.jsp";
    }
}
