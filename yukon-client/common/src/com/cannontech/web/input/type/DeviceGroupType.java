package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.web.input.validate.DefaultValidator;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Implementation of input type which represents a date input type.
 */
public class DeviceGroupType implements InputType<DeviceGroup> {
    private DeviceGroupService deviceGroupService;

    private String renderer = "stringType.jsp";

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    public Class<DeviceGroup> getTypeClass() {
        return DeviceGroup.class;
    }

    public InputValidator getValidator() {
        return DefaultValidator.getInstance();
    }

    public PropertyEditor getPropertyEditor() {
        PropertyEditor editor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                DeviceGroup group = deviceGroupService.resolveGroupName(text);
                setValue(group);
            }
            
            @Override
            public String getAsText() {
                DeviceGroup group = (DeviceGroup) getValue();
                String fullName = group.getFullName();
                return fullName;
            }
        };
        return editor;
    }

}
