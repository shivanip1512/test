package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.groups.dao.DeviceGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.web.input.validate.InputValidator;
import com.cannontech.web.input.validate.NullValidator;

public class DeviceGroupType extends BaseEnumeratedType<DeviceGroup> {

    private DeviceGroupDao deviceGroupDao = null;
    private DeviceGroupService deviceGroupService = null;

    public void setDeviceGroupDao(DeviceGroupDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    public List<String> getOptionList() {

        List<String> groupNameList = new ArrayList<String>();

        List<? extends DeviceGroup> groupList = deviceGroupDao.getAllGroups();
        for (DeviceGroup group : groupList) {
            groupNameList.add(group.getFullName());
        }

        return groupNameList;
    }
    
    public PropertyEditor getPropertyEditor() {

        return new PropertyEditorSupport() {

            @Override
            public void setAsText(String arg0) {
                DeviceGroup group = deviceGroupService.resolveGroupName(arg0);
                setValue(group);
            }

            @Override
            public String getAsText() {
                
                if(getValue() == null){
                    return "";
                }
                
                return ((DeviceGroup) getValue()).getFullName();
            }

        };
    }

    public Class<DeviceGroup> getTypeClass() {
        return DeviceGroup.class;
    }


    public InputValidator<DeviceGroup> getValidator() {
        return NullValidator.getInstance();
    }

}
