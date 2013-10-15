package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;

/**
 * Implementation of input type which represents a meter input type.
 */

public class MeterType extends DefaultValidatedType<YukonMeter> {

    private MeterDao meterDao = null;
    private String renderer = null;

    public MeterType() {
        setRenderer("integerType.jsp");
    }

    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Override
    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    @Override
    public Class<YukonMeter> getTypeClass() {
        return YukonMeter.class;
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        PropertyEditor editor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                int deviceID = Integer.parseInt(text);
                YukonMeter meter = meterDao.getForId(deviceID);
                setValue(meter);
            }
            
            @Override
            public String getAsText() {
                YukonMeter meter = (YukonMeter) getValue();
                String deviceId = String.valueOf(meter.getDeviceId());
                return deviceId;
            }
        };
        return editor;
    }
}