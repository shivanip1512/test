package com.cannontech.web.widget.meterInfo.model;

import com.cannontech.amr.meter.model.IedMeter;

public class IEDMeterModel extends MeterModel {
    private int portId;

    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public static IEDMeterModel of(IedMeter meter) {

        IEDMeterModel model = new IEDMeterModel();
        model.setDeviceId(meter.getDeviceId());
        model.setDisabled(meter.isDisabled());
        model.setMeterNumber(meter.getMeterNumber());
        model.setName(meter.getName());
        model.setPortId(meter.getPortId());

        return model;
    }

    public static IEDMeterModel of(CreateMeterModel meter) {

        IEDMeterModel model = new IEDMeterModel();
        model.setDeviceId(meter.getDeviceId());
        model.setDisabled(meter.isDisabled());
        model.setMeterNumber(meter.getMeterNumber());
        model.setName(meter.getName());
        model.setPortId(meter.getPortId());

        return model;
    }

    public static IEDMeterModel of(MeterModel meter) {

        if (meter instanceof IEDMeterModel) {
            return (IEDMeterModel) meter;
        }
        if (meter instanceof CreateMeterModel) {
            return of((CreateMeterModel) meter);
        }

        return null;
    }
}
