package com.cannontech.stars.dr.general.service;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.user.YukonUserContext;

public interface OperatorGeneralSearchService {

	public AccountSearchResultHolder customerAccountSearch(OperatorAccountSearchBy searchBy, 
														   String searchValue,
														   int startIndex,
														   int count,
														   LiteStarsEnergyCompany energyCompany, 
														   YukonUserContext userContext);
}
