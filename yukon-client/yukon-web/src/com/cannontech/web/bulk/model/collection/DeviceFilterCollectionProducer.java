package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterByGenerator;
import com.cannontech.common.bulk.collection.DeviceFilterCollectionHelper;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.RangeBasedDeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;

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
        MeterSearchOrderBy orderBy = new MeterSearchOrderBy(orderByField,
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
    public DeviceCollection createDeviceGroupCollection(final List<FilterBy> filterBys, final MeterSearchOrderBy orderBy) {
        return new RangeBasedDeviceCollection() {
            @Override
            public DeviceCollectionType getCollectionType() {
                return getSupportedType();
            }
            
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
                SearchResults<YukonMeter> searchResult = meterSearchDao.search(filterBys, orderBy, start, size);
                List<YukonMeter> resultList = searchResult.getResultList();
                List<SimpleDevice> result = PaoUtils.asSimpleDeviceListFromPaos(resultList);
                return result;
            }

            @Override
            public Set<String> getErrorDevices() {
                return null;
            }

            @Override
            public int getDeviceErrorCount() {
                return 0;
            }

            @Override
            public String getUploadFileName() {
                return null;
            }

            @Override
            public String getHeader() {
                return null;
            }
            
        };
    }
    
    @Override
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase collectionBase) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection deviceCollection) {
        throw new UnsupportedOperationException();
    }
}
