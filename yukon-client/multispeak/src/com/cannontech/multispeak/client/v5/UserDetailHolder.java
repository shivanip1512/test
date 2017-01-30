package com.cannontech.multispeak.client.v5;

import com.cannontech.database.data.lite.LiteYukonUser;

public class UserDetailHolder {
    private static ThreadLocal<LiteYukonUser> threadLocal =

    new ThreadLocal<LiteYukonUser>() {
        @Override
        protected LiteYukonUser initialValue() {
            return null;
        }

    };

    private UserDetailHolder() {
    }

    public static LiteYukonUser getYukonUser() {
        return threadLocal.get();
    }

    public static void setYukonUser(LiteYukonUser user) {
        threadLocal.set(user);
    }

    public static void removeYukonUser() {
        threadLocal.remove();
    }
}
