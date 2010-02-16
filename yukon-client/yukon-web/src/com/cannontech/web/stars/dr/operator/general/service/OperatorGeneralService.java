package com.cannontech.web.stars.dr.operator.general.service;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.model.DisplayableContact;
import com.cannontech.web.stars.dr.operator.general.model.OperatorGeneralUiExtras;

public interface OperatorGeneralService {

	public void updateAccount(int accountId, OperatorGeneralUiExtras operatorGeneralUiExtras);
	
	public OperatorGeneralUiExtras getOperatorGeneralUiExtras(int accountId, YukonUserContext userContext);
	
	public DisplayableContact findDisplayableContact(int customerId, boolean isPrimary);
}
