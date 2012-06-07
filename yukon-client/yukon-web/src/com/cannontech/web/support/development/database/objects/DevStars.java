package com.cannontech.web.support.development.database.objects;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

public class DevStars extends DevObject {
    private LiteStarsEnergyCompany energyCompany;
    private DevStarsAccounts devStarsAccounts = new DevStarsAccounts();
    private DevStarsHardware devStarsHardware = new DevStarsHardware();
    private boolean createCooperEC = false;

    public LiteStarsEnergyCompany getEnergyCompany() {
        return energyCompany;
    }

    public void setEnergyCompany(LiteStarsEnergyCompany energyCompany) {
        this.energyCompany = energyCompany;
    }

    public DevStarsAccounts getDevStarsAccounts() {
        return devStarsAccounts;
    }

    public void setDevStarsAccounts(DevStarsAccounts devStarsAccounts) {
        this.devStarsAccounts = devStarsAccounts;
    }

    public DevStarsHardware getDevStarsHardware() {
        return devStarsHardware;
    }

    public void setDevStarsHardware(DevStarsHardware devStarsHardware) {
        this.devStarsHardware = devStarsHardware;
    }

    @Override
    public int getTotal() {
        if (!isCreate()) {
            return 0;
        }
        int accounts = devStarsAccounts.getNumAccounts();
        int accountHardware = devStarsAccounts.getNumAccounts() * devStarsHardware.getNumHardwarePerAccount();
        int extraHardware = devStarsHardware.getNumExtraTotal();
        int total = accounts + accountHardware + extraHardware;
        return total;
    }

    public boolean isCreateCooperEC() {
        return createCooperEC;
    }

    public void setCreateCooperEC(boolean createCooperEC) {
        this.createCooperEC = createCooperEC;
    }
}
