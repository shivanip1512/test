package com.cannontech.web.dev.database.objects;

import java.util.List;

import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.google.common.collect.Lists;

public class DevStarsAccounts {
    private Integer accountNumMin = 100000;
    private Integer accountNumMax = 999999999;
    private Integer numAccounts = 10;
    private List<UpdatableAccount> accounts = Lists.newArrayList();

    public Integer getAccountNumMin() {
        return accountNumMin;
    }

    public void setAccountNumMin(Integer accountNumMin) {
        this.accountNumMin = accountNumMin;
    }

    public Integer getNumAccounts() {
        return numAccounts;
    }

    public void setNumAccounts(Integer numAccounts) {
        this.numAccounts = numAccounts;
    }

    public List<UpdatableAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<UpdatableAccount> accounts) {
        this.accounts = accounts;
    }

    public Integer getAccountNumMax() {
        return accountNumMax;
    }
    public void setAccountNumMax(Integer accountNumMax) {
        this.accountNumMax = accountNumMax;
    }

}
