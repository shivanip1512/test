package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class YukonEnergyCompanyServiceAdapter implements YukonEnergyCompanyService {

    @Override
    public YukonEnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public YukonEnergyCompany getEnergyCompanyByAccountId(int accountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public YukonEnergyCompany getEnergyCompanyByInventoryId(int inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<YukonEnergyCompany> getAllEnergyCompanies() {
        throw new UnsupportedOperationException("not implemented");
    }

}
