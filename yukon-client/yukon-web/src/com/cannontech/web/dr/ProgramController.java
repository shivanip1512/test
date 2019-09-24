package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.disconnect.model.DrDisconnectStatusCallback;
import com.cannontech.amr.disconnect.model.DrDisconnectStatusCallback.ControlOperation;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.program.widget.model.ProgramData;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteHardwarePAObject;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.DrMeterEventStatus;
import com.cannontech.dr.meterDisconnect.MeterDisconnectMessageListener.MeterCollection;
import com.cannontech.dr.meterDisconnect.service.DrMeterDisconnectStatusService;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.program.model.ProgramState;
import com.cannontech.dr.program.service.DisconnectStatusService;
import com.cannontech.dr.program.service.ProgramWidgetService;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.dr.LoadGroupHelper.LoadGroupFilter;
import com.cannontech.web.dr.ProgramsHelper.ProgramFilter;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class ProgramController extends ProgramControllerBase {
    
    private static final Logger log = YukonLogManager.getLogger(ProgramController.class);

    @Autowired private AssetAvailabilityPingService assetAvailabilityPingService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private LoadGroupHelper loadGroupHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ScenarioService scenarioService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private MeterDao meterDao;
    @Autowired private DisconnectService disconnectService;
    @Autowired private ProgramWidgetService programWidgetService;
    @Autowired private DisconnectStatusService disconnectStatusService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private DrMeterDisconnectStatusService meterDisconnectService;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private IDatabaseCache dbCache;

    @RequestMapping(value = "/program/list", method = RequestMethod.GET)
    public String list(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("filter") ProgramFilter filter, BindingResult bindingResult,
            @DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir=Direction.asc, sort="NAME") SortingParameters sorting,
            FlashScope flashScope) {
        
        programsHelper.filterPrograms(model, userContext, filter, bindingResult, null, sorting, paging);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        programsHelper.addColumns(model, accessor, sorting);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/program/list.jsp";
    }

    @RequestMapping(value = "/program/detail", method = RequestMethod.GET)
    public String detail(int programId, ModelMap model, LiteYukonUser user,
            @ModelAttribute("filter") LoadGroupFilter filter,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope,
            @DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir=Direction.asc, sort="NAME") SortingParameters sorting) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(user, program, Permission.LM_VISIBLE);
        model.addAttribute("program", program);

        boolean changeGearAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHANGE_GEARS,
                userContext.getYukonUser());
        model.addAttribute("changeGearAllowed", changeGearAllowed);
        boolean enableDisableProgramsAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_DISABLE_PROGRAM,
                userContext.getYukonUser());
        model.addAttribute("enableDisableProgramsAllowed", enableDisableProgramsAllowed);
        
        UiFilter<DisplayablePao> detailFilter = new LoadGroupsForProgramFilter(programId);
        loadGroupHelper.filterGroups(model, userContext, filter,
                                               bindingResult, detailFilter, flashScope,
                                               paging, sorting);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        loadGroupHelper.addColumns(model, accessor, sorting);

        DisplayablePao parentControlArea =
            controlAreaService.findControlAreaForProgram(userContext, programId);
        model.addAttribute("parentControlArea", parentControlArea);
        List<Scenario> parentScenarios = scenarioDao.findScenariosForProgram(programId);
        model.addAttribute("parentScenarios", parentScenarios);
        
        return "dr/program/detail.jsp";
    }

    @GetMapping("/program/activityDetails")
    public String activityDetails(ModelMap model, YukonUserContext userContext) {
        Map<String, List<ProgramData>> programsData = programWidgetService.buildProgramDetailsData(userContext);
        model.addAttribute("programsData", programsData);

        return "dr/program/activityDetails.jsp";
    }

    @RequestMapping("/program/assetAvailability")
    public String assetAvailability(ModelMap model, YukonUserContext userContext, int paoId) {
        model.addAttribute("paoId", paoId);
        DisplayablePao program = programService.getProgram(paoId);
        if(rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser())) {
            getAssetAvailabilityInfo(program, model, userContext);
        }
        return "dr/assetAvailability.jsp";
    }
    
    @GetMapping("/program/disconnectStatus")
    public String disconnectStatus(ModelMap model, int programId, String[] disconnectStatus, 
                                   @DefaultSort(dir=Direction.asc, sort="device") SortingParameters sorting, 
                                   @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {
        DisplayablePao program = programService.getProgram(programId);
        model.addAttribute("program", program);
        model.addAttribute("programId", programId);
        
        //get opted out devices
        List<LoadGroup> loadGroups = loadGroupDao.getByProgramId(programId);
        List<Integer> loadGroupIds = new ArrayList<>();
        loadGroups.forEach(group -> loadGroupIds.add(group.getLoadGroupId()));
        Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(loadGroupIds);
        model.addAttribute("optedOutDevices", optOutInventory);

        getDisconnectStatusResults(sorting, paging, programId, disconnectStatus, userContext, model);

        return "dr/disconnectStatus/detail.jsp";
    }
    
    @GetMapping("/program/disconnectStatusTable")
    public String disconnectStatusTable(ModelMap model, int programId, String[] disconnectStatus, 
                                   @DefaultSort(dir=Direction.asc, sort="device") SortingParameters sorting, 
                                   @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {
        getDisconnectStatusResults(sorting, paging, programId, disconnectStatus, userContext, model);
        return "dr/disconnectStatus/filteredResultsTable.jsp";
    }
    
    private void getDisconnectStatusResults(SortingParameters sorting, PagingParameters paging, int programId, 
                                            String[] disconnectStatus, YukonUserContext userContext, ModelMap model) {
        Map<LiteHardwarePAObject, PointValueHolder> disconnectStatusMap = disconnectStatusService.getDisconnectStatuses(programId, disconnectStatus, userContext);

        //create device collection
        List<Integer> deviceIds = new ArrayList<>();
        disconnectStatusMap.keySet().forEach(device -> deviceIds.add(device.getPaoIdentifier().getPaoId()));
        DeviceCollection deviceCollection = dcProducer.createDeviceCollection(deviceIds, null);
        model.addAttribute("deviceCollection", deviceCollection);
        
        SearchResults<Map.Entry<LiteHardwarePAObject, PointValueHolder>> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, disconnectStatusMap.size());
        
        DisconnectSortBy sortBy = DisconnectSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        Comparator<Map.Entry<LiteHardwarePAObject, PointValueHolder>> comparator = (o1, o2) -> {
            return o1.getKey().getPaoName().compareTo(o2.getKey().getPaoName());
        };
        if (sortBy == DisconnectSortBy.status) {
            comparator = (o1, o2) -> {
                String formattedValue1 = pointFormattingService.getValueString(o1.getValue(), Format.VALUE, userContext);
                String formattedValue2 = pointFormattingService.getValueString(o2.getValue(), Format.VALUE, userContext);
                return formattedValue1.compareTo(formattedValue2);            
            };
        } else if (sortBy == DisconnectSortBy.timestamp) {
            comparator = (o1, o2) -> {
                return o1.getValue().getPointDataTimeStamp().compareTo(o2.getValue().getPointDataTimeStamp());         
            };
        }
        List<Map.Entry<LiteHardwarePAObject, PointValueHolder>> list =
                new LinkedList<>(disconnectStatusMap.entrySet());
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(list, comparator);
        
        list = list.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, disconnectStatusMap.size());
        searchResult.setResultList(list);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        List<SortableColumn> columns = new ArrayList<>();
        for (DisconnectSortBy column : DisconnectSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }
        
        model.addAttribute("disconnectStatusList", searchResult);

        List<String> stateValues = list.stream()
                .map(p -> pointFormattingService.getValueString(p.getValue(), Format.VALUE, userContext))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("disconnectStatuses", stateValues);
    }
    
    @GetMapping("/program/disconnectStatus/download")
    public void download(int programId, String[] disconnectStatus, YukonUserContext userContext, HttpServletResponse response) throws IOException {
        DisplayablePao program = programService.getProgram(programId);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[3];

        headerRow[0] = accessor.getMessage(DisconnectSortBy.device);
        headerRow[1] = accessor.getMessage(DisconnectSortBy.status);
        headerRow[2] = accessor.getMessage(DisconnectSortBy.timestamp);
        
        Map<LiteHardwarePAObject, PointValueHolder> disconnectStatusMap = disconnectStatusService.getDisconnectStatuses(programId, disconnectStatus, userContext);

        List<String[]> dataRows = Lists.newArrayList();
        for (Entry<LiteHardwarePAObject, PointValueHolder> status : disconnectStatusMap.entrySet()) {
            String[] dataRow = new String[3];
            LiteHardwarePAObject obj = status.getKey();
            PointValueHolder point = status.getValue();
            dataRow[0] = obj.getPaoName();
            dataRow[1] = pointFormattingService.getValueString(point, Format.VALUE, userContext);
            dataRow[2] =  dateFormattingService.format(point.getPointDataTimeStamp(), 
                                                            DateFormatEnum.BOTH, userContext);
            dataRows.add(dataRow);
        }
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "DisconnectStatus_" + program.getName() + "_" + now + ".csv");
    }
    
    @PostMapping("/disconnectStatus/change")
    public @ResponseBody Map<String, Object> changeDisconnectStatus(int deviceId, int programId, boolean connect, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        YukonMeter meter = meterDao.getForId(deviceId);
        DisconnectCommand command = connect ? DisconnectCommand.CONNECT : DisconnectCommand.DISCONNECT;
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(deviceId);
        SimpleDevice device = new SimpleDevice(pao);
        MeterCollection collection = new MeterCollection(Lists.newArrayList(device));
        Integer eventId = null;
        try {
            Optional<Integer> optEvent = meterDisconnectService.findActiveEventForDevice(deviceId);
            if (optEvent.isPresent()) {
                eventId = optEvent.get();
                SimpleCallback<CollectionActionResult> doNothingCallback = result -> {};
                DrDisconnectStatusCallback statusCallback = 
                        new DrDisconnectStatusCallback(ControlOperation.of(connect), eventId, meterDisconnectService, 
                                                       smartNotificationEventCreationService, program.getName());

                CollectionActionResult result = disconnectService.execute(command, collection, doNothingCallback, statusCallback, userContext);
                if (!result.getExecutionExceptionText().isEmpty()) {
                    json.put("errors", result.getExecutionExceptionText());
                }
                return json;
            }
        } catch (Exception e) {
            log.info("No current event found for device: " + pao.getPaoName(), e);
        }

        DisconnectMeterResult result = disconnectService.execute(command, DeviceRequestType.METER_CONNECT_DISCONNECT_WIDGET, meter,
                                                                 userContext.getYukonUser());
        addDisconnectResultToModel(json, result, accessor);

        return json;
    }
    
    private void addDisconnectResultToModel(Map<String, Object> json, DisconnectMeterResult result, MessageSourceAccessor accessor) {
        if (result.getError() != null) {
            json.put("errors", result.getError().getDescription());
        }
        if (StringUtils.isNotEmpty(result.getProcessingException())) {
            json.put("errors", result.getProcessingException());
        }
        
        json.put("success", result.isSuccess());
        json.put("status", accessor.getMessage(result.getState().getFormatKey()));
        json.put("time", result.getDisconnectTime());
    }
    
    public enum DisconnectSortBy implements DisplayableEnum {

        device,
        status,
        timestamp;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.disconnectStatus." + name();
        }
    }
    
    @GetMapping("/program/controlStatus")
    public String controlStatus(ModelMap model, int programId, DrMeterControlStatus[] controlStatuses, DrMeterControlStatus[] restoreStatuses,
                                @DefaultSort(dir=Direction.asc, sort="device") SortingParameters sorting, 
                                @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {
        DisplayablePao program = programService.getProgram(programId);
        model.addAttribute("program", program);
        model.addAttribute("programId", programId);

        model.addAttribute("controlStatuses", DrMeterControlStatus.getShedStatuses());
        model.addAttribute("restoreStatuses", DrMeterControlStatus.getRestoreStatuses());
        
        //get opted out devices
        List<LoadGroup> loadGroups = loadGroupDao.getByProgramId(programId);
        List<Integer> loadGroupIds = new ArrayList<>();
        loadGroups.forEach(group -> loadGroupIds.add(group.getLoadGroupId()));
        Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(loadGroupIds);
        model.addAttribute("optedOutDevices", optOutInventory);
        
        getControlStatusResults(sorting, paging, programId, controlStatuses, restoreStatuses, userContext, model);

        return "dr/controlStatus/detail.jsp";
    }
    
    @GetMapping("/program/controlStatusTable")
    public String controlStatusTable(ModelMap model, int programId, DrMeterControlStatus[] controlStatuses, DrMeterControlStatus[] restoreStatuses,
                                   @DefaultSort(dir=Direction.asc, sort="device") SortingParameters sorting, 
                                   @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {
        getControlStatusResults(sorting, paging, programId, controlStatuses, restoreStatuses, userContext, model);

        return "dr/controlStatus/filteredResultsTable.jsp";
    }
    
    private void getControlStatusResults(SortingParameters sorting, PagingParameters paging, int programId, DrMeterControlStatus[] controlStatuses, 
                                         DrMeterControlStatus[] restoreStatuses, YukonUserContext userContext, ModelMap model) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        List<DrMeterControlStatus> selectedControlStatuses = controlStatuses == null ? DrMeterControlStatus.getShedStatuses() : Arrays.asList(controlStatuses);
        List<DrMeterControlStatus> selectedRestoreStatuses = restoreStatuses == null ? DrMeterControlStatus.getRestoreStatuses() : Arrays.asList(restoreStatuses);

        List<DrMeterEventStatus> statuses = meterDisconnectService.getAllCurrentStatusForLatestProgramEvent(programId, selectedControlStatuses, selectedRestoreStatuses);
        
        //create device collection
        List<Integer> deviceIds = new ArrayList<>();
        statuses.forEach(device -> deviceIds.add(device.getDeviceId()));
        DeviceCollection deviceCollection = dcProducer.createDeviceCollection(deviceIds, null);
        model.addAttribute("deviceCollection", deviceCollection);
        
        SearchResults<DrMeterEventStatus> searchResult = new SearchResults<>();

        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, statuses.size());
        
        ControlStatusSortBy sortBy = ControlStatusSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        Comparator<DrMeterEventStatus> comparator = (o1, o2) -> {
            return o1.getMeterDisplayName().compareTo(o2.getMeterDisplayName());
        };
        if (sortBy == ControlStatusSortBy.controlStatus) {
            comparator = (o1, o2) -> {
                String status1 = accessor.getMessage(o1.getControlStatus().getFormatKey());
                String status2 = accessor.getMessage(o2.getControlStatus().getFormatKey());
                return status1.compareTo(status2);            
            };
        } else if (sortBy == ControlStatusSortBy.controlStatusTimestamp) {
            comparator = (o1, o2) -> {
                return o1.getControlStatusTime().compareTo(o2.getControlStatusTime());         
            };
        } else if (sortBy == ControlStatusSortBy.restoreStatus) {
            comparator = (o1, o2) -> {
                String status1 = accessor.getMessage(o1.getRestoreStatus().getFormatKey());
                String status2 = accessor.getMessage(o2.getRestoreStatus().getFormatKey());
                return status1.compareTo(status2);            
            };
        } else if (sortBy == ControlStatusSortBy.restoreStatusTimestamp) {
            comparator = (o1, o2) -> {
                return o1.getRestoreStatusTime().compareTo(o2.getRestoreStatusTime());         
            };
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(statuses, comparator);
        
        List<DrMeterEventStatus> itemList = Lists.newArrayList(statuses);

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, statuses.size());
        searchResult.setResultList(itemList);
        
        List<SortableColumn> columns = new ArrayList<>();
        for (ControlStatusSortBy column : ControlStatusSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }
        
        model.addAttribute("statuses", searchResult);
    }
    
    
    @GetMapping("/program/controlStatus/download")
    public void download(int programId, DrMeterControlStatus[] controlStatuses, DrMeterControlStatus[] restoreStatuses, 
                         YukonUserContext userContext, HttpServletResponse response) throws IOException {
        DisplayablePao program = programService.getProgram(programId);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[5];

        headerRow[0] = accessor.getMessage(ControlStatusSortBy.device);
        headerRow[1] = accessor.getMessage(ControlStatusSortBy.controlStatus);
        headerRow[2] = accessor.getMessage(ControlStatusSortBy.controlStatusTimestamp);
        headerRow[3] = accessor.getMessage(ControlStatusSortBy.restoreStatus);
        headerRow[4] = accessor.getMessage(ControlStatusSortBy.restoreStatusTimestamp);
        
        List<DrMeterControlStatus> selectedControlStatuses = controlStatuses == null ? DrMeterControlStatus.getShedStatuses() : Arrays.asList(controlStatuses);
        List<DrMeterControlStatus> selectedRestoreStatuses = restoreStatuses == null ? DrMeterControlStatus.getRestoreStatuses() : Arrays.asList(restoreStatuses);
        
        List<DrMeterEventStatus> statuses = meterDisconnectService.getAllCurrentStatusForLatestProgramEvent(programId, selectedControlStatuses, selectedRestoreStatuses);

        List<String[]> dataRows = Lists.newArrayList();
        for (DrMeterEventStatus status : statuses) {
            String[] dataRow = new String[5];
            dataRow[0] = status.getMeterDisplayName();
            dataRow[1] = accessor.getMessage(status.getControlStatus().getFormatKey());
            dataRow[2] =  dateFormattingService.format(status.getControlStatusTime(), DateFormatEnum.BOTH, userContext);
            dataRow[3] = accessor.getMessage(status.getRestoreStatus().getFormatKey());
            dataRow[4] =  dateFormattingService.format(status.getRestoreStatusTime(), DateFormatEnum.BOTH, userContext);

            dataRows.add(dataRow);
        }
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "ControlStatus_" + program.getName() + "_" + now + ".csv");
    }
    
    public enum ControlStatusSortBy implements DisplayableEnum {

        device,
        controlStatus,
        controlStatusTimestamp,
        restoreStatus,
        restoreStatusTimestamp;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.controlStatus." + name();
        }
    }

    @ResponseBody
    @RequestMapping("/program/pingDevices")
    public void pingDevices(int assetId, LiteYukonUser user) {
        DisplayablePao controlArea = programService.getProgram(assetId);
        assetAvailabilityPingService.readDevicesInDrGrouping(controlArea.getPaoIdentifier(), user);
    }

    @RequestMapping("/program/{id}/aa/download/{type}")
    public void downloadAssetAvailability(HttpServletResponse response, 
            YukonUserContext userContext, 
            @PathVariable int id, 
            @PathVariable String type) 
    throws IOException {
        
        List<AssetAvailabilityCombinedStatus> filters = getAssetAvailabilityFilters(type);
        
        downloadAssetAvailability(id, userContext, filters.toArray(new AssetAvailabilityCombinedStatus[]{}), response);
    }
    
    @RequestMapping("/program/downloadToCsv")
    public void downloadToCsv(int assetId,
            @RequestParam(value="filter[]", required=false) AssetAvailabilityCombinedStatus[] filters,
            HttpServletResponse response,
            YukonUserContext userContext) throws IOException {
        
        downloadAssetAvailability(assetId, userContext, filters, response);
    }
    
    private void downloadAssetAvailability(int assetId, 
            YukonUserContext userContext, 
            AssetAvailabilityCombinedStatus[] filters, 
            HttpServletResponse response) throws IOException {
        
        DisplayablePao program = programService.getProgram(assetId);
        
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(program, filters, userContext);
        
        String dateStr = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        String fileName = "Program_" + program.getName() + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
    }

    @RequestMapping("/program/getChangeGearValue")
    public String getChangeGearValue(ModelMap modelMap, int programId, YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        addGearsToModel(program, modelMap);
        modelMap.addAttribute("program", program);

        return "dr/program/getChangeGearValue.jsp";
    }
    
    @RequestMapping("/program/changeGear")
    public @ResponseBody Map<String, String> changeGear(int programId, int gearNumber, YukonUserContext userContext,
                                                        FlashScope flashScope) {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.changeGear(programId, gearNumber, ProgramOriginSource.MANUAL);
        
        demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.getChangeGearValue.gearChanged"));

        return Collections.singletonMap("action", "reload");
    }
    
    @RequestMapping("/program/changeGearMultiplePopup")
    public String changeGearMultiplePopup(ModelMap model, @ModelAttribute("filter") ChangeMultipleGearsBackingBean filter,
                                          BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
        UiFilter<DisplayablePao> uifilter = null;

        String paoName = null;
        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (filter.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(filter.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
            paoName = controlArea.getName();
            uifilter = new ForControlAreaFilter(filter.getControlAreaId());
        }
        if (filter.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(filter.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
            paoName = scenario.getName();
            uifilter = new ForScenarioFilter(filter.getScenarioId());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(filter.getScenarioId());
            model.addAttribute("scenarioPrograms", scenarioPrograms);
        }

        if (uifilter == null) {
            throw new IllegalArgumentException();
        }

        SearchResults<DisplayablePao> searchResult =
            programService.filterPrograms(uifilter, new DisplayablePaoComparator(),
                                          0, Integer.MAX_VALUE, userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {

            model.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.startMultiplePrograms.noPrograms." +
                                                 (filter.getControlAreaId() != null ? "controlArea" : "scenario"),
                                                 paoName);
            model.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        model.addAttribute("programs", programs);

        List<ProgramGearChangeInfo> programGearChangeInfo = new ArrayList<>(programs.size());
        
        for (DisplayablePao program : programs) {
            int programId = program.getPaoIdentifier().getPaoId();
            int gearNumber = 1;
            if (scenarioPrograms != null) {
                ScenarioProgram scenarioProgram = scenarioPrograms.get(programId);
                if (scenarioProgram != null) {
                    gearNumber = scenarioProgram.getStartGear();
                }
            }
            programGearChangeInfo.add(new ProgramGearChangeInfo(programId, gearNumber, true));
        }
        filter.setProgramGearChangeInfo(programGearChangeInfo);
        
        addGearsToModel(searchResult.getResultList(), model);
        addErrorsToFlashScopeIfNecessary(bindingResult, flashScope);
        
        Map<Integer, Map<Integer, Boolean>> programIndexTargetGearMap = getIndexBasedIsTargetGearMap(programs);

        model.addAttribute("targetGearMap",programIndexTargetGearMap);
      return "dr/program/changeMultipleProgramsGearsDetails.jsp";
  }
    
    @RequestMapping("/program/changeMultipleGears")
    public @ResponseBody Map<String, String> changeMultipleGears(ModelMap model,
                @ModelAttribute("filter") ChangeMultipleGearsBackingBean filter, BindingResult bindingResult,
                YukonUserContext userContext, FlashScope flashScope) {

        if (filter.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(filter.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
        }
        if (filter.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(filter.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
        }
        boolean gearChanged = false;
        for (ProgramGearChangeInfo gearChangeInfo : filter.getProgramGearChangeInfo()) {
            if (gearChangeInfo.isChangeGear()) {
                gearChanged = true;
                DisplayablePao program = programService.getProgram(gearChangeInfo.getProgramId());
    
                LiteYukonUser yukonUser = userContext.getYukonUser();
                programService.changeGear(gearChangeInfo.getProgramId(), gearChangeInfo.getGearNumber(), ProgramOriginSource.MANUAL);
                demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());

            }
        }
        if (gearChanged) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.getChangeGearValue.multipleGearChanged"));
        }

        return Collections.singletonMap("action", "reload");
    }
    
    @RequestMapping("/program/sendEnableConfirm")
    public String sendEnableConfirm(ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("program", program);
        modelMap.addAttribute("isEnabled", isEnabled);
        return "dr/program/sendEnableConfirm.jsp";
    }
    
    @RequestMapping("/program/setEnabled")
    public @ResponseBody Map<String, String> setEnabled(int programId, boolean isEnabled, YukonUserContext userContext,
            Boolean suppressRestoration, FlashScope flashScope) {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        if (!isEnabled && (suppressRestoration != null && suppressRestoration)) {
            programService.disableAndSupressRestoration(programId);
        } else {
            programService.setEnabled(programId, isEnabled);
        }

        if(isEnabled) {
            demandResponseEventLogService.threeTierProgramEnabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.enabled"));
        } else {
            demandResponseEventLogService.threeTierProgramDisabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.disabled"));
        }
        
        return Collections.singletonMap("action", "reload");
    }
    
    @RequestMapping("/program/sendEnableDisableProgramsConfirm")
    public String sendEnableDisableProgramsConfirm(ModelMap modelMap, YukonUserContext userContext,
                                             Integer controlAreaId, Integer scenarioId, boolean enable) {
        
        modelMap.addAttribute("enable", enable);
        
        //Determine if parent object is a control area or scenario
        DisplayablePao parent = null;
        UiFilter<DisplayablePao> uifilter = null;
        if(controlAreaId != null) {
            parent = controlAreaService.getControlArea(controlAreaId);
            uifilter = new ForControlAreaFilter(controlAreaId);
        } else if(scenarioId != null){
            parent = scenarioService.getScenario(scenarioId);
            uifilter = new ForScenarioFilter(scenarioId);
        } else {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.invalidParent");
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("parent", parent);
        
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     parent,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);
        //Get programs
        SearchResults<DisplayablePao> searchResult = programService.filterPrograms(uifilter, 
                                                                                  new DisplayablePaoComparator(),
                                                                                  0, 
                                                                                  Integer.MAX_VALUE, 
                                                                                  userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.noPrograms." +
                                                (controlAreaId != null ? "controlArea" : "scenario"));
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("programs", programs);
        
        //get current gears and statuses
        List<String> gears = Lists.newArrayListWithCapacity(programs.size());
        List<ProgramState> states = Lists.newArrayListWithCapacity(programs.size());
        for(DisplayablePao program : programs) {
            DatedObject<LMProgramBase> datedObject = programService.findDatedProgram(program.getPaoIdentifier().getPaoId());
            LMProgramBase programBase = datedObject.getObject();
            LMProgramDirectGear gear = null;
            
            ProgramState state = programBase.getProgramState();
            states.add(state);
            
            if (programBase instanceof IGearProgram) {
                gear = ((IGearProgram) programBase).getCurrentGear();
            }
            if (gear != null) {
                gears.add(gear.getGearName());
            } else {
                MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
                String dashes = messageSourceAccessor.getMessage("");
                gears.add(dashes);
            }
        }
        modelMap.addAttribute("states", states);
        modelMap.addAttribute("gears", gears);
        
        return "dr/program/sendEnableDisableProgramsConfirm.jsp";
    }
    
    @RequestMapping("/program/enableDisablePrograms")
    public @ResponseBody Map<String, String> enableDisablePrograms(HttpServletRequest request, FlashScope flashScope,
                Boolean supressRestoration, boolean enable) {
        
        String[] programIds = request.getParameterValues("disableProgram");
        
        if(programIds == null) {
            YukonMessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.noProgramsSelected");
            flashScope.setError(message);

            return Collections.singletonMap("action", "reload");
        }
        
        for(String programIdString : programIds) {
            int programId = Integer.parseInt(programIdString);
            
            if(!enable && (supressRestoration != null && supressRestoration)) {
                programService.disableAndSupressRestoration(programId);
            } else {
                programService.setEnabled(programId, enable);
            }
        }
        
        YukonMessageSourceResolvable message;
        if(enable) {
            message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableProgramsConfirm.programsEnabledConfirmation", programIds.length);
        } else {
            message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.programsDisabledConfirmation", programIds.length);
        }
        flashScope.setConfirm(message);
        
        return Collections.singletonMap("action", "reload");
    }
    
    private void addFilterErrorsToFlashScopeIfNecessary(ModelMap model,
            BindingResult bindingResult, FlashScope flashScope) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hasFilterErrors", true);
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programsHelper.initBinder(binder, userContext, "programList");
        loadGroupHelper.initBinder(binder, userContext);
    }
}
