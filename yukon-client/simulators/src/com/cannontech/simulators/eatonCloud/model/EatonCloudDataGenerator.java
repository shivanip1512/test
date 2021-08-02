package com.cannontech.simulators.eatonCloud.model;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;

public abstract class EatonCloudDataGenerator {

    protected int status = HttpStatus.OK.value();
    protected EatonCloudSimulatorDeviceCreateRequest createRequest;
    protected NextValueHelper nextValueHelper;
    protected Map<PaoType, HardwareType> paoTypeToHardware = Map.of(PaoType.LCR6600C, HardwareType.LCR_6600C, PaoType.LCR6200C,
            HardwareType.LCR_6200C);
 
    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreateRequest(EatonCloudSimulatorDeviceCreateRequest request) {
        this.createRequest = request;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
