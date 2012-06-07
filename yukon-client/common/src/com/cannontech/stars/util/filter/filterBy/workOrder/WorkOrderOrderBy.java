package com.cannontech.stars.util.filter.filterBy.workOrder;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.util.filter.OrderBy;

public enum WorkOrderOrderBy implements OrderBy {
    
    ORDER_NUMBER(
        YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO,
        "wob.OrderNumber"),

    COMPANY_NAME(
        YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_SERV_COMP,
        "sc.CompanyName"),
    
    STATUS(
        YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_SERV_STAT,
        "yle.EntryText"),
    
    TYPE(
        YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_SERV_TYPE,
        "yle2.EntryText"),
    
    DATE(
        YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_DATE_TIME,
        "wob.DateReported,wob.OrderID");

    private static final WorkOrderOrderBy DEFAULT_ORDERBY = ORDER_NUMBER;
    private final int orderById;
    private final String sql;
    
    private WorkOrderOrderBy(int orderById, String sql) {
        this.orderById = orderById;
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    private int getOrderById() {
        return orderById;
    }
    
    public static final OrderBy valueOf(int orderById) {
        for (final WorkOrderOrderBy orderBy : WorkOrderOrderBy.values()) {
            if (orderById == orderBy.getOrderById()) return orderBy;
        }
        return DEFAULT_ORDERBY;
    }
    
}
