package com.cannontech.stars.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.hardware.Warehouse;

public class InventoryFilter extends AbstractFilter<LiteInventoryBase> {
    private Map<Integer,Map<Integer,Integer>> applianceAccountsCache;
    private Map<String,Map<Integer,Integer>> customerAccountsPostalCodesCache;
    private Map<Integer,Map<Integer,Integer>> customerAccountsTypeCache;
    private Map<Integer,List<Integer>> warehouseListCache;
    
    @Override
    protected boolean doFilterCheck(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final int filterTypeId = Integer.valueOf(filter.getFilterTypeID());
        switch (filterTypeId) {
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE :
                return this.filterByDevType(inventoryBase, filter);
                
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY :
                return this.filterByServiceCompany(inventoryBase, filter);
            
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_POSTAL_CODES :
                return this.filterByPostalCodes(inventoryBase, filter);
                
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE :
                return this.filterByWarehouse(inventoryBase, filter);
                
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX :
                return this.filterBySerialRangeMax(inventoryBase, filter);
                
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_APPLIANCE_TYPE :
                return this.filterByApplianceType(inventoryBase, filter);
                
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CUST_TYPE :
                return this.filterByCustomerType(inventoryBase, filter);
            
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS :    
                return this.filterByDeviceStatus(inventoryBase, filter);
                
            default : return true;
        }
    }

    @Override
    protected Comparator<Integer> getFilterMapComparator() {
        return new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                final Integer speed1 = getSpeed(o1);
                final Integer speed2 = getSpeed(o2);
                int result = speed1.compareTo(speed2);
                return result;
            }
        };
    }
    
    public Integer getSpeed(final int filterType) {
        switch (filterType) {
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CUST_TYPE : return 8; 
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_APPLIANCE_TYPE : return 7; 
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE : return 6; 
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_POSTAL_CODES : return 5; 
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE : return 4;
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN : return 3;
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX : return 2;
            case YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS : return 1;
            default : return 0;
        }
    }
    
    private boolean filterByDeviceStatus(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final int specificFilterId = Integer.parseInt(filter.getFilterID());
        /** This is another ugly Xcel specific requirement
         * They don't want meters shown on the "(none)" state filter
         */
        if (specificFilterId == 0 && !(inventoryBase instanceof LiteStarsLMHardware) ) {
            return false;
        }
        
        /**
         * TODO CurrentState in the Xcel world will need to be more elegantly
         * combined with the old DeviceStatus.  They are currently separate so as
         * to not break STARS too much for existing customers.
         */
        if (inventoryBase.getCurrentStateID() == specificFilterId ||
                inventoryBase.getDeviceStatus() == specificFilterId) {
            return true;
        }    
        return false;
    }
    
    private boolean filterByCustomerType(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final int specificFilterId = Integer.parseInt(filter.getFilterID());
        Map<Integer, Integer> accounts = this.getCustomerAccountsType(specificFilterId); 
        if (accounts.get(inventoryBase.getAccountID()) != null && inventoryBase instanceof LiteStarsLMHardware) {
            return true;
        }
        return false;
    }
    
    private boolean filterByApplianceType(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final int specificFilterId = Integer.parseInt(filter.getFilterID());
        Map<Integer,Integer> accounts = this.getApplianceAccounts(specificFilterId);
        if(accounts.get(inventoryBase.getAccountID()) != null && inventoryBase instanceof LiteStarsLMHardware) {
            return true;
        }
        return false;
    }
    
    private boolean filterBySerialRangeMax(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final String specificFilterString = filter.getFilterID();
        if (inventoryBase instanceof LiteStarsLMHardware) {
            String serial = ((LiteStarsLMHardware) inventoryBase).getManufacturerSerialNumber();
            if (InventoryUtils.isSerialWithPossibleCharsLessThan(serial, specificFilterString)) {
                return true;
            }    
        }
        return false;
    }
    
    private boolean filterByWarehouse(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final int specificFilterId = Integer.parseInt(filter.getFilterID());
        final List<Integer> warehousedInventory  = this.getWarehouseList(specificFilterId);
        
        boolean match = false;
        for (int x = 0; x < warehousedInventory.size(); x++) {
            Integer wareHouseId = warehousedInventory.get(x);
            if (inventoryBase.getLiteID() == wareHouseId.intValue()) {
                match = true;
                //let's try to speed this up over time
                warehousedInventory.remove(x);
                break;
            }
        }
        return match;
    }
    
    private boolean filterByPostalCodes(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final String specificFilterString = filter.getFilterID();
        Map<Integer,Integer> accounts = this.getCustomerAccountsPostalCodes(specificFilterString);
        if(accounts.get(inventoryBase.getAccountID()) != null && inventoryBase instanceof LiteStarsLMHardware) {
            return true;
        }
        return false;
    }
    
    private boolean filterByServiceCompany(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final int specificFilterId = Integer.parseInt(filter.getFilterID());
        if (inventoryBase.getInstallationCompanyID() == specificFilterId) {
            return true;
        }    
        return false;
    }
    
    private boolean filterByDevType(final LiteInventoryBase inventoryBase, final FilterWrapper filter) {
        final int specificFilterId = Integer.parseInt(filter.getFilterID());
        final int devTypeMCT = getEnergyCompany().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT ).getEntryID();

        /*
         * Was comparing the yukon def ids.  we don't want that.  we want it to instead compare entry IDs 
         * (ie. the customized type that the customer has given)
         *
               DaoFactory.getYukonListDao().areSameInYukon( ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID(), specificFilterID.intValue() )
         */
        if (inventoryBase instanceof LiteStarsLMHardware && 
                ((LiteStarsLMHardware) inventoryBase).getLmHardwareTypeID() == specificFilterId  ||
                specificFilterId == devTypeMCT && InventoryUtils.isMCT(inventoryBase.getCategoryID())) {
            return true;
        }
        return false;
    }
    
    private Map<Integer,List<Integer>> getWarehouseListCache() {
        if (this.warehouseListCache == null) {
            this.warehouseListCache = new HashMap<Integer,List<Integer>>();
        }
        return this.warehouseListCache;
    }
    
    private List<Integer> getWarehouseList(final int filterId) {
        List<Integer> warehouseList = this.getWarehouseListCache().get(filterId);
        if (warehouseList == null) {
            warehouseList = (filterId == 0) ?
                    Warehouse.getAllInventoryNotInAWarehouse() : Warehouse.getAllInventoryInAWarehouse(filterId);
            this.getWarehouseListCache().put(filterId, warehouseList);
        }
        return warehouseList;
    }
    
    
    private Map<String,Map<Integer,Integer>> getCustomerAccountsPostalCodesCache() {
        if (this.customerAccountsPostalCodesCache == null) {
            this.customerAccountsPostalCodesCache = new HashMap<String,Map<Integer,Integer>>();
        }
        return this.customerAccountsPostalCodesCache;
    }
    
    private Map<Integer,Map<Integer,Integer>> getCustomerAccountsTypeCache() {
        if (this.customerAccountsTypeCache == null) {
            this.customerAccountsTypeCache = new HashMap<Integer,Map<Integer,Integer>>();
        }
        return this.customerAccountsTypeCache;
    }
    
    private Map<Integer,Integer> getCustomerAccountsType(final int filterId) {
        Map<Integer,Integer> accountsMap = this.getCustomerAccountsTypeCache().get(filterId);
        if (accountsMap == null) {
            accountsMap = (filterId == -1) ?
                    CustomerAccount.getAccountIDsNonCommercial(-1) : CustomerAccount.getAccountIDsFromCustomerType(filterId);
            this.getCustomerAccountsTypeCache().put(filterId, accountsMap); 
        }
        return accountsMap;
    }
    
    private Map<Integer,Integer> getCustomerAccountsPostalCodes(final String specificFilterString) {
        Map<Integer,Integer> accountsMap = this.getCustomerAccountsPostalCodesCache().get(specificFilterString);
        if (accountsMap == null) {
            accountsMap = CustomerAccount.getAccountIDsFromZipCode(specificFilterString);
            this.getCustomerAccountsPostalCodesCache().put(specificFilterString, accountsMap);
        }
        return accountsMap;
    }
    
    private Map<Integer,Map<Integer,Integer>> getApplianceAccountsCache() {
        if (this.applianceAccountsCache == null) {
            this.applianceAccountsCache = new HashMap<Integer,Map<Integer,Integer>>();
        }
        return this.applianceAccountsCache;
    }
    
    private Map<Integer,Integer> getApplianceAccounts(final int filterId) {
        Map<Integer,Integer> accountsMap = this.getApplianceAccountsCache().get(filterId);
        if (accountsMap == null) {
            accountsMap = ApplianceBase.getAllAccountIDsFromApplianceCategory(filterId);
            this.getApplianceAccountsCache().put(filterId, accountsMap);
        }
        return accountsMap;
    }

}
