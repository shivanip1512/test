package com.cannontech.common.api.token;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This class hold the liteYukonUser based on the JWT token to pass on various intercepter.
 * Example : liteYukonUser will be used in Rest controller and security intercepter (Roles etc)
 */
public class ApiRequestContext {

    private static final ThreadLocal<ApiRequestContext> CONTEXT = ThreadLocal.withInitial(ApiRequestContext::new);
    LiteYukonUser liteYukonUser;

    public static ApiRequestContext getContext() {
        return CONTEXT.get();
    }

    public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
        this.liteYukonUser = liteYukonUser;
    }

    public LiteYukonUser getLiteYukonUser() {
        return liteYukonUser;
    }
}
