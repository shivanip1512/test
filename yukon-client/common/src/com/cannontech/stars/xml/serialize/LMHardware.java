package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class LMHardware {
    private int routeID;
    private boolean hasRouteID;
    private String manufacturerSerialNumber;
    private Vector<StarsLMHardwareConfig> starsLMHardwareConfigList;
    private StarsThermostatSettings starsThermostatSettings;
    private StarsLMConfiguration starsLMConfiguration;

    public LMHardware() {
        starsLMHardwareConfigList = new Vector<StarsLMHardwareConfig>();
    }

    public void addStarsLMHardwareConfig(StarsLMHardwareConfig vStarsLMHardwareConfig) {
        starsLMHardwareConfigList.addElement(vStarsLMHardwareConfig);
    }

    public void addStarsLMHardwareConfig(int index, StarsLMHardwareConfig vStarsLMHardwareConfig) {
        starsLMHardwareConfigList.insertElementAt(vStarsLMHardwareConfig, index);
    }

    public void deleteRouteID() {
        this.hasRouteID = false;
    }

    public Enumeration<StarsLMHardwareConfig> enumerateStarsLMHardwareConfig() {
        return starsLMHardwareConfigList.elements();
    } 

    public String getManufacturerSerialNumber() {
        return this.manufacturerSerialNumber;
    } 

    public int getRouteID() {
        return this.routeID;
    }

    public StarsLMConfiguration getStarsLMConfiguration() {
        return this.starsLMConfiguration;
    }

    public StarsLMHardwareConfig getStarsLMHardwareConfig(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > starsLMHardwareConfigList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return starsLMHardwareConfigList.elementAt(index);
    }

    public StarsLMHardwareConfig[] getStarsLMHardwareConfig() {
        int size = starsLMHardwareConfigList.size();
        StarsLMHardwareConfig[] mArray = new StarsLMHardwareConfig[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = starsLMHardwareConfigList.elementAt(index);
        }
        return mArray;
    }

    public int getStarsLMHardwareConfigCount() {
        return starsLMHardwareConfigList.size();
    }

    public StarsThermostatSettings getStarsThermostatSettings() {
        return this.starsThermostatSettings;
    }

    public boolean hasRouteID() {
        return this.hasRouteID;
    }

    public void removeAllStarsLMHardwareConfig() {
        starsLMHardwareConfigList.removeAllElements();
    }

    public StarsLMHardwareConfig removeStarsLMHardwareConfig(int index) {
        java.lang.Object obj = starsLMHardwareConfigList.elementAt(index);
        starsLMHardwareConfigList.removeElementAt(index);
        return (StarsLMHardwareConfig) obj;
    }

    public void setManufacturerSerialNumber(java.lang.String manufacturerSerialNumber) {
        this.manufacturerSerialNumber = manufacturerSerialNumber;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
        this.hasRouteID = true;
    }

    public void setStarsLMConfiguration(StarsLMConfiguration starsLMConfiguration) {
        this.starsLMConfiguration = starsLMConfiguration;
    }

    public void setStarsLMHardwareConfig(int index, StarsLMHardwareConfig vStarsLMHardwareConfig) {
        //-- check bounds for index
        if ((index < 0) || (index > starsLMHardwareConfigList.size())) {
            throw new IndexOutOfBoundsException();
        }
        starsLMHardwareConfigList.setElementAt(vStarsLMHardwareConfig, index);
    }

    public void setStarsLMHardwareConfig(StarsLMHardwareConfig[] starsLMHardwareConfigArray) {
        //-- copy array
        starsLMHardwareConfigList.removeAllElements();
        for (int i = 0; i < starsLMHardwareConfigArray.length; i++) {
            starsLMHardwareConfigList.addElement(starsLMHardwareConfigArray[i]);
        }
    }

    public void setStarsThermostatSettings(StarsThermostatSettings starsThermostatSettings) {
        this.starsThermostatSettings = starsThermostatSettings;
    }

}
