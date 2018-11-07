package com.cannontech.stars.dr.adapters;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class EnergyCompanyDaoAdapter implements EnergyCompanyDao {

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
    public boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany) {
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
    public EnergyCompany getEnergyCompany(LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany findEnergyCompany(int ecId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany getEnergyCompany(String energyCompanyName) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany findEnergyCompany(String energyCompanyName) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<LiteYukonPAObject> getAllRoutes(EnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void addCiCustomer(int customerId, EnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Integer> getCiCustomerIds(EnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<EnergyCompany> getEnergyCompaniesByCiCustomer(int customerId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int createEnergyCompany(String name, int contactId, LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void updateCompanyName(String name, int ecId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Integer> getOperatorUserIds(EnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EnergyCompany getDefaultEnergyCompanyForThirdPartyApiOrSystemUsage() {
        throw new UnsupportedOperationException("not implemented");
    }
}
