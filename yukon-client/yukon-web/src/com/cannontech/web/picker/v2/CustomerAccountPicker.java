package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.search.UltraLightCustomerAccount;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CustomerAccountPicker extends LucenePicker<UltraLightCustomerAccount> {

    private EnergyCompanyService energyCompanyService;
    
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.customerAccount.";

        OutputColumn column = new OutputColumn("accountNumber", titleKeyPrefix + "accountNumber");
        column.setMaxCharsDisplayed(40);
        columns.add(column);

        outputColumns = Collections.unmodifiableList(columns);
    }
    
    @Override
    public YukonObjectCriteria combineCriteria(YukonObjectCriteria baseCriteria, String extraArgs) {
        
        // Get available energy company ids
        int energyCompanyId = Integer.parseInt(extraArgs);
        Set<LiteStarsEnergyCompany> memberEnergyCompanies = energyCompanyService.getMemberCandidates(energyCompanyId);
        Set<Integer> searchableEnergyCompanyIds = 
            Sets.newHashSet(Iterables.transform(memberEnergyCompanies, new Function<LiteStarsEnergyCompany, Integer>() {
                @Override
                public Integer apply(LiteStarsEnergyCompany energyCompany) {
                    return energyCompany.getEnergyCompanyId();
                }
            }));
        searchableEnergyCompanyIds.add(energyCompanyId);

        // Build up lucene query
        final BooleanQuery query = new BooleanQuery(false);
        if (baseCriteria != null) {
            query.add(baseCriteria.getCriteria(), Occur.MUST);
        }
        
        LuceneQueryHelper.buildQueryByEnergyCompanyIds(query, searchableEnergyCompanyIds);
        
        return new YukonObjectCriteria() {
            @Override
            public Query getCriteria() {
                return query;
            }
        };
    }

    @Override
    public String getIdFieldName() {
        return "accountId";
    }

    @Override
    protected String getLuceneIdFieldName() {
        return "accountId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
    
    // DI Setters
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
}
