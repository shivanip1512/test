package com.cannontech.web.amr.dataCollection;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService.RangeType;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/dataCollection/*")
public class DataCollectionController {
    
    @Autowired private DataCollectionWidgetService dataCollectionWidgetService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private final static String baseKey = "yukon.web.modules.amr.dataCollection.detail.";
    
    @RequestMapping(value="updateChart", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> updateChart(ModelMap model, String deviceGroup, Boolean includeDisabled, HttpServletResponse resp) throws Exception {
        Map<String, Object> json = new HashMap<>();
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        DataCollectionSummary summary = dataCollectionWidgetService.getDataCollectionSummary(group, true);
        json.put("summary",  summary);
        return json;
    }
    
    @RequestMapping("forceUpdate")
    public @ResponseBody Map<String, Object> forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        dataCollectionWidgetService.collectData();
        json.put("success", true);
        return json;
    }
    
    @RequestMapping("detail")
    public String detail(ModelMap model, String deviceGroup, Boolean includeDisabled, YukonUserContext userContext,
                         @DefaultSort(dir=Direction.asc, sort="deviceName") SortingParameters sorting, 
                         @DefaultItemsPerPage(value=50) PagingParameters paging) throws Exception {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroup);
        List<DeviceCollectionDetail> detail = dataCollectionWidgetService.getDeviceCollectionResult(group, group, includeDisabled, RangeType.values());
        model.addAttribute("deviceGroup", deviceGroup);
        model.addAttribute("includeDisabled", includeDisabled);
        
        SearchResults<DeviceCollectionDetail> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, detail.size());
        
        DetailSortBy sortBy = DetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        List<DeviceCollectionDetail>itemList = Lists.newArrayList(detail);
        
        Comparator<DeviceCollectionDetail> comparator = (o1, o2) -> o1.getDeviceName().compareTo(o2.getDeviceName());
        if (sortBy == DetailSortBy.meterSerialNumber) {
            comparator = (o1, o2) -> o1.getMeterSerialNumber().compareTo(o2.getMeterSerialNumber());
        } else if (sortBy == DetailSortBy.deviceType) {
            comparator = (o1, o2) -> o1.getPaoIdentifier().getPaoType().getPaoTypeName().compareTo(o2.getPaoIdentifier().getPaoType().getPaoTypeName());
        } else if (sortBy == DetailSortBy.address) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getAddress()).compareTo(Integer.valueOf(o2.getAddress()));
        } else if (sortBy == DetailSortBy.routeGateway) {
            comparator = (o1, o2) -> o1.getRoute().compareTo(o2.getRoute());
        } else if (sortBy == DetailSortBy.recentReading) {
            comparator = (o1, o2) -> o1.getValue().getPointDataTimeStamp().compareTo(o2.getValue().getPointDataTimeStamp());
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        for (DetailSortBy column : DetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, detail.size());
        searchResult.setResultList(itemList);  
        model.addAttribute("detail", searchResult);

        return "dataCollection/detail.jsp";
    }
    
    public enum DetailSortBy implements DisplayableEnum {

        deviceName,
        meterSerialNumber,
        deviceType,
        address,
        routeGateway,
        recentReading;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}
