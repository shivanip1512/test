package com.cannontech.stars.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
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
		
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_FIRST_NAME])) {
            acctDto.setFirstName(custFields[ImportManagerUtil.IDX_FIRST_NAME]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_LAST_NAME])) {
            acctDto.setLastName(custFields[ImportManagerUtil.IDX_LAST_NAME]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_EMAIL])) {
            acctDto.setEmailAddress(custFields[ImportManagerUtil.IDX_EMAIL]);
        }
		
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_HOME_PHONE])) {
            acctDto.setHomePhone(custFields[ImportManagerUtil.IDX_HOME_PHONE]);
        }		
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_WORK_PHONE])) {
            acctDto.setWorkPhone(custFields[ImportManagerUtil.IDX_WORK_PHONE]);
        }		

        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_IVR_USERNAME])) {
            acctDto.setIvrLogin(custFields[ImportManagerUtil.IDX_IVR_USERNAME]);
        }

        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_IVR_PIN])) {
            acctDto.setVoicePIN(custFields[ImportManagerUtil.IDX_IVR_PIN]);
        }
        
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_USERNAME])) {
            acctDto.setUserName(custFields[ImportManagerUtil.IDX_USERNAME]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_PASSWORD])) {
            acctDto.setPassword(custFields[ImportManagerUtil.IDX_PASSWORD]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_LOGIN_GROUP])) {
            acctDto.setLoginGroup(custFields[ImportManagerUtil.IDX_LOGIN_GROUP]);
        }		
		
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_COMPANY_NAME])) {
            acctDto.setCompanyName(custFields[ImportManagerUtil.IDX_COMPANY_NAME]);
        }
		
		if(custFields[ImportManagerUtil.IDX_CUSTOMER_TYPE].equalsIgnoreCase("COM") || custFields[ImportManagerUtil.IDX_COMPANY_NAME].length() > 0) {
			acctDto.setIsCommercial(true);
		} else {
			acctDto.setIsCommercial(false);
		}
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_MAP_NO])) {
            acctDto.setMapNumber(custFields[ImportManagerUtil.IDX_MAP_NO]);
        }
		
		
		// street address
		Address streetAddress = acctDto.getStreetAddress();
		String cityName = custFields[ImportManagerUtil.IDX_CITY];
		if (!StringUtils.isEmpty(cityName)) {
		    streetAddress.setCityName(cityName);
		}

		String county = custFields[ImportManagerUtil.IDX_COUNTY];
		if (!StringUtils.isEmpty(county)) {
		    streetAddress.setCounty(county);
		}
		
		String locationAddress1 = custFields[ImportManagerUtil.IDX_STREET_ADDR1];
		if (!StringUtils.isEmpty(locationAddress1)) {
		    streetAddress.setLocationAddress1(locationAddress1);
		}
		    
		String locationAddress2 = custFields[ImportManagerUtil.IDX_STREET_ADDR2];
		if (!StringUtils.isEmpty(locationAddress2)) {
		    streetAddress.setLocationAddress2(locationAddress2);
		}
		
		String stateCode = custFields[ImportManagerUtil.IDX_STATE];
		if (!StringUtils.isEmpty(stateCode)) {
		    streetAddress.setStateCode(stateCode);
		}
		
		String zipCode = custFields[ImportManagerUtil.IDX_ZIP_CODE];
		if (!StringUtils.isEmpty(zipCode)) {
		    streetAddress.setZipCode(zipCode);
		}
 		
		// site
		SiteInformation siteInfo = acctDto.getSiteInfo();
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_FEEDER])) {
            siteInfo.setFeeder(custFields[ImportManagerUtil.IDX_FEEDER]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_POLE])) {
            siteInfo.setPole(custFields[ImportManagerUtil.IDX_POLE]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_SERV_VOLT])) {
            siteInfo.setServiceVoltage(custFields[ImportManagerUtil.IDX_SERV_VOLT]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_SUBSTATION])) {
            siteInfo.setSubstationName(custFields[ImportManagerUtil.IDX_SUBSTATION]);
        }
        if (!StringUtils.isEmpty(custFields[ImportManagerUtil.IDX_TRFM_SIZE])) {
            siteInfo.setTransformerSize(custFields[ImportManagerUtil.IDX_TRFM_SIZE]);
        }		
		
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
