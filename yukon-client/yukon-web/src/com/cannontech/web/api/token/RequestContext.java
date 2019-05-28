package com.cannontech.web.api.token;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This class hold the liteYukonUser based on the  JWT token to pass on various intercepter.
 * Example : liteYukonUser will be used in Rest controller and security intercepter (Roles etc) 
 */
public class RequestContext {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    LiteYukonUser liteYukonUser;
    public static RequestContext getContext() {
        RequestContext result = CONTEXT.get();

        if (result == null) {
            result = new RequestContext();
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
