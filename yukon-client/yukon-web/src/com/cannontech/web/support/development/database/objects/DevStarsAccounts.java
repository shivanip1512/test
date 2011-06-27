package com.cannontech.web.support.development.database.objects;

import java.util.List;

import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.google.common.collect.Lists;

public class DevStarsAccounts {
    private int accountNumMin = 100000;
    private int accountNumMax = 999999999;
    private int numAccounts = 10;
    private List<UpdatableAccount> accounts = Lists.newArrayList();

    public int getAccountNumMin() {
        return accountNumMin;
    }

    public void setAccountNumMin(int accountNumMin) {
        this.accountNumMin = accountNumMin;
    }

    public int getNumAccounts() {
        return numAccounts;
    }

    public void setNumAccounts(int numAccounts) {
        this.numAccounts = numAccounts;
    }

    public List<UpdatableAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<UpdatableAccount> accounts) {
        this.accounts = accounts;
    }

    public int getAccountNumMax() {
        return accountNumMax;
    }
    public void setAccountNumMax(int accountNumMax) {
        this.accountNumMax = accountNumMax;
    }

}
