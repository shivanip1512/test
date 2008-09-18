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
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.FilterByGenerator;
import com.cannontech.amr.meter.search.model.OrderBy;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.RangeBasedDeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DeviceFilterCollectionProducer extends DeviceCollectionProducerBase implements DeviceFilterCollectionHelper {
    private MeterSearchDao meterSearchDao;
    
    @Autowired
    public void setMeterSearchDao(MeterSearchDao meterSearchDao) {
        this.meterSearchDao = meterSearchDao;
    }

    @Override
    public String getSupportedType() {
        return "deviceFilter";
    }

    @Override
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
        throws ServletRequestBindingException {
        String orderByField = ServletRequestUtils.getStringParameter(request,
                                                                     getParameterName("orderBy"),
                                                                     MeterSearchField.PAONAME.toString());
        boolean orderByDescending = ServletRequestUtils.getBooleanParameter(request,
                                                                            getParameterName("descending"),
                                                                            false);
        OrderBy orderBy = new OrderBy(orderByField,
                                      orderByDescending);
        
        List<FilterBy> filterByList = FilterByGenerator.getFilterByList();
        List<FilterBy> queryFilter = new ArrayList<FilterBy>();

        for (FilterBy filterBy : filterByList) {
            String filterValue = ServletRequestUtils.getStringParameter(request, getParameterName(filterBy.getName()), "").trim();
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
                
                paramMap.put("collectionType", getSupportedType());

                for (FilterBy filterBy : filterBys) {
                    paramMap.put(getParameterName(filterBy.getName()), filterBy.getFilterValue());
                }
                
                paramMap.put(getParameterName("orderBy"), orderBy.getField().getName());
                paramMap.put(getParameterName("descending"), Boolean.toString(orderBy.isDescending()));
                
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
            public List<YukonDevice> getDevices(int start, int size) {
                SearchResult<Meter> searchResult = meterSearchDao.search(filterBys, orderBy, start, size);
                List<Meter> resultList = searchResult.getResultList();
                ObjectMapper<Meter, YukonDevice> mapper = new ObjectMapper<Meter, YukonDevice>() {
                    public YukonDevice map(Meter from) throws ObjectMappingException {
                        YukonDevice yukonDevice = new YukonDevice(from.getDeviceId(), from.getType());
                        return yukonDevice;
                    }
                };
                List<YukonDevice> result = new MappingList<Meter, YukonDevice>(resultList, mapper);
                
                return result;
            }
            
        };
    }

}
