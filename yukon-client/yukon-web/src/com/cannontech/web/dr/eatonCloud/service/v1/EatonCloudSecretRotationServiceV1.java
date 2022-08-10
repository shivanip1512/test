package com.cannontech.web.dr.eatonCloud.service.v1;

import org.joda.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.web.dr.eatonCloud.model.EatonCloudSecretExpiryTime;

public interface EatonCloudSecretRotationServiceV1 {

    /**
     * @return expiry times for secret 1 and 2, expiry time for one or both secrets can be null if secret doesn't exist
     * @throws EatonCloudCommunicationExceptionV1
     */
    EatonCloudSecretExpiryTime getSecretExpiryTime();

    /**
     * @param secretNumber - 1 to rotate secret one, 2 to rotate secret 2
     * @return new expiry time for the rotated secret
     * @throws EatonCloudCommunicationExceptionV1
     */
    Instant rotateSecret(int secretNumber, LiteYukonUser user);
    
    /**
     * Returns true if a secret rotation is allowed at the current time. Rotating secrets too often can result in rate limit errors.
     * @param secretNumber the secret number that you are checking
     * @return true if a secret rotation can be performed now, false if a secret rotation should wait
     */
    Boolean secretRotationAllowed(int secretNumber);
    
    /**
     * Returns the time that the next secret rotation will be allowed at. Will not persist properly across restarts so DO NOT TRY AND INFER THE TIME OF THE LAST ROTATION FROM THIS. 
     * @param secretNumber The secret number that you want to know the next allowed time of rotation for
     * @return An instant with the time that the next rotation will be allowed at. 
     */
    Instant timeNextRotationAllowed(int secretNumber);
}
