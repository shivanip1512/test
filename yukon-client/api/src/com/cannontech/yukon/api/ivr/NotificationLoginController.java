package com.cannontech.yukon.api.ivr;

import java.io.IOException;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.yukon.INotifConnection;

@Controller
@RequestMapping("/notification/login/*")
public class NotificationLoginController {
    private AuthDao authDao;
    private ContactDao contactDao;
    private INotifConnection notifClientConnection;

    @RequestMapping
    public void pin(String callToken, Writer out) throws IOException {
        int contactId = notifClientConnection.requestMessageContactId(callToken);
        LiteContact contact = contactDao.getContact(contactId);
        String pin = authDao.getFirstNotificationPin(contact);
        out.write(pin);
    }

    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Autowired
    public void setNotifClientConnection(INotifConnection notifClientConnection) {
        this.notifClientConnection = notifClientConnection;
    }
}
