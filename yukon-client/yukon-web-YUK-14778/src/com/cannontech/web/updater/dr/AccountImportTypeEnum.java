package com.cannontech.web.updater.dr;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;
import com.cannontech.web.updater.ResultAccessor;

public enum AccountImportTypeEnum {
    COMPLETED_COUNT(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            return accountImportResult.getCompleted();
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            return accountImportResult.isComplete();
        }
    }),
    
    IS_CANCELED(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            return accountImportResult.isCanceled();
        }
    }),
    
    PRESCAN_STATUS(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.web.modules.operator.accountImport." + accountImportResult.getPrescanStatus());
            return resolvableTemplate;
        }
    }),
    
    PRESCAN_STATUS_COLOR(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            return accountImportResult.getPrescanStatusColor();
        }
    }),
    
    ACCOUNTS_PROCESSED(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            int accounts = accountImportResult.getCustLines() == null ? 0 : accountImportResult.getCustLines().size()-1;
            return accounts;
        }
    }),
    
    HARDWARE_PROCESSED(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            int hardware = accountImportResult.getHwLines() == null ? 0 : accountImportResult.getHwLines().size()-1;
            return hardware;
        }
    }),
    
    IMPORT_ERRORS(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            return accountImportResult.getImportErrors();
        }
    }),
    
    HARDWARE_STATS(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            YukonMessageSourceResolvable msg = new YukonMessageSourceResolvable("yukon.web.modules.operator.accountImport.hardwareStats", 
                                                                                accountImportResult.getHardwareAdded().size(), 
                                                                                accountImportResult.getHardwareUpdated().size(), 
                                                                                accountImportResult.getHardwareRemoved().size());
            return msg;
        }
    }),
    
    ACCOUNT_STATS(new ResultAccessor<AccountImportResult>() {
        public Object getValue(AccountImportResult accountImportResult) {
            YukonMessageSourceResolvable msg = new YukonMessageSourceResolvable("yukon.web.modules.operator.accountImport.accountStats", 
                                                                                accountImportResult.getAccountsAdded().size(), 
                                                                                accountImportResult.getAccountsUpdated().size(), 
                                                                                accountImportResult.getAccountsRemoved().size());
            return msg;
        }
    });
    
    
    private ResultAccessor<AccountImportResult> accountImportValueAccessor;
    
    AccountImportTypeEnum(ResultAccessor<AccountImportResult> accountImportValueAccessor) {

        this.accountImportValueAccessor = accountImportValueAccessor;
    }
    
    public Object getValue(AccountImportResult accountImportResult) {
        return this.accountImportValueAccessor.getValue(accountImportResult);
    }
}