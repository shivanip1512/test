package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Comparator;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.event.EventWorkOrder;
import com.cannontech.database.data.stars.report.WorkOrderBase;

public class WorkOrderFilter extends AbstractFilter<LiteWorkOrderBase> {

    @Override
    protected boolean doFilterCheck(LiteWorkOrderBase workOrderBase, FilterWrapper filter) {
        final int filterTypeId = Integer.parseInt(filter.getFilterTypeID());
        final int filterId = Integer.parseInt(filter.getFilterID());
        switch (filterTypeId) {
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY : 
                return this.filterByServiceCompany(workOrderBase, filterId);
                
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE :
                return this.filterByServiceType(workOrderBase, filterId);
                
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMP_CODES :
                return this.filterByServiceCompanyCodes(workOrderBase, filter);
                
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_CUST_TYPE :
                return this.filterByCustomerType(workOrderBase, filterId);
                
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS :
                return this.filterByStatus(workOrderBase, filterId);
                
            default : return true;    
        }
    }

    @Override
    protected Comparator<Integer> getFilterMapComparator() {
        return new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                final Integer speed1 = getSpeed(o1);
                final Integer speed2 = getSpeed(o2);
                
                if (speed1.equals(speed2)) return o1.compareTo(o2);
                
                int result = speed1.compareTo(speed2);
                return result;
            }
        };
    }
    
    public Integer getSpeed(final int filterType) {
        switch (filterType) {
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS : return 5;
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_CUST_TYPE : return 4;
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMP_CODES : return 3;
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE : return 2;
            case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY : return 1;
            default : return 0;
        }
    }
    
    private boolean filterByStatus(final LiteWorkOrderBase workOrderBase, final int filterId) {
        //no dates are specified, just check current state ID
        if (getStartDate() == null && getStopDate() == null) {
            if (workOrderBase.getCurrentStateID() == filterId) { 
                return true;
            }    
        } else {
            //Dates were specified so we look at the event type to see if it matches the status filter
            final WorkOrderBase workOrderBaseHeavy = (WorkOrderBase) StarsLiteFactory.createDBPersistent(workOrderBase);
            final ArrayList<EventWorkOrder> eventWorkOrderList = workOrderBaseHeavy.getEventWorkOrders();
            for (final EventWorkOrder eventWorkOrder : eventWorkOrderList) {
                boolean addWorkOrder = false;
                if (eventWorkOrder.getEventBase().getActionID().intValue() == filterId) {
                    if (getStartDate() != null && getStopDate() == null) {
                        if (eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStartDate()) >= 0)
                            addWorkOrder = true;
                    } else if (getStopDate() != null && getStartDate() == null) {
                        if (eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStopDate()) <= 0)
                            addWorkOrder = true;
                    } else {
                        if ((eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStartDate()) >= 0) &&
                                (eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStopDate()) <= 0))
                            addWorkOrder = true;
                    }
                    if (addWorkOrder) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean filterByCustomerType(final LiteWorkOrderBase workOrderBase, final int filterId) {
        LiteStarsCustAccountInformation liteCustAcctInfo = getEnergyCompany().getBriefCustAccountInfo(workOrderBase.getAccountID(), true);
        LiteCustomer customer = liteCustAcctInfo.getCustomer();

        boolean match = false;
        switch (filterId) {
            case -1 : { //RESIDENTIAL CUSTOMER
                if (customer.getCustomerTypeID() == CustomerTypes.CUSTOMER_RESIDENTIAL) {
                    match = true;
                }
                break;
            }
            case YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL :
            {
                if (customer instanceof LiteCICustomer) {
                    int customerTypeId = ((LiteCICustomer) customer).getCICustType();
                    if (customerTypeId == filterId) {
                        match = true;
                    }
                } else {
                    int customerTypeId = customer.getCustomerTypeID();
                    if (customerTypeId == CustomerTypes.CUSTOMER_CI) {
                        match = true;
                    }
                }
                break;
            }
            case YukonListEntryTypes.CUSTOMER_TYPE_INDUSTRIAL:
            case YukonListEntryTypes.CUSTOMER_TYPE_MANUFACTURING:
            case YukonListEntryTypes.CUSTOMER_TYPE_MUNICIPAL: {
                if (customer instanceof LiteCICustomer) {
                    int customerTypeId = ((LiteCICustomer) customer).getCICustType();
                    if (customerTypeId == filterId) {
                        match = true;
                    }
                }
            }
        }
        return match;
    }
    
    private boolean filterByServiceCompanyCodes(final LiteWorkOrderBase workOrderBase, final FilterWrapper filter) {
        LiteStarsCustAccountInformation liteCustAcctInfo = getEnergyCompany().getBriefCustAccountInfo(workOrderBase.getAccountID(), true);
        LiteAddress liteAddr = getEnergyCompany().getAddress(liteCustAcctInfo.getAccountSite().getStreetAddressID());
        
        //The filterText is formatted "Label: value".  By parsing for the last space char we can get just the value.
        String tempCode = filter.getFilterText().substring(filter.getFilterText().lastIndexOf(" ")+1);
        if (liteAddr.getZipCode().startsWith(tempCode)) {
            return true;   
        } 
        return false;
    }

    private boolean filterByServiceType(final LiteWorkOrderBase workOrderBase, final int filterId) {
        if (workOrderBase.getWorkTypeID() == filterId) {
            return true;
        }
        return false;
    }
    
    private boolean filterByServiceCompany(final LiteWorkOrderBase workOrderBase, final int filterId) {
        if (workOrderBase.getServiceCompanyID() == filterId) {
            return true;
        }
        return false;
    }
    
    

}
