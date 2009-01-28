package com.cannontech.stars.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.service.UpdatableAccountConverter;
import com.cannontech.stars.web.util.ImportManagerUtil;

public class UpdatableAccountConverterImpl implements UpdatableAccountConverter {

	private AccountService accountService;
	private SiteInformationDao siteInformationDao;
	
	@Override
	public UpdatableAccount createNewUpdatableAccount(String[] custFields, LiteStarsEnergyCompany ec) {
		
		// create new UpdatableAccount with acct number from custFields
		UpdatableAccount acct = new UpdatableAccount();
		acct.setAccountNumber(custFields[ImportManagerUtil.IDX_ACCOUNT_NO]);
		
		// new acct dto
		AccountDto acctDto = new AccountDto();
		
		// update dto with cust fields
		setCustFieldsOnDto(acctDto, custFields);
		acct.setAccountDto(acctDto);
		
		return acct;
		
	}
	
	@Override
	public UpdatableAccount getUpdatedUpdatableAccount(LiteStarsCustAccountInformation starsCustAcctInfo, String[] custFields, LiteStarsEnergyCompany ec) {
		
		String accountNumber = starsCustAcctInfo.getCustomerAccount().getAccountNumber();
		
		// create new UpdatableAccount based on existing cust acct number
		UpdatableAccount acct = new UpdatableAccount();
		acct.setAccountNumber(accountNumber);
		
		// get dto based on existing cust acct
		AccountDto acctDto = accountService.getAccountDto(accountNumber, ec);
		
		// update dto with cust fields
		setCustFieldsOnDto(acctDto, custFields);
		acct.setAccountDto(acctDto);
		
		return acct;
	}
	
	private void setCustFieldsOnDto(AccountDto acctDto, String[] custFields) {
		
		
		acctDto.setFirstName(custFields[ImportManagerUtil.IDX_FIRST_NAME]);
		acctDto.setLastName(custFields[ImportManagerUtil.IDX_LAST_NAME]);
		acctDto.setEmailAddress(custFields[ImportManagerUtil.IDX_EMAIL]);
		
		acctDto.setHomePhone(custFields[ImportManagerUtil.IDX_HOME_PHONE]);
		acctDto.setWorkPhone(custFields[ImportManagerUtil.IDX_WORK_PHONE]);
		
		acctDto.setUserName(custFields[ImportManagerUtil.IDX_USERNAME]);
		acctDto.setPassword(custFields[ImportManagerUtil.IDX_PASSWORD]);
		acctDto.setLoginGroup(custFields[ImportManagerUtil.IDX_LOGIN_GROUP]);
		
		acctDto.setCompanyName(custFields[ImportManagerUtil.IDX_COMPANY_NAME]);
		if(custFields[ImportManagerUtil.IDX_CUSTOMER_TYPE].equalsIgnoreCase("COM") || custFields[ImportManagerUtil.IDX_COMPANY_NAME].length() > 0) {
			acctDto.setIsCommercial(true);
		} else {
			acctDto.setIsCommercial(false);
		}
		
		acctDto.setMapNumber(custFields[ImportManagerUtil.IDX_MAP_NO]);
		
		// street address
		Address streetAddress = acctDto.getStreetAddress();
		streetAddress.setCityName(custFields[ImportManagerUtil.IDX_CITY]);
		streetAddress.setCounty(custFields[ImportManagerUtil.IDX_COUNTY]);
		streetAddress.setLocationAddress1(custFields[ImportManagerUtil.IDX_STREET_ADDR1]);
		streetAddress.setLocationAddress2(custFields[ImportManagerUtil.IDX_STREET_ADDR2]);
		streetAddress.setStateCode(custFields[ImportManagerUtil.IDX_STATE]);
		streetAddress.setZipCode(custFields[ImportManagerUtil.IDX_ZIP_CODE]);
		
		// site
		SiteInformation siteInfo = acctDto.getSiteInfo();
		siteInfo.setFeeder(custFields[ImportManagerUtil.IDX_FEEDER]);
		siteInfo.setPole(custFields[ImportManagerUtil.IDX_POLE]);
		siteInfo.setServiceVoltage(custFields[ImportManagerUtil.IDX_SERV_VOLT]);
		siteInfo.setSubstationName(custFields[ImportManagerUtil.IDX_SUBSTATION]);
		siteInfo.setTransformerSize(custFields[ImportManagerUtil.IDX_TRFM_SIZE]);
		if (custFields[ImportManagerUtil.IDX_SUBSTATION].length() > 0) {
			try {
				// might be id
				int subId = Integer.parseInt(custFields[ImportManagerUtil.IDX_SUBSTATION]);
				String subNameFromId = siteInformationDao.getSubstationNameById(subId);
				siteInfo.setSubstationName(subNameFromId);
			} catch (NumberFormatException e) {
				siteInfo.setSubstationName(custFields[ImportManagerUtil.IDX_SUBSTATION]);
			} catch (NotFoundException e) {
				// don't set
			}
		}
		
	}
	
	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@Autowired
	public void setSiteInformationDao(SiteInformationDao siteInformationDao) {
		this.siteInformationDao = siteInformationDao;
	}
}
