package com.cannontech.multispeak.data.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeters;
import com.cannontech.msp.beans.v5.multispeak.GasMeter;
import com.cannontech.msp.beans.v5.multispeak.GasMeters;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.msp.beans.v5.multispeak.WaterMeter;
import com.cannontech.msp.beans.v5.multispeak.WaterMeters;
import com.cannontech.multispeak.data.MspReturnList;

public class MspMeterReturnList extends MspReturnList {

    private Meters meters;

    private Object lastProcessed;
    
    public Meters getMeters() {
        if (meters == null) {
            meters = new Meters();
            meters.setElectricMeters(new ElectricMeters());
            meters.setWaterMeters(new WaterMeters());
            meters.setGasMeters(new GasMeters());
        }
        return meters;
    }
    public List<ElectricMeter> getElectricMeters() {
        return getMeters().getElectricMeters().getElectricMeter();
    }

    public void setElectricMeters(List<ElectricMeter> electricMeters) {
        getMeters().getElectricMeters().getElectricMeter().addAll(electricMeters);
    }

    public List<WaterMeter> getWaterMeters() {
        return getMeters().getWaterMeters().getWaterMeter();
    }

    public void setWaterMeters(List<WaterMeter> waterMeters) {
        getMeters().getWaterMeters().getWaterMeter().addAll(waterMeters);
    }
    
    public List<WaterMeter> getGasMeters() {
        return getMeters().getWaterMeters().getWaterMeter();
    }

    public void setGasMeters(List<GasMeter> gasMeters) {
        getMeters().getGasMeters().getGasMeter().addAll(gasMeters);
    }
    
    /**
     * This is a helper field to keep track of the last object added to any of the meter lists.
     * @param lastProcessed
     */
    public void setLastProcessed(Object lastProcessed) {
        this.lastProcessed = lastProcessed;
    }
    
    public Object getLastProcessed() {
        return lastProcessed;
    }
    
    public int getSize() {
        return getElectricMeters().size() + getWaterMeters().size();
    }
}
