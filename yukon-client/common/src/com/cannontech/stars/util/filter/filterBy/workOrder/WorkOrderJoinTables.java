package com.cannontech.stars.util.filter.filterBy.workOrder;

import com.cannontech.stars.util.filter.JoinTable;

public enum WorkOrderJoinTables implements JoinTable {
    
    CUSTOMER_ACCOUNT(
        "JOIN CustomerAccount ca ON ca.AccountID = wob.AccountID"),
    
    ACCOUNT_SITE(
        "JOIN AccountSite asite ON asite.AccountSiteID = ca.AccountSiteID",
        CUSTOMER_ACCOUNT),
        
    ADDRESS(
        "JOIN Address addr ON addr.AddressID = asite.StreetAddressID",
        ACCOUNT_SITE),
        
    CUSTOMER(
        "JOIN Customer cus ON cus.CustomerID = ca.CustomerID",
        CUSTOMER_ACCOUNT),
        
    CICUSTOMER(
        "LEFT OUTER JOIN CICustomerBase cicb ON cicb.CustomerID = cus.CustomerID",
        CUSTOMER),
        
    ENERGY_COMPANY_TO_WORKORDER_MAPPING(
        "JOIN ECToWorkOrderMapping ectwm ON ectwm.WorkOrderID = wob.OrderID");       
    
    private final String sql;
    private final JoinTable dependency;

    private WorkOrderJoinTables(String sql) {
        this(sql, null);
    }

    private WorkOrderJoinTables(String sql, JoinTable dependency) {
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
