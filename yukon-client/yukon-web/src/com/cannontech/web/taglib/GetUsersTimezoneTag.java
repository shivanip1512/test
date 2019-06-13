package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;

@Configurable(value="getUsersTimezoneTagPrototype", autowire=Autowire.BY_NAME)
public class GetUsersTimezoneTag extends YukonTagSupport {
        
    @Autowired private AuthDao authDao;

    @Override
    public void doTag() throws IOException {
        try {
            LiteYukonUser user = getYukonUser();
            if (user != null) {
                TimeZone timeZone = authDao.getUserTimeZone(user);
                getJspContext().getOut().print(timeZone.getID());
            }
        } catch (NotLoggedInException e) {
            //not needed until user is logged in
        }

    }

}