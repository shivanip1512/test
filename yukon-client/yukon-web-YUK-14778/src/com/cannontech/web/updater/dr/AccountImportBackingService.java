package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class AccountImportBackingService extends RecentResultUpdateBackingService {

    private RecentResultsCache<AccountImportResult> resultsCache;
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {

        AccountImportResult accountImportResult = resultsCache.getResult(resultId);
       
       if (accountImportResult == null) {
           return "";
       }
       AccountImportTypeEnum accountImportTypeEnum = AccountImportTypeEnum.valueOf(resultTypeStr);
       return accountImportTypeEnum.getValue(accountImportResult);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Required
    public void setResultsCache(RecentResultsCache<AccountImportResult> resultsCache) {
        this.resultsCache = resultsCache;
    }
}