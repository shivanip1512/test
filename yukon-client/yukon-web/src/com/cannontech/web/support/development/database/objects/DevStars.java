package com.cannontech.web.support.development.database.objects;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;

public class DevStars extends DevObject {
    private LiteStarsEnergyCompany energyCompany;
    private DevStarsAccounts devStarsAccounts = new DevStarsAccounts();
    private DevStarsHardware devStarsHardware = new DevStarsHardware();
    
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
        int accounts = devStarsAccounts.getNumAccounts();
        int accountHardware = devStarsAccounts.getNumAccounts() * devStarsHardware.getNumHardwarePerAccount();
        int extraHardware = devStarsHardware.getNumExtraTotal();
        int total = accounts + accountHardware + extraHardware;
        return total;
    }
}
