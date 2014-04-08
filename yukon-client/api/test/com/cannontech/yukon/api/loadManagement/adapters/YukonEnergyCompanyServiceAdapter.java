package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class YukonEnergyCompanyServiceAdapter implements YukonEnergyCompanyService {

    @Override
    public List<EnergyCompany> getAllEnergyCompanies() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany getEnergyCompanyByAccountId(int accountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany getEnergyCompanyByInventoryId(int inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    @Override
    public boolean isEnergyCompanyOperator(LiteYukonUser operator) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getEnergyCompanyIdByOperator(LiteYukonUser operator) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Integer> getChildEnergyCompanies(int energyCompanyId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Integer> getDirectChildEnergyCompanies(int energyCompanyId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Integer getParentEnergyCompany(int energyCompanyId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Integer findParentEnergyCompany(int energyCompanyId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isPrimaryOperator(int operatorLoginId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany getEnergyCompany(int ecId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Integer> getRouteIds(int ecId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany getEnergyCompanyByUser(LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany findEnergyCompany(int ecId) {
        throw new UnsupportedOperationException("not implemented");
    }
}
