package com.cannontech.stars.dr.general.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.user.UserUtils;

public class ContactServiceImpl implements ContactService {

	private ContactDao contactDao;
	
	public LiteContact createContact(String firstName, String lastName, LiteYukonUser contactUser) {
		
		LiteContact liteContact = new LiteContact(-1); //  contactDao.saveContact will insert for -1, otherwise update
		saveContact(liteContact, firstName, lastName, contactUser == null ? UserUtils.USER_DEFAULT_ID : contactUser.getUserID());
	    
	    return liteContact;
	}
	
	public LiteContact createAdditionalContact(String firstName, String lastName, int customerId, LiteYukonUser contactUser) {
		
		LiteContact liteContact =  this.createContact(firstName, lastName, contactUser);
	    contactDao.associateAdditionalContact(customerId, liteContact.getContactID());
	    
	    return liteContact;
	}
	
	public void updateContact(LiteContact liteContact, String firstName, String lastName, Integer loginId) {
	
		saveContact(liteContact, firstName, lastName, loginId);
	}
	
	private void saveContact(LiteContact liteContact, String firstName, String lastName, Integer loginId) {
		
		liteContact.setContFirstName(StringUtils.isBlank(firstName) ? "" : firstName);
	    liteContact.setContLastName(StringUtils.isBlank(lastName) ? "" : lastName);
	    if (loginId != null) {
	    	liteContact.setLoginID(loginId);
	    }
	    contactDao.saveContact(liteContact);
	}
	
	@Autowired
	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}
}
