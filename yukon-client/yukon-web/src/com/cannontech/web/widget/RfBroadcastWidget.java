package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

@Controller
@RequestMapping("/rfBroadcastWidget/*")
public class RfBroadcastWidget extends AdvancedWidgetControllerBase {

    private final static String widgetKey = "yukon.web.widgets.";
    private static final Duration SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardSeconds(30);

    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;

    @RequestMapping("render")
    public String updateWidget(ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Instant lastUpdateTime = new Instant();
        // Get latest 6 RFBroadcast event data.
        List<PerformanceVerificationEventMessageStats> results = getRFBroadcastMockData();
        model.addAttribute("results", results);
        model.addAttribute("lastAttemptedRefresh", lastUpdateTime);
        Instant nextRun = lastUpdateTime.plus(SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH);
        String nextRefreshDate = dateFormattingService.format(nextRun, DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
        model.addAttribute("forceRefreshInterval", SECONDS_TO_WAIT_BEFORE_NEXT_REFRESH.getMillis());
        model.addAttribute("refreshTooltip", accessor.getMessage(widgetKey + "nextRefresh") + nextRefreshDate);
        model.addAttribute("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
        return "rfBroadcastWidget/render.jsp";
    }

    //TODO This method needs to be deleted after back-end changes (YUK-18962)
    private List<PerformanceVerificationEventMessageStats> getRFBroadcastMockData(){
        List<PerformanceVerificationEventMessageStats> tests = new ArrayList<>();
        tests.add(new PerformanceVerificationEventMessageStats(1, Instant.now(), false, 2,2,3));
        tests.add(new PerformanceVerificationEventMessageStats(2, Instant.now().minus(86400000), false, 5,5,6));
        tests.add(new PerformanceVerificationEventMessageStats(3, Instant.now().minus(86400000 * 2), false, 6,6,6));
        tests.add(new PerformanceVerificationEventMessageStats(4, Instant.now().minus(86400000 * 3), false, 5,5,6));
        tests.add(new PerformanceVerificationEventMessageStats(5, Instant.now().minus(86400000 * 4), false, 1,4,6));
        tests.add(new PerformanceVerificationEventMessageStats(6, Instant.now().minus(86400000 * 5), false, 5,4,6));
        return tests;
    }

}
