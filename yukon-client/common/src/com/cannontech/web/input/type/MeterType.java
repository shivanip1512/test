package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.web.input.validate.DefaultValidator;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Implementation of input type which represents a date input type.
 */

public class MeterType implements InputType<Meter> {

    private MeterDao meterDao = null;
    private String renderer = null;

    public MeterType() {
        setRenderer("integerType.jsp");
    }

    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<Meter> getTypeClass() {
        return Meter.class;
    }

    public InputValidator getValidator() {
        return DefaultValidator.getInstance();
    }
    
    public PropertyEditor getPropertyEditor() {
        PropertyEditor editor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                int deviceID = Integer.parseInt(text);
                Meter meter = meterDao.getForId(deviceID);
                setValue(meter);
            }
            
            @Override
            public String getAsText() {
                Meter meter = (Meter) getValue();
                String deviceId = String.valueOf(meter.getDeviceId());
                return deviceId;
            }
        };
        return editor;
    }
}