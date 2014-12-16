package com.cannontech.database.data.lite;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LazyList;
import com.cannontech.user.UserUtils;

public class LiteContact extends LiteBase {
    private String contFirstName = null;
    private String contLastName = null;
    private int loginID = UserUtils.USER_YUKON_ID;
    private int addressID = CtiUtilities.NONE_ZERO_ID;

    private List<LiteContactNotification> liteContactNotifications = LazyList.ofInstance(LiteContactNotification.class);

    public LiteContact(int contactId) {
        super();
        setContactID(contactId);
        setLiteType(LiteTypes.CONTACT);
    }

    public LiteContact(int contactId, String firstName, String lastName) {
        this(contactId);
        setContFirstName(firstName);
        setContLastName(lastName);
    }

    public LiteContact(int contactId, String firstName, String lastName, int loginId) {
        this(contactId);
        setContFirstName(firstName);
        setContLastName(lastName);
        setLoginID(loginId);
    }

    public LiteContact(int contactId, String firstName, String lastName, int loginId, int addressId) {
        this(contactId);
        setContFirstName(firstName);
        setContLastName(lastName);
        setLoginID(loginId);
        setAddressID(addressId);
    }

    public int getContactID() {
        return getLiteID();
    }

    public java.lang.String getContFirstName() {
        return contFirstName;
    }

    public java.lang.String getContLastName() {
        return contLastName;
    }

    public void setContactID(int newContactID) {
        setLiteID(newContactID);
    }

    public void setContFirstName(java.lang.String newContFirstName) {
        contFirstName = newContFirstName;
    }

    public void setContLastName(java.lang.String newContLastName) {
        contLastName = newContLastName;
    }

    @Override
    public String toString() {
        if (StringUtils.isBlank(getContLastName()) && StringUtils.isBlank(getContFirstName())) {
            return CtiUtilities.STRING_NONE + "," + CtiUtilities.STRING_NONE;
        }

        if (StringUtils.isBlank(getContLastName())) {
            return getContFirstName();
        } else {
            if (StringUtils.isBlank(getContFirstName())) {
                return getContLastName();
            } else {
                return getContLastName() + ", " + getContFirstName();
            }
        }
    }

    public int getLoginID() {
        return loginID;
    }

    public void setLoginID(int loginID_) {
        this.loginID = loginID_;
    }

    public int getAddressID() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    /**
     * Returns the liteContactNotifications.
     */
    public List<LiteContactNotification> getLiteContactNotifications() {
        return liteContactNotifications;
    }

    public void setNotifications(List<LiteContactNotification> notificationList) {
        liteContactNotifications = notificationList;
    }
}
