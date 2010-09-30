package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountToECFilter;
import com.cannontech.stars.dr.account.dao.CustomerAccountWithECIdRowMapper;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class CustomerAccountPicker extends DatabasePicker<CustomerAccount> {
    private StarsDatabaseCache starsDatabaseCache;

    private final static String[] searchColumnNames = new String[] {
        "CA.AccountNumber"
        };
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.customerAccount.";

        columns.add(new OutputColumn("accountNumber", titleKeyPrefix + "accountNumber"));
        columns.add(new OutputColumn("accountId", titleKeyPrefix + "accountId"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public CustomerAccountPicker() {
        super(new CustomerAccountWithECIdRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
                                 List<PostProcessingFilter<CustomerAccount>> postProcessingFilters,
                                 String extraArgs, YukonUserContext userContext) {
        
        int energyCompanyId = NumberUtils.toInt(extraArgs, 0);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<Integer> energyCompanyIds = energyCompany.getAllEnergyCompaniesDownward();
        sqlFilters.add(new CustomerAccountToECFilter(energyCompanyIds));
    }

    @Override
    public String getIdFieldName() {
        return "accountId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
}
