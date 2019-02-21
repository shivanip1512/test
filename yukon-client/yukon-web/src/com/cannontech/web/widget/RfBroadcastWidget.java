package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.Range;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.OnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/rfBroadcastWidget/*")
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class RfBroadcastWidget extends AdvancedWidgetControllerBase {

    private final static String widgetKey = "yukon.web.widgets.";
    private static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardMinutes(1);

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PerformanceVerificationDao rfPerformanceDao;

    @GetMapping("render")
    public String updateWidget(ModelMap model, YukonUserContext userContext) {
        
        OnOff rfPerformance = globalSettingDao.getEnum(GlobalSettingType.RF_BROADCAST_PERFORMANCE, OnOff.class);
        boolean showRfBroadcastWidget = (rfPerformance == OnOff.ON);
        if (showRfBroadcastWidget) {
            /* Get last 6 days Rf Broadcast Performance data */
            DateTime now = DateTime.now();
            Instant to = now.toInstant();
            Instant from = now.withTimeAtStartOfDay().minusDays(5).toInstant();
            List<PerformanceVerificationEventMessageStats> results = rfPerformanceDao.getReports(Range.inclusiveExclusive(from, to));
            Collections.sort(results, (t1, t2) -> t2.getTimeMessageSent().compareTo(t1.getTimeMessageSent()));
            model.addAttribute("results", results);
            
            Map<String, Object> json = Maps.newHashMap();
            Instant lastUpdateTime = now.toInstant();
            json.put("lastUpdateTime", lastUpdateTime);
            Instant nextRun = lastUpdateTime.plus(MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH);
            json.put("nextRun", nextRun);
            json.put("forceRefreshInterval", MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH.getMillis());
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            json.put("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
            model.addAttribute("widgetUpdateDate", json);
        }
        model.addAttribute("showRfBroadcastWidget", showRfBroadcastWidget);
        return "rfBroadcastWidget/render.jsp";
    }
}
