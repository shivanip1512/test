package com.cannontech.services.eatonCloud.authToken.service;

import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;
import com.cannontech.system.GlobalSettingType;

public interface EatonCloudAuthTokenServiceV1 {

    EatonCloudTokenV1 retrieveNewToken(GlobalSettingType type, String serviceAccountId) throws EatonCloudCommunicationExceptionV1;

}
