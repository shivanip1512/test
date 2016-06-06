package com.cannontech.common.device.creation;

public class BadTemplateDeviceCreationException extends DeviceCreationException {
    public BadTemplateDeviceCreationException(String templateName) {
        super("Template '" + templateName + "' not found.", "invalidTemplateName", templateName);
    }
}
