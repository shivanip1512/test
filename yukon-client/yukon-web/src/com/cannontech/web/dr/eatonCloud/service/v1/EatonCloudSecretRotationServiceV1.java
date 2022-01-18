package com.cannontech.web.dr.eatonCloud.service.v1;

import org.joda.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.eatonCloud.model.EatonCloudException;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.web.dr.eatonCloud.model.EatonCloudSecretExpiryTime;

public interface EatonCloudSecretRotationServiceV1 {

    /**
     * @return expiry times for secret 1 and 2, expiry time for one or both secrets can be null if secret doesn't exist
     * @throws EatonCloudCommunicationExceptionV1
     * @throws EatonCloudException
     */
    EatonCloudSecretExpiryTime getSecretExpiryTime();

    /**
     * @param secretNumber - 1 to rotate secret one, 2 to rotate secret 2
     * @return new expiry time for the rotated secret
     * @throws EatonCloudCommunicationExceptionV1
     * @throws EatonCloudException
     */

    Instant rotateSecret(int secretNumber, LiteYukonUser user);
}
