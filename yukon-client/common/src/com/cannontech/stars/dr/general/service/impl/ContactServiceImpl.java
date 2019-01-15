package com.cannontech.stars.dr.general.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.user.UserUtils;

public class ContactServiceImpl implements ContactService {

    @Autowired private ContactDao contactDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private UsersEventLogService usersEventLogService; 

	@Override
    public LiteContact createContact(String firstName, String lastName, LiteYukonUser contactUser) {
		
		LiteContact liteContact = new LiteContact(-1); //  contactDao.saveContact will insert for -1, otherwise update
		saveContact(liteContact, firstName, lastName, contactUser == null ? UserUtils.USER_NONE_ID : contactUser.getUserID());
	    
	    return liteContact;
	}
	
	@Override
	@Transactional
	public LiteContact createAdditionalContact(String firstName, String lastName, LiteCustomer customer, LiteYukonUser createdByUser) {
	    LiteYukonUser user = null;
        LiteStarsEnergyCompany energyCompany =  starsDatabaseCache.getEnergyCompany(customer.getEnergyCompanyID());
	    boolean autoCreateLogin = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS, energyCompany.getEnergyCompanyId());

	    if(autoCreateLogin) {
	        List<LiteUserGroup> custUserGroups = ecMappingDao.getResidentialUserGroups(customer.getEnergyCompanyID());
	        user = yukonUserDao.createLoginForAdditionalContact(firstName, lastName, custUserGroups.get(0));
            usersEventLogService.userCreated(user.getUsername(), custUserGroups.get(0).getUserGroupName(),
                energyCompany.getName(), user.getLoginStatus(), createdByUser);
            usersEventLogService.userAdded(user.getUsername(), custUserGroups.get(0).getUserGroupName(), createdByUser);
	    }
		LiteContact liteContact =  createContact(firstName, lastName, user);
	    contactDao.associateAdditionalContact(customer.getCustomerID(), liteContact.getContactID());
	    
	    return liteContact;
	}
	
	@Override
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