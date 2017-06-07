package com.cannontech.web.amr.usageThresholdReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.usageThresholdReport.model.DataAvailability;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdDescriptor;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFormCriteria;
import com.cannontech.web.amr.usageThresholdReport.service.ThresholdReportService;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
@RequestMapping("/usageThresholdReport/*")
public class UsageThresholdReportController {
    
    private final static String baseKey = "yukon.web.modules.amr.usageThresholdReport.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ThresholdReportService reportService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping(value="report", method = RequestMethod.GET)
    public String report(ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Set<BuiltInAttribute> usageAttributes = BuiltInAttribute.getAttributesForGroup(AttributeGroup.USAGE);
        List<BuiltInAttribute> sortedAttributes = new ArrayList<>(usageAttributes);
        BuiltInAttribute.sort(sortedAttributes, accessor);
        model.addAttribute("usageAttributes", sortedAttributes);
        Instant max = new Instant();
        Instant min = max.minus(Duration.standardDays(7));
        ThresholdReportFormCriteria criteria = new ThresholdReportFormCriteria();
        criteria.setStartDate(min);
        criteria.setEndDate(max);
        model.addAttribute("criteria", criteria);
        return "usageThresholdReport/report.jsp";
    }
    

    @RequestMapping(value="report", method = RequestMethod.POST)
    public String runReport(@ModelAttribute ThresholdReportFormCriteria criteria, ModelMap model, HttpServletRequest request, 
                            @RequestParam String minDate, @RequestParam String maxDate, YukonUserContext userContext) 
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        model.addAttribute("dataAvailabilityOptions", DataAvailability.values());
        model.addAttribute("thresholdOptions", ThresholdDescriptor.values());
        DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
        criteria.setDeviceCollection(collection);
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
        Instant start = Instant.parse(minDate, formatter);
        Instant end = Instant.parse(maxDate, formatter);
        criteria.setStartDate(start);
        criteria.setEndDate(end);
        criteria.setRunTime(new Instant());
        if (collection.getCollectionType() == DeviceCollectionType.group) {
            String groupName = collection.getCollectionParameters().get("group.name");
            DeviceGroup grp = deviceGroupService.resolveGroupName(groupName);
            criteria.setDescription(grp.getName());
        } else {
            criteria.setDescription(collection.getDeviceCount() + " devices");
        }
        model.addAttribute("filter", new ThresholdReportFilter(null, 0, null, null, false));
        int reportId = reportService.createThresholdReport(criteria, collection.getDeviceList());
        criteria.setReportId(reportId);
        model.addAttribute("criteria", criteria);
        return "usageThresholdReport/results.jsp";
    }
    
    @RequestMapping(value="results", method = RequestMethod.POST)
    public String filterResults(@ModelAttribute ThresholdReportFilter filter, String[] deviceSubGroups, int reportId, 
                                @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                                PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<DeviceGroup> subGroups = new ArrayList<>();
        if (deviceSubGroups != null) {
            for (String subGroup : deviceSubGroups) {
                subGroups.add(deviceGroupService.resolveGroupName(subGroup));
            }
            filter.setGroups(subGroups);
        }
        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (DetailSortBy column : DetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        SearchResults<ThresholdReportDetail> reportDetail =
                reportService.getReportDetail(reportId, filter, paging, null, dir);
        System.out.println(reportDetail);
        model.addAttribute("detail", reportDetail);
        return "usageThresholdReport/deviceTable.jsp";

    }
    
    public enum DetailSortBy implements DisplayableEnum {

        deviceName,
        meterNumber,
        deviceType,
        serialNumberAddress,
        delta;

        @Override
        public String getFormatKey() {
            return baseKey + "results." + name();
        }
    }
}