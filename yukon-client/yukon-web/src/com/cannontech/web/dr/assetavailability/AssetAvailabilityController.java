package com.cannontech.web.dr.assetavailability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.dao.AssetAvailabilityDao.SortBy;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.widgets.model.AssetAvailabilityWidgetSummary;
import com.cannontech.web.common.widgets.service.AssetAvailabilityWidgetService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.support.SiteMapPage;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/assetAvailability/*")
@CheckRoleProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY)
public class AssetAvailabilityController {

    private final static String widgetKey = "yukon.web.widgets.";
    private final static String baseKey = "yukon.web.modules.dr.assetAvailability.detail.";
    private final static String menuKey = "yukon.web.menu.";
    
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AssetAvailabilityWidgetService assetAvailabilityWidgetService;
    @Autowired private IDatabaseCache cache;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;

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
        json.put("nextRefreshTime", nextRun);
        json.put("refreshMillis", assetAvailabilityWidgetService.getRefreshMilliseconds());
        json.put("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));

        return json;
    }
    
    @GetMapping(value = "detail")
    public String detail(ModelMap model, Integer paobjectId, Integer[] selectedGatewayIds, AssetAvailabilityCombinedStatus[] statuses,
            YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        Instant lastUpdateTime = new Instant();
        AssetAvailabilityWidgetSummary summary = assetAvailabilityWidgetService.getAssetAvailabilitySummary(
            paobjectId, lastUpdateTime);
        model.addAttribute("selectedGateways", selectedGatewayIds);
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
        
        PaoIdentifier paoIdentifier = cache.getAllPaosMap().get(paobjectId).getPaoIdentifier();
        List<RfnGateway> gateways = assetAvailabilityService.getRfnGatewayList(paoIdentifier);
        model.addAttribute("gateways", gateways);
        
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
        setPaoTypeAndBreadcumbUri(paobjectId, model, accessor);
        model.addAttribute("home", accessor.getMessage(menuKey + "home"));
        model.addAttribute("dr", accessor.getMessage(menuKey + "config.dr.home"));
        model.addAttribute("assetAvailability", accessor.getMessage(baseKey + "crumbTitle"));
        return "dr/assetAvailability/detail.jsp";
    }
    
    @GetMapping(value = "collectionAction")
    public String collectionAction(String actionUrl, Integer paobjectId, Integer[] selectedGatewayIds, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, YukonUserContext userContext) {
        PaoIdentifier paoIdentifier = cache.getAllPaosMap().get(paobjectId).getPaoIdentifier();
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        SearchResults<AssetAvailabilityDetails> searchResults = assetAvailabilityService.getAssetAvailabilityDetails(
            subGroups, paoIdentifier, PagingParameters.EVERYTHING, statuses, selectedGatewayIds,
            AssetAvailabilitySortBy.SERIAL_NUM.getValue(), Direction.asc, userContext);
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        List<Integer> deviceIds = searchResults.getResultList().stream().filter(assetAvailabilityDetail -> assetAvailabilityDetail.getDeviceId() != 0)
                                                                        .map(AssetAvailabilityDetails::getDeviceId)
                                                                        .collect(Collectors.toList());
        List<SimpleDevice> devices = deviceDao.getYukonDeviceObjectByIds(deviceIds);
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        return "redirect:" + actionUrl + "?collectionType=group&group.name=" + tempGroup.getFullName();
    }

    private void setPaoTypeAndBreadcumbUri(Integer paoId, ModelMap model, MessageSourceAccessor accessor) {
        YukonPao yukonPao = cache.getAllPaosMap().get(paoId);
        PaoType paoType = yukonPao.getPaoIdentifier().getPaoType();
        String paoDetailsUri = paoDetailUrlHelper.getUrlForPaoDetailPage(yukonPao);
        String paoTypeName = StringUtils.EMPTY;
        String paoListUri = StringUtils.EMPTY;
        switch (paoType.getPaoClass()) {
        case LOADMANAGEMENT:
            if (PaoType.LM_SCENARIO == paoType) {
                paoTypeName = accessor.getMessage(menuKey + "config.dr.home.scenarios");
                paoListUri = SiteMapPage.SCENARIOS.getLink();
            } else if (PaoType.LM_CONTROL_AREA == paoType) {
                paoTypeName = accessor.getMessage(menuKey + "config.dr.home.controlareas");
                paoListUri = SiteMapPage.CONTROL_AREAS.getLink();
            } else if (paoType.isLmProgram()) {
                paoTypeName = accessor.getMessage(menuKey + "config.dr.home.programs");
                paoListUri = SiteMapPage.PROGRAMS.getLink();
            }
            break;
        case GROUP:
            paoTypeName = accessor.getMessage(menuKey + "config.dr.home.loadgroups");
            paoListUri = SiteMapPage.LOAD_GROUPS.getLink();
            break;
        default:
            break;
        }
        model.addAttribute("paoTypeName", paoTypeName);
        model.addAttribute("paoListUri", paoListUri);
        model.addAttribute("paoDetailsUri", paoDetailsUri);
    }

    @GetMapping(value = "filterResults")
    public String filterResults(ModelMap model, Integer paobjectId, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, Integer[] selectedGateways, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        getFilteredResults(model, sorting, paging, userContext, paobjectId, selectedGateways, deviceSubGroups,
            statuses);
        return "dr/assetAvailability/filteredResults.jsp";
    }
    
    @GetMapping(value = "filterResultsTable")
    public String filterResultsTable(ModelMap model, Integer paobjectId,  String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, Integer[] selectedGateways, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "SERIAL_NUM") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        getFilteredResults(model, sorting, paging, userContext, paobjectId, selectedGateways, deviceSubGroups,
            statuses);
        return "dr/assetAvailability/filteredResultsTable.jsp";
    }
    
    private void getFilteredResults(ModelMap model, SortingParameters sorting, PagingParameters paging,
            YukonUserContext userContext, Integer paobjectId, Integer[] selectedGatewayIds, String[] deviceSubGroups,
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
        SortBy sortByValue = AssetAvailabilitySortBy.valueOf(sorting.getSort()).getValue();
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        SearchResults<AssetAvailabilityDetails> searchResults = assetAvailabilityService.getAssetAvailabilityDetails(
            subGroups, paoIdentifier, paging, statuses, selectedGatewayIds, sortByValue, sorting.getDirection(), userContext);
        model.addAttribute("selectedGateways", selectedGatewayIds);
        model.addAttribute("searchResults", searchResults);
        
        /* PAO Notes */
        List<Integer> paoIds = searchResults.getResultList().stream().filter(assetAvailabilityDetail -> assetAvailabilityDetail.getDeviceId() != 0)
                                                                     .map(AssetAvailabilityDetails::getDeviceId)
                                                                     .collect(Collectors.toList());
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(paoIds);
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
        List<String[]> dataRows = getDownloadDataRows(liteYukonPAObject, userContext, null, null, null);

        writeToCSV(headerRow, dataRows, liteYukonPAObject.getPaoName(), response, userContext);
    }

    @GetMapping("/downloadFilteredResults")
    public void downloadFilteredAssetAvailability(Integer paobjectId, String[] deviceSubGroups, Integer[] selectedGatewayIds,
            AssetAvailabilityCombinedStatus[] statuses, HttpServletResponse response, YukonUserContext userContext)
            throws IOException {
        LiteYukonPAObject liteYukonPAObject = cache.getAllPaosMap().get(paobjectId);
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows =
            getDownloadDataRows(liteYukonPAObject, userContext, retrieveSubGroups(deviceSubGroups), statuses, selectedGatewayIds);

        writeToCSV(headerRow, dataRows, liteYukonPAObject.getPaoName(), response, userContext);
    }

    @GetMapping("/inventoryAction")
    public String inventoryAction(ModelMap model, Integer paobjectId, Integer[] selectedGatewayIds, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, YukonUserContext userContext) {
        PaoIdentifier paoIdentifier = cache.getAllPaosMap().get(paobjectId).getPaoIdentifier();
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        SearchResults<AssetAvailabilityDetails> searchResults = assetAvailabilityService.getAssetAvailabilityDetails(
            subGroups, paoIdentifier, PagingParameters.EVERYTHING, statuses, selectedGatewayIds,
            AssetAvailabilitySortBy.SERIAL_NUM.getValue(), Direction.asc, userContext);

        List<InventoryIdentifier> inventoryIdentifieres = searchResults.getResultList().stream().map(
            assetAvailabilityDetails -> new InventoryIdentifier(assetAvailabilityDetails.getInventoryId(),
                assetAvailabilityDetails.getType())).collect(Collectors.toList());
        String description =
            messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(baseKey + "description");
        InventoryCollection temporaryCollection =
            memoryCollectionProducer.createCollection(inventoryIdentifieres.iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());

        return "redirect:/stars/operator/inventory/inventoryActions";
    }

    private String[] getDownloadHeaderRow(YukonUserContext userContext) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[6];
        headerRow[0] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.SERIAL_NUM);
        headerRow[1] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.TYPE);
        headerRow[2] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.GATEWAY_NAME);
        headerRow[3] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.LAST_COMM);
        headerRow[4] = messageSourceAccessor.getMessage(AssetAvailabilitySortBy.LAST_RUN);
        headerRow[5] = messageSourceAccessor.getMessage(baseKey + "AVAILABILITY");

        return headerRow;
    }

    private List<String[]> getDownloadDataRows(LiteYukonPAObject liteYukonPAObject, YukonUserContext userContext,
            List<DeviceGroup> subGroups, AssetAvailabilityCombinedStatus[] statuses, Integer[] selectedGatewayIds) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        SearchResults<AssetAvailabilityDetails> results = assetAvailabilityService.getAssetAvailabilityDetails(
            subGroups, liteYukonPAObject.getPaoIdentifier(), PagingParameters.EVERYTHING, statuses, selectedGatewayIds, null, Direction.asc, userContext);

        List<String[]> dataRows = Lists.newArrayList();

        results.getResultList().forEach(details -> {
            String[] dataRow = new String[6];

            dataRow[0] = details.getSerialNumber();
            dataRow[1] = details.getType().toString();
            dataRow[2] = details.getGatewayName();
            dataRow[3] = (details.getLastComm() == null) ? "" : dateFormattingService.format(details.getLastComm(), DateFormatEnum.BOTH, userContext);
            dataRow[4] = (details.getLastRun() == null) ? "" : dateFormattingService.format(details.getLastRun(), DateFormatEnum.BOTH, userContext);
            dataRow[5] = messageSourceAccessor.getMessage(details.getAvailability());
            dataRows.add(dataRow);
        });

        return dataRows;
    }

    private void writeToCSV(String[] headerRow, List<String[]> dataRows, String paoName, HttpServletResponse response,
            YukonUserContext userContext) throws IOException {
        String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()),
            DateFormatEnum.FILE_TIMESTAMP, userContext);
        String fileName = paoName + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
    }

    public enum AssetAvailabilitySortBy implements DisplayableEnum {

        SERIAL_NUM(SortBy.SERIALNUM),
        TYPE(SortBy.TYPE),
        LAST_COMM(SortBy.LASTCOMM),
        LAST_RUN(SortBy.LASTRUN),
        GATEWAY_NAME(SortBy.GATEWAYNAME);

        private final SortBy value;

        private AssetAvailabilitySortBy(SortBy value) {
            this.value = value;
        }

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return baseKey + name();

        }
    }

}
