package com.cannontech.web.support.development.database.objects;

import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

public class DevStars extends DevObject {
    private LiteUserGroup userGroupResidential;
    private LiteUserGroup userGroupOperator;
    private LiteStarsEnergyCompany energyCompany;
    private DevStarsAccounts devStarsAccounts = new DevStarsAccounts();
    private DevStarsHardware devStarsHardware = new DevStarsHardware();
    private String newEnergyCompanyName;

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
        int extraHardware = devStarsHardware.getNumExtraTotal();
        
        int hardwareTypes = 0;
        for (DevHardwareType devHardwareType: getDevStarsHardware().getHardwareTypes()) {
            if (devHardwareType.isCreate()) {
                hardwareTypes++;
            }
        }
        
        int accountHardware = hardwareTypes * accounts * devStarsHardware.getNumHardwarePerAccount();
        int total = accounts + accountHardware + extraHardware;
        
        return total;
    }

    public LiteUserGroup getUserGroupResidential() {
        return userGroupResidential;
    }
    
    public void setUserGroupResidential(LiteUserGroup userGroupResidential) {
        this.userGroupResidential = userGroupResidential;
    }

    public LiteUserGroup getUserGroupOperator() {
        return userGroupOperator;
    }
    
    public void setUserGroupOperator(LiteUserGroup userGroupOperator) {
        this.userGroupOperator = userGroupOperator;
    }

    public String getNewEnergyCompanyName() {
        return newEnergyCompanyName;
    }
    
    public void setNewEnergyCompanyName(String newEnergyCompanyName) {
        this.newEnergyCompanyName = newEnergyCompanyName;
    }
}
