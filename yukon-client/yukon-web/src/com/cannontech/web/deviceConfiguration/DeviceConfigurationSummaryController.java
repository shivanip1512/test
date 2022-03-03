package com.cannontech.web.deviceConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
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
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao.SortBy;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao.StateSelection;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;
import com.cannontech.web.util.WebFileUtils;

@Controller
@RequestMapping("/summary/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationSummaryController {
    
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceConfigSummaryDao deviceConfigSummaryDao;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private ServerDatabaseCache dbCache;
    ExecutorService executor = Executors.newCachedThreadPool();
    
    private final static String baseKey = "yukon.web.modules.tools.configs.summary.";
    private final static int DEVICE_CONFIG_ASSIGNED_TO_ANY = -998;
    private final static int DEVICE_CONFIG_UNASSIGNED = -999;

    @GetMapping("view")
    public String view(ModelMap model, @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                       @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {
        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllConfigsWithDeviceConfigStateEntry();
        model.addAttribute("configurations", configurations);
        model.addAttribute("states", StateSelection.values());
        DeviceConfigSummaryFilter filter = new DeviceConfigSummaryFilter();
        //defaults are All Configurations and All Statuses
        filter.setConfigurationIds(new ArrayList<>(Arrays.asList(DEVICE_CONFIG_ASSIGNED_TO_ANY)));
        filter.setStateSelection(StateSelection.ALL);
        setFilterValues(filter, null);
        prepareModel(model, filter, sorting, paging, userContext);
        //setFilterValues changes this to an array of all configurations, so we need to switch it back to All for the UI
        filter.setConfigurationIds(new ArrayList<>(Arrays.asList(DEVICE_CONFIG_ASSIGNED_TO_ANY)));
        model.addAttribute("filter", filter);
        return "summary/summary.jsp";
    }
    
    @GetMapping("filter")
    public String filter(@DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, @DefaultItemsPerPage(value=250) PagingParameters paging,
                       ModelMap model, @ModelAttribute DeviceConfigSummaryFilter filter, String[] deviceSubGroups, YukonUserContext userContext) {
        boolean assignedToAny = filter.getConfigurationIds() != null && filter.getConfigurationIds().contains(DEVICE_CONFIG_ASSIGNED_TO_ANY);
        boolean unassigned = filter.getConfigurationIds() != null && filter.getConfigurationIds().contains(DEVICE_CONFIG_UNASSIGNED);
        setFilterValues(filter, deviceSubGroups);
        prepareModel(model, filter, sorting, paging, userContext);
        model.addAttribute("deviceSubGroups", deviceSubGroups);
        if (assignedToAny) {
            filter.setConfigurationIds(new ArrayList<>(Arrays.asList(DEVICE_CONFIG_ASSIGNED_TO_ANY)));
        }
        if (unassigned) {
            filter.getConfigurationIds().add(DEVICE_CONFIG_UNASSIGNED);
        }
        model.addAttribute("filter", filter);
        return "summary/resultsTable.jsp";
    }
    
    private void prepareModel(ModelMap model, DeviceConfigSummaryFilter filter, SortingParameters sorting, PagingParameters paging, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (DetailSortBy column : DetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        SearchResults<DeviceConfigSummaryDetail> results = deviceConfigSummaryDao.getSummary(filter, paging, sortBy.value, dir);
        model.addAttribute("results",  results);
    }
    
    @GetMapping("{id}/viewHistory")
    public String viewHistory(ModelMap model, @PathVariable int id) {
        model.addAttribute("details", deviceConfigSummaryDao.getDeviceConfigActionHistory(id));
        return "summary/history.jsp";
    }
    
    @GetMapping("{id}/outOfSync")
    public String outOfSync(ModelMap model, YukonUserContext context, @PathVariable int id) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        SimpleDevice device = deviceDao.getYukonDevice(id);
        DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStateByDeviceId(id);
        if (configState != null && configState.getCurrentState() == ConfigState.OUT_OF_SYNC) {
            VerifyResult result = deviceConfigService.verifyConfig(device, context.getYukonUser());
            model.put("verifyResult", result);
        } else if (configState != null && configState.getCurrentState() == ConfigState.UNREAD) {
            model.put("needsUploadMessage", accessor.getMessage(baseKey + "needsUploadUnreadMessage"));
        } else {
            model.put("needsUploadMessage", accessor.getMessage(baseKey + "needsUploadMessage"));
        }

        return "summary/outOfSync.jsp";
    }
    
    @GetMapping("{errorCode}/displayError")
    public String displayError(ModelMap model, YukonUserContext context, @PathVariable int errorCode) {
        DeviceErrorDescription description = deviceErrorTranslatorDao.translateErrorCode(errorCode, context);
        model.put("error",  description);
        return "summary/error.jsp";
    }
       
    @PostMapping("{id}/uploadConfig")
    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    public @ResponseBody Map<String, Object> uploadConfig(@PathVariable int id, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        Map<String, Object> json = new HashMap<>();
        SimpleDevice device = deviceDao.getYukonDevice(id);
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
        executor.submit(() -> deviceConfigService.sendConfig(device, context.getYukonUser()));
        json.put("successMessage", accessor.getMessage(baseKey + "uploadConfig.success", pao.getPaoName()));
        return json;
    }
    
    @PostMapping("{id}/validateConfig")
    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    public @ResponseBody Map<String, Object> validateConfig(@PathVariable int id, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        Map<String, Object> json = new HashMap<>();
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        executor.submit(() -> deviceConfigService.readConfig(new SimpleDevice(pao.getLiteID(), pao.getPaoType()), context.getYukonUser()));
        json.put("successMessage", accessor.getMessage(baseKey + "validateConfig.success", pao.getPaoName()));
        return json;
    }
    
    @GetMapping("{id}/refreshDeviceRow")
    public String refreshDeviceRow(@PathVariable int id, ModelMap model) {
        DeviceConfigSummaryDetail detail = deviceConfigSummaryDao.getSummaryForDevice(id);
        model.addAttribute("detail", detail);
        return "summary/summaryResultRow.jsp";
    }
    
    private void setFilterValues(DeviceConfigSummaryFilter filter, String[] deviceSubGroups) {
        List<DeviceGroup> subGroups = new ArrayList<>();
        if (deviceSubGroups != null) {
            for (String subGroup : deviceSubGroups) {
                subGroups.add(deviceGroupService.resolveGroupName(subGroup));
            }
        }
        if (filter.getConfigurationIds() != null) {
            //Include Unassigned
            int index = filter.getConfigurationIds().indexOf(DEVICE_CONFIG_UNASSIGNED);
            if (index > -1) {
                filter.getConfigurationIds().remove(index);
                filter.setDisplayUnassigned(true);
            }
            //Include any assigned
            if (filter.getConfigurationIds().contains(DEVICE_CONFIG_ASSIGNED_TO_ANY)) {
                filter.setConfigurationIds(deviceConfigurationDao.getAllConfigsWithDeviceConfigStateEntry()
                        .stream()
                        .map(config -> config.getConfigurationId())
                        .collect(Collectors.toList()));
            }
        }
        filter.setGroups(subGroups);
    }
    
    @GetMapping("collectionAction/{action}")
    public String collectionAction(@PathVariable String action, @ModelAttribute DeviceConfigSummaryFilter filter,
            String[] deviceSubGroups, YukonUserContext userContext) {
        setFilterValues(filter, deviceSubGroups);
        SearchResults<DeviceConfigSummaryDetail> results = deviceConfigSummaryDao.getSummary(filter, PagingParameters.EVERYTHING,
                SortBy.DEVICE_NAME, Direction.asc);
        List<SimpleDevice> devices = results.getResultList().stream().map(d -> new SimpleDevice(d.getDevice()))
                .collect(Collectors.toList());
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        return "redirect:/bulk/config/deviceConfigs?action=" + action + "&collectionType=group&group.name="
                + tempGroup.getFullName();
    }

    @GetMapping("download")
    public String download(HttpServletResponse response, ModelMap model, @ModelAttribute DeviceConfigSummaryFilter filter,
            String[] deviceSubGroups, YukonUserContext userContext) throws IOException {
        setFilterValues(filter, deviceSubGroups);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        SearchResults<DeviceConfigSummaryDetail> details = deviceConfigSummaryDao.getSummary(filter, PagingParameters.EVERYTHING,
                SortBy.DEVICE_NAME, Direction.asc);
        List<String> headerRow = getHeader(accessor);
        List<List<String>> dataRows = getDataRows(details, accessor, userContext);
        String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "Device_Config_Summary_" + now + ".csv");
        return "";
    }
    
    private List<String> getHeader(MessageSourceAccessor accessor) {
        ArrayList<String> retValue = new ArrayList<>();
        retValue.add(accessor.getMessage(DetailSortBy.deviceName));
        retValue.add(accessor.getMessage(DetailSortBy.type));
        retValue.add(accessor.getMessage(DetailSortBy.deviceConfiguration));
        retValue.add(accessor.getMessage(DetailSortBy.lastAction));
        retValue.add(accessor.getMessage(DetailSortBy.lastActionStatus));
        retValue.add(accessor.getMessage(DetailSortBy.currentState));
        retValue.add(accessor.getMessage(DetailSortBy.lastActionStart));
        retValue.add(accessor.getMessage(DetailSortBy.lastActionEnd));
        return retValue;
    }
    
    private List<List<String>> getDataRows(SearchResults<DeviceConfigSummaryDetail> details, MessageSourceAccessor accessor, YukonUserContext userContext) {
        ArrayList<List<String>> retValue = new ArrayList<>();
        String naText = accessor.getMessage("yukon.common.na");
        String unassignedText = accessor.getMessage(baseKey + "configurations.unassigned");
        details.getResultList().forEach(d -> {
            ArrayList<String> row = new ArrayList<>();
            row.add(d.getDevice().getName());
            row.add(d.getDevice().getPaoIdentifier().getPaoType().getPaoTypeName());
            row.add(d.getDeviceConfig() == null ? unassignedText : d.getDeviceConfig().getName());
            row.add(d.getAction() == null ? naText : accessor.getMessage(d.getAction()));
            row.add(d.getStatus() == null ? naText : accessor.getMessage(d.getStatus()));
            row.add(d.getState() == null ? naText : accessor.getMessage(d.getState()));
            if (d.getActionStart() != null) {
                String start = dateFormattingService.format(d.getActionStart(), DateFormatEnum.BOTH, userContext);
                row.add(start);
            } else {
                row.add(naText);
            }
            if (d.getActionEnd() != null) {
                String end = dateFormattingService.format(d.getActionEnd(), DateFormatEnum.BOTH, userContext);
                row.add(end);
            } else {
                row.add(naText);
            }
            retValue.add(row);
        });
        return retValue;
    }
    
    public enum DetailSortBy implements DisplayableEnum {

        deviceName(SortBy.DEVICE_NAME),
        type(SortBy.DEVICE_TYPE),
        deviceConfiguration(SortBy.DEVICE_CONFIGURATION),
        lastAction(SortBy.ACTION),
        lastActionStatus(SortBy.ACTION_STATUS),
        currentState(SortBy.STATE),
        lastActionStart(SortBy.START),
        lastActionEnd(SortBy.END);
        
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
