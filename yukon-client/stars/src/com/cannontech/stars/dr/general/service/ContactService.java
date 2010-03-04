package com.cannontech.stars.dr.general.service;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ContactService {

	/**
	 * Creates a new contact, saves it, process db change add.
	 * @param firstName First name of contact
	 * @param lastName Last name of contact
	 * @param contactUser user the contact is associated with, will be used to pull loginId from. Pass null to use UserUtils.USER_DEFAULT_ID (-9999) as loginId
	 * @return newly created LiteContact
	 */
	public LiteContact createContact(String firstName, String lastName, LiteYukonUser contactUser);
	
	/**
	 * Updates an existing contact, saves it, process db change update
	 * @param liteContact Existing contact to update
	 * @param firstName First name of contact
	 * @param lastName Last name of contact
	 * @param loginId optional, use null avoid updating the loginId
	 */
	public void updateContact(LiteContact liteContact, String firstName, String lastName, Integer loginId);
}
