package com.cannontech.database.data.lite;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.google.common.base.Function;

/**
 * This class represents the non-password related bits of a user. For the password-related bits, see
 * {@link AuthenticationService} and {@link UserAuthenticationInfo}.
 */
public class LiteYukonUser extends LiteBase {
    
    private String username;
    private LoginStatusEnum loginStatus;
    private boolean forceReset;
    private Integer userGroupId;
    
    /** 
     *  New YukonUsers should be created using the non-lite objects. Don't use this anymore for creating users!
     *    In Short, LiteYukonUser should NEVER have been used for a "create" object since it requires a liteId to exist.
     *    This only exists to support legacy YukonUserDao.save functionality.
     */
    @Deprecated public final static int CREATE_NEW_USER_ID = 0;
    
    public final static Function<LiteYukonUser, Integer> USER_ID_FUNCTION = new Function<LiteYukonUser, Integer>() {
        @Override
        public Integer apply(LiteYukonUser input) {
            return input.getLiteID();
        };
    };
    
    public LiteYukonUser() {
        
    }
    
    public LiteYukonUser(int id) {
        this(id,null,null);
    }
    
    public LiteYukonUser(int id, String username) {
        this(id, username, null);
    }
    
    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus) {
        this(id, username, loginStatus, false, null);
    }
    
    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus, boolean forceReset, Integer userGroupId) {
        setLiteType(LiteTypes.YUKON_USER);
        setLiteID(id);
        this.username = username;
        this.loginStatus = loginStatus;
        this.forceReset = forceReset;
        this.userGroupId = userGroupId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getUserID() {
        return getLiteID();
    }
    
    public void setUserID(int userID) {
        setLiteID(userID);
    }
    
    public LoginStatusEnum getLoginStatus() {
        return loginStatus;
    }
    
    public void setLoginStatus(LoginStatusEnum loginStatus) {
        this.loginStatus = loginStatus;
    }
    
    public boolean isForceReset() {
        return forceReset;
    }
    
    public void setForceReset(boolean forceReset) {
        this.forceReset = forceReset;
    }
    
    public Integer getUserGroupId() {
        return userGroupId;
    }
    
    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }
    
    @Override
    public String toString() {
        return getUsername();
    }
    
    public boolean isEnabled() {
        return loginStatus == LoginStatusEnum.ENABLED;
    }
}