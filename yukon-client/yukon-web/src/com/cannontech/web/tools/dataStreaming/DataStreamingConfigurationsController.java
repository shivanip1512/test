package com.cannontech.web.tools.dataStreaming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.SummarySearchCriteria;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.ImmutableMap.Builder;

@Controller
@RequestMapping("/dataStreaming/*")
public class DataStreamingConfigurationsController {

    @Autowired private DataStreamingService dataStreamingService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RfnGatewayService rfnGatewayService;
    
    //TODO: Move these outside to helper
    private static final List<Integer> intervals = ImmutableList.of(1, 3, 5, 15, 30);
    private static final List<BuiltInAttribute> attributes = ImmutableList.of(BuiltInAttribute.KVAR,
        BuiltInAttribute.DEMAND, BuiltInAttribute.DELIVERED_KWH, BuiltInAttribute.RECEIVED_KWH);
    
    private Map<ConfigurationSortBy, Comparator<DataStreamingConfig>> sorters;
    
    @PostConstruct
    public void initialize() {
        Builder<ConfigurationSortBy, Comparator<DataStreamingConfig>> builder = ImmutableMap.builder();
        builder.put(ConfigurationSortBy.interval, getIntervalComparator());
        builder.put(ConfigurationSortBy.attributes, getAttributesComparator());
        sorters = builder.build();
    }
    
    @RequestMapping("configurations")
    public String configurations(@DefaultSort(dir=Direction.asc, sort="attributes") SortingParameters sorting, PagingParameters paging, ModelMap model, YukonUserContext userContext) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        List<DataStreamingConfig> existingConfigs = dataStreamingService.getAllDataStreamingConfigurations();
        existingConfigs.forEach(config -> config.setAccessor(accessor));
        existingConfigs.forEach(config -> config.setSelectedInterval(config.getAttributes().get(0).getInterval()));
        existingConfigs.forEach(config -> config.getName());
        
        SearchResults<DataStreamingConfig> searchResult = new SearchResults<DataStreamingConfig>();
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
        
        List<SortableColumn> columns = new ArrayList<SortableColumn>();
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

        return "../dataStreaming/configurations.jsp";
    }
    
    @RequestMapping("summary")
    public String summary(ModelMap model, YukonUserContext userContext) throws ServletException {
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        List<DataStreamingConfig> existingConfigs = dataStreamingService.getAllDataStreamingConfigurations();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        existingConfigs.forEach(config -> config.setAccessor(accessor));
        model.addAttribute("existingConfigs", existingConfigs);
        model.addAttribute("attributes", attributes);
        model.addAttribute("intervals", intervals);
        model.addAttribute("searchFilters", new SummarySearchCriteria());
        return "../dataStreaming/summary.jsp";
    }
    
    @RequestMapping(value="summary", method=RequestMethod.POST)
    public String summarySubmit(@ModelAttribute("searchFilters") SummarySearchCriteria searchFilters, BindingResult result,
                                RedirectAttributes redirectAttributes, FlashScope flash, HttpServletRequest request) throws ServletException {
        Integer[] gatewaysSelected = ArrayUtils.toObject(ServletRequestUtils.getIntParameters(request, "gatewaysSelect"));
        searchFilters.setSelectedGatewayIds(Arrays.asList(gatewaysSelected));
        String[] attributesSelected = ServletRequestUtils.getStringParameters(request, "attributesSelect");
        searchFilters.setSelectedAttributes(Arrays.asList(attributesSelected));
        
        //TODO: Return search results

        return "../dataStreaming/summary.jsp";
    }

    
    public enum ConfigurationSortBy implements DisplayableEnum {
        
        attributes,
        interval;

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
}