package com.cannontech.development.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Multimap;

public class DevAmr extends DevObject {
    private boolean createCartObjects = true;
    private boolean createRfnTemplates = true;
    private int routeId;
    private Integer numAdditionalMeters = 0;
    private Integer addressRangeMin = 1000000;
    private Integer addressRangeMax = 999999999;
    private List<DevPaoType> meterTypes = new ArrayList<>();

    {
        PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);
        Multimap<String, PaoDefinition> creatableDevices = paoDefinitionService.getCreatablePaoDisplayGroupMap();
        for (PaoDefinition definition : creatableDevices.values()) {
            if (definition.getType().isRfMeter() || definition.getType().isMct()) {
                meterTypes.add(new DevPaoType(definition.getType()));
            }
        }
        meterTypes.add(new DevPaoType(PaoType.RFN_RELAY));
        meterTypes.add(new DevPaoType(PaoType.CRLY856));
    }
    
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
            total += RfnManufacturerModel.values().length;
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
