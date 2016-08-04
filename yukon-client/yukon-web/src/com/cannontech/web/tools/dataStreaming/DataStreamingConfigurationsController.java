package com.cannontech.web.tools.dataStreaming;

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

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchCriteria;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchResult;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/dataStreaming/*")
public class DataStreamingConfigurationsController {

    @Autowired private DataStreamingService dataStreamingService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    
    private static final List<Integer> intervals = ImmutableList.of(1, 3, 5, 15, 30);
    
    private Map<ConfigurationSortBy, Comparator<DataStreamingConfig>> sorters;
    private Map<SummarySortBy, Comparator<SummarySearchResult>> summarySorters;
    
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
        summaryBuilder.put(SummarySortBy.meterNumber, getMeterNumberComparator());
        summaryBuilder.put(SummarySortBy.gatewayName, getGatewayNameComparator());
        summaryBuilder.put(SummarySortBy.gatewayLoading, getGatewayLoadingComparator());
        summaryBuilder.put(SummarySortBy.attributes, getSummaryAttributesComparator());
        summaryBuilder.put(SummarySortBy.interval, getSummaryIntervalComparator());
        summarySorters = summaryBuilder.build();
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
        
        List<DataStreamingConfig>itemList = Lists.newArrayList(existingConfigs);

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
    
    @RequestMapping("summary")
    public String summary(@DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, PagingParameters paging, ModelMap model, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        List<DataStreamingConfig> existingConfigs = dataStreamingService.getAllDataStreamingConfigurations();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        existingConfigs.forEach(config -> config.setAccessor(accessor));
        model.addAttribute("existingConfigs", existingConfigs);
        
        List<PaoType> types = new ArrayList<PaoType>();
        types.addAll(PaoType.getRfMeterTypes());
        List<BuiltInAttribute> attributes = new ArrayList<BuiltInAttribute>(dataStreamingAttributeHelper.getAllSupportedAttributes(types));
        attributes.sort((BuiltInAttribute a1, BuiltInAttribute a2) -> a1.getDescription().compareTo(a2.getDescription()));
        model.addAttribute("searchAttributes", attributes);
        
        model.addAttribute("searchIntervals", intervals);
        getSummaryResults(model, sorting, paging, userContext, request);
        
        return "../dataStreaming/summary.jsp";
    }
    
    @RequestMapping("summaryResults")
    public String summaryResults(@DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, PagingParameters paging, ModelMap model, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        getSummaryResults(model, sorting, paging, userContext, request);
        return "../dataStreaming/summaryResults.jsp";
    }
    
    private void getSummaryResults(ModelMap model, SortingParameters sorting, PagingParameters paging, YukonUserContext userContext, HttpServletRequest request) {
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
        searchFilter.setSelectedAttributes(Arrays.asList(attributesSelected));
        model.addAttribute("searchFilters", searchFilter);
        List<SummarySearchResult> results = getSearchResults(searchFilter, accessor, model);
        int endIndex = Math.min(startIndex + itemsPerPage, results.size());

        SummarySortBy sortBy = SummarySortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        List<SummarySearchResult>itemList = Lists.newArrayList(results);

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
        searchResult.getResultList().forEach(device -> deviceIds.add(device.getMeter().getPaoIdentifier().getPaoId()));
        DeviceCollection deviceCollection = dcProducer.createDeviceCollection(deviceIds, null);
        model.addAttribute("deviceCollection", deviceCollection);
        String deviceIdList = deviceIds.stream()
                .map(i -> i.toString())
                .collect(Collectors.joining(","));
        model.addAttribute("deviceIds", deviceIdList);
        
    }
    
    private List<SummarySearchResult> getSearchResults(SummarySearchCriteria criteria, MessageSourceAccessor accessor, ModelMap model) {
        List<SummarySearchResult> results = dataStreamingService.search(criteria);
        for(SummarySearchResult result: results){
            result.getConfig().setSelectedInterval(result.getConfig().getAttributes().get(0).getInterval());
            result.getConfig().setAccessor(accessor);
        }
        return results;
    }
    
    public enum SummarySortBy implements DisplayableEnum {
        
        deviceName,
        deviceType,
        meterNumber,
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
    
    private Comparator<SummarySearchResult> getMeterNumberComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<SummarySearchResult> meterNumberOrdering = normalStringComparer
            .onResultOf(new Function<SummarySearchResult, String>() {
                @Override
                public String apply(SummarySearchResult from) {
                    return from.getMeter().getName();
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
                    return from.getGateway().getLoadingPercent();
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