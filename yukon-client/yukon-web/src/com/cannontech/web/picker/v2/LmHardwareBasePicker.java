package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.dao.InventoryToECFilter;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseWithECIdRowMapper;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class LmHardwareBasePicker extends DatabasePicker<LMHardwareBase> {
    private StarsDatabaseCache starsDatabaseCache;

    private final static String[] searchColumnNames = new String[] {
        "ManufacturerSerialNumber"
        };
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.lmHardwareBase.";

        columns.add(new OutputColumn("manufacturerSerialNumber", titleKeyPrefix + "manufacturerSerialNumber"));
        columns.add(new OutputColumn("inventoryId", titleKeyPrefix + "inventoryId"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public LmHardwareBasePicker() {
        super(new LMHardwareBaseWithECIdRowMapper(), searchColumnNames);
    }

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
                                 List<PostProcessingFilter<LMHardwareBase>> postProcessingFilters,
                                 String extraArgs, YukonUserContext userContext) {
        int energyCompanyId;
        try {
            energyCompanyId = Integer.parseInt(extraArgs);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The energy company id was not supplied.");
        }

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<Integer> energyCompanyIds = energyCompany.getAllEnergyCompaniesDownward();
        sqlFilters.add(new InventoryToECFilter(energyCompanyIds));
    }

    @Override
    public String getIdFieldName() {
        return "inventoryId";
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
