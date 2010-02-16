package com.cannontech.stars.dr.general.service;

import com.cannontech.database.data.lite.LiteContact;

public interface ContactService {

	/**
	 * Creates a new contact, saves it, process db change add
	 * @param firstName
	 * @param lastName
	 * @param loginId required, use UserUtils.USER_DEFAULT_ID if you don't have a user login to use
	 * @return
	 */
	public LiteContact createContact(String firstName, String lastName, int loginId);
	
	/**
	 * Updates an existing contact, saves it, process db change update
	 * @param liteContact
	 * @param firstName
	 * @param lastName
	 * @param loginId optional, use null avoid updating the loginId
	 */
	public void updateContact(LiteContact liteContact, String firstName, String lastName, Integer loginId);
}
