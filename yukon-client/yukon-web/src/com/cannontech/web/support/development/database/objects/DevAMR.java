package com.cannontech.web.support.development.database.objects;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Lists;

public class DevAMR extends DevObject {
    private boolean createCartObjects = true;
    private boolean createRfnTemplates = true;
    private int routeId;
    private Integer numAdditionalMeters = 0;
    private Integer addressRangeMin = 1000000;
    private Integer addressRangeMax = 999999999;
    private List<DevPaoType> meterTypes =
        Lists
            .newArrayList(new DevPaoType(PaoType.MCT410CL),
                          new DevPaoType(PaoType.MCT410FL),
                          new DevPaoType(PaoType.MCT410IL),
                          new DevPaoType(PaoType.MCT420CL),
                          new DevPaoType(PaoType.MCT420FL),
                          new DevPaoType(PaoType.MCT430A),
                          new DevPaoType(PaoType.MCT430S4),
                          new DevPaoType(PaoType.MCT470),
                          new DevPaoType(PaoType.RFN420FL),
                          new DevPaoType(PaoType.RFN420FX),
                          new DevPaoType(PaoType.RFWMETER));
    
    public boolean isCreateRfnTemplates() {
        return createRfnTemplates;
    }

    public void setCreateRfnTemplates(boolean createRfnTemplates) {
        this.createRfnTemplates = createRfnTemplates;
    }

    public boolean isCreateCartObjects() {
        return createCartObjects;
    }

    public void setCreateCartObjects(boolean createCartObjects) {
        this.createCartObjects = createCartObjects;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public Integer getNumAdditionalMeters() {
        return numAdditionalMeters;
    }

    public void setNumAdditionalMeters(Integer numAdditionalMeters) {
        this.numAdditionalMeters = numAdditionalMeters;
    }
    public List<DevPaoType> getMeterTypes() {
        return meterTypes;
    }
    public void setMeterTypes(List<DevPaoType> meterTypes) {
        this.meterTypes = meterTypes;
    }

    public Integer getAddressRangeMin() {
        return addressRangeMin;
    }
    public void setAddressRangeMin(Integer addressRangeMin) {
        this.addressRangeMin = addressRangeMin;
    }
    public Integer getAddressRangeMax() {
        return addressRangeMax;
    }
    public void setAddressRangeMax(Integer addressRangeMax) {
        this.addressRangeMax = addressRangeMax;
    }
    @Override
    public int getTotal() {
        int total = getNumObjectsToCreate();
        if (createCartObjects) {
            total += DevMeter.values().length;
        }
        if (createRfnTemplates) {
            total += DevRfnTemplateMeter.values().length;
        }
        return total;
    }
    
    private int getNumObjectsToCreate() {
        int num = 0;
        for (DevPaoType meterType: meterTypes) {
            if (meterType.isCreate()) {
                num += numAdditionalMeters;
            }
        }
        return num;
    }
}
