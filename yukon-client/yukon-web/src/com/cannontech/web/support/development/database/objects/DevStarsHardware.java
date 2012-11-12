package com.cannontech.web.support.development.database.objects;

import java.util.List;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.google.common.collect.Lists;

public class DevStarsHardware {
    private Integer serialNumMin = 100000;
    private Integer serialNumMax = 999999999;
    private Integer numPerAccount = 1;
    private Integer numExtra = 10;
    private List<DevHardwareType> hardwareTypes = getAllHardwareTypes();
    private List<Hardware> hardware = Lists.newArrayList();

    public Integer getSerialNumMin() {
        return serialNumMin;
    }
    public void setSerialNumMin(Integer serialNumMin) {
        this.serialNumMin = serialNumMin;
    }
    public Integer getSerialNumMax() {
        return serialNumMax;
    }
    public void setSerialNumMax(Integer serialNumMax) {
        this.serialNumMax = serialNumMax;
    }
    public Integer getNumPerAccount() {
        return numPerAccount;
    }
    public void setNumPerAccount(Integer numPerAccount) {
        this.numPerAccount = numPerAccount;
    }
    public Integer getNumExtra() {
        return numExtra;
    }
    public void setNumExtra(Integer numExtra) {
        this.numExtra = numExtra;
    }
    public List<Hardware> getHardware() {
        return hardware;
    }
    public void setHardware(List<Hardware> hardware) {
        this.hardware = hardware;
    }
    public List<DevHardwareType> getHardwareTypes() {
        return hardwareTypes;
    }
    public void setHardwareTypes(List<DevHardwareType> hardwareTypes) {
        this.hardwareTypes = hardwareTypes;
    }
    public Integer getNumTypesToCreate() {
        Integer numTypesToCreate = 0;
        for(DevHardwareType devHardwareType: hardwareTypes) {
            if (devHardwareType.isCreate()) {
                numTypesToCreate++;
            }
        }
        return numTypesToCreate;
    }
    public Integer getNumHardwarePerAccount() {
        Integer numAccountHardwareToCreate = getNumTypesToCreate() * numPerAccount;
        return numAccountHardwareToCreate;
    }
    public Integer getNumExtraTotal() {
        Integer numExtraToCreate = getNumTypesToCreate() * numExtra;
        return numExtraToCreate;
    }
    public Integer getNumToCreate() {
        int numExtraToCreate = getNumExtraTotal();
        int result = getNumHardwarePerAccount() + numExtraToCreate;
        return result;
    }
    private List<DevHardwareType> getAllHardwareTypes() {
        List<DevHardwareType> hardwareTypes = Lists.newArrayList();
        for (HardwareType hardwareType: HardwareType.values()) {
            if ((hardwareType.isThermostat() || hardwareType.isSwitch())
                && hardwareType != HardwareType.UTILITY_PRO_ZIGBEE) {
                hardwareTypes.add(new DevHardwareType(hardwareType));
            }
        }
        return hardwareTypes;
    }
    public void setHardwareTypeCreate(Iterable<HardwareType> typesToCreate, boolean create) {
        for (HardwareType hwType : typesToCreate) {
            setHardwareTypeCreate(hwType, create);
        }
    }
    public void setHardwareTypeCreate(HardwareType type, boolean create) {
        for (DevHardwareType devType : hardwareTypes) {
            if (devType.getHardwareType() == type) {
                devType.setCreate(create);
                return;
            }
        }
    }
    
}
