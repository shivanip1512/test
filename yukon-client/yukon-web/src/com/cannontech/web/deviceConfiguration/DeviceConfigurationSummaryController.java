package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
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
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao.SortBy;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.InSync;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastAction;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastActionStatus;

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
    ExecutorService executor = Executors.newCachedThreadPool();
    
    private final static String baseKey = "yukon.web.modules.tools.configs.summary.";

    @RequestMapping(value="view", method=RequestMethod.GET)
    public String view(ModelMap model, @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                       @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {
        DeviceConfigSummaryFilter filter = new DeviceConfigSummaryFilter();
        //set defaults
        filter.setActions(Arrays.asList(LastAction.VERIFY));
        filter.setInSync(Arrays.asList(InSync.OUT_OF_SYNC));
        filter.setStatuses(Arrays.asList(LastActionStatus.FAILURE));
        model.addAttribute("filter", filter);
        prepareModel(model, filter, sorting, paging, userContext);
        return "summary/summary.jsp";
    }
    
    @RequestMapping(value="filter", method=RequestMethod.GET)
    public String filter(@DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, @DefaultItemsPerPage(value=250) PagingParameters paging,
                       ModelMap model, @ModelAttribute DeviceConfigSummaryFilter filter, String[] deviceSubGroups, YukonUserContext userContext) {
        boolean assignedToAny = filter.getConfigurationIds() != null && filter.getConfigurationIds().contains(-998);
        boolean unassigned = filter.getConfigurationIds() != null && filter.getConfigurationIds().contains(-999);
        setFilterValues(filter, deviceSubGroups);
        prepareModel(model, filter, sorting, paging, userContext);
        model.addAttribute("deviceSubGroups", deviceSubGroups);
        if (assignedToAny) {
            filter.setConfigurationIds(new ArrayList<>());
            filter.getConfigurationIds().add(-998);
        }
        if (unassigned) {
            filter.getConfigurationIds().add(-999);
        }
        model.addAttribute("filter", filter);
        return "summary/summary.jsp";
    }
    
    private void prepareModel(ModelMap model, DeviceConfigSummaryFilter filter, SortingParameters sorting, PagingParameters paging, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();
        model.addAttribute("configurations", configurations);
        model.addAttribute("lastActionOptions", LastAction.values());
        model.addAttribute("statusOptions", LastActionStatus.values());
        model.addAttribute("syncOptions", InSync.values());
        for (DetailSortBy column : DetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        SearchResults<DeviceConfigSummaryDetail> results = deviceConfigSummaryDao.getSummary(filter, paging, sortBy.value, dir);
        model.addAttribute("results",  results);
    }
    
    @RequestMapping(value="{id}/viewHistory", method=RequestMethod.GET)
    public String viewHistory(ModelMap model, @PathVariable int id) {
        model.addAttribute("details", deviceConfigSummaryDao.getDeviceConfigActionHistory(id));
        return "summary/history.jsp";
    }
    
    @RequestMapping(value="{id}/outOfSync", method=RequestMethod.GET)
    public String outOfSync(ModelMap model, YukonUserContext context, @PathVariable int id) {
        YukonDevice device = deviceDao.getYukonDevice(id);
        VerifyResult result = deviceConfigService.verifyConfig(device, context.getYukonUser());
        model.put("verifyResult", result);
        return "summary/outOfSync.jsp";
    }
    
    @RequestMapping(value="{errorCode}/displayError", method=RequestMethod.GET)
    public String displayError(ModelMap model, YukonUserContext context, @PathVariable int errorCode) {
        errorCode = 99;
        DeviceErrorDescription description = deviceErrorTranslatorDao.translateErrorCode(errorCode, context);
        model.put("error",  description);
        return "summary/error.jsp";
    }
       
    @RequestMapping(value="{id}/sendConfig", method=RequestMethod.POST)
    public void sendConfig(ModelMap model, @PathVariable int id, FlashScope flash, YukonUserContext context,
            HttpServletResponse resp) {
        YukonDevice device = deviceDao.getYukonDevice(id);
        executor.submit(() -> deviceConfigService.sendConfig(device, context.getYukonUser()));
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "sendConfig.success"));
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping(value="{id}/readConfig", method=RequestMethod.POST)
    public void readConfig(ModelMap model, @PathVariable int id, FlashScope flash, YukonUserContext context,
            HttpServletResponse resp) {
        YukonDevice device = deviceDao.getYukonDevice(id);
        executor.submit(() -> deviceConfigService.readConfig(device, context.getYukonUser()));
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "readConfig.success"));
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
        
    @RequestMapping(value="{id}/verifyConfig", method=RequestMethod.POST)
    public void verifyConfig(ModelMap model, @PathVariable int id, FlashScope flash, YukonUserContext context,
            HttpServletResponse resp) {
        YukonDevice device = deviceDao.getYukonDevice(id);
        VerifyResult result = deviceConfigService.verifyConfig(device, context.getYukonUser());
        if (result.isSynced()) {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.widgets.configWidget.inSync"));
        } else {
            List<MessageSourceResolvable> messages = new ArrayList<>();
            messages.add(
                new YukonMessageSourceResolvable("yukon.common.device.bulk.verifyConfigResults.failureResult"));
            result.getDiscrepancies().forEach(d -> {
                messages.add(YukonMessageSourceResolvable.createDefaultWithoutCode(d));
            });
            flash.setError(messages);
        }
        resp.setStatus(HttpStatus.NO_CONTENT.value());
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
            int index = filter.getConfigurationIds().indexOf(-999);
            if (index > -1) {
                filter.getConfigurationIds().remove(index);
                filter.setDisplayUnassigned(true);
            }
            //Include any assigned
            if (filter.getConfigurationIds().contains(-998)) {
                List<Integer> allIds = new ArrayList<>();
                List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();
                configurations.forEach(config -> allIds.add(config.getConfigurationId()));
                filter.setConfigurationIds(allIds);
            }
        }
        filter.setGroups(subGroups);
        //default to all if user selects none
        if (filter.getActions() == null || filter.getActions().isEmpty()) {
            filter.setActions(Arrays.asList(LastAction.values()));
        }
        if (filter.getInSync() == null || filter.getInSync().isEmpty()) {
            filter.setInSync(Arrays.asList(InSync.values()));
        }
        if (filter.getStatuses() == null || filter.getStatuses().isEmpty()) {
            filter.setStatuses(Arrays.asList(LastActionStatus.values()));
        }
    }
    
    @RequestMapping(value="collectionAction/{action}", method=RequestMethod.GET)
    public String collectionAction(@PathVariable String action, @ModelAttribute DeviceConfigSummaryFilter filter, String[] deviceSubGroups, YukonUserContext userContext) {
        setFilterValues(filter, deviceSubGroups);
        SearchResults<DeviceConfigSummaryDetail> results = deviceConfigSummaryDao.getSummary(filter, PagingParameters.EVERYTHING, SortBy.DEVICE_NAME, Direction.asc);
        List<SimpleDevice> devices = results.getResultList().stream().map(d -> new SimpleDevice(d.getDevice())).collect(Collectors.toList());
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup,  devices);
        return "redirect:/bulk/config/" + action + "?collectionType=group&group.name=" + tempGroup.getFullName();
    }
    
    public enum DetailSortBy implements DisplayableEnum {

        deviceName(SortBy.DEVICE_NAME),
        type(SortBy.DEVICE_TYPE),
        deviceConfiguration(SortBy.DEVICE_CONFIGURATION),
        lastAction(SortBy.ACTION),
        lastActionStatus(SortBy.ACTION_STATUS),
        inSync(SortBy.IN_SYNC),
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
