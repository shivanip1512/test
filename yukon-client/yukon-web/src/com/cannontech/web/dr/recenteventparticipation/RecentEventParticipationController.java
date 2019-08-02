package com.cannontech.web.dr.recenteventparticipation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.recenteventparticipation.model.ControlDeviceDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationStats;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationSummary;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class RecentEventParticipationController {
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired RecentEventParticipationService recentEventParticipationService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DateFormattingService dateFormattingService;

    @RequestMapping(value = "/recenteventparticipation/auditReport", method = RequestMethod.GET)
    public String statistics(ModelMap model, LiteYukonUser user) {
        List<RecentEventParticipationSummary> recentEventParticipationSummary =
            recentEventParticipationService.getRecentEventParticipationSummary(7);
        model.addAttribute("recentEventParticipationSummary", recentEventParticipationSummary);

        return "dr/recenteventparticipation/auditReport.jsp";
    }

    @RequestMapping(value = "/recenteventparticipation/details", method = RequestMethod.GET)
    public String details(ModelMap model, @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to, PagingParameters paging) {
        setUpModel(model, from, to, paging);
        return "dr/recenteventparticipation/details.jsp";
    }

    @RequestMapping("/recenteventparticipation/recentEventsTable")
    public String recentEventsTable(ModelMap model, @RequestParam(required = false) String from,
            @RequestParam(required = false) String to, PagingParameters paging) {
        Instant fromDate = Instant.parse(from);
        Instant toDate = Instant.parse(to);
        setUpModel(model, fromDate.minus(Duration.standardHours(12)), toDate, paging);
        return "dr/recenteventparticipation/recentEventsTable.jsp";
    }

    private void setUpModel(ModelMap model, Instant from, @RequestParam(required = false) Instant to,
            PagingParameters paging) {
        Instant toFullDay = null;
        if (to != null) {
            toFullDay = to.plus(Duration.standardDays(1)).toDateTime().withTimeAtStartOfDay().toInstant();
        }
        Range<Instant> range = (from != null && to != null) ? Range.inclusiveExclusive(from, toFullDay) : null;
        List<RecentEventParticipationStats> result =
            recentEventParticipationService.getRecentEventParticipationStats(range, paging);
        int totalRecentEvents = recentEventParticipationService.getNumberOfEvents(range);
        SearchResults<RecentEventParticipationStats> recentEventParticipationStatsResults =
            SearchResults.pageBasedForSublist(result, paging, totalRecentEvents);
        model.addAttribute("recentEventParticipationStatsResults", recentEventParticipationStatsResults);
        model.addAttribute("totalEvents", totalRecentEvents);
        if (from == null && result.size() > 0) {
            RecentEventParticipationStats recentEventParticipationStats = result.get(result.size() - 1);
            from = recentEventParticipationStats.getStartTime();
        }
        if (to == null) {
            to = new Instant();
        }
        model.addAttribute("from", from);
        model.addAttribute("to", to);
    }

    @RequestMapping(value = "/recenteventparticipation/details/export", method = RequestMethod.GET)
    public void downloadDetails(ModelMap model, @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to, YukonUserContext userContext, HttpServletResponse response)
            throws IOException {
        if (from == null) {
            from = new Instant().minus(Duration.standardDays(7));
        }
        if (to == null) {
            to = new Instant();
        }
        Instant toFullDay = to.plus(Duration.standardDays(1)).toDateTime().withTimeAtStartOfDay().toInstant();
        List<RecentEventParticipationDetail> recentEventParticipationDetails =
            recentEventParticipationService.getRecentEventParticipationDetails(Range.inclusiveExclusive(from, toFullDay));
        List<String> columnNames = getColumnNames(userContext);
        List<List<String>> dataGrid = getGrid(recentEventParticipationDetails);
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, "RecentEventParticipationReport" + "_" + now + ".csv");
    }

    @RequestMapping("/recenteventparticipation/download")
    private void downloadAuditReportForEvent(@RequestParam("eventId") int eventId, YukonUserContext userContext,
            HttpServletResponse response) throws IOException {

        List<RecentEventParticipationDetail> recentEventParticipationDetails =
            recentEventParticipationService.getRecentEventParticipationDetail(eventId);
        List<String> columnNames = getColumnNames(userContext);
        List<List<String>> dataGrid = getGrid(recentEventParticipationDetails);
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, "RecentEventParticipationReport_" + eventId + "_" + now +".csv");
    }

    private List<String> getColumnNames(YukonUserContext userContext) {
        List<String> columnNames = Lists.newArrayList();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.eventID"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.program"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.group"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.startTime"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.stopTime"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.deviceName"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.accountNumber"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.eventPhase"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.participationState"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.serialNumber"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.recentEventParticipation.details.optOutStatus"));
        return columnNames;
    }

    private List<List<String>> getGrid(List<RecentEventParticipationDetail> recentEventParticipationDetails) {
        List<List<String>> lists = new ArrayList<>();
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.BOTH, YukonUserContext.system);
        for (RecentEventParticipationDetail recentEventParticipationDetail : recentEventParticipationDetails) {
            for (ControlDeviceDetail controlDeviceDetail : recentEventParticipationDetail.getDeviceDetails()) {
                List<String> row = new ArrayList<>();
                row.add(new Integer(recentEventParticipationDetail.getControlEventId()).toString());
                row.add(recentEventParticipationDetail.getProgramName());
                row.add(recentEventParticipationDetail.getGroupName());
                row.add(recentEventParticipationDetail.getStartTime().toString(formatter));
                row.add(recentEventParticipationDetail.getStopTime().toString(formatter));
                row.add(controlDeviceDetail.getDeviceName());
                row.add(controlDeviceDetail.getAccountNumber());
                row.add(controlDeviceDetail.getEventPhase());
                row.add(controlDeviceDetail.getParticipationState());
                row.add(controlDeviceDetail.getSerialNumber());
                row.add(controlDeviceDetail.getOptOutStatus().name());
                lists.add(row);
            }
        }
        return lists;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
}
