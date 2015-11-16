package com.cannontech.web.login;

import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface SessionInitializer {

    public void initSession(LiteYukonUser user, HttpSession session);
    
}
