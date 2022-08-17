package com.cannontech.web.tools.dataStreaming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfigException;
import com.cannontech.amr.rfn.dataStreaming.model.DiscrepancyResult;
import com.cannontech.amr.rfn.dataStreaming.model.SummarySearchCriteria;
import com.cannontech.amr.rfn.dataStreaming.model.SummarySearchResult;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.CollectionActionAlertHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/dataStreaming/*")
@CheckCparm(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED)
public class DataStreamingConfigurationsController {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private AlertService alertService;

    private static final List<Integer> intervals = ImmutableList.of(1, 3, 5, 15, 30);
    private final static String baseKey = "yukon.web.modules.tools.dataStreaming.";

    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public String configurations(@DefaultSort(dir=Direction.asc, sort="attributes") SortingParameters sorting, 
                                 PagingParameters paging, ModelMap model, YukonUserContext userContext) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        Map<DataStreamingConfig, DeviceCollection> configsAndDevices = dataStreamingService.getAllDataStreamingConfigurationsAndDevices();
        Set<DataStreamingConfig> existingConfigs = configsAndDevices.keySet();

        existingConfigs.forEach(config -> {
            config.setAccessor(accessor);
            config.setSelectedInterval(config.getAttributes().get(0).getInterval());
            config.getName();
            config.setNumberOfDevices(configsAndDevices.get(config).getDeviceCount());
        });

        SearchResults<DataStreamingConfig> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, existingConfigs.size());

        ConfigurationSortBy sortBy = ConfigurationSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<DataStreamingConfig> itemList = Lists.newArrayList(existingConfigs);

        Comparator<DataStreamingConfig> comparator = (o1, o2) -> o1.getCommaDelimitedAttributes().compareTo(o2.getCommaDelimitedAttributes());
        if (sortBy == ConfigurationSortBy.interval) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getSelectedInterval()).compareTo(Integer.valueOf(o2.getSelectedInterval()));
        } else if (sortBy == ConfigurationSortBy.numberOfDevices) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getNumberOfDevices()).compareTo(Integer.valueOf(o2.getNumberOfDevices()));
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        List<SortableColumn> columns = new ArrayList<>();
        for (ConfigurationSortBy column : ConfigurationSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, existingConfigs.size());
        searchResult.setResultList(itemList);

        model.addAttribute("existingConfigs", searchResult);
        model.addAttribute("configsAndDevices", configsAndDevices);

        return "../dataStreaming/configurations.jsp";
    }

    @RequestMapping(value = "createTemporaryGroup", method = RequestMethod.POST)
    public String createTemporaryGroup(HttpServletRequest request) {

        String redirectUrl = request.getParameter("redirectUrl");
        String ids = request.getParameter("idList.ids");
        String [] deviceIds = ids.split(",");

        StoredDeviceGroup deviceGroup = tempDeviceGroupService.createTempGroup();
        List<SimpleDevice> devices = new ArrayList<>();

        for (String deviceId : deviceIds) {
            int deviceIdInt = Integer.parseInt(deviceId);
            SimpleDevice device = deviceDao.getYukonDevice(deviceIdInt);
            devices.add(device);
        }

        deviceGroupMemberEditorDao.addDevices(deviceGroup,  devices);

        return "redirect:" + redirectUrl + "?collectionType=group&group.name=" + deviceGroup.getFullName();

    }

    @RequestMapping(value = "summary", method = RequestMethod.GET)
    public String summary(@DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
            PagingParameters paging, ModelMap model, YukonUserContext userContext, 
            HttpServletRequest request, FlashScope flash) throws ServletException {

        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        List<DataStreamingConfig> existingConfigs = dataStreamingService.getAllDataStreamingConfigurations();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        existingConfigs.forEach(config -> config.setAccessor(accessor));
        model.addAttribute("existingConfigs", existingConfigs);

        List<BuiltInAttribute> attributes = new ArrayList<>(dataStreamingAttributeHelper.getAllSupportedAttributes());
        attributes.sort((a1, a2) -> a1.getDescription().compareTo(a2.getDescription()));
        model.addAttribute("searchAttributes", attributes);

        model.addAttribute("searchIntervals", intervals);
        getSummaryResults(model, flash, sorting, paging, userContext, request);

        return "../dataStreaming/summary.jsp";
    }

    @RequestMapping(value = "summaryResults", method = RequestMethod.GET)
    public String summaryResults(@DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
            PagingParameters paging, ModelMap model, YukonUserContext userContext, 
            HttpServletRequest request, FlashScope flash) throws ServletException {

        getSummaryResults(model, flash, sorting, paging, userContext, request);
        return "../dataStreaming/summaryResults.jsp";
    }

    private void getSummaryResults(ModelMap model, FlashScope flash, SortingParameters sorting, PagingParameters paging, 
            YukonUserContext userContext, HttpServletRequest request) {

        SearchResults<SummarySearchResult> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        SummarySearchCriteria searchFilter = new SummarySearchCriteria();
        int selectedConfiguration = ServletRequestUtils.getIntParameter(request, "selectedConfiguration", -1);
        searchFilter.setSelectedConfiguration(selectedConfiguration);
        int[] selectedGateways = ServletRequestUtils.getIntParameters(request,"selectedGatewayIds");
        selectedGateways = selectedGateways.length > 0 ? selectedGateways : new int[]{-1};
        Integer[] gatewaysSelected = ArrayUtils.toObject(selectedGateways);
        searchFilter.setSelectedGatewayIds(Arrays.asList(gatewaysSelected));
        String[] attributesSelected = ServletRequestUtils.getStringParameters(request, "selectedAttributes");
        attributesSelected = attributesSelected.length > 0 ? attributesSelected : new String[]{"-1"};
        int selectedInterval = ServletRequestUtils.getIntParameter(request, "selectedInterval", -1);
        searchFilter.setSelectedInterval(selectedInterval);
        searchFilter.setSelectedAttributes(Arrays.asList(attributesSelected));

        try {
            Double minPercent = ServletRequestUtils.getDoubleParameter(request,  "minLoadPercent");
            searchFilter.setMinLoadPercent(minPercent);
        } catch (ServletRequestBindingException e) {
            searchFilter.setMinLoadPercent(null);
        }

        try {
            Double maxPercent = ServletRequestUtils.getDoubleParameter(request,  "maxLoadPercent");
            searchFilter.setMaxLoadPercent(maxPercent);
        } catch (ServletRequestBindingException e) {
            searchFilter.setMaxLoadPercent(null);
        }
        model.addAttribute("searchFilters", searchFilter);

        List<SummarySearchResult> results = new ArrayList<>();

        if (searchFilter.getMaxLoadPercent() != null && searchFilter.getMinLoadPercent() != null
            && searchFilter.getMaxLoadPercent() < searchFilter.getMinLoadPercent()) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "summary.filter.maxLessThanMinError"));
        } else {
            try {
                results = getSearchResults(searchFilter, accessor);
            } catch (DataStreamingConfigException e) {
                flash.setError(e.getMessageSourceResolvable());
            }
        }

        int endIndex = Math.min(startIndex + itemsPerPage, results.size());

        SummarySortBy sortBy = SummarySortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<SummarySearchResult> itemList = Lists.newArrayList(results);

        Comparator<SummarySearchResult> comparator = (o1, o2) -> o1.getMeter().getName().compareTo(o2.getMeter().getName());
        if (sortBy == SummarySortBy.deviceType) {
            comparator = (o1, o2) -> o1.getMeter().getPaoIdentifier().getPaoType().getPaoTypeName()
                .compareTo(o2.getMeter().getPaoIdentifier().getPaoType().getPaoTypeName());
        } else if (sortBy == SummarySortBy.serialNumber) {
            comparator = (o1, o2) -> o1.getMeter().getRfnIdentifier().getSensorSerialNumber()
                .compareTo(o2.getMeter().getRfnIdentifier().getSensorSerialNumber());
        } else if (sortBy == SummarySortBy.gatewayName) {
            comparator = (o1, o2) -> o1.getGateway().getName().compareTo(o2.getGateway().getName());
        } else if (sortBy == SummarySortBy.gatewayLoading) {
            comparator = (o1, o2) -> Double.valueOf(o1.getGateway().getData().getDataStreamingLoadingPercent())
                .compareTo(Double.valueOf(o2.getGateway().getData().getDataStreamingLoadingPercent()));
        } else if (sortBy == SummarySortBy.attributes) {
            comparator = (o1, o2) -> o1.getConfig().getCommaDelimitedAttributes().compareTo(o2.getConfig().getCommaDelimitedAttributes());
        } else if (sortBy == SummarySortBy.interval) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getConfig().getSelectedInterval())
                .compareTo(Integer.valueOf(o2.getConfig().getSelectedInterval()));
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        List<SortableColumn> columns = new ArrayList<>();
        for (SummarySortBy column : SummarySortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, results.size());
        searchResult.setResultList(itemList);

        model.addAttribute("searchResults", searchResult);

        List<Integer> deviceIds = new ArrayList<>();
        results.forEach(device -> deviceIds.add(device.getMeter().getPaoIdentifier().getPaoId()));
        DeviceCollection deviceCollection = dcProducer.createDeviceCollection(deviceIds, null);
        model.addAttribute("deviceCollection", deviceCollection);
        String deviceIdList = deviceIds.stream()
                .map(i -> i.toString())
                .collect(Collectors.joining(","));
        model.addAttribute("deviceIds", deviceIdList);

    }

    private List<SummarySearchResult> getSearchResults(SummarySearchCriteria criteria, 
                                                       MessageSourceAccessor accessor) throws DataStreamingConfigException {
        List<SummarySearchResult> results = dataStreamingService.search(criteria);
        for(SummarySearchResult result: results){
            result.getConfig().setAccessor(accessor);
        }
        return results;
    }

    @RequestMapping(value = "exportSearch", method = RequestMethod.GET)
    public String exportSearchResults(ModelMap model, FlashScope flash, SummarySearchCriteria criteria,
            HttpServletResponse response,
            YukonUserContext userContext) {
        try {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

            downloadSearchResults(criteria, response, accessor);
            
            return null;
        } catch (IOException ioe) {
            flash.setError(YukonMessageSourceResolvable.createSingleCodeWithArguments(baseKey + "summary.results.errors.connectionError", ioe));
        } catch (DataStreamingConfigException dsce) {
            flash.setError(dsce.getMessageSourceResolvable());
        }
        return "redirect:/tools/dataStreaming/summary";
    }
    
    private void downloadSearchResults(SummarySearchCriteria criteria, HttpServletResponse response, MessageSourceAccessor accessor)
            throws DataStreamingConfigException, IOException {

        List<SummarySearchResult> results = getSearchResults(criteria, accessor);

        List<String> columnNames = Lists.newArrayList();

        columnNames.add(accessor.getMessage(baseKey + "summary.results.deviceName"));
        columnNames.add(accessor.getMessage(baseKey + "summary.results.deviceType"));
        columnNames.add(accessor.getMessage(baseKey + "summary.results.serialNumber"));
        columnNames.add(accessor.getMessage(baseKey + "summary.results.gatewayName"));
        columnNames.add(accessor.getMessage(baseKey + "summary.results.gatewayLoading"));
        columnNames.add(accessor.getMessage(baseKey + "summary.results.attributes"));
        columnNames.add(accessor.getMessage(baseKey + "summary.results.interval"));

        List<List<String>> dataGrid = getGrid(results, accessor);

        String csvFileName = accessor.getMessage(baseKey + "summary.results.exportFileName");
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, csvFileName + "_" + now + ".csv");
    }

    private List<List<String>> getGrid(List<SummarySearchResult> results, MessageSourceAccessor accessor){
        List<List<String>> lists = new ArrayList<>();
        for(SummarySearchResult result: results){
            List<String> row = new ArrayList<>();
            row.add(result.getMeter().getName());
            row.add(accessor.getMessage(result.getMeter().getPaoIdentifier().getPaoType().getFormatKey()));
            row.add(result.getMeter().getRfnIdentifier().getSensorSerialNumber());
            row.add((result.getGateway() != null) ? result.getGateway().getName() : "N/A");
            row.add(String.valueOf((result.getGateway() != null)
                    ? result.getGateway().getData().getDataStreamingLoadingPercent() : "N/A"));
            row.add(result.getConfig().getCommaDelimitedAttributes());
            row.add(String.valueOf(result.getConfig().getSelectedInterval()));
            lists.add(row);
        }
        return lists;
    }

    @RequestMapping(value = "discrepancies", method = RequestMethod.GET)
    public String discrepancies(@DefaultSort(dir=Direction.asc, sort="device") SortingParameters sorting, 
                                PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        List<DiscrepancyResult> discrepancies = dataStreamingService.findDiscrepancies();
        for (DiscrepancyResult result : discrepancies) {
            if (result.getExpected() == null) {
                result.setExpected(new DataStreamingConfig());
            }
            result.getExpected().setAccessor(accessor);
            if (result.getActual() == null) {
                result.setActual(new DataStreamingConfig());
            }
            result.getActual().setAccessor(accessor);
        }

        SearchResults<DiscrepancyResult> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, discrepancies.size());

        DiscrepancySortBy sortBy = DiscrepancySortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<DiscrepancyResult>itemList = Lists.newArrayList(discrepancies);

        Comparator<DiscrepancyResult> comparator = (o1, o2) -> o1.getPaoName().compareTo(o2.getPaoName());
        if (sortBy == DiscrepancySortBy.actualAttributes) {
            comparator = (o1, o2) -> o1.getActual().getCommaDelimitedAttributesOnOff()
                .compareTo(o2.getActual().getCommaDelimitedAttributesOnOff());
        } else if (sortBy == DiscrepancySortBy.expectedAttributes) {
            comparator = (o1, o2) -> o1.getExpected().getCommaDelimitedAttributesOnOff()
                .compareTo(o2.getExpected().getCommaDelimitedAttributesOnOff());
        } else if (sortBy == DiscrepancySortBy.status) {
            comparator = (o1, o2) -> o1.getStatus().name().compareTo(o2.getStatus().name());
        } else if (sortBy == DiscrepancySortBy.lastCommunicated) {
            comparator = (o1, o2) -> o1.getLastCommunicated().compareTo(o2.getLastCommunicated());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        List<SortableColumn> columns = new ArrayList<>();
        for (DiscrepancySortBy column : DiscrepancySortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, discrepancies.size());
        searchResult.setResultList(itemList);

        model.addAttribute("discrepancies", searchResult);

        List<Integer> deviceIds = new ArrayList<>();
        itemList.forEach(discrepancy -> deviceIds.add(discrepancy.getDeviceId()));
        String deviceIdList = deviceIds.stream()
                .map(i -> i.toString())
                .collect(Collectors.joining(","));
        model.addAttribute("deviceIds", deviceIdList);

        return "../dataStreaming/discrepancies.jsp";
    }

    @RequestMapping(value = "discrepancies/{deviceId}/resend", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String resendDevice(ModelMap model, @PathVariable int deviceId, YukonUserContext userContext,
            FlashScope flash, PagingParameters paging, SortingParameters sorting) {

        try {
            CollectionActionResult result= dataStreamingService.resend(Arrays.asList(deviceId), null, userContext);
            if(result.getInfoText() != null){
                flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(result.getInfoText()));
            }
            return "redirect:/collectionActions/progressReport/view?key=" + result.getCacheKey();
        } catch (DataStreamingConfigException e) {
            flash.setError(e.getMessageSourceResolvable());
            return getSortingPagingUrl(sorting, paging);
        }
    }
    
    @RequestMapping(value = "discrepancies/{deviceId}/read", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String readDevice(ModelMap model, @PathVariable int deviceId, YukonUserContext userContext,
            FlashScope flash, PagingParameters paging, SortingParameters sorting) {        
        int cacheKey = dataStreamingService.read(deviceId, userContext);
        return "redirect:/collectionActions/progressReport/view?key=" + cacheKey;
    }


    @RequestMapping(value = "discrepancies/{deviceId}/accept", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String acceptDevice(ModelMap model, @PathVariable int deviceId, YukonUserContext userContext,
            FlashScope flash, PagingParameters paging, SortingParameters sorting) {
        try {
            dataStreamingService.accept(Arrays.asList(deviceId), userContext);
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(deviceId);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "discrepancies.acceptSuccess", pao.getPaoName()));
        } catch (DataStreamingConfigException e) {
            flash.setError(e.getMessageSourceResolvable());
        }
        return getSortingPagingUrl(sorting, paging);
    }

    @RequestMapping(value = "discrepancies/{deviceId}/remove", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String removeDevice(ModelMap model, @PathVariable int deviceId, YukonUserContext userContext,
            FlashScope flash, PagingParameters paging, SortingParameters sorting) {
        dataStreamingService.deleteDataStreamingReportAndUnassignConfig(deviceId, userContext.getYukonUser());
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(deviceId);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "discrepancies.removeSuccess", pao.getPaoName()));
        return getSortingPagingUrl(sorting, paging);
    }

    @RequestMapping(value = "discrepancies/resendAll", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String resendAll(ModelMap model, HttpServletRequest request, YukonUserContext userContext, FlashScope flash,
            PagingParameters paging, SortingParameters sorting) {
        List<Integer> deviceList = new ArrayList<>();
        String ids = request.getParameter("deviceIds");
        String [] deviceIds = ids.split(",");
        for (String deviceId : deviceIds) {
            deviceList.add(Integer.parseInt(deviceId));
        }

        try {
            SimpleCallback<CollectionActionResult> alertCallback =
                    CollectionActionAlertHelper.createAlert(AlertType.DATA_STREAMING, alertService,
                    messageSourceResolver.getMessageSourceAccessor(userContext), request);
            CollectionActionResult result = dataStreamingService.resend(deviceList, alertCallback, userContext);
            return "redirect:/collectionActions/progressReport/view?key=" + result.getCacheKey();
        } catch (DataStreamingConfigException e) {
            flash.setError(e.getMessageSourceResolvable());
            return getSortingPagingUrl(sorting, paging);
        }
    }

    @RequestMapping(value = "discrepancies/acceptAll", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String acceptAll(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                            FlashScope flash, PagingParameters paging, SortingParameters sorting) {
        List<Integer> deviceList = new ArrayList<>();
        String ids = request.getParameter("deviceIds");
        String [] deviceIds = ids.split(",");
        for (String deviceId : deviceIds) {
            deviceList.add(Integer.parseInt(deviceId));
        }

        try {
            dataStreamingService.accept(deviceList, userContext);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "discrepancies.acceptAllSuccess"));
        } catch (DataStreamingConfigException e) {
            flash.setError(e.getMessageSourceResolvable());
        }
        return getSortingPagingUrl(sorting, paging);
    }
    
    private String getSortingPagingUrl(SortingParameters sorting, PagingParameters paging) {
        String baseUrl = "redirect:/tools/dataStreaming/discrepancies";
        if (paging != null) {
            baseUrl += "?page=" + paging.getPage() + "&itemsPerPage=" + paging.getItemsPerPage();
            if (sorting != null) {
                baseUrl += "&sort=" + sorting.getSort() + "&dir=" + sorting.getDirection();
            }
        }

        return baseUrl;
    }

    public enum DiscrepancySortBy implements DisplayableEnum {

        device,
        expectedAttributes,
        actualAttributes,
        status,
        lastCommunicated;

        @Override
        public String getFormatKey() {
            return baseKey + "discrepancies." + name();
        }
    }

    public enum SummarySortBy implements DisplayableEnum {

        deviceName,
        deviceType,
        serialNumber,
        gatewayName,
        gatewayLoading,
        attributes,
        interval;

        @Override
        public String getFormatKey() {
            return baseKey + "summary.results." + name();
        }
    }

    public enum ConfigurationSortBy implements DisplayableEnum {

        attributes,
        interval,
        numberOfDevices;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}