package com.cannontech.web.user.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;

/**
 * TODO FIXME:  MOVE ContactDto into a more common package, eg. com.cannontech.web.common >>> SEE ROSS
 * 
 * 
 * @see com.cannontech.web.common.ContactDto
 */
public class UserProfile {

    private ContactDto contact;
    private int userId;
    private String username;
    private List<ContactNotificationDto> contactNotifications = new ArrayList<>();

    public UserProfile() {}

    public UserProfile(LiteYukonUser user) {
        userId = user.getUserID();
        username = user.getUsername();
    }

    public UserProfile(LiteYukonUser user, LiteContact contact, List<LiteContactNotification> notifs) {
        this(user);
        initContact(contact);
        initContactNotifications(notifs);
    }

    public UserProfile(LiteYukonUser user, ContactDto contactDto, List<LiteContactNotification> notifs) {
        this(user);
        this.contact = contactDto;
        initContactNotifications(notifs);
    }

    public void initContact(LiteContact value) {
        if(value == null ) {
            return;
        }
        if(this.contact == null) {
            this.contact = new ContactDto();
        }
        this.contact.setContactId(value.getContactID());
        this.contact.setFirstName(value.getContFirstName());
        this.contact.setLastName(value.getContLastName());
        this.contact.setPrimary(true);
    }

    public void initContactNotifications(List<LiteContactNotification> notifs) {
        if(notifs == null || notifs.isEmpty()) {
            return;
        }
        if(this.contact == null) {
            this.contact = new ContactDto();
        }
        String email = null;
        for(LiteContactNotification notif : notifs) {
            if(email == null && notif.getContactNotificationType() == ContactNotificationType.EMAIL) {
                email = notif.getNotification();
                continue;
            }
            ContactNotificationDto dto = new ContactNotificationDto();
            dto.setNotificationId(notif.getContactNotifID());
            dto.setContactNotificationType(notif.getContactNotificationType());
            dto.setNotificationValue(notif.getNotification());
            contactNotifications.add(dto);
        }
        this.contact.setEmail(email);
    }
    

    public List<ContactNotificationDto> getContactNotifications() {
        return contactNotifications;
    }

    public void setContactNotifications(List<ContactNotificationDto> contactNotifications) {
        this.contactNotifications = contactNotifications;
    }

    public ContactDto getContact() {
        return contact;
    }
    
    public void setContact(ContactDto contact) {
        this.contact = contact;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
}