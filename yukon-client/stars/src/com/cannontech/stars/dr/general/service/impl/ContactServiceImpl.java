package com.cannontech.stars.dr.general.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.dr.general.service.ContactService;

public class ContactServiceImpl implements ContactService {

	private ContactDao contactDao;
	private DBPersistentDao dbPersistentDao;
	
	public LiteContact createContact(String firstName, String lastName, int loginId) {
		
		LiteContact liteContact = new LiteContact(-1); //  contactDao.saveContact will insert for -1, otherwise update
		saveContact(liteContact, firstName, lastName, loginId);
	    dbPersistentDao.processDBChange(new DBChangeMsg(liteContact.getLiteID(),
                DBChangeMsg.CHANGE_CONTACT_DB,
                DBChangeMsg.CAT_CUSTOMERCONTACT,
                DBChangeMsg.CAT_CUSTOMERCONTACT,
                DBChangeMsg.CHANGE_TYPE_ADD));
	    
	    return liteContact;
	}
	
	public void updateContact(LiteContact liteContact, String firstName, String lastName, Integer loginId) {
	
		saveContact(liteContact, firstName, lastName, loginId);
		dbPersistentDao.processDBChange(new DBChangeMsg(liteContact.getLiteID(),
	            DBChangeMsg.CHANGE_CONTACT_DB,
	            DBChangeMsg.CAT_CUSTOMERCONTACT,
	            DBChangeMsg.CAT_CUSTOMERCONTACT,
	            DBChangeMsg.CHANGE_TYPE_UPDATE));
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
	
	@Autowired
	public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
		this.dbPersistentDao = dbPersistentDao;
	}
}
