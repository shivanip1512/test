package com.cannontech.multispeak.data.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ArrayOfElectricMeter;
import com.cannontech.msp.beans.v4.ArrayOfGasMeter;
import com.cannontech.msp.beans.v4.ArrayOfWaterMeter;
import com.cannontech.msp.beans.v4.ElectricMeter;
import com.cannontech.msp.beans.v4.GasMeter;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.WaterMeter;
import com.cannontech.multispeak.data.MspReturnList;

public class MspMeterReturnList extends MspReturnList {

    private Meters meters;

    private Object lastProcessed;

    public Meters getMeters() {
        if (meters == null) {
            meters = new Meters();
            meters.setElectricMeters(new ArrayOfElectricMeter());
            meters.setWaterMeters(new ArrayOfWaterMeter());
            meters.setGasMeters(new ArrayOfGasMeter());
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
    
    public List<GasMeter> getGasMeters(){
        return getMeters().getGasMeters().getGasMeter();
    }
    
    public void setGasMeters(List<GasMeter> gasMeter) {
        getMeters().getGasMeters().getGasMeter().addAll(gasMeter);
    }

    /**
     * This is a helper field to keep track of the last object added to any of the meter lists.
     * 
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
