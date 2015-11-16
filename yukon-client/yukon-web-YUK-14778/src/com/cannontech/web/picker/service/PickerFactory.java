package com.cannontech.web.picker.service;

import com.cannontech.web.picker.Picker;

public interface PickerFactory {
    public Picker<?> getPicker(String typeName);
}
