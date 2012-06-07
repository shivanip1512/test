package com.cannontech.stars.util.filter.filterBy.inventory;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.filter.OrderBy;

public enum InventoryOrderBy implements OrderBy {

    INVENTORY_ID(
        Integer.MIN_VALUE, // There is no YukonListEntryTypes to represent this OrderBy.
        "ib.InventoryID"),
    
    SERIAL_NUMBER(
        YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO,
        "lmhb.ManufacturerSerialNumber"),
    
    INSTALL_DATE(
        YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE,
        "ib.InstallDate");
    
    private static final OrderBy DEFAULT_ORDERBY = INVENTORY_ID;
    private final int orderByid;
    private final String sql;
    
    private InventoryOrderBy(int orderById, String sql) {
        this.orderByid = orderById;
        this.sql = sql;
    }
    
    @Override
    public String getSql() {
        return sql;
    }
    
    private int getOrderById() {
        return orderByid;
    }
    
    public static final OrderBy valueOf(int orderById) {
        for (final InventoryOrderBy orderBy : InventoryOrderBy.values()) {
            if (orderById == orderBy.getOrderById()) return orderBy;
        }
        return DEFAULT_ORDERBY;
    }
    
}
