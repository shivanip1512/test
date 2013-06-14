package com.cannontech.user;

import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public final class UserUtils {

    public static final int USER_ADMIN_ID = -1;
    public static final int USER_YUKON_ID = -2;
    public static final int USER_DEFAULTCTI_ID = -100;
    public static final int USER_NONE_ID = -9999;

    /**
     * True if id matches user for admin, yukon, or 'none' user
     */
    public static boolean isReservedUserId(int id) {
        return id == USER_ADMIN_ID
                || id == USER_YUKON_ID
                || id == USER_NONE_ID
                || id == USER_DEFAULTCTI_ID;
    }

    public static LiteYukonUser getYukonUser() {
        return new LiteYukonUser(USER_YUKON_ID, "yukon", LoginStatusEnum.ENABLED);
    }

    public static LiteYukonUser getAdminYukonUser() {
        return new LiteYukonUser(USER_ADMIN_ID, "admin", LoginStatusEnum.ENABLED);
    }
}