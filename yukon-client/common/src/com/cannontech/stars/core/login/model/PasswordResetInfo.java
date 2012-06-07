package com.cannontech.stars.core.login.model;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.UserUtils;

public class PasswordResetInfo{
    private LiteYukonUser user;
    private LiteContact contact;
    
    public LiteYukonUser getUser() {
        return user;
    }
    public void setUser(LiteYukonUser user) {
        this.user = user;
    }

    public LiteContact getContact() {
        return contact;
    }
    public void setContact(LiteContact contact) {
        this.contact = contact;
    }

    public boolean isValidUser() {
        if (user == null || user.getUserID() == UserUtils.USER_DEFAULT_ID) {
            return false;
        }

        return true;
    }
    
    public boolean isValidContact() {
        return contact != null;
    }
    
    public boolean isPasswordResetInfoValid() {
        return  (isValidUser() && isValidContact());
    }
}