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

import com.cannontech.amr.csr.dao.CsrSearchDao;
import com.cannontech.amr.csr.model.CsrSearchField;
import com.cannontech.amr.csr.model.ExtendedMeter;
import com.cannontech.amr.csr.model.FilterBy;
import com.cannontech.amr.csr.model.FilterByGenerator;
import com.cannontech.amr.csr.model.OrderBy;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.RangeBasedDeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DeviceFilterCollectionProducer extends DeviceCollectionProducerBase implements DeviceFilterCollectionHelper {
    private CsrSearchDao csrSearchDao;
    
    @Autowired
    public void setCsrSearchDao(CsrSearchDao csrSearchDao) {
        this.csrSearchDao = csrSearchDao;
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
                                                                     CsrSearchField.PAONAME.toString());
        boolean orderByDescending = ServletRequestUtils.getBooleanParameter(request,
                                                                            getParameterName("descending"),
                                                                            false);
        OrderBy orderBy = new OrderBy(orderByField,
                                      orderByDescending);
        
        List<FilterBy> filterByList = FilterByGenerator.getFilterByList();
        List<FilterBy> queryFilter = new ArrayList<FilterBy>();

        for (FilterBy filterBy : filterByList) {
            String filterValue = ServletRequestUtils.getStringParameter(request, getParameterName(filterBy.getName()));
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
                        filterByString .add(filterBy.toCsrString());
                    }
                    String searchDescription = StringUtils.join(filterByString, " and ");
                    return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.deviceFilter", searchDescription);
                } else {
                    return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.deviceFilter.all");
                }

            }

            @Override
            public List<YukonDevice> getDevices(int start, int size) {
                SearchResult<ExtendedMeter> searchResult = csrSearchDao.search(filterBys, orderBy, start, size);
                List<ExtendedMeter> resultList = searchResult.getResultList();
                ObjectMapper<ExtendedMeter, YukonDevice> mapper = new ObjectMapper<ExtendedMeter, YukonDevice>() {
                    public YukonDevice map(ExtendedMeter from) throws ObjectMappingException {
                        YukonDevice yukonDevice = new YukonDevice(from.getDeviceId(), from.getType());
                        return yukonDevice;
                    }
                };
                List<YukonDevice> result = new MappingList<ExtendedMeter, YukonDevice>(resultList, mapper);
                
                return result;
            }
            
        };
    }

}
