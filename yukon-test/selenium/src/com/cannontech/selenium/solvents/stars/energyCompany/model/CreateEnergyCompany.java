package com.cannontech.selenium.solvents.stars.energyCompany.model;

import java.util.Collection;
import java.util.List;

import com.cannontech.selenium.solvents.common.userLogin.model.Login;
import com.google.common.collect.Lists;

public class CreateEnergyCompany {
    private String companyName;
    private String email;
    private String defaultRoute;
    private String primaryOperatorGroup;
    private List<String> additionalOperatorGroups = Lists.newArrayList();
    private List<String> residentialGroups = Lists.newArrayList();
    private Login changeLogin;

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultRoute() {
        return defaultRoute;
    }
    public void setDefaultRoute(String defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public String getPrimaryOperatorGroup() {
        return primaryOperatorGroup;
    }
    public void setPrimaryOperatorGroup(String primaryOperatorGroup) {
        this.primaryOperatorGroup = primaryOperatorGroup;
    }

    public List<String> getAdditionalOperatorGroups() {
        return additionalOperatorGroups;
    }
    public void setAdditionalOperatorGroups(Collection<String> additionalOperatorGroups) {
        this.additionalOperatorGroups = Lists.newArrayList(additionalOperatorGroups);
    }

    public List<String> getResidentialGroups() {
        return residentialGroups;
    }
    public void setResidentialGroups(Collection<String> residentialGroups) {
        this.residentialGroups = Lists.newArrayList(residentialGroups);
    }

    public Login getChangeLogin() {
        return changeLogin;
    }
    public void setChangeLogin(Login changeLogin) {
        this.changeLogin = changeLogin;
    }

}