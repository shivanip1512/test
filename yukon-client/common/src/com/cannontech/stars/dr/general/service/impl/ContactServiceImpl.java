package com.cannontech.stars.dr.general.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.user.UserUtils;

public class ContactServiceImpl implements ContactService {

    @Autowired private ContactDao contactDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
	
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
	        List<LiteUserGroup> custUserGroups = ecMappingDao.getResidentialUserGroups(customer.getEnergyCompanyID());
	        user = yukonUserDao.createLoginForAdditionalContact(firstName, lastName, custUserGroups.get(0));
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
}