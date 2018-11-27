package com.cannontech.web.dr.assetavailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.widgets.model.AssetAvailabilityWidgetSummary;
import com.cannontech.web.common.widgets.service.AssetAvailabilityWidgetService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/assetAvailability/*")
@CheckRoleProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY)
public class AssetAvailabilityController {

    private final static String widgetKey = "yukon.web.widgets.";
    private final static String baseKey = "yukon.web.modules.dr.assetAvailability.detail.";
    
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AssetAvailabilityWidgetService assetAvailabilityWidgetService;
    @Autowired private IDatabaseCache cache;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private PaoNotesService paoNotesService;

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
    public String detail(ModelMap model, Integer controlAreaOrProgramOrScenarioId, String[] deviceSubGroups,
            AssetAvailabilityCombinedStatus[] statuses, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "deviceName") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) throws Exception {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Instant lastUpdateTime = new Instant();
        AssetAvailabilityWidgetSummary summary = assetAvailabilityWidgetService.getAssetAvailabilitySummary(
            controlAreaOrProgramOrScenarioId, lastUpdateTime);
        model.addAttribute("summary", summary);
        model.addAttribute("statusTypes", AssetAvailabilityCombinedStatus.values());
        model.addAttribute("paoName", cache.getAllPaosMap().get(controlAreaOrProgramOrScenarioId).getPaoName());
        model.addAttribute("totalDevices", summary.getTotalDeviceCount());
        if (statuses == null) {
            model.addAttribute("statuses", AssetAvailabilityCombinedStatus.values());
        } else {
            model.addAttribute("statuses", statuses);
        }
        model.addAttribute("deviceSubGroups", deviceSubGroups);
        model.addAttribute("controlAreaOrProgramOrScenarioId", controlAreaOrProgramOrScenarioId);
        AssetAvailabilitySortBy sortBy = AssetAvailabilitySortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (AssetAvailabilitySortBy column : AssetAvailabilitySortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        /* Filter Results */
        //TODO: Replace call to mockSearchResults() later with service call once YUK-18954 is done.
        SearchResults<AssetAvailabilityDetails> searchResults = mockSearchResults();
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
        return "dr/assetAvailabilityDetails.jsp";
    }
    
    //TODO: Remove this once YUK-18954 is done..
    private SearchResults<AssetAvailabilityDetails> mockSearchResults() {

        SearchResults<AssetAvailabilityDetails> results = SearchResults.emptyResult();

        List<AssetAvailabilityDetails> list = Lists.newArrayList();
        AssetAvailabilityDetails aad = new AssetAvailabilityDetails();
        aad.setAvailability(AssetAvailabilityCombinedStatus.ACTIVE);
        aad.setLastComm(new Instant());
        aad.setLastRun(new Instant());
        aad.setSerialNumber("1123421");
        aad.setType(HardwareType.COMMERCIAL_EXPRESSSTAT);
        aad.setInventoryId(6993);
        aad.setDeviceId(128884);
        list.add(aad);

        AssetAvailabilityDetails aad1 = new AssetAvailabilityDetails();
        aad1.setAvailability(AssetAvailabilityCombinedStatus.ACTIVE);
        aad1.setLastComm(new Instant());
        aad1.setLastRun(new Instant());
        aad1.setSerialNumber("777");
        aad1.setType(HardwareType.ECOBEE_SMART);
        aad1.setInventoryId(6992);
        aad1.setDeviceId(128885);
        list.add(aad1);
        
        AssetAvailabilityDetails aad2 = new AssetAvailabilityDetails();
        aad2.setAvailability(AssetAvailabilityCombinedStatus.OPTED_OUT);
        aad2.setLastComm(new Instant());
        aad2.setLastRun(new Instant());
        aad2.setSerialNumber("76477");
        aad2.setType(HardwareType.HONEYWELL_FOCUSPRO);
        aad2.setInventoryId(63);
        aad2.setDeviceId(34736);
        list.add(aad2);
        
        AssetAvailabilityDetails aad3 = new AssetAvailabilityDetails();
        aad3.setAvailability(AssetAvailabilityCombinedStatus.UNAVAILABLE);
        aad3.setLastComm(new Instant());
        aad3.setLastRun(new Instant());
        aad3.setSerialNumber("6666");
        aad3.setType(HardwareType.LCR_5000_VERSACOM);
        aad3.setInventoryId(362);
        aad3.setDeviceId(0);
        list.add(aad3);
        
        AssetAvailabilityDetails aad4 = new AssetAvailabilityDetails();
        aad4.setAvailability(AssetAvailabilityCombinedStatus.UNAVAILABLE);
        aad4.setLastComm(new Instant());
        aad4.setLastRun(new Instant());
        aad4.setSerialNumber("54533");
        aad4.setType(HardwareType.EXPRESSSTAT);
        aad4.setInventoryId(394);
        aad4.setDeviceId(0);
        list.add(aad4);
        
        results.setResultList(list);
        results.setBounds(0, 5, 5);
        results.setHitCount(5);
        return results;
    }

    //TODO: Update this enum once YUK-18954 is done.
    public enum AssetAvailabilitySortBy implements DisplayableEnum {

        /*deviceName(SortBy.PAO_NAME),
        deviceType(SortBy.PAO_TYPE),
        noteText(SortBy.NOTE_TEXT),
        createdBy(SortBy.CREATE_USERNAME),
        createDate(SortBy.CREATE_DATE),
        editedBy(SortBy.EDIT_USERNAME),
        editDate(SortBy.EDIT_DATE);

        private final SortBy value;

        private PaoNoteSortBy(SortBy value) {
            this.value = value;
        }

        public SortBy getValue() {
            return value;
        }*/
        
        deviceName,
        serialNumber,
        deviceType,
        lastCommunication,
        lastRun;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }

}
