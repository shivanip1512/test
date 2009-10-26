package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.OrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterByGenerator;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceCollectionType;
import com.cannontech.common.bulk.collection.RangeBasedDeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.bulk.model.DeviceCollectionProducer;

public class DeviceFilterCollectionProducer implements DeviceCollectionProducer, DeviceFilterCollectionHelper {
    private MeterSearchDao meterSearchDao;
    private MspMeterSearchService mspMeterSearchService;
    
    @Autowired
    public void setMeterSearchDao(MeterSearchDao meterSearchDao) {
        this.meterSearchDao = meterSearchDao;
    }
    
    @Autowired
    public void setMspMeterSearchService(MspMeterSearchService mspMeterSearchService) {
		this.mspMeterSearchService = mspMeterSearchService;
	}

    @Override
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.deviceFilter;
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
        throws ServletRequestBindingException {
        String orderByField = ServletRequestUtils.getStringParameter(request,
                                                                     getSupportedType().getParameterName("orderBy"),
                                                                     MeterSearchField.PAONAME.toString());
        boolean orderByDescending = ServletRequestUtils.getBooleanParameter(request,
                                                                            getSupportedType().getParameterName("descending"),
                                                                            false);
        OrderBy orderBy = new OrderBy(orderByField,
                                      orderByDescending);
        
        // all filters
        List<FilterBy> filterByList = new ArrayList<FilterBy>();
        filterByList.addAll(StandardFilterByGenerator.getStandardFilterByList());
        filterByList.addAll(mspMeterSearchService.getMspFilterByList());
        
        // query filter
        List<FilterBy> queryFilter = new ArrayList<FilterBy>();
        for (FilterBy filterBy : filterByList) {
            String filterValue = ServletRequestUtils.getStringParameter(request, getSupportedType().getParameterName(filterBy.getName()), "").trim();
            if (!StringUtils.isBlank(filterValue)) {
                filterBy.setFilterValue(filterValue);
                queryFilter.add(filterBy);
            }
        }
        
        return createDeviceGroupCollection(queryFilter, orderBy);
    }

    @Override
    public DeviceCollection createDeviceGroupCollection(final List<FilterBy> filterBys, final OrderBy orderBy) {
        return new RangeBasedDeviceCollection() {

            @Override
            public Map<String, String> getCollectionParameters() {
                Map<String, String> paramMap = new HashMap<String, String>();
                
                paramMap.put("collectionType", getSupportedType().name());

                for (FilterBy filterBy : filterBys) {
                    paramMap.put(getSupportedType().getParameterName(filterBy.getName()), filterBy.getFilterValue());
                }
                
                paramMap.put(getSupportedType().getParameterName("orderBy"), orderBy.getField().getName());
                paramMap.put(getSupportedType().getParameterName("descending"), Boolean.toString(orderBy.isDescending()));
                
                return paramMap;
            }

            @Override
            public MessageSourceResolvable getDescription() {

                int filterSize = filterBys.size();
                if (filterSize > 0) {
                    List<String> filterByString = new ArrayList<String>(filterSize);
                    for (FilterBy filterBy : filterBys) {
                        filterByString .add(filterBy.toSearchString());
                    }
                    String searchDescription = StringUtils.join(filterByString, " and ");
                    return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.deviceFilter", searchDescription);
                } else {
                    return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.deviceFilter.all");
                }

            }

            @Override
            public List<SimpleDevice> getDevices(int start, int size) {
                SearchResult<Meter> searchResult = meterSearchDao.search(filterBys, orderBy, start, size);
                List<Meter> resultList = searchResult.getResultList();
                ObjectMapper<Meter, SimpleDevice> mapper = new ObjectMapper<Meter, SimpleDevice>() {
                    public SimpleDevice map(Meter from) throws ObjectMappingException {
                        SimpleDevice yukonDevice = new SimpleDevice(from.getDeviceId(), from.getType());
                        return yukonDevice;
                    }
                };
                List<SimpleDevice> result = new MappingList<Meter, SimpleDevice>(resultList, mapper);
                
                return result;
            }
            
        };
    }

}
