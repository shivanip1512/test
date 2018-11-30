package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;

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
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.OnOff;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

@Controller
@RequestMapping("/rfBroadcastWidget/*")
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class RfBroadcastWidget extends AdvancedWidgetControllerBase {

    private final static String widgetKey = "yukon.web.widgets.";
    private static final Duration SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardSeconds(30);

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
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
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            Instant lastUpdateTime = now.toInstant();
            model.addAttribute("results", results);
            model.addAttribute("lastAttemptedRefresh", lastUpdateTime);

            Instant nextRun = lastUpdateTime.plus(SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH);
            String nextRefreshDate =
                dateFormattingService.format(nextRun, DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);

            model.addAttribute("forceRefreshInterval", SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH.getMillis());
            model.addAttribute("refreshTooltip", accessor.getMessage(widgetKey + "nextRefresh") + nextRefreshDate);
            model.addAttribute("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
        }
        model.addAttribute("showRfBroadcastWidget", showRfBroadcastWidget);
        return "rfBroadcastWidget/render.jsp";
    }
}
