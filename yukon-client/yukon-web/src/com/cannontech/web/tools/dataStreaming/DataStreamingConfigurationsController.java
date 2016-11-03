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

import javax.annotation.PostConstruct;
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

import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
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
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfigResult;
import com.cannontech.web.rfn.dataStreaming.model.DiscrepancyResult;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchCriteria;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchResult;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/dataStreaming/*")
@CheckCparm(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED)
public class DataStreamingConfigurationsController {
    
    private final static String baseKey = "yukon.web.modules.tools.dataStreaming.";

    @Autowired private DataStreamingService dataStreamingService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceDao deviceDao;
    
    private static final List<Integer> intervals = ImmutableList.of(1, 3, 5, 15, 30);
    
    private Map<ConfigurationSortBy, Comparator<DataStreamingConfig>> sorters;
    private Map<SummarySortBy, Comparator<SummarySearchResult>> summarySorters;
    private Map<DiscrepancySortBy, Comparator<DiscrepancyResult>> discrepancySorters;

    
    @PostConstruct
    public void initialize() {
        Builder<ConfigurationSortBy, Comparator<DataStreamingConfig>> builder = ImmutableMap.builder();
        builder.put(ConfigurationSortBy.interval, getIntervalComparator());
        builder.put(ConfigurationSortBy.attributes, getAttributesComparator());
        builder.put(ConfigurationSortBy.numberOfDevices, getNumberOfDevicesComparator());
        sorters = builder.build();
        Builder<SummarySortBy, Comparator<SummarySearchResult>> summaryBuilder = ImmutableMap.builder();
        summaryBuilder.put(SummarySortBy.deviceName, getDeviceNameComparator());
        summaryBuilder.put(SummarySortBy.deviceType, getDeviceTypeComparator());
        summaryBuilder.put(SummarySortBy.serialNumber, getSerialNumberComparator());
        summaryBuilder.put(SummarySortBy.gatewayName, getGatewayNameComparator());
        summaryBuilder.put(SummarySortBy.gatewayLoading, getGatewayLoadingComparator());
        summaryBuilder.put(SummarySortBy.attributes, getSummaryAttributesComparator());
        summaryBuilder.put(SummarySortBy.interval, getSummaryIntervalComparator());
        summarySorters = summaryBuilder.build();
        Builder<DiscrepancySortBy, Comparator<DiscrepancyResult>> discrepancyBuilder = ImmutableMap.builder();
        discrepancyBuilder.put(DiscrepancySortBy.device, getDeviceComparator());
        discrepancyBuilder.put(DiscrepancySortBy.expectedAttributes, getExpectedAttributesComparator());
        discrepancyBuilder.put(DiscrepancySortBy.actualAttributes, getActualAttributesComparator());
        discrepancyBuilder.put(DiscrepancySortBy.status, getStatusComparator());
        discrepancyBuilder.put(DiscrepancySortBy.lastCommunicated, getLastCommunicatedComparator());
        discrepancySorters = discrepancyBuilder.build();
    }
    
    @RequestMapping("configurations")
    public String configurations(@DefaultSort(dir=Direction.asc, sort="attributes") SortingParameters sorting, PagingParameters paging, ModelMap model, YukonUserContext userContext) throws ServletException {
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

        Comparator<DataStreamingConfig> comparator = sorters.get(sortBy);
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
    
    @RequestMapping("createTemporaryGroup")
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
    
    @RequestMapping("summary")
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
    
    @RequestMapping("summaryResults")
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
        Integer[] gatewaysSelected = ArrayUtils.toObject(ServletRequestUtils.getIntParameters(request, "gatewaysSelect"));
        searchFilter.setSelectedGatewayIds(Arrays.asList(gatewaysSelected));
        String[] attributesSelected = ServletRequestUtils.getStringParameters(request, "attributesSelect");
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
        
        if (searchFilter.getMaxLoadPercent() != null && searchFilter.getMinLoadPercent() != null && searchFilter.getMaxLoadPercent() < searchFilter.getMinLoadPercent()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.tools.dataStreaming.summary.filter.maxLessThanMinError"));
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

        Comparator<SummarySearchResult> comparator = summarySorters.get(sortBy);
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
    
    private List<SummarySearchResult> getSearchResults(SummarySearchCriteria criteria, MessageSourceAccessor accessor) throws DataStreamingConfigException {
        List<SummarySearchResult> results = dataStreamingService.search(criteria);
        for(SummarySearchResult result: results){
            result.getConfig().setAccessor(accessor);
        }
        return results;
    }
    
    @RequestMapping(value = "exportSearch")
    public String exportSearchResults(ModelMap model, FlashScope flash, SummarySearchCriteria criteria,
            HttpServletResponse response,
            YukonUserContext userContext) {
        try {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

            downloadSearchResults(criteria, response, accessor);
        } catch (IOException ioe) {
            flash.setError(YukonMessageSourceResolvable.createSingleCodeWithArguments(
                    "yukon.web.modules.tools.dataStreaming.summary.results.errors.connectionError", ioe));
        } catch (DataStreamingConfigException dsce) {
            flash.setError(dsce.getMessageSourceResolvable());
        }
        return "../dataStreaming/summaryResults.jsp";
    }
    private void downloadSearchResults(SummarySearchCriteria criteria, HttpServletResponse response, MessageSourceAccessor accessor)
            throws DataStreamingConfigException, IOException {
        
        List<SummarySearchResult> results = getSearchResults(criteria, accessor);
        
        List<String> columnNames = Lists.newArrayList();

        columnNames.add(accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.deviceName"));
        columnNames.add(accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.deviceType"));
        columnNames.add(accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.serialNumber"));
        columnNames.add(accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.gatewayName"));
        columnNames.add(accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.gatewayLoading"));
        columnNames.add(accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.attributes"));
        columnNames.add(accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.interval"));
        
        List<List<String>> dataGrid = getGrid(results, accessor);
        
        String csvFileName = accessor.getMessage("yukon.web.modules.tools.dataStreaming.summary.results.exportFileName");
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, csvFileName + ".csv");
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
    
    @RequestMapping("discrepancies")
    public String discrepancies(@DefaultSort(dir=Direction.asc, sort="device") SortingParameters sorting, PagingParameters paging, ModelMap model, YukonUserContext userContext) {
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

        Comparator<DiscrepancyResult> comparator = discrepancySorters.get(sortBy);
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
    
    @RequestMapping("discrepancies/{deviceId}/resend")
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String resendDevice(ModelMap model, @PathVariable int deviceId, YukonUserContext userContext,
            FlashScope flash, PagingParameters paging, SortingParameters sorting) {
        LiteYukonUser user = userContext.getYukonUser();
        String redirectUrl = "redirect:/tools/dataStreaming/discrepancies?page=" + paging.getPage() + "&itemsPerPage=" + paging.getItemsPerPage() + "&sort=" + sorting.getSort() + "&dir=" + sorting.getDirection();

        DataStreamingConfigResult result;
        try {
            result = dataStreamingService.resend(Arrays.asList(deviceId), user);
            if(result.acceptedWithError()){
                flash.setError(new YukonMessageSourceResolvable(baseKey + "discrepancies.acceptedWithError"));
                return redirectUrl;
            }
            model.addAttribute("resultsId", result.getResultsId());
        } catch (DataStreamingConfigException e) {
            flash.setError(e.getMessageSourceResolvable());
            return redirectUrl;
        }
        return "redirect:/bulk/dataStreaming/dataStreamingResults";
    }
    
    @RequestMapping("discrepancies/{deviceId}/accept")
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String acceptDevice(ModelMap model, @PathVariable int deviceId, YukonUserContext userContext, FlashScope flash) {
        LiteYukonUser user = userContext.getYukonUser();

        dataStreamingService.accept(Arrays.asList(deviceId), user);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "discrepancies.acceptSuccess"));
        return "redirect:/tools/dataStreaming/discrepancies";
    }
    
    @RequestMapping("discrepancies/{deviceId}/remove")
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String removeDevice(ModelMap model, @PathVariable int deviceId, YukonUserContext userContext,
            FlashScope flash) {
        LiteYukonUser user = userContext.getYukonUser();
        
        DataStreamingConfigResult result = dataStreamingService.deleteDataStreamingReportAndUnassignConfig(deviceId, user);
        model.addAttribute("resultsId", result.getResultsId());
        return "redirect:/bulk/dataStreaming/dataStreamingResults";

    }
    
    @RequestMapping(value="discrepancies/resendAll", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String resendAll(ModelMap model, HttpServletRequest request, YukonUserContext userContext, FlashScope flash,
            PagingParameters paging, SortingParameters sorting) {
        List<Integer> deviceList = new ArrayList<>();
        String ids = request.getParameter("deviceIds");
        String [] deviceIds = ids.split(",");
        for (String deviceId : deviceIds) {
            deviceList.add(Integer.parseInt(deviceId));
        }
        String redirectUrl = "redirect:/tools/dataStreaming/discrepancies?page=" + paging.getPage() + "&itemsPerPage=" + paging.getItemsPerPage() + "&sort=" + sorting.getSort() + "&dir=" + sorting.getDirection();

        LiteYukonUser user = userContext.getYukonUser();
    
        DataStreamingConfigResult result;
        try {
            result = dataStreamingService.resend(deviceList, user);
        } catch (DataStreamingConfigException e) {
            flash.setError(e.getMessageSourceResolvable());
            return redirectUrl;
        }
        if (result.acceptedWithError()) {
            if (result.getAcceptedWithErrorFailedDeviceCount() == 0) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "discrepancies.acceptedWithError"));
            } else {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "discrepancies.acceptedWithError.resending",
                    result.getAcceptedWithErrorFailedDeviceCount(), result.getTotalItems()));
            }
            return redirectUrl;
        }
        model.addAttribute("resultsId", result.getResultsId());
        return "redirect:/bulk/dataStreaming/dataStreamingResults";
    }
    
    @RequestMapping(value="discrepancies/acceptAll", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
    public String acceptAll(ModelMap model, HttpServletRequest request, YukonUserContext userContext, FlashScope flash) {
        List<Integer> deviceList = new ArrayList<>();
        String ids = request.getParameter("deviceIds");
        String [] deviceIds = ids.split(",");
        for (String deviceId : deviceIds) {
            deviceList.add(Integer.parseInt(deviceId));
        }
        LiteYukonUser user = userContext.getYukonUser();
        
        dataStreamingService.accept(deviceList, user);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "discrepancies.acceptAllSuccess"));
        return "redirect:/tools/dataStreaming/discrepancies";
      
    }
    
    public enum DiscrepancySortBy implements DisplayableEnum {
        
        device,
        expectedAttributes,
        actualAttributes,
        status,
        lastCommunicated;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.dataStreaming.discrepancies." + name();
        }
    }
    
    private Comparator<DiscrepancyResult> getDeviceComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DiscrepancyResult> nameOrdering = normalStringComparer
            .onResultOf(new Function<DiscrepancyResult, String>() {
                @Override
                public String apply(DiscrepancyResult from) {
                    return from.getPaoName();
                }
            });
        return nameOrdering;
    }
    
    private Comparator<DiscrepancyResult> getExpectedAttributesComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DiscrepancyResult> attOrdering = normalStringComparer
            .onResultOf(new Function<DiscrepancyResult, String>() {
                @Override
                public String apply(DiscrepancyResult from) {
                    return from.getExpected().getCommaDelimitedAttributesOnOff();
                }
            });
        return attOrdering;
    }
    
    private Comparator<DiscrepancyResult> getActualAttributesComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DiscrepancyResult> attOrdering = normalStringComparer
            .onResultOf(new Function<DiscrepancyResult, String>() {
                @Override
                public String apply(DiscrepancyResult from) {
                    return from.getActual().getCommaDelimitedAttributesOnOff();
                }
            });
        return attOrdering;
    }
    
    private Comparator<DiscrepancyResult> getStatusComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DiscrepancyResult> statusOrdering = normalStringComparer
            .onResultOf(new Function<DiscrepancyResult, String>() {
                @Override
                public String apply(DiscrepancyResult from) {
                    return from.getStatus().name();
                }
            });
        return statusOrdering;
    }
    
    private Comparator<DiscrepancyResult> getLastCommunicatedComparator() {
        Ordering<Instant> instantComparer = Ordering.natural();
        Ordering<DiscrepancyResult> statusOrdering = instantComparer
            .onResultOf(new Function<DiscrepancyResult, Instant>() {
                @Override
                public Instant apply(DiscrepancyResult from) {
                    return from.getLastCommunicated();
                }
            });
        return statusOrdering;
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
            return "yukon.web.modules.tools.dataStreaming.summary.results." + name();
        }
    }
    
    private Comparator<SummarySearchResult> getDeviceNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<SummarySearchResult> nameOrdering = normalStringComparer
            .onResultOf(new Function<SummarySearchResult, String>() {
                @Override
                public String apply(SummarySearchResult from) {
                    return from.getMeter().getName();
                }
            });
        return nameOrdering;
    }
    
    private Comparator<SummarySearchResult> getDeviceTypeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<SummarySearchResult> typeOrdering =
            normalStringComparer.onResultOf(new Function<SummarySearchResult, String>() {
                @Override
                public String apply(SummarySearchResult from) {
                    return from.getMeter().getPaoIdentifier().getPaoType().getPaoTypeName();
                }
            });
        return typeOrdering;
    }
    
    private Comparator<SummarySearchResult> getSerialNumberComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<SummarySearchResult> meterNumberOrdering = normalStringComparer
            .onResultOf(new Function<SummarySearchResult, String>() {
                @Override
                public String apply(SummarySearchResult from) {
                    return from.getMeter().getRfnIdentifier().getSensorSerialNumber();
                }
            });
        return meterNumberOrdering;
    }
    
    private Comparator<SummarySearchResult> getGatewayNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<SummarySearchResult> gatewayOrdering = normalStringComparer
            .onResultOf(new Function<SummarySearchResult, String>() {
                @Override
                public String apply(SummarySearchResult from) {
                    return from.getGateway().getName();
                }
            });
        return gatewayOrdering;
    }
    
    private Comparator<SummarySearchResult> getGatewayLoadingComparator() {
        Ordering<Double> normalDoubleComparer = Ordering.natural();
        Ordering<SummarySearchResult> doubleOrdering = normalDoubleComparer
            .onResultOf(new Function<SummarySearchResult, Double>() {
                @Override
                public Double apply(SummarySearchResult from) {
                    return from.getGateway().getData().getDataStreamingLoadingPercent();
                }
            });
        return doubleOrdering;
    }
    
    private Comparator<SummarySearchResult> getSummaryAttributesComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<SummarySearchResult> attOrdering = normalStringComparer
            .onResultOf(new Function<SummarySearchResult, String>() {
                @Override
                public String apply(SummarySearchResult from) {
                    return from.getConfig().getCommaDelimitedAttributes();
                }
            });
        return attOrdering;
    }
    
    private Comparator<SummarySearchResult> getSummaryIntervalComparator() {
        Ordering<Integer> normalIntComparer = Ordering.natural();
        Ordering<SummarySearchResult> intOrdering = normalIntComparer
            .onResultOf(new Function<SummarySearchResult, Integer>() {
                @Override
                public Integer apply(SummarySearchResult from) {
                    return from.getConfig().getSelectedInterval();
                }
            });
        return intOrdering;
    }

    
    public enum ConfigurationSortBy implements DisplayableEnum {
        
        attributes,
        interval,
        numberOfDevices;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.dataStreaming." + name();
        }
    }
    
    private Comparator<DataStreamingConfig> getIntervalComparator() {
        Ordering<Integer> normalIntComparer = Ordering.natural();
        Ordering<DataStreamingConfig> intOrdering = normalIntComparer
            .onResultOf(new Function<DataStreamingConfig, Integer>() {
                @Override
                public Integer apply(DataStreamingConfig from) {
                    return from.getSelectedInterval();
                }
            });
        return intOrdering;
    }
    
    private Comparator<DataStreamingConfig> getAttributesComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<DataStreamingConfig> attOrdering = normalStringComparer
            .onResultOf(new Function<DataStreamingConfig, String>() {
                @Override
                public String apply(DataStreamingConfig from) {
                    return from.getCommaDelimitedAttributes();
                }
            });
        return attOrdering;
    }
    
    private Comparator<DataStreamingConfig> getNumberOfDevicesComparator() {
        Ordering<Integer> normalIntComparer = Ordering.natural();
        Ordering<DataStreamingConfig> intOrdering = normalIntComparer
            .onResultOf(new Function<DataStreamingConfig, Integer>() {
                @Override
                public Integer apply(DataStreamingConfig from) {
                    return from.getNumberOfDevices();
                }
            });
        return intOrdering;
    }
}