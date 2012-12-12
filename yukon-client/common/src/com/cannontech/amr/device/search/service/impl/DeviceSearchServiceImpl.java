package com.cannontech.amr.device.search.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.amr.device.search.dao.DeviceSearchDao;
import com.cannontech.amr.device.search.model.DeviceSearchCategory;
import com.cannontech.amr.device.search.model.DeviceSearchField;
import com.cannontech.amr.device.search.model.DeviceSearchFilterBy;
import com.cannontech.amr.device.search.model.DeviceSearchFilterByGenerator;
import com.cannontech.amr.device.search.model.DeviceSearchOrderBy;
import com.cannontech.amr.device.search.service.DeviceSearchService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

@Service
public class DeviceSearchServiceImpl implements DeviceSearchService {
    @Autowired private DeviceSearchDao deviceSearchDao;
    @Autowired private PaoDao paoDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    public SearchResult<LiteYukonPAObject> search(DeviceSearchCategory category, List<DeviceSearchFilterBy> filterBy, DeviceSearchOrderBy orderBy, Boolean orderByDescending, int start, int count) {
        return deviceSearchDao.search(category, filterBy, orderBy, orderByDescending, start, count);
    }

    @Override
    public List<DeviceSearchField> getFieldsForCategory(DeviceSearchCategory category) {
        List<DeviceSearchField> fields = new ArrayList<DeviceSearchField>();
        
        switch(category) {
            case MCT:
                fields.add(DeviceSearchField.NAME);
                fields.add(DeviceSearchField.TYPE);
                fields.add(DeviceSearchField.ADDRESS);
                fields.add(DeviceSearchField.METERNUMBER);
                fields.add(DeviceSearchField.ROUTE);
                break;
            case IED:
            case RTU:
                fields.add(DeviceSearchField.NAME);
                fields.add(DeviceSearchField.TYPE);
                fields.add(DeviceSearchField.METERNUMBER);
                fields.add(DeviceSearchField.COMM_CHANNEL);
                break;
            case TRANSMITTER:
                fields.add(DeviceSearchField.NAME);
                fields.add(DeviceSearchField.TYPE);
                fields.add(DeviceSearchField.COMM_CHANNEL);
                break;
            case LMGROUP:
                fields.add(DeviceSearchField.LOAD_GROUP);
                fields.add(DeviceSearchField.LMGROUP_TYPE);
                //fields.add(DeviceSearchField.LMGROUP_ROUTE);
                //fields.add(DeviceSearchField.LMGROUP_SERIAL);
                //fields.add(DeviceSearchField.LMGROUP_CAPACITY);
                break;
            case CAP:
                fields.add(DeviceSearchField.NAME);
                fields.add(DeviceSearchField.TYPE);
                //fields.add(DeviceSearchField.CBC_SERIAL);
                break;
        }
        
        return fields;
    }

    @Override
    public List<DeviceSearchFilterBy> getFiltersForFields(List<DeviceSearchField> fields) {
        return DeviceSearchFilterByGenerator.getFilterForFields(fields, paoDao, deviceDao);
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
}
