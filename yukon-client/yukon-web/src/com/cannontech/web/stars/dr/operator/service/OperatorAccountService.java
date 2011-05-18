package com.cannontech.web.stars.dr.operator.service;

import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.ContactDto;
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;

public interface OperatorAccountService {

    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator, OperatorGeneralUiExtras operatorGeneralUiExtras);
	public void updateAccount(int accountId, OperatorGeneralUiExtras operatorGeneralUiExtras);
	
	public OperatorGeneralUiExtras getOperatorGeneralUiExtras(int accountId, YukonUserContext userContext);
	
	public ContactDto getContactDto(int contactId, YukonUserContext userContext);
	public ContactDto getBlankContactDto(int additionalBlankNotifications);
	
	/**
	 * Add/Update contact.
	 * Add/Updates/Delete contact notifications.
	 */
	public void saveContactDto(ContactDto contactDto, LiteCustomer customer);
	
	public AccountInfoFragment getAccountInfoFragment(int accountId);
}
