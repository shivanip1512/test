package com.cannontech.stars.dr.general.service;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ContactService {

	/**
	 * Creates a new contact, saves it, process db change add. Does NOT associate the newly created contact with a customer.
	 * @param firstName First name of contact
	 * @param lastName Last name of contact
	 * @param contactUser user the contact is associated with, will be used to pull loginId from. Pass null to use UserUtils.USER_NONE_ID (-9999) as loginId
	 * @return newly created LiteContact
	 */
	public LiteContact createContact(String firstName, String lastName, LiteYukonUser contactUser);
	
	/**
	 * Creates a new contact, saves it, process db change add. Associates the contact with a given customer as an additional contact.
	 * @param firstName First name of contact
	 * @param lastName Last name of contact
	 * @param customer The customer this additional contact is being created for.
	 * @return newly created LiteContact
	 */
	public LiteContact createAdditionalContact(String firstName, String lastName, LiteCustomer customer, LiteYukonUser createdByUser);
	
	/**
	 * Updates an existing contact, saves it, process db change update
	 * @param liteContact Existing contact to update
	 * @param firstName First name of contact
	 * @param lastName Last name of contact
	 * @param loginId optional, use null avoid updating the loginId
	 */
	public void updateContact(LiteContact liteContact, String firstName, String lastName, Integer loginId);
}
