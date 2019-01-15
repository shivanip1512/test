package com.cannontech.web.stars.dr.operator.service;

import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;

public interface OperatorAccountService {

    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator, OperatorGeneralUiExtras operatorGeneralUiExtras);

    /**
     * Updates fields of an account which can only be updated by an operator. This method should
     * never be from a consumer page and will probably be accompanied by an
     * {@link AccountService#updateAccount(UpdatableAccount, int, LiteYukonUser)}.
     */
    public void updateAccount(int accountId, OperatorGeneralUiExtras operatorGeneralUiExtras);
	
	public OperatorGeneralUiExtras getOperatorGeneralUiExtras(int accountId, YukonUserContext userContext);

    public ContactDto getContactDto(int contactId, YukonUserContext userContext);
    public ContactDto getBlankContactDto(int additionalBlankNotifications);

    /**
     * Load the Contact and set values within the ContactDTO, but allow the caller to
     * be selective about which ContactNotifications are removed from the collection
     * and set on the DTO.
     * 
     * @param contactId
     * @param setDtoHomePhone       true if you want the first HOME_PHONE removed from the ConNos and contactDto.setHomePhone(..)
     * @param setDtoWorkPhone       true if you want the first WORK_PHONE removed from the ConNos and contactDto.setWorkPhone(..)
     * @param setDtoEmail           true if you want the first EMAIL removed from the ConNos and contactDto.setEmail(..)
     * @param userContext
     * @return
     */
    public ContactDto getContactDto(int contactId, boolean setDtoHomePhone, boolean setDtoWorkPhone, boolean setDtoEmail, YukonUserContext userContext);
	
	/**
	 * Add/Update contact.
	 * Add/Updates/Delete contact notifications.
	 */
	public void saveContactDto(ContactDto contactDto, LiteCustomer customer, LiteYukonUser user);
	/**
	 * Creates the new primary contact (if not exists), or updates the existing.. first? contact.
	 * @param contactDto
	 * @param user
	 */
    public void saveContactDto(ContactDto contactDto, LiteYukonUser user);
	
	public AccountInfoFragment getAccountInfoFragment(int accountId);
}
