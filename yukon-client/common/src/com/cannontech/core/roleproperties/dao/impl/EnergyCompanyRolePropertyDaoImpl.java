package com.cannontech.core.roleproperties.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class EnergyCompanyRolePropertyDaoImpl implements EnergyCompanyRolePropertyDao {
    
    private RolePropertyDao rolePropertyDao;
    
    public enum SerialNumberValidation {
        NUMERIC,
        ALPHANUMERIC
    }

    @Override
    public boolean checkAllProperties(YukonEnergyCompany energyCompany, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties) {
        verifyEnergyCompanyRoleProperties(firstProperty, otherProperties);
        return rolePropertyDao.checkAllProperties(energyCompany.getEnergyCompanyUser(), firstProperty, otherProperties);
    }

    @Override
    public boolean checkAnyProperties(YukonEnergyCompany energyCompany, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties) {
        verifyEnergyCompanyRoleProperties(firstProperty, otherProperties);
        return rolePropertyDao.checkAnyProperties(energyCompany.getEnergyCompanyUser(), firstProperty, otherProperties);
    }

    @Override
    public boolean checkFalseProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany) {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.checkFalseProperty(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public boolean checkProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany) {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.checkProperty(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public boolean getPropertyBooleanValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.getPropertyBooleanValue(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public double getPropertyDoubleValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.getPropertyDoubleValue(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public <E extends Enum<E>> E getPropertyEnumValue(YukonRoleProperty property, Class<E> enumClass, YukonEnergyCompany energyCompany) throws UserNotInRoleException {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.getPropertyEnumValue(property, enumClass, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public float getPropertyFloatValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.getPropertyFloatValue(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public int getPropertyIntegerValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.getPropertyIntegerValue(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public long getPropertyLongValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.getPropertyLongValue(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public String getPropertyStringValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.getPropertyStringValue(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public boolean isCheckPropertyCompatible(YukonRoleProperty property) {
        verifyEnergyCompanyRoleProperties(property);
        return rolePropertyDao.isCheckPropertyCompatible(property);
    }

    @Override
    public void verifyAnyProperties(YukonEnergyCompany energyCompany, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties) throws NotAuthorizedException {
        verifyEnergyCompanyRoleProperties(firstProperty, otherProperties);
        rolePropertyDao.verifyAnyProperties(energyCompany.getEnergyCompanyUser(), firstProperty, otherProperties);
    }

    @Override
    public void verifyFalseProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws NotAuthorizedException {
        verifyEnergyCompanyRoleProperties(property);
        rolePropertyDao.verifyFalseProperty(property, energyCompany.getEnergyCompanyUser());
    }

    @Override
    public void verifyProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws NotAuthorizedException {
        verifyEnergyCompanyRoleProperties(property);
        rolePropertyDao.verifyProperty(property, energyCompany.getEnergyCompanyUser());
    }

    
    private void verifyEnergyCompanyRoleProperties(YukonRoleProperty first, YukonRoleProperty... properties) throws IllegalArgumentException{
        verifyEnergyCompanyRoleProperty(first);
        for (YukonRoleProperty property : properties) {
            verifyEnergyCompanyRoleProperty(property);
        }
    }

    private void verifyEnergyCompanyRoleProperty(YukonRoleProperty property) throws IllegalArgumentException{
        if(property.getRole() != YukonRole.ENERGY_COMPANY) {
            throw new IllegalArgumentException("Role property must be in the Energy Company role.");
        }
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}