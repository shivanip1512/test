package com.cannontech.web.dr.controlaudit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.controlaudit.model.ControlAuditDetail;
import com.cannontech.dr.controlaudit.model.ControlAuditStats;
import com.cannontech.dr.controlaudit.model.ControlAuditSummary;
import com.cannontech.dr.controlaudit.model.ControlDeviceDetail;
import com.cannontech.dr.controlaudit.service.ControlEventService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class ControlAuditController {
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired ControlEventService controlEventService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;

    @RequestMapping(value = "/controlaudit/auditReport", method = RequestMethod.GET)
    public String statistics(ModelMap model, LiteYukonUser user) {
        List<ControlAuditSummary> controlAuditSummary = controlEventService.getControlAuditSummary(7);
        model.addAttribute("controlAuditSummary", controlAuditSummary);

        return "dr/controlaudit/auditReport.jsp";
    }

    @RequestMapping(value = "/controlaudit/details", method = RequestMethod.GET)
    public String details(ModelMap model, @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to, @DefaultItemsPerPage(15) PagingParameters paging,
            @DefaultSort(dir = Direction.asc, sort = "EventId") SortingParameters sorting) {
        if (from == null) {
            from = new Instant().minus(Duration.standardDays(7));
        }
        if (to == null) {
            to = new Instant();
        }
        Instant toFullDay = to.plus(Duration.standardDays(1)).toDateTime().withTimeAtStartOfDay().toInstant();

        model.addAttribute("from", from);
        model.addAttribute("to", to);

        List<ControlAuditStats> result =
            controlEventService.getControlAuditStats(Range.inclusiveExclusive(from, toFullDay), paging, sorting);
        int totalCount = result != null ? result.size() : 0;
        SearchResults<ControlAuditStats> auditEventMessageStats =
            SearchResults.pageBasedForSublist(result, paging, totalCount);
        model.addAttribute("auditEventMessageStats", auditEventMessageStats);
        return "dr/controlaudit/details.jsp";
    }

    @RequestMapping(value = "/controlaudit/details/{export}", method = RequestMethod.GET)
    public void downloadDetails(ModelMap model, @PathVariable String export,
            @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to,
            @DefaultItemsPerPage(15) PagingParameters paging,
            @DefaultSort(dir = Direction.asc, sort = "EventId") SortingParameters sorting,
            YukonUserContext userContext, HttpServletResponse response) throws IOException {
        if (from == null) {
            from = new Instant().minus(Duration.standardDays(7));
        }
        if (to == null) {
            to = new Instant();
        }
        Instant toFullDay = to.plus(Duration.standardDays(1)).toDateTime().withTimeAtStartOfDay().toInstant();

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<ControlAuditDetail> controlAuditDetails =
            controlEventService.getControlAuditDetails(Range.inclusiveExclusive(from, toFullDay));

        List<String> columnNames = Lists.newArrayList();

        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.eventID"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.program"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.group"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.startTime"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.stopTime"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.accountNumber"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.deviceName"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.eventPhase"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.participationState"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.serialNumber"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.optOutStatus"));

        List<List<String>> dataGrid = getGrid(controlAuditDetails, accessor);

        WebFileUtils.writeToCSV(response, columnNames, dataGrid, "ControlAuditReport" + ".csv");
    }

    @RequestMapping("/controlaudit/download")
    private void downloadAuditReportForEvent(@RequestParam("eventId") int eventId,
            @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to,
            YukonUserContext userContext, HttpServletResponse response) throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        ControlAuditDetail controlAuditDetail = controlEventService.getControlAuditDetail(eventId);

        List<String> columnNames = Lists.newArrayList();

        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.eventID"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.program"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.group"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.startTime"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.stopTime"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.accountNumber"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.deviceName"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.eventPhase"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.participationState"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.serialNumber"));
        columnNames.add(accessor.getMessage("yukon.web.modules.dr.controlaudit.details.optOutStatus"));

        List<List<String>> dataGrid = getGrid(controlAuditDetail, accessor);

        WebFileUtils.writeToCSV(response, columnNames, dataGrid, "ControlAuditReport_" + eventId + ".csv");
    }

    private List<List<String>> getGrid(ControlAuditDetail controlAuditDetail, MessageSourceAccessor accessor) {
        List<List<String>> lists = new ArrayList<>();

        for (ControlDeviceDetail controlDeviceDetail : controlAuditDetail.getDeviceDetails()) {
            List<String> row = new ArrayList<>();
            row.add(new Integer(controlAuditDetail.getControlEventId()).toString());
            row.add(controlAuditDetail.getProgramName());
            row.add(controlAuditDetail.getGroupName());
            row.add(controlAuditDetail.getStartTime().toString());
            row.add(controlAuditDetail.getStopTime().toString());
            row.add(controlAuditDetail.getAccountNumber());
            row.add(controlDeviceDetail.getDeviceName());
            row.add(controlDeviceDetail.getEventPhase());
            row.add(controlDeviceDetail.getParticipationState());
            row.add(controlDeviceDetail.getSerialNumber());
            row.add(controlDeviceDetail.getOptOutStatus().name());
            lists.add(row);
        }

        return lists;
    }

    private List<List<String>> getGrid(List<ControlAuditDetail> controlAuditDetails, MessageSourceAccessor accessor) {
        List<List<String>> lists = new ArrayList<>();
        for (ControlAuditDetail controlAuditDetail : controlAuditDetails) {
            List<String> row = new ArrayList<>();
            row.add(new Integer(controlAuditDetail.getControlEventId()).toString());
            row.add(controlAuditDetail.getProgramName());
            row.add(controlAuditDetail.getGroupName());
            row.add(controlAuditDetail.getStartTime().toString());
            for (ControlDeviceDetail controlDeviceDetail : controlAuditDetail.getDeviceDetails()) {
                row.add(controlAuditDetail.getStopTime().toString());
                row.add(controlAuditDetail.getAccountNumber());
                row.add(controlDeviceDetail.getDeviceName());
                row.add(controlDeviceDetail.getEventPhase());
                row.add(controlDeviceDetail.getParticipationState());
                row.add(controlDeviceDetail.getSerialNumber());
                row.add(controlDeviceDetail.getOptOutStatus().name());
            }
            lists.add(row);
        }
        return lists;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
}
