package com.cannontech.simulators.eatonCloud.model;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;

public abstract class EatonCloudDataGenerator {

    protected int status = HttpStatus.OK.value();
    protected int successPercentage = 100;
    protected EatonCloudSimulatorDeviceCreateRequest createRequest;

    protected Map<PaoType, HardwareType> paoTypeToHardware = Map.of(PaoType.LCR6600C, HardwareType.LCR_6600C, PaoType.LCR6200C,
            HardwareType.LCR_6200C);
 
    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreateRequest(EatonCloudSimulatorDeviceCreateRequest request) {
        this.createRequest = request;
    }

    public void setSuccessPercentage(int successPercentage) {
        this.successPercentage = successPercentage;
    }
    
    public abstract EatonCloudDataGenerator getDataGenerator(EatonCloudVersion version);
}
