package com.cannontech.web.support.development.database.objects;

import java.util.List;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.google.common.collect.Lists;

public class DevStarsHardware {
    private int serialNumMin = 100000;
    private int serialNumMax = 999999999;
    private int numPerAccount = 1;
    private int numExtra = 10;
    private List<DevHardwareType> hardwareTypes = getAllHardwareTypes();
    private List<HardwareDto> hardware = Lists.newArrayList();

    public int getSerialNumMin() {
        return serialNumMin;
    }
    public void setSerialNumMin(int serialNumMin) {
        this.serialNumMin = serialNumMin;
    }
    public int getSerialNumMax() {
        return serialNumMax;
    }
    public void setSerialNumMax(int serialNumMax) {
        this.serialNumMax = serialNumMax;
    }
    public int getNumPerAccount() {
        return numPerAccount;
    }
    public void setNumPerAccount(int numPerAccount) {
        this.numPerAccount = numPerAccount;
    }
    public int getNumExtra() {
        return numExtra;
    }
    public void setNumExtra(int numExtra) {
        this.numExtra = numExtra;
    }
    public List<HardwareDto> getHardware() {
        return hardware;
    }
    public void setHardware(List<HardwareDto> hardware) {
        this.hardware = hardware;
    }
    public List<DevHardwareType> getHardwareTypes() {
        return hardwareTypes;
    }
    public void setHardwareTypes(List<DevHardwareType> hardwareTypes) {
        this.hardwareTypes = hardwareTypes;
    }
    public int getNumTypesToCreate() {
        int numTypesToCreate = 0;
        for(DevHardwareType devHardwareType: hardwareTypes) {
            if (devHardwareType.isCreate()) {
                numTypesToCreate++;
            }
        }
        return numTypesToCreate;
    }
    public int getNumHardwarePerAccount() {
        int numAccountHardwareToCreate = getNumTypesToCreate() * numPerAccount;
        return numAccountHardwareToCreate;
    }
    public int getNumExtraTotal() {
        int numExtraToCreate = getNumTypesToCreate() * numExtra;
        return numExtraToCreate;
    }
    public int getNumToCreate() {
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
    
}
