package com.cannontech.common.user;

import org.joda.time.Instant;

import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;

public class UserAuthenticationInfo {
    private final int userId;
    private final AuthType authType;
    private final AuthenticationCategory authenticationCategory;
    private final Instant lastChangedDate;

    public UserAuthenticationInfo(int userId, AuthType authType, Instant lastChangedDate) {
        this.userId = userId;
        this.authType = authType;
        this.authenticationCategory = AuthenticationCategory.getByAuthType(authType);
        this.lastChangedDate = lastChangedDate;
    }

    public int getUserId() {
        return userId;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public AuthenticationCategory getAuthenticationCategory() {
        return authenticationCategory;
    }

    public Instant getLastChangedDate() {
        return lastChangedDate;
    }
}
