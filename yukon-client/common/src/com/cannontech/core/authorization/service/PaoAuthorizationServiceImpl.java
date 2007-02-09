package com.cannontech.core.authorization.service;

import java.util.List;

import com.cannontech.core.authorization.support.DeviceAuthorization;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.PaoPermission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service class for determining device authorization
 */
public class PaoAuthorizationServiceImpl implements PaoAuthorizationService {

    private List<DeviceAuthorization> authorizationList = null;

    public boolean isAuthorized(LiteYukonUser user, PaoPermission permission, LiteYukonPAObject device) {

        for (DeviceAuthorization authorization : authorizationList) {

            AuthorizationResponse response = authorization.isAuthorized(user, permission, device);
            if (AuthorizationResponse.UNKNOWN.equals(response)) {
                continue;
            } else if (AuthorizationResponse.AUTHORIZED.equals(response)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<DeviceAuthorization> getAuthorizationList() {
        return authorizationList;
    }

    public void setAuthorizationList(List<DeviceAuthorization> authorizationList) {
        this.authorizationList = authorizationList;
    }

}
