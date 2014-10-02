package com.cannontech.core.dao;

import java.util.TimeZone;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface AuthDao {

    /**
     * Return true if the use has access to the given PAOid.
     * By default, the given user has access to ALL PAOS (backwards compatability)
     *
     * @param LiteYukonUser, int
     * @return boolean
     */
    public boolean userHasAccessPAO(LiteYukonUser user, int paoID);

    /**
     * Attempts to log a voice user into the system using the
     * given ContactID and pin. If the PIN is matched to any PIN
     * the contact hast, then we are logged in. Returns null if
     * we are not logged in. This method is CASE SENSITIVE for the
     * PIN (however, most of the time the PIN will be numeric only!)
     *
     */
    public LiteYukonUser voiceLogin(int contactid, String pin);

    public String getFirstNotificationPin(LiteContact contact);

    /**
     * This method returns the TimeZone for the given user.
     * First, the YUkonRoleProperty.TIMEZONE role property is checked, if found return
     * else if isBlank, then the ConfigurationRole.DEFAULT_TIMEZONE role property is checked, if found return
     * else if isBlank, then return the server timezone.
     * Throws BadConfigurationException when timeZone string value is not valid.
     * Throws IllegalArgumentException when user is null.
     *
     * @param user
     * @return
     */
    public TimeZone getUserTimeZone(LiteYukonUser user) throws BadConfigurationException, IllegalArgumentException;
}