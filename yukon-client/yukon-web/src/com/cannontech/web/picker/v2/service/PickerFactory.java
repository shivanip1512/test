package com.cannontech.web.picker.v2.service;

import com.cannontech.web.picker.v2.Picker;

public interface PickerFactory {
    public Picker<?> getPicker(String typeName);
}
