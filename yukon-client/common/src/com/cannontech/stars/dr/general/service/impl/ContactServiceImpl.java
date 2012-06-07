package com.cannontech.stars.dr.general.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.user.UserUtils;

public class ContactServiceImpl implements ContactService {

	private ContactDao contactDao;
	private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
	private YukonUserDao yukonUserDao;
	private StarsDatabaseCache starsDatabaseCache;
	
	public LiteContact createContact(String firstName, String lastName, LiteYukonUser contactUser) {
		
		LiteContact liteContact = new LiteContact(-1); //  contactDao.saveContact will insert for -1, otherwise update
		saveContact(liteContact, firstName, lastName, contactUser == null ? UserUtils.USER_DEFAULT_ID : contactUser.getUserID());
	    
	    return liteContact;
	}
	
	@Override
	@Transactional
	public LiteContact createAdditionalContact(String firstName, String lastName, LiteCustomer customer) {
	    LiteYukonUser user = null;
		LiteStarsEnergyCompany energyCompany =  starsDatabaseCache.getEnergyCompany(customer.getEnergyCompanyID());
	    boolean autoCreateLogin = energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS, energyCompany);
	    if(autoCreateLogin) {
	        LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
	        user = yukonUserDao.createLoginForAdditionalContact(firstName, lastName, custGroups[0]);
	    }
		LiteContact liteContact =  createContact(firstName, lastName, user);
	    contactDao.associateAdditionalContact(customer.getCustomerID(), liteContact.getContactID());
	    
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
	
	@Autowired
	public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
	
	@Autowired
	public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
}