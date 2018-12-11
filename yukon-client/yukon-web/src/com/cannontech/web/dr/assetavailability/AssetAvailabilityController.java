package com.cannontech.web.dr.assetavailability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
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
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.widgets.model.AssetAvailabilityWidgetSummary;
import com.cannontech.web.common.widgets.service.AssetAvailabilityWidgetService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/assetAvailability/*")
@CheckRoleProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY)
public class AssetAvailabilityController {

    private final static String widgetKey = "yukon.web.widgets.";
    private final static String baseKey = "yukon.web.modules.dr.assetAvailability.detail.";
    
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AssetAvailabilityWidgetService assetAvailabilityWidgetService;
    @Autowired private IDatabaseCache cache;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;

    @GetMapping(value = "updateChart")
    public @ResponseBody Map<String, Object> updateChart(Integer controlAreaOrProgramOrScenarioId, YukonUserContext userContext) throws Exception {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();

        Instant lastUpdateTime = new Instant();
        AssetAvailabilityWidgetSummary summary = assetAvailabilityWidgetService.getAssetAvailabilitySummary(controlAreaOrProgramOrScenarioId, lastUpdateTime);

        if (summary.getTotalDeviceCount() == 0) {
            String errorMsg = accessor.getMessage(widgetKey + "assetAvailabilityWidget.noDevicesFound");
            json.put("errorMessage", errorMsg);
        } else {
            json.put("summary",  summary);
        }

        json.put("lastAttemptedRefresh", lastUpdateTime);
        Instant nextRun = assetAvailabilityWidgetService.getNextRefreshTime(lastUpdateTime);
        json.put("refreshMillis", assetAvailabilityWidgetService.getRefreshMilliseconds());
        String nextRefreshDate = dateFormattingService.format(nextRun, DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
        json.put("refreshTooltip", accessor.getMessage(widgetKey + "nextRefresh") + nextRefreshDate);
        json.put("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));

        return json;
    }
    
    @GetMapping(value = "detail")
    public String detail(ModelMap model, Integer paobjectId, AssetAvailabilityCombinedStatus[] statuses,
            YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        Instant lastUpdateTime = new Instant();
        AssetAvailabilityWidgetSummary summary = assetAvailabilityWidgetService.getAssetAvailabilitySummary(
            paobjectId, lastUpdateTime);
        model.addAttribute("summary", summary);
        model.addAttribute("totalDevices", summary.getTotalDeviceCount());
        model.addAttribute("statusTypes", AssetAvailabilityCombinedStatus.values());
        model.addAttribute("paoName", cache.getAllPaosMap().get(paobjectId).getPaoName());
        model.addAttribute("paobjectId", paobjectId);
        if (statuses == null) {
            model.addAttribute("statuses", AssetAvailabilityCombinedStatus.values());
        } else {
            model.addAttribute("statuses", statuses);
        }
        
        /* set Asset Availability filter help text. */
        long totalHours = globalSettingDao.getInteger(GlobalSettingType.LAST_RUNTIME_HOURS);
        long totalDays = TimeUnit.DAYS.convert(totalHours, TimeUnit.HOURS);
        long hoursRemaining = TimeUtil.hoursRemainingAfterConveritngToDays(totalHours);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String days = accessor.getMessage("yukon.common.duration.days", totalDays);
        String hours = "";
        if (totalDays > 0) {
            hours = accessor.getMessage("yukon.common.duration.hours.withDays", hoursRemaining);
        } else {
            hours = accessor.getMessage("yukon.common.duration.hours", hoursRemaining);
        }
        String helpText = accessor.getMessage(baseKey + "helpText",
            days, hours, globalSettingDao.getString(GlobalSettingType.LAST_COMMUNICATION_HOURS));
        model.addAttribute("helpText", helpText);
        
        return "dr/assetAvailability/detail.jsp";
    }
    
    @GetMapping(value = "filterResults")
    public String filterResults(ModelMap model, Integer paobjectId, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        getFilteredResults(model, sorting, paging, userContext, paobjectId, deviceSubGroups,
            statuses);
        return "dr/assetAvailability/filteredResults.jsp";
    }
    
    @GetMapping(value = "filterResultsTable")
    public String filterResultsTable(ModelMap model, Integer paobjectId, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        getFilteredResults(model, sorting, paging, userContext, paobjectId, deviceSubGroups,
            statuses);
        return "dr/assetAvailability/filteredResultsTable.jsp";
    }
    
    private void getFilteredResults(ModelMap model, SortingParameters sorting, PagingParameters paging,
            YukonUserContext userContext, Integer paobjectId, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        if (statuses == null) {
            model.addAttribute("statuses", AssetAvailabilityCombinedStatus.values());
        } else {
            model.addAttribute("statuses", statuses);
        }
        model.addAttribute("deviceSubGroups", deviceSubGroups);
        model.addAttribute("paobjectId", paobjectId);
        AssetAvailabilitySortBy sortBy = AssetAvailabilitySortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (AssetAvailabilitySortBy column : AssetAvailabilitySortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        PaoIdentifier paoIdentifier = cache.getAllPaosMap().get(paobjectId).getPaoIdentifier();

        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        SearchResults<AssetAvailabilityDetails> searchResults = assetAvailabilityService.getAssetAvailabilityDetails(
            subGroups, paoIdentifier, paging, statuses, sorting, userContext);

        model.addAttribute("searchResults", searchResults);
        
        /* Build temporary collection for Collection Actions */
        List<SimpleDevice> devices = new ArrayList<>();
        searchResults.getResultList().stream().filter(assetAvailabilityDetail -> assetAvailabilityDetail.getDeviceId() != 0)
                                              .forEach(assetAvailabilityDetail -> devices.add(deviceDao.getYukonDevice(assetAvailabilityDetail.getDeviceId())));
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
        model.addAttribute("deviceCollection", deviceCollection);
        
        /* PAO Notes */
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(devices.stream()
                                                                            .map(device -> device.getPaoIdentifier().getPaoId())
                                                                            .collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);
        
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

    @GetMapping("/{paobjectId}/downloadAll")
    public void downloadAssetAvailability(HttpServletResponse response, YukonUserContext userContext,
            @PathVariable int paobjectId) throws IOException {

        LiteYukonPAObject liteYukonPAObject = cache.getAllPaosMap().get(paobjectId);
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(liteYukonPAObject, userContext, null, null);

        String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()),
            DateFormatEnum.BOTH, userContext);
        String fileName = liteYukonPAObject.getPaoName() + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
    }

    @GetMapping("/downloadFilteredResults")
    public void downloadFilteredAssetAvailability(Integer paobjectId, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, HttpServletResponse response, YukonUserContext userContext)
            throws IOException {
        LiteYukonPAObject liteYukonPAObject = cache.getAllPaosMap().get(paobjectId);
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows =
            getDownloadDataRows(liteYukonPAObject, userContext, retrieveSubGroups(deviceSubGroups), statuses);

        String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()),
            DateFormatEnum.BOTH, userContext);
        String fileName = liteYukonPAObject.getPaoName() + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
    }

    private String[] getDownloadHeaderRow(YukonUserContext userContext) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[5];
        headerRow[0] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.SERIAL_NUM);
        headerRow[1] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.TYPE);
        headerRow[2] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.LAST_COMM);
        headerRow[3] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.LAST_RUN);
        headerRow[4] = messageSourceAccessor.getMessage(baseKey + "AVAILABILITY");

        return headerRow;
    }

    private List<String[]> getDownloadDataRows(LiteYukonPAObject liteYukonPAObject, YukonUserContext userContext,
            List<DeviceGroup> subGroups, AssetAvailabilityCombinedStatus[] statuses) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        SearchResults<AssetAvailabilityDetails> results = assetAvailabilityService.getAssetAvailabilityDetails(
            subGroups, liteYukonPAObject.getPaoIdentifier(), PagingParameters.EVERYTHING, statuses, null, userContext);

        List<String[]> dataRows = Lists.newArrayList();

        results.getResultList().forEach(details -> {
            String[] dataRow = new String[5];

            dataRow[0] = details.getSerialNumber();
            dataRow[1] = details.getType().toString();
            dataRow[2] = (details.getLastComm() == null) ? "" : dateFormattingService.format(details.getLastComm(), DateFormatEnum.BOTH, userContext);
            dataRow[3] = (details.getLastRun() == null) ? "" : dateFormattingService.format(details.getLastRun(), DateFormatEnum.BOTH, userContext);
            dataRow[4] = messageSourceAccessor.getMessage(details.getAvailability());
            dataRows.add(dataRow);
        });

        return dataRows;
    }

    public enum AssetAvailabilitySortBy implements DisplayableEnum {

        SERIAL_NUM, 
        TYPE, 
        LAST_COMM, 
        LAST_RUN;

        @Override
        public String getFormatKey() {
            return baseKey + name();

        }
    }

}
