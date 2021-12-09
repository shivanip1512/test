package com.cannontech.simulators.eatonCloud.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSecretV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudServiceAccountDetailV1;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public abstract class EatonCloudDataGenerator {

    protected int status = HttpStatus.OK.value();
    protected int successPercentage = 100;
    protected EatonCloudSimulatorDeviceCreateRequest createRequest;
    protected NextValueHelper nextValueHelper;
    protected EatonCloudServiceAccountDetailV1 account;

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

    public void setSettingDao(GlobalSettingDao settingDao) {
        if (account != null) {
            return;
        }
        String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);

        List<EatonCloudSecretV1> secrets = new ArrayList<>();
        secrets.add(new EatonCloudSecretV1("secret1", DateTime.now().toString()));
        secrets.add(new EatonCloudSecretV1("secret2", DateTime.now().toString()));
        account = new EatonCloudServiceAccountDetailV1(serviceAccountId, "", true, "", "", secrets, "", "", "", "");
    }
    
    public void setSuccessPercentage(int successPercentage) {
        this.successPercentage = successPercentage;
    }
}
