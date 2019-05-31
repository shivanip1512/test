package com.cannontech.common.api.token;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This class hold the liteYukonUser based on the  JWT token to pass on various intercepter.
 * Example : liteYukonUser will be used in Rest controller and security intercepter (Roles etc) 
 */
public class APIRequestContext {

    private static final ThreadLocal<APIRequestContext> CONTEXT = new ThreadLocal<>();

    LiteYukonUser liteYukonUser;
    public static APIRequestContext getContext() {
        APIRequestContext result = CONTEXT.get();

        if (result == null) {
            result = new APIRequestContext();
            CONTEXT.set(result);
        }

        return result;
    }

    public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
        this.liteYukonUser = liteYukonUser;
    }

    public LiteYukonUser getLiteYukonUser() {
        return liteYukonUser;
    }
}
