package com.cannontech.web.amr.dataCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionActionUrl;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.SortBy;
import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
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
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;
import com.cannontech.web.tools.mapping.MappingColorCollection;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/dataCollection/*")
public class DataCollectionController {
    
    @Autowired private DataCollectionWidgetService dataCollectionWidgetService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private DeviceMemoryCollectionProducer producer;
    
    private final static String baseKey = "yukon.web.modules.amr.dataCollection.detail.";
    private final static String widgetKey = "yukon.web.widgets.";
    private Logger log = YukonLogManager.getLogger(DataCollectionController.class);
    
    @RequestMapping(value="updateChart", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> updateChart(String deviceGroup, Boolean includeDisabled, YukonUserContext userContext) throws Exception {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        try {
            DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
            DataCollectionSummary summary = dataCollectionWidgetService.getDataCollectionSummary(group, includeDisabled);
            if (summary.getTotalDeviceCount() == 0) {
                String errorMsg = accessor.getMessage(widgetKey + "dataCollectionWidget.noDevicesFound", StringEscapeUtils.escapeXml11(deviceGroup));
                json.put("errorMessage", errorMsg);
            } else {
                json.put("summary",  summary);
            }
        } catch (NotFoundException e) {
            String errorMsg = accessor.getMessage(widgetKey + "dataCollectionWidget.deviceGroupNotFound", StringEscapeUtils.escapeXml11(deviceGroup));
            log.error(errorMsg);
            json.put("errorMessage", errorMsg);
        }

        json.put("lastAttemptedRefresh", dataCollectionWidgetService.getRunTime(false));
        Instant nextRun = dataCollectionWidgetService.getRunTime(true);
        if (nextRun.isAfterNow()) {
            json.put("nextRefresh", nextRun);
            json.put("isRefreshPossible", false);
        } else {
            json.put("isRefreshPossible", true);
            json.put("refreshTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
        }
        return json;
    }
    
    @RequestMapping("forceUpdate")
    public @ResponseBody Map<String, Object> forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        dataCollectionWidgetService.collectData();
        json.put("success", true);
        return json;
    }
    
    @RequestMapping("detail")
    public String detail(ModelMap model, String deviceGroup, String[] deviceSubGroups, Boolean includeDisabled, RangeType[] ranges, YukonUserContext userContext,
                         @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                         @DefaultItemsPerPage(value=250) PagingParameters paging) throws Exception {
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        DataCollectionSummary summary = dataCollectionWidgetService.getDataCollectionSummary(group, includeDisabled);
        model.addAttribute("summary", summary);
        model.addAttribute("rangeTypes", RangeType.values());
        
        getDeviceResults(model, sorting, paging, userContext, deviceGroup, deviceSubGroups, includeDisabled, ranges);

        return "dataCollection/detail.jsp";
    }
    
    @RequestMapping("deviceResults")
    public String deviceResults(@DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
            PagingParameters paging, ModelMap model, YukonUserContext userContext, String deviceGroup, String[] deviceSubGroups, Boolean includeDisabled, RangeType[] ranges, 
            HttpServletRequest request, FlashScope flash) throws ServletException {
        getDeviceResults(model, sorting, paging, userContext, deviceGroup, deviceSubGroups, includeDisabled, ranges);
        return "dataCollection/deviceTable.jsp";
    }
    
    private void getDeviceResults(ModelMap model, SortingParameters sorting, PagingParameters paging, 
                                  YukonUserContext userContext, String deviceGroup, String[] deviceSubGroups, Boolean includeDisabled, RangeType[] ranges) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        if (ranges == null) {
            ranges = RangeType.values();
        }
        model.addAttribute("deviceGroup", deviceGroup);
        model.addAttribute("deviceGroupName", group.getName());
        DeviceCollection allDevicesCollection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        model.addAttribute("totalDeviceCount", allDevicesCollection.getDeviceCount());
        model.addAttribute("deviceSubGroups", deviceSubGroups);
        model.addAttribute("includeDisabled", includeDisabled);
        model.addAttribute("ranges", ranges);

        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        SearchResults<DeviceCollectionDetail> detail = dataCollectionWidgetService.getDeviceCollectionResult(group, subGroups, includeDisabled, Lists.newArrayList(ranges), paging, sortBy.getValue(), dir);

        for (DetailSortBy column : DetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        model.addAttribute("detail", detail);
        
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(detail.getResultList()
                                                                           .stream()
                                                                           .map(det -> det.getPaoIdentifier().getPaoId())
                                                                           .collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);
        
        //add collection action types for page
        model.addAttribute("collectionActionsType", CollectionActionUrl.COLLECTION_ACTIONS);
        model.addAttribute("mappingType", CollectionActionUrl.MAPPING);
        model.addAttribute("sendCommandType", CollectionActionUrl.SEND_COMMAND);
        model.addAttribute("readAttributeType", CollectionActionUrl.READ_ATTRIBUTE);
        
    }
    
    private List<DeviceGroup> retrieveSubGroups(String[] deviceSubGroups) {
        List<DeviceGroup> subGroups = new ArrayList<>();
        if (deviceSubGroups != null) {
            for (String subGroup : deviceSubGroups) {
                subGroups.add(deviceGroupService.resolveGroupName(subGroup));
            }
        }
        return subGroups;
    }
    
    @RequestMapping(value="collectionAction", method=RequestMethod.GET)
    public String collectionAction(CollectionActionUrl actionType, String deviceGroup, String[] deviceSubGroups, Boolean includeDisabled,
                                   RangeType[] ranges, RedirectAttributes attrs) {
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        SearchResults<DeviceCollectionDetail> allDetail = dataCollectionWidgetService.getDeviceCollectionResult(group, subGroups, includeDisabled,
                                                                  Lists.newArrayList(ranges), PagingParameters.EVERYTHING, SortBy.DEVICE_NAME, Direction.asc);
        List<YukonPao> devices = allDetail.getResultList().stream().map(d -> new SimpleDevice(d.getPaoIdentifier())).collect(Collectors.toList());
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        if (actionType == CollectionActionUrl.MAPPING) {
            List<MappingColorCollection> colorCollections = new ArrayList<MappingColorCollection>();
            Map<String, List<Integer>> mappingMap = new HashMap<String, List<Integer>>();
            for (RangeType range : ranges) {
                List<Integer> deviceIds = allDetail.getResultList().stream()
                    .filter(detail -> detail.getRange().equals(range))
                    .map(d -> d.getPaoIdentifier().getPaoId())
                    .collect(Collectors.toList());
                DeviceCollection rangeCollection = producer.createDeviceCollection(deviceIds);
                MappingColorCollection mapCollection = new MappingColorCollection(rangeCollection, range.getColor(), range.getLabelKey());
                colorCollections.add(mapCollection);
                mappingMap.put(range.getColor(), deviceIds);
            }
            attrs.addFlashAttribute("mappingColors", mappingMap);
            attrs.addFlashAttribute("colorCollections", colorCollections);
        }
        return "redirect:" + actionType.getUrl() + "?collectionType=group&group.name=" + tempGroup.getFullName();
    }
    
    @RequestMapping("download")
    public String download(YukonUserContext userContext, String deviceGroup, String[] deviceSubGroups, Boolean includeDisabled, RangeType[] ranges, 
                          @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                          @DefaultItemsPerPage(value=250) PagingParameters paging,
                          HttpServletResponse response) throws IOException {
        paging = PagingParameters.EVERYTHING;
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        SearchResults<DeviceCollectionDetail> details = dataCollectionWidgetService.getDeviceCollectionResult(group,
            subGroups, includeDisabled, Lists.newArrayList(ranges), paging, sortBy.getValue(), dir);

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[10];

        headerRow[0] = accessor.getMessage(DetailSortBy.deviceName);
        headerRow[1] = accessor.getMessage(DetailSortBy.meterNumber);
        headerRow[2] = accessor.getMessage(DetailSortBy.deviceType);
        headerRow[3] = accessor.getMessage(DetailSortBy.serialNumberAddress);
        headerRow[4] = accessor.getMessage(DetailSortBy.primaryGateway);
        headerRow[5] = accessor.getMessage(baseKey + "category");
        headerRow[6] = accessor.getMessage(baseKey + "timestamp");
        headerRow[7] = accessor.getMessage(baseKey + "value");
        headerRow[8] = accessor.getMessage(baseKey + "units");
        headerRow[9] = accessor.getMessage(baseKey + "quality");


        List<String[]> dataRows = Lists.newArrayList();
        for (DeviceCollectionDetail detail: details.getResultList()) {
            String[] dataRow = new String[10];
            dataRow[0] = detail.getDeviceName();
            dataRow[1] = detail.getMeterNumber();
            dataRow[2] = detail.getPaoIdentifier().getPaoType().getPaoTypeName();
            dataRow[3] = detail.getAddressSerialNumber();
            dataRow[4] = detail.getGatewayName();
            if (detail.getRange() != null) {
                dataRow[5] = accessor.getMessage(baseKey + "rangeType." + detail.getRange().name());
            }
            if (detail.getValue() != null) {
                dataRow[6] = pointFormattingService.getValueString(detail.getValue(), Format.DATE, userContext);
                dataRow[7] = pointFormattingService.getValueString(detail.getValue(), Format.VALUE, userContext);
                dataRow[8] = pointFormattingService.getValueString(detail.getValue(), Format.UNIT, userContext);
                dataRow[9] = pointFormattingService.getValueString(detail.getValue(), Format.QUALITY, userContext);
            } else {
                dataRow[6] = accessor.getMessage(baseKey + "noRecentReadingFound");
            }
            dataRows.add(dataRow);
        }
        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "recentReadings_" + group.getName() + "_" + now + ".csv");
        return null;
      }

    
    public enum DetailSortBy implements DisplayableEnum {

        deviceName(SortBy.DEVICE_NAME),
        meterNumber(SortBy.METER_NUMBER),
        deviceType(SortBy.DEVICE_TYPE),
        serialNumberAddress(SortBy.SERIAL_NUMBER_ADDRESS),
        primaryGateway(SortBy.PRIMARY_GATEWAY),
        recentReading(SortBy.TIMESTAMP);
        
        private DetailSortBy(SortBy value) {
            this.value = value;
        }
        
        private final SortBy value;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }

        public SortBy getValue() {
            return value;
        }
    }
}
