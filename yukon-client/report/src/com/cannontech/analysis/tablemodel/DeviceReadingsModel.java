package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class DeviceReadingsModel extends BareDatedReportModelBase<DeviceReadingsModel.ModelRow> 
                                   implements UserContextModelAttributes {

    private Logger log = YukonLogManager.getLogger(DeviceReadingsModel.class);

    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Attribute attribute;
    private List<String> groupsFilter;
    private List<String> deviceFilter;

    private DeviceGroupService deviceGroupService;

    private DeviceGroupEditorDao deviceGroupEditorDao;

    private boolean getAll = true;

    private boolean excludeDisabledDevices = false;

    private DeviceDao deviceDao;

    private PointFormattingService pointFormattingService;
    private PaoLoadingService paoLoadingService;
    private RawPointHistoryDao rawPointHistoryDao;

    private YukonUserContext userContext;

    static public class ModelRow {
        public String deviceName;
        public String type;
        public String date;
        public String value;
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public String getTitle() {
        return "Device Readings Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        List<SimpleDevice> devices = getDeviceList();
        List<DisplayablePao> displayableDevices = paoLoadingService.getDisplayableDevices(devices);
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> intermediateResults;
        
        if (getAll) {
            intermediateResults = rawPointHistoryDao.getAttributeData(displayableDevices, attribute, getStartDate(), getStopDate(), excludeDisabledDevices);
        } else {
            intermediateResults = rawPointHistoryDao.getLimitedAttributeData(displayableDevices, attribute, null, null, 1, excludeDisabledDevices);
        }
        CachingPointFormattingService cachingPointFormattingService = pointFormattingService.getCachedInstance();            
        for (DisplayablePao displayablePao : displayableDevices) {
            List<PointValueQualityHolder> values = intermediateResults.get(displayablePao.getPaoIdentifier());
            for (PointValueQualityHolder pointValueHolder : values) {
        
                DeviceReadingsModel.ModelRow row = new DeviceReadingsModel.ModelRow();
                row.deviceName = displayablePao.getName();
                row.type = displayablePao.getPaoIdentifier().getPaoType().getPaoTypeName();
                row.date = cachingPointFormattingService.getValueString(pointValueHolder,
                                                                        Format.DATE,
                                                                        userContext);
                row.value = cachingPointFormattingService.getValueString(pointValueHolder,
                                                                         Format.SHORT,
                                                                         userContext);
                data.add(row);
            }
        }

        log.info("Report Records Collected from Database: " + data.size());
    }

    private List<SimpleDevice> getDeviceList() {
        if (groupsFilter != null && !groupsFilter.isEmpty()) {
            Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupsFilter);
            return Lists.newArrayList(deviceGroupService.getDevices(groups));
        } else if (deviceFilter != null && !deviceFilter.isEmpty()) {
            List<SimpleDevice> devices = Lists.newArrayList();
            for(String deviceName : deviceFilter){
                try {
                    devices.add(deviceDao.getYukonDeviceObjectByName(deviceName));
                } catch (DataAccessException e) {
                    log.error("Unable to find device with name: " + deviceName + ". This device will be skipped.");
                    continue;
                }
            }
            return devices;
        } else {
            /* If they didn't pick anything to filter on, assume all devices. */
            /* Use contents of SystemGroupEnum.DEVICETYPES. */
            DeviceGroup group = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICETYPES);
            return Lists.newArrayList(deviceGroupService.getDevices(Collections.singletonList(group)));
        }
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setRetrieveAll(boolean all) {
        this.getAll = all;
    }

    public void setGroupsFilter(List<String> namesList) {
        this.groupsFilter = namesList;
    }

    public void setDeviceFilter(List<String> idsSet) {
        this.deviceFilter = idsSet;
    }

    public void setUserContext(YukonUserContext userContext){
        this.userContext = userContext;
    }
    
    public YukonUserContext getUserContext() {
        return userContext;
    }

    public void setExcludeDisabledDevices(boolean excludeDisabledDevices) {
        this.excludeDisabledDevices = excludeDisabledDevices;
    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Required
    public void setPointFormattingService(PointFormattingService pointFormattingService){
        this.pointFormattingService = pointFormattingService;
    }
    
    @Required
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Required
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
}