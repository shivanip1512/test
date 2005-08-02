/**
 * SpatialFeatureGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class SpatialFeatureGroup  extends com.cannontech.multispeak.MspPointObject  implements java.io.Serializable {
    private com.cannontech.multispeak.CapacitorBank[] capacitorBank;
    private com.cannontech.multispeak.LoadManagementDevice[] loadManagementDevice;
    private com.cannontech.multispeak.MeasurementDevice[] measurementDevice;
    private com.cannontech.multispeak.Meter[] meter;
    private com.cannontech.multispeak.OutageDetectionDevice[] outageDetectionDevice;
    private com.cannontech.multispeak.OvercurrentDeviceBank[] overcurrentDeviceBank;
    private com.cannontech.multispeak.Pole[] pole;
    private com.cannontech.multispeak.PowerSystemDevice[] powerSystemDevice;
    private com.cannontech.multispeak.RegulatorBank[] regulatorBank;
    private com.cannontech.multispeak.Riser[] riser;
    private com.cannontech.multispeak.ServiceLocation[] serviceLocation;
    private com.cannontech.multispeak.StreetLight[] streetLight;
    private com.cannontech.multispeak.Substation[] substation;
    private com.cannontech.multispeak.PrimaryCabinet[] primaryCabnet;
    private com.cannontech.multispeak.SwitchDeviceBank[] switchDeviceBank;
    private com.cannontech.multispeak.TransformerBank[] transformerBank;
    private com.cannontech.multispeak.OhPrimaryLine[] ohPrimaryLine;
    private com.cannontech.multispeak.OhSecondaryLine[] ohSecondaryLine;
    private com.cannontech.multispeak.UgPrimaryLine[] ugPrimaryLine;
    private com.cannontech.multispeak.UgSecondaryLine[] ugSecondaryLine;
    private com.cannontech.multispeak.SecondaryJunctionBox[] secondaryJunctionBox;

    public SpatialFeatureGroup() {
    }

    public SpatialFeatureGroup(
           com.cannontech.multispeak.CapacitorBank[] capacitorBank,
           com.cannontech.multispeak.LoadManagementDevice[] loadManagementDevice,
           com.cannontech.multispeak.MeasurementDevice[] measurementDevice,
           com.cannontech.multispeak.Meter[] meter,
           com.cannontech.multispeak.OutageDetectionDevice[] outageDetectionDevice,
           com.cannontech.multispeak.OvercurrentDeviceBank[] overcurrentDeviceBank,
           com.cannontech.multispeak.Pole[] pole,
           com.cannontech.multispeak.PowerSystemDevice[] powerSystemDevice,
           com.cannontech.multispeak.RegulatorBank[] regulatorBank,
           com.cannontech.multispeak.Riser[] riser,
           com.cannontech.multispeak.ServiceLocation[] serviceLocation,
           com.cannontech.multispeak.StreetLight[] streetLight,
           com.cannontech.multispeak.Substation[] substation,
           com.cannontech.multispeak.PrimaryCabinet[] primaryCabnet,
           com.cannontech.multispeak.SwitchDeviceBank[] switchDeviceBank,
           com.cannontech.multispeak.TransformerBank[] transformerBank,
           com.cannontech.multispeak.OhPrimaryLine[] ohPrimaryLine,
           com.cannontech.multispeak.OhSecondaryLine[] ohSecondaryLine,
           com.cannontech.multispeak.UgPrimaryLine[] ugPrimaryLine,
           com.cannontech.multispeak.UgSecondaryLine[] ugSecondaryLine,
           com.cannontech.multispeak.SecondaryJunctionBox[] secondaryJunctionBox) {
           this.capacitorBank = capacitorBank;
           this.loadManagementDevice = loadManagementDevice;
           this.measurementDevice = measurementDevice;
           this.meter = meter;
           this.outageDetectionDevice = outageDetectionDevice;
           this.overcurrentDeviceBank = overcurrentDeviceBank;
           this.pole = pole;
           this.powerSystemDevice = powerSystemDevice;
           this.regulatorBank = regulatorBank;
           this.riser = riser;
           this.serviceLocation = serviceLocation;
           this.streetLight = streetLight;
           this.substation = substation;
           this.primaryCabnet = primaryCabnet;
           this.switchDeviceBank = switchDeviceBank;
           this.transformerBank = transformerBank;
           this.ohPrimaryLine = ohPrimaryLine;
           this.ohSecondaryLine = ohSecondaryLine;
           this.ugPrimaryLine = ugPrimaryLine;
           this.ugSecondaryLine = ugSecondaryLine;
           this.secondaryJunctionBox = secondaryJunctionBox;
    }


    /**
     * Gets the capacitorBank value for this SpatialFeatureGroup.
     * 
     * @return capacitorBank
     */
    public com.cannontech.multispeak.CapacitorBank[] getCapacitorBank() {
        return capacitorBank;
    }


    /**
     * Sets the capacitorBank value for this SpatialFeatureGroup.
     * 
     * @param capacitorBank
     */
    public void setCapacitorBank(com.cannontech.multispeak.CapacitorBank[] capacitorBank) {
        this.capacitorBank = capacitorBank;
    }

    public com.cannontech.multispeak.CapacitorBank getCapacitorBank(int i) {
        return this.capacitorBank[i];
    }

    public void setCapacitorBank(int i, com.cannontech.multispeak.CapacitorBank _value) {
        this.capacitorBank[i] = _value;
    }


    /**
     * Gets the loadManagementDevice value for this SpatialFeatureGroup.
     * 
     * @return loadManagementDevice
     */
    public com.cannontech.multispeak.LoadManagementDevice[] getLoadManagementDevice() {
        return loadManagementDevice;
    }


    /**
     * Sets the loadManagementDevice value for this SpatialFeatureGroup.
     * 
     * @param loadManagementDevice
     */
    public void setLoadManagementDevice(com.cannontech.multispeak.LoadManagementDevice[] loadManagementDevice) {
        this.loadManagementDevice = loadManagementDevice;
    }

    public com.cannontech.multispeak.LoadManagementDevice getLoadManagementDevice(int i) {
        return this.loadManagementDevice[i];
    }

    public void setLoadManagementDevice(int i, com.cannontech.multispeak.LoadManagementDevice _value) {
        this.loadManagementDevice[i] = _value;
    }


    /**
     * Gets the measurementDevice value for this SpatialFeatureGroup.
     * 
     * @return measurementDevice
     */
    public com.cannontech.multispeak.MeasurementDevice[] getMeasurementDevice() {
        return measurementDevice;
    }


    /**
     * Sets the measurementDevice value for this SpatialFeatureGroup.
     * 
     * @param measurementDevice
     */
    public void setMeasurementDevice(com.cannontech.multispeak.MeasurementDevice[] measurementDevice) {
        this.measurementDevice = measurementDevice;
    }

    public com.cannontech.multispeak.MeasurementDevice getMeasurementDevice(int i) {
        return this.measurementDevice[i];
    }

    public void setMeasurementDevice(int i, com.cannontech.multispeak.MeasurementDevice _value) {
        this.measurementDevice[i] = _value;
    }


    /**
     * Gets the meter value for this SpatialFeatureGroup.
     * 
     * @return meter
     */
    public com.cannontech.multispeak.Meter[] getMeter() {
        return meter;
    }


    /**
     * Sets the meter value for this SpatialFeatureGroup.
     * 
     * @param meter
     */
    public void setMeter(com.cannontech.multispeak.Meter[] meter) {
        this.meter = meter;
    }

    public com.cannontech.multispeak.Meter getMeter(int i) {
        return this.meter[i];
    }

    public void setMeter(int i, com.cannontech.multispeak.Meter _value) {
        this.meter[i] = _value;
    }


    /**
     * Gets the outageDetectionDevice value for this SpatialFeatureGroup.
     * 
     * @return outageDetectionDevice
     */
    public com.cannontech.multispeak.OutageDetectionDevice[] getOutageDetectionDevice() {
        return outageDetectionDevice;
    }


    /**
     * Sets the outageDetectionDevice value for this SpatialFeatureGroup.
     * 
     * @param outageDetectionDevice
     */
    public void setOutageDetectionDevice(com.cannontech.multispeak.OutageDetectionDevice[] outageDetectionDevice) {
        this.outageDetectionDevice = outageDetectionDevice;
    }

    public com.cannontech.multispeak.OutageDetectionDevice getOutageDetectionDevice(int i) {
        return this.outageDetectionDevice[i];
    }

    public void setOutageDetectionDevice(int i, com.cannontech.multispeak.OutageDetectionDevice _value) {
        this.outageDetectionDevice[i] = _value;
    }


    /**
     * Gets the overcurrentDeviceBank value for this SpatialFeatureGroup.
     * 
     * @return overcurrentDeviceBank
     */
    public com.cannontech.multispeak.OvercurrentDeviceBank[] getOvercurrentDeviceBank() {
        return overcurrentDeviceBank;
    }


    /**
     * Sets the overcurrentDeviceBank value for this SpatialFeatureGroup.
     * 
     * @param overcurrentDeviceBank
     */
    public void setOvercurrentDeviceBank(com.cannontech.multispeak.OvercurrentDeviceBank[] overcurrentDeviceBank) {
        this.overcurrentDeviceBank = overcurrentDeviceBank;
    }

    public com.cannontech.multispeak.OvercurrentDeviceBank getOvercurrentDeviceBank(int i) {
        return this.overcurrentDeviceBank[i];
    }

    public void setOvercurrentDeviceBank(int i, com.cannontech.multispeak.OvercurrentDeviceBank _value) {
        this.overcurrentDeviceBank[i] = _value;
    }


    /**
     * Gets the pole value for this SpatialFeatureGroup.
     * 
     * @return pole
     */
    public com.cannontech.multispeak.Pole[] getPole() {
        return pole;
    }


    /**
     * Sets the pole value for this SpatialFeatureGroup.
     * 
     * @param pole
     */
    public void setPole(com.cannontech.multispeak.Pole[] pole) {
        this.pole = pole;
    }

    public com.cannontech.multispeak.Pole getPole(int i) {
        return this.pole[i];
    }

    public void setPole(int i, com.cannontech.multispeak.Pole _value) {
        this.pole[i] = _value;
    }


    /**
     * Gets the powerSystemDevice value for this SpatialFeatureGroup.
     * 
     * @return powerSystemDevice
     */
    public com.cannontech.multispeak.PowerSystemDevice[] getPowerSystemDevice() {
        return powerSystemDevice;
    }


    /**
     * Sets the powerSystemDevice value for this SpatialFeatureGroup.
     * 
     * @param powerSystemDevice
     */
    public void setPowerSystemDevice(com.cannontech.multispeak.PowerSystemDevice[] powerSystemDevice) {
        this.powerSystemDevice = powerSystemDevice;
    }

    public com.cannontech.multispeak.PowerSystemDevice getPowerSystemDevice(int i) {
        return this.powerSystemDevice[i];
    }

    public void setPowerSystemDevice(int i, com.cannontech.multispeak.PowerSystemDevice _value) {
        this.powerSystemDevice[i] = _value;
    }


    /**
     * Gets the regulatorBank value for this SpatialFeatureGroup.
     * 
     * @return regulatorBank
     */
    public com.cannontech.multispeak.RegulatorBank[] getRegulatorBank() {
        return regulatorBank;
    }


    /**
     * Sets the regulatorBank value for this SpatialFeatureGroup.
     * 
     * @param regulatorBank
     */
    public void setRegulatorBank(com.cannontech.multispeak.RegulatorBank[] regulatorBank) {
        this.regulatorBank = regulatorBank;
    }

    public com.cannontech.multispeak.RegulatorBank getRegulatorBank(int i) {
        return this.regulatorBank[i];
    }

    public void setRegulatorBank(int i, com.cannontech.multispeak.RegulatorBank _value) {
        this.regulatorBank[i] = _value;
    }


    /**
     * Gets the riser value for this SpatialFeatureGroup.
     * 
     * @return riser
     */
    public com.cannontech.multispeak.Riser[] getRiser() {
        return riser;
    }


    /**
     * Sets the riser value for this SpatialFeatureGroup.
     * 
     * @param riser
     */
    public void setRiser(com.cannontech.multispeak.Riser[] riser) {
        this.riser = riser;
    }

    public com.cannontech.multispeak.Riser getRiser(int i) {
        return this.riser[i];
    }

    public void setRiser(int i, com.cannontech.multispeak.Riser _value) {
        this.riser[i] = _value;
    }


    /**
     * Gets the serviceLocation value for this SpatialFeatureGroup.
     * 
     * @return serviceLocation
     */
    public com.cannontech.multispeak.ServiceLocation[] getServiceLocation() {
        return serviceLocation;
    }


    /**
     * Sets the serviceLocation value for this SpatialFeatureGroup.
     * 
     * @param serviceLocation
     */
    public void setServiceLocation(com.cannontech.multispeak.ServiceLocation[] serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public com.cannontech.multispeak.ServiceLocation getServiceLocation(int i) {
        return this.serviceLocation[i];
    }

    public void setServiceLocation(int i, com.cannontech.multispeak.ServiceLocation _value) {
        this.serviceLocation[i] = _value;
    }


    /**
     * Gets the streetLight value for this SpatialFeatureGroup.
     * 
     * @return streetLight
     */
    public com.cannontech.multispeak.StreetLight[] getStreetLight() {
        return streetLight;
    }


    /**
     * Sets the streetLight value for this SpatialFeatureGroup.
     * 
     * @param streetLight
     */
    public void setStreetLight(com.cannontech.multispeak.StreetLight[] streetLight) {
        this.streetLight = streetLight;
    }

    public com.cannontech.multispeak.StreetLight getStreetLight(int i) {
        return this.streetLight[i];
    }

    public void setStreetLight(int i, com.cannontech.multispeak.StreetLight _value) {
        this.streetLight[i] = _value;
    }


    /**
     * Gets the substation value for this SpatialFeatureGroup.
     * 
     * @return substation
     */
    public com.cannontech.multispeak.Substation[] getSubstation() {
        return substation;
    }


    /**
     * Sets the substation value for this SpatialFeatureGroup.
     * 
     * @param substation
     */
    public void setSubstation(com.cannontech.multispeak.Substation[] substation) {
        this.substation = substation;
    }

    public com.cannontech.multispeak.Substation getSubstation(int i) {
        return this.substation[i];
    }

    public void setSubstation(int i, com.cannontech.multispeak.Substation _value) {
        this.substation[i] = _value;
    }


    /**
     * Gets the primaryCabnet value for this SpatialFeatureGroup.
     * 
     * @return primaryCabnet
     */
    public com.cannontech.multispeak.PrimaryCabinet[] getPrimaryCabnet() {
        return primaryCabnet;
    }


    /**
     * Sets the primaryCabnet value for this SpatialFeatureGroup.
     * 
     * @param primaryCabnet
     */
    public void setPrimaryCabnet(com.cannontech.multispeak.PrimaryCabinet[] primaryCabnet) {
        this.primaryCabnet = primaryCabnet;
    }

    public com.cannontech.multispeak.PrimaryCabinet getPrimaryCabnet(int i) {
        return this.primaryCabnet[i];
    }

    public void setPrimaryCabnet(int i, com.cannontech.multispeak.PrimaryCabinet _value) {
        this.primaryCabnet[i] = _value;
    }


    /**
     * Gets the switchDeviceBank value for this SpatialFeatureGroup.
     * 
     * @return switchDeviceBank
     */
    public com.cannontech.multispeak.SwitchDeviceBank[] getSwitchDeviceBank() {
        return switchDeviceBank;
    }


    /**
     * Sets the switchDeviceBank value for this SpatialFeatureGroup.
     * 
     * @param switchDeviceBank
     */
    public void setSwitchDeviceBank(com.cannontech.multispeak.SwitchDeviceBank[] switchDeviceBank) {
        this.switchDeviceBank = switchDeviceBank;
    }

    public com.cannontech.multispeak.SwitchDeviceBank getSwitchDeviceBank(int i) {
        return this.switchDeviceBank[i];
    }

    public void setSwitchDeviceBank(int i, com.cannontech.multispeak.SwitchDeviceBank _value) {
        this.switchDeviceBank[i] = _value;
    }


    /**
     * Gets the transformerBank value for this SpatialFeatureGroup.
     * 
     * @return transformerBank
     */
    public com.cannontech.multispeak.TransformerBank[] getTransformerBank() {
        return transformerBank;
    }


    /**
     * Sets the transformerBank value for this SpatialFeatureGroup.
     * 
     * @param transformerBank
     */
    public void setTransformerBank(com.cannontech.multispeak.TransformerBank[] transformerBank) {
        this.transformerBank = transformerBank;
    }

    public com.cannontech.multispeak.TransformerBank getTransformerBank(int i) {
        return this.transformerBank[i];
    }

    public void setTransformerBank(int i, com.cannontech.multispeak.TransformerBank _value) {
        this.transformerBank[i] = _value;
    }


    /**
     * Gets the ohPrimaryLine value for this SpatialFeatureGroup.
     * 
     * @return ohPrimaryLine
     */
    public com.cannontech.multispeak.OhPrimaryLine[] getOhPrimaryLine() {
        return ohPrimaryLine;
    }


    /**
     * Sets the ohPrimaryLine value for this SpatialFeatureGroup.
     * 
     * @param ohPrimaryLine
     */
    public void setOhPrimaryLine(com.cannontech.multispeak.OhPrimaryLine[] ohPrimaryLine) {
        this.ohPrimaryLine = ohPrimaryLine;
    }

    public com.cannontech.multispeak.OhPrimaryLine getOhPrimaryLine(int i) {
        return this.ohPrimaryLine[i];
    }

    public void setOhPrimaryLine(int i, com.cannontech.multispeak.OhPrimaryLine _value) {
        this.ohPrimaryLine[i] = _value;
    }


    /**
     * Gets the ohSecondaryLine value for this SpatialFeatureGroup.
     * 
     * @return ohSecondaryLine
     */
    public com.cannontech.multispeak.OhSecondaryLine[] getOhSecondaryLine() {
        return ohSecondaryLine;
    }


    /**
     * Sets the ohSecondaryLine value for this SpatialFeatureGroup.
     * 
     * @param ohSecondaryLine
     */
    public void setOhSecondaryLine(com.cannontech.multispeak.OhSecondaryLine[] ohSecondaryLine) {
        this.ohSecondaryLine = ohSecondaryLine;
    }

    public com.cannontech.multispeak.OhSecondaryLine getOhSecondaryLine(int i) {
        return this.ohSecondaryLine[i];
    }

    public void setOhSecondaryLine(int i, com.cannontech.multispeak.OhSecondaryLine _value) {
        this.ohSecondaryLine[i] = _value;
    }


    /**
     * Gets the ugPrimaryLine value for this SpatialFeatureGroup.
     * 
     * @return ugPrimaryLine
     */
    public com.cannontech.multispeak.UgPrimaryLine[] getUgPrimaryLine() {
        return ugPrimaryLine;
    }


    /**
     * Sets the ugPrimaryLine value for this SpatialFeatureGroup.
     * 
     * @param ugPrimaryLine
     */
    public void setUgPrimaryLine(com.cannontech.multispeak.UgPrimaryLine[] ugPrimaryLine) {
        this.ugPrimaryLine = ugPrimaryLine;
    }

    public com.cannontech.multispeak.UgPrimaryLine getUgPrimaryLine(int i) {
        return this.ugPrimaryLine[i];
    }

    public void setUgPrimaryLine(int i, com.cannontech.multispeak.UgPrimaryLine _value) {
        this.ugPrimaryLine[i] = _value;
    }


    /**
     * Gets the ugSecondaryLine value for this SpatialFeatureGroup.
     * 
     * @return ugSecondaryLine
     */
    public com.cannontech.multispeak.UgSecondaryLine[] getUgSecondaryLine() {
        return ugSecondaryLine;
    }


    /**
     * Sets the ugSecondaryLine value for this SpatialFeatureGroup.
     * 
     * @param ugSecondaryLine
     */
    public void setUgSecondaryLine(com.cannontech.multispeak.UgSecondaryLine[] ugSecondaryLine) {
        this.ugSecondaryLine = ugSecondaryLine;
    }

    public com.cannontech.multispeak.UgSecondaryLine getUgSecondaryLine(int i) {
        return this.ugSecondaryLine[i];
    }

    public void setUgSecondaryLine(int i, com.cannontech.multispeak.UgSecondaryLine _value) {
        this.ugSecondaryLine[i] = _value;
    }


    /**
     * Gets the secondaryJunctionBox value for this SpatialFeatureGroup.
     * 
     * @return secondaryJunctionBox
     */
    public com.cannontech.multispeak.SecondaryJunctionBox[] getSecondaryJunctionBox() {
        return secondaryJunctionBox;
    }


    /**
     * Sets the secondaryJunctionBox value for this SpatialFeatureGroup.
     * 
     * @param secondaryJunctionBox
     */
    public void setSecondaryJunctionBox(com.cannontech.multispeak.SecondaryJunctionBox[] secondaryJunctionBox) {
        this.secondaryJunctionBox = secondaryJunctionBox;
    }

    public com.cannontech.multispeak.SecondaryJunctionBox getSecondaryJunctionBox(int i) {
        return this.secondaryJunctionBox[i];
    }

    public void setSecondaryJunctionBox(int i, com.cannontech.multispeak.SecondaryJunctionBox _value) {
        this.secondaryJunctionBox[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SpatialFeatureGroup)) return false;
        SpatialFeatureGroup other = (SpatialFeatureGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.capacitorBank==null && other.getCapacitorBank()==null) || 
             (this.capacitorBank!=null &&
              java.util.Arrays.equals(this.capacitorBank, other.getCapacitorBank()))) &&
            ((this.loadManagementDevice==null && other.getLoadManagementDevice()==null) || 
             (this.loadManagementDevice!=null &&
              java.util.Arrays.equals(this.loadManagementDevice, other.getLoadManagementDevice()))) &&
            ((this.measurementDevice==null && other.getMeasurementDevice()==null) || 
             (this.measurementDevice!=null &&
              java.util.Arrays.equals(this.measurementDevice, other.getMeasurementDevice()))) &&
            ((this.meter==null && other.getMeter()==null) || 
             (this.meter!=null &&
              java.util.Arrays.equals(this.meter, other.getMeter()))) &&
            ((this.outageDetectionDevice==null && other.getOutageDetectionDevice()==null) || 
             (this.outageDetectionDevice!=null &&
              java.util.Arrays.equals(this.outageDetectionDevice, other.getOutageDetectionDevice()))) &&
            ((this.overcurrentDeviceBank==null && other.getOvercurrentDeviceBank()==null) || 
             (this.overcurrentDeviceBank!=null &&
              java.util.Arrays.equals(this.overcurrentDeviceBank, other.getOvercurrentDeviceBank()))) &&
            ((this.pole==null && other.getPole()==null) || 
             (this.pole!=null &&
              java.util.Arrays.equals(this.pole, other.getPole()))) &&
            ((this.powerSystemDevice==null && other.getPowerSystemDevice()==null) || 
             (this.powerSystemDevice!=null &&
              java.util.Arrays.equals(this.powerSystemDevice, other.getPowerSystemDevice()))) &&
            ((this.regulatorBank==null && other.getRegulatorBank()==null) || 
             (this.regulatorBank!=null &&
              java.util.Arrays.equals(this.regulatorBank, other.getRegulatorBank()))) &&
            ((this.riser==null && other.getRiser()==null) || 
             (this.riser!=null &&
              java.util.Arrays.equals(this.riser, other.getRiser()))) &&
            ((this.serviceLocation==null && other.getServiceLocation()==null) || 
             (this.serviceLocation!=null &&
              java.util.Arrays.equals(this.serviceLocation, other.getServiceLocation()))) &&
            ((this.streetLight==null && other.getStreetLight()==null) || 
             (this.streetLight!=null &&
              java.util.Arrays.equals(this.streetLight, other.getStreetLight()))) &&
            ((this.substation==null && other.getSubstation()==null) || 
             (this.substation!=null &&
              java.util.Arrays.equals(this.substation, other.getSubstation()))) &&
            ((this.primaryCabnet==null && other.getPrimaryCabnet()==null) || 
             (this.primaryCabnet!=null &&
              java.util.Arrays.equals(this.primaryCabnet, other.getPrimaryCabnet()))) &&
            ((this.switchDeviceBank==null && other.getSwitchDeviceBank()==null) || 
             (this.switchDeviceBank!=null &&
              java.util.Arrays.equals(this.switchDeviceBank, other.getSwitchDeviceBank()))) &&
            ((this.transformerBank==null && other.getTransformerBank()==null) || 
             (this.transformerBank!=null &&
              java.util.Arrays.equals(this.transformerBank, other.getTransformerBank()))) &&
            ((this.ohPrimaryLine==null && other.getOhPrimaryLine()==null) || 
             (this.ohPrimaryLine!=null &&
              java.util.Arrays.equals(this.ohPrimaryLine, other.getOhPrimaryLine()))) &&
            ((this.ohSecondaryLine==null && other.getOhSecondaryLine()==null) || 
             (this.ohSecondaryLine!=null &&
              java.util.Arrays.equals(this.ohSecondaryLine, other.getOhSecondaryLine()))) &&
            ((this.ugPrimaryLine==null && other.getUgPrimaryLine()==null) || 
             (this.ugPrimaryLine!=null &&
              java.util.Arrays.equals(this.ugPrimaryLine, other.getUgPrimaryLine()))) &&
            ((this.ugSecondaryLine==null && other.getUgSecondaryLine()==null) || 
             (this.ugSecondaryLine!=null &&
              java.util.Arrays.equals(this.ugSecondaryLine, other.getUgSecondaryLine()))) &&
            ((this.secondaryJunctionBox==null && other.getSecondaryJunctionBox()==null) || 
             (this.secondaryJunctionBox!=null &&
              java.util.Arrays.equals(this.secondaryJunctionBox, other.getSecondaryJunctionBox())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getCapacitorBank() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCapacitorBank());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCapacitorBank(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLoadManagementDevice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLoadManagementDevice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLoadManagementDevice(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMeasurementDevice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeasurementDevice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeasurementDevice(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMeter() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeter());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeter(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOutageDetectionDevice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOutageDetectionDevice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutageDetectionDevice(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOvercurrentDeviceBank() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOvercurrentDeviceBank());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOvercurrentDeviceBank(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPole() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPole());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPole(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPowerSystemDevice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPowerSystemDevice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPowerSystemDevice(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRegulatorBank() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegulatorBank());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRegulatorBank(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRiser() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRiser());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRiser(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getServiceLocation() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getServiceLocation());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getServiceLocation(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getStreetLight() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getStreetLight());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getStreetLight(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSubstation() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSubstation());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSubstation(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPrimaryCabnet() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPrimaryCabnet());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPrimaryCabnet(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSwitchDeviceBank() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSwitchDeviceBank());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSwitchDeviceBank(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTransformerBank() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTransformerBank());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTransformerBank(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOhPrimaryLine() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOhPrimaryLine());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOhPrimaryLine(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOhSecondaryLine() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOhSecondaryLine());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOhSecondaryLine(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getUgPrimaryLine() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUgPrimaryLine());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUgPrimaryLine(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getUgSecondaryLine() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUgSecondaryLine());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUgSecondaryLine(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSecondaryJunctionBox() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSecondaryJunctionBox());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSecondaryJunctionBox(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SpatialFeatureGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "spatialFeatureGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capacitorBank");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitorBank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitorBank"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadManagementDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectionDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("overcurrentDeviceBank");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pole");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("powerSystemDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerSystemDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerSystemDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regulatorBank");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorBank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorBank"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("riser");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "riser"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "riser"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetLight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "streetLight"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryCabnet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "primaryCabnet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "primaryCabinet"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("switchDeviceBank");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchDeviceBank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchDeviceBank"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transformerBank");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformerBank"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ohPrimaryLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohPrimaryLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohPrimaryLine"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ohSecondaryLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohSecondaryLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohSecondaryLine"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ugPrimaryLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugPrimaryLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugPrimaryLine"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ugSecondaryLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugSecondaryLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugSecondaryLine"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryJunctionBox");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secondaryJunctionBox"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secondaryJunctionBox"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
