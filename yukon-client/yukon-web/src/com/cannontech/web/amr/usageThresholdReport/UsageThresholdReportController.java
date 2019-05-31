package com.cannontech.web.amr.usageThresholdReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao.SortBy;
import com.cannontech.web.amr.usageThresholdReport.model.DataAvailability;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdDescriptor;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReport;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;
import com.cannontech.web.amr.usageThresholdReport.service.ThresholdReportService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/usageThresholdReport/*")
@CheckRoleProperty(YukonRoleProperty.USAGE_THRESHOLD_REPORT)
public class UsageThresholdReportController {
    
    private final static String baseKey = "yukon.web.modules.amr.usageThresholdReport.results.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ThresholdReportService reportService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private PaoNotesService paoNotesService;
    
    @RequestMapping(value="report", method = RequestMethod.GET)
    public String report(ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Set<BuiltInAttribute> usageAttributes = BuiltInAttribute.getAttributesForGroup(AttributeGroup.USAGE);
        List<BuiltInAttribute> sortedAttributes = new ArrayList<>(usageAttributes);
        BuiltInAttribute.sort(sortedAttributes, accessor);
        model.addAttribute("usageAttributes", sortedAttributes);
        Instant yesterday = Instant.now().minus(Duration.standardDays(1));
        ThresholdReportCriteria criteria = new ThresholdReportCriteria();
        criteria.setStartDate(yesterday);
        criteria.setEndDate(yesterday);
        criteria.setAttribute(BuiltInAttribute.USAGE);
        Object criteriaObj = model.get("criteria");
        if (criteriaObj instanceof ThresholdReportCriteria) {
            criteria = (ThresholdReportCriteria) model.get("criteria");
        }
        model.addAttribute("thresholdOptions", ThresholdDescriptor.values());
        model.addAttribute("criteria", criteria);
        return "usageThresholdReport/report.jsp";
    }

    @RequestMapping(value="report", method = RequestMethod.POST)
    public String runReport(@ModelAttribute ThresholdReportCriteria criteria, ModelMap model, HttpServletRequest request, 
                            @RequestParam String minDate, @RequestParam String maxDate, YukonUserContext userContext, 
                            FlashScope flashScope, RedirectAttributes redirectAtts) throws Exception {
        model.addAttribute("dataAvailabilityOptions", DataAvailability.values());
        model.addAttribute("thresholdOptions", ThresholdDescriptor.values());
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
        DateTimeZone timeZone = userContext.getJodaTimeZone();
        DateTime startDateTime = formatter.parseDateTime(minDate).withTimeAtStartOfDay().withZone(timeZone);
        DateTime endDateTime = formatter.parseDateTime(maxDate).withTimeAtStartOfDay().withZone(timeZone);
        criteria.setStartDate(startDateTime.toInstant());
        criteria.setEndDate(endDateTime.toInstant());
        criteria.setRunTime(new Instant());
        DeviceCollection collection = null;
        try {
            collection = deviceCollectionFactory.createDeviceCollection(request);
        } catch (Exception e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.amr.usageThresholdReport.report.criteria.invalidDevicesSelected"));
            redirectAtts.addFlashAttribute("criteria", criteria);
            return "redirect:/amr/usageThresholdReport/report";
        }
        if (collection != null) {
            if (collection.getCollectionType() == DeviceCollectionType.group) {
                String groupName = collection.getCollectionParameters().get("group.name");
                DeviceGroup grp = deviceGroupService.resolveGroupName(groupName);
                criteria.setDescription(grp.getName());
                model.addAttribute("totalDeviceCount", collection.getDeviceCount());
            } else {
                criteria.setDescription(collection.getDeviceCount() + " devices");
            }
            int reportId = reportService.createThresholdReport(criteria, collection.getDeviceList());
            criteria.setReportId(reportId);
        }
        ThresholdReportFilter filter = new ThresholdReportFilter();
        filter.setThresholdDescriptor(criteria.getThresholdDescriptor());
        filter.setThreshold(criteria.getThreshold());
        model.addAttribute("filter", filter);
        model.addAttribute("criteria", criteria);
        return "usageThresholdReport/results.jsp";
    }
    
    @RequestMapping(value="results", method = RequestMethod.GET)
    public String filterResults(int reportId, String[] deviceSubGroups, boolean includeDisabled, 
                                ThresholdDescriptor thresholdDescriptor, double threshold, DataAvailability[] availability,
                                @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                                @DefaultItemsPerPage(value=250) PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        ThresholdReportFilter filter = createFilter(deviceSubGroups, includeDisabled, thresholdDescriptor, threshold, availability);
        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (DetailSortBy column : DetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        ThresholdReport report = reportService.getReportDetail(reportId, filter, paging, sortBy.getValue(), dir);
        model.addAttribute("report", report);
        List<SimpleDevice> devices = report.getAllDevices();
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup,  devices);
        
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(report.getDetail()
                                                                           .getResultList()
                                                                           .stream()
                                                                           .map(detail -> detail.getPaoIdentifier().getPaoId())
                                                                           .collect(Collectors.toList()));        
        model.addAttribute("notesList", notesList);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("filter", filter);
        model.addAttribute("reportId", reportId);
        model.addAttribute("dataAvailabilityOptions", DataAvailability.values());
        return "usageThresholdReport/deviceTable.jsp";
    }
    
    private ThresholdReportFilter createFilter(String[] deviceSubGroups, boolean includeDisabled, 
                                               ThresholdDescriptor thresholdDescriptor, double threshold, DataAvailability[] availability) {
        ThresholdReportFilter filter = new ThresholdReportFilter();
        List<DeviceGroup> subGroups = new ArrayList<>();
        if (deviceSubGroups != null) {
            for (String subGroup : deviceSubGroups) {
                subGroups.add(deviceGroupService.resolveGroupName(subGroup));
            }
        }
        filter.setGroups(subGroups);
        filter.setIncludeDisabled(includeDisabled);
        filter.setThresholdDescriptor(thresholdDescriptor);
        filter.setThreshold(threshold);
        List<DataAvailability> selectedAvail = new ArrayList<>();
        if (availability != null) {
            selectedAvail = Lists.newArrayList(availability);
        }
        filter.setAvailability(selectedAvail);
        return filter;
    }
    
    @RequestMapping("download")
    public String download(YukonUserContext userContext, int reportId, String[] deviceSubGroups, boolean includeDisabled, 
                           ThresholdDescriptor thresholdDescriptor, double threshold, DataAvailability[] availability,
                          @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                          @DefaultItemsPerPage(value=250) PagingParameters paging,
                          HttpServletResponse response) throws IOException {
        ThresholdReportFilter filter = createFilter(deviceSubGroups, includeDisabled, thresholdDescriptor, threshold, availability);

        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        ThresholdReport allDevicesReport = reportService.getReportDetail(reportId, filter, PagingParameters.EVERYTHING, sortBy.getValue(), dir);
        
        String[] headerRow = getHeaderRows(userContext);
        List<String[]> dataRows = getDataRows(allDevicesReport, userContext);

        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "usageThresholdReport_" + now + ".csv");
        return null;
      }
    
    @RequestMapping("downloadAll")
    public String downloadAll(YukonUserContext userContext, int reportId, 
                          HttpServletResponse response) throws IOException {
        ThresholdReportFilter filter = new ThresholdReportFilter();
        filter.setThresholdDescriptor(null);
        filter.setAvailability(Lists.newArrayList(DataAvailability.values()));
        ThresholdReport allDevicesReport = reportService.getReportDetail(reportId, filter, PagingParameters.EVERYTHING, DetailSortBy.deviceName.value, Direction.asc);

        String[] headerRow = getHeaderRows(userContext);
        List<String[]> dataRows = getDataRows(allDevicesReport, userContext);

        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "usageThresholdReport_" + now + ".csv");
        return null;
      }
    
    private String[] getHeaderRows(YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[15];

        headerRow[0] = accessor.getMessage(DetailSortBy.deviceName);
        headerRow[1] = accessor.getMessage(DetailSortBy.meterNumber);
        headerRow[2] = accessor.getMessage(DetailSortBy.deviceType);
        headerRow[3] = accessor.getMessage(DetailSortBy.serialNumberAddress);
        headerRow[4] = accessor.getMessage(DetailSortBy.primaryGateway);
        headerRow[5] = accessor.getMessage(DetailSortBy.delta);
        headerRow[6] = accessor.getMessage(baseKey + "dataAvailability");
        headerRow[7] = accessor.getMessage(baseKey + "earliest.timestamp");
        headerRow[8] = accessor.getMessage(baseKey + "earliest.value");
        headerRow[9] = accessor.getMessage(baseKey + "earliest.units");
        headerRow[10] = accessor.getMessage(baseKey + "earliest.quality");
        headerRow[11] = accessor.getMessage(baseKey + "latest.timestamp");
        headerRow[12] = accessor.getMessage(baseKey + "latest.value");
        headerRow[13] = accessor.getMessage(baseKey + "latest.units");
        headerRow[14] = accessor.getMessage(baseKey + "latest.quality");
        
        return headerRow;
    }
    
    private List<String[]> getDataRows(ThresholdReport allDevicesReport, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<String[]> dataRows = Lists.newArrayList();
        for (ThresholdReportDetail detail: allDevicesReport.getDetail().getResultList()) {
            String[] dataRow = new String[15];
            dataRow[0] = detail.getDeviceName();
            dataRow[1] = detail.getMeterNumber();
            dataRow[2] = detail.getPaoIdentifier().getPaoType().getPaoTypeName();
            dataRow[3] = detail.getAddressSerialNumber();
            dataRow[4] = detail.getGatewayName();
            if (detail.getDelta() != null) {
                dataRow[5] = detail.getDelta().toString();
            }
            if (detail.getAvailability() != null) {
                dataRow[6] = accessor.getMessage(baseKey + "dataAvailability." + detail.getAvailability().name());
            }
            PointValueQualityHolder earlyRead = detail.getEarliestReading();
            if (earlyRead != null) {
                dataRow[7] = pointFormattingService.getValueString(earlyRead, Format.DATE, userContext);
                dataRow[8] = pointFormattingService.getValueString(earlyRead, Format.VALUE, userContext);
                dataRow[9] = pointFormattingService.getValueString(earlyRead, Format.UNIT, userContext);
                dataRow[10] = pointFormattingService.getValueString(earlyRead, Format.QUALITY, userContext);
            } else {
                dataRow[7] = accessor.getMessage(baseKey + "noReadingFound");
            }
            PointValueQualityHolder lateRead = detail.getLatestReading();
            if (lateRead != null) {
                dataRow[11] = pointFormattingService.getValueString(lateRead, Format.DATE, userContext);
                dataRow[12] = pointFormattingService.getValueString(lateRead, Format.VALUE, userContext);
                dataRow[13] = pointFormattingService.getValueString(lateRead, Format.UNIT, userContext);
                dataRow[14] = pointFormattingService.getValueString(lateRead, Format.QUALITY, userContext);
            } else {
                dataRow[11] = accessor.getMessage(baseKey + "noReadingFound");
            }
            dataRows.add(dataRow);
        }
        return dataRows;
    }
    
    public enum DetailSortBy implements DisplayableEnum {

        deviceName(SortBy.DEVICE_NAME),
        meterNumber(SortBy.METER_NUMBER),
        deviceType(SortBy.DEVICE_TYPE),
        serialNumberAddress(SortBy.SERIAL_NUMBER_ADDRESS),
        primaryGateway(SortBy.PRIMARY_GATEWAY),
        delta(SortBy.DELTA),
        earliestReading(SortBy.EARLIEST_READING),
        latestReading(SortBy.LATEST_READING);
        
        private DetailSortBy(SortBy value) {
            this.value = value;
        }

        private final SortBy value;

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}