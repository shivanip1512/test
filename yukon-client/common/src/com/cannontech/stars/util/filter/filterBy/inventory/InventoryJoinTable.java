package com.cannontech.stars.util.filter.filterBy.inventory;

import com.cannontech.stars.util.filter.JoinTable;

public enum InventoryJoinTable implements JoinTable {
    
    CUSTOMER_ACCOUNT(
        "LEFT OUTER JOIN CustomerAccount ca ON ib.AccountID = ca.AccountID"),
                 
    CUSTOMER(
         "LEFT OUTER JOIN Customer c ON ca.CustomerID = c.CustomerID",
         CUSTOMER_ACCOUNT),
         
    ACCOUNT_SITE(
        "LEFT OUTER JOIN AccountSite aSite ON ca.AccountSiteID = aSite.AccountSiteID",
        CUSTOMER_ACCOUNT),
             
    ADDRESS(
        "LEFT OUTER JOIN Address a ON aSite.StreetAddressID = a.AddressID",
        ACCOUNT_SITE),
        
    LM_HARDWARE_CONFIGURATION(
        "LEFT OUTER JOIN LMHardwareConfiguration ON LMHardwareConfiguration.InventoryID = ib.InventoryID"),
    
    APPLIANCE_BASE(
        "LEFT OUTER JOIN ApplianceBase ON ApplianceBase.ApplianceID = LMHardwareConfiguration.ApplianceID",
        LM_HARDWARE_CONFIGURATION),

    INVENTORY_TO_WAREHOUSE_MAPPING(
        "LEFT OUTER JOIN InventoryToWareHouseMapping itwhm ON itwhm.InventoryID = ib.InventoryId"),
        
    ENERGY_COMPANY_TO_INVENTORY_MAPPING(
        "JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");    

    private final String sql;
    private final JoinTable dependency;

    private InventoryJoinTable(String sql) {
        this(sql, null);
    }

    private InventoryJoinTable(String sql, JoinTable dependency) {
        this.sql = sql;
        this.dependency = dependency;
    }
    
    @Override
    public String getSql() {
        return sql;
    }
    
    @Override
    public JoinTable getDependency() {
        return dependency;
    }

}
