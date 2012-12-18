package com.cannontech.amr.device.search.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.amr.device.search.dao.DeviceSearchDao;
import com.cannontech.amr.device.search.model.CompositeFilterBy;
import com.cannontech.amr.device.search.model.DeviceSearchCategory;
import com.cannontech.amr.device.search.model.DeviceSearchField;
import com.cannontech.amr.device.search.model.DeviceSearchResultEntry;
import com.cannontech.amr.device.search.model.FilterBy;
import com.cannontech.amr.device.search.model.FilterByField;
import com.cannontech.amr.device.search.model.FilterByField.Comparator;
import com.cannontech.amr.device.search.model.OrderByField;
import com.cannontech.amr.device.search.model.SearchField;
import com.cannontech.amr.device.search.service.DeviceSearchService;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;

@Service
public class DeviceSearchServiceImpl implements DeviceSearchService {
    @Autowired private DeviceSearchDao deviceSearchDao;
    @Autowired private PaoDao paoDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private LoadGroupDao loadGroupDao;
    
    @Override
    public SearchResult<DeviceSearchResultEntry> search(List<SearchField> fields, List<FilterBy> filters, OrderByField orderBy, int start, int count) {
        return deviceSearchDao.search(fields, filters, orderBy, start, count);
    }

    @Override
    public List<SearchField> getFieldsForCategory(DeviceSearchCategory category) {
        List<SearchField> fields = new ArrayList<SearchField>();
        
        fields.add(DeviceSearchField.ID);
        fields.add(DeviceSearchField.NAME);
        fields.add(DeviceSearchField.TYPE);
        
        switch(category) {
            case MCT:
                fields.add(DeviceSearchField.ADDRESS);
                fields.add(DeviceSearchField.METERNUMBER);
                fields.add(DeviceSearchField.ROUTE);
                break;
            case IED:
            case RTU:
                fields.add(DeviceSearchField.METERNUMBER);
                fields.add(DeviceSearchField.COMM_CHANNEL);
                break;
            case TRANSMITTER:
                fields.add(DeviceSearchField.COMM_CHANNEL);
                break;
            case LMGROUP:
                fields.add(DeviceSearchField.LMGROUP_ROUTE);
                fields.add(DeviceSearchField.LMGROUP_SERIAL);
                fields.add(DeviceSearchField.LMGROUP_CAPACITY);
                break;
            case CAP:
                fields.add(DeviceSearchField.CBC_SERIAL);
                break;
        }
        
        return fields;
    }

    @Override
    public FilterBy getFiltersForCategory(DeviceSearchCategory category) {
        switch(category) {
            case CAP:
                return new FilterByField(DeviceSearchField.CLASS, PaoClass.CAPCONTROL.getDbString(), Comparator.STARTS_WITH_IGNORE_CASE);
            case IED:
                return new FilterByField(DeviceSearchField.TYPE, createStringList(PaoType.getIedTypes().iterator()), Comparator.IN);
            case LMGROUP:
                return CompositeFilterBy.or(
                        new FilterByField(DeviceSearchField.CLASS, PaoClass.GROUP.getDbString(), Comparator.STARTS_WITH_IGNORE_CASE),
                        new FilterByField(DeviceSearchField.CLASS, PaoClass.LOADMANAGEMENT.getDbString(), Comparator.STARTS_WITH_IGNORE_CASE)
                );
            case MCT:
                return new FilterByField(DeviceSearchField.TYPE, createStringList(PaoType.getMctTypes().iterator()), Comparator.IN);
            case RTU:
                return new FilterByField(DeviceSearchField.TYPE, createStringList(PaoType.getRtuTypes().iterator()), Comparator.IN);
            case TRANSMITTER:
                return new FilterByField(DeviceSearchField.CLASS, PaoClass.TRANSMITTER.getDbString(), Comparator.STARTS_WITH_IGNORE_CASE);
        }
        
        return null;
    }

    @Override
    public LiteYukonPAObject getDevice(int deviceId) {
        return paoDao.getLiteYukonPAO(deviceId);
    }

    @Override
    public DeviceSearchCategory getDeviceCategory(int deviceId) {
        LiteYukonPAObject lPao = paoDao.getLiteYukonPAO(deviceId);
        return DeviceSearchCategory.fromLiteYukonPAObject(lPao);
    }

    @Override
    public boolean isMeterDetailsSupported(LiteYukonPAObject lPao) {
        return paoDefinitionDao.isTagSupported(lPao.getPaoType(), PaoTag.METER_DETAIL_DISPLAYABLE);
    }

    private String createStringList(Iterator<PaoType> iterator) {
        StringBuilder stringList = new StringBuilder();
        while(iterator.hasNext()) {
            PaoType type = iterator.next();
            stringList.append("'").append(type.getDbString()).append("'");
            if(iterator.hasNext()) {
                stringList.append(',');
            }
        }
        
        return stringList.toString();
    }
}
